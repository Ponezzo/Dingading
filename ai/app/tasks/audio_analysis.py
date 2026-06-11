import os
import json
import tempfile
import requests
import numpy as np
import librosa
import soundfile as sf
from io import BytesIO
from celery import Celery
from minio import Minio
from fastdtw import fastdtw
from scipy.spatial.distance import euclidean,cosine
from requests.exceptions import RequestException
from collections import defaultdict
import ssl
from urllib3 import PoolManager

from sklearn.preprocessing import StandardScaler
import scipy.signal

scipy.signal.hann = scipy.signal.windows.hann

# 환경 변수 설정
env_mode = os.getenv("ENV_MODE")
if not env_mode:
    from dotenv import load_dotenv
    load_dotenv()
    env_mode = os.getenv("ENV_MODE", "development")
env_mode = env_mode.strip().lower().replace(".local", "").strip(".")

# MinIO 설정
MINIO_HOST = os.getenv("MINIO_HOST", "localhost")
MINIO_PORT = os.getenv("MINIO_PORT", "9001")
MINIO_ENDPOINT = f"{MINIO_HOST}:{MINIO_PORT}"
MINIO_USERNAME = os.getenv("MINIO_USERNAME")
MINIO_PASSWORD = os.getenv("MINIO_PASSWORD")
MINIO_BUCKET_NAME = os.getenv("_NAME", "original-mp3-audio-bucket")

# 1. SSL 인증서 검증을 무시하는 SSLContext 생성
ssl_context = ssl._create_unverified_context()

# 2. 커스텀 HTTP 클라이언트 생성
http_client = PoolManager(ssl_context=ssl_context)

minio_client = Minio(
    MINIO_ENDPOINT,
    access_key=MINIO_USERNAME,
    secret_key=MINIO_PASSWORD,
    secure=True,
    http_client=http_client
)

# Celery 설정
celery = Celery(
    "tasks",
    broker=os.getenv("CELERY_BROKER_URL", "redis://localhost:6379/0"),
    backend=os.getenv("CELERY_RESULT_BACKEND", "redis://localhost:6379/0")
)

celery.conf.update(
    worker_concurrency=4,
    task_acks_late=True,
    task_time_limit=120,
    worker_prefetch_multiplier=4
)

def check_minio_connection():
    try:
        return minio_client.bucket_exists(MINIO_BUCKET_NAME)
    except:
        return False

def load_user_audio_from_tests(song_id, instrument_id):
    file_path = f"../../tests/cover_audio/{song_id}/{instrument_id}/CHALLENGE.mp3"
    if not os.path.exists(file_path):
        raise FileNotFoundError(f"사용자 오디오가 존재하지 않습니다: {file_path}")
    y, sr = librosa.load(file_path, sr=None)
    return y.astype(np.float32), sr

def load_json_from_minio(song_id, file_name):
    object_path = f"DB/{song_id}/{file_name}"
    try:
        response = minio_client.get_object(MINIO_BUCKET_NAME, object_path)
        return json.load(BytesIO(response.read()))
    except Exception:
        raise RuntimeError()

def load_npz_from_minio(song_id, instrument_id):
    object_path = f"DB/{song_id}/{instrument_id}/{instrument_id}.npz"
    try:
        response = minio_client.get_object(MINIO_BUCKET_NAME, object_path)
        buffer = BytesIO(response.read())
        data = np.load(buffer)
        if "sr" not in data or data["sr"] is None or "y" not in data:
            raise ValueError(f"npz 파일 오류: {object_path}")
        return data["y"], int(data["sr"])
    except Exception as e:
        raise RuntimeError(f"MinIO에서 npz 로드 실패: {object_path} → {e}")

def align_audio_signals(y_org, y_cov, top_db=30):
    y_trimmed, idx = librosa.effects.trim(y_org, top_db=top_db)
    start_idx, end_idx = idx
    y_org_aligned = y_org[start_idx:end_idx]
    if len(y_cov) >= end_idx:
        y_cov_aligned = y_cov[start_idx:end_idx]
    else:
        y_cov_aligned = y_cov[start_idx:]
    return y_org_aligned, y_cov_aligned

def extract_mfcc_stack(y, sr):
    if y is None or len(y) == 0 or np.allclose(y, 0):
        return np.zeros((39, 1))
    mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
    if mfcc.shape[1] == 0:
        return np.zeros((39, 1))
    mfcc = np.nan_to_num(mfcc, nan=-600.0, posinf=-600.0, neginf=-600.0)
    delta = librosa.feature.delta(mfcc)
    delta2 = librosa.feature.delta(mfcc, order=2)
    return np.vstack([mfcc, delta, delta2])

def extract_pyin_pitch(y, sr, hop_length=512):
    if y is None or len(y) == 0 or np.allclose(y, 0):
        n_frames = 1 + len(y) // hop_length
        return np.zeros(n_frames)
    f0, _, _ = librosa.pyin(y, fmin=librosa.note_to_hz('C2'), fmax=librosa.note_to_hz('C7'))
    return np.nan_to_num(f0, nan=0.0)

def extract_onset_strength(y, sr):
    if y is None or len(y) == 0:
        return np.zeros(1)
    return np.nan_to_num(librosa.onset.onset_strength(y=y, sr=sr), nan=0.0)

def safe_onset_detect(y, sr):
    if y is None or len(y) == 0:
        return np.array([0.0])
    onset_pos = librosa.onset.onset_detect(y=y, sr=sr, units='time')
    return onset_pos if len(onset_pos) > 0 else np.array([0.0])

def extract_tempo(y, sr, onset_pos, silence_threshold=1e-4):
    if y is None or len(y) == 0 or np.max(np.abs(y)) < silence_threshold:
        return 0.0
    try:
        tempo = float(librosa.beat.tempo(onset_envelope=onset_pos, sr=sr, aggregate=np.mean))
        if abs(tempo - 120.0) < 0.1 and np.std(onset_pos) < 1e-3:
            return 0.0
        return round(tempo, 2)
    except Exception:
        return 0.0

def evaluate_user_scores(pitch_user, pitch_ref,
                         onset_user_norm, onset_ref_norm,
                         onset_pos_user, onset_pos_ref,
                         tempo_user, tempo_ref,
                         mfcc_stack_user, mfcc_stack_ref, rms_user, rms_ref,
                         sr):

    def dtw_pitch_score(p1, p2):
        p1 = np.ravel(p1)
        p2 = np.ravel(p2)
    
        min_len = min(len(p1), len(p2))
        p1 = p1[:min_len]
        p2 = p2[:min_len]
    
        # 정규화 (스케일 차 제거)
        scaler = StandardScaler()
        p1_norm = scaler.fit_transform(p1.reshape(-1, 1)).flatten()
        p2_norm = scaler.fit_transform(p2.reshape(-1, 1)).flatten()
    
        # DTW + 안정적 스코어
        def scalar_euclidean(a, b): return abs(a - b)
        distance, _ = fastdtw(p1_norm, p2_norm, dist=scalar_euclidean)
        score = 100 / (1 + np.log1p(distance / len(p1_norm)))
        return round(max(0.0, min(100.0, score)), 2)

    def transpose_score(p1, p2):
        p1 = np.ravel(p1)
        p2 = np.ravel(p2)

        user_valid = p1[p1 > 0]
        ref_valid = p2[p2 > 0]

        if len(user_valid) == 0 or len(ref_valid) == 0:
            return 0.0

        mean_user = np.mean(user_valid)
        mean_ref = np.mean(ref_valid)

        if mean_user <= 0 or mean_ref <= 0:
            return 0.0

        semitone_shift = 12 * np.log2(mean_user / mean_ref)
        penalty = abs(semitone_shift) * 5
        return round(max(0.0, 100.0 - min(100.0, penalty)), 2)

    
    def pitch_chunk_score(p1, p2, chunks=10):
        p1 = np.ravel(p1)
        p2 = np.ravel(p2)

        length = min(len(p1), len(p2))
        if length == 0:
            return 0.0

        chunk_size = max(1, length // chunks)
        errors = []
        for i in range(chunks):
            s = i * chunk_size
            e = min((i + 1) * chunk_size, length)
            if s >= e:
                continue
            diff = np.mean(np.abs(p1[s:e] - p2[s:e]))
            errors.append(diff)

        avg_error = np.mean(errors) if errors else 0.0
        return round(max(0.0, 100.0 - avg_error), 2)


    def mfcc_cosine_score(m1, m2, rms_user, rms_ref, threshold=0.02):
        min_len = min(m1.shape[1], m2.shape[1], len(rms_user), len(rms_ref))
        m1 = m1[:, :min_len]
        m2 = m2[:, :min_len]
        rms_user = rms_user[:min_len]
        rms_ref = rms_ref[:min_len]

        epsilon = 1e-6
        rms_user = rms_user / (np.max(rms_user) + epsilon)
        rms_ref = rms_ref / (np.max(rms_ref) + epsilon)

        scores = []
        weights = []

        for i in range(min_len):
            u = m1[:, i]
            r = m2[:, i]
            u_silent = rms_user[i] < threshold
            r_silent = rms_ref[i] < threshold

            if u_silent and r_silent:
                continue
            elif u_silent != r_silent:
                scores.append(0.0)
                weights.append(1.0)
            else:
                sim = 1 - cosine(u, r)
                scores.append(sim * 100)
                weights.append((rms_user[i] + rms_ref[i]) / 2)

        if np.sum(weights) == 0:
            return 0.0
        score = np.sum(np.array(scores) * np.array(weights)) / np.sum(weights)
        return round(score, 2)

    def mfcc_chunk_rmse(m1, m2, rms_user, rms_ref, chunks=10, threshold=0.02):
        def average_mfcc_by_chunks(mfcc, num_chunks):
            num_frames = mfcc.shape[1]
            step = max(1, num_frames // num_chunks)
            result = []
            for i in range(num_chunks):
                s = i * step
                e = min((i + 1) * step, num_frames)
                avg = np.mean(mfcc[:, s:e], axis=1) if e > s else np.zeros(mfcc.shape[0])
                avg = np.nan_to_num(avg, nan=-590.0, posinf=-590.0, neginf=-590.0)
                result.append(avg)
            return np.array(result)

        def adaptive_score(rmse):
            if rmse < 20: return 100 - rmse * 0.25
            elif rmse < 100: return 95 - (rmse - 20) * 0.125
            elif rmse < 200: return 85 - (rmse - 100) * 0.05
            elif rmse < 300: return 80 - (rmse - 200) * 0.1
            else: return max(0, 70 - (rmse - 300) * 0.2)

        min_len = min(m1.shape[1], m2.shape[1], len(rms_user), len(rms_ref))
        m1 = m1[:, :min_len]
        m2 = m2[:, :min_len]
        rms_user = rms_user[:min_len]
        rms_ref = rms_ref[:min_len]

        rms_user = rms_user / (np.max(rms_user) + 1e-6)
        rms_ref = rms_ref / (np.max(rms_ref) + 1e-6)

        if (
            np.max(m1) < 5 and np.max(m2) < 5
        ) or (
            np.max(rms_user) < threshold and np.max(rms_ref) < threshold
        ):
            return 0.0

        user_chunks = average_mfcc_by_chunks(m1, chunks)
        ref_chunks = average_mfcc_by_chunks(m2, chunks)

        distances = []
        for i in range(len(user_chunks)):
            rms_u = np.mean(rms_user[i::chunks])  # chunk 내 평균
            rms_r = np.mean(rms_ref[i::chunks])
            if rms_u < threshold and rms_r < threshold:
                continue
            if (rms_u < threshold) != (rms_r < threshold):
                distances.append(100.0)
            else:
                dist = euclidean(user_chunks[i], ref_chunks[i])
                distances.append(dist)

        if not distances:
            return 0.0

        rmse = np.sqrt(np.mean(np.square(distances)))
        return round(adaptive_score(rmse), 2)

    def onset_match_score(user_onsets, ref_onsets, rms_user, rms_ref,
                                 sr, hop_length=512, delta=0.1, threshold=0.02,
                                 shift_range=np.linspace(-0.1, 0.1, 11), verbose=False):
        def match_score(a, b, rms_a, rms_b):
            matched = 0
            valid = 0
            for r in b:
                r_idx = int(r * sr / hop_length)
                if r_idx >= len(rms_b) or rms_b[r_idx] < threshold:
                    continue
                valid += 1
                for u in a:
                    u_idx = int(u * sr / hop_length)
                    if abs(r - u) < delta and u_idx < len(rms_a) and rms_a[u_idx] >= threshold:
                        matched += 1
                        break
            return 100.0 * matched / max(1, valid)

        best_score = 0.0
        best_shift = 0.0
        for shift in shift_range:
            shifted_user = user_onsets + shift
            forward = match_score(shifted_user, ref_onsets, rms_user, rms_ref)
            backward = match_score(ref_onsets, shifted_user, rms_ref, rms_user)
            avg_score = (forward + backward) / 2
            if avg_score > best_score:
                best_score = avg_score
                best_shift = shift

        if verbose:
            print(f"[onset_match_score] Best shift: {round(best_shift, 3)} sec")
        return round(best_score, 2)


    def onset_chunk_rmse(user_onset, ref_onset, rms_user, rms_ref, chunks=10, threshold=0.02, k=0.08):
        min_len = min(len(user_onset), len(ref_onset), len(rms_user), len(rms_ref))
        user_onset = user_onset[:min_len]
        ref_onset = ref_onset[:min_len]
        rms_user = rms_user[:min_len]
        rms_ref = rms_ref[:min_len]

        epsilon = 1e-6
        rms_user = rms_user / (np.max(rms_user) + epsilon)
        rms_ref = rms_ref / (np.max(rms_ref) + epsilon)

        if (
            np.max(user_onset) < 5 and np.max(ref_onset) < 5
        ) or (
            np.max(rms_user) < threshold and np.max(rms_ref) < threshold
        ):
            return 0.0

        step = max(1, min_len // chunks)
        rmse_chunks = []

        for i in range(chunks):
            start = i * step
            end = min((i + 1) * step, min_len)
            if end <= start:
                continue
            u_chunk = user_onset[start:end]
            r_chunk = ref_onset[start:end]
            rms_u = np.mean(rms_user[start:end])
            rms_r = np.mean(rms_ref[start:end])

            if rms_u < threshold and rms_r < threshold:
                continue
            elif (rms_u < threshold) != (rms_r < threshold):
                rmse_chunks.append(100.0)
            else:
                rmse = np.sqrt(np.mean((u_chunk - r_chunk) ** 2))
                rmse_chunks.append(rmse)

        if not rmse_chunks:
            return 0.0

        aggregated_rmse = np.mean(rmse_chunks)
        score = 100 * np.exp(-k * aggregated_rmse)
        return round(min(100.0, max(0.0, score)), 2)


    def tempo_threshold_score(t1, t2, rms_user=None, rms_ref=None, threshold=0.02, method="log") -> float:
        """
        사용자 tempo와 기준 tempo의 차이를 기반으로 점수를 계산합니다.
        - method: 'abs', 'ratio', 'log', 'threshold'
        """
        if (rms_user is not None and rms_ref is not None):
            if np.max(rms_user) < threshold or np.max(rms_ref) < threshold:
                return 0.0

        diff = abs(t1 - t2)
        if t1 <= 0 or t2 <= 0:
            return 0.0

        if method == "abs":
            return round(max(0, 100 - diff), 2)

        elif method == "ratio":
            ratio = diff / t2
            return round(max(0, 100 * (1 - ratio)), 2)

        elif method == "log":
            return round(100 / (1 + np.log1p(diff)), 2)

        elif method == "threshold":
            max_score = 100
            min_score = 0
            step = 5           # 점수 감소 단위
            bucket_size = 1    # diff가 증가할 때마다 점수 깎는 간격
            penalty = (diff // bucket_size) * step
            return round(max(min_score, max_score - penalty), 2)

        else:
            raise ValueError(f"Unknown tempo scoring method: {method}")


    scores = {
        "pitch_dtw": round(dtw_pitch_score(pitch_user, pitch_ref), 2),
        "transpose": round(transpose_score(pitch_user, pitch_ref), 2),
        "pitch_chunk": round(pitch_chunk_score(pitch_user, pitch_ref), 2),
        "mfcc_cos": round(mfcc_cosine_score(mfcc_stack_user, mfcc_stack_ref, rms_user, rms_ref), 2),
        "mfcc_chunk": round(mfcc_chunk_rmse(mfcc_stack_user, mfcc_stack_ref, rms_user, rms_ref), 2),
        # "onset_match": round(onset_match_score(onset_pos_user, onset_pos_ref, rms_user, rms_ref, sr), 2),
        "onset_chunk": round(onset_chunk_rmse(onset_user_norm, onset_ref_norm, rms_user, rms_ref), 2),
        "tempo_thresh": round(tempo_threshold_score(tempo_user, tempo_ref, rms_user, rms_ref), 2)
    }
    return scores

def compute_grouped_scores(scores: dict, weights: dict, categories: dict) -> dict:
    group_totals = defaultdict(float)
    group_weights = defaultdict(float)
    for k, v in scores.items():
        group = categories.get(k, "unknown")
        w = weights.get(k, 0)
        group_totals[group] += v * w
        group_weights[group] += w
    return {
        group: round(group_totals[group] / group_weights[group], 2)
        for group in group_totals
        if group_weights[group]
    }

def compute_weighted_score(scores: dict, weights: dict) -> float:
    total_weight = sum(weights.get(k, 0) for k in scores)
    if total_weight == 0:
        return 0.0
    weighted_sum = sum(scores[k] * weights.get(k, 0) for k in scores)
    return round(weighted_sum / total_weight, 2)

all_weights = {
    "pitch_dtw": 0.12, 
    "transpose": 0.12, 
    "pitch_chunk": 0.06,
    "mfcc_cos": 0.16,
    "mfcc_chunk": 0.17,
    # "onset_match": 0.11,
    "onset_chunk": 0.17,
    "tempo_thresh": 0.2
}

categories = {
    "pitch_dtw": "tune", 
    "transpose": "tune", 
    "pitch_chunk": "tune",
    "mfcc_cos": "tone",
    "mfcc_chunk": "tone",
    # "onset_match": "beat",
    "onset_chunk": "beat",
    "tempo_thresh": "beat"
}

@celery.task(name="ai.app.tasks.audio_analysis.run_audio_analysis")
def run_audio_analysis(user_audio_url: str, original_json_url: str):
    tmp_audio_path = None
    try:
        try:
            audio_response = requests.get(user_audio_url, verify=False)
            audio_response.raise_for_status()
        except RequestException as e:
            raise RuntimeError(f"사용자 오디오 다운로드 실패: {e}")
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp_audio_file:
            tmp_audio_file.write(audio_response.content)
            tmp_audio_path = tmp_audio_file.name

        user_y, user_sr = librosa.load(tmp_audio_path, sr=None)
        user_y = user_y.astype(np.float32)

        # 원본 특징 JSON 다운로드
        try:
            json_response = requests.get(original_json_url, verify=False)
            json_response.raise_for_status()
            features_json = json_response.json()
        except RequestException as e:
            raise RuntimeError(f"features.json 다운로드 실패: {e}")

        def get_onset_pos_org(features_json, user_y, user_sr):
            if "onset_pos" in features_json:
                return np.array(features_json["onset_pos"])

            # JSON에도 없을 경우: 평균적인 박자 기준으로 생성 (예: 120bpm → 0.5초 간격)
            print("[INFO] onset_pos_org not found. Generating synthetic onsets every 0.5s.")
            duration = len(user_y) / user_sr
            return np.arange(0, duration, 0.5)

        onset_pos_org = get_onset_pos_org(features_json, user_y, user_sr)
        # 원본 특징 값 로딩
        pitch_org = np.array(features_json["pyin"])
        try:
            mfcc_org = np.array(features_json["mfcc"])
        except KeyError:
            mfcc_org = np.array(features_json["mfcc_stack"])
        onset_env_org = np.array(features_json["onset"])

        # onset을 이용한 tempo 계산
        def compute_tempo_from_onset(onset_times):
            if onset_times is None or len(onset_times) < 2:
                return 0.0
            intervals = np.diff(onset_times)
            mean_interval = np.mean(intervals)
            if mean_interval <= 0:
                return 0.0
            return round(60.0 / mean_interval, 2)

        if "beat" in features_json:
            tempo_org = float(features_json["beat"])
        else:
            tempo_org = compute_tempo_from_onset(onset_pos_org)

        
        # 사용자 특징 추출
        pitch_user = extract_pyin_pitch(user_y, user_sr)
        mfcc_user = extract_mfcc_stack(user_y, user_sr)
        onset_env_user = extract_onset_strength(user_y, user_sr)
        onset_pos_user = safe_onset_detect(user_y, user_sr)
        tempo_user = extract_tempo(user_y, user_sr, onset_pos_user)
        rms_user = librosa.feature.rms(y=user_y)[0]
        rms_org = np.ones_like(rms_user)  # 원본 오디오가 없으므로 길이 맞춤용 (선택)

        # 점수 평가
        scores = evaluate_user_scores(
            pitch_user, pitch_org,
            onset_env_user, onset_env_org,
            onset_pos_user, onset_pos_user,
            tempo_user, tempo_org,
            mfcc_user, mfcc_org,
            rms_user, rms_org,
            user_sr
        )

        grouped = compute_grouped_scores(scores, all_weights, categories)
        print("Grouped Scores:", grouped)
        print("All Scores:", scores)
        total = compute_weighted_score(scores, all_weights)

        final_result = {
            "tune_score": grouped.get("tune", 0.0),
            "tone_score": grouped.get("tone", 0.0),
            "beat_score": grouped.get("beat", 0.0)
        }

        return final_result

    except Exception:
        return {
            "tune_score": 0.0,
            "beat_score": 0.0,
            "tone_score": 0.0
        }

    finally:
        if tmp_audio_path and os.path.exists(tmp_audio_path):
            os.remove(tmp_audio_path)
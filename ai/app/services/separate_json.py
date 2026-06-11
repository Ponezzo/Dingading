# 뭉친 json 분할할 임시 파일
import os
import json

# 통합된 features.json 경로
merged_path = "../../tests/original_features.json"

# 저장할 루트 디렉토리
output_root = "C:/Users/SSAFY/Downloads/DB"

# JSON 파일 불러오기ffmpeg
with open(merged_path, "r", encoding="utf-8") as f:
    all_data = json.load(f)

# 각 곡마다 분리하여 저장
for song_title, song_data in all_data.items():
    song_dir = os.path.join(output_root, song_title)
    os.makedirs(song_dir, exist_ok=True)

    save_path = os.path.join(song_dir, "features.json")
    with open(save_path, "w", encoding="utf-8") as f:
        json.dump(song_data, f, indent=2, ensure_ascii=False)
    
    print(f"✅ 저장 완료: {save_path}")

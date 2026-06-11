'use client'

import { useState, useRef, useEffect, useCallback, forwardRef, useImperativeHandle } from 'react';
import { useRecordingStore } from '@/store/recording';
import styles from './audioRecorder.module.css';

// 외부에서 사용할 메서드 타입 정의
export interface AudioRecorderHandle {
  startRecording: () => Promise<void>;
  stopRecording: () => void;
}

interface AudioRecorderProps {
  songId: string;
  title: string;
  autoStartWithPlay?: boolean;
  autoStopWithEnd?: boolean;
  externalStartTrigger?: boolean;
  externalStopTrigger?: boolean;
  hideControls?: boolean;
}

// 함수 선언 방식으로 변경하여 일관성 유지
const AudioRecorder = forwardRef<AudioRecorderHandle, AudioRecorderProps>(function AudioRecorder(props, ref) {
  const {
    songId,
    title,
    externalStartTrigger = false,
    externalStopTrigger = false, 
    hideControls = false
  } = props;
  
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [audioURL, setAudioURL] = useState<string | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const timerRef = useRef<NodeJS.Timeout | null>(null);
  const audioPlayerRef = useRef<HTMLAudioElement | null>(null);
  
  const { setCurrentRecording } = useRecordingStore();
  
  // 미디어 스트림 초기화
  const initMediaStream = useCallback(async () => {
    try {
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
      
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      
      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;
      
      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };
      
      mediaRecorder.onstop = () => {
        const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/wav' });
        const url = URL.createObjectURL(audioBlob);
        setAudioURL(url);
        
        // 녹음 저장
        setCurrentRecording({
          id: `recording-${Date.now()}`,
          songId,
          blob: audioBlob,
          url,
          duration: recordingTime
        });
        
        audioChunksRef.current = [];
      };
      
      return true;
    } catch (error) {
      console.error('미디어 스트림 초기화 오류:', error);
      return false;
    }
  }, [songId, recordingTime, setCurrentRecording]);

  // 녹음 시작
  const startRecording = useCallback(async () => {
    if (isRecording) return;
    
    const initialized = await initMediaStream();
    if (!initialized) {
      alert('마이크 접근에 실패했습니다. 권한을 확인해주세요.');
      return;
    }
    
    try {
      audioChunksRef.current = [];
      setRecordingTime(0);
      
      if (mediaRecorderRef.current) {
        mediaRecorderRef.current.start();
        setIsRecording(true);
        
        // 녹음 시간 타이머 시작
        timerRef.current = setInterval(() => {
          setRecordingTime(prev => prev + 1);
        }, 1000);
      }
    } catch (error) {
      console.error('녹음 시작 오류:', error);
    }
  }, [isRecording, initMediaStream]);

  // 녹음 중지
  const stopRecording = useCallback(() => {
    if (!isRecording) return;
    
    try {
      if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
        mediaRecorderRef.current.stop();
      }
      
      if (timerRef.current) {
        clearInterval(timerRef.current);
        timerRef.current = null;
      }
      
      setIsRecording(false);
      
      // 미디어 스트림 정리
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
        streamRef.current = null;
      }
    } catch (error) {
      console.error('녹음 중지 오류:', error);
    }
  }, [isRecording]);

  // 외부에서 메서드 접근 가능하도록 설정
  useImperativeHandle(ref, () => ({
    startRecording,
    stopRecording
  }));

  // 녹음된 오디오 재생/정지
  const togglePlayRecording = () => {
    if (!audioPlayerRef.current || !audioURL) return;
    
    if (isPlaying) {
      audioPlayerRef.current.pause();
      setIsPlaying(false);
    } else {
      audioPlayerRef.current.play();
      setIsPlaying(true);
    }
  };

  // 오디오 이벤트 리스너
  useEffect(() => {
    const audioPlayer = audioPlayerRef.current;
    
    if (audioPlayer) {
      const handleEnded = () => setIsPlaying(false);
      
      audioPlayer.addEventListener('ended', handleEnded);
      
      return () => {
        audioPlayer.removeEventListener('ended', handleEnded);
      };
    }
  }, [audioURL]);

  // 외부 트리거에 의한 녹음 시작/중지
  useEffect(() => {
    if (externalStartTrigger) {
      startRecording();
    }
  }, [externalStartTrigger, startRecording]);

  useEffect(() => {
    if (externalStopTrigger) {
      stopRecording();
    }
  }, [externalStopTrigger, stopRecording]);

  // 컴포넌트 언마운트 시 리소스 정리
  useEffect(() => {
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
      
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
      
      if (audioURL) {
        URL.revokeObjectURL(audioURL);
      }
    };
  }, [audioURL]);

  // 시간 포맷팅 함수
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <div className={styles.audioRecorder}>
      <div className={styles.title}>{title}</div>
      
      {!hideControls && (
        <div className={styles.controls}>
          {!audioURL ? (
            <>
              <button
                className={`${styles.recordButton} ${isRecording ? styles.recording : ''}`}
                onClick={isRecording ? stopRecording : startRecording}
              >
                {isRecording ? '녹음 중지' : '녹음 시작'}
              </button>
              
              {isRecording && (
                <div className={styles.recordingIndicator}>
                  <span className={styles.recordingDot}></span>
                  <span>{formatTime(recordingTime)}</span>
                </div>
              )}
            </>
          ) : (
            <>
              <button
                className={styles.playButton}
                onClick={togglePlayRecording}
              >
                {isPlaying ? '정지' : '재생'}
              </button>
              
              <button
                className={styles.recordAgainButton}
                onClick={() => {
                  setAudioURL(null);
                  if (audioURL) {
                    URL.revokeObjectURL(audioURL);
                  }
                }}
              >
                다시 녹음
              </button>
            </>
          )}
        </div>
      )}
      
      {isRecording && (
        <div className={styles.recordingStatus}>
          <span className={styles.recordingDot}></span>
          <span>녹음 중... {formatTime(recordingTime)}</span>
        </div>
      )}
      
      {audioURL && (
        <div className={styles.waveform}>
          <audio ref={audioPlayerRef} src={audioURL} />
          <div className={styles.visualizer}>
            <div className={styles.simplifiedWave}></div>
          </div>
        </div>
      )}
    </div>
  );
});

AudioRecorder.displayName = 'AudioRecorder';

export default AudioRecorder;
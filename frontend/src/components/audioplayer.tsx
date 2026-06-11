'use client'

import { useState, useRef, useEffect, forwardRef, useImperativeHandle } from 'react';
import styles from './audioPlayer.module.css';

// 인터페이스 이름 오타 수정 (AudioPlaerHandle -> AudioPlayerHandle)
export interface AudioPlayerHandle {
  play: () => void;
  pause: () => void;
}

interface AudioPlayerProps {
  src: string;
  title: string;
  waveformSrc?: string;
  onPlayStart?: () => void;
  onPlayEnd?: () => void;
  autoPlay?: boolean;
  hideControls?: boolean;
}

// forwardRef를 사용하도록 수정
const AudioPlayer = forwardRef<AudioPlayerHandle, AudioPlayerProps>(function AudioPlayer(props, ref) {
  const {
    src,
    title,
    waveformSrc,
    onPlayStart,
    onPlayEnd,
    autoPlay = false, 
    hideControls = false 
  } = props;
  
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(0);
  const [duration, setDuration] = useState(0);
  const audioRef = useRef<HTMLAudioElement>(null);
  const waveformRef = useRef<HTMLDivElement>(null);

  // 외부에서 메서드 접근 가능하도록 설정
  useImperativeHandle(ref, () => ({
    play: () => {
      const audio = audioRef.current;
      if (!audio) return;
      
      const playPromise = audio.play();
      if (playPromise !== undefined) {
        playPromise.catch(error => {
          console.error('재생 실패:', error);
        });
      }
    },
    pause: () => {
      const audio = audioRef.current;
      if (!audio) return;
      
      audio.pause();
    }
  }));

  // 오디오 로드 시 처리
  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) {
      console.log("audioplayer.tsx, audio가 존재하지 않습니다.");
      return;
    } 
    console.log("audioplayer.tsx, audio element 초기화 , src == ", src);
    
    const handleLoadedMetadata = () => {
      setDuration(audio.duration);
    };

    // 오디오 로드 에러 처리 추가
    const handleError = (e: Event) => {
      console.error('오디오 로드 중 오류:', e);
    };

    audio.addEventListener('loadedmetadata', handleLoadedMetadata);
    audio.addEventListener('error', handleError);
    
    return () => {
      audio.removeEventListener('loadedmetadata', handleLoadedMetadata);
      audio.removeEventListener('error', handleError);
    };
  }, [src]);

  // 오디오 이벤트 처리
  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;

    const handleTimeUpdate = () => {
      setProgress(audio.currentTime / audio.duration * 100);
    };

    const handlePlay = () => {
      setIsPlaying(true);
      if (onPlayStart) onPlayStart();
    };

    const handlePause = () => {
      setIsPlaying(false);
    };

    const handleEnded = () => {
      setIsPlaying(false);
      setProgress(0);
      if (onPlayEnd) onPlayEnd();
    };

    audio.addEventListener('timeupdate', handleTimeUpdate);
    audio.addEventListener('play', handlePlay);
    audio.addEventListener('pause', handlePause);
    audio.addEventListener('ended', handleEnded);

    return () => {
      audio.removeEventListener('timeupdate', handleTimeUpdate);
      audio.removeEventListener('play', handlePlay);
      audio.removeEventListener('pause', handlePause);
      audio.removeEventListener('ended', handleEnded);
    };
  }, [onPlayStart, onPlayEnd]);

  // 자동 재생
  useEffect(() => {
    if (autoPlay && audioRef.current) {
      // 자동 재생 정책으로 인해 사용자 상호작용이 필요할 수 있음
      const playPromise = audioRef.current.play();
      
      if (playPromise !== undefined) {
        playPromise.catch(error => {
          console.error('자동 재생 실패:', error);
        });
      }
    }
  }, [autoPlay]);

  // src 변경 감지 추가
  useEffect(() => {
    if (audioRef.current && src && src.trim() !== '') {
      audioRef.current.load(); // src가 변경되면 오디오 다시 로드
    }
  }, [src]);

  const togglePlay = () => {
    const audio = audioRef.current;
    if (!audio) return;

    if (isPlaying) {
      audio.pause();
    } else {
      audio.play();
    }
  };

  const handleProgressBarClick = (e: React.MouseEvent<HTMLDivElement>) => {
    const audio = audioRef.current;
    const progressBar = e.currentTarget;
    if (!audio || !progressBar) return;

    const rect = progressBar.getBoundingClientRect();
    const clickPosition = (e.clientX - rect.left) / rect.width;
    const newTime = clickPosition * audio.duration;
    
    audio.currentTime = newTime;
    setProgress(clickPosition * 100);
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <div className={styles.audioPlayer}>
      <div className={styles.title}>{title}</div>
      
      <audio ref={audioRef} src={src} />
      
      {!hideControls && (
        <div className={styles.controls}>
          <button 
            className={styles.playButton} 
            onClick={togglePlay}
          >
            {isPlaying ? '일시정지' : '재생'}
          </button>
          
          <div className={styles.timeInfo}>
            <span>{formatTime(audioRef.current?.currentTime || 0)}</span>
            <span>/</span>
            <span>{formatTime(duration)}</span>
          </div>
        </div>
      )}
      
      <div className={styles.progressContainer} onClick={handleProgressBarClick}>
        <div className={styles.progressBackground}>
          {waveformSrc ? (
            <div 
              ref={waveformRef} 
              className={styles.waveform} 
              style={{ backgroundImage: `url(${waveformSrc})` }}
            />
          ) : (
            <div className={styles.progressBar} />
          )}
        </div>
        <div 
          className={styles.progressCurrent} 
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
});

AudioPlayer.displayName = 'AudioPlayer';

export default AudioPlayer;
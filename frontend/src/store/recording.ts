'use client'

import { create } from 'zustand';

// 녹음 정보 인터페이스
export interface Recording {
  id: string;
  songId: string;
  blob: Blob;
  url: string;
  duration: number;
  createdAt?: Date;
}

// 녹음 스토어 인터페이스
interface RecordingState {
  currentRecording: Recording | null;
  recordings: Recording[];
  isRecording: boolean;
  
  // 현재 녹음 설정
  setCurrentRecording: (recording: Recording) => void;
  
  // 녹음 목록에 추가
  addRecording: (recording: Recording) => void;
  
  // 녹음 목록에서 제거
  removeRecording: (id: string) => void;
  
  // 녹음 상태 설정
  setIsRecording: (isRecording: boolean) => void;
  
  // 녹음 관련 리소스 정리
  cleanup: () => void;
}

// 녹음 스토어 생성
// 녹음 관련 리소스 정리를 위한 헬퍼 함수
export const cleanupRecordingResources = () => {
  const store = useRecordingStore.getState();
  store.cleanup();
};

export const useRecordingStore = create<RecordingState>((set, get) => ({
  currentRecording: null,
  recordings: [],
  isRecording: false,
  
  setCurrentRecording: (recording) => {
    // 이전 녹음이 있으면 URL 정리
    const prev = get().currentRecording;
    if (prev && prev.url) {
      URL.revokeObjectURL(prev.url);
    }
    
    set({ currentRecording: recording });
    
    // 녹음 목록에 자동 추가
    get().addRecording(recording);
  },
  
  addRecording: (recording) => {
    // 이미 같은 ID의 녹음이 있는지 확인
    const exists = get().recordings.some(r => r.id === recording.id);
    
    if (!exists) {
      set(state => ({
        recordings: [...state.recordings, {
          ...recording,
          createdAt: recording.createdAt || new Date()
        }]
      }));
    }
  },
  
  removeRecording: (id) => {
    // 해당 녹음 찾기
    const recording = get().recordings.find(r => r.id === id);
    
    // URL 정리
    if (recording && recording.url) {
      URL.revokeObjectURL(recording.url);
    }
    
    // 현재 녹음인 경우 현재 녹음도 초기화
    if (get().currentRecording?.id === id) {
      set({ currentRecording: null });
    }
    
    // 목록에서 제거
    set(state => ({
      recordings: state.recordings.filter(r => r.id !== id)
    }));
  },
  
  setIsRecording: (isRecording) => {
    set({ isRecording });
  },
  
  cleanup: () => {
    // 모든 녹음 URL 정리
    const { recordings, currentRecording } = get();
    
    recordings.forEach(recording => {
      if (recording.url) {
        URL.revokeObjectURL(recording.url);
      }
    });
    
    if (currentRecording?.url) {
      URL.revokeObjectURL(currentRecording.url);
    }
    
    // 상태 초기화
    set({
      currentRecording: null,
      recordings: [],
      isRecording: false
    });
  }
}));
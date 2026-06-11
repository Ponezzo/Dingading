'use client'
import { create } from "zustand"
import { SongByInstrumentApi } from "../../generated/api"
import { SongByInstrumentDTO } from "../../generated/model/song-by-instrument-dto"
import { useConfigStore } from "./config"

// API에서 요구하는 Instrument 열거형 타입 정의
// (이미 generated/api에 정의되어 있을 수도 있음)
type InstrumentEnum = 'VOCAL' | 'GUITAR' | 'DRUM' | 'BASS';

interface SongByInstrumentState {
  songByInstrument: SongByInstrumentDTO | null
  songUrl: string | null
  loading: boolean
  error: string | null
  
  // 악기별 곡 상세 정보 조회
  fetchSongByInstrument: (songByInstrumentId: number) => Promise<void>
  
  // 악기별 곡 URL 조회
  fetchSongByInstrumentUrl: (songByInstrumentId: number) => Promise<void>
  
  // 악기, 티어별 곡 URL 조회 - 파라미터 타입 수정
  fetchSongByInstrumentUrlBySongIdAndInstrument: (songId: number, instrument: InstrumentEnum) => Promise<void>
}

// Zustand store 생성
export const useSongByInstrumentStore = create<SongByInstrumentState>((set) => ({
  songByInstrument: null,
  songUrl: null,
  loading: false,
  error: null,
  
  fetchSongByInstrument: async (songByInstrumentId: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const songByInstrumentApi = new SongByInstrumentApi(apiConfig)
      
      const response = await songByInstrumentApi.getSongByInstrument({ songByInstrumentId })
      
      set({
        songByInstrument: response.data,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("songbyinstrument.ts, Error fetching song by instrument:", error)
      set({
        loading: false,
        error: error instanceof Error ? error.message : "알 수 없는 오류가 발생했습니다."
      })
    }
  },
  
  // instrument 파라미터 타입을 InstrumentEnum으로 수정
  fetchSongByInstrumentUrlBySongIdAndInstrument: async (songId: number, instrument: InstrumentEnum) => {
    try {
      set({ loading: true, error: null });
      
      const configStore = useConfigStore.getState();
      const apiConfig = configStore.apiConfig;
      
      const songByInstrumentApi = new SongByInstrumentApi(apiConfig);
      
      // POST 메서드 사용 - API 명세서에 따르면 이 엔드포인트는 POST를 사용함
      const response = await songByInstrumentApi.getSongByInstrumentUrlBySongIdAndInstrument({
        songId: songId,
        instrument: instrument  // 이제 올바른 타입의 값이 전달됨
      });
      
      // 응답 데이터에서 URL 추출
      const url = response.data.songByInstrumentUrl;
      
      if (!url) {
        throw new Error('유효한 음원 URL이 없습니다.');
      }
      
      set({ songUrl: url, loading: false });
      console.log('음원 URL 로드 성공:', url);
    } catch (error) {
      console.error('음원 URL 로드 실패:', error);
      set({ 
        error: '음원 URL을 가져오는데 실패했습니다.', 
        loading: false,
        songUrl: '' // 에러 시 URL 초기화
      });
    }
  }, 
  
  fetchSongByInstrumentUrl: async (songByInstrumentId: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const songByInstrumentApi = new SongByInstrumentApi(apiConfig)
      
      const response = await songByInstrumentApi.requestSongByInstrumentUrl({ songByInstrumentId })
      
      set({
        songUrl: response.data.songByInstrumentUrl,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("Error fetching song by instrument URL:", error)
      set({
        loading: false,
        error: error instanceof Error ? error.message : "알 수 없는 오류가 발생했습니다."
      })
    }
  }
}))
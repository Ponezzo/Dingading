'use client'

import { create } from "zustand"
import { useConfigStore } from "./config"
import { SongApi, SongApiGetSongsByInstrumentAndTierRequest, GetSongsByInstrumentAndTierInstrumentEnum, GetSongsByInstrumentAndTierTierEnum } from "@generated/api"
import axios, { AxiosError } from "axios"
// import { GetSongsByTierTierEnum, SongApi, SongApiGetSongsByTierRequest } from "@generated/api"
// import { useConfigStore } from "./config"

// 타입 정의
interface Song {
  songId: number
  title: string
  artist: string
  youtubeUrl: string
  description?: string  // description을 옵셔널 필드로 변경
  audioUrl?: string 
  waveforUrl?: string 
}

// API 응답 타입 정의
interface SongPageResponse {
  content: Array<{
    songId: number
    title: string
    artist: string
    youtubeUrl: string
    description?: string
    songFilename?: string
    instruments?: Array<{
      songByInstrumentId: number
      songId: number
      songInstrumentPackId: number
      songByInstrumentExFilename: string
      songByInstrumentFilename: string
      songByInstrumentAnalysisJson: string
      instrument: string
      tier: string
    }>
  }>
  last: boolean
  first: boolean
  totalElements: number
  totalPages: number
  size: number
  number: number
  numberOfElements: number
  empty: boolean
  pageable?: {
    pageNumber: number
    pageSize: number
    sort: null | {
      empty: boolean
      sorted: boolean
      unsorted: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
}

interface SongsState {
  songs: Song[]
  loading: boolean
  error: string | null
  fetchSongs: (instrument: string, tier: string) => Promise<void>
}

// Zustand store 생성
export const useSongsStore = create<SongsState>((set) => ({
  songs: [],
  loading: false,
  error: null,
  
  fetchSongs: async (instrument: string, tier: string) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const songApi = new SongApi(apiConfig)
      
      // 파라미터 로깅
      console.log("API 요청 파라미터:", { instrument, tier })
      
      const params: SongApiGetSongsByInstrumentAndTierRequest = {
        instrument: instrument.toUpperCase() as GetSongsByInstrumentAndTierInstrumentEnum,
        tier: tier.toUpperCase() as GetSongsByInstrumentAndTierTierEnum
      }
      
      // 변환된 파라미터 로깅
      console.log("변환된 API 요청 파라미터:", params)
      
      const response = await songApi.getSongsByInstrumentAndTier(params)
      console.log("songs.ts fetchSongs response : ", response.data)
      
      // 응답 데이터를 Song[] 형태로 변환
      const songPageResponse = response.data as unknown as SongPageResponse
      const formattedSongs = songPageResponse.content.map((song) => ({
        songId: song.songId,
        title: song.title,
        artist: song.artist,
        youtubeUrl: song.youtubeUrl,
        description: song.description,
        audioUrl: song.songFilename,
        waveforUrl: song.instruments?.find(i => i.instrument === instrument.toUpperCase())?.songByInstrumentAnalysisJson
      }))
      
      set({
        songs: formattedSongs,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("Error fetching songs:", error)
      // 더 자세한 에러 정보 로깅
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response) {
          // 서버가 응답을 반환한 경우
          console.error("서버 응답 데이터:", axiosError.response.data)
          console.error("서버 응답 상태:", axiosError.response.status)
          console.error("서버 응답 헤더:", axiosError.response.headers)
        } else if (axiosError.request) {
          // 요청이 전송되었지만 응답을 받지 못한 경우
          console.error("요청 정보:", axiosError.request)
        } else {
          // 요청 설정 중 오류가 발생한 경우
          console.error("에러 메시지:", axiosError.message)
        }
      } else {
        console.error("알 수 없는 에러:", error)
      }
      
      set({
        loading: false,
        error: error instanceof Error ? error.message : "알 수 없는 오류가 발생했습니다."
      })
    }
  }
}))
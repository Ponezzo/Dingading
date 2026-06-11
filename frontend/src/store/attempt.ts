// src/store/attempt.ts
'use client'

import { create } from "zustand"
import { AttemptApi, AttemptApiGetAttemptRequest } from "../../generated/api"
import { AttemptDTO } from "../../generated/model/attempt-dto"
import { useConfigStore } from "./config"

interface AttemptState {
  attempt: AttemptDTO | null
  loading: boolean
  error: string | null
  
  // 특정 시도 기록 상세 정보 조회
  fetchAttempt: (attemptId: number) => Promise<void>
  
  // 더미 데이터 설정 (API 연결이 안될 경우 테스트용)
  setDummyData: () => void
}

// 더미 데이터 생성
const dummyAttemptData: AttemptDTO = {
  attemptId: 1,
  songByInstrumentId: 1,
  status: 'SUCCESS',
  gameType: 'RANK',
  rankType: 'CHALLENGE',
  tuneScore: 85,
  toneScore: 90,
  beatScore: 88,
  totalScore: 92,
  createdAt: new Date().toISOString(),
  // songByInstrument는 API 응답에 따라 설정할 수 있음
}

// Zustand store 생성
export const useAttemptStore = create<AttemptState>((set) => ({
  attempt: null,
  loading: false,
  error: null,
  
  fetchAttempt: async (attemptId: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const attemptApi = new AttemptApi(apiConfig)
      
      const requestParams: AttemptApiGetAttemptRequest = {
        attemptId: attemptId
      }
      
      const response = await attemptApi.getAttempt(requestParams)
      
      set({
        attempt: response.data,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("Error fetching attempt:", error)
      set({
        loading: false,
        error: error instanceof Error ? error.message : "알 수 없는 오류가 발생했습니다."
      })
      
      // API 호출 실패 시 자동으로 더미 데이터 사용
      set({ attempt: dummyAttemptData })
    }
  },
  
  // 테스트용 더미 데이터 설정 함수
  setDummyData: () => {
    set({
      attempt: dummyAttemptData,
      loading: false,
      error: null
    })
  }
}))
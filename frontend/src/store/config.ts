// config.ts
'use client'

import { Configuration } from "@generated/configuration"
import { create } from "zustand"

// 토큰을 로컬 스토리지에 저장
const saveTokenToStorage = (token: string) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('accessToken', token);
  }
}

// memberId를 로컬 스토리지에 저장
const saveMemberIdToStorage = (memberId: string) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('memberId', memberId);
  }
}

// 토큰을 로컬 스토리지에서 가져오기
const getTokenFromStorage = (): string | null => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('accessToken');
  }
  return null;
}

// memberId를 로컬 스토리지에서 가져오기
const getMemberIdFromStorage = (): string | null => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('memberId');
  }
  return null;
}

const baseUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}`

// Zustand 스토어 타입 정의 
interface ConfigState {
  apiConfig: Configuration
  token: string | null
  memberId: string | null
  setAccessToken: (token: string) => void
  getAccessToken: () => string | null
  setMemberId: (memberId: string) => void
  getMemberId: () => string | null
}

const initialToken = getTokenFromStorage();
const initialMemberId = getMemberIdFromStorage();

// Zustand 스토어 생성
export const useConfigStore = create<ConfigState>((set, get) => ({
  // API 설정 초기화
  apiConfig: new Configuration({
    basePath: baseUrl,
    // accessToken: `Bearer ${initialToken || ""}`, // 문자열로 초기화
    baseOptions: {
      headers: {
        // 'Content-Type': 'application/json', 
        ...(initialToken ? { 'Authorization': `Bearer ${initialToken}` } : {})

      }
    }
  }),
  
  token: initialToken,
  memberId: initialMemberId,
  
  // 액세스 토큰 설정 함수
  setAccessToken: (token: string) => {
    saveTokenToStorage(token);
    
    console.log("Setting accessToken:", token);
    
    set((state) => ({
      token: token,
      apiConfig: new Configuration({
        ...state.apiConfig,
        baseOptions: {
          ...state.apiConfig.baseOptions,
          headers: {
            ...state.apiConfig.baseOptions?.headers,
            'Authorization': `Bearer ${token}`
          }
        }
      }),
    }));
  },
  
  // 액세스 토큰 가져오기 함수
  getAccessToken: () => {
    return get().token;
  },

  // memberId 설정 함수
  setMemberId: (memberId: string) => {
    saveMemberIdToStorage(memberId);
    set({ memberId });
  },

  // memberId 가져오기 함수
  getMemberId: () => {
    return get().memberId;
  },
}));
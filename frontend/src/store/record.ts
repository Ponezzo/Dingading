'use client'

import { create } from "zustand"
import { useConfigStore } from "./config"
import { RecordApi, RecordApiCreateRecordRequest } from "../../generated/api/record-api"
import { RecordCreateRequestDTO } from "../../generated/model/record-create-request-dto"
import axios, { AxiosError } from "axios"
import { createAsyncThunk } from "@reduxjs/toolkit"

const apiConfig = useConfigStore.getState().apiConfig
const recordApi = new RecordApi(apiConfig)

// 녹음 유형
type RecordType = 'CHALLENGE' | 'LIVE_HOUSE' | 'PRACTICE'

// 녹음 DTO 인터페이스
interface LocalRecordDTO {
  recordId: number;
  memberId: string;
  attemptId?: number;
  dtype: RecordType;
  title: string;
  recordUrl: string;
  createdAt?: string;
}

// 녹음 생성 요청 DTO 인터페이스
interface LocalRecordCreateRequestDTO {
  attemptId?: number;
  dtype: RecordType;
  title: string;
}

// 페이지네이션 응답 인터페이스
interface PageResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

// 녹음 상태 인터페이스
interface RecordState {
  record: LocalRecordDTO | null;
  records: LocalRecordDTO[] | null;
  loading: boolean;
  error: string | null;
  
  // 녹음 생성
  createRecord: (recordInfo: LocalRecordCreateRequestDTO, audioFile: File) => Promise<void>;
  
  // 도전별 녹음 조회
  getAttemptRecords: (attemptId: number) => Promise<void>;
  
  // 회원별 녹음 목록 조회
  getMemberRecords: (memberId: string, dtype?: RecordType, page?: number, size?: number) => Promise<void>;
  
  // 녹음 상세 조회
  getRecord: (recordId: number) => Promise<void>;
  
  // 녹음 목록 조회
  getRecords: (memberId?: string, dtype?: RecordType, page?: number, size?: number) => Promise<void>;
}

// Zustand store 생성
export const useRecordStore = create<RecordState>((set) => ({
  record: null,
  records: null,
  loading: false,
  error: null,
  
  // 녹음 생성
  createRecord: async (recordInfo: LocalRecordCreateRequestDTO, audioFile: File) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      // const baseURL = apiConfig.basePath || process.env.NEXT_PUBLIC_API_BASE_URL
      
      const recordApi = new RecordApi(apiConfig)

      const request : RecordCreateRequestDTO = {
        dtype : recordInfo.dtype , title : recordInfo.title , attemptId : recordInfo.attemptId
      }

      const params : RecordApiCreateRecordRequest = {
        recordInfo : request, 
        audioFile : audioFile
      }

      const response = await recordApi.createRecord(params)
      
      set({
        record: response.data,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("녹음 생성 오류:", error)
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        if (axiosError.response) {
          console.error("서버 응답 데이터:", axiosError.response.data)
          console.error("서버 응답 상태:", axiosError.response.status)
        } else if (axiosError.request) {
          console.error("요청 정보:", axiosError.request)
        } else {
          console.error("에러 메시지:", axiosError.message)
        }
      } else {
        console.error("알 수 없는 에러:", error)
      }
      
      set({
        loading: false,
        error: error instanceof Error ? error.message : "녹음 생성 중 오류가 발생했습니다."
      })
    }
  }, 
  
  // 도전별 녹음 조회
  getAttemptRecords: async (attemptId: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const recordApi = new RecordApi(apiConfig)
      
      console.log("도전별 녹음 조회 요청:", { attemptId })
      
      const response = await recordApi.getAttemptRecords({ attemptId })
      console.log("도전별 녹음 조회 응답:", response.data)
      
      set({
        record: response.data,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("도전별 녹음 조회 오류:", error)
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        if (axiosError.response) {
          console.error("서버 응답 데이터:", axiosError.response.data)
          console.error("서버 응답 상태:", axiosError.response.status)
        } else if (axiosError.request) {
          console.error("요청 정보:", axiosError.request)
        } else {
          console.error("에러 메시지:", axiosError.message)
        }
      } else {
        console.error("알 수 없는 에러:", error)
      }
      
      set({
        loading: false,
        error: error instanceof Error ? error.message : "도전별 녹음 조회 중 오류가 발생했습니다."
      })
    }
  },
  
  // 회원별 녹음 목록 조회
  getMemberRecords: async (memberId: string, dtype?: RecordType, page?: number, size?: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const recordApi = new RecordApi(apiConfig)
      
      console.log("회원별 녹음 목록 조회 요청:", { memberId, dtype, page, size })
      
      // API 호출 시 페이지네이션 파라미터를 options 객체를 통해 전달
      const options = {
        params: {
          page: page || 0,
          size: size || 10
        }
      }
      
      const response = await recordApi.getMemberRecords({
        memberId,
        dtype
      }, options)
      
      console.log("회원별 녹음 목록 조회 응답:", response.data)
      
      const pageResponse = response.data as PageResponse<LocalRecordDTO>
      set({
        records: pageResponse.content,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("회원별 녹음 목록 조회 오류:", error)
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        if (axiosError.response) {
          console.error("서버 응답 데이터:", axiosError.response.data)
          console.error("서버 응답 상태:", axiosError.response.status)
        } else if (axiosError.request) {
          console.error("요청 정보:", axiosError.request)
        } else {
          console.error("에러 메시지:", axiosError.message)
        }
      } else {
        console.error("알 수 없는 에러:", error)
      }
      
      set({
        loading: false,
        error: error instanceof Error ? error.message : "회원별 녹음 목록 조회 중 오류가 발생했습니다."
      })
    }
  },
  
  // 녹음 상세 조회
  getRecord: async (recordId: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const recordApi = new RecordApi(apiConfig)
      
      console.log("녹음 상세 조회 요청:", { recordId })
      
      const response = await recordApi.getRecord({ recordId })
      console.log("녹음 상세 조회 응답:", response.data)
      
      set({
        record: response.data,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("녹음 상세 조회 오류:", error)
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        if (axiosError.response) {
          console.error("서버 응답 데이터:", axiosError.response.data)
          console.error("서버 응답 상태:", axiosError.response.status)
        } else if (axiosError.request) {
          console.error("요청 정보:", axiosError.request)
        } else {
          console.error("에러 메시지:", axiosError.message)
        }
      } else {
        console.error("알 수 없는 에러:", error)
      }
      
      set({
        loading: false,
        error: error instanceof Error ? error.message : "녹음 상세 조회 중 오류가 발생했습니다."
      })
    }
  },
  
  // 녹음 목록 조회
  getRecords: async (memberId?: string, dtype?: RecordType, page?: number, size?: number) => {
    set({ loading: true, error: null })
    try {
      const apiConfig = useConfigStore.getState().apiConfig
      const recordApi = new RecordApi(apiConfig)
      
      console.log("녹음 목록 조회 요청:", { memberId, dtype, page, size })
      
      // API 호출 시 페이지네이션 파라미터를 options 객체를 통해 전달
      const options = {
        params: {
          page: page || 0,
          size: size || 10
        }
      }
      
      const response = await recordApi.getRecords({
        memberId,
        dtype
      }, options)
      
      console.log("녹음 목록 조회 응답:", response.data)
      
      const pageResponse = response.data as PageResponse<LocalRecordDTO>
      set({
        records: pageResponse.content,
        loading: false,
        error: null
      })
    } catch (error) {
      console.error("녹음 목록 조회 오류:", error)
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        if (axiosError.response) {
          console.error("서버 응답 데이터:", axiosError.response.data)
          console.error("서버 응답 상태:", axiosError.response.status)
        } else if (axiosError.request) {
          console.error("요청 정보:", axiosError.request)
        } else {
          console.error("에러 메시지:", axiosError.message)
        }
      } else {
        console.error("알 수 없는 에러:", error)
      }
      
      set({
        loading: false,
        error: error instanceof Error ? error.message : "녹음 목록 조회 중 오류가 발생했습니다."
      })
    }
  }
}))

export const createRecord = createAsyncThunk(
  'record/createRecord',
  async ({ recordInfo, audioFile }: { recordInfo: LocalRecordCreateRequestDTO; audioFile: File }) => {
    const formData = new FormData();
    formData.append('recordInfo', new Blob([JSON.stringify(recordInfo)], { type: 'application/json' }));
    formData.append('audioFile', audioFile);
    
    const response = await recordApi.createRecord({ recordInfo, audioFile });
    return response.data;
  }
);

export const getMemberRecords = createAsyncThunk(
  'record/getMemberRecords',
  async ({ memberId, page, size }: { memberId: string; page: number; size: number }) => {
    const options = {
      params: {
        page: page || 0,
        size: size || 10
      }
    }
    
    const response = await recordApi.getMemberRecords({
      memberId
    }, options);
    return response.data;
  }
);

export const getRecords = createAsyncThunk(
  'record/getRecords',
  async ({ page, size }: { page: number; size: number }) => {
    const options = {
      params: {
        page: page || 0,
        size: size || 10
      }
    }
    
    const response = await recordApi.getRecords({}, options);
    return response.data;
  }
);

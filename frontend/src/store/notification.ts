'use client'

import { create } from "zustand"
import { NotificationDTO } from "../components/notice"
import { useAuthStore } from "./auth"

interface NotificationState {
  notifications: NotificationDTO[]
  loading: boolean
  error: string | null
  
  // 알림 목록 가져오기
  fetchNotification: () => Promise<void>
  
  // 알림 읽음 처리
  markAsRead: (notificationId: number) => Promise<void>
  
  // 알림 모두 읽음 처리
  markAllAsRead: () => Promise<void>
  
  // 새 알림 추가
  addNotification: (notification: NotificationDTO) => void
}

export const useNotificationStore = create<NotificationState>((set) => ({
  notifications: [],
  loading: false,
  error: null,
  
  fetchNotification: async () => {
    set({ loading: true, error: null });
    
    try {
      const { accessToken } = useAuthStore.getState();
      
      if (!accessToken) {
        throw new Error('인증 토큰이 없습니다');
      }
      
      const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || '';
      const response = await fetch(`${baseUrl}/api/notifications?unreadOnly=false&page=0&size=10`, {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`API 호출 실패: ${response.status}`);
      }
      
      const data = await response.json();
      
      if (data && data.content) {
        set({ 
          notifications: data.content, 
          loading: false 
        });
      } else {
        set({ 
          notifications: [], 
          loading: false 
        });
      }
    } catch (error) {
      console.error('알림을 가져오는데 실패했습니다:', error);
      set({ 
        error: error instanceof Error ? error.message : '알림을 가져오는데 실패했습니다',
        loading: false 
      });
    }
  },
  
  markAsRead: async (notificationId: number) => {
    try {
      const { accessToken } = useAuthStore.getState();
      
      if (!accessToken) {
        throw new Error('인증 토큰이 없습니다');
      }
      
      // 디버깅을 위한 로깅 추가
      console.log(`API 호출 시작: PUT /api/notifications/${notificationId}/read`);
      
      const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || '';
      const url = `${baseUrl}/api/notifications/${notificationId}/read`;
      console.log(`완전한 URL: ${url}`);
      
      const response = await fetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        }
      });
      
      // 응답 상태 로깅
      console.log(`API 응답 상태: ${response.status}`);
      
      if (!response.ok) {
        // 응답 본문 추가 로깅
        const errorText = await response.text();
        console.error(`API 오류 응답: ${errorText}`);
        throw new Error(`API 호출 실패: ${response.status}`);
      }
      
      const data = await response.json();
      console.log(`API 응답 데이터:`, data);
      
      // 상태 업데이트
      set(state => ({
        notifications: state.notifications.map(notification => 
          notification.notificationId === notificationId 
            ? {...notification, readOrNot: true} 
            : notification
        )
      }));
    } catch (error) {
      console.error('알림 읽음 처리에 실패했습니다:', error);
      set({ 
        error: error instanceof Error ? error.message : '알림 읽음 처리에 실패했습니다'
      });
      throw error; // 에러를 다시 던져서 컴포넌트에서 처리할 수 있게 함
    }
  },
  
  markAllAsRead: async () => {
    try {
      const { accessToken } = useAuthStore.getState();
      
      if (!accessToken) {
        throw new Error('인증 토큰이 없습니다');
      }
      
      const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || '';
      const response = await fetch(`${baseUrl}/api/notifications/read-all`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`API 호출 실패: ${response.status}`);
      }
      
      // 모든 알림을 읽음 상태로 설정
      set(state => ({
        notifications: state.notifications.map(notification => ({
          ...notification, 
          readOrNot: true
        }))
      }));
    } catch (error) {
      console.error('모든 알림 읽음 처리에 실패했습니다:', error);
      set({ 
        error: error instanceof Error ? error.message : '모든 알림 읽음 처리에 실패했습니다'
      });
    }
  },
  
  addNotification: (notification: NotificationDTO) => {
    set(state => ({
      notifications: [notification, ...state.notifications]
    }));
  }
}));
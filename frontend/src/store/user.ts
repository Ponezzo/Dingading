import { MemberApi } from "@generated/api";
import { create } from "zustand";
import { useConfigStore } from "./config";

// 사용자 정보 타입 정의
interface User {
  // MemberDTO
  memberId: string; // 회원 고유 식별자
  username: string; // 사용자 로그인 아이디
  nickname: string; // 사용자 닉네임
  favoriteBandId: number; // 즐겨찾기 된 밴드 ID (integer -> number)
  profileImgUrl: string; // 프로필 이미지 URL
  createdAt: string; // 계정 생성 일시
}

interface UserState {
  user: User | null;
  loading: boolean;
  error: string | null;
  fetchUser: () => Promise<void>;
  clearUser: () => void;
}

export const useUserStore = create<UserState>((set) => ({
  user: null,
  loading: false,
  error: null,
  
  fetchUser: async () => {
    try {
      set({ loading: true, error: null });
      
      const configStore = useConfigStore.getState();
      const apiConfig = configStore.apiConfig;
      
      console.log("user.ts getCurrentMember request:", {
        basePath : apiConfig.basePath, 
        hasToken : !!configStore.token, 
        tokenLength : configStore.token?.length || 0
      });

      const memberApi = new MemberApi(apiConfig);
      const response = await memberApi.testLogin();
      
      console.log("user.ts getCurrentMember response  :", response);
      
      set({
        // user: response.data,
        loading: false
      });
    } catch (error) {
      console.error("user.ts getCurrentMember error:", error);
      set({ 
        error: error instanceof Error ? error.message : "사용자 정보를 가져오는 중 오류가 발생했습니다",
        loading: false 
      });
    }
  },
  
  clearUser: () => set({ user: null, error: null })
}));
'use client'

import { create } from "zustand"
interface AuthState {
  accessToken: string // JWT 액세스 토큰
  memberId: string // 회원 ID
  isLoggedIn: boolean // 로그인 상태
  loading: boolean // 로딩 상태
  error: string | null // 에러 상태
  // refreshToken?: string // JWT 리프레시 토큰 
  // expiresIn?: number // 토큰 만료 시간(초) 
  // tokenType: string // 토큰 타입 
  loginId : string 
  nickname : string 
  profileImg : string 
  
  // Google 로그인 메서드
  // loginWithGoogle: (googleIdToken: string) => Promise<void>
  // 로그아웃 메서드
  logout: () => void
  // 사용자 정보 가져오기
  // fetchUser: () => Promise<void>
  loginWithToken : (token : string, loginId : string, nickname : string, profileImg : string) => void 
  initAuth : () => void 
}

export const useAuthStore = create<AuthState>((set) => ({
  // 초기 상태 설정
  accessToken: '', 
  memberId: '',
  isLoggedIn: false,
  loading: false,
  error: null,
  loginId : '', 
  nickname : "",
  profileImg : "",  

  // Google 로그인 메서드
  // loginWithGoogle: async (googleIdToken: string) => {
  //   set({ loading: true, error: null });

    // try {
    //   const configStore = useConfigStore.getState();
    //   const apiConfig = configStore.apiConfig;
    //   const authApi = new AuthApi(apiConfig);

    //   // Google 인증 요청 파라미터 구성
    //   const params: AuthApiAuthenticateGoogleRequest = {
    //     googleAuthRequest : {
    //       idToken: googleIdToken
    //     }
    //   };

    //   // Google 인증 API 호출
    //   const response = await authApi.authenticateGoogle(params);
    //   // 응답에서 필요한 데이터 추출
    //   const { accessToken, memberId } = response.data;
      
    //   // 상태 업데이트
    //   set({ 
    //     accessToken, 
    //     memberId,
    //     isLoggedIn: true,
    //     loading: false
    //   });
      
    //   console.log("googleAuthLogin success : ", "accessToken : ", accessToken )
      
    //   // 토큰을 ConfigStore에도 저장 
    //   configStore.setAccessToken(accessToken)

    //   // 토큰을 로컬 스토리지에 저장 (필요한 경우)
    //   if (typeof window !== 'undefined') {
    //     localStorage.setItem('accessToken', accessToken);
    //     // if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
    //   }
    // } catch (error) {
    //   console.error('Google 로그인 실패:', error);
    //   set({ 
    //     loading: false, 
    //     error: error instanceof Error ? error.message : '구글 로그인 중 오류가 발생했습니다.'
    //   });
    // }
  // },
  
  loginWithToken : (token, loginId, nickname, profileImg) => {
    let memberId = '';
    try {
      const payload = token.split('.')[1];
      const decodedPayload = JSON.parse(atob(payload));
      memberId = decodedPayload.id || '';
    } catch (error) {
      console.error('토큰 디코딩 오류:', error);
    }
    
    // 상태 업데이트 및 로컬 스토리지 저장
    set({ 
      accessToken: token, 
      memberId,
      loginId, 
      nickname: decodeURIComponent(nickname),
      profileImg,
      isLoggedIn: true,
      loading: false
    });

    // localStorage에 모든 필요한 정보 저장
    if (typeof window !== 'undefined') {
      localStorage.setItem('accessToken', token);
      localStorage.setItem('memberId', memberId);
      localStorage.setItem('loginId', loginId);
      localStorage.setItem('nickname', decodeURIComponent(nickname));
      localStorage.setItem('profileImg', profileImg || '');
      
      console.log('localStorage 저장 완료:', {
        token,
        memberId,
        loginId,
        nickname: decodeURIComponent(nickname),
        profileImg
      });
    }
  }, 

  // 로그아웃 메서드
  logout: () => {
    console.log("auth.ts logout 함수 실행")
    // 상태 초기화
    set({
      accessToken: '',
      memberId: '',
      isLoggedIn: false
    });

    // 로컬 스토리지에서 토큰 제거
    if (typeof window !== 'undefined') {
      console.log("로컬 스토리지 항목 제거 시도");
      localStorage.removeItem('accessToken');
      localStorage.removeItem("memberId");
      localStorage.removeItem("loginId");  // 이 항목도 제거
      localStorage.removeItem("nickname");  // 이 항목도 제거
      localStorage.removeItem("profileImg");  // 이 항목도 제거
      
      // 제거 후 확인
      console.log("제거 후 토큰 존재 여부:", !!localStorage.getItem('accessToken'));
    }
  },

  // 로컬 스토리지에서 사용자 정보 가져오기
  initAuth: () => {
    if (typeof window === 'undefined') return;
    
    const token = localStorage.getItem('accessToken');
    const memberId = localStorage.getItem('memberId');
    const loginId = localStorage.getItem('loginId');
    const nickname = localStorage.getItem('nickname');
    const profileImg = localStorage.getItem('profileImg');
    
    if (token && memberId) {
      set({
        accessToken: token,
        memberId,
        loginId: loginId || '',
        nickname: nickname || '',
        profileImg: profileImg || '',
        isLoggedIn: true
      });
    }
  },


  // // 사용자 정보 가져오기 - 현재 토큰 유효성 확인 
  // fetchUser: async () => {
  //   const { accessToken } = get();
    
  //   // 토큰이 없으면 로그인 상태가 아님
  //   if (!accessToken) {
  //     set({ isLoggedIn: false });
  //     return;
  //   }

  //   if (typeof window !== "undefined") {
  //     const storedMemberId = localStorage.getItem("memberId")
  //     const storedToken = localStorage.getItem("accessToken")
      
  //     console.log("auth.ts , storedMemberId : ", storedMemberId)
  //     console.log("auth.ts , storedToken : ", storedToken)

  //     if (storedToken && storedMemberId) {
  //       set({
  //         accessToken : storedToken, 
  //         memberId : storedMemberId, 
  //         isLoggedIn : true
  //       })
  //     }
  //   }

  //   // set({ loading: true, error: null });
  // }
}))
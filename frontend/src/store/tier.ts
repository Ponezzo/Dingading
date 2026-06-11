import { create } from "zustand";
import { useConfigStore } from "./config";
import { MemberRankApi } from "@generated/api";

// 악기 및 티어 타입 정의
type InstrumentType = 'GUITAR' | 'BASS' | 'DRUM' | 'VOCAL';
type TierType = 'UNRANKED' | 'IRON' | 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';

// 티어 정보 인터페이스
interface TierState {
  instrumentTiers: Record<InstrumentType, TierType>; // 각 악기별 티어 정보
  isLoading: boolean;
  error: string | null;
  fetchMyRanks: () => Promise<void>; // 내 악기별 랭크 정보 조회
}

export const useTierStore = create<TierState>((set) => ({
  // 초기 악기별 티어 정보
  instrumentTiers: {
    GUITAR: 'UNRANKED',
    BASS: 'UNRANKED',
    DRUM: 'UNRANKED',
    VOCAL: 'UNRANKED'
  },
  isLoading: false,
  error: null,
  fetchMyRanks: async () => {
    try {
      // 로딩 상태 시작
      set({ isLoading: true, error: null });
      
      // 설정 스토어에서 API 설정 가져오기
      const configStore = useConfigStore.getState();
      const apiConfig = configStore.apiConfig;
      
      // MemberRank API 인스턴스 생성
      const memberRankApi = new MemberRankApi(apiConfig);
      
      // API 호출: 현재 로그인한 회원의 악기별 랭크 정보 조회
      // openapi.yaml에 정의된 /api/members/me/ranks 엔드포인트 사용
      const response = await memberRankApi.getMyRanks();
      
      // 가져온 데이터로 악기별 티어 정보 업데이트
      const formattedTiers: Record<InstrumentType, TierType> = {
        GUITAR: 'UNRANKED',
        BASS: 'UNRANKED',
        DRUM: 'UNRANKED',
        VOCAL: 'UNRANKED'
      };
      
      // API 응답 데이터를 formattedTiers 객체에 매핑
      if (response.data && Array.isArray(response.data)) {
        response.data.forEach((rank) => {
          if (rank.instrument && rank.tier) {
            formattedTiers[rank.instrument as InstrumentType] = rank.tier as TierType;
          }
        });
      }
      
      // 상태 업데이트
      set({
        instrumentTiers: formattedTiers,
        isLoading: false
      });
      
      console.log("내 악기별 티어 정보 불러오기 성공:", formattedTiers);
    } catch (error) {
      console.error("내 악기별 티어 정보 불러오기 실패:", error);
      
      // 에러 상태 설정
      set({
        error: "악기별 티어 정보를 불러오는데 실패했습니다.",
        isLoading: false
      });
    }
  }
}));
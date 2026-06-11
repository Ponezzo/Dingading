'use client'

import { useEffect } from "react"
import { useParams } from "next/navigation"
import Tiercard from "@/components/tiercard"
import styles from "./tierpage.module.css"
import { useTierStore } from "@/store/tier"
import { useSongsStore } from "@/store/songs"
import SongCarousel from "@/components/songcarousel"

// 악기 타입과 티어 타입 정의
type InstrumentType = 'GUITAR' | 'BASS' | 'DRUM' | 'VOCAL';
type TierType = 'UNRANKED' | 'IRON' | 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';

export default function TierPage() {
  // URL 파라미터에서 악기와 티어 정보 가져오기
  const { instrument, tier } = useParams();
  
  // 기본값 설정 및 타입 단언
  const nowTier = typeof tier === 'string' ? tier as TierType : "IRON" as TierType;
  const nowInstrument = typeof instrument === 'string' ? instrument as InstrumentType : "DRUM" as InstrumentType;
  
  // 티어 스토어와 곡 스토어 사용
  const { instrumentTiers, fetchMyRanks, isLoading } = useTierStore();
  const { fetchSongs, songs } = useSongsStore();
  
  // 현재 악기의 티어 정보 가져오기
  const currentTier = instrumentTiers[nowInstrument];
  
  useEffect(() => {
    // 티어 정보 로드
    fetchMyRanks();
    
    // 현재 악기와 티어에 맞는 곡 목록 가져오기
    console.log("[tier]/page.tsx useEffect 실행");
    fetchSongs(nowInstrument, nowTier);
  }, [nowInstrument, nowTier, fetchMyRanks, fetchSongs]);
  
  return (
    <div className={styles.tierpage}>
      {isLoading ? (
        <p>티어 정보를 불러오는 중...</p>
      ) : (
        <Tiercard
          currentTier={currentTier} // URL의 악기에 해당하는 티어 정보 사용
          tier={nowTier}
        />
      )}
      
      <div className={styles.songs}>
        {songs.length > 0 && (
          <SongCarousel
            songs={songs}
          />
        )}
      </div>
    </div>
  )
}
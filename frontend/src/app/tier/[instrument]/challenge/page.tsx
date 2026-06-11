'use client'

import { useEffect } from "react"
import { useParams } from "next/navigation"
import styles from "./challenge.module.css"
import Tiercard from "@/components/tiercard"
import { useTierStore } from "@/store/tier"

const tierOrder = ['IRON', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND']

export default function Challenge() {
  // URL에서 악기 정보 가져오기
  const params = useParams();
  const instrument = params.instrument as 'GUITAR' | 'BASS' | 'DRUM' | 'VOCAL';
  
  // useTierStore에서 필요한 상태와 함수 가져오기
  const { instrumentTiers, isLoading, error, fetchMyRanks } = useTierStore();
  
  // 컴포넌트 마운트 시 티어 정보 불러오기
  useEffect(() => {
    fetchMyRanks();
  }, [fetchMyRanks]);
  
  // 현재 URL의 악기에 해당하는 티어 정보
  const currentTier = instrumentTiers[instrument];

  return (
    <div className={styles.challenge}>
      {/* <h2>{instrument} 티어 도전</h2> */}
      
      {isLoading ? (
        <p>티어 정보를 불러오는 중...</p>
      ) : error ? (
        <p>에러: {error}</p>
      ) : (
        <div className={styles.container}>
          {currentTier ? (
            tierOrder.map((tier) => (
              <Tiercard 
                key={tier}
                tier={tier}
                currentTier={currentTier}
                noticeThing={tier === currentTier ? "다음 방어전까지 7일 2시간 남았습니다." : ""}
              />
            ))
          ) : (
            <p>{instrument} 티어 정보가 없습니다...</p>
          )}
        </div>
      )}
    </div>
  )
}
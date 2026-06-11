'use client'

import { useRouter, useSearchParams } from "next/navigation"
import { useEffect, useState } from "react"
import styles from "./successPage.module.css"
import { useAttemptStore } from "@/store/attempt"
import dynamic from 'next/dynamic'

// Use 'object' type or a more specific interface if the component requires props
type InstrumentViewerProps = object

// 클라이언트 컴포넌트는 params 대신 attemptId를 직접 props로 받습니다
export default function SuccessContent({ attemptId }: { attemptId: string }) {
  console.log("Attempt ID:", attemptId)
  const router = useRouter()
  const searchParams = useSearchParams()
  
  const { attempt, loading, fetchAttempt } = useAttemptStore()
  
  // 티어 및 악기 정보를 위한 상태 추가
  const [instrumentComponent, setInstrumentComponent] = useState<React.ComponentType<InstrumentViewerProps> | null>(null)
  const [userTier, setLocalUserTier] = useState<string>('') // 로컬 상태로 userTier 추가
  
  useEffect(() => {
    if (attemptId) {
      // String을 숫자로 변환
      const parsedAttemptId = parseInt(attemptId, 10)
      if (!isNaN(parsedAttemptId)) {
        // 자동으로 더미 데이터 사용 옵션 활성화
        fetchAttempt(parsedAttemptId);
      }
    }
  }, [attemptId, fetchAttempt])
  
  // 티어와 악기 정보를 URL 파라미터에서 가져오고 컴포넌트 동적 로드
  useEffect(() => {
    // URL에서 tierInfo 파라미터 가져오기
    let tierAndInstrument = searchParams.get('tierInfo') || 'GOLDBASS'
    
    // UNRANKED를 IRON으로 변환
    if (tierAndInstrument.includes('UNRANKED') || tierAndInstrument.includes('IRON')) {
      tierAndInstrument = tierAndInstrument.replace('UNRANKED', 'BRONZE').replace('IRON', 'BRONZE')
    }
    
    console.log("사용할 컴포넌트:", tierAndInstrument)
    
    // 티어 추출 로직
    const extractTier = (tierAndInstrument: string): string => {
      // 가능한 티어 목록
      const tiers = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND']
      
      // 문자열에서 티어 부분 찾기
      for (const tier of tiers) {
        if (tierAndInstrument.includes(tier)) {
          return tier
        }
      }
      
      // 기본값 반환
      return 'BRONZE'
    }
    
    // 티어 추출 및 상태 설정
    const extractedTier = extractTier(tierAndInstrument)
    setLocalUserTier(extractedTier)

    // 동적으로 컴포넌트 로드
    const loadComponent = async () => {
      try {
        // 동적 import 사용
        const InstrumentComponent = dynamic<InstrumentViewerProps>(
          () => import(`@/app/3dtest/Viewer/${tierAndInstrument}`),
          { 
            loading: () => <div>3D 모델 로딩 중...</div>,
            ssr: false 
          }
        )
        
        setInstrumentComponent(() => InstrumentComponent)
      } catch (error) {
        console.error("컴포넌트 로드 실패:", error)
        // 오류 시 기본 GOLDBASS으로 대체
        const DefaultComponent = dynamic<InstrumentViewerProps>(
          () => import('@/app/3dtest/Viewer/GOLDBASS'),
          { 
            loading: () => <div>3D 모델 로딩 중...</div>,
            ssr: false 
          }
        )
        setInstrumentComponent(() => DefaultComponent)
      }
    }
    
    loadComponent()
  }, [searchParams])
  
  const toTierPage = () => {
    router.push(`/tier`)
  }
  
  // 로딩 상태 처리
  if (loading) {
    return <div className={styles.successPage}>데이터를 불러오는 중...</div>
  }
  
  // InstrumentComponent가 null이면 로딩 중으로 표시
  const InstrumentComponent = instrumentComponent

  return (
    <div className={styles.successPage}>
      <div className={styles.pageContainer}>
        <div className={styles.left}>
          <div className={styles.scoreSection}>
            <div className={styles.unityArea}>
              {InstrumentComponent ? <InstrumentComponent /> : <div>모델 로딩 중...</div>}
            </div>
          </div>
          <div className={styles.title}>
            <div className={styles.text}>
              🎉 축하합니다! {userTier}에 도달했습니다 🎉
             </div>
           </div>
        </div>
        <div className={styles.right}>
          {attempt && (
            <>
              <div className={styles.score}>총점: {attempt.totalScore || 0}/100</div>
              <div className={styles.accuracy}>음정 점수: {attempt.tuneScore || 0}</div>
              <div className={styles.combo}>톤 점수: {attempt.toneScore || 0}</div>
              <div className={styles.combo}>박자 점수: {attempt.beatScore || 0}</div>
              
              <div className={styles.gameType}>
                게임 모드: {attempt.gameType === 'RANK' ? '랭크모드' : '연습모드'}
              </div>
              {attempt.rankType && (
                <div className={styles.rankType}>
                  랭크 유형: {
                    attempt.rankType === 'FIRST' ? '첫 도전' :
                    attempt.rankType === 'CHALLENGE' ? '챌린지' : 
                    attempt.rankType === 'DEFENCE' ? '방어전' : ''
                  }
                </div>
              )}
            </>
          )}
          <div className={styles.toTierPage} onClick={toTierPage}>
            돌아가기
          </div>
        </div>
      </div>
    </div>
  )
}
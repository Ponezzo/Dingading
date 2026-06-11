'use client'

// import { StaticImageData } from "next/image"
import styles from "./tiercard.module.css"
import { useParams, useRouter } from "next/navigation"
import React from "react"

// 티어별, 악기별 컴포넌트 동적 임포트
interface TiercardProps {
  tier : string , 
  currentTier : string, 
  noticeThing? : string 
}

export default function Tiercard ({ tier, currentTier, noticeThing} : TiercardProps) {
  const tierOrder = ['IRON', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND']
  const instruments = ['DRUM', 'BASS', 'GUITAR', 'VOCAL']

  const getTierStatus = () => {
    const currentIndex = tierOrder.indexOf(currentTier)
    const tierIndex = tierOrder.indexOf(tier)
     
    if (tierIndex === currentIndex) {
      return { isBlurred : false, navigateText : "방어하기", showNotice : true }
    } else if (tierIndex === currentIndex + 1) {
      return { isBlurred : true, navigateText : "도전하기", showNotice : false }
    } else if (tierIndex < currentIndex) {
      return { isBlurred : false, navigateText : "", showNotice : false }
    } else {
      return { isBlurred : true, navigateText : "", showNotice : false }
    }
  }

  const params = useParams() 
  const { instrument } = params

  // 현재 선택된 악기가 유효한지 확인
  const validInstrument = instruments.includes(String(instrument)) ? String(instrument) : 'DRUM'

  // 동적으로 컴포넌트를 가져오기 위한 React.lazy 컴포넌트
  const [MedalComponent, setMedalComponent] = React.useState<React.ComponentType | null>(null)
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    // 동적 임포트를 위한 함수
    const loadMedalComponent = async () => {
      try {
        setLoading(true)
        // 티어와 악기에 따라 동적으로 컴포넌트 임포트
        const { default : LoadedComponent} = await import(`@/app/3dtest/Viewer/${tier}${validInstrument}`)
        setMedalComponent(() => LoadedComponent)
      } catch (error) {
        console.error(`Failed to load component for ${tier}${validInstrument}:`, error)
        // 기본 컴포넌트로 fallback
        try {
          const fallbackModule = await import('@/app/3dtest/Viewer/BRONZEDRUM')
          setMedalComponent(() => fallbackModule.default)
        } catch (fallbackError) {
          console.error('Failed to load fallback component:', fallbackError)
        }
      } finally {
        setLoading(false)
      }
    }

    loadMedalComponent()
  }, [tier, validInstrument])

  const tierStatus = getTierStatus() 

  const router = useRouter()

  const toTier = () => {
    if (tier) router.push(`/tier/${instrument}/challenge/${tier}`)
  }

  return (
    <div className={styles.tiercard} onClick={toTier}>
      <div className={`${styles.icon} ${tierStatus.isBlurred ? styles.blurred : ''}`}>
        <div style={{width: '10rem', display: "flex", flexDirection: "column", alignItems: "center"}}>
          {loading ? (
            <div>Loading...</div>
          ) : (
            MedalComponent && <MedalComponent />
          )}
        </div>
      </div>
      <div className={styles.text}>{tier}</div>
      <div className={styles.navigate}>{tierStatus.navigateText}</div>
      {tierStatus.showNotice && (
        <div 
          className={styles.notice}
          dangerouslySetInnerHTML={{__html : noticeThing || ""}}
        >
        </div>
      )}
    </div>
  )
}
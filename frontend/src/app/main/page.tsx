'use client'
import { NextPage } from "next"
import "@/styles/main.css"
import Card from "@/components/card"
import card1 from "@/app/3dtest/Mainpage/Drum111.gif"
import card2 from "@/app/3dtest/Mainpage/Guitar161.gif"
import card3 from "@/app/3dtest/Mainpage/Bass138.gif"
import BigCard from "@/components/bigcard"
import { useEffect, useState, Suspense } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { useAuthStore } from "@/store/auth"

// 로그인 처리를 위한 별도 컴포넌트
function LoginHandler() {
  const searchParams = useSearchParams()
  const router = useRouter()
  const { loginWithToken, isLoggedIn } = useAuthStore()
  const [tokenProcessed, setTokenProcessed] = useState(false)

  useEffect(() => {
    // 이미 로그인되어 있는 경우
    if (isLoggedIn) {
      return
    }

    // 토큰 처리가 완료된 경우
    if (tokenProcessed) return
    
    // URL에서 토큰과 사용자 정보 추출
    const token = searchParams.get('token');
    const loginId = searchParams.get('loginId');
    const nickname = searchParams.get('nickname');
    const profileImg = searchParams.get('profileImg');
    
    // 토큰이 있으면 로그인 처리
    if (token && loginId && nickname) {
      console.log("URL에서 받은 토큰으로 로그인 처리 시작");
      loginWithToken(token, loginId, nickname, profileImg || '');
      setTokenProcessed(true);
      // 쿼리 파라미터 없이 메인 페이지로 리다이렉트
      router.replace('/main');
    } else {
      // URL에 토큰이 없는 경우 처리 완료로 표시
      setTokenProcessed(true);
    }
  }, [searchParams, loginWithToken, router, tokenProcessed, isLoggedIn]);

  return null;
}

// 메인 컴포넌트
const MainContent = () => {
  return (
    <div className="main">
      <div className="left">
        <BigCard
          subText1="당신의 티어를 측정해보세요"
          titleText="티어 측정하기"
          image={card1}
          href="/tier"
        />
      </div>
      <div className="right">
        <Card
          subText1="나와 맞는 밴드원과"
          subText2="밴드를 결성해보세요"
          titleText="밴드원 구하기"
          image={card2}
          href="/band"
        />
        <Card
          subText1="다른 사람의 연주를 듣고"
          subText2="내 실력을 뽐내 보세요"
          titleText="라이브 하우스"
          image={card3}
          href="/livehouses"
        />
      </div>
    </div>
  )
}

// 메인 페이지
const Main: NextPage = () => {
  return (
    <>
      {/* useSearchParams를 사용하는 컴포넌트는 Suspense로 감싸기 */}
      <Suspense fallback={<div>로딩 중...</div>}>
        <LoginHandler />
      </Suspense>
      <MainContent />
    </>
  )
}

export default Main
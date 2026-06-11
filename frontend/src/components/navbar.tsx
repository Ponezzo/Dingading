'use client'

import { useEffect, useState } from 'react'
import '@/styles/components/navbar.css'
import Searchbar from './searchbar'
import Logo from './logo'
import Image from 'next/image'
import hamburger from "@/assets/hamburger.svg"
import chevronLeft from "@/assets/chevron-left.svg"
import { usePathname, useRouter } from 'next/navigation'
import missA from "@/app/3dtest/Sub/Look.png"
// import NotificationComponent from './notification'
import Notice from './notice'
import { useAuthStore } from '@/store/auth'

const Navbar: React.FC = () => {

  const [isOpen, setIsOpen] = useState(false) // 메뉴 열림 상태
  const pathName = usePathname()

  useEffect(() => {
    setIsOpen(false)
  }, [pathName]) // pathname이 변할 때 , navbar는 접기
  
  const handleHamburgerClick = () => { // 메뉴 열림 상태 조절 
    setIsOpen(!isOpen)
  }

  // 조건문 별 들어갈 내용 단순화 위한 컴포넌트 정의 
  const commonThings = (
    <div className="navbar-container-small"> 
      <div className="hamburger-button" onClick={handleHamburgerClick}>
        <Image 
          src={hamburger}
          alt='hamburger-button'
        />
      </div>
      <Logo />
      <Notice />
      {/* <NotificationComponent /> */}
    </div>
  )

  const searchbarThings = (
    <div className="searchbar-container">
      <Searchbar/> 
    </div>
  )

  const router = useRouter() 
  const toGo = (href : string) => {
    router.push(href)
  }

  const { isLoggedIn, logout } = useAuthStore() 

  const handleLogout = () => {
    router.push("/main")
    logout()
  }

  const middleThings = (
    <div className="navbar-container-middle">
      <div className="columns">
        <div className="image-3d">
          <Image
            src={missA}
            alt='miss A'
            width={250}
            style={{width: "100%"}}
          />
        </div>
        {searchbarThings}
        <div className="texts">
          {!isLoggedIn && <div className="nav-text" onClick={()=>toGo(`/login`)}>로그인</div>}
          {/* <div className="nav-text" onClick={()=>toGo(`/signup`)}>회원가입</div> */}
          <div className="nav-text" onClick={()=>toGo(`/band`)}>주변 밴드 찾기</div>
          {/* <div className="nav-text" onClick={()=>toGo(`/myband`)}>내 밴드</div> */}
          <div className="nav-text" onClick={()=>toGo(`/tier`)}>티어 측정하기</div>
          {/* <div className="nav-text" onClick={()=>toGo(`/tier`)}>이달의 도전곡</div> */}
          <div className="nav-text" onClick={()=>toGo(`/livehouses`)}>라이브 하우스</div>
          {isLoggedIn && <div className="nav-text" onClick={handleLogout}>로그아웃</div>}
        </div>
      </div>
    </div>
  )

  const renderNavbarContent = () => {
    if (!isOpen) {
      return (
        <div className='container' style={{width : "5rem"}}>
          {commonThings}
        </div>
      )
    } else {  
      return (
        <div className='container' style={{width : "25rem"}}>
          {commonThings}
          {middleThings}
          <div className="chevron-nav" onClick={handleHamburgerClick}>
            <Image src={chevronLeft} alt='chevron left'/>
          </div>
        </div>
      )
    }
  }

  return (
    <div className="navbar">
      {renderNavbarContent()}
      <div className="right-nav">
        <div className="end-line"></div>
      </div>
    </div>
  )
}

export default Navbar 
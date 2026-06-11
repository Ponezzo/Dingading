'use client'

import "@/styles/components/searchbar.css"
import React from "react"
import Image from "next/image"
import search from "@/assets/search.svg"

const Searchbar:React.FC = () => {

  const onClickSearch = () => {
    alert("검색 기능은 아직 구현 중입니다.")
  }

  return (
    <div className="searchbar">
      <input type="text" className="text-area"/>
      {/* <img src={search.src} alt="search icon" width={36} height={36} /> */}
      <Image 
        src={search} 
        alt="search icon" 
        className="icon"
        width={20}  // 원하는 크기로 조정
        height={20} // 원하는 크기로 조정
        onClick={onClickSearch}
        style={{ width : "36px", height : "36px"}}
      />
    </div>
  )
}

export default Searchbar 
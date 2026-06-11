'use client'

import React, { useState } from 'react'
import Songcard from "@/components/songcard"
import styles from './songcarousel.module.css'
import Image from 'next/image'
import chevronLeft from "@/assets/chevron-left.svg"
import { getYouTubeThumbnail } from '@/utils/getYoutubeThumbnail'

interface Song {
  songId: number
  title: string 
  artist: string
  description? : string
  youtubeUrl : string
}

interface SongCarouselProps {
  songs: Song[]
}

export default function SongCarousel({ songs }: SongCarouselProps) {
  const [currentIndex, setCurrentIndex] = useState(0)

  // 다섯 곡 중 현재 곡의 위아래 곡 보여주기
  const handleNext = () => {
    setCurrentIndex((prevIndex) => 
      prevIndex < songs.length - 1 ? prevIndex + 1 : prevIndex
    )
  }

  const handlePrev = () => {
    setCurrentIndex((prevIndex) => 
      prevIndex > 0 ? prevIndex - 1 : prevIndex
    )
  }

  // // youtube 썸네일 활용해 이미지 구하기 
  // const getYouTubeThumbnail = (url: string): string | null => {
  //   const match = url.match(/(?:https?:\/\/)?(?:www\.)?youtube\.com\/watch\?v=([^&]+)/);
  //   const thumbnailUrl = match ? `https://img.youtube.com/vi/${match[1]}/hqdefault.jpg` : null;
  //   return thumbnailUrl
  // }

  const bgImage = getYouTubeThumbnail(songs[currentIndex]?.youtubeUrl)
  console.log("songcarousel.tsx/ bgImage : ", bgImage)

  return (
    <div className={styles.songCarousel}>
      <div className={styles.carouselWrapper}>
        {/* 이전 곡 (위쪽) */}
        {currentIndex > 0 && (
          <div className={styles.song}>
            <Songcard 
              songId={songs[currentIndex - 1].songId}
              key={songs[currentIndex - 1].songId}
              songName={songs[currentIndex - 1].title}
              artist={songs[currentIndex - 1].artist}
              thumbnailImg={getYouTubeThumbnail(songs[currentIndex - 1].youtubeUrl) || ""}
              />
          </div>
        )}
        <div className={styles.navigationButtons}>
          <button 
            onClick={handlePrev} 
            disabled={currentIndex === 0}
            className={styles.prevButton}
            >
            <Image 
              src={chevronLeft}
              alt='to next'
              style={{transform : "rotate(90deg)"}}
            />
          </button>
        </div>

        {/* 현재 곡 (가운데) */}
        <div className={styles.song}>
          <Songcard 
            songId={
              songs[currentIndex].songId
            }
            key={songs[currentIndex].songId}
            songName={songs[currentIndex].title}
            artist={songs[currentIndex].artist}
            // className={styles.largerCard}
            thumbnailImg={getYouTubeThumbnail(songs[currentIndex].youtubeUrl) || ""}
            />
          <div className={styles.soundTest}>
            음향 테스트
          </div>
        </div>

        <div className={styles.navigationButtons}>
          <button 
            onClick={handleNext} 
            disabled={currentIndex === songs.length - 1}
            className={styles.nextButton}
            >
            <Image 
              src={chevronLeft}
              alt='to next'
              style={{transform : "rotate(-90deg)"}}
            />
          </button>
        </div>

        {/* 다음 곡 (아래쪽) */}
        {currentIndex < songs.length - 1 && (
          <div className={styles.song}>
            <Songcard 
              songId={songs[currentIndex + 1].songId}
              key={songs[currentIndex + 1].songId}
              songName={songs[currentIndex + 1].title}
              artist={songs[currentIndex + 1].artist}
              // className={styles.smallerCard}
              thumbnailImg={getYouTubeThumbnail(songs[currentIndex + 1].youtubeUrl) || ""}
            />
          </div>
        )}
      </div>
    </div>
  )
}
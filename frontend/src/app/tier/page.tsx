'use client'

import styles from "./tier.module.css";
import Card from "@/components/card";
import { NextPage } from "next";
import Image from "next/image";
import ChevronLeft from "@/assets/chevron-left.svg";
import ChevronRight from "@/assets/chevron-right.svg";
import { useState, lazy, Suspense } from "react";
// import card1 from "@/app/3dtest/Mainpage/Drum_zoom.png"
// import card2 from "@/app/3dtest/Mainpage/Base_zoom.png"
import challenge from "@/assets/Challenge.png"
import practice from "@/assets/Practice.png"

// 악기별 컴포넌트 임포트 (동적 컴포넌트로 변경)
const DrumsCharacter = lazy(() => import("@/app/3dtest/Viewer/DRUMModel"));
const BassCharacter = lazy(() => import("@/app/3dtest/Viewer/BASSModel"));
const GuitarCharacter = lazy(() => import("@/app/3dtest/Viewer/GUITARModel"));
const VocalsCharacter = lazy(() => import("@/app/3dtest/Viewer/VOCALModel"));

const Tier: NextPage = () => {
  // instrument 타입을 명시적으로 정의
  type InstrumentType = "DRUM" | "BASS" | "GUITAR" | "VOCAL";
  const [instrument, setInstrument] = useState<InstrumentType>("DRUM");

  // 현재 렌더링할 컴포넌트 결정
  const renderCharacterComponent = () => {
    switch (instrument) {
      case "DRUM":
        return <DrumsCharacter />
      case "BASS":
        return <BassCharacter />
      case "GUITAR":
        return <GuitarCharacter />
      case "VOCAL":
        return <VocalsCharacter />
      default:
        return <DrumsCharacter />
    }
  };

  const changeInstrument = (direction: "left" | "right") => {
    const instruments: InstrumentType[] = ["DRUM", "BASS", "GUITAR", "VOCAL"];
    const currentIndex = instruments.indexOf(instrument);
    let nextIndex = currentIndex;
    
    if (direction === "left") {
      nextIndex = currentIndex === 0 ? instruments.length - 1 : currentIndex - 1;
    } else if (direction === "right") {
      nextIndex = currentIndex === instruments.length - 1 ? 0 : currentIndex + 1;
    }
    setInstrument(instruments[nextIndex]);
  };

  return (
    <div className={styles.tier}>
      <div className={styles.unityArea}>
        <div className={styles.upper}>
          <div className={styles.changeInstrument} onClick={() => changeInstrument("left")}>
            <Image src={ChevronLeft} alt="changing instrument" />
          </div>
          <div className={styles.playingPeople}>
            <Suspense fallback={<div>Loading...</div>}>
              {renderCharacterComponent()}
            </Suspense>
          </div>
          <div className={styles.changeInstrument} onClick={() => changeInstrument("right")}>
            <Image src={ChevronRight} alt="change instrument" />
          </div>
        </div>
        <div className={styles.under}>
          <div className={styles.tierContainer}>
            <div className={styles.dummy}>tier icon</div>
            <div className={styles.dummy}>tier text</div>
          </div>
          <div className={styles.notice}>
            notice area <br /> (방어전 정보, 최근 성공한 날)
          </div>
        </div>
      </div>
      {/* <div className={styles.toNext}>
        <Image src={ChevronRight} alt="to next area" style={{ opacity: 0.6 }} />
      </div> */}
      <div className={styles.navArea}>
        <Card
          href={`/tier/${instrument}/challenge/`}
          subText1="내 티어를 방어하고"
          subText2="더 높은 티어에 도전하세요"
          titleText="도전하기"
          image={challenge}
        />
        <Card
          href={`/tier/${instrument}/practice`}
          subText1="내 티어에 맞는 곡으로"
          subText2="연습해보세요"
          titleText="연습하기"
          image={practice}
        />
      </div>
    </div>
  );
};

export default Tier;
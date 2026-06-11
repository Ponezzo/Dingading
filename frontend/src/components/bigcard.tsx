'use client'

import { useRouter } from "next/navigation"
import styles from "./bigcard.module.css"
import Image, { StaticImageData } from "next/image"
import ChevronRight from "@/assets/chevron-right.svg"

interface BircardProps {
  href : string 
  image : StaticImageData 
  subText1? : string, 
  subText2? : string, 
  titleText? : string, 
}

export default function BigCard ({href, image, subText1, subText2, titleText} : BircardProps) {

  const router = useRouter()
  const handleClick = () => {
    if (href) router.push(href)
  }

  return (
    <div 
      className={styles.container}
      onClick={handleClick}
      style={{backgroundImage : `url(${image.src})`}}
      >
      <div className={styles.textContainer}>
        <div className={styles.text}>
          <div className={styles.subText}>{subText1}</div>
          <div className={styles.subText}>{subText2}</div>
        </div>
        <div className={styles.navText}>
          <div className={styles.titleText}>{titleText}</div>
          <div className={styles.navigateIcon}>
            <Image
              src={ChevronRight}
              alt="chevron right"
              className={styles.image}
              />
          </div>
        </div>
      </div>            
    </div>
  )
}
'use client'

import { useParams, useRouter } from "next/navigation"
import styles from "./failpage.module.css"

export default function FailPage () {

  const router = useRouter()
  const { tier , instrument } = useParams() 
  const toTierPage = () => {
    router.push(`/tier/${instrument}/challenge/${tier}`)
  }

  return (
    <div className={styles.successPage}>
      Fail.. 
      <div className={styles.toTierPage} onClick={toTierPage}>
        do one more !  
      </div>
    </div>
    
  )
}
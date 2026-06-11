// components/providers/AuthInitializer.tsx
'use client'

import { useAuthStore } from '@/store/auth'
import { useEffect } from 'react'

export default function AuthInitializer() {
  const initAuth = useAuthStore(state => state.initAuth)
  
  useEffect(() => {
    initAuth()
  }, [initAuth])
  
  return null // UI를 렌더링하지 않음
} 
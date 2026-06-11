import React, { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
import { AudienceModel } from '../../3dtest/LiveHouse/Audience'

export default function IronDrumScene() {
  return (
    <div style={{ width: '100vw', height: '100vh' }}>
      <Canvas 
        camera={{ 
          position: [0, 0, 5], 
          fov: 60
        }}
        style={{ background: 'transparent' }}
      >        
        {/* 방향성 조명 */}
        <directionalLight position={[0, 10, 60]} intensity={2}/>
        <directionalLight position={[-10, 0, 0]} intensity={2}/>
        <directionalLight position={[10, 0, 0]} intensity={2}/>
        <directionalLight position={[0, 10, 0]} intensity={2}/>
        <directionalLight position={[0, -10, 0]} intensity={2}/>

        {/* 모델 로딩 중 대기 */}
        <Suspense fallback={null}>
          <AudienceModel position={[-0.7, -3.7, 0.9]} rotation={[0, Math.PI, 0]} />
        </Suspense>
      </Canvas>
    </div>
  )
}
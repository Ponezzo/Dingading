import React, { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
// import { OrbitControls } from '@react-three/drei'
import { AudienceModel } from '../LiveHouse/Audience'

export default function IronDrumScene() {
  return (
    <div style={{ width: '100vw', height: '100vh' }}>
      <Canvas 
        camera={{ 
          position: [0, 0, 5], 
          fov: 45 
        }}
        style={{ background: '#22252d' }}
      >        
        {/* 방향성 조명 */}
        <directionalLight position={[0, 10, 60]} intensity={2}/>
        <directionalLight position={[-10, 0, 0]} intensity={2}/>
        <directionalLight position={[10, 0, 0]} intensity={2}/>
        <directionalLight position={[0, 10, 0]} intensity={2}/>
        <directionalLight position={[0, -10, 0]} intensity={2}/>

        {/* 모델 로딩 중 대기 */}
        <Suspense fallback={null}>
          <AudienceModel position={[0.5, -2.8, 1]} rotation={[0, Math.PI, 0]} />
        </Suspense>

        {/* 모델 컨트롤 */}
        {/* <OrbitControls /> */}
      </Canvas>
    </div>
  )
}
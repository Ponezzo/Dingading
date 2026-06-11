import React, { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
// import { OrbitControls } from '@react-three/drei'
import { DrumModel } from '../../../assets/Character/DC_transfer'

export default function IronDrumScene() {
  return (
    <div style={{ width: '100vw', height: '100vh' }}>
      <Canvas 
        camera={{ 
          position: [0, 0, 5], 
          fov: 45 
        }}
      >        
        {/* 방향성 조명 */}
        <directionalLight position={[0, 10, 60]} intensity={2}/>
        <directionalLight position={[-10, 0, 0]} intensity={2}/>
        <directionalLight position={[10, 0, 0]} intensity={2}/>
        <directionalLight position={[0, 10, 0]} intensity={2}/>
        <directionalLight position={[0, -10, 0]} intensity={2}/>

        {/* 모델 로딩 중 대기 */}
        <Suspense fallback={null}>
          <DrumModel position={[0, -1, -0.5]} />
        </Suspense>

        {/* 모델 컨트롤 */}
        {/* <OrbitControls /> */}
      </Canvas>
    </div>
  )
}
import React, { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
// import { OrbitControls } from '@react-three/drei'
import { BronzeVocal } from '../../../assets/Vocal/BV_transfer'

export default function BronzeVocalScene() {
  return (
    <div style={{ width: '18rem', height: '24rem' }}>
      <Canvas 
        camera={{ 
          position: [0, 0, 2], 
          fov: 45 
        }}
      >             
        {/* 방향성 조명 */}
        <directionalLight position={[0, 0, 60]} intensity={10}/>
        <directionalLight position={[-10, 0, 0]} intensity={3}/>
        <directionalLight position={[10, 0, 0]} intensity={3}/>
        <directionalLight position={[0, 10, 0]} intensity={3}/>
        <directionalLight position={[0, -10, 0]} intensity={3}/>

        {/* 모델 로딩 중 대기 */}
        <Suspense fallback={null}>
          <BronzeVocal position={[0, -0.6, -0.5]} />
        </Suspense>

        {/* 모델 컨트롤 */}
        {/* <OrbitControls /> */}
      </Canvas>
    </div>
  )
}
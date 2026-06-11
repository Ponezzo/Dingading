import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import {BassModel}  from '../../../assets/Character/BC_transfer';

export default function BassModelScene() { // isPlaying prop 추가
  return (
    <div style={{ width: '100vw', height: '100vh' }}>
      <Canvas
        camera={{
          position: [0, 0, 5],
          fov: 45,
        }}
        style={{ background: 'transparent' }}
      >
        {/* 방향성 조명 */}
        <directionalLight position={[0, 10, 60]} intensity={2} />
        <directionalLight position={[-10, 0, 0]} intensity={2} />
        <directionalLight position={[10, 0, 0]} intensity={2} />
        <directionalLight position={[0, 10, 0]} intensity={2} />
        <directionalLight position={[0, -10, 0]} intensity={2} />

        {/* 모델 로딩 중 대기 */}
        <Suspense fallback={null}>
          <BassModel position={[-1.1, -1.5, -2]} /> {/* isPlaying 전달 */}
        </Suspense>
      </Canvas>
    </div>
  );
}
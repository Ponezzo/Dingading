
import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { VocalModel } from '../../../assets/Character/VC_transfer';


export default function VocalModelScene() {
  return (
    <div style={{ width: '100vw', height: '100vh' }}>
      <Canvas
        camera={{
          position: [0, 0, 5],
          fov: 45,
        }}
        style={{ background: 'transparent' }}
      >
        <directionalLight position={[0, 10, 60]} intensity={2} />
        <directionalLight position={[-10, 0, 0]} intensity={2} />
        <directionalLight position={[10, 0, 0]} intensity={2} />
        <directionalLight position={[0, 10, 0]} intensity={2} />
        <directionalLight position={[0, -10, 0]} intensity={2} />

        <Suspense fallback={null}>
          <VocalModel position={[-1.1, -1.5, -2]}/>
        </Suspense>
      </Canvas>
    </div>
  );
}
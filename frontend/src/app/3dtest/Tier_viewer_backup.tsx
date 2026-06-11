import React, { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
import { OrbitControls } from '@react-three/drei'
import { BronzeDrum } from '../../assets/Drum/BD_transfer'
import { IronDrum } from '../../assets/Drum/ID_transfer'
import { SilverDrum } from '../../assets/Drum/SD_Transfer'
import { GoldDrum } from '../../assets/Drum/GD_transfer'
import { PlatinumDrum } from '../../assets/Drum/PD_transfer'
import { DiamondDrum } from '../../assets/Drum/DD_transfer'
import { IronGuitar } from '../../assets/Guitar/IG_transfer'
import { BronzeGuitar } from '../../assets/Guitar/BG_transfer'
import { SilverGuitar } from '../../assets/Guitar/SG_transfer'
import { GoldGuitar } from '../../assets/Guitar/GG_transfer'
import { PlatinumGuitar } from '../../assets/Guitar/PG_transfer'
import { DiamondGuitar } from '../../assets/Guitar/DG_transfer'
import { IronVocal } from '../../assets/Vocal/IV_transfer'
import { BronzeVocal } from '../../assets/Vocal/BV_transfer'
import { SilverVocal } from '../../assets/Vocal/SV_transfer'
import { GoldVocal } from '../../assets/Vocal/GV_transfer'
import { PlatinumVocal } from '../../assets/Vocal/PV_transfer'
import { DiamondVocal } from '../../assets/Vocal/DV_transfer'
import { IronBass } from '../../assets/Bass/IB_transfer'
import { BronzeBass } from '../../assets/Bass/BB_transfer'
import { SilverBass } from '../../assets/Bass/SB_transfer'
import { GoldBass } from '../../assets/Bass/GB_transfer'
import { PlatinumBass } from '../../assets/Bass/PB_transfer'
import { DiamondBass } from '../../assets/Bass/DB_transfer'

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
        <directionalLight position={[0, 0, 60]} intensity={10}/>
        <directionalLight position={[-10, 0, 0]} intensity={3}/>
        <directionalLight position={[10, 0, 0]} intensity={3}/>
        <directionalLight position={[0, 10, 0]} intensity={3}/>
        <directionalLight position={[0, -10, 0]} intensity={3}/>

        {/* 모델 로딩 중 대기 */}
        <Suspense fallback={null}>
          <IronDrum position={[-3, 1.5, 0]} />
          <BronzeDrum position={[-1.8, 1.5, 0]} />
          <SilverDrum position={[-0.6, 1.5, 0]} />
          <GoldDrum position={[0.6, 1.5, 0]} />
          <PlatinumDrum position={[1.8, 1.5, 0]} />
          <DiamondDrum position={[3, 1.5, 0]} />
          <IronGuitar position={[-3, 0, 0]} />
          <BronzeGuitar position={[-1.8, 0, 0]} />
          <SilverGuitar position={[-0.6, 0, 0]} />
          <GoldGuitar position={[0.6, 0, 0]} />
          <PlatinumGuitar position={[1.8, 0, 0]} />
          <DiamondGuitar position={[3, 0, 0]} />
          <IronVocal position={[-3, -1.5, 0]} />
          <BronzeVocal position={[-1.8, -1.5, 0]} />
          <SilverVocal position={[-0.6, -1.5, 0]} />
          <GoldVocal position={[0.6, -1.5, 0]} />
          <PlatinumVocal position={[1.8, -1.5, 0]} />
          <DiamondVocal position={[3, -1.5, 0]} />
          <IronBass position={[-3, -3, 0]} />
          <BronzeBass position={[-1.8, -3, 0]} />
          <SilverBass position={[-0.6, -3, 0]} />
          <GoldBass position={[0.6, -3, 0]} />
          <PlatinumBass position={[1.8, -3, 0]} />
          <DiamondBass position={[3, -3, 0]} />
        </Suspense>

        {/* 모델 컨트롤 */}
        <OrbitControls />
      </Canvas>
    </div>
  )
}
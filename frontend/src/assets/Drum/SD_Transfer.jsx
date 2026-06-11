import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function SilverDrum(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Drum/Silver_Drum.glb')
  const { actions } = useAnimations(animations, group)

  useEffect(() => {
    Object.values(actions).forEach((action) => {
      if (action) {
        action.play()
      }
    })
  }, [actions])

  return (
    <group ref={group} {...props} dispose={null}>
      <group name="Scene">
        <group 
          name="Silver" 
          position={[0, 0.594, 0]} 
          rotation={[-Math.PI / 2, 0, 0]} 
          scale={2}
        >
          <mesh
            name="Coin"
            castShadow
            receiveShadow
            geometry={nodes.Coin.geometry}
            material={materials.Silver_coin}
            rotation={[-Math.PI / 2, 0, 0]}
          />
          <mesh
            name="Drums"
            castShadow
            receiveShadow
            geometry={nodes.Drums.geometry}
            material={materials.Silver_Ins}
            position={[0.007, 0.092, -0.107]}
            rotation={[-Math.PI / 2, 0, -Math.PI]}
            scale={[0.19, 0.19, 0.095]}
          />
          <mesh
            name="Silver_Text"
            castShadow
            receiveShadow
            geometry={nodes.Silver_Text.geometry}
            material={materials.Silver_Ins}
            position={[-0.12, -0.055, -0.049]}
            rotation={[Math.PI / 2, 0, 0]}
            scale={15}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Drum/Silver_Drum.glb')

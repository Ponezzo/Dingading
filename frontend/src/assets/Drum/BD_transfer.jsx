import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function BronzeDrum(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Drum/Bronze_Drum.glb')
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
          name="Bronze" 
          position={[0, 0.594, 0]} 
          rotation={[-Math.PI / 2, 0, 0]} 
          scale={2}
        >
          <mesh
            name="Bronze_Text"
            castShadow
            receiveShadow
            geometry={nodes.Bronze_Text.geometry}
            material={materials['Bronze_Ins.001']}
            position={[-0.151, -0.055, -0.049]}
            rotation={[Math.PI / 2, 0, 0]}
            scale={15}
          />
          <mesh
            name="Coin001"
            castShadow
            receiveShadow
            geometry={nodes.Coin001.geometry}
            material={materials['Bronze_coin.001']}
            rotation={[-Math.PI / 2, 0, 0]}
          />
          <mesh
            name="Drums001"
            castShadow
            receiveShadow
            geometry={nodes.Drums001.geometry}
            material={materials['Bronze_Ins.001']}
            position={[0.007, 0.092, -0.107]}
            rotation={[-Math.PI / 2, 0, -Math.PI]}
            scale={[0.19, 0.19, 0.095]}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Drum/Bronze_Drum.glb')

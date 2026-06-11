import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function DiamondDrum(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Drum/Diamond_Drum.glb')
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
          name="Diamond" 
          position={[0, 0.594, 0]} 
          rotation={[-Math.PI / 2, 0, 0]} 
          scale={2}
        >
          <mesh
            name="bitcoin"
            castShadow
            receiveShadow
            geometry={nodes.bitcoin.geometry}
            material={materials['Diamond_coin.001']}
            rotation={[-Math.PI / 2, 0, 0]}
          />
          <mesh
            name="Diamond_Text"
            castShadow
            receiveShadow
            geometry={nodes.Diamond_Text.geometry}
            material={materials['Diamond_Ins.001']}
            position={[-0.186, -0.055, -0.049]}
            rotation={[1.58, 0, 0]}
            scale={15}
          />
          <mesh
            name="Drums"
            castShadow
            receiveShadow
            geometry={nodes.Drums.geometry}
            material={materials['Diamond_Ins.001']}
            position={[0.007, 0.092, -0.107]}
            rotation={[-Math.PI / 2, 0, -3.124]}
            scale={[0.19, 0.19, 0.095]}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Drum/Diamond_Drum.glb')

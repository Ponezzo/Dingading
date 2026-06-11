import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function DiamondVocal(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Vocal/Diamond_Vocal.glb')
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
        <group name="Diamond" position={[0, 0.594, 0]} rotation={[-Math.PI / 2, 0, 0]} scale={2}>
          <mesh
            name="bitcoin"
            castShadow
            receiveShadow
            geometry={nodes.bitcoin.geometry}
            material={materials['Diamond_coin.002']}
            rotation={[-Math.PI / 2, 0, 0]}
          />
          <mesh
            name="Diamond_Text"
            castShadow
            receiveShadow
            geometry={nodes.Diamond_Text.geometry}
            material={materials['Diamond_Ins.002']}
            position={[-0.186, -0.055, -0.049]}
            rotation={[Math.PI / 2, 0, 0]}
            scale={15}
          />
          <mesh
            name="Microphone"
            castShadow
            receiveShadow
            geometry={nodes.Microphone.geometry}
            material={materials['Diamond_Ins.002']}
            position={[-0.003, 0.072, -0.113]}
            rotation={[-Math.PI / 2, -1.012, -Math.PI]}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Vocal/Diamond_Vocal.glb')

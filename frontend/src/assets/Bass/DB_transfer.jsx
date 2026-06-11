import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function DiamondBass(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Bass/Diamond_Bass.glb')
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
            name="Bass_Guitar"
            castShadow
            receiveShadow
            geometry={nodes.Bass_Guitar.geometry}
            material={materials['Diamond_Ins.002']}
            position={[-0.03, 0.028, 0.032]}
            rotation={[-Math.PI, Math.PI / 4, -Math.PI]}
            scale={0.5}
          />
          <mesh
            name="Coin"
            castShadow
            receiveShadow
            geometry={nodes.Coin.geometry}
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
            rotation={[1.58, 0, 0]}
            scale={15}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Bass/Diamond_Bass.glb')

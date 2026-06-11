import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function DiamondGuitar(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Guitar/Diamond_Guitar.glb')
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
          <mesh
            name="Electric_Guitar"
            castShadow
            receiveShadow
            geometry={nodes.Electric_Guitar.geometry}
            material={materials['Diamond_Ins.002']}
            position={[-0.03, 0.04, 0.032]}
            rotation={[-Math.PI, Math.PI / 4, -Math.PI]}
            scale={0.5}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Guitar/Diamond_Guitar.glb')

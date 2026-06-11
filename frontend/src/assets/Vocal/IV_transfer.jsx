import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function IronVocal(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Vocal/Iron_Vocal.glb')
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
          name="Iron" 
          position={[0, 0.594, 0]} 
          rotation={[-Math.PI / 2, 0, 0]} 
          scale={2}
        >
          <mesh
            name="Coin"
            castShadow
            receiveShadow
            geometry={nodes.Coin.geometry}
            material={materials['Iron_coin.003']}
            rotation={[-Math.PI / 2, 0, 0]}
          />
          <mesh
            name="Iron_Text"
            castShadow
            receiveShadow
            geometry={nodes.Iron_Text.geometry}
            material={materials['Iron_Ins.003']}
            position={[-0.086, -0.055, -0.049]}
            rotation={[Math.PI / 2, 0, 0]}
            scale={15}
          />
          <mesh
            name="Microphone"
            castShadow
            receiveShadow
            geometry={nodes.Microphone.geometry}
            material={materials['Iron_Ins.003']}
            position={[-0.003, 0.072, -0.113]}
            rotation={[-Math.PI / 2, -1.012, -Math.PI]}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Vocal/Iron_Vocal.glb')

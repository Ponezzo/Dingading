import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function SilverGuitar(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Guitar/Silver_Guitar.glb')
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
            material={materials['Silver_coin.001']}
            rotation={[-Math.PI / 2, 0, 0]}
          />
          <mesh
            name="Electric_Guitar"
            castShadow
            receiveShadow
            geometry={nodes.Electric_Guitar.geometry}
            material={materials['Silver_Ins.001']}
            position={[-0.03, 0.04, 0.032]}
            rotation={[-Math.PI, Math.PI / 4, -Math.PI]}
            scale={0.5}
          />
          <mesh
            name="Silver_Text"
            castShadow
            receiveShadow
            geometry={nodes.Silver_Text.geometry}
            material={materials['Silver_Ins.001']}
            position={[-0.12, -0.055, -0.049]}
            rotation={[Math.PI / 2, 0, 0]}
            scale={15}
          />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Guitar/Silver_Guitar.glb')

import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function DrumModel(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Character/Drum.glb')
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
        <mesh
          name="Drums"
          castShadow
          receiveShadow
          geometry={nodes.Drums.geometry}
          material={materials['Main.002']}
          position={[0, 0, 0.938]}
        />
        <group name="Character_2@Breathing_Idle_(1)">
          <group name="Accessories" />
          <group name="Body" />
          <group name="Faces" />
          <group name="Full_body" />
          <group name="Glasses" />
          <group name="Gloves" />
          <group name="Hairstyle" />
          <group name="Hat" />
          <group name="Mustache" />
          <group name="Outerwear" />
          <group name="Pants" />
          <group name="Shoes" />
          <group name="Skeleton">
            <skinnedMesh
              name="Accessories003"
              geometry={nodes.Accessories003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Accessories003.skeleton}
            />
            <skinnedMesh
              name="Body003"
              geometry={nodes.Body003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Body003.skeleton}
            />
            <skinnedMesh
              name="Faces003"
              geometry={nodes.Faces003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Faces003.skeleton}
            />
            <skinnedMesh
              name="Full_body003"
              geometry={nodes.Full_body003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Full_body003.skeleton}
            />
            <skinnedMesh
              name="Glasses003"
              geometry={nodes.Glasses003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Glasses003.skeleton}
            />
            <skinnedMesh
              name="Gloves003"
              geometry={nodes.Gloves003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Gloves003.skeleton}
            />
            <skinnedMesh
              name="Hairstyle003"
              geometry={nodes.Hairstyle003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Hairstyle003.skeleton}
            />
            <skinnedMesh
              name="Hat003"
              geometry={nodes.Hat003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Hat003.skeleton}
            />
            <skinnedMesh
              name="Mustache003"
              geometry={nodes.Mustache003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Mustache003.skeleton}
            />
            <skinnedMesh
              name="Outerwear003"
              geometry={nodes.Outerwear003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Outerwear003.skeleton}
            />
            <skinnedMesh
              name="Pants003"
              geometry={nodes.Pants003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Pants003.skeleton}
            />
            <skinnedMesh
              name="Shoes003"
              geometry={nodes.Shoes003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.Shoes003.skeleton}
            />
            <skinnedMesh
              name="T_Shirt003"
              geometry={nodes.T_Shirt003.geometry}
              material={materials['Color.003']}
              skeleton={nodes.T_Shirt003.skeleton}
            />
            <primitive object={nodes.Root} />
          </group>
          <group name="T_Shirt" />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Drum.glb')

import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function VocalModel(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Character/Vocal.glb')
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
        <group name="Sing@Singing">
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
              name="Accessories001"
              geometry={nodes.Accessories001.geometry}
              material={materials.Color}
              skeleton={nodes.Accessories001.skeleton}
            />
            <skinnedMesh
              name="Body001"
              geometry={nodes.Body001.geometry}
              material={materials.Color}
              skeleton={nodes.Body001.skeleton}
            />
            <skinnedMesh
              name="Faces001"
              geometry={nodes.Faces001.geometry}
              material={materials.Color}
              skeleton={nodes.Faces001.skeleton}
            />
            <skinnedMesh
              name="Full_body001"
              geometry={nodes.Full_body001.geometry}
              material={materials.Color}
              skeleton={nodes.Full_body001.skeleton}
            />
            <skinnedMesh
              name="Glasses001"
              geometry={nodes.Glasses001.geometry}
              material={materials.Color}
              skeleton={nodes.Glasses001.skeleton}
            />
            <skinnedMesh
              name="Gloves001"
              geometry={nodes.Gloves001.geometry}
              material={materials.Color}
              skeleton={nodes.Gloves001.skeleton}
            />
            <skinnedMesh
              name="Hairstyle001"
              geometry={nodes.Hairstyle001.geometry}
              material={materials.Color}
              skeleton={nodes.Hairstyle001.skeleton}
            />
            <skinnedMesh
              name="Hat001"
              geometry={nodes.Hat001.geometry}
              material={materials.Color}
              skeleton={nodes.Hat001.skeleton}
            />
            <skinnedMesh
              name="Mustache001"
              geometry={nodes.Mustache001.geometry}
              material={materials.Color}
              skeleton={nodes.Mustache001.skeleton}
            />
            <skinnedMesh
              name="Outerwear001"
              geometry={nodes.Outerwear001.geometry}
              material={materials.Color}
              skeleton={nodes.Outerwear001.skeleton}
            />
            <skinnedMesh
              name="Pants001"
              geometry={nodes.Pants001.geometry}
              material={materials.Color}
              skeleton={nodes.Pants001.skeleton}
            />
            <skinnedMesh
              name="Shoes001"
              geometry={nodes.Shoes001.geometry}
              material={materials.Color}
              skeleton={nodes.Shoes001.skeleton}
            />
            <skinnedMesh
              name="T_Shirt"
              geometry={nodes.T_Shirt.geometry}
              material={materials.Color}
              skeleton={nodes.T_Shirt.skeleton}
            />
            <primitive object={nodes.Root} />
          </group>
          <group name="T_Shirt001" />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Vocal.glb')

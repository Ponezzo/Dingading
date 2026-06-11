import React, { useRef, useEffect } from 'react'
import { useGLTF, useAnimations } from '@react-three/drei'

export function GuitarModel(props) {
  const group = useRef()
  const { nodes, materials, animations } = useGLTF('/Character/Guitar.glb')
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
        <group name="Character@Guitar_Playing">
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
              name="Accessories002"
              geometry={nodes.Accessories002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Accessories002.skeleton}
            />
            <skinnedMesh
              name="Body002"
              geometry={nodes.Body002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Body002.skeleton}
            />
            <skinnedMesh
              name="Faces002"
              geometry={nodes.Faces002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Faces002.skeleton}
            />
            <skinnedMesh
              name="Full_body002"
              geometry={nodes.Full_body002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Full_body002.skeleton}
            />
            <skinnedMesh
              name="Glasses002"
              geometry={nodes.Glasses002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Glasses002.skeleton}
            />
            <skinnedMesh
              name="Gloves002"
              geometry={nodes.Gloves002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Gloves002.skeleton}
            />
            <skinnedMesh
              name="Hairstyle002"
              geometry={nodes.Hairstyle002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Hairstyle002.skeleton}
            />
            <skinnedMesh
              name="Hat002"
              geometry={nodes.Hat002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Hat002.skeleton}
            />
            <skinnedMesh
              name="Mustache002"
              geometry={nodes.Mustache002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Mustache002.skeleton}
            />
            <skinnedMesh
              name="Outerwear002"
              geometry={nodes.Outerwear002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Outerwear002.skeleton}
            />
            <skinnedMesh
              name="Pants002"
              geometry={nodes.Pants002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Pants002.skeleton}
            />
            <skinnedMesh
              name="Shoes002"
              geometry={nodes.Shoes002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.Shoes002.skeleton}
            />
            <skinnedMesh
              name="T_Shirt002"
              geometry={nodes.T_Shirt002.geometry}
              material={materials['Color.002']}
              skeleton={nodes.T_Shirt002.skeleton}
            />
            <primitive object={nodes.Root} />
          </group>
          <group name="T_Shirt" />
        </group>
      </group>
    </group>
  )
}

useGLTF.preload('/Guitar.glb')

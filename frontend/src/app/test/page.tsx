'use client'
import Button from "@/components/button"
import { useCountStore } from "@/store/count"
import { Canvas } from "@react-three/fiber"
import { Model } from "@/components/glbmodels"
import TestMedal from "@/app/3dtest/Viewer/BRONZEBASS"

const Test = () => {

  const { count , increment, decrement, reset } = useCountStore()

  return (
    <div className="test">
      <p>Test</p>
      <Button></Button>
      <div className="GLBModel">
        <Canvas camera={{position : [0, 0, 5]}}>
          <ambientLight color={"#FFFFFF"} intensity={2.5} />
          <directionalLight position={[1, 1, 1]} color={"#FFFFFF"} intensity={5} />
          {/* <Model glbPath="/models/Iron.glb" scale={2}/> */}
          {/* <Model glbPath="/models/Bronze.glb" scale={2}/> */}
          <Model glbPath="/models/Silver.glb" position={[0, 0, 2]} rotation={[0, Math.PI, 0]} scale={4} />
          {/* <Model glbPath="/models/Gold.glb" scale={2}/> */}
      </Canvas>
      </div>

      <div style={{width : '100px' , border : "5px solid", display:"flex", flexDirection:"column", alignItems: "center"}}>
        <TestMedal /> 
      </div>

      <div className="count border-1">
        <p> store 테스트 코드  </p>
        <p>count : {count}</p>
        <div onClick={increment}>+</div> 
        <div onClick={decrement}>-</div> 
        <div onClick={reset}>RESET</div> 
      </div>
    </div>
  )
}

export default Test
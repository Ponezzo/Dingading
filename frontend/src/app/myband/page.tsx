'use client'
import { NextPage } from "next"
import HorizontalScrollWrapper from "@/components/horizontalscrollwrapper"
const Myband: NextPage = () => {

  return (
    <HorizontalScrollWrapper>
    <div> 
      <h1> Myband </h1>
      <div style={{ display: "flex", width: "300vw", height: "100vh" }}>
      <div style={{ width: "100vw", backgroundColor: "red" }}>Page 1</div>
      <div style={{ width: "100vw", backgroundColor: "green" }}>Page 2</div>
      <div style={{ width: "100vw", backgroundColor: "blue" }}>Page 3</div>
    </div>
    </div>
    </HorizontalScrollWrapper>
  )
}

export default Myband
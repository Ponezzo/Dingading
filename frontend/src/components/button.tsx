'use client'

import React from "react"

const Button:React.FC = () => {

  const handleClick = () => {
    alert("버튼이 클릭되었습니다.")
  }

  return (
    <button onClick={handleClick}>button</button>
  )
}

export default Button

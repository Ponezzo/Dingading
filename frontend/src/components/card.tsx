'use client'
import "@/styles/components/card.css"
import ChevronRight from "@/assets/chevron-right.svg"
import Image, { StaticImageData } from "next/image"
import { useRouter } from "next/navigation"

interface CardProps {
  subText1? : string, 
  subText2? : string, 
  titleText? : string, 
  image : StaticImageData, 
  href? : string,  
}

const Card = ({ subText1, subText2, titleText, image, href } : CardProps) => {

  const router = useRouter()
  const handleClick = () => {
    if (href) router.push(href)
  }

  return (
    <div className="card">
      <div className="card-container" onClick={handleClick}>
        <div className="image-container" style={{backgroundImage : `url(${image.src})`}}>
          <div className="text-container">
            <div className="text">
              <div className="sub-text">
                <div className="small-text">{subText1}</div>
                <div className="small-text">{subText2}</div>
              </div>
              <div className="nav-text">
                <div className="large-text">{titleText}
                <div className="navigate-icon">
                  <Image
                    src={ChevronRight}
                    alt="chevron right"
                  />
                </div>
              </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Card
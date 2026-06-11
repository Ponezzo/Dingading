import type React from "react"

interface AudioLevelMeterProps {
  level: number
  isMuted: boolean
}

const AudioLevelMeter: React.FC<AudioLevelMeterProps> = ({ level, isMuted }) => {
  // 0~1 사이의 레벨 값을 0~100% 너비로 변환
  const width = Math.min(level * 500, 100)

  // 레벨에 따른 색상 결정
  const getColor = () => {
    if (isMuted) return "bg-gray-400"
    if (level < 0.01) return "bg-red-500"
    if (level < 0.05) return "bg-yellow-500"
    if (level < 0.1) return "bg-green-500"
    return "bg-green-600"
  }

  return (
    <div className="audio-level-meter">
      <div className="h-4 bg-gray-200 rounded-full overflow-hidden">
        <div className={`h-full transition-all duration-100 ${getColor()}`} style={{ width: `${width}%` }}></div>
      </div>
      <div className="mt-1 text-xs">
        {isMuted ? (
          <span className="text-gray-500">음소거됨</span>
        ) : level < 0.01 ? (
          <span className="text-red-500">소리가 감지되지 않음</span>
        ) : level < 0.05 ? (
          <span className="text-yellow-500">소리가 작음</span>
        ) : (
          <span className="text-green-500">정상</span>
        )}
      </div>
    </div>
  )
}

export default AudioLevelMeter

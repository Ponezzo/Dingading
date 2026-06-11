"use client"

import type React from "react"
import { useState, useEffect, useRef } from "react"

interface MicTestModalProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: (stream: MediaStream, deviceId: string) => void // 스트림과 장치 ID를 전달
}

const MicTestModal: React.FC<MicTestModalProps> = ({ isOpen, onClose, onConfirm }) => {
  const [audioLevel, setAudioLevel] = useState<number>(0)
  const [isMicActive, setIsMicActive] = useState<boolean>(false)
  const [selectedDevice, setSelectedDevice] = useState<string>("")
  const [audioDevices, setAudioDevices] = useState<MediaDeviceInfo[]>([])
  const [errorMessage, setErrorMessage] = useState<string>("")

  const streamRef = useRef<MediaStream | null>(null)
  const audioContextRef = useRef<AudioContext | null>(null)
  const analyserRef = useRef<AnalyserNode | null>(null)
  const animationFrameRef = useRef<number | null>(null)

  // 마이크 장치 목록 가져오기
  useEffect(() => {
    const getDevices = async () => {
      try {
        // 마이크 권한 요청을 위해 임시 스트림 생성
        const tempStream = await navigator.mediaDevices.getUserMedia({ audio: true })
        tempStream.getTracks().forEach((track) => track.stop())

        const devices = await navigator.mediaDevices.enumerateDevices()
        const audioInputs = devices.filter((device) => device.kind === "audioinput")
        setAudioDevices(audioInputs)

        if (audioInputs.length > 0) {
          setSelectedDevice(audioInputs[0].deviceId)
        }

        console.log("사용 가능한 마이크 장치:", audioInputs)
      } catch (error) {
        console.error("마이크 장치 목록 가져오기 실패:", error)
        setErrorMessage("마이크 접근 권한이 없습니다. 브라우저 설정에서 마이크 권한을 허용해주세요.")
      }
    }

    if (isOpen) {
      getDevices()
    }

    return () => {
      stopMicTest()
    }
  }, [isOpen])

  // 마이크 테스트 시작
  const startMicTest = async () => {
    try {
      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop())
      }

      // 선택한 마이크로 스트림 생성
      streamRef.current = await navigator.mediaDevices.getUserMedia({
        audio: {
          deviceId: selectedDevice ? { exact: selectedDevice } : undefined,
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
        },
      })

      console.log("마이크 테스트 스트림 생성 성공:", streamRef.current)
      console.log("오디오 트랙:", streamRef.current.getAudioTracks())

      // 오디오 트랙 활성화 상태 확인
      const audioTracks = streamRef.current.getAudioTracks()
      if (audioTracks.length > 0) {
        console.log("마이크 테스트 오디오 트랙 활성화 상태:", audioTracks[0].enabled)
        console.log("마이크 테스트 오디오 트랙 설정:", audioTracks[0].getSettings())
      }

      // 오디오 컨텍스트 및 분석기 설정
      audioContextRef.current = new AudioContext()
      analyserRef.current = audioContextRef.current.createAnalyser()
      const source = audioContextRef.current.createMediaStreamSource(streamRef.current)
      source.connect(analyserRef.current)

      analyserRef.current.fftSize = 256
      const bufferLength = analyserRef.current.frequencyBinCount
      const dataArray = new Uint8Array(bufferLength)

      // 오디오 레벨 체크 함수
      const checkAudioLevel = () => {
        if (!analyserRef.current) return

        analyserRef.current.getByteTimeDomainData(dataArray)
        let sum = 0
        for (let i = 0; i < bufferLength; i++) {
          const value = (dataArray[i] - 128) / 128
          sum += value * value
        }
        const rms = Math.sqrt(sum / bufferLength)
        setAudioLevel(rms)

        // 오디오 레벨 로깅 (1초에 한 번)
        if (Date.now() % 1000 < 50) {
          console.log("마이크 테스트 오디오 레벨:", rms)
        }

        animationFrameRef.current = requestAnimationFrame(checkAudioLevel)
      }

      checkAudioLevel()
      setIsMicActive(true)
      setErrorMessage("")
    } catch (error) {
      console.error("마이크 테스트 시작 실패:", error)
      setErrorMessage("마이크 접근에 실패했습니다. 마이크가 연결되어 있고 다른 앱에서 사용 중이 아닌지 확인해주세요.")
      setIsMicActive(false)
    }
  }

  // 마이크 테스트 중지
  const stopMicTest = () => {
    if (animationFrameRef.current) {
      cancelAnimationFrame(animationFrameRef.current)
      animationFrameRef.current = null
    }

    if (audioContextRef.current) {
      audioContextRef.current.close().catch((err) => console.error("AudioContext 닫기 실패:", err))
      audioContextRef.current = null
    }

    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop())
      streamRef.current = null
    }

    setIsMicActive(false)
    setAudioLevel(0)
  }

  // 마이크 변경 시 테스트 재시작
  const handleDeviceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const deviceId = e.target.value
    setSelectedDevice(deviceId)

    if (isMicActive) {
      stopMicTest()
      setTimeout(() => {
        startMicTest()
      }, 300)
    }
  }

  // 확인 버튼 클릭 시 스트림 전달
  const handleConfirm = () => {
    if (streamRef.current) {
      // 스트림을 복제하여 전달 (원본 스트림은 정리)
      const audioTrack = streamRef.current.getAudioTracks()[0]
      if (audioTrack) {
        const newStream = new MediaStream([audioTrack.clone()])
        console.log("마이크 테스트에서 생성한 스트림을 전달합니다:", newStream)
        onConfirm(newStream, selectedDevice)
      } else {
        alert("마이크 오디오 트랙을 찾을 수 없습니다.")
      }
    } else {
      alert("마이크 테스트를 먼저 시작해주세요.")
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">마이크 테스트</h2>

        {errorMessage && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-md">{errorMessage}</div>}

        <div className="mb-4">
          <label className="block mb-2 font-medium">마이크 선택</label>
          <select className="w-full p-2 border rounded-md" value={selectedDevice} onChange={handleDeviceChange}>
            {audioDevices.map((device) => (
              <option key={device.deviceId} value={device.deviceId}>
                {device.label || `마이크 ${audioDevices.indexOf(device) + 1}`}
              </option>
            ))}
          </select>
        </div>

        <div className="mb-6">
          <label className="block mb-2 font-medium">마이크 레벨</label>
          <div className="h-8 bg-gray-200 rounded-md overflow-hidden">
            <div
              className="h-full bg-green-500 transition-all duration-100"
              style={{ width: `${Math.min(audioLevel * 500, 100)}%` }}
            ></div>
          </div>
          <div className="mt-2 text-sm text-gray-600">
            {audioLevel < 0.01 ? (
              <span className="text-red-500">소리가 감지되지 않습니다. 마이크를 확인해주세요.</span>
            ) : audioLevel < 0.05 ? (
              <span className="text-yellow-500">
                소리가 매우 작습니다. 마이크를 더 가까이 대거나 볼륨을 높여주세요.
              </span>
            ) : (
              <span className="text-green-500">마이크가 정상적으로 작동합니다.</span>
            )}
          </div>
        </div>

        <div className="flex justify-between">
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
            onClick={isMicActive ? stopMicTest : startMicTest}
          >
            {isMicActive ? "테스트 중지" : "테스트 시작"}
          </button>

          <div>
            <button className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 mr-2" onClick={onClose}>
              취소
            </button>
            <button
              className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600"
              onClick={handleConfirm}
              disabled={audioLevel < 0.01 && isMicActive}
            >
              확인
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default MicTestModal

"use client"

import type React from "react"
import { useState, useEffect, useRef, useCallback } from "react"
import axios from "axios"
import { OpenVidu, type Session } from "openvidu-browser"
import BassModelScene from "./BassModel"
import DrumModelScene from "./DrumModel"
import VocalModelScene from "./VocalModel"
import GuitarModelScene from "./GuitarModel"
import Audience from "./Audience"
import "./livehouse.css"
import image from "../../../../public/LiveHouse/BackGround.png"

interface SegmentParams {
  [key: string]: string | string[] | undefined
}

interface PageProps {
  params: Promise<SegmentParams>
}

interface Livehouse {
  title: string
  description: string
  hostId: string
  hostNickname: string
  participantId: string
  participantCount: number
  maxParticipants: number
}

interface ChatMessage {
  id: string
  text: string
  sender: string
  timestamp: string
}

interface JoinResponse {
  livehouseId: number
  sessionId: string
  token: string
  participantId: number
  nickname: string
}

const characterList = [
  { name: "drum", component: DrumModelScene },
  { name: "guitar", component: GuitarModelScene },
  { name: "vocal", component: VocalModelScene },
  { name: "basssomewherebass", component: BassModelScene },
]

export default function LivehousePage({ params }: PageProps) {
  const [livehouse, setLivehouse] = useState<Livehouse | null>(null)
  const [isHost, setIsHost] = useState(false)
  const [livehouseId, setLivehouseId] = useState<string | null>(null)
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>([])
  const [chatInput, setChatInput] = useState("")
  const [currentIndex, setCurrentIndex] = useState(0)
  const [isPlaying, setIsPlaying] = useState(false)
  const [volumeThreshold, setVolumeThreshold] = useState(0.01)
  const [displayThreshold, setDisplayThreshold] = useState(1)
  const [isMuted, setIsMuted] = useState(false)
  const [lastPlayingTime, setLastPlayingTime] = useState<number | null>(null)
  const [canChangeCharacter, setCanChangeCharacter] = useState(false)

  const chatContainerRef = useRef<HTMLDivElement>(null)
  const streamRef = useRef<MediaStream | null>(null)
  const audioContextRef = useRef<AudioContext | null>(null)
  const analyserRef = useRef<AnalyserNode | null>(null)
  const animationFrameRef = useRef<number | null>(null)
  const ovRef = useRef<OpenVidu | null>(null)
  const sessionRef = useRef<Session | null>(null)

  useEffect(() => {
    const resolveParams = async () => {
      const resolvedParams = await params
      setLivehouseId(resolvedParams.livehouseId as string)
    }
    resolveParams()
  }, [params])

  const getToken = () => localStorage.getItem("accessToken")
  const getCurrentUserNickname = () => localStorage.getItem("nickname")

  const initializeSession = useCallback(async () => {
    if (!livehouseId) return

    const token = getToken()
    if (!token) {
      console.error("No access token found")
      alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.")
      window.location.href = "/login"
      return
    }

    try {
      const joinResponse = await axios.post<JoinResponse>(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses/${livehouseId}/join`,
        {},
        { headers: { Authorization: `Bearer ${token}` } },
      )

      const { token: ovToken, nickname } = joinResponse.data
      const livehouseResponse = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses/${livehouseId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })

      setLivehouse(livehouseResponse.data)
      const currentUserNickname = nickname
      const hostStatus = currentUserNickname === livehouseResponse.data.hostNickname
      setIsHost(hostStatus)

      if (process.env.NEXT_PUBLIC_USE_MOCK === 'true') {
        return
      }

      await setupOpenVidu(ovToken, currentUserNickname)
    } catch (error) {
      console.error("Failed to initialize session:", error)
    }
  }, [livehouseId])

  const setupOpenVidu = async (ovToken: string, nickname: string) => {
    ovRef.current = new OpenVidu()
    sessionRef.current = ovRef.current.initSession()

    sessionRef.current.on("signal:chat", (event) => {
      if (event.data) {
        const msg = JSON.parse(event.data)
        setChatMessages((prev) => [...prev, msg])
      }
    })

    sessionRef.current.on("signal:character", (event) => {
      if (event.data) {
        const { index } = JSON.parse(event.data)
        setCurrentIndex(index)
      }
    })

    await sessionRef.current.connect(ovToken, { clientData: nickname })
  }

  useEffect(() => {
    if (!livehouseId) return

    initializeSession()

    return () => {
      if (sessionRef.current) sessionRef.current.disconnect()
      if (streamRef.current) streamRef.current.getTracks().forEach((track) => track.stop())
      if (audioContextRef.current) audioContextRef.current.close()
      if (animationFrameRef.current) cancelAnimationFrame(animationFrameRef.current)
    }
  }, [livehouseId, initializeSession])

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight
    }
  }, [chatMessages])

  useEffect(() => {
    let mounted = true

    const setupAudio = async () => {
      if (!isHost) return

      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        streamRef.current = stream

        audioContextRef.current = new AudioContext()
        analyserRef.current = audioContextRef.current.createAnalyser()
        const source = audioContextRef.current.createMediaStreamSource(stream)
        source.connect(analyserRef.current)

        analyserRef.current.fftSize = 256
        const bufferLength = analyserRef.current.frequencyBinCount
        const dataArray = new Uint8Array(bufferLength)

        const checkVolume = () => {
          if (!mounted || !analyserRef.current) return

          analyserRef.current.getByteTimeDomainData(dataArray)
          let sum = 0
          for (let i = 0; i < bufferLength; i++) {
            const value = (dataArray[i] - 128) / 128
            sum += value * value
          }
          const rms = Math.sqrt(sum / bufferLength)

          if (rms > volumeThreshold) {
            setIsPlaying(true)
            setLastPlayingTime(Date.now())
            setCanChangeCharacter(false)
          } else {
            setIsPlaying(false)
            if (lastPlayingTime && Date.now() - lastPlayingTime >= 3000) {
              setCanChangeCharacter(true)
            }
          }

          animationFrameRef.current = requestAnimationFrame(checkVolume)
        }

        checkVolume()
      } catch (error) {
        console.error("마이크 접근 오류:", error)
        alert("마이크에 접근할 수 없습니다.")
      }
    }

    setupAudio()

    return () => {
      mounted = false
      if (animationFrameRef.current) cancelAnimationFrame(animationFrameRef.current)
      if (streamRef.current) streamRef.current.getTracks().forEach((track) => track.stop())
      if (audioContextRef.current) audioContextRef.current.close()
    }
  }, [volumeThreshold, isHost]) // isMuted 제거

  const handleLeave = async () => {
    const token = getToken()
    if (!token) return

    if (livehouse?.participantId && livehouseId) {
      await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses/${livehouseId}/leave/${livehouse.participantId}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } },
      )
    }
    if (sessionRef.current) sessionRef.current.disconnect()
    window.location.href = "/livehouses"
  }

  const handleClose = async () => {
    const token = getToken()
    if (!token || !isHost || !livehouseId || !livehouse) return // livehouse null 체크 추가
  
    await axios.post(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses/${livehouseId}/close`,
      { participantId: livehouse.participantId },
      { headers: { Authorization: `Bearer ${token}` } },
    )
    if (sessionRef.current) sessionRef.current.disconnect()
    window.location.href = "/livehouses"
  }
  const handleChatSubmit = () => {
    if (chatInput.trim() === "") return

    const newMessage: ChatMessage = {
      id: Date.now().toString(),
      text: chatInput,
      sender: getCurrentUserNickname() || "Me",
      timestamp: new Date().toLocaleTimeString(),
    }

    if (sessionRef.current) {
      sessionRef.current.signal({
        type: "chat",
        data: JSON.stringify(newMessage),
      })
    }
    setChatInput("")
  }

  const handlePrev = () => {
    if (isHost && canChangeCharacter) {
      const newIndex = (currentIndex - 1 + characterList.length) % characterList.length
      setCurrentIndex(newIndex)
      sessionRef.current?.signal({
        type: "character",
        data: JSON.stringify({ index: newIndex }),
      })
    }
  }

  const handleNext = () => {
    if (isHost && canChangeCharacter) {
      const newIndex = (currentIndex + 1) % characterList.length
      setCurrentIndex(newIndex)
      sessionRef.current?.signal({
        type: "character",
        data: JSON.stringify({ index: newIndex }),
      })
    }
  }

  const mapDisplayToActual = (displayValue: number) => {
    return 0.01 + ((displayValue - 1) * (0.1 - 0.01)) / 9
  }

  const handleThresholdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (!isHost) return
    const newDisplayValue = Number.parseInt(event.target.value)
    setDisplayThreshold(newDisplayValue)
    setVolumeThreshold(mapDisplayToActual(newDisplayValue))
  }

  const handleMuteToggle = () => {
    if (!isHost || !streamRef.current) return
    const newMuteState = !isMuted
    streamRef.current.getAudioTracks().forEach((track) => (track.enabled = !newMuteState))
    setIsMuted(newMuteState)
  }

  const CurrentCharacterScene = characterList[currentIndex].component
  const cleanNickname = (nickname: string) => nickname.replace(/[0-9]/g, "")

  if (!livehouse || !livehouseId) return <div className="loading">Loading...</div>

  return (
    <div className="livehouse-container">
      <div className="livehouse-main" style={{backgroundImage : `url(${image.src})`}}>
        <div className="livehouse-content">
          <div className="livehouse-audience-layer">
            <Audience />
          </div>
          <div className="livehouse-character-layer">
            <CurrentCharacterScene />
          </div>
          {isHost && (
            <div className="livehouse-character-controls">
              {canChangeCharacter && (
                <>
                  <div className="livehouse-prev-button" onClick={handlePrev}>
                    <span>◀</span>
                  </div>
                  <div className="livehouse-next-button" onClick={handleNext}>
                    <span>▶</span>
                  </div>
                </>
              )}
            </div>
          )}
          <div className="livehouse-info">
            {isHost && (
              <div className="livehouse-control-group">
                <label className="livehouse-threshold-label">감도: {displayThreshold}</label>
                <input
                  type="range"
                  min="1"
                  max="10"
                  step="1"
                  value={displayThreshold}
                  onChange={handleThresholdChange}
                  className="livehouse-threshold-slider"
                />
                <button onClick={handleMuteToggle} className={`livehouse-mute-button ${isMuted ? "muted" : ""}`}>
                  {isMuted ? "음소거 해제" : "음소거"}
                </button>
              </div>
            )}
            <div className="livehouse-button-group">
              <button className="livehouse-leave-button" onClick={handleLeave}>
                나가기
              </button>
              {isHost && (
                <button className="livehouse-close-button" onClick={handleClose}>
                  방 닫기
                </button>
              )}
            </div>
          </div>
        </div>
        <div className="livehouse-chat">
          <div ref={chatContainerRef} className="livehouse-chat-container">
            {chatMessages.map((msg) => (
              <div
                key={msg.id}
                className={`livehouse-chat-message ${msg.sender === getCurrentUserNickname() ? "me" : ""}`}
              >
                <strong className="sender">{cleanNickname(msg.sender)}</strong>{" "}
                <span className="timestamp">({msg.timestamp})</span>
                <p>{msg.text}</p>
              </div>
            ))}
          </div>
          <div className="livehouse-chat-input-group">
            <input
              type="text"
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleChatSubmit()}
              placeholder="채팅을 입력해주세요."
              className="livehouse-chat-input"
            />
            <button className="livehouse-chat-submit" onClick={handleChatSubmit}>
              전송
            </button>
          </div>
          {isHost && (
            <div
              style={{
                position: "absolute",
                top: "1%",
                right: "21.5%",
                width: "50px",
                height: "50px",
                borderRadius: "50%",
                backgroundColor: isPlaying ? "red" : "grey",
              }}
            />
          )}
        </div>
      </div>
    </div>
  )
}
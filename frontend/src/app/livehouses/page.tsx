"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import Link from "next/link";
import { useRouter } from "next/navigation";
import Image from "next/image";
import "./livehouses.css";

interface Livehouse {
  livehouseId: string;
  title: string;
  hostNickname: string;
  participantCount: number;
  image?: string;
}

interface CreateResponse {
  livehouseId: string;
  sessionId: string; // OpenVidu 세션 ID
  token: string;     // OpenVidu 토큰
}

export default function LivehousesPage() {
  const [livehouses, setLivehouses] = useState<Livehouse[]>([]);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [maxParticipants, setMaxParticipants] = useState(2);
  const router = useRouter();

  const fetchLivehouses = async (keyword = "") => {
    try {
      const url = keyword
        ? `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses/search?keyword=${keyword}&page=0&size=20`
        : `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses`;
      const response = await axios.get<{ content: Livehouse[] }>(url);
      const updatedLivehouses = response.data.content.map((livehouse, index) => ({
        ...livehouse,
        image: `/images/pic${(index % 8) + 1}.jpg`,
      }));
      setLivehouses(updatedLivehouses);
    } catch (error) {
      console.error("Failed to fetch livehouses:", error);
    }
  };

  useEffect(() => {
    fetchLivehouses();
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchLivehouses(searchKeyword);
  };

  const handleCreateLivehouse = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem("accessToken");

      if (!token) {
        console.error("No access token found");
        alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        router.push("/login");
        return;
      }

      // 서버에 방 생성 요청
      const response = await axios.post<CreateResponse>(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/livehouses`,
        {
          title,
          description,
          maxParticipants,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const { livehouseId } = response.data;

      setIsModalOpen(false);
      // 생성된 방으로 이동 (로컬 스토리지 저장 제거)
      router.push(`/livehouses/${livehouseId}`);
    } catch (error) {
      console.error("Failed to create livehouse:", error);
      alert("라이브하우스 생성에 실패했습니다. 다시 시도해주세요.");
    }
  };

  const getNicknameWithoutNumbers = (nickname: string) => {
    return nickname.replace(/\d+$/, "");
  };

  return (
    <div className="container">
      <div className="header">
        <h1 className="title">라이브하우스</h1>
        <div className="header-right">
          <form onSubmit={handleSearch} className="search-form">
            <input
              type="text"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              placeholder="라이브하우스 검색..."
              className="search-input"
            />
          </form>
          <button onClick={() => setIsModalOpen(true)} className="create-button">
            방 생성하기
          </button>
        </div>
      </div>

      <div className="grid">
        {livehouses.map((livehouse) => (
          <Link
            href={`/livehouses/${livehouse.livehouseId}`}
            key={livehouse.livehouseId}
            className="card"
            style={{ backgroundImage: `url(${livehouse.image})` }}
          >
            <div className="card-content">
              <div className="left-section">
                <h2>{livehouse.title}</h2>
                <p className="host-nickname">
                  {getNicknameWithoutNumbers(livehouse.hostNickname)}
                </p>
              </div>
              <div className="right-section">
                <p className="participant-count">
                  {livehouse.participantCount}
                  <Image
                    src="/icons/peopleicon.png"
                    alt="people"
                    width={16}
                    height={16}
                    className="participant-icon"
                  />
                </p>
              </div>
            </div>
          </Link>
        ))}
      </div>

      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>라이브하우스 생성</h2>
            <form onSubmit={handleCreateLivehouse}>
              <div className="form-group">
                <label>제목</label>
                <input
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="input"
                  required
                />
              </div>
              <div className="form-group">
                <label>설명</label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="textarea"
                />
              </div>
              <div className="form-group">
                <label>최대 참여자 수 (2-10)</label>
                <input
                  type="number"
                  value={maxParticipants}
                  onChange={(e) => setMaxParticipants(Number(e.target.value))}
                  min={2}
                  max={10}
                  className="input"
                  required
                />
              </div>
              <div className="modal-buttons">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="cancel-button"
                >
                  취소
                </button>
                <button type="submit" className="submit-button">
                  생성
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
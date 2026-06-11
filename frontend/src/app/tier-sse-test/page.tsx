// C:\Users\SSAFY\Desktop\project\S12P21E107\frontend\src\app\tier-sse-test\page.tsx
"use client";

import { NextPage } from "next";
import { useState, useRef, useEffect } from "react";
import { useAuthStore } from "@/store/auth";

// 로그 항목 타입 정의
interface LogItem {
  text: string;
  timestamp: string;
  type: "info" | "success" | "error" | "event";
}

// // 알림 타입 정의
// interface Notification {
//   notificationId: number;
//   type: string;
//   message: string;
//   createdAt: string;
// }

const Page: NextPage = () => {
  const { accessToken, memberId: currentUserId } = useAuthStore();

  // 상태 관리
  const [targetMemberId, setTargetMemberId] = useState<string>(currentUserId || "");
  const [serverUrl] = useState<string>(process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080");
  const [logs, setLogs] = useState<LogItem[]>([]);

  // 티어 분석 결과 필드
  const [tierResult, setTierResult] = useState<string>("SILVER");
  const [tuneScore, setTuneScore] = useState<number>(80);
  const [beatScore, setBeatScore] = useState<number>(80);
  const [toneScore, setToneScore] = useState<number>(80);
  const [totalScore, setTotalScore] = useState<number>(85);
  const [analysisContent, setAnalysisContent] = useState<string>("조율이 잘 맞고 박자감이 좋습니다.");
  const [attemptId, setAttemptId] = useState<number>(1);

  const logEndRef = useRef<HTMLDivElement>(null);
  const tierOptions = ["BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"];

  // 스크롤을 항상 최신 로그로 유지
  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [logs]);

  // 로그 추가 함수
  const addLog = (text: string, type: "info" | "success" | "error" | "event" = "info") => {
    const timestamp = new Date().toLocaleTimeString();
    setLogs((prevLogs) => [...prevLogs, { text, timestamp, type }]);
  };

  // 티어 분석 결과 전송
  const sendAnalysisResult = async () => {
    if (!accessToken) {
      addLog("토큰이 없습니다. 로그인이 필요합니다.", "error");
      return;
    }

    if (!targetMemberId) {
      addLog("대상 사용자 ID를 입력해주세요", "error");
      return;
    }

    try {
      addLog(`RabbitMQ 메시지 전송 시도...`, "info");

      // UUID 형식으로 변환
      let senderUuid;
      try {
        senderUuid = targetMemberId;
        // UUID 형식 검증
        if (!/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(senderUuid)) {
          throw new Error("유효한 UUID 형식이 아닙니다");
        }
      } catch (error) {
        addLog(`잘못된 UUID 형식: ${error instanceof Error ? error.message : String(error)}`, "error");
        return;
      }

      // MessageDTO 형식에 맞게 페이로드 구성
      const payload = {
        content: {
          result: "success",
          tierResult: tierResult,
          score: totalScore,
          analysis: analysisContent,
        },
        senderId: senderUuid,
        attemptId: attemptId,
        beat_score: beatScore,
        tune_score: tuneScore,
        tone_score: toneScore,
        recordFilename: "", // 필수 필드지만 테스트에서는 빈 값
        originalFilename: "", // 필수 필드지만 테스트에서는 빈 값
        timestamp: new Date().toISOString(),
      };

      addLog(`전송할 페이로드: ${JSON.stringify(payload)}`, "info");

      const response = await fetch(`${serverUrl}/api/test/send-rabbitmq-message`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(`메시지 전송 실패: ${response.status} ${response.statusText}`);
      }

      const result = await response.json();
      addLog(`메시지 전송 성공: ${JSON.stringify(result)}`, "success");
      addLog("곧 SSE를 통해 알림이 도착해야 합니다", "info");
    } catch (error) {
      addLog(`메시지 전송 오류: ${error instanceof Error ? error.message : String(error)}`, "error");
    }
  };

  // 로그 초기화
  const clearLogs = () => {
    setLogs([]);
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-4 bg-gray-50">
      <div className="w-full max-w-6xl bg-white p-6 rounded-lg shadow-md">
        <h1 className="text-2xl font-bold mb-6 text-center">티어 분석 알림 테스트</h1>

        <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-6">
          <p className="text-blue-700">
            <strong>안내:</strong> 이 페이지에서는 티어 분석 결과를 임의로 생성하여 전송하고, 서버로부터 SSE를 통해
            알림을 받을 수 있습니다.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          {/* 티어 분석 결과 설정 */}
          <div className="border rounded-md p-4">
            <h2 className="text-lg font-semibold mb-4">티어 분석 결과 설정</h2>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">대상 사용자 ID (UUID)</label>
                <input
                  type="text"
                  value={targetMemberId}
                  onChange={(e) => setTargetMemberId(e.target.value)}
                  className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
                  placeholder="UUID 형식으로 입력 (예: 123e4567-e89b-12d3-a456-426614174000)"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Attempt ID</label>
                <input
                  type="number"
                  value={attemptId}
                  onChange={(e) => setAttemptId(Number(e.target.value))}
                  className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
                  placeholder="Attempt ID"
                  min="1"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">티어 결과</label>
                <select
                  value={tierResult}
                  onChange={(e) => setTierResult(e.target.value)}
                  className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
                >
                  {tierOptions.map((tier) => (
                    <option key={tier} value={tier}>
                      {tier}
                    </option>
                  ))}
                </select>
              </div>

              <button
                onClick={sendAnalysisResult}
                className="w-full py-2 bg-green-500 hover:bg-green-600 text-white rounded-md"
              >
                분석 결과 전송
              </button>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
          {/* 조율 점수 */}
          <div className="border rounded-md p-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">조율 점수</label>
            <input
              type="number"
              value={tuneScore}
              onChange={(e) => setTuneScore(Math.min(100, Math.max(0, parseInt(e.target.value) || 0)))}
              className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
              min="0"
              max="100"
            />
            <input
              type="range"
              value={tuneScore}
              onChange={(e) => setTuneScore(parseInt(e.target.value))}
              className="w-full mt-2"
              min="0"
              max="100"
            />
          </div>

          {/* 리듬감 점수 */}
          <div className="border rounded-md p-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">리듬감 점수</label>
            <input
              type="number"
              value={beatScore}
              onChange={(e) => setBeatScore(Math.min(100, Math.max(0, parseInt(e.target.value) || 0)))}
              className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
              min="0"
              max="100"
            />
            <input
              type="range"
              value={beatScore}
              onChange={(e) => setBeatScore(parseInt(e.target.value))}
              className="w-full mt-2"
              min="0"
              max="100"
            />
          </div>

          {/* 음색 점수 */}
          <div className="border rounded-md p-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">음색 점수</label>
            <input
              type="number"
              value={toneScore}
              onChange={(e) => setToneScore(Math.min(100, Math.max(0, parseInt(e.target.value) || 0)))}
              className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
              min="0"
              max="100"
            />
            <input
              type="range"
              value={toneScore}
              onChange={(e) => setToneScore(parseInt(e.target.value))}
              className="w-full mt-2"
              min="0"
              max="100"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          {/* 총 점수 */}
          <div className="border rounded-md p-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">총 점수</label>
            <input
              type="number"
              value={totalScore}
              onChange={(e) => setTotalScore(Math.min(100, Math.max(0, parseInt(e.target.value) || 0)))}
              className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
              min="0"
              max="100"
            />
            <input
              type="range"
              value={totalScore}
              onChange={(e) => setTotalScore(parseInt(e.target.value))}
              className="w-full mt-2"
              min="0"
              max="100"
            />
          </div>

          {/* 분석 내용 */}
          <div className="border rounded-md p-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">분석 내용</label>
            <textarea
              value={analysisContent}
              onChange={(e) => setAnalysisContent(e.target.value)}
              className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
              rows={3}
              placeholder="분석 내용을 입력하세요"
            />
          </div>
        </div>

        {/* 로그창 */}
        <div className="border rounded-md">
          <div className="flex justify-between items-center p-2 border-b">
            <h2 className="text-lg font-semibold">로그</h2>
            <button
              onClick={clearLogs}
              className="px-3 py-1 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-md text-sm"
            >
              로그 지우기
            </button>
          </div>
          <div className="h-64 p-2 overflow-y-auto bg-gray-50" style={{ scrollBehavior: "smooth" }}>
            {logs.length === 0 ? (
              <p className="text-gray-500 italic">로그가 없습니다.</p>
            ) : (
              logs.map((log, index) => (
                <div
                  key={index}
                  className={`mb-1 ${
                    log.type === "error"
                      ? "text-red-600"
                      : log.type === "success"
                      ? "text-green-600"
                      : log.type === "event"
                      ? "text-purple-600"
                      : "text-gray-800"
                  }`}
                >
                  <span className="text-xs text-gray-500">[{log.timestamp}]</span> {log.text}
                </div>
              ))
            )}
            <div ref={logEndRef} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Page;

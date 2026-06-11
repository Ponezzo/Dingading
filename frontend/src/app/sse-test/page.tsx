// C:\Users\SSAFY\Desktop\project\S12P21E107\frontend\src\app\sse-test\page.tsx
"use client";

import { NextPage } from "next";
import { useState, useRef, useEffect } from "react";
import { useAuthStore } from "@/store/auth";

// 로그 항목 타입 정의
interface LogItem {
  text: string;
  timestamp: string;
  type: 'info' | 'success' | 'error' | 'event';
}

// 메시지 타입 enum
enum MessageType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  AUDIO = 'AUDIO',
  VIDEO = 'VIDEO',
  FILE = 'FILE',
  SYSTEM = 'SYSTEM'
}

const NotificationTest: NextPage = () => {
  const { accessToken } = useAuthStore();
  
  // 상태 관리
  const [memberId, setMemberId] = useState<string>('');
  const [roomId, setRoomId] = useState<string>('');
  const [message, setMessage] = useState<string>('');
  const [logs, setLogs] = useState<LogItem[]>([]);
  
  const logEndRef = useRef<HTMLDivElement>(null);


  // 스크롤을 항상 최신 로그로 유지
  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [logs]);

  // 로그 추가 함수
  const addLog = (text: string, type: 'info' | 'success' | 'error' | 'event' = 'info') => {
    const timestamp = new Date().toLocaleTimeString();
    setLogs(prevLogs => [...prevLogs, { text, timestamp, type }]);
  };

  // 방 만들기 버튼 클릭 핸들러
  const handleCreateRoom = async () => {
    if (!memberId) {
      addLog('Member ID를 입력해주세요', 'error');
      return;
    }

    try {
      addLog(`POST /chatrooms/personal/${memberId} 요청 시작`);
      
      if (!accessToken) {
        addLog('토큰이 없습니다. 로그인이 필요합니다.', 'error');
        return;
      }

      const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || '';
      const response = await fetch(`${baseUrl}/chatrooms/personal/${memberId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        }
      });

      if (!response.ok) {
        throw new Error(`API 호출 실패: ${response.status}`);
      }

      const data = await response.json();
      addLog(`응답 성공: ${JSON.stringify(data)}`, 'success');
      
      if (data && data.chatroomId) {
        setRoomId(data.chatroomId);
        addLog(`방 ID가 설정되었습니다: ${data.chatroomId}`, 'success');
      } else {
        addLog('응답에서 방 ID를 찾을 수 없습니다', 'error');
      }
    } catch (error) {
      addLog(`에러 발생: ${error instanceof Error ? error.message : '알 수 없는 오류'}`, 'error');
    }
  };

  // 메세지 전송 버튼 클릭 핸들러
  const handleSendMessage = async () => {
    if (!roomId) {
      addLog('방 ID를 먼저 설정해주세요', 'error');
      return;
    }
    
    if (!message) {
      addLog('메세지를 입력해주세요', 'error');
      return;
    }

    try {
      addLog(`POST /chatrooms/${roomId}/messages 요청 시작`);
      
      if (!accessToken) {
        addLog('토큰이 없습니다. 로그인이 필요합니다.', 'error');
        return;
      }

      const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || '';
      const response = await fetch(`${baseUrl}/chatrooms/${roomId}/messages`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify({
          message: message,
          messageType: MessageType.TEXT
        })
      });

      if (!response.ok) {
        throw new Error(`API 호출 실패: ${response.status}`);
      }

      const data = await response.json();
      addLog(`메세지 전송 성공: ${JSON.stringify(data)}`, 'success');
      addLog(`알림이 자동으로 수신되어야 합니다. 알림을 확인해보세요.`, 'info');
      setMessage(''); // 메세지 필드 초기화
    } catch (error) {
      addLog(`에러 발생: ${error instanceof Error ? error.message : '알 수 없는 오류'}`, 'error');
    }
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-4">
      <div className="w-full max-w-6xl bg-white p-6 rounded-lg shadow-md">
        <h1 className="text-2xl font-bold mb-6 text-center">알림 테스트 페이지</h1>
        
        <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-6">
          <p className="text-blue-700">
            <strong>안내:</strong> 로그인 시 SSE 연결이 자동으로 설정되어 있습니다. 알림을 받아보려면 다른 사용자와 채팅해보세요.
          </p>
        </div>
        
        {/* 세로 레이아웃으로 변경 */}
        <div className="flex flex-wrap mb-6">
          {/* 입력 필드 (왼쪽 2/3) */}
          <div className="w-full md:w-2/3 pr-0 md:pr-6 mb-6 md:mb-0">
            <div className="space-y-4 p-4 border rounded-md h-full">
              <h2 className="text-lg font-semibold mb-2">입력</h2>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Member ID (채팅 상대방)</label>
                <input
                  id="memberId"
                  type="text"
                  value={memberId}
                  onChange={(e) => setMemberId(e.target.value)}
                  className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
                  placeholder="UUID 형식으로 입력"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">방 ID</label>
                <input
                  id="roomId"
                  type="text"
                  value={roomId}
                  onChange={(e) => setRoomId(e.target.value)}
                  className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
                  placeholder="방 ID (자동 설정됨)"
                  readOnly={roomId !== ''}
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">메세지</label>
                <input
                  id="message"
                  type="text"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500 text-black"
                  placeholder="보낼 메세지"
                />
              </div>
            </div>
          </div>
          
          {/* 버튼 및 안내 (오른쪽 1/3 - 세로 정렬) */}
          <div className="w-full md:w-1/3">
            <div className="flex flex-col h-full space-y-4 p-4 border rounded-md">
              <h2 className="text-lg font-semibold mb-2">동작</h2>
              
              {/* 버튼 세로 정렬 */}
              <button
                onClick={handleCreateRoom}
                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-4 rounded"
              >
                방 만들기
              </button>
              
              <button
                onClick={handleSendMessage}
                className="w-full bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-4 rounded"
                disabled={!roomId}
              >
                메세지 전송
              </button>
              
              <div className="mt-4 p-4 bg-gray-50 rounded-md flex-grow">
                <h3 className="font-medium text-gray-700 mb-2">알림 테스트 방법:</h3>
                <ol className="list-decimal pl-5 text-sm text-gray-600 space-y-1">
                  <li>상대방 Member ID를 입력하고 방 만들기 버튼 클릭.</li>
                  <li>메세지를 입력하고 메세지 전송 버튼 클릭.</li>
                  <li>대화 상대방이 온라인 상태라면 알림이 전송됩니다.</li>
                  <li>상대방에게 메시지 받으면 알림이 표시됩니다.</li>
                </ol>
              </div>
            </div>
          </div>
        </div>
        
        {/* 로그창 */}
        <div className="border rounded-md">
          <h2 className="text-lg font-semibold p-2 border-b">로그</h2>
          <div 
            id="log" 
            className="h-64 p-2 overflow-y-auto bg-gray-50"
            style={{ scrollBehavior: 'smooth' }}
          >
            {logs.length === 0 ? (
              <p className="text-gray-500 italic">로그가 없습니다.</p>
            ) : (
              logs.map((log, index) => (
                <div 
                  key={index} 
                  className={`mb-1 ${
                    log.type === 'error' 
                      ? 'text-red-600' 
                      : log.type === 'success' 
                      ? 'text-green-600' 
                      : log.type === 'event' 
                      ? 'text-purple-600' 
                      : 'text-gray-800'
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

export default NotificationTest;

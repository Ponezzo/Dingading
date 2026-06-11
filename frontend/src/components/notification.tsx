'use client'

import { useEffect, useState, useRef } from 'react'
import { useRouter } from 'next/navigation'
import styles from "./notice.module.css"
import Image from "next/image"
import bell from "@/assets/Bell.svg"
import { useAuthStore } from "@/store/auth"
import { useNotificationStore } from "@/store/notification"

// 알림 타입 열거형
export enum NotificationType {
  CHAT = 'CHAT',
  RECRUITMENT = 'RECRUITMENT',
  BAND = 'FOLLOW',
  RANK = 'RANK' // 티어 관련 알림 추가
}

// 알림 정보 인터페이스
export interface NotificationDTO {
  notificationId: number;
  chatRoomId?: string;
  messageId?: string;
  senderId?: string;
  senderNickname?: string;
  senderProfileUrl?: string;
  receiverId: string;
  message: string;
  readOrNot: boolean;
  type: NotificationType;
  acceptUrl?: string;
  rejectUrl?: string;
  createdAt: string;
  attemptId?: number; // 시도 ID
  isSuccess?: boolean; // 성공 여부
  tierAndInstrument?: string;
}

const Notice = () => {
  // 로컬 상태는 UI 관련 상태만 유지
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);
  const notificationRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  const { accessToken, isLoggedIn } = useAuthStore();
  // 스토어에서 필요한 모든 함수와 상태 가져오기
  const {
    fetchNotification,
    notifications,
    markAsRead: storeMarkAsRead,
    markAllAsRead: storeMarkAllAsRead,
    addNotification
  } = useNotificationStore();

  // SSE 이벤트 리스너 설정 상태
  const [sseListenersSetup, setSseListenersSetup] = useState(false);

  // 외부 클릭 시 알림 드롭다운 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (notificationRef.current && !notificationRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // 초기 알림 목록 가져오기 및 SSE 이벤트 리스너 설정
  useEffect(() => {
    if (isLoggedIn && accessToken && !sseListenersSetup) {
      // 초기 알림 목록 가져오기
      fetchNotification();

      // SSE 이벤트 리스너 설정
      setupSseListeners();
      setSseListenersSetup(true);
    }
  }, [isLoggedIn, accessToken, fetchNotification, sseListenersSetup]);

  // 읽지 않은 알림 개수 계산 - notifications가 변경될 때마다 실행
  useEffect(() => {
    if (notifications) {
      const unread = notifications.filter(notification => !notification.readOrNot).length;
      setUnreadCount(unread);
    }
  }, [notifications]);

  // 커스텀 이벤트 타입 정의
  interface NotificationEvent extends CustomEvent {
    detail: string; // NotificationDTO를 담은 JSON 문자열
  }

  // SSE 이벤트 리스너 설정 함수
  const setupSseListeners = () => {
    if (!isLoggedIn || !accessToken) return;

    // 전역 이벤트 리스너를 통해 알림 이벤트 구독
    const handleNotificationEvent = (event: NotificationEvent) => {
      try {
        const notificationData = JSON.parse(event.detail) as NotificationDTO;
        console.log("새로운 알림 수신:", notificationData);

        // 스토어에 직접 알림 추가 - 즉시 UI에 반영됨
        addNotification(notificationData);

        // 토스트 알림 표시
        showNotificationToast(notificationData);
      } catch (e) {
        console.error("알림 데이터 파싱 에러:", e);
      }
    };

    // 커스텀 이벤트 리스너 등록
    window.addEventListener('notification-received', handleNotificationEvent as EventListener);

    // 컴포넌트 언마운트 시 이벤트 리스너 제거를 위해 정리 함수 반환
    return () => {
      window.removeEventListener('notification-received', handleNotificationEvent as EventListener);
    };
  };

  // 토스트 알림 표시 함수
  const showNotificationToast = (notification: NotificationDTO) => {
    // 브라우저 알림 권한 요청 및 표시
    if (Notification.permission === "granted") {
      // 알림 타입에 따라 다른 타이틀과 아이콘 설정
      let title = '';
      let icon = '';

      switch(notification.type) {
        case NotificationType.CHAT:
          title = '새 메시지가 도착했습니다';
          icon = '/images/chat-icon.png';
          break;
        case NotificationType.RANK:
          title = '티어 알림';
          icon = '/images/rank-icon.png';
          break;
        default:
          title = '새 알림이 있습니다';
          icon = '/images/notification-icon.png';
      }

      const notificationOptions = {
        body: notification.message,
        icon: icon
      };

      new Notification(title, notificationOptions);
    } else if (Notification.permission !== "denied") {
      Notification.requestPermission();
    }
  };

  // 알림 읽음 처리 - 스토어 함수 사용
  const handleMarkAsRead = async (notificationId: number) => {
    try {
      // 디버깅을 위한 로그 추가
      console.log(`알림 읽음 처리 시작: ${notificationId}`);

      await storeMarkAsRead(notificationId);

      console.log(`알림 읽음 처리 완료: ${notificationId}`);
    } catch (error) {
      console.error('알림 읽음 처리에 실패했습니다:', error);
    }
  };

  // 알림 클릭 핸들러
  const handleNotificationClick = (notification: NotificationDTO) => {
    handleMarkAsRead(notification.notificationId);

    // 알림 타입에 따른 다른 처리
    switch(notification.type) {
      case NotificationType.CHAT:
        if (notification.chatRoomId) {
          router.push(`/chat/${notification.chatRoomId}`);
        }
        break;
      case NotificationType.RANK:
        console.log("티어 알림 클릭:", notification);

        // UNRANKED를 IRON으로 변환
        let tierInfo = notification.tierAndInstrument;
        if (tierInfo && tierInfo.includes('UNRANKED')) {
          tierInfo = tierInfo.replace('UNRANKED', 'IRON');
        }

        if (notification.attemptId) {
          if (notification.isSuccess) {
            // tierAndInstrument 정보를 URL 쿼리 파라미터로 전달
            const tierInfoParam = tierInfo
                ? `?tierInfo=${encodeURIComponent(tierInfo)}`
                : '';
            router.push(`/tier/attempt/${notification.attemptId}/success${tierInfoParam}`);
          } else {
            router.push(`/tier/attempt/${notification.attemptId}/fail`);
          }
        } else {
          router.push('/tier');
        }
        break;
      default:
        // 기본적으로는 아무 동작도 하지 않음
        break;
    }

    // 드롭다운 닫기
    setShowNotifications(false);
  };

  // 모든 알림 읽음 처리 - 스토어 함수 사용
  const handleMarkAllAsRead = async () => {
    try {
      await storeMarkAllAsRead();
    } catch (error) {
      console.error('모든 알림 읽음 처리에 실패했습니다:', error);
    }
  };

  // 알림 버튼 클릭 핸들러
  const toggleNotifications = () => {
    setShowNotifications(!showNotifications);
  };

  // 로그인 상태가 아니면 null 반환
  if (!isLoggedIn) return null;

  return (
      <div className={styles.container} ref={notificationRef}>
        <div
            className={styles.bellContainer}
            onClick={toggleNotifications}
        >
          <div className={styles.bellIcon}>
            <Image
                src={bell}
                alt='bell'
                width={24}
                height={24}
            />
          </div>
          {unreadCount > 0 && (
              <span className={styles.badge}>{unreadCount}</span>
          )}
        </div>

        {showNotifications && (
            <div className={styles.dropdown}>
              <div className={styles.header}>
                <h3>알림</h3>
                {unreadCount > 0 && (
                    <button
                        className={styles.readAllBtn}
                        onClick={handleMarkAllAsRead}
                    >
                      모두 읽음
                    </button>
                )}
              </div>
              <div className={styles.list}>
                {notifications && notifications.length > 0 ? (
                    notifications.map(notification => (
                        <div
                            key={notification.notificationId}
                            className={`${styles.item} ${!notification.readOrNot ? styles.unread : ''}`}
                            onClick={() => handleNotificationClick(notification)}
                        >
                          <div className={styles.content}>
                            {notification.senderProfileUrl && (
                                <div className={styles.avatar}>
                                  <img
                                      src={notification.senderProfileUrl}
                                      alt={notification.senderNickname || '사용자'}
                                  />
                                </div>
                            )}
                            <div className={styles.message}>
                              <p>{notification.message}</p>
                              <small>{new Date(notification.createdAt).toLocaleString()}</small>
                            </div>
                          </div>

                          {!notification.readOrNot && (
                              <div className={styles.indicator}></div>
                          )}
                        </div>
                    ))
                ) : (
                    <div className={styles.empty}>
                      <p>알림이 없습니다</p>
                    </div>
                )}
              </div>
            </div>
        )}
      </div>
  );
};

export default Notice;
package com.mickey.dinggading.domain.member.repository;

import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.model.entity.Notification;
import com.mickey.dinggading.model.NotificationType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiver(Member receiver, Pageable pageable);

    Page<Notification> findByReceiverAndType(Member receiver, NotificationType type, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification c SET c.readOrNot = true WHERE c.receiver.memberId = :receiverId AND c.readOrNot = false")
    int markAllAsReadByReceiverId(@Param("receiverId") UUID receiverId);

    @Modifying
    @Query("UPDATE Notification c SET c.readOrNot = true WHERE c.chatRoomId = :chatRoomId AND c.receiver.memberId = :receiverId AND c.readOrNot = false")
    int markAllAsReadByChatRoomIdAndReceiverId(@Param("chatRoomId") String chatRoomId,
                                               @Param("receiverId") UUID receiverId);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :receiver AND n.readOrNot = :readOrNot")
    Page<Notification> findByReceiverAndReadStatus(@Param("receiver") Member receiver,
                                                   @Param("readOrNot") boolean readOrNot, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.memberId = :receiverId AND n.readOrNot = false")
    int countUnreadNotifications(@Param("receiverId") UUID receiverId);
}
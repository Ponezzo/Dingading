// 파일 경로: src/main/java/com/mickey/dinggading/domain/livehouse/repository/LivehouseRepository.java
package com.mickey.dinggading.domain.livehouse.repository;

import com.mickey.dinggading.domain.livehouse.entity.Livehouse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LivehouseRepository extends JpaRepository<Livehouse, Long> {
    @Query(value = "SELECT l.* FROM livehouses l " +
            "LEFT JOIN participants p ON l.livehouse_id = p.livehouse_id " +
            "WHERE l.session_id IN :sessionIds " +
            "GROUP BY l.livehouse_id " +
            "ORDER BY COUNT(p.participant_id) DESC",
            countQuery = "SELECT COUNT(*) FROM livehouses WHERE session_id IN :sessionIds",
            nativeQuery = true)
    Page<Livehouse> findBySessionIdInOrderByParticipantCountDesc(List<String> sessionIds, Pageable pageable);

    Optional<Livehouse> findBySessionIdInAndHostId(List<String> activeSessions, UUID memberId);

    Optional<Livehouse> findBySessionIdInAndLivehouseId(List<String> activeSessions, Long livehouseId);

    @Query(value = "SELECT l.* FROM livehouses l " +
            "LEFT JOIN participants p ON l.livehouse_id = p.livehouse_id " +
            "WHERE l.session_id IN :activeSessions " +
            "AND (l.title LIKE %:keyword% OR l.host_nickname LIKE %:keyword%) " +
            "GROUP BY l.livehouse_id " +
            "ORDER BY COUNT(p.participant_id) DESC",
            countQuery = "SELECT COUNT(*) FROM livehouses l " +
                    "WHERE l.session_id IN :activeSessions " +
                    "AND (l.title LIKE %:keyword% OR l.host_nickname LIKE %:keyword%)",
            nativeQuery = true)
    Page<Livehouse> searchByKeyword(@Param("activeSessions") List<String> activeSessions,
                                    @Param("keyword") String keyword, Pageable pageable);

    Optional<Livehouse> findBySessionId(String sessionId);
}

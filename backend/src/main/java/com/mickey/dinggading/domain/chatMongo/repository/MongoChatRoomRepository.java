package com.mickey.dinggading.domain.chatMongo.repository;

import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomMongo;
import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomSettingMongo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MongoChatRoomRepository extends MongoRepository<ChatRoomMongo, String> {

    List<ChatRoomMongo> findByParticipantsMemberId(String currentUserId);

    @Query("{'roomType': 'PERSONAL', $and: [{'participants': {$elemMatch: {memberId: ?0}}}, {'participants': {$elemMatch: {memberId: ?1}}}]}")
    Optional<ChatRoomMongo> findPersonalChatroom(String currentUserId, String targetUserId);

    @Query("{'roomType': 'SELF', 'participants': {$elemMatch: {memberId: ?#{#memberId}}}}")
    Optional<ChatRoomMongo> findByRoomTypeAndParticipantId(@Param("memberId") String memberId);

    @Query(value = "{'_id' : ?0, 'participants.memberId': ?1}", exists = true)
    Boolean existsByIdAndParticipantsMemberId(String chatRoomId, String memberId);

    Optional<ChatRoomMongo> findById(String roomId);

    @Query(value = "{'_id': ?0, 'settings': {$elemMatch: {'memberId': ?1}}}", fields = "{'settings.$': 1}")
    Optional<ChatRoomSettingMongo> findChatRoomSettingByRoomIdAndMemberId(String chatRoomId, String memberId);
}

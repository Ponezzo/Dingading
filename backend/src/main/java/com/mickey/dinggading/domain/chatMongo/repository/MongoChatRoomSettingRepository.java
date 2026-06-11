package com.mickey.dinggading.domain.chatMongo.repository;

import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomSettingMongo;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoChatRoomSettingRepository extends MongoRepository<ChatRoomSettingMongo, String> {
    List<ChatRoomSettingMongo> findAllByChatRoomId(String roomId);
}

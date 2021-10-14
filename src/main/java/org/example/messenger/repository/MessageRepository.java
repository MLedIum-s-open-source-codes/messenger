package org.example.messenger.repository;

import org.example.messenger.domain.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

  Optional<Message> findById(String id);

  @Query("{ chatId: ?0, usersPersonalSequenceIds: { $elemMatch: { objectId: ?1 } } }")
  List<Message> findAllByChatIdAndUserId(String chatId, String userId);

  @Query("{ chatId: ?0, usersPersonalSequenceIds: { $elemMatch: { objectId: ?1 } }, chatSeqId: { $eq: ?2 } }")
  Optional<Message> findByChatIdAndUserIdAndChatSeqId(String chatId, String userId, Integer chatSeqId);

  @Query("{ usersPersonalSequenceIds: { $elemMatch: { objectId: ?0, seqId: ?1 } } }")
  Optional<Message> findByUserIdAndPersonalSeqId(String userId, Integer personalSeqId);

}

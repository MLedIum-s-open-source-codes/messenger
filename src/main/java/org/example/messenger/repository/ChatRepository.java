package org.example.messenger.repository;

import org.example.messenger.domain.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

  Optional<Chat> findById(String id);

  @Query("{ users: { $all: [ { $elemMatch: { userId: ?0 } },  { $elemMatch: { userId: ?1 } } ] }, type: ?2 }")
  Optional<Chat> findByUsersIds(String userId1, String userId2, String type);

  @Query("{ users: { $elemMatch: { userId: ?0 } }, type: ?1 }")
  Optional<Chat> findByUserId(String userId, String type);

  @Query("{ users: { $elemMatch: { userId: ?0 } } }")
  List<Chat> findAllByUserId(String userId);

}

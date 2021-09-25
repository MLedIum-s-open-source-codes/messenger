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

  @Query("{ users: { $all: [ { $elemMatch: { userId: ?0 } },  { $elemMatch: { userId: ?1 } } ] } }")
  Optional<Chat> findByUsersIds(String id1, String id2);

  @Query(" { users: { $elemMatch: { userId: ?0 } } } ")
  List<Chat> findAllByUserId(String id);

}

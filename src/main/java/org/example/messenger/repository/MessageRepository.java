package org.example.messenger.repository;

import org.example.messenger.domain.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

  Optional<Message> findById(String id);

}

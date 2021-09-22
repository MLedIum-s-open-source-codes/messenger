package org.example.messenger.repository;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

  Optional<Chat> findById(String id);

  Optional<Chat> findByUsersContains(User... users);

  List<Chat> findAllByUsersContains(User... user);

  boolean existsByUsersContains(User... users);

}

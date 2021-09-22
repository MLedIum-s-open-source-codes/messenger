package org.example.messenger.repository;

import org.example.messenger.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findById(String id);

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

}

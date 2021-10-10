package org.example.messenger.repository;

import org.example.messenger.domain.model.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends MongoRepository<MediaFile, String> {

  @Override
  Optional<MediaFile> findById(String s);

}

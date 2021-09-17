package org.example.messenger.repository;

import org.example.messenger.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatRepository extends JpaRepository<ChatUser, Long> {

  Optional<ChatUser> findByChat_IdAndUser_Id(Long chatId, Long userId);

  List<ChatUser> getAllByUser_Id(Long userId);

  List<ChatUser> getAllByChat_Id(Long chatId);

  boolean existsByChat_IdAndUser_Id(Long chatId, Long userId);

}

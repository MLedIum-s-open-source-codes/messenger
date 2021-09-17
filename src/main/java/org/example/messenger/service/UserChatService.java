package org.example.messenger.service;

import org.example.messenger.entity.ChatUser;

import java.util.List;

public interface UserChatService {

  ChatUser create(Long chatId, Long... userId);

  ChatUser get(Long chatId, Long userId);

  List<ChatUser> getAllByUserId(Long userId);

  List<ChatUser> getAllByChatId(Long chatId);

  void checkContainsChatWithIdUserWithId(Long chatId, Long userId);

  ChatUser update(ChatUser userChat);

  void delete(Long chatId, Long... userId);

}

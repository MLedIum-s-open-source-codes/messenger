package org.example.messenger.service;

import org.example.messenger.domain.model.Chat;

import java.util.List;

public interface ChatService {

  Chat getOrCreateChat(String userId, String interlocutorId);

  Chat getChat(String userId, String interlocutorId);

  List<Chat> getChatsByUserId(String userId);

  Chat update(Chat chat);

}

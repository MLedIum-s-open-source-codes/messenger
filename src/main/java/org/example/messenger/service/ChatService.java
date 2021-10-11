package org.example.messenger.service;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.request.CreateConversationRequest;

import java.util.List;

public interface ChatService {

  Chat getOrCreate(String userId, String interlocutorId);

  Chat getOrCreate(String userId);

  Chat createConversation(String creatorId, CreateConversationRequest createConversationRequest);

  Chat getById(String id);

  Chat getByUsersIds(String userId, String interlocutorId);

  Chat getByUsersIds(String userId);

  List<Chat> getAllByUserId(String userId);

  Chat update(Chat chat);

}

package org.example.messenger.service;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.request.CreateConversationRequest;

public interface ConversationChatService extends ChatService {

  Chat createConversation(String creatorId, CreateConversationRequest createConversationRequest);

  void addUserToConversation(String chatId, String userId, String addingUserId);

  void removeUserFromConversation(String chatId, String userId, String removableUserId);

}

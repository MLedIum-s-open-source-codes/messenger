package org.example.messenger.service;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.request.CreateConversationRequest;

import java.util.List;

public interface ChatService {

  Chat getById(String id);

  List<Chat> getAllByUserId(String userId);

  Chat update(Chat chat);

}

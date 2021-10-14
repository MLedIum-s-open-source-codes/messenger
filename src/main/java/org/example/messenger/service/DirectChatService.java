package org.example.messenger.service;

import org.example.messenger.domain.model.Chat;

public interface DirectChatService extends ChatService {

  Chat getOrCreate(String userId, String interlocutorId);

  Chat getOrCreate(String userId);

  Chat getByUsersIds(String userId, String interlocutorId);

  Chat getByUsersIds(String userId);

}

package org.example.messenger.service;

import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {

  Message sendToFavourites(String userId, MessageDto dto);

  Message sendToUser(String from, String to, MessageDto dto);

  Message sendToConversation(String from, Integer conversationSeqId, MessageDto dto);

  Message get(String id);

  List<Message> getAllByChatIdAndUserId(String chatId, String userId);

  Optional<Message> findLastByChatIdAndUserId(String chatId, String userId);

  Message update(Message message);

}

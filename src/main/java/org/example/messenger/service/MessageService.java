package org.example.messenger.service;

import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Message;

import java.util.List;

public interface MessageService {

  Message sendToFavourites(String userId, MessageDto dto);

  Message sendToUser(String from, String to, MessageDto dto);

  Message sendToConversation(String from, Integer conversationSeqId, MessageDto dto);

  Message get(String id);

  Message update(Message message);

}

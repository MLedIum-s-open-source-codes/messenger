package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.Message;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatService chatService;

  @Override
  public Message sendMessage(String userId, String interlocutorId, MessageDto dto) {
    Chat chat = chatService.getOrCreateChat(userId, interlocutorId);

    Integer newSeqId = chat.getLastSeqId() + 1;

    Message message = Message.builder()
        .senderId(userId)
        .text(dto.getText())
        .seqId(newSeqId)
        .build();

    message = update(message);

    chat.getMessages().add(message);
    chat.setLastSeqId(newSeqId);

    chatService.update(chat);

    return message;
  }

  @Override
  public Message update(Message message) {

    return messageRepository.save(message);
  }

}

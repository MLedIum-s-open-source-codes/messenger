package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.Message;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final UserService userService;
  private final ChatService chatService;

  @Override
  public Message sendMessage(String userId, String interlocutorId, MessageDto dto) {
    Chat chat = chatService.getOrCreateChat(userId, interlocutorId);

    Message message = Message.builder()
        .sender(userService.get(userId))
        .text(dto.getText())
        .build();

    message = update(message);

    chat.getMessages().add(message);

    chatService.update(chat);

    return message;
  }

  @Override
  public List<Message> getMessages(String userId, String interlocutorId) {
    Chat chat = chatService.getOrCreateChat(userId, interlocutorId);

    return chat.getMessages();
  }

  @Override
  public Message update(Message message) {

    return messageRepository.save(message);
  }

}

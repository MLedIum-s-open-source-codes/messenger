package org.example.messenger.service.impl.chat;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@Qualifier(value = "chatService")
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;

  @Autowired
  public ChatServiceImpl(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  @Override
  public Chat getById(String id) {
    Optional<Chat> optional = chatRepository.findById(id);
    if (optional.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Chat was not found");

    return optional.get();
  }

  @Override
  public List<Chat> getAllByUserId(String userId) {

    return chatRepository.findAllByUserId(userId);
  }

  @Override
  public Chat update(Chat chat) {

    return chatRepository.save(chat);
  }

}

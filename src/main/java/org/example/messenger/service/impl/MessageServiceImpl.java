package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.*;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatService chatService;
  private final UserService userService;

  @Override
  public Message get(String id) {
    Optional<Message> optional = messageRepository.findById(id);
    if (optional.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Message was not found");

    return optional.get();
  }

  @Override
  public Message send(String userId, String interlocutorId, MessageDto dto) {
    Chat chat = chatService.getOrCreateChat(userId, interlocutorId);
    User user = userService.get(userId);
    User interlocutor = userService.get(interlocutorId);

    Message repliedMessage = getRepliedMessage(dto, chat, user);
    List<Message> forwardedMessages = getForwardedMessages(dto, user);

    Integer newChatSeqId = chat.getLastSeqId() + 1;

    Message message = Message.builder()
        .senderId(userId)
        .text(dto.getText())
        .chatSeqId(newChatSeqId)
        .repliedMessage(repliedMessage)
        .forwardedMessages(forwardedMessages)
        .build();
    message = update(message);

    user.addMessage(message);
    userService.update(user);

    interlocutor.addMessage(message);
    userService.update(interlocutor);

    chat.getMessages().add(MessageRef.builder().msgId(message.getId()).seqId(newChatSeqId).build());
    chat.setLastSeqId(newChatSeqId);
    chatService.update(chat);

    message.setPersonalSequenceId(user.getLastMsgSeqId());

    return message;
  }

  private Message getRepliedMessage(MessageDto dto, Chat chat, User user) {
    if (dto.getRepliedMessage() == null)
        return null;

    Optional<MessageRef> mPSOptional = user.getMessagePersonalSequenceBySeqId(dto.getRepliedMessage().getId());
    if (mPSOptional.isEmpty())
        return null;

    Optional<Message> optional = messageRepository.findById(mPSOptional.get().getMsgId());
    if (optional.isEmpty())
        return null;

    if (!chat.containsMessageWithId(optional.get().getId())) {

      if (dto.getForwardedMessages() == null)
          dto.setForwardedMessages(new ArrayList<>());

      dto.getForwardedMessages().add(dto.getRepliedMessage());
      return null;
    }

    return optional.get();
  }

  private List<Message> getForwardedMessages(MessageDto dto, User user) {
    if (dto.getForwardedMessages() == null)
        return null;

    Set<Message> forwardedMessages = new HashSet<>();

    dto.getForwardedMessages().forEach(forwardedMessage -> {
      Optional<MessageRef> mPSOptional = user.getMessagePersonalSequenceBySeqId(forwardedMessage.getId());
      if (mPSOptional.isEmpty())
          return;

      Optional<Message> msgOptional = messageRepository.findById(mPSOptional.get().getMsgId());
      msgOptional.ifPresent(forwardedMessages::add);
    });

    return new ArrayList<>(forwardedMessages);
  }

  @Override
  public Message update(Message message) {

    return messageRepository.save(message);
  }

}

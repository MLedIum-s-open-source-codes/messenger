package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.model.MessagePersonalSequence;
import org.example.messenger.domain.model.User;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatService chatService;
  private final UserService userService;

  @Override
  public Message sendMessage(String userId, String interlocutorId, MessageDto dto) {
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
    interlocutor.addMessage(message);

    chat.getMessages().add(message);
    chat.setLastSeqId(newChatSeqId);

    userService.update(user);
    userService.update(interlocutor);
    chatService.update(chat);

    message.setPersonalSequenceId(user.getLastMsgSeqId());

    return message;
  }

  private Message getRepliedMessage(MessageDto dto, Chat chat, User user) {
    if (dto.getRepliedMessage() == null)
        return null;

    Optional<MessagePersonalSequence> mPSOptional = user.getMessagePersonalSequenceBySeqId(dto.getRepliedMessage().getId());
    if (mPSOptional.isEmpty())
        return null;

    Optional<Message> optional = messageRepository.findById(mPSOptional.get().getMsgId());
    if (optional.isEmpty())
        return null;

    if (!chat.getMessages().contains(optional.get())) {

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

    List<Message> forwardedMessages = new ArrayList<>();

    dto.getForwardedMessages().forEach(forwardedMessage -> {
      Optional<MessagePersonalSequence> mPSOptional = user.getMessagePersonalSequenceBySeqId(forwardedMessage.getId());
      if (mPSOptional.isEmpty())
          return;

      Optional<Message> msgOptional = messageRepository.findById(mPSOptional.get().getMsgId());
      msgOptional.ifPresent(forwardedMessages::add);
    });

    return forwardedMessages;
  }

  @Override
  public Message update(Message message) {

    return messageRepository.save(message);
  }

}

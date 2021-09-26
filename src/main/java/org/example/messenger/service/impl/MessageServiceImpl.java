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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatService chatService;
  private final UserService userService;

  @Override
  public Message sendMessage(String userId, String interlocutorId, MessageDto dto) { //TODO Shorten the code in the method
    Chat chat = chatService.getOrCreateChat(userId, interlocutorId);
    User user = userService.get(userId);
    User interlocutor = userService.get(interlocutorId);

    Integer newChatSeqId = chat.getLastSeqId() + 1;

    Optional<Message> repliedMessageOptional = Optional.empty();
    if (dto.getRepliedMessage() != null) {
      Optional<MessagePersonalSequence> mPSOptional = user.getMessagePersonalSequenceBySeqId(dto.getRepliedMessage().getId());
      if (mPSOptional.isPresent()) {
        repliedMessageOptional = messageRepository.findById(mPSOptional.get().getMsgId());
      }
    }

    Message message = Message.builder()
        .senderId(userId)
        .text(dto.getText())
        .chatSeqId(newChatSeqId)
        .repliedMessage(repliedMessageOptional.isEmpty() ? null : repliedMessageOptional.get())
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

  @Override
  public Message update(Message message) {

    return messageRepository.save(message);
  }

}

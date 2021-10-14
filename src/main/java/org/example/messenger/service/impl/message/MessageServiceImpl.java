package org.example.messenger.service.impl.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.*;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final DirectChatService chatService;
  private final UserService userService;
  private final MediaService mediaService;

  @Override
  public Message sendToFavourites(String userId, MessageDto dto) {
    Chat chat = chatService.getOrCreate(userId);
    User user = userService.get(userId);

    Message message = createMessage(dto, chat, user);

    user.addMessage(message);
    userService.update(user);

    addMessageToChat(message, chat);

    message.setPersonalSequenceId(user.getLastMsgSeqId());
    return message;
  }

  @Override
  public Message sendToUser(String userId, String interlocutorId, MessageDto dto) {
    Chat chat = chatService.getOrCreate(userId, interlocutorId);
    User user = userService.get(userId);
    User interlocutor = userService.get(interlocutorId);

    Message message = createMessage(dto, chat, user);

    user.addMessage(message);
    userService.update(user);

    interlocutor.addMessage(message);
    userService.update(interlocutor);

    addMessageToChat(message, chat);

    message.setPersonalSequenceId(user.getLastMsgSeqId());
    return message;
  }

  @Override
  public Message sendToConversation(String userId, Integer conversationSeqId, MessageDto dto) {
    User user = userService.get(userId);

    Optional<ObjectRef> conversationRef = user.getConversationPersonalSequenceBySeqId(conversationSeqId);
    if (conversationRef.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found");

    Chat chat = chatService.getById(conversationRef.get().getObjectId());

    Message message = createMessage(dto, chat, user);

    chat.getUsers().forEach(conversationChatUser -> {
      User conversationUser = userService.get(conversationChatUser.getUserId());
      conversationUser.addMessage(message);
      userService.update(conversationUser);
    });

    addMessageToChat(message, chat);

    // +1 is used because when adding a message to the sender,
    // we do not update it in the current method and the value of the last seqId remains obsolete
    message.setPersonalSequenceId(user.getLastMsgSeqId()+1);
    return message;
  }

  @Override
  public Message get(String id) {
    Optional<Message> optional = messageRepository.findById(id);
    if (optional.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Message was not found");

    return optional.get();
  }

  @Override
  public Message update(Message message) {

    return messageRepository.save(message);
  }

  private Message createMessage(MessageDto dto, Chat chat, User user) {
    Message repliedMessage = getRepliedMessage(dto, chat, user);
    List<Message> forwardedMessages = getForwardedMessages(dto, user);
    List<MediaFile> attachedFiles = getAttachedFiles(dto);

    Integer newChatSeqId = chat.getLastSeqId() + 1;

    return update(Message.builder()
        .senderId(user.getId())
        .text(dto.getText())
        .chatSeqId(newChatSeqId)
        .repliedMessage(repliedMessage)
        .forwardedMessages(forwardedMessages)
        .attachedFiles(attachedFiles)
        .build());
  }

  private Message getRepliedMessage(MessageDto dto, Chat chat, User user) {
    if (dto.getRepliedMessage() == null)
        return null;

    Optional<ObjectRef> mPSOptional = user.getMessagePersonalSequenceBySeqId(dto.getRepliedMessage().getId());
    if (mPSOptional.isEmpty())
        return null;

    Optional<Message> optional = messageRepository.findById(mPSOptional.get().getObjectId());
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
      Optional<ObjectRef> mPSOptional = user.getMessagePersonalSequenceBySeqId(forwardedMessage.getId());
      if (mPSOptional.isEmpty())
          return;

      Optional<Message> msgOptional = messageRepository.findById(mPSOptional.get().getObjectId());
      msgOptional.ifPresent(forwardedMessages::add);
    });

    return new ArrayList<>(forwardedMessages);
  }

  private List<MediaFile> getAttachedFiles(MessageDto dto) {
    if (dto.getAttachedFiles() == null)
        return null;

    Set<MediaFile> attachedFiles = new HashSet<>();
    dto.getAttachedFiles().forEach(attachedFile -> attachedFiles.add(mediaService.get(attachedFile.getId())));

    return new ArrayList<>(attachedFiles);
  }

  private void addMessageToChat(Message message, Chat chat) {
    chat.getMessages().add(ObjectRef.builder().objectId(message.getId()).seqId(message.getChatSeqId()).build());
    chat.setLastSeqId(message.getChatSeqId());
    chatService.update(chat);
  }

}
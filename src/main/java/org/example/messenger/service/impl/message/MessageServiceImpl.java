package org.example.messenger.service.impl.message;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.*;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.*;
import org.springframework.stereotype.Service;

import java.util.*;

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
    chatService.update(chat);

    user.addMessage(message);
    userService.update(user);

    return update(message);
  }

  @Override
  public Message sendToUser(String userId, String interlocutorId, MessageDto dto) {
    Chat chat = chatService.getOrCreate(userId, interlocutorId);
    User user = userService.get(userId);
    User interlocutor = userService.get(interlocutorId);

    Message message = createMessage(dto, chat, user);
    chatService.update(chat);

    user.addMessage(message);
    userService.update(user);

    interlocutor.addMessage(message);
    userService.update(interlocutor);

    return update(message);
  }

  @Override
  public Message sendToConversation(String userId, Integer conversationSeqId, MessageDto dto) {
    User user = userService.get(userId);

    Optional<ObjectRef> conversationRef = user.getConversationPersonalSequenceBySeqId(conversationSeqId);
    if (conversationRef.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found");

    Chat chat = chatService.getById(conversationRef.get().getObjectId());

    Message message = createMessage(dto, chat, user);
    chatService.update(chat);

    chat.getUsers().forEach(conversationChatUser -> {
      User conversationUser = userService.get(conversationChatUser.getUserId());
      conversationUser.addMessage(message);
      userService.update(conversationUser);
    });

    return update(message);
  }

  @Override
  public Message get(String id) {
    Optional<Message> optional = messageRepository.findById(id);
    if (optional.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Message was not found");

    return optional.get();
  }

  @Override
  public List<Message> getAllByChatIdAndUserId(String chatId, String userId) {

    return messageRepository.findAllByChatIdAndUserId(chatId, userId);
  }

  @Override
  public Optional<Message> findLastByChatIdAndUserId(String chatId, String userId) {
    Chat chat = chatService.getById(chatId);

    return findByChatIdAndUserIdAndChatSeqId(chatId, userId, chat.getLastSeqId());
  }

  private Optional<Message> findByChatIdAndUserIdAndChatSeqId(String chatId, String userId, Integer seqId) {

    return messageRepository.findByChatIdAndUserIdAndChatSeqId(chatId, userId, seqId);
  }

  private Optional<Message> findByUserIdAndPersonalSeqId(String userId, Integer seqId) {

    return messageRepository.findByUserIdAndPersonalSeqId(userId, seqId);
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
    chat.setLastSeqId(newChatSeqId);

    return update(Message.builder()
        .chatId(chat.getId())
        .chatSeqId(newChatSeqId)
        .senderId(user.getId())
        .text(dto.getText())
        .repliedMessage(repliedMessage)
        .forwardedMessages(forwardedMessages)
        .attachedFiles(attachedFiles)
        .build());
  }

  private Message getRepliedMessage(MessageDto dto, Chat chat, User user) {
    if (dto.getRepliedMessage() == null)
        return null;

    Optional<Message> repliedMessageOptional = findByUserIdAndPersonalSeqId(user.getId(), dto.getRepliedMessage().getId());
    if (repliedMessageOptional.isEmpty() || !repliedMessageOptional.get().getChatId().equals(chat.getId()))
        return null;

    return repliedMessageOptional.get();
  }

  private List<Message> getForwardedMessages(MessageDto dto, User user) {
    if (dto.getForwardedMessages() == null)
        return null;

    Set<Message> forwardedMessages = new HashSet<>();

    dto.getForwardedMessages().forEach(forwardedMessageDto -> {
      Optional<Message> forwardedMessageOptional = findByUserIdAndPersonalSeqId(user.getId(), forwardedMessageDto.getId());
      forwardedMessageOptional.ifPresent(forwardedMessages::add);
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

}

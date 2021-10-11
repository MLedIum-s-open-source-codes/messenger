package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.MediaFileDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.*;
import org.example.messenger.domain.request.CreateConversationRequest;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.domain.response.DialogsPreload;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/messenger")
@RequiredArgsConstructor
public class ChatRestController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final UserService userService;

  @PostMapping
  public ResponseEntity<ChatHistory> createConversation(
      @RequestBody CreateConversationRequest createConversationRequest,
      @UserId String userId
  ) {

    Chat chat = chatService.createConversation(userId, createConversationRequest);

    List<UserDto> users = chat.getUsers().stream().map(
        chatUser -> UserDto.of(userService.get(chatUser.getUserId()))
    ).collect(Collectors.toList());
    MediaFileDto photo = chat.getPhoto() == null ? null : MediaFileDto.of(chat.getPhoto());

    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .photo(photo)
        .usersData(users)
        .chatType(ChatTypeEnum.CONVERSATION)
        .messages(null)
        .build());
  }

  @GetMapping
  public ResponseEntity<DialogsPreload> getChatsPreviews(
      @UserId String userId
  ) {
    User user = userService.get(userId);
    List<Chat> chatsModels = chatService.getAllByUserId(userId);

    List<ChatDto> chats = new ArrayList<>();
    List<UserDto> users = getUploadedUsersIds(chatsModels, userId).stream().map(
        uploadedUserId -> UserDto.of(userService.get(uploadedUserId))
    ).collect(Collectors.toList());

    if (!chatsModels.isEmpty()) {
      chatsModels.forEach(chatModel -> {
        Optional<ObjectRef> lastMessageRef = chatModel.getLastMessageRef();
        lastMessageRef.ifPresent(messageRef -> {
          Message message = messageService.get(messageRef.getObjectId());
          Optional<ChatUser> interlocutor = chatModel.getInterlocutor(userId);
          chats.add(ChatDto.builder()
              .id(interlocutor.isPresent() ? interlocutor.get().getUserId() : userId)
              .lastMessage(MessageDto.of(message, user))
              .chatType(chatModel.getType())
              .build()
          );
        });
      });
    }

    return ResponseEntity.ok(DialogsPreload.builder()
        .me(UserDto.of(user))
        .chatsPreload(chats)
        .usersData(users)
        .build()
    );
  }

  @GetMapping("/{interlocutorId}")
  public ResponseEntity<ChatHistory> getChatHistoryWith(
      @PathVariable String interlocutorId,
      @UserId String userId
  ) {
    Chat chatModel = userId.equals(interlocutorId) ? chatService.getByUsersIds(userId) : chatService.getByUsersIds(userId, interlocutorId);
    User user = userService.get(userId);

    List<MessageDto> messages = null;
    List<UserDto> users = new ArrayList<>();

    if (chatModel != null) {
      messages = chatModel.getMessages() == null ? null : chatModel.getMessages().stream().map(
          messageRef -> MessageDto.of(messageService.get(messageRef.getObjectId()), user)
      ).collect(Collectors.toList());

      users = getUploadedUsersIds(chatModel, userId, false).stream().map(
          uploadedUserId -> UserDto.of(userService.get(uploadedUserId))
      ).collect(Collectors.toList());
    }

    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .chatType(ChatTypeEnum.DIRECT_MESSAGE)
        .usersData(users)
        .messages(messages)
        .build());
  }

  @GetMapping("/c{conversationSeqId}")
  public ResponseEntity<ChatHistory> getConversationHistory(
      @PathVariable Integer conversationSeqId,
      @UserId String userId
  ) {
    User user = userService.get(userId);

    Optional<ObjectRef> ref = user.getConversationPersonalSequenceBySeqId(conversationSeqId);
    if (ref.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found");

    Chat chatModel = chatService.getById(ref.get().getObjectId());

    List<UserDto> users = getUploadedUsersIds(chatModel, userId, false).stream().map(
        usrId -> UserDto.of(userService.get(usrId))
    ).collect(Collectors.toList());

    List<MessageDto> messages = chatModel.getMessages() == null ? null : chatModel.getMessages().stream().map(
        messageRef -> MessageDto.of(messageService.get(messageRef.getObjectId()), user)
    ).collect(Collectors.toList());

    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .photo(chatModel.getPhoto() == null ? null : MediaFileDto.of(chatModel.getPhoto()))
        .chatType(ChatTypeEnum.CONVERSATION)
        .usersData(users)
        .messages(messages)
        .build());
  }

  private Set<String> getUploadedUsersIds(List<Chat> chats, String userId) {
    Set<String> uploadedUserId = new HashSet<>();

    if (!chats.isEmpty()) {
      chats.forEach(chat -> {
        uploadedUserId.addAll(getUploadedUsersIds(chat, userId, true));
      });
    }

    return uploadedUserId;
  }

  private Set<String> getUploadedUsersIds(Chat chat, String userId, boolean isPreview) { // FIXME optimize
    Set<String> uploadedUserId = new HashSet<>();

    if (chat != null) {
      Optional<ChatUser> interlocutor = chat.getInterlocutor(userId);
      interlocutor.ifPresent(chatUser -> uploadedUserId.add(chatUser.getUserId()));
      if (!isPreview) {
        chat.getMessages().forEach(messageRef ->
            uploadedUserId.addAll(messageService.get(messageRef.getObjectId()).getForwardedMessagesSendersIds())
        );
      }
    }

    return uploadedUserId;
  }

}

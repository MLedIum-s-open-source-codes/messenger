package org.example.messenger.controller.chat;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.MediaFileDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.domain.model.ObjectRef;
import org.example.messenger.domain.model.User;
import org.example.messenger.domain.request.CreateConversationRequest;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/messenger")
@RequiredArgsConstructor
public class ConversationChatRestController {

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

  @GetMapping("/c{conversationSeqId}")
  public ResponseEntity<ChatHistory> getChatHistory(
      @PathVariable Integer conversationSeqId,
      @UserId String userId
  ) {
    User user = userService.get(userId);

    Optional<ObjectRef> ref = user.getConversationPersonalSequenceBySeqId(conversationSeqId);
    if (ref.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found");

    Chat chatModel = chatService.getById(ref.get().getObjectId());

    List<UserDto> users = getUploadedUsersIds(chatModel, userId).stream().map(
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

  private Set<String> getUploadedUsersIds(Chat chat, String userId) {
    Set<String> uploadedUsersIds = new HashSet<>();

    if (chat != null) {
      Optional<ChatUser> interlocutor = chat.getInterlocutor(userId);
      interlocutor.ifPresent(chatUser -> uploadedUsersIds.add(chatUser.getUserId()));
      chat.getMessages().forEach(messageRef ->
          uploadedUsersIds.addAll(messageService.get(messageRef.getObjectId()).getForwardedMessagesSendersIds())
      );
    }

    return uploadedUsersIds;
  }

}

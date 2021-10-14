package org.example.messenger.controller.chat;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.MediaFileDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ObjectRef;
import org.example.messenger.domain.model.User;
import org.example.messenger.domain.request.CreateConversationRequest;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.service.ConversationChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.http.HttpStatus;
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

  private final ConversationChatService chatService;
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

    List<MessageDto> messages = messageService.getAllByChatIdAndUserId(chatModel.getId(), userId)
        .stream().map(message -> MessageDto.of(message, userId)).filter(msg -> msg.getId() != null).collect(Collectors.toList());

    List<UserDto> users = getUploadedUsersIds(chatModel, messages).stream().map(
        usrId -> UserDto.of(userService.get(usrId))
    ).collect(Collectors.toList());

    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .photo(chatModel.getPhoto() == null ? null : MediaFileDto.of(chatModel.getPhoto()))
        .chatType(ChatTypeEnum.CONVERSATION)
        .usersData(users)
        .messages(messages)
        .build());
  }

  @PostMapping("/c{conversationSeqId}/users")
  public ResponseEntity<UserDto> addChatUser(
      @PathVariable Integer conversationSeqId,
      @RequestBody UserDto addingUserDto,
      @UserId String userId
  ) {
    User user = userService.get(userId);
    User addingUser = userService.get(addingUserDto.getId());

    Optional<ObjectRef> ref = user.getConversationPersonalSequenceBySeqId(conversationSeqId);
    if (ref.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found");

    chatService.addUserToConversation(ref.get().getObjectId(), userId, addingUserDto.getId());
    addingUser.addConversation(chatService.getById(ref.get().getObjectId()));
    userService.update(addingUser);

    return ResponseEntity.ok(UserDto.of(addingUser));
  }

  @DeleteMapping("/c{conversationSeqId}/users")
  public ResponseEntity<HttpStatus> removeChatUser(
      @PathVariable Integer conversationSeqId,
      @RequestBody String removableUserId,
      @UserId String userId
  ) {
    User user = userService.get(userId);
    User removableUser = userService.get(removableUserId);

    Optional<ObjectRef> ref = user.getConversationPersonalSequenceBySeqId(conversationSeqId);
    String convId = ref.orElseThrow(() -> new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found")).getObjectId();

    chatService.removeUserFromConversation(convId, userId, removableUserId);
    removableUser.removeConversation(chatService.getById(convId));
    userService.update(removableUser);

    return ResponseEntity.ok().build();
  }

  private Set<String> getUploadedUsersIds(Chat chat, List<MessageDto> messages) {
    Set<String> uploadedUsersIds = new HashSet<>();

    if (chat != null)
        getUploadedUsersIds(chat, uploadedUsersIds);
    if (messages != null)
        getUploadedUsersIds(messages, uploadedUsersIds);

    return uploadedUsersIds;
  }

  private Set<String> getUploadedUsersIds(Chat chat, Set<String> uploadedUsersIds) {
    if (chat != null)
        chat.getUsers().forEach(chatUser -> uploadedUsersIds.add(chatUser.getUserId()));

    return uploadedUsersIds;
  }

  private Set<String> getUploadedUsersIds(List<MessageDto> messages, Set<String> uploadedUsersIds) {
    if (messages != null)
        messages.forEach(message -> {
            uploadedUsersIds.add(message.getSenderId());
          if (message.getRepliedMessage() != null)
              getUploadedUsersIds(List.of(message.getRepliedMessage()), uploadedUsersIds);
          if (message.getForwardedMessages() != null)
              getUploadedUsersIds(message.getForwardedMessages(), uploadedUsersIds);
        });

    return uploadedUsersIds;
  }

}

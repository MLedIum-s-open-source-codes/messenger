package org.example.messenger.controller.chat;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.domain.model.User;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/messenger")
@RequiredArgsConstructor
public class DirectChatRestController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final UserService userService;

  @GetMapping("/{interlocutorId}")
  public ResponseEntity<ChatHistory> getChatHistory(
      @PathVariable String interlocutorId,
      @UserId String userId
  ) {
    Chat chatModel = userId.equals(interlocutorId) ? chatService.getByUsersIds(userId) : chatService.getByUsersIds(userId, interlocutorId);
    User user = userService.get(userId);

    List<MessageDto> messages = new ArrayList<>();
    List<UserDto> users = new ArrayList<>();

    if (chatModel != null) {
      messages = chatModel.getMessages() == null ? null : chatModel.getMessages().stream().map(
          messageRef -> MessageDto.of(messageService.get(messageRef.getObjectId()), user)
      ).collect(Collectors.toList());

      users = getUploadedUsersIds(chatModel, userId).stream().map(
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

  private Set<String> getUploadedUsersIds(Chat chat, String userId) {
    Set<String> uploadedUsersIds = new HashSet<>();


    Optional<ChatUser> interlocutor = chat.getInterlocutor(userId);
    interlocutor.ifPresent(chatUser -> uploadedUsersIds.add(chatUser.getUserId()));
    chat.getMessages().forEach(messageRef ->
        uploadedUsersIds.addAll(messageService.get(messageRef.getObjectId()).getForwardedMessagesSendersIds())
    );

    return uploadedUsersIds;
  }

}

package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.model.User;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.domain.response.DialogsPreload;
import org.example.messenger.service.ChatService;
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
  private final UserService userService;

  @GetMapping
  public ResponseEntity<DialogsPreload> getChatsPreviews(
      @UserId String userId
  ) {

    List<Chat> chatsModels = chatService.getChatsByUserId(userId);

    List<ChatDto> chats = new ArrayList<>();
    List<UserDto> users = getUsersData(chatsModels, userId, true);
    User user = userService.get(userId);

    if (!chatsModels.isEmpty()) {
      chatsModels.forEach(chatModel -> {
        Optional<Message> lastMessage = chatModel.getLastMessage();
        lastMessage.ifPresent(message -> chats.add( //TODO Checking is proposed for deletion
            ChatDto.builder()
                .id(chatModel.getInterlocutor(userId).getUserId())
                .lastMessage(MessageDto.of(message, user))
                .build()
        ));
      });
    }

    return ResponseEntity.ok(DialogsPreload.builder()
        .me(UserDto.of(user))
        .chatsPreload(chats)
        .usersData(users)
        .build()
    );
  }

  private List<UserDto> getUsersData(List<Chat> chatsModels, String userId, boolean isPreview) {
    Set<UserDto> users = new HashSet<>();

    if (!chatsModels.isEmpty()) {
      chatsModels.forEach(chatModel -> {
        users.add(UserDto.of(
            userService.get(chatModel.getInterlocutor(userId).getUserId())
        ));
        if (!isPreview) {
          chatModel.getMessages().forEach(message -> {
            users.addAll(
                message.getForwardedMessagesSendersIds().stream().map(
                    senderId -> UserDto.of(userService.get(senderId))
                ).collect(Collectors.toSet())
            );
          });
        }
      });
    }

    return new ArrayList<>(users);
  }

  @GetMapping("/{interlocutorId}")
  public ResponseEntity<ChatHistory> getChatHistoryWith(
      @PathVariable String interlocutorId,
      @UserId String userId
  ) {
    Chat chatModel = chatService.getOrCreateChat(userId, interlocutorId);
    User user = userService.get(userId);

    List<MessageDto> messages = chatModel.getMessages() == null ? null
        : chatModel.getMessages().stream().map(
            message -> MessageDto.of(message, user)
    ).collect(Collectors.toList());

    List<UserDto> users = getUsersData(List.of(chatModel), userId, false);

    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .usersData(users)
        .messages(messages)
        .build());
  }

}

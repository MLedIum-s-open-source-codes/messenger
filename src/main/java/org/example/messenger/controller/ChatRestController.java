package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.domain.response.DialogsPreload;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    if (!chatsModels.isEmpty()) {
      chatsModels.forEach(chatModel -> {
        Optional<Message> lastMessage = chatModel.getLastMessage();
        chats.add(ChatDto.builder()
            .id(chatModel.getInterlocutor(userId).getUserId())
            .lastMessage(lastMessage.isEmpty() ? null : MessageDto.of(lastMessage.get()))
            .build()
        );
      });
    }

    return ResponseEntity.ok(DialogsPreload.builder()
        .me(UserDto.of(userService.get(userId)))
        .chatsPreload(chats)
        .usersData(users)
        .build()
    );
  }

  private List<UserDto> getUsersData(List<Chat> chatsModels, String userId, boolean isPreview) {
    List<UserDto> users = new ArrayList<>();

    if (!chatsModels.isEmpty()) {
      chatsModels.forEach(chatModel -> {
        users.add(UserDto.of(
            userService.get(chatModel.getInterlocutor(userId).getUserId())
        ));
        //if (!isPreview) {
        //  chatModel.getMessages();
        //}
      });
    }

    return users;
  }

  @GetMapping("/{interlocutorId}")
  public ResponseEntity<ChatHistory> getChatHistoryWith(
      @PathVariable String interlocutorId,
      @UserId String userId
  ) {
    Chat chatModel = chatService.getOrCreateChat(userId, interlocutorId);

    List<MessageDto> messages = chatModel.getMessages() == null ? null : chatModel.getMessages().stream().map(MessageDto::of).collect(Collectors.toList());
    List<UserDto> users = getUsersData(Arrays.asList(chatModel), userId, false);

    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .usersData(users)
        .messages(messages)
        .build());
  }

}

package org.example.messenger.controller.chat;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.*;
import org.example.messenger.domain.response.DialogsPreload;
import org.example.messenger.enumeration.ChatTypeEnum;
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
public class BaseChatRestController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final UserService userService;

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
        Optional<Message> lastMessage = messageService.findLastByChatIdAndUserId(chatModel.getId(), userId);

        if (lastMessage.isPresent() || chatModel.getType() != ChatTypeEnum.DIRECT_MESSAGE) {
          Message message = lastMessage.orElse(null);
          Optional<ChatUser> interlocutor = chatModel.getInterlocutor(userId);
          chats.add(ChatDto.builder()
              .id(interlocutor.isPresent() ? interlocutor.get().getUserId() : userId)
              .lastMessage(message == null ? null : MessageDto.of(message, userId))
              .chatType(chatModel.getType())
              .build()
          );
        }
      });
    }

    return ResponseEntity.ok(DialogsPreload.builder()
        .me(UserDto.of(user))
        .chatsPreload(chats)
        .usersData(users)
        .build()
    );
  }

  private Set<String> getUploadedUsersIds(List<Chat> chats, String userId) {
    Set<String> uploadedUsersIds = new HashSet<>();

    if (!chats.isEmpty()) {
      chats.forEach(chat -> {
        if (chat != null) {
          Optional<ChatUser> interlocutor = chat.getInterlocutor(userId);
          interlocutor.ifPresent(chatUser -> uploadedUsersIds.add(chatUser.getUserId()));
        }
      });
    }

    return uploadedUsersIds;
  }

}

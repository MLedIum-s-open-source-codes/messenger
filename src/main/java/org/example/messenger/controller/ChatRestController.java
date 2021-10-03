package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.model.*;
import org.example.messenger.domain.response.ChatHistory;
import org.example.messenger.domain.response.DialogsPreload;
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

  @GetMapping
  public ResponseEntity<DialogsPreload> getChatsPreviews(
      @UserId String userId
  ) {
    List<Chat> chatsModels = chatService.getChatsByUserId(userId);

    List<ChatDto> chats = new ArrayList<>();
    List<UserDto> users = getUploadedUsersIds(chatsModels, userId).stream().map(uploadedUserId -> UserDto.of(userService.get(uploadedUserId))).collect(Collectors.toList());
    User user = userService.get(userId);

    if (!chatsModels.isEmpty()) {
      chatsModels.forEach(chatModel -> {
        Optional<MessageRef> lastMessageRef = chatModel.getLastMessageRef();
        lastMessageRef.ifPresent(messageRef -> {
          Message message = messageService.get(messageRef.getMsgId());
          Optional<ChatUser> interlocutor = chatModel.getInterlocutor(userId);
          chats.add(ChatDto.builder()
              .id(interlocutor.isPresent() ? interlocutor.get().getUserId() : userId)
              .lastMessage(MessageDto.of(message, user))
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
        if (!isPreview) { // FIXME optimize
          chat.getMessages().forEach(messageRef ->
              uploadedUserId.addAll(messageService.get(messageRef.getMsgId()).getForwardedMessagesSendersIds())
          );
        }
    }

    return uploadedUserId;
  }

  @GetMapping("/{interlocutorId}")
  public ResponseEntity<ChatHistory> getChatHistoryWith(
      @PathVariable String interlocutorId,
      @UserId String userId
  ) {
    Chat chatModel = chatService.getChat(userId, interlocutorId);
    User user = userService.get(userId);
    User interlocutor = userService.get(interlocutorId);

    List<MessageDto> messages = null;
    List<UserDto> users = new ArrayList<>(List.of(UserDto.of(interlocutor)));

    if (chatModel != null) {
      if (chatModel.getMessages() != null) { // FIXME optimize
        //chatModel.setMessages(chatModel.getMessages().stream().sorted((o1, o2) -> o2.getSeqId() - o1.getSeqId()).limit(30).collect(Collectors.toList()));
        messages = chatModel.getMessages().stream().map(
            messageRef -> MessageDto.of(messageService.get(messageRef.getMsgId()), user)
        ).collect(Collectors.toList());
      }

      users = getUploadedUsersIds(chatModel, userId, false).stream().map(usrId -> UserDto.of(userService.get(usrId))).collect(Collectors.toList());
    }


    return ResponseEntity.ok(ChatHistory.builder()
        .me(UserDto.of(userService.get(userId)))
        .usersData(users)
        .messages(messages)
        .build());
  }

}

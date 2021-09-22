package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.ChatsDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/messenger")
@RequiredArgsConstructor
public class ChatRestController {

  private final ChatService chatService;

  @GetMapping
  public ResponseEntity<ChatsDto> getChatsPreviews(
      @UserId String userId
  ) {

    List<Chat> chats = chatService.getChatsByUserId(userId);
    chats.forEach(chat -> {
      chat.setUsers(chat.getUsers().stream().filter(strUser -> !strUser.getId().equals(userId)).collect(Collectors.toList()));
    });

    return ResponseEntity.ok(new ChatsDto(chats, true));
  }

  @GetMapping("/{interlocutorId}")
  public ResponseEntity<ChatDto> getChatWith(
      @PathVariable String interlocutorId,
      @UserId String userId
  ) {
    Chat chat = chatService.getOrCreateChat(userId, interlocutorId);

    chat.setUsers(chat.getUsers().stream().filter(strUser -> strUser.getId().equals(interlocutorId)).collect(Collectors.toList()));


    return ResponseEntity.ok(ChatDto.of(chat));
  }

}

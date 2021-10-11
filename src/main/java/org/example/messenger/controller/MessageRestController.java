package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Message;
import org.example.messenger.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/messenger/")
@RequiredArgsConstructor
public class MessageRestController {

  private final MessageService messageService;

  @PostMapping("{interlocutorId}/messages")
  public ResponseEntity<MessageDto> sendMessageToUser(
      @PathVariable String interlocutorId,
      @RequestBody MessageDto dto,
      @UserId String userId
  ) {

    Message message = userId.equals(interlocutorId)
        ? messageService.sendToFavourites(userId, dto)
        : messageService.sendToUser(userId, interlocutorId, dto);

    return ResponseEntity.ok(MessageDto.of(message));
  }

  @PostMapping("c{conversationSeqId}/messages")
  public ResponseEntity<MessageDto> sendMessageToConversation(
      @PathVariable Integer conversationSeqId,
      @RequestBody MessageDto dto,
      @UserId String userId
  ) {

    return ResponseEntity.ok(MessageDto.of(
        messageService.sendToConversation(userId, conversationSeqId, dto)
    ));
  }

}

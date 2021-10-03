package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/messenger/{interlocutorId}/messages")
@RequiredArgsConstructor
public class MessageRestController {

  private final MessageService messageService;

  @PostMapping
  public ResponseEntity<MessageDto> sendMessageToUser(
      @PathVariable String interlocutorId,
      @RequestBody MessageDto dto,
      @UserId String userId
  ) {

    return ResponseEntity.ok(MessageDto.of(
        messageService.send(userId, interlocutorId, dto)
    ));
  }

}

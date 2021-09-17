package org.example.messenger.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.messenger.entity.Chat;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ChatsDto {

  private List<ChatDto> chats;

  public ChatsDto(List<Chat> chats) {
    this.chats = chats == null ? null : chats.stream().map(ChatDto::of).collect(Collectors.toList());
  }

}

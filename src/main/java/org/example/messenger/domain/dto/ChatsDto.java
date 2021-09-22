package org.example.messenger.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.messenger.domain.model.Chat;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ChatsDto {

  List<ChatDto> chats;

  public ChatsDto(List<Chat> chats) {
    this(chats, false);
  }

  public ChatsDto(List<Chat> chats, boolean isPreviews) {
    if (isPreviews) {
      this.chats = chats == null ? null
          : chats.stream().map(ChatDto::previewOf).collect(Collectors.toList());
    } else {
      this.chats = chats == null ? null
          : chats.stream().map(ChatDto::of).collect(Collectors.toList());
    }
  }

}

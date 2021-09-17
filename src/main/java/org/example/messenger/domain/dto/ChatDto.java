package org.example.messenger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.messenger.entity.Chat;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

  private Long id;
  private List<UserDto> users;
  private List<MessageDto> messages;

  public static ChatDto of(Chat chat) {
    return ChatDto.builder()
        .id(chat.getId())
        .users(chat.getChatUsers().stream().map(chatUser -> UserDto.of(chatUser.getUser())).collect(Collectors.toList()))
        .messages(chat.getMessages().stream().map(message -> MessageDto.of(message)).collect(Collectors.toList()))
        .build();
  }
}

package org.example.messenger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

  private String id;

  private MessageDto lastMessage;

  public static ChatDto of(Chat chat) {
    return ChatDto.builder()
        .id(null)
        .lastMessage(chat.getMessages() == null ? null : chat.getMessages().stream().sorted().limit(1).map(MessageDto::of).collect(Collectors.toList()).get(0))
        .build();
  }

}

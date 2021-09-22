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

  private String title;

  private List<MessageDto> messages;

  public static ChatDto of(Chat chat) {
    User interlocutor = chat.getUsers().stream().findFirst().get();
    return ChatDto.builder()
        .id(interlocutor.getId())
        .title(interlocutor.getPublicName())
        .messages(chat.getMessages() == null ? null : chat.getMessages().stream().map(MessageDto::of).collect(Collectors.toList()))
        .build();
  }

  public static ChatDto previewOf(Chat chat) {
    User interlocutor = chat.getUsers().stream().findFirst().get();
    return ChatDto.builder()
        .id(interlocutor.getId())
        .title(interlocutor.getPublicName())
        .messages(chat.getMessages() == null ? null : chat.getMessages().stream().limit(1).map(MessageDto::of).collect(Collectors.toList()))
        .build();
  }

}

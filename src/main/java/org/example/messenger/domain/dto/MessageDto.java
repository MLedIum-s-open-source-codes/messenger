package org.example.messenger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.messenger.entity.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

  private Long id;
  private String text;
  private Long senderId;
  private Long chatId;

  public static MessageDto of(Message message) {
    return MessageDto.builder()
        .id(message.getId())
        .text(message.getMessageText().getText())
        .senderId(message.getSender().getId())
        .chatId(message.getChat().getId())
        .build();
  }

}

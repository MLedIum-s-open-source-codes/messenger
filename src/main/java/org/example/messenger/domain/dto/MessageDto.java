package org.example.messenger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.messenger.domain.model.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

  private String id;

  private String text;

  private Integer recId;

  private UserDto sender;

  public static MessageDto of(Message message) {
    return MessageDto.builder()
        .id(message.getId())
        .text(message.getText())
        .recId(message.getRecId())
        .sender(UserDto.of(message.getSender()))
        .build();
  }

  public static MessageDto previewOf(Message message) {
    return MessageDto.builder()
        .id(message.getId())
        .text(message.getText())
        .recId(message.getRecId())
        .sender(UserDto.of(message.getSender()))
        .build();
  }

}

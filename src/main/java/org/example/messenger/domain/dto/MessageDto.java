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

  private Integer seqId;

  private String text;

  private String senderId;

  public static MessageDto of(Message message) {
    return MessageDto.builder()
        .id(message.getId())
        .text(message.getText())
        .seqId(message.getSeqId())
        .senderId(message.getSenderId())
        .build();
  }

}

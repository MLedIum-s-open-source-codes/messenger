package org.example.messenger.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.messenger.entity.Message;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class MessagesDto {

  private List<MessageDto> messages;

  public MessagesDto(List<Message> messages) {
    this.messages = messages == null ? null : messages.stream().map(MessageDto::of).collect(Collectors.toList());
  }

}

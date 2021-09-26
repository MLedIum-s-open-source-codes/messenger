package org.example.messenger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.model.MessagePersonalSequence;
import org.example.messenger.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

  private Integer id;

  private String text;

  private String senderId;

  private MessageDto repliedMessage;

  private List<MessageDto> forwardedMessages;

  public static MessageDto of(Message message, User user) {
    Optional<MessagePersonalSequence> MPS = user.getMessagePersonalSequenceByMsgId(message.getId());
    if (MPS.isPresent())
      message.setPersonalSequenceId(MPS.get().getSeqId());

    return of(message);
  }

  public static MessageDto of(Message message) {
    return MessageDto.builder()
        .id(message.getPersonalSequenceId() == null ? null : message.getPersonalSequenceId())
        .text(message.getText())
        .senderId(message.getSenderId())
        .repliedMessage(message.getRepliedMessage() == null ? null : MessageDto.of(message.getRepliedMessage()))
        .forwardedMessages(
           message.getForwardedMessages() == null ? null : message.getForwardedMessages().stream().map(MessageDto::of).collect(Collectors.toList())
        )
        .build();
  }

}

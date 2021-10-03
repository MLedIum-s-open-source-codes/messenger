package org.example.messenger.domain.dto;

import lombok.*;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.model.MessageRef;
import org.example.messenger.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class MessageDto {

  private Integer id;

  private String text;

  private String senderId;

  private MessageDto repliedMessage;

  private List<MessageDto> forwardedMessages;

  public static MessageDto of(Message message, User user) {
    Optional<MessageRef> MPS = user.getMessagePersonalSequenceByMsgId(message.getId());
    MPS.ifPresent(
        messagePersonalSequence -> message.setPersonalSequenceId(messagePersonalSequence.getSeqId())
    );

    Message repliedMessage = message.getRepliedMessage();
    if (repliedMessage != null) {
      MPS = user.getMessagePersonalSequenceByMsgId(repliedMessage.getId());
      MPS.ifPresent(
          messagePersonalSequence -> repliedMessage.setPersonalSequenceId(messagePersonalSequence.getSeqId())
      );
    }

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

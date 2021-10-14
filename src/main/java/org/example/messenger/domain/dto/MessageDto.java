package org.example.messenger.domain.dto;

import lombok.*;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.model.ObjectRef;
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

  private List<MediaFileDto> attachedFiles;

  public static MessageDto of(Message message, String userId) {
    Optional<ObjectRef> optional = message.getPersonalSeqIdByUserId(userId);
    Integer personalSequenceId = optional.map(ObjectRef::getSeqId).orElse(null);

    return MessageDto.builder()
        .id(personalSequenceId)
        .text(message.getText())
        .senderId(message.getSenderId())
        .repliedMessage(message.getRepliedMessage() == null ? null :
            MessageDto.of(message.getRepliedMessage(), userId)
        )
        .forwardedMessages(message.getForwardedMessages() == null ? null :
            message.getForwardedMessages().stream().map(forwardedMessage -> MessageDto.of(forwardedMessage, userId)).collect(Collectors.toList())
        )
        .attachedFiles(message.getAttachedFiles() == null ? null :
            message.getAttachedFiles().stream().map(MediaFileDto::of).collect(Collectors.toList())
        )
        .build();
  }

}

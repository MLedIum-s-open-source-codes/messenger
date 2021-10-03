package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Message extends BaseModel {

  @Id
  private String id;

  @Transient
  private Integer personalSequenceId;

  private Integer chatSeqId;

  private String text;

  private String senderId;

  @DBRef
  private Message repliedMessage;

  @DBRef
  @Builder.Default
  private List<Message> forwardedMessages = new ArrayList<>();

  public Set<String> getForwardedMessagesSendersIds() {
    Set<String> sendersIds = new HashSet<>();

    return getForwardedMessagesSendersIds(sendersIds);
  }

  public Set<String> getForwardedMessagesSendersIds(Set<String> sendersIds) {
    sendersIds.add(senderId);
    if (forwardedMessages != null) {
      forwardedMessages.forEach(forwardedMessage -> {
        forwardedMessage.getForwardedMessagesSendersIds(sendersIds);
      });
    }

    return sendersIds;
  }

}

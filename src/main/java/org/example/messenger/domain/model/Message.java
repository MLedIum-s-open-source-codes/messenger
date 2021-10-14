package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Message extends BaseModel {

  @Id
  private String id;

  private String chatId;

  private Integer chatSeqId;

  private String senderId;

  private String text;

  @DBRef
  private Message repliedMessage;

  @DBRef
  @Builder.Default
  private List<Message> forwardedMessages = new ArrayList<>();

  @DBRef
  @Builder.Default
  private List<MediaFile> attachedFiles = new ArrayList<>();

  @Builder.Default
  private List<ObjectRef> usersPersonalSequenceIds = new ArrayList<>();

  public Optional<ObjectRef> getPersonalSeqIdByUserId(String userId) {

    return usersPersonalSequenceIds.stream().filter(objectRef -> objectRef.getObjectId().equals(userId)).findFirst();
  }

}

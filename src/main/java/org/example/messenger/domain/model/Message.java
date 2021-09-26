package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@EqualsAndHashCode(of = {"id"})
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

}

package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@EqualsAndHashCode(of = {"id"})
public class Message extends BaseModel implements Comparable<Message> {

  @Id
  private String id;

  private Integer seqId;

  private String text;

  private String senderId;

  @Override
  public int compareTo(Message o) {
    return o.getSeqId() - seqId; // In decreasing order of seqId
  }

}

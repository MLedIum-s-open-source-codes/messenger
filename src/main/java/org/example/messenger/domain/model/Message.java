package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@EqualsAndHashCode(of = {"id"})
public class Message extends BaseModel {

  @Id
  private String id;

  @DBRef
  private User sender;

  private String text;

  private Integer recId;

}

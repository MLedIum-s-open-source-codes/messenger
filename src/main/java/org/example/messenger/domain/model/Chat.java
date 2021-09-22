package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
@EqualsAndHashCode(of = {"id"})
public class Chat extends BaseModel {

  @Id
  private String id;

  @DBRef
  private List<User> users;

  @DBRef
  private List<Message> messages;

}

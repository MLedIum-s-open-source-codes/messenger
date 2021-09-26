package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
@EqualsAndHashCode(of = {"id"})
public class Chat extends BaseModel {

  @Id
  private String id;

  @Singular
  private List<ChatUser> users;

  @DBRef
  @Builder.Default
  private List<Message> messages = new ArrayList<>();

  @Builder.Default
  private Integer lastSeqId = 0;

  public ChatUser getInterlocutor(String userId) {
    AtomicReference<ChatUser> interlocutor = new AtomicReference<>();
    getUsers().forEach(chatUser -> {
      if (!chatUser.getUserId().equals(userId)) {
        interlocutor.set(chatUser);
      }
    });
    return interlocutor.get();
  }

  public Optional<Message> getLastMessage() {

    return getMessages().stream().filter(message -> message.getChatSeqId().equals(lastSeqId)).findFirst();
  }

}

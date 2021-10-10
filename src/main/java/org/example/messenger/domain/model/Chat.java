package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Chat extends BaseModel {

  @Id
  private String id;

  @Singular
  private List<ChatUser> users;

  @Builder.Default
  private List<MessageRef> messages = new ArrayList<>();

  @Builder.Default
  private Integer lastSeqId = 0;

  @Builder.Default
  private ChatTypeEnum type = ChatTypeEnum.DIRECT_MESSAGE;

  public Optional<ChatUser> getInterlocutor(String currentUserId) {
    for (ChatUser chatUser : getUsers()) {
      if (!chatUser.getUserId().equals(currentUserId)) {
        return Optional.of(chatUser);
      }
    }

    return Optional.empty();
  }

  public Optional<MessageRef> getLastMessageRef() {

    return getMessages().stream().filter(message -> message.getSeqId().equals(lastSeqId)).findFirst();
  }

  public boolean containsMessageWithId(String id) {
    for (MessageRef chatMessage : messages) {
      if (chatMessage.getMsgId().equals(id))
          return true;
    }

    return false;
  }

}

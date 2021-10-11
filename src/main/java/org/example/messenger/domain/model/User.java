package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.enumeration.RoleEnum;
import org.example.messenger.exception.CustomException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class User extends BaseModel {

  @Id
  private String id;

  @Indexed
  private String username;

  private String publicName;

  private String password;

  @Builder.Default
  private boolean enabled = false;

  @Builder.Default
  private Integer lastMsgSeqId = 0;

  @Builder.Default
  private Integer recMsgSeqId = 0;

  @Builder.Default
  List<ObjectRef> messages = new ArrayList<>();

  @Builder.Default
  private Integer lastConversationSeqId = 0;

  @Builder.Default
  List<ObjectRef> conversations = new ArrayList<>();

  @Builder.Default
  private Set<String> roles = new HashSet<>();

  @DBRef
  private MediaFile avatar;

  public void addMessage(Message message) {
    messages.add(
        ObjectRef.builder()
        .objectId(message.getId())
        .seqId(++lastMsgSeqId)
        .build()
    );
  }

  public Optional<ObjectRef> getMessagePersonalSequenceByMsgId(String MPSMsgId) {

    return messages.stream().filter(
        mPS -> mPS.getObjectId().equals(MPSMsgId)
    ).findFirst();
  }

  public Optional<ObjectRef> getMessagePersonalSequenceBySeqId(Integer MPSSeqId) {

    return messages.stream().filter(
        mPS -> mPS.getSeqId().equals(MPSSeqId)
    ).findFirst();
  }

  public void addConversation(Chat chat) {
    lastConversationSeqId++;
    conversations.add(
        ObjectRef.builder()
            .objectId(chat.getId())
            .seqId(lastConversationSeqId)
            .build()
    );
  }

  public void removeConversation(Chat chat) {
    Optional<ObjectRef> ref = getConversationPersonalSequenceByConversationId(chat.getId());
    if (ref.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Conversation not found");

    conversations.remove(ref.get());
  }

  public Optional<ObjectRef> getConversationPersonalSequenceByConversationId(String CPSMsgId) {

    return conversations.stream().filter(
        cPS -> cPS.getObjectId().equals(CPSMsgId)
    ).findFirst();
  }

  public Optional<ObjectRef> getConversationPersonalSequenceBySeqId(Integer CPSSeqId) {

    return conversations.stream().filter(
        cPS -> cPS.getSeqId().equals(CPSSeqId)
    ).findFirst();
  }

  public void addRole(RoleEnum role) {

    this.roles.add(role.getName());
  }

  public void removeRole(RoleEnum role) {

    this.roles.remove(role.getName());
  }

  public boolean isAdmin() {

    return getRoles().stream().anyMatch(r -> r.equals(RoleEnum.ADMIN.name()));
  }

}

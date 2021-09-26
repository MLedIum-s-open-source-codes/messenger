package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.example.messenger.enumeration.RoleEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@EqualsAndHashCode(of = {"id"})
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
  List<MessagePersonalSequence> messages = new ArrayList<>();

  @Builder.Default
  private Set<String> roles = new HashSet<>();

  public void addMessage(Message message) {
    messages.add(
        MessagePersonalSequence.builder()
        .msgId(message.getId())
        .seqId(++lastMsgSeqId)
        .build()
    );
  }

  public Optional<MessagePersonalSequence> getMessagePersonalSequenceByMsgId(String MPSMsgId) {

    return messages.stream().filter(
        mPS -> mPS.getMsgId().equals(MPSMsgId)
    ).findFirst();
  }

  public Optional<MessagePersonalSequence> getMessagePersonalSequenceBySeqId(Integer MPSSeqId) {

    return messages.stream().filter(
        mPS -> mPS.getSeqId().equals(MPSSeqId)
    ).findFirst();
  }

  public void addRole(RoleEnum role) {

    this.roles.add(role.getName());
  }

  public void removeRole(RoleEnum role) {
    if (this.roles.contains(role.getName())) {
      this.roles.remove(role.getName());
    }
  }

  public boolean isAdmin() {

    return getRoles().stream().anyMatch(r -> r.equals(RoleEnum.ADMIN.name()));
  }

}

package org.example.messenger.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId"})
public class ChatUser {

  private String userId;

  @Builder.Default
  private Integer readSeqId = 0;

  private Boolean isOwner;

  private String invitedBy;

  public ChatUser(String userId, String invitedBy) {
    this.userId = userId;
    this.invitedBy = invitedBy;
  }

  public ChatUser(String userId, Boolean isOwner) {
    this.userId = userId;
    this.isOwner = isOwner;
  }

}

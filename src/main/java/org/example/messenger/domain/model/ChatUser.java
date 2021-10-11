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

  public ChatUser(String userId) {
    this.userId = userId;
  }

  public ChatUser(String userId, Boolean isOwner) {
    this.userId = userId;
    this.isOwner = isOwner;
  }

}

package org.example.messenger.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class ChatUser {

  private String userId;

  @Builder.Default
  private Integer readSeqId = 0;

  @Builder.Default
  private Integer recSeqId = 0;

}

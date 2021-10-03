package org.example.messenger.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"msgId"})
public class MessageRef {

  private String msgId;

  private Integer seqId;

}

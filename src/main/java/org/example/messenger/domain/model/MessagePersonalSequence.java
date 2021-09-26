package org.example.messenger.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"msgId", "seqId"})
public class MessagePersonalSequence {

  private String msgId;

  private Integer seqId;

}

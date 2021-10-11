package org.example.messenger.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"objectId", "seqId"})
public class ObjectRef {

  private String objectId;

  private Integer seqId;

}

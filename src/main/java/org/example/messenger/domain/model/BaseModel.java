package org.example.messenger.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
public abstract class BaseModel {

  @JsonIgnore
  @CreatedBy
  private Long createdBy;

  @JsonIgnore
  @CreatedDate
  private Instant createdDate;

  @JsonIgnore
  @LastModifiedBy
  private Long lastModifiedBy;

  @JsonIgnore
  @LastModifiedDate
  private Instant lastModifiedDate;

}

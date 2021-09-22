package org.example.messenger.domain.dto;

import org.example.messenger.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private String id;

  private String name;

  public static UserDto of(User user) {
    return UserDto.builder()
        .id(user.getId())
        .name(user.getPublicName())
        .build();
  }

}

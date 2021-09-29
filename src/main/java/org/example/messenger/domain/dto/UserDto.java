package org.example.messenger.domain.dto;

import lombok.*;
import org.example.messenger.domain.model.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
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

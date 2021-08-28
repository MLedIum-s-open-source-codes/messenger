package org.example.messenger.domain.dto;

import org.example.messenger.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class UsersDto {

  List<UserDto> users;

  public UsersDto(List<User> users) {
    this.users = users == null ? null : users.stream().map(UserDto::of).collect(Collectors.toList());
  }

}

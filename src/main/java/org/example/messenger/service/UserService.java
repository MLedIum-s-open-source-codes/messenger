package org.example.messenger.service;

import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.entity.User;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;

import static java.lang.String.format;

public interface UserService {

  User create(AuthenticationRequest authenticationRequest);

  User get(Long id);

  User getByUsername(String username);

  User edit(UserDto userDto);

  default void checkNotExistsUserWithUsername(String username) {
    if (existsUserWithUsername(username))
      throw new CustomException(ErrorTypeEnum.ALREADY_EXIST, format("User with username '%s' already exist", username));
  }

  default void checkExistsUserWithId(Long id) {
    if (!existsUserWithId(id))
      throw new CustomException(ErrorTypeEnum.NOT_FOUND, format("User with id '%s' was not found", id));
  }

  default void checkExistsUserWithUsername(String username) {
    if (!existsUserWithUsername(username))
      throw new CustomException(ErrorTypeEnum.NOT_FOUND, format("User with username '%s' was not found", username));
  }

  boolean existsUserWithId(Long id);

  boolean existsUserWithUsername(String username);

  User update(User user);

}

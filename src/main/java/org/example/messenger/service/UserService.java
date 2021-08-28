package org.example.messenger.service;

import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.entity.User;

public interface UserService {

  User create(AuthenticationRequest authenticationRequest);

  User get(Long id);

  User getByUsername(String username);

  User edit(UserDto userDto);

  boolean existsUserWithUsername(String username);

  User update(User user);

}

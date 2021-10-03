package org.example.messenger.service.impl;

import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.domain.model.User;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.enumeration.RoleEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.UserRepository;
import org.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User create(AuthenticationRequest authenticationRequest) {
    checkNotExistsUserWithUsername(authenticationRequest.getUsername());

    User user = User.builder()
        .username(authenticationRequest.getUsername())
        .publicName(authenticationRequest.getPublicName())
        .password(authenticationRequest.getPassword())
        .enabled(true)
        .build();

    user.addRole(RoleEnum.USER);

    return update(user);
  }

  @Override
  public User get(String id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, format("User with id '%s' was not found", id));

    return user.get();
  }

  @Override
  public User getByUsername(String username) {
    Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
    if (user.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, format("User with username '%s' was not found", username));

    return user.get();
  }

  @Override
  public User edit(UserDto userDto) {
    User user = get(userDto.getId());
    if (userDto.getName() != null)
        user.setUsername(userDto.getName());

    return update(user);
  }

  @Override
  public boolean existsUserWithId(String id) {

    return userRepository.existsById(id);
  }

  @Override
  public boolean existsUserWithUsername(String username) {

    return userRepository.existsByUsername(username);
  }

  @Override
  public User update(User user) {

    return userRepository.save(user);
  }

}

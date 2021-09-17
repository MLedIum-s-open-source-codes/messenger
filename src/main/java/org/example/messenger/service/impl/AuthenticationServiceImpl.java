package org.example.messenger.service.impl;

import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.domain.response.AuthenticationResponse;
import org.example.messenger.entity.User;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.security.token.JwtTokenProvider;
import org.example.messenger.service.AuthenticationService;
import org.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;
  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
    User user = userService.getByUsername(authenticationRequest.getUsername());

    if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
      throw new CustomException(ErrorTypeEnum.INCORRECT_LOGIN_OR_PASSWORD, format("Incorrect login or password"));
    }

    String token = jwtTokenProvider.createToken(user.getUsername());

    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }

  @Override
  public AuthenticationResponse register(AuthenticationRequest authenticationRequest) {
    userService.checkNotExistsUserWithUsername(authenticationRequest.getUsername());

    authenticationRequest.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
    User user = userService.create(authenticationRequest);

    String token = jwtTokenProvider.createToken(user.getUsername());

    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }

  @Override
  public void logout(HttpServletRequest request) {

    jwtTokenProvider.addToBlackList(request);
  }

}

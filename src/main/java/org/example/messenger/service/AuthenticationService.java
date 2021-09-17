package org.example.messenger.service;

import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.domain.response.AuthenticationResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {

  AuthenticationResponse login(AuthenticationRequest authenticationRequest);

  AuthenticationResponse register(AuthenticationRequest authenticationRequest);

  void logout(HttpServletRequest request);

}

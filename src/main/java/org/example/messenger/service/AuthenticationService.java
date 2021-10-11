package org.example.messenger.service;

import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.domain.request.RegistrationRequest;
import org.example.messenger.domain.response.AuthenticationResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {

  AuthenticationResponse login(AuthenticationRequest authenticationRequest);

  AuthenticationResponse register(RegistrationRequest registrationRequest);

  void logout(HttpServletRequest request);

}

package org.example.messenger.controller;

import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.domain.request.RegistrationRequest;
import org.example.messenger.domain.response.AuthenticationResponse;
import org.example.messenger.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {

    return ResponseEntity.ok(authenticationService.login(authenticationRequest));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {

    return ResponseEntity.ok(authenticationService.register(registrationRequest));
  }

  @DeleteMapping("/logout")
  public ResponseEntity<HttpStatus> logout(HttpServletRequest request) {
    authenticationService.logout(request);
    return ResponseEntity.ok().build();
  }

}

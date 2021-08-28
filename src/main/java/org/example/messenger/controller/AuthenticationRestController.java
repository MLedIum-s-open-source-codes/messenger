package org.example.messenger.controller;

import org.example.messenger.domain.request.AuthenticationRequest;
import org.example.messenger.domain.response.AuthenticationResponse;
import org.example.messenger.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest authenticationRequest) {

    return ResponseEntity.ok(authenticationService.register(authenticationRequest));
  }

}

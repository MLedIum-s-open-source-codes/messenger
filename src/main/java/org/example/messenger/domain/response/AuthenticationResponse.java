package org.example.messenger.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticationResponse {

  private final String token;

}

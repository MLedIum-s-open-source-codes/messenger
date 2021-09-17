package org.example.messenger.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtTokenProperties {

  String issuer = "Wwwwww";

  String secretKey = "123456789";

  long expiredTimeSecAuthToken = 604800;
  long expiredTimeSecOtherToken = 86400;

  long timeoutSec = 86400;

}

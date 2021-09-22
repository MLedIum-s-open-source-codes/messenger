package org.example.messenger.domain.audit;

import org.example.messenger.security.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuditing implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }
    if (authentication.getPrincipal() instanceof UserDetailsImpl) {
      return Optional.ofNullable(((UserDetailsImpl) authentication.getPrincipal()).getId());
    }
    return Optional.ofNullable(authentication.getPrincipal().toString());
  }

}

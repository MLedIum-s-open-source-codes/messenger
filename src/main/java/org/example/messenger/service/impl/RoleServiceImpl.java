package org.example.messenger.service.impl;

import org.example.messenger.entity.Role;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.RoleRepository;
import org.example.messenger.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;

  @Override
  public Role getRole(Long roleId) {
    Optional<Role> role = roleRepository.findById(roleId);
    if (role.isPresent()) {
      return role.get();
    }
    log.error("Role with id '{}' not found", roleId);
    throw new CustomException(
        ErrorTypeEnum.NOT_FOUND,
        String.format("Role with id '%s' not found", roleId));
  }

  @Override
  public Role getRole(String name) {
    Optional<Role> role = roleRepository.findByNameIgnoreCase(name);
    if (role.isPresent()) {
      return role.get();
    }
    log.error("Role with name '{}' not found", name);
    throw new CustomException(
        ErrorTypeEnum.NOT_FOUND,
        String.format("Role with name '%s' not found", name));
  }

  @Override
  public List<Role> getAllRoles() {

    return roleRepository.findAll();
  }

}

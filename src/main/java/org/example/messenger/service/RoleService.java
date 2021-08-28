package org.example.messenger.service;

import org.example.messenger.entity.Role;

import java.util.List;

public interface RoleService {

  Role getRole(Long roleId);

  Role getRole(String name);

  List<Role> getAllRoles();

}

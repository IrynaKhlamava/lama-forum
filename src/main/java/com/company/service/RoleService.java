package com.company.service;

import com.company.model.Role;
import com.company.model.enumType.RoleName;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(RoleName name);

}

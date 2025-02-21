package com.company.repository;

import com.company.model.Role;
import com.company.model.enums.RoleName;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(RoleName name);

}

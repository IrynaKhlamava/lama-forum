package com.company.service.impl;

import com.company.model.Role;
import com.company.model.enums.RoleName;
import com.company.repository.RoleRepository;
import com.company.service.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleRepository.findByName(name);
    }

}

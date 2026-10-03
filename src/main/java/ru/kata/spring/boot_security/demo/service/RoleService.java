package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Collection;
import java.util.List;

public interface RoleService {
    void save(Role role);

    Role findByName(String name);

    List<Role> findByIds(Collection<Long> ids);

    List<Role> listRoles();
}

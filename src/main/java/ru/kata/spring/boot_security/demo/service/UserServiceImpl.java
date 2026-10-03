package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void add(String firstName, String lastName, String email, String username, String rawPassword, List<Long> roleIds) {
        validate(firstName, lastName, email, username);
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Укажите пароль");
        }
        checkUsernameUnique(username, null);

        User user = new User(firstName, lastName, email, username, passwordEncoder.encode(rawPassword));
        user.setRoles(resolveRoles(roleIds));
        userDao.add(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        return userDao.getById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public void update(Long id, String firstName, String lastName, String email, String username, String rawPassword, List<Long> roleIds) {
        validate(firstName, lastName, email, username);
        User user = userDao.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        checkUsernameUnique(username, id);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setRoles(resolveRoles(roleIds));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }
        user.getAuthorities().size();
        return user;
    }

    private void validate(String firstName, String lastName, String email, String username) {
        if (firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()
                || email == null || email.isBlank()
                || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Заполните все поля");
        }
    }

    private void checkUsernameUnique(String username, Long currentId) {
        User existing = userDao.findByUsername(username);
        if (existing != null && (currentId == null || !existing.getId().equals(currentId))) {
            throw new IllegalArgumentException("Пользователь с таким логином уже есть");
        }
    }

    private Set<Role> resolveRoles(List<Long> roleIds) {
        Set<Role> roles = new HashSet<>(roleDao.findByIds(roleIds));
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Выберите хотя бы одну роль");
        }
        return roles;
    }
}

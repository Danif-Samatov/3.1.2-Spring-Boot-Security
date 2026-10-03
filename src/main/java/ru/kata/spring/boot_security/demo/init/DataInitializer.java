package ru.kata.spring.boot_security.demo.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = getOrCreate("ROLE_ADMIN");
        Role userRole = getOrCreate("ROLE_USER");

        if (userDao.findByUsername("admin") == null) {
            User admin = new User("Admin", "Adminov", "admin@mail.ru", "admin", passwordEncoder.encode("admin"));
            admin.setRoles(new HashSet<>(Set.of(adminRole, userRole)));
            userDao.add(admin);
        }
        if (userDao.findByUsername("user") == null) {
            User user = new User("User", "Userov", "user@mail.ru", "user", passwordEncoder.encode("user"));
            user.setRoles(new HashSet<>(Set.of(userRole)));
            userDao.add(user);
        }
    }

    private Role getOrCreate(String name) {
        Role role = roleDao.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleDao.save(role);
        }
        return role;
    }
}

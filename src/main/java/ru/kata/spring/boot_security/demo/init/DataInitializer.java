package ru.kata.spring.boot_security.demo.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;

    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");
        roleService.save(adminRole);
        roleService.save(userRole);

        User admin = new User("Admin", "Adminov", "admin@mail.ru", "admin", "admin");
        userService.add(admin, List.of(adminRole.getId(), userRole.getId()));

        User user = new User("User", "Userov", "user@mail.ru", "user", "user");
        userService.add(user, List.of(userRole.getId()));
    }
}

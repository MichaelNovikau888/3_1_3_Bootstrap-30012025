package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
//import ru.kata.spring.boot_security.demo.service.RoleService;
//import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;

    @Autowired
    public AdminUserController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Получаем email (username) аутентифицированного пользователя
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b) // Объединяем роли через запятую
                .orElse("");
        model.addAttribute("authenticatedEmail", email);
        model.addAttribute("authenticatedRoles", roles);
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin";
    }
 /*   @GetMapping
    public String adminPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Получаем email (username) аутентифицированного пользователя
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b) // Объединяем роли через запятую
                .orElse("");
        model.addAttribute("authenticatedEmail", email);
        model.addAttribute("authenticatedRoles", roles);
        return "admin";
    }*/

    @GetMapping("/addUser")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/editUser")
    public String editUserForm(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "editUser";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute User user) {
        User existingUser = userService.getUserById(user.getId()).orElseThrow();

        // Обновляем только необходимые поля
        existingUser.setSurname(user.getSurname());
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setPassword(user.getPassword());
        // Преобразование строк в объекты Role
        Set<Role> updatedRoles = user.getRoles().stream()
                .map(role -> roleService.getRoleByName(role.getName())) // Найти Role по имени
                .collect(Collectors.toSet());
        existingUser.setRoles(updatedRoles);

        userService.updateUser(existingUser);
        return "redirect:/admin";
    }


    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}


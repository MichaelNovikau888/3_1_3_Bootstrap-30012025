package ru.kata.spring.boot_security.demo.controller;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminUserController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminUserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(Model model, @AuthenticationPrincipal User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        model.addAttribute("authenticatedEmail", email);
        model.addAttribute("authenticatedRoles", roles);

        model.addAttribute("user", user); // Добавляем авторизованного пользователя в модель
        model.addAttribute("users", userService.findAll()); // Добавляем всех пользователей в модель
        model.addAttribute("allRoles", roleService.findAll()); // Добавляем роли в модель
        return "admin"; // Возвращаем имя вашего шаблона
    }

    @GetMapping("/users/{id}")
    public String findUserById(Model model, @PathVariable Long id) {
        model.addAttribute("user", userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("allRoles", roleService.findAll());
        return "user"; // Возвращаем имя вашего шаблона user.html
    }

    @PostMapping("/users")
    public String addUser(User user) {
        userService.add(user);
        return "redirect:/admin"; // Перенаправление на страницу администратора после добавления пользователя
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (!userService.removeById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/admin"; // Перенаправление на страницу администратора после удаления пользователя
    }

    @PutMapping("/users/{id}")
    public String updateUser(User user) {
        if (!userService.update(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/admin"; // Перенаправление на страницу администратора после обновления пользователя
    }
}

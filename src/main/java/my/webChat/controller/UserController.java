package my.webChat.controller;

import my.webChat.data.User;
import my.webChat.exception.ForbiddenException;
import my.webChat.service.UserLogService;
import my.webChat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserLogService logService;

    @GetMapping("profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("list")
    public String getUsersList(@AuthenticationPrincipal User user, Principal principal, Model model) {
        if(user == null) throw new ForbiddenException();
        model.addAttribute("users", userService.getUsersNames(user));
        return "fragments::users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("changePassword")
    public String changePasswordAdministrator(@RequestParam("useridCP") User user,
                                              @RequestParam("passwordCP") String password, Model model) {
        userService.updatePassword(user, password);
        return "redirect:/statistics";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("changeUserName")
    public String changeUserNameAdministrator(@RequestParam("useridCUN") User user,
                                              @RequestParam("usernameCUN") String username, Model model) {
        userService.updateUserName(user, username);
        return "redirect:/statistics";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("changeToken")
    public String changeTokenAdministrator(@RequestParam("useridCT") User user, Model model) {
        userService.updateToken(user);
        return "redirect:/statistics";
    }

    @PostMapping("profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @Valid User profileUser, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }
        userService.updatePassword(user, profileUser.getPassword());
        return "redirect:/form";
    }

    @GetMapping("registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            logService.addLog("add new user without name or password", request.getRemoteAddr());
            return "registration";
        }
        if (userService.addUser(user)) {
            logService.addLog("add new user " + user.getUsername(), request.getRemoteAddr());
            return "redirect:login";
        } else {
            bindingResult.addError(new ObjectError("globalError", "user exist"));
            logService.addLog("add new user error user exist " + user.getUsername(), request.getRemoteAddr());
            return "registration";
        }
    }
}

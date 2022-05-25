package my.webChat.controller;

import my.webChat.data.Message;
import my.webChat.data.User;
import my.webChat.service.ActiveUser;
import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Controller
public class WebChatController {
    @Autowired
    MessageService service;
    @Autowired
    UserService userService;

    @Autowired
    ActiveUser activeUser;

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @GetMapping("login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/form";
    }

    @GetMapping("error")
    public String error() {
        return "error";
    }

    @PostMapping("form")
    public String addMessage(@Valid @ModelAttribute("msg") Message msg,
                             BindingResult bindingResult, Model model,
                             @AuthenticationPrincipal User user,
                             @RequestParam("file") MultipartFile file) {
        if (msg.getReceivers() == null) {
            bindingResult.addError(new ObjectError("globalError", "select receivers"));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getUsers(user));
            return "form";
        }
        service.addMessage(msg.getText(), user,
                msg.getReceivers(),
                service.receiveFile(file), file.getOriginalFilename(), file.getSize());
        return "redirect:/form";
    }

    @GetMapping("form")
    public String chat(@AuthenticationPrincipal User user, Model model,
                       @RequestParam(value = "filter", required = false) Optional<String> filter) {
        if (user == null) {
            log.error("Error Authentication user");
            return "redirect:/login";
        }
        String f = filter.orElse("");
        Message msg = new Message();
        msg.setReceivers(new HashSet<>());
        model.addAttribute("users", userService.getUsers(user));
        model.addAttribute("filter", f);
        model.addAttribute("msg", msg);
        model.addAttribute("online", activeUser.getUsers());
        return "form";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("statistics")
    public String getStatistics(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("statistics", service.getStatisticsSendMessages());
        model.addAttribute("online", activeUser.getUsers());
        return "statistics";
    }
}

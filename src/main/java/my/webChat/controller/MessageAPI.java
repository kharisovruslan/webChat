package my.webChat.controller;

import my.webChat.data.Message;
import my.webChat.data.User;
import my.webChat.data.dto.UserMessage;
import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("messagesAPI")
public class MessageAPI {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Resource(name="authenticationManager")
    private AuthenticationManager authManager;

    @PostMapping(value = "last", produces = "text/plain")
    public String getLast(@RequestParam("token") String token) {
        User user = userService.getUserByToken(UUID.fromString(token));
        return messageService.getLastTime(user);
    }

    @GetMapping("loginToken")
    public void loginToken(@RequestParam("token") String token,
                           final HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = userService.getUserByToken(UUID.fromString(token));
        // only as user
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
        response.sendRedirect("/form");
    }

    @PostMapping(value = "messagesafter", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserMessage> getMessages(@RequestParam("token") String token, @RequestParam("last") String afterTime) {
        User user = userService.getUserByToken(UUID.fromString(token));
        return messageService.getUserMessagesAfter(user, afterTime).stream()
                .map(m -> new UserMessage(m.getText(), m.getCreate(), m.getAuthor().getUsername()))
                .collect(Collectors.toList());
    }
}

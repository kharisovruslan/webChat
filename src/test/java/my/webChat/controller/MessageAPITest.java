package my.webChat.controller;

import my.webChat.SpringSecurityWebTestConfig;
import my.webChat.data.Message;
import my.webChat.data.MessageRepository;
import my.webChat.data.User;
import my.webChat.data.UserRepository;
import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringSecurityWebTestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("user1")
class MessageAPITest {
    @Autowired
    MockMvc mvc;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss SSS");

    @Test
    void getLast() throws Exception {
        User u1 = new User("user1", "password1");
        User u2 = new User("user2", "password2");
        User u3 = new User("user3", "password3");
        userService.addUser(u1);
        userService.addUser(u2);
        userService.addUser(u3);
        Message m1 = messageService.addMessage("hello", u2, Collections.singleton(u1), "", "", 0);
        Message m2 = messageService.addMessage("hello", u1, Collections.singleton(u2), "", "", 0);
        Message m3 = messageService.addMessage("hello", u2, Collections.singleton(u3), "", "", 0);
        Message m4 = messageService.addMessage("hello0", u3, Collections.singleton(u1), "", "", 0);
        Assertions.assertEquals(3, userService.getUsers().size());
        mvc.perform(post("/messagesAPI/last").param("token", u1.getToken().toString())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(m4.getCreate().format(dateTimeFormatter))));
        Message m5 = messageService.addMessage("hello1", u3, Collections.singleton(u1), "", "", 0);
        Message m6 = messageService.addMessage("hello2", u3, Collections.singleton(u1), "", "", 0);
        mvc.perform(post("/messagesAPI/messagesafter").param("token", u1.getToken().toString())
                        .param("last", m3.getCreate().format(dateTimeFormatter))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString("hello2")));
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }
}
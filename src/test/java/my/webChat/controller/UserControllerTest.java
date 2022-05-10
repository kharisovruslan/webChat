package my.webChat.controller;

import my.webChat.SpringSecurityWebTestConfig;
import my.webChat.data.User;
import my.webChat.data.UserRepository;
import net.bytebuddy.asm.Advice;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringSecurityWebTestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("user1")
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepository repository;

    @Test
    void users() throws Exception {
        User u1 = new User("user1", "password");
        User u2 = new User("user2", "password");
        User u3 = new User("user3", "password");
        given(repository.findAll()).willReturn(List.of(u1, u2, u3));
        mvc.perform(get("/users/list")).andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(content().string(CoreMatchers.containsString("user2")))
                .andExpect(content().string(CoreMatchers.not(CoreMatchers.containsString("user1"))))
                .andExpect(xpath("//tr").nodeCount(2));
    }

    @Test
    void registration() throws Exception {
        mvc.perform(get("/users/registration")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void profile() throws Exception {
        mvc.perform(get("/users/profile")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(xpath("//input[@value='user1']").exists());
    }

    @Test
    void updateProfile() throws Exception {
        given(repository.save(any(User.class))).willReturn(new User());
        mvc.perform(post("/users/profile")
                        .param("username", "user1")
                        .param("password", "newPassword")
                        .with(csrf())).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/form"));
        verify(repository, times(1)).save(any(User.class));
        ArgumentCaptor<User> arg = ArgumentCaptor.forClass(User.class);
        verify(repository).save(arg.capture());
        Assertions.assertNotEquals("password", arg.getValue().getPassword());
    }

    @Test
    void addUser() throws Exception {
        given(repository.save(any(User.class))).willReturn(new User());
        mvc.perform(post("/users/registration")
                        .param("username", "NewUser")
                        .param("password", "passwordNewUser")
                        .with(csrf()))
                .andDo(print()).
                andExpect(status().is3xxRedirection());
        ArgumentCaptor<User> arg = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(arg.capture());
        Assertions.assertEquals("NewUser", arg.getValue().getUsername());
        Assertions.assertTrue(arg.getValue().getPassword().length() > 0);
    }
}
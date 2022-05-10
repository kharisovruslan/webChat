package my.webChat.config.service;

import my.webChat.data.Role;
import my.webChat.data.User;
import my.webChat.data.UserRepository;
import my.webChat.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Set;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Test
    void addUser() {
        String userPassword = "passwordUser";
        User u1 = new User("user", userPassword);
        u1.setRoles(Collections.singleton(Role.USER));
        Assertions.assertTrue(userService.addUser(u1), "user with name user is not in database");
        User u2 = new User("user", "passwordAdmin");
        u2.setRoles(Set.of(Role.ADMIN, Role.USER));
        Assertions.assertFalse(userService.addUser(u2), "user with name user in database");
        Assertions.assertNotEquals(userRepository.findByUsername("user").get().getPassword(),
                userPassword, "error encode password");
        User userByName = userService.getByUserName("user");
        Assertions.assertEquals("user", userByName.getUsername());
        userRepository.deleteAll();
        Assertions.assertEquals(0, userRepository.findAll().size());
    }

    @Test
    void loadUserByUsername() {
        User u1 = new User("user", "passwordUser");
        u1.setRoles(Collections.singleton(Role.USER));
        User su1 = userRepository.save(u1);
        User u2 = new User("admin", "passwordAdmin");
        u2.setRoles(Set.of(Role.ADMIN, Role.USER));
        User su2 = this.userRepository.save(u2);
        Assertions.assertNotNull(su1);
        Assertions.assertNotNull(su2);
        Assertions.assertEquals(2, userRepository.findAll().size());
        Assertions.assertEquals("user", userService.loadUserByUsername("user").getUsername());
        Assertions.assertThrowsExactly(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("user1").getUsername(),
                "Error waitable exception UsernameNotFoundException");
        userRepository.deleteAll();
        Assertions.assertEquals(0, userRepository.findAll().size());
    }
}
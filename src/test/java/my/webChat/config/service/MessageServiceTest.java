package my.webChat.config.service;

import my.webChat.data.Message;
import my.webChat.data.MessageRepository;
import my.webChat.data.User;
import my.webChat.data.UserRepository;
import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void findAuthorMessageOrMessageForUser() {
        User u1 = new User("admin", "password");
        User u2 = new User("user2", "password");
        User u3 = new User("user3", "password");
        userService.addUser(u1);
        userService.addUser(u2);
        userService.addUser(u3);
        messageService.addMessage("Hello from admin to u2", u1, Set.of(u2), "", "", 0);
        messageService.addMessage("Hello from admin to u3", u1, Set.of(u3), "", "", 0);
        messageService.addMessage("Hello from u2 to u3", u2, Set.of(u3), "", "", 0);
        messageService.addMessage("Hello from u3 to admin", u3, Set.of(u1), "", "", 0);
        messageService.addMessage("Hello from u2 to admin", u2, Set.of(u1), "", "", 0);
        Assertions.assertEquals(5, messageRepository.findAll().size(), "Error add message");

        Pageable page = PageRequest.of(0, 10);
        List<Message> adminMessages = messageService.findAuthorMessageOrMessageForUser(u1, page).getContent();
        List<Message> adminMessagesFilter = messageService.findAuthorMessageOrMessageForUser(u1, page, "u2").getContent();
        Assertions.assertEquals(2, adminMessagesFilter.size());
        Assertions.assertEquals(4, adminMessages.size());
        List<Message> u2Messages = messageService.findAuthorMessageOrMessageForUser(u2, page).getContent();
        Assertions.assertEquals(3, u2Messages.size());
        List<Message> u3Messages = messageService.findAuthorMessageOrMessageForUser(u3, page).getContent();
        Assertions.assertEquals(3, u3Messages.size());
        Assertions.assertEquals(7, u3Messages.get(0).getId());
        Map<User, String> statistics = messageService.getStatisticsSendMessages();
        Assertions.assertNotNull(statistics);
        Assertions.assertEquals(3, statistics.size());

        Assertions.assertEquals(7, Integer.parseInt(messageService.getLastID(u3)));
        messageRepository.deleteAll();
        Assertions.assertEquals(0, messageRepository.findAll().size(), "Error remove message");
        userRepository.deleteAll();
        Assertions.assertEquals(0, userRepository.findAll().size(), "Error remove user");
    }
}
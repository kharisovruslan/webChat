package my.webChat.data;

import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Profile("dev")
@Service
public class testDataInsert {
    @Autowired
    MessageService service;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(testDataInsert.class);

    List<User> users;

    List<String> load(String filename) {
        try {
            byte[] a = Files.readAllBytes(Path.of(filename));
            String s = new String(a, StandardCharsets.UTF_8);
            return Arrays.asList(s.split("%"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String msg) {
        Random r = new Random();
        if ((msg.length() < 5) || (msg.length() > 4000)) {
            return;
        }
        if (users.size() < 1) {
            throw new IllegalArgumentException("users size " + users.size());
        }
        int author = r.nextInt(users.size());
        int receiver;
        do {
            receiver = r.nextInt(users.size());
        } while (author == receiver);
        service.addMessage(msg, users.get(author), Collections.singleton(users.get(receiver)), "", "", 0);
    }

    @PostConstruct
    public void init() {
        if (userRepository.findAll().size() == 0) {
            userService.addUser(new User("admin", "1234"));
            userService.addUser(new User("user1", "1234"));
            userService.addUser(new User("user2", "1234"));
            userService.addUser(new User("user3", "1234"));
            userService.addUser(new User("user4", "1234"));
            userService.addUser(new User("user5", "1234"));
            users = userRepository.findAll();
            try {
                Files.walkFileTree(Path.of("S:\\fortune"), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        log.info("load " + file);
                        load(file.toAbsolutePath().toString()).forEach(msg -> sendMessage(msg));
                        return super.visitFile(file, attrs);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package my.webChat.service;

import my.webChat.data.User;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActiveUser {
    private Map<User, LocalDateTime> online = new HashMap<>();
    public synchronized void updateUser(User user) {
        online.put(user, LocalDateTime.now());
    }

    // repeat every 30 sec start after one minute
    @Scheduled(fixedDelay = 30_000, initialDelay = 60_000)
    public synchronized void removeOffUsers() {
        LocalDateTime n = LocalDateTime.now();
        online.entrySet().removeIf(v -> ChronoUnit.SECONDS.between(v.getValue(), n) > 100);
    }

    public synchronized List<User> getUsers() {
        return online.keySet().stream().collect(Collectors.toList());
    }
}

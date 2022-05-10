package my.webChat.service;

import my.webChat.data.UserLog;
import my.webChat.data.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserLogService {
    @Autowired
    UserLogRepository repository;

    public void addLog(@NonNull String text, @NonNull String address) {
        UserLog userLog = new UserLog(text, LocalDateTime.now(), address);
        repository.save(userLog);
    }
}

package my.webChat.service;

import my.webChat.data.Message;
import my.webChat.data.MessageRepository;
import my.webChat.data.dto.StatisticsUser;
import my.webChat.data.User;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Value("upload.folder")
    private String uploadFolder;

    @Autowired
    EntityManager em;
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss SSS");

    public Message addMessage(String text, User author, Set<User> receivers, String fileName,
                              String fileOrigName, long fileSize) {
        Message m = new Message(author, LocalDateTime.now(), receivers, text);
        m.setFileOrigName(fileOrigName);
        m.setFileName(fileName);
        m.setFileSize(FileUtils.byteCountToDisplaySize(fileSize));
        return messageRepository.save(m);
    }

    public Page<Message> findAuthorMessageOrMessageForUser(User user, Pageable page) {
        if (user == null)
            throw new IllegalArgumentException("user null");
        return messageRepository.findDistinctByAuthorOrReceiversOrderByCreateDesc(user, user, page);
    }

    public Page<Message> findAuthorMessageOrMessageForUser(User user, Pageable page, String filter) {
        if (user == null)
            throw new IllegalArgumentException("user null");
        return messageRepository.findByFilter(filter, user, page);
    }

    public String getLastID(User user) {
        return Long.toString(messageRepository.findFirstByAuthorOrReceiversOrderByCreateDesc(user, user).map(Message::getId).orElse(0L));
    }

    public String getLastTime(User user) {
        return messageRepository.findFirstByReceiversOrderByCreateDesc(user)
                .map(Message::getCreate)
                .orElse(LocalDateTime.now()).format(dateTimeFormatter);
    }

    public List<Message> getUserMessagesAfter(User user, String afterTime) {
        LocalDateTime after = LocalDateTime.parse(afterTime, dateTimeFormatter);
        List<Message> messages = messageRepository.findDistinctByReceiversAndCreateAfterOrderByCreateDesc(user, after);
        if (messages.size() > 1) {
            messages.remove((messages.size() - 1));
        }
        return messages;
    }

    public byte[] sendFile(String uuid) throws IOException {
        File parent = new File(uploadFolder);
        File file = new File(parent, uuid);
        return Files.readAllBytes(file.toPath());
    }

    public String receiveFile(MultipartFile file) {
        if (!file.getOriginalFilename().isEmpty()) {
            UUID uuid = UUID.randomUUID();
            File parent = new File(uploadFolder);
            if (!parent.exists()) {
                parent.mkdir();
            }
            File fileTo = new File(parent, uuid.toString());
            try {
                file.transferTo(fileTo.getAbsoluteFile());
            } catch (IOException e) {
                log.error("Error save file " + file.getOriginalFilename() + " to " + fileTo);
                log.error(e.getMessage(), e);
                try {
                    Files.deleteIfExists(fileTo.toPath());
                } catch (IOException ex) {
                    log.error(e.getMessage(), e);
                }
                return "";
            }
            return uuid.toString();
        }
        return "";
    }

    public List<StatisticsUser> getStatisticsSendMessages() {
        return messageRepository.getStatistics().stream().map(a -> {
            return new StatisticsUser((User) a[0], 0, (Long) a[1]);
        }).sorted().collect(Collectors.toList());
    }
}

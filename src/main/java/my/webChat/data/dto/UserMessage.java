package my.webChat.data.dto;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class UserMessage {
    @NonNull
    private String text;
    @NonNull
    private LocalDateTime create;
    @NonNull
    private String author;
}

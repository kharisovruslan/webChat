package my.webChat.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "User_Log")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull
    @Column(name = "log_text", length = 1024)
    private String text;
    @NonNull
    @Column(name = "log_create")
    private LocalDateTime create;
    @NonNull
    @Column(name = "log_address", length = 256)
    private String address;
}

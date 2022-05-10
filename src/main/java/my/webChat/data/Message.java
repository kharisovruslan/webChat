package my.webChat.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Message(User author, LocalDateTime create, Set<User> receivers, String text) {
        this.author = author;
        this.create = create;
        this.receivers = receivers;
        this.text = text;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "createtime")
    private LocalDateTime create;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "receivers_message",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> receivers;

    @Size(min = 1, max = 4096, message = "size between 1 - 4096 chars")
    @Column(name = "message_text", length = 4096)
    private String text;

    @Column(length = 1024)
    private String fileName;

    @Column(length = 1024)
    private String fileOrigName;
    @Column(length = 250)
    private String fileSize;
}

package my.webChat.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {
    Page<Message> findDistinctByAuthorOrReceiversOrderByCreateDesc(User author, User receiver, Pageable page);
    Optional<Message> findFirstByAuthorOrReceiversOrderByCreateDesc(User author, User receiver);
    Optional<Message> findFirstByReceiversOrderByCreateDesc(User receiver);
    @Query(value = "select distinct m from Message m join m.receivers r where (lower(m.text) like lower(CONCAT('%',:filter,'%'))) and ((m.author = :user) or (r = :user)) order by m.create")
    Page<Message> findByFilter(String filter, User user, Pageable page);
    List<Message> findAll();
    @Query(value = "SELECT a, COUNT(m.author) FROM Message AS m RIGHT JOIN m.author AS a GROUP BY a ORDER BY a")
    List<Object[]> getStatistics();
    List<Message> findDistinctByReceiversAndCreateAfterOrderByCreateDesc(User user, LocalDateTime after);
}

package my.webChat.data;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAll();

    Optional<User> findByToken(UUID token);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.visited = :visited WHERE u.id = :id")
    void updateVisitedDateTime(@Param(value = "id") long id, @Param(value = "visited") LocalDateTime visited);
}

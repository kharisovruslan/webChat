package my.webChat.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserLogRepository extends CrudRepository<UserLog, Long> {
}

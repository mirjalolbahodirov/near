package uz.algo.near.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import uz.algo.near.domains.Message;

import java.util.List;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {

    @Query(value = "select * from message where (from_id = ?1 and to_id = ?2) or (from_id = ?2 and to_id = ?1) " +
            "order by message_time desc limit 20 offset ?3", nativeQuery = true)
    List<Message> findAllUserMessages(Long fromId, Long toId, int offset);

}

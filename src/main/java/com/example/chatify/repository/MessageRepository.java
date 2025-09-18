package com.example.chatify.repository;


import com.example.chatify.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientIdOrderBySentAtAsc(Long userId);
    List<Message> findBySenderIdAndRecipientIdOrSenderIdAndRecipientIdOrderBySentAtAsc(
            Long s1, Long r1, Long s2, Long r2);
    List<Message> findByGroupChatIdOrderBySentAtAsc(Long groupId);
}
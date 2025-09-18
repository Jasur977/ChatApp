package com.example.chatify.repository;


import com.example.chatify.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;


@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
}
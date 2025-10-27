package com.dcm.demo.repository;

import com.dcm.demo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
        ORDER BY m.sentTime DESC
        LIMIT 1
    """)
    Optional<Message> findLastestMessageByConversationId(Integer conversationId);

    @Query(value = """
        (
            SELECT * FROM tin_nhan
            WHERE id_phong_chat = :conversationId
              AND id <= :lastReadId
            ORDER BY id DESC
            LIMIT :beforeCount
        )
        UNION ALL
        (
            SELECT * FROM tin_nhan
            WHERE id_phong_chat = :conversationId
              AND id > :lastReadId
            ORDER BY id ASC
        )
        ORDER BY id ASC
    """, nativeQuery = true)
    List<Message> findMessagesAroundUnread(
            Integer conversationId,
            Integer lastReadId,
            Integer beforeCount
    );

    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
        ORDER BY m.id DESC
        LIMIT :limit
    """)
    List<Message> findMessageLatest(Integer conversationId, Integer limit);

    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.id < :messageId
        ORDER BY m.id DESC
        LIMIT 30
    """)
    List<Message> findMessageBeforeId(
            Integer conversationId,
            Integer messageId
    );

    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.id <= :lastReadId
    """)
    int countRead(Integer conversationId, Integer lastReadId);
    int countByConversationId(Integer conversationId);

    void deleteByConversationId(Integer conversationId);
}

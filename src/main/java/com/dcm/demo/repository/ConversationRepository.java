package com.dcm.demo.repository;

import com.dcm.demo.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer>, JpaSpecificationExecutor<Conversation> {
    List<Conversation> findByPatientId(Integer userId);
}

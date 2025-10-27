package com.dcm.demo.repository;

import com.dcm.demo.model.Message;
import com.dcm.demo.model.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageImageRepository extends JpaRepository<MessageImage, Integer> {


}

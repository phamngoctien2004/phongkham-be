package com.dcm.demo.repository;

import com.dcm.demo.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>, JpaSpecificationExecutor<Room> {

    @Query("""
            SELECT r FROM Room r
            WHERE (:departmentId IS NULL OR r.department.id IN :departmentId)
            AND (:keyword IS NULL OR LOWER(r.roomName) LIKE LOWER(CONCAT('%', :keyword , '%')))
            """)
    Page<Room> findAll(Pageable pageable, List<Integer> departmentId, String keyword);

}

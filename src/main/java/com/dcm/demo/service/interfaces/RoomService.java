package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.RoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    Page<RoomDTO> findAll(Pageable pageable, String keyword, List<Integer> departmentId);
}

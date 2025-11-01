package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.RoomDTO;
import com.dcm.demo.mapper.RoomMapper;
import com.dcm.demo.model.Room;
import com.dcm.demo.repository.RoomRepository;
import com.dcm.demo.service.interfaces.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository repository;
    private final RoomMapper mapper;
    @Override
    public Page<RoomDTO> findAll(Pageable pageable, String keyword, List<Integer> departmentId) {

        return repository.findAll(pageable, departmentId, keyword).map(this::toDto);
    }

    private RoomDTO toDto(Room room) {
        RoomDTO roomDTO = mapper.toResponse(room);
        roomDTO.setDepartmentName(room.getDepartment().getName());
        return roomDTO;
    }
}

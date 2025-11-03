package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.RoomDTO;
import com.dcm.demo.mapper.RoomMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Room;
import com.dcm.demo.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;
    private final RoomMapper mapper;
    @Override
    public Page<RoomDTO> findAll(Pageable pageable, String keyword, List<Integer> departmentId) {

        return repository.findAll(pageable, departmentId, keyword).map(this::toDto);
    }

    @Override
    public RoomDTO findById(Integer id) {
        return this.toResponse(
                repository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"))
        );
    }

    @Override
    public RoomDTO create(RoomDTO roomDTO) {
        Room room = mapper.toEntity(roomDTO);
        Department department = departmentRepository.findById(roomDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
        room.setDepartment(department);
        return this.toResponse(repository.save(room));
    }

    @Override
    public RoomDTO update(RoomDTO roomDTO) {
        Room room = repository.findById(roomDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Department department = departmentRepository.findById(roomDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
        room.setDepartment(department);
        room.setRoomName(roomDTO.getRoomName());
        room.setRoomNumber(roomDTO.getRoomNumber());

        return this.toResponse(repository.save(room));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private RoomDTO toDto(Room room) {
        RoomDTO roomDTO = mapper.toResponse(room);
        roomDTO.setDepartmentName(room.getDepartment().getName());
        return roomDTO;
    }

    private RoomDTO toResponse(Room room) {
        RoomDTO response = mapper.toResponse(room);
        response.setDepartmentId(room.getDepartment().getId());
        response.setDepartmentName(room.getDepartment().getName());
        return response;
    }
}

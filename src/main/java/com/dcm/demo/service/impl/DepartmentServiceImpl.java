package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.DepartmentRequest;
import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.DepartmentMapper;
import com.dcm.demo.mapper.DoctorMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Room;
import com.dcm.demo.repository.DepartmentRepository;
import com.dcm.demo.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;
    private final DoctorMapper doctorMapper;

    @Override
    public List<DepartmentResponse> getAllDepartment() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Page<DepartmentResponse> getAllDepartment(Pageable pageable, String keyword) {
        Specification<Department> spec = FilterHelper.contain(keyword, List.of("name"));
        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Override
    public Department findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<DepartmentResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DoctorResponse> findDoctorByDepartments(Integer id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department Not Found"));
        Room room = department.getRooms().stream()
                .filter(it -> it.getRoomNumber().contains("A"))
                .findFirst()
                .orElse(null);
        return department.getDoctors().stream()
                .map(it -> {
                    DoctorResponse response = new DoctorResponse();
                    response.setId(it.getId());
                    response.setPosition(it.getPosition());
                    if (room != null) {
                        response.setRoomNumber(room.getRoomNumber());
                        response.setRoomName(room.getRoomName());
                    }

                    response.setExaminationFee(it.getDegree().getExaminationFee());

                    return response;
                })
                .toList();
    }

    @Override
    public String getRoomFromDepartment(Department department) {
        Room room = department.getRooms().stream()
                .filter(it -> it.getRoomNumber().contains("A"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng khám"));
        return room.getRoomName() + " - " + room.getRoomNumber();
    }

    @Override
    public DepartmentResponse findResponseById(Integer id) {
        return null;
    }

    @Override
    public DepartmentResponse create(DepartmentRequest request) {
        return null;
    }

    @Override
    public DepartmentResponse update(DepartmentRequest request) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}

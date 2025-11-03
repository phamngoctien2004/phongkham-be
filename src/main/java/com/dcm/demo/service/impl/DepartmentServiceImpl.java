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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;
    private final DoctorMapper doctorMapper;

    @Override
    public Page<DepartmentResponse> getAllDepartment(Pageable pageable, String keyword) {
        Specification<Department> spec = FilterHelper.contain(keyword, List.of("name"));
        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Override
    @Cacheable(value = "departments", key = "#id")
    public DepartmentResponse findById(Integer id) {
        log.info("Find Department by ID: {}", id);
        return mapper.toResponse(
                repository.findById(id).orElseThrow(() -> new RuntimeException("Department Not Found"))
        );
    }

    @Override
    @Cacheable(value = "departments", key = "'all'")
    public List<DepartmentResponse> findAll() {
        log.info("Find All Departments");
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
    @CacheEvict(value = "departments", key = "'all'")
    @CachePut(value = "departments", key = "#result.id")
    public DepartmentResponse create(DepartmentRequest request) {
        Department department = mapper.toEntity(request);
        return mapper.toResponse(repository.save(department));
    }

    @Override
    @CachePut(value = "departments", key = "#request.id")
    @CacheEvict(value = "departments", key = "'all'")
    public DepartmentResponse update(DepartmentRequest request) {
        Department department = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Department Not Found"));
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setPhone(request.getPhone());
        return mapper.toResponse(repository.save(department));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "departments", key = "'all'"),
                    @CacheEvict(value = "departments", key = "#id")
            }
    )
    public void delete(Integer id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department Not Found"));
        repository.delete(department);
    }
}

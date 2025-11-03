package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.DoctorRequest;
import com.dcm.demo.dto.response.DegreeResponse;
import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.ProfileData;
import com.dcm.demo.mapper.DoctorMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.DegreeRepository;
import com.dcm.demo.repository.DepartmentRepository;
import com.dcm.demo.repository.DoctorRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.ProfileLoader;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService, ProfileLoader {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository repository;
    private final DegreeRepository degreeRepository;
    private final DoctorMapper mapper;
    private final DepartmentRepository departmentRepository;

    @Override
    public Doctor findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(value = "doctors", key = "'all'")
    public List<DoctorResponse> findAll() {
        log.info("Fetching all doctors from database");
        return repository.findAllByOrderByDepartmentIdAscDegreeExaminationFeeDesc().stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public Page<DoctorResponse> findAllByPage(Pageable pageable, String keyword, Integer departmentId, Integer degreeId) {

        return repository.findAll(pageable, keyword, departmentId, degreeId)
                .map(this::buildResponse);
    }

    @Override
    @Cacheable(value = "doctors", key = "#id")
    public DoctorResponse findResponseById(Integer id) {
        log.info("Fetching doctors from database with id: {}", id);
        return repository.findById(id)
                .map(this::buildResponse)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @Override
    @CacheEvict(value = "doctors", key = "'all'")
    @CachePut(value = "doctors", key = "#result.id")
    @Transactional(rollbackFor = Exception.class)
    public DoctorResponse create(DoctorRequest doctorRequest) {
        Doctor doctor = mapper.toEntity(doctorRequest);

        Integer degreeId = doctorRequest.getDegreeId();
        Integer departmentId = doctorRequest.getDepartmentId();

        Degree degree = degreeRepository.findById(degreeId).orElse(null);
        doctor.setDegree(degree);
        assert degree != null;
        doctor.setPosition(degree.getCode() + ". " + doctor.getFullName());

        Department department = departmentRepository.findById(departmentId).orElse(null);
        doctor.setDepartment(department);

        User user = new User();
        user.setEmail(doctorRequest.getEmail());
        user.setName(doctorRequest.getFullName());
        user.setPhone(doctorRequest.getPhone());
        user.setRole(User.Role.BAC_SI);
        user.setPassword(passwordEncoder.encode(doctorRequest.getPhone()));
        user.setDoctor(doctor);
        doctor.setUser(user);

        return buildResponse(repository.save(doctor));
    }

    @Override
    @CachePut(value = "doctors", key = "#doctorRequest.id")
    @CacheEvict(value = "doctors", key = "'all'")
    public DoctorResponse update(DoctorRequest doctorRequest) {
        Doctor doctor = repository.findById(doctorRequest.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        buildDoctor(doctor, doctorRequest);
        return buildResponse(repository.save(doctor));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "doctors", key = "'all'"),
                    @CacheEvict(value = "doctors", key = "#id")
            }
    )
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public boolean supports(User.Role role) {
        return true;
    }

    @Override
    public Optional<? extends ProfileData> loadProfile() {
        User user = userService.getCurrentUser();
        return user.getDoctor() != null
                ? Optional.ofNullable(mapper.toResponse(user.getDoctor()))
                : Optional.empty();
    }

    private DoctorResponse buildResponse(Doctor doctor) {
        Department department = doctor.getDepartment();
        Degree degree = doctor.getDegree();

        DoctorResponse response = mapper.toResponse(doctor);
        response.setId(doctor.getId());
        response.setFullName(doctor.getFullName());
        response.setPosition(doctor.getPosition());


        if (department.getRooms() != null) {
            Room room = department.getRooms().stream()
                    .filter(r -> r.getRoomNumber().contains("A"))
                    .findFirst()
                    .orElse(null);
            if (room != null) {
                response.setRoomNumber(room.getRoomNumber());
                response.setRoomName(room.getRoomName());
            }
            DepartmentResponse departmentResponse = new DepartmentResponse();
            departmentResponse.setId(department.getId());
            departmentResponse.setName(department.getName());
            response.setDepartmentResponse(departmentResponse);
        }
        if (degree.getDegreeName() != null) {
            DegreeResponse degreeResponse = new DegreeResponse();
            degreeResponse.setDegreeId(degree.getDegreeId());
            degreeResponse.setDegreeName(degree.getDegreeName());
            degreeResponse.setExaminationFee(degree.getExaminationFee());
            response.setDegreeResponse(degreeResponse);
            response.setExaminationFee(degree.getExaminationFee());
        }

        return response;
    }

    private void buildDoctor(Doctor doctor, DoctorRequest request) {
        doctor.setPhone(request.getPhone());
        doctor.setAddress(request.getAddress());
        doctor.setBirth(request.getBirth());
        doctor.setGender(request.getGender());
        doctor.setFullName(request.getFullName());
        doctor.setExp(request.getExp());

        Degree degree = degreeRepository.findById(request.getDegreeId()).orElse(null);
        doctor.setDegree(degree);
        assert degree != null;
        doctor.setPosition(degree.getCode() + ". " + doctor.getFullName());

        Department department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        doctor.setDepartment(department);
    }
}

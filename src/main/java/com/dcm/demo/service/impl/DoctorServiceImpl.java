package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DegreeResponse;
import com.dcm.demo.dto.response.DepartmentResponse;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.ProfileData;
import com.dcm.demo.mapper.DoctorMapper;
import com.dcm.demo.model.*;
import com.dcm.demo.repository.DoctorRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.ProfileLoader;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService, ProfileLoader {
    private final UserService userService;
    private final DoctorRepository repository;
    private final DoctorMapper mapper;

    @Override
    public Doctor findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DoctorResponse> findAll() {
        return repository.findAllByOrderByDepartmentIdAscDegreeExaminationFeeDesc().stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public DoctorResponse findResponseById(Integer id) {
        return repository.findById(id)
                .map(this::buildResponse)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
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
        Room room = department.getRooms().stream()
                .filter(r -> r.getRoomNumber().contains("A"))
                .findFirst()
                .orElse(null);
        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setFullName(doctor.getFullName());
        response.setPosition(doctor.getPosition());

        response.setExaminationFee(doctor.getDegree().getExaminationFee());
        if (room != null) {
            response.setRoomNumber(room.getRoomNumber());
            response.setRoomName(room.getRoomName());
        }
        Degree degree = doctor.getDegree();
        DegreeResponse degreeResponse = new DegreeResponse();
        degreeResponse.setDegreeId(degree.getDegreeId());
        degreeResponse.setDegreeName(degree.getDegreeName());
        degreeResponse.setExaminationFee(degree.getExaminationFee());
        response.setDegreeResponse(degreeResponse);

        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(department.getId());
        departmentResponse.setName(department.getName());
        response.setDepartmentResponse(departmentResponse);
        return response;
    }
}

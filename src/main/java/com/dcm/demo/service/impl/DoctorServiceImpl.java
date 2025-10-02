package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.ProfileData;
import com.dcm.demo.mapper.DoctorMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Room;
import com.dcm.demo.model.User;
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
        if (room != null) {
            response.setRoomNumber(room.getRoomNumber());
            response.setRoomName(room.getRoomName());
        }
        return response;
    }
}

package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.mapper.DoctorMapper;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Room;
import com.dcm.demo.repository.DoctorRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository repository;
    private final DoctorMapper mapper;

    @Override
    public Doctor findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DoctorResponse> findAll() {
        List<DoctorResponse> doctors = repository.findAllByOrderByDepartmentIdAscDegreeExaminationFeeDesc().stream()
                .map(it -> {
                    Department department = it.getDepartment();
                    Room room = department.getRooms().stream()
                            .filter(r -> r.getRoomNumber().contains("A"))
                            .findFirst()
                            .orElse(null);
                    DoctorResponse response = new DoctorResponse();
                    response.setId(it.getId());
                    response.setFullName(it.getFullName());
                    response.setPosition(it.getPosition());
                    if(room != null) {
                        response.setRoomNumber(room.getRoomNumber());
                        response.setRoomName(room.getRoomName());
                    }
                    return response;
                })
                .toList();;

        return doctors;
    }


}

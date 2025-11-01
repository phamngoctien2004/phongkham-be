package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.DoctorRequest;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {
    Doctor findById(Integer id);
    List<DoctorResponse> findAll();
    Page<DoctorResponse> findAllByPage(Pageable pageable, String keyword, Integer departmentId, Integer degreeId);
    DoctorResponse findResponseById(Integer id);
    DoctorResponse create(DoctorRequest doctorRequest);
    DoctorResponse update(DoctorRequest doctorRequest);
    void delete(Integer id);
}

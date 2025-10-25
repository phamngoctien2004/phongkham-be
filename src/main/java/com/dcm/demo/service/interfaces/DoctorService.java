package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor findById(Integer id);
    List<DoctorResponse> findAll();
    DoctorResponse findResponseById(Integer id);
}

package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.model.MedicalRecord;

public interface MedicalRecordService {
    void create(MedicalRequest request);
    void update(MedicalRequest request);

    MedicalRecord findById(Integer id);
}

package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.MedicalRequest;

public interface MedicalRecordService {
    void create(MedicalRequest request);
    void update(MedicalRequest request);


}

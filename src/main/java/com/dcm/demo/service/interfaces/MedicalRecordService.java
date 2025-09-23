package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.response.MedicalResponse;
import com.dcm.demo.model.MedicalRecord;

import java.util.List;

public interface MedicalRecordService {
    void create(MedicalRequest request);
    void update(MedicalRequest request);
    List<MedicalResponse> me();
    List<MedicalResponse> getRelationMedicalRecord(String cccd);
    MedicalRecord findById(Integer id);
}

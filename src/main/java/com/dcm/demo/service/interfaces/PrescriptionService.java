package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.PresDetailRequest;
import com.dcm.demo.dto.request.PrescriptionRequest;
import com.dcm.demo.dto.response.PreDetailResponse;
import com.dcm.demo.dto.response.PrescriptionResponse;

import java.util.List;

public interface PrescriptionService {
    PrescriptionResponse getPrescriptionsByMedicalRecordId(Integer medicalRecordId);
    PrescriptionResponse create(PrescriptionRequest request);
    PrescriptionResponse update(PrescriptionRequest request);


    PreDetailResponse createPreDetail(PresDetailRequest request);
    PreDetailResponse updatePreDetail(PresDetailRequest request);
    void deletePreDetail(Integer id);
}

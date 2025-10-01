package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.MedicalRecord;

import java.util.List;

public interface LabOrderService {
    List<LabOrderResponse> getAll();
    LabOrderResponse findByRecordCode(String code);
    LabOrderResponse buildResponse(LabOrder labOrder);
    LabOrder findById(Integer id);
    LabOrderResponse findResponseById(Integer id);
    void createLabOrder(LabOrderRequest request);
    void updateLabOrder(LabOrderRequest request);
    void createLabOrderFromHealthPlan(MedicalRecord medicalRecord, Integer healthPlanId);
}

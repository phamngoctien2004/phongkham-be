package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.MedicalRequest;
import com.dcm.demo.dto.request.WebhookRequest;
import com.dcm.demo.dto.response.MedicalResponse;
import com.dcm.demo.model.MedicalRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MedicalRecordService {
    MedicalRecord create(MedicalRequest request);
    void updatePaymentForLabOrder(MedicalRequest.UpdatePaymentRequest request);
    void updateMedicalRecordInvoiceForCash(MedicalRequest.UpdatePaymentRequest request);
    void update(MedicalRequest request);
    void updateStatus(Integer id, MedicalRecord.RecordStatus status);
    byte[] exportPdf(Integer id);
    List<MedicalResponse> me();
    List<MedicalResponse> getRelationMedicalRecord(String cccd);
    void updateTotal(MedicalRecord medicalRecord, BigDecimal total);
    MedicalRecord findById(Integer id);
    MedicalResponse getDetailById(Integer id);
    List<MedicalResponse> findAll(String keyword, MedicalRecord.RecordStatus status, LocalDate date);
    void webhookPayosForCheckStatus(WebhookRequest request);

}

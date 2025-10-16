package com.dcm.demo.service.impl;

import com.dcm.demo.Utils.ConvertUtil;
import com.dcm.demo.dto.request.PresDetailRequest;
import com.dcm.demo.dto.request.PrescriptionRequest;
import com.dcm.demo.dto.response.PreDetailResponse;
import com.dcm.demo.dto.response.PrescriptionResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.model.Medicine;
import com.dcm.demo.model.Prescription;
import com.dcm.demo.model.PrescriptionDetail;
import com.dcm.demo.repository.MedicalRecordRepository;
import com.dcm.demo.repository.MedicineRepository;
import com.dcm.demo.repository.PrescriptionDetailRepository;
import com.dcm.demo.repository.PrescriptionRepository;
import com.dcm.demo.service.interfaces.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository repository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionDetailRepository pdRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public PrescriptionResponse getPrescriptionsByMedicalRecordId(Integer medicalRecordId) {
        return ConvertUtil.convert(
                repository.findByMedicalRecordId(medicalRecordId)
                        .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND))
        );
    }

    @Override
    public PrescriptionResponse create(PrescriptionRequest request) {
        Prescription prescription = new Prescription();
        prescription.setCode("DT" + System.currentTimeMillis() / 1000);
        prescription.setGeneralInstructions(request.getGeneralInstructions());

        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        prescription.setMedicalRecord(medicalRecord);
        prescription.setDoctorCreated(medicalRecord.getDoctor().getFullName());
        return ConvertUtil.convert(repository.save(prescription));
    }

    @Override
    public PrescriptionResponse update(PrescriptionRequest request) {
        Prescription prescription = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        prescription.setGeneralInstructions(request.getGeneralInstructions());

////      them chi tiet don thuoc (neu co)
//        List<PrescriptionDetail> prescriptionDetails = prescription.getPrescriptionDetails();
//        request.getPresDetail().forEach(detail -> {
//            prescriptionDetails.add(ConvertUtil.convert(detail));x
//        });

        return ConvertUtil.convert(repository.save(prescription));
    }

    @Override
    public PreDetailResponse createPreDetail(PresDetailRequest request) {
        PrescriptionDetail prescriptionDetail = ConvertUtil.convert(request);
        Prescription prescription = repository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        prescriptionDetail.setMedicine(medicine);
        prescriptionDetail.setPrescription(prescription);
        return ConvertUtil.convert(pdRepository.save(prescriptionDetail));
    }

    @Override
    public PreDetailResponse updatePreDetail(PresDetailRequest request) {
        PrescriptionDetail prescriptionDetail = pdRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Prescription detail not found"));
        prescriptionDetail.setUsageInstructions(request.getUsageInstructions());
        prescriptionDetail.setQuantity(request.getQuantity());
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        medicine.setId(request.getMedicineId());
        prescriptionDetail.setMedicine(medicine);
        return ConvertUtil.convert(pdRepository.save(prescriptionDetail));
    }

    @Override
    public void deletePreDetail(Integer id) {
        pdRepository.deleteById(id);
    }

}

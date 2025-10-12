package com.dcm.demo.Utils;

import com.dcm.demo.dto.request.PresDetailRequest;
import com.dcm.demo.dto.response.InvoiceDetailResponse;
import com.dcm.demo.dto.response.MedicineResponse;
import com.dcm.demo.dto.response.PreDetailResponse;
import com.dcm.demo.dto.response.PrescriptionResponse;
import com.dcm.demo.model.*;

import java.util.List;

public class ConvertUtil {
    public static InvoiceDetailResponse.LabDetail convert(LabOrder labOrder) {
        InvoiceDetailResponse.LabDetail labDetail = new InvoiceDetailResponse.LabDetail();
        labDetail.setId(labOrder.getId());
        labDetail.setCode(labOrder.getCode());
        if (labOrder.getPerformingDoctor() != null) {
            labDetail.setDoctorPerforming(labOrder.getPerformingDoctor().getFullName());
        }
        labDetail.setRoom(labOrder.getRoom());
        labDetail.setCreatedAt(labOrder.getOrderDate());
        labDetail.setStatus(labOrder.getStatus().toString());
        return labDetail;
    }

    public static InvoiceDetailResponse convert(InvoiceDetail detail) {
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        response.setId(detail.getId());
        response.setHealthPlanId(detail.getHealthPlan().getId());
        response.setHealthPlanName(detail.getHealthPlan().getName());
        response.setHealthPlanPrice(detail.getFee().intValue());
        response.setPaid(detail.getPaidAmount().intValue());
        response.setDescription(detail.getDescription());
        response.setStatus(detail.getStatus().toString());
        return response;
    }

    public static PrescriptionResponse convert(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setCode(prescription.getCode());
        response.setGeneralInstructions(prescription.getGeneralInstructions());
        response.setDoctorCreated(prescription.getDoctorCreated());
        response.setPrescriptionDate(prescription.getPrescriptionDate());

        List<PrescriptionDetail> details = prescription.getPrescriptionDetails();
        List<PreDetailResponse> detailResponses = details.stream().map(detail -> {
            PreDetailResponse detailResponse = new PreDetailResponse();
            detailResponse.setId(detail.getId());
            detailResponse.setMedicineResponse(ConvertUtil.convert(detail.getMedicine()));
            detailResponse.setQuantity(detail.getQuantity());
            detailResponse.setUsageInstructions(detail.getUsageInstructions());
            return detailResponse;
        }).toList();

        response.setDetailResponses(detailResponses);
        return response;
    }

    public static MedicineResponse convert(Medicine medicine) {
        MedicineResponse response = new MedicineResponse();
        response.setId(medicine.getId());
        response.setName(medicine.getName());
        response.setDescription(medicine.getDescription());
        response.setConcentration(medicine.getConcentration());
        response.setDosageForm(medicine.getDosageForm());
        response.setUnit(medicine.getUnit());
        return response;
    }
    public static PreDetailResponse convert(PrescriptionDetail detail){
        PreDetailResponse response = new PreDetailResponse();
        response.setId(detail.getId());
        response.setQuantity(detail.getQuantity());
        response.setUsageInstructions(detail.getUsageInstructions());
        response.setMedicineResponse(convert(detail.getMedicine()));
        return response;
    }
    public static PrescriptionDetail convert(PresDetailRequest request){
        PrescriptionDetail detail = new PrescriptionDetail();
        detail.setId(request.getId());
        detail.setQuantity(request.getQuantity());
        detail.setUsageInstructions(request.getUsageInstructions());

        Medicine medicine = new Medicine();
        medicine.setId(request.getMedicineId());
        detail.setMedicine(medicine);
        return detail;
    }
}

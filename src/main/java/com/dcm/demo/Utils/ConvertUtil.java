package com.dcm.demo.Utils;

import com.dcm.demo.dto.response.InvoiceDetailResponse;
import com.dcm.demo.model.InvoiceDetail;
import com.dcm.demo.model.LabOrder;

public class ConvertUtil {
    public static InvoiceDetailResponse.LabDetail convert(LabOrder labOrder) {
        InvoiceDetailResponse.LabDetail labDetail = new InvoiceDetailResponse.LabDetail();
        labDetail.setId(labOrder.getId());
        labDetail.setCode(labOrder.getCode());
        if(labOrder.getPerformingDoctor() != null) {
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
}

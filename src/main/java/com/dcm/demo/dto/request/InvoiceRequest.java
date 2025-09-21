package com.dcm.demo.dto.request;

import com.dcm.demo.model.Invoice;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {
    private Integer id;
    private Integer medicalRecordId;
    private Invoice.PaymentMethod paymentMethod;
    private List<Integer> healthPlanIds;
    private Integer doctorId;
    private Long orderCode;
}

package com.dcm.demo.dto.response;

import com.dcm.demo.model.Invoice;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceResponse {
    private Integer id;
    private String code;
    private String paymentMethod;
    private Integer totalAmount;
    private Invoice.PaymentStatus status;

}

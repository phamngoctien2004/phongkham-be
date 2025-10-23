package com.dcm.demo.dto.response;

import com.dcm.demo.model.Invoice;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceResponse {
    private Integer id;
    private String code;
    private String paymentMethod;
    private Integer totalAmount;
    private Integer paidAmount;
    private Invoice.PaymentStatus status;
    private Integer examFee;
    private LocalDateTime date;
}

package com.dcm.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Integer invoiceId;
    private String qrCode;
    private Long orderCode;
}

package com.dcm.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.payos.type.CheckoutResponseData;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Integer invoiceId;
    private String qrCode;
    private Long orderCode;
}

package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.InvoiceResponse;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);
    void updateStatusPayment(Long invoiceId, String codeStatus, Long amount);
    boolean checkStatusPayment(Integer invoiceId);
}

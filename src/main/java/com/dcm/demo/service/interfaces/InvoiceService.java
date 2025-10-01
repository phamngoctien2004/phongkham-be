package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.MedicalRecord;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {
    Invoice findById(Integer id);
    InvoiceResponse createInvoice(InvoiceRequest request);
    void createDetailInvoice(MedicalRecord record);
    void updateStatusPayment(Long invoiceId, String codeStatus, Long amount);
    void updateTotalAmount(Integer recordId, BigDecimal amount);
    boolean checkStatusPayment(Integer invoiceId);
    List<Integer> getServicesUnPay(Integer recordId);
    void save(Invoice invoice);
}

package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.FilterRequest;
import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.model.Invoice;
import com.dcm.demo.model.InvoiceDetail;
import com.dcm.demo.model.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    Page<InvoiceResponse> findAll(String keyword, Invoice.PaymentStatus status, Invoice.PaymentMethod method, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    Invoice findById(Integer id);
    Invoice findByPayosOrder(Long orderCode);
    Invoice findByCode(String code);
    InvoiceResponse createInvoiceForQR(InvoiceRequest request);
    void createInvoiceForCash(MedicalRecord record);
    void updateTotal(Invoice invoice, BigDecimal amount);
    void updatePaidAmount(Invoice invoice, BigDecimal amount);
    boolean checkStatusPayment(Integer invoiceId);
    List<Integer> getServicesUnPay(Integer recordId);
    void save(Invoice invoice);
    void buildInvoiceDetail(Invoice invoice, Integer healthPlanId, BigDecimal price, InvoiceDetail.Status status, Invoice.PaymentMethod paymentMethod, BigDecimal paymentAmount);
}

package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.HtmlExportDto;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.model.*;
import com.dcm.demo.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportHtmlServiceImpl implements ExportHtmlService {
    private final TemplateEngine templateEngine;
    private final MedicalRecordService medicalRecordService;
    private final DepartmentService departmentService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    @Override
    public String exportMedicalRecordHtml(Integer id) {
        MedicalRecord medicalRecord = medicalRecordService.findById(id);
        Patient patient = medicalRecord.getPatient();
        Doctor doctor = medicalRecord.getDoctor();

        Map<String, Object> params = new LinkedHashMap<>();

        params.put("patientName", patient.getFullName());
        params.put("patientGender", patient.getGender() != null ? patient.getGender().toString() : "");
        params.put("patientAddress", patient.getAddress());
        params.put("patientPhone", patient.getPhone());
        params.put("patientBirthDate", patient.getBirth());
        params.put("insuranceNumber", "1023888519");

        params.put("appointmentDate", medicalRecord.getDate().toLocalDate());
        params.put("appointmentTime", medicalRecord.getDate().toLocalTime());

        params.put("doctorName", doctor.getFullName());
        params.put("roomName", departmentService.getRoomFromDepartment(doctor.getDepartment()));
        params.put("departmentName", doctor.getDepartment().getName());

        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(locale);
        String formattedFee = currencyVN.format(medicalRecord.getFee()); // ví dụ examFee = 150000 → "150.000 ₫"

        params.put("examFee", formattedFee);
        Context ctx = new Context();
        ctx.setVariables(params);

        return templateEngine.process("pdfs/phieukham.html", ctx);
    }

    @Override
    public String exportInvoiceHtml(Integer medicalRecordId) {
        MedicalRecord medicalRecord = medicalRecordService.findById(medicalRecordId);
        Invoice invoice = medicalRecord.getInvoice();
        Patient patient = medicalRecord.getPatient();
        Doctor doctor = medicalRecord.getDoctor();

        Map<String, Object> params = new LinkedHashMap<>();

        params.put("patientName", patient.getFullName());
        params.put("patientGender", patient.getGender() != null ? patient.getGender().toString() : "");
        params.put("patientAddress", patient.getAddress());
        params.put("patientPhone", patient.getPhone());
        params.put("patientCode", patient.getCode());
        params.put("patientBirthDate", patient.getBirth());
        params.put("insuranceNumber", "1023888519");

        List<HtmlExportDto> htmlExportDTOs = new ArrayList<>();
        invoice.getInvoiceDetails().forEach(invoiceDetail -> {
            HtmlExportDto htmlExportDto = new HtmlExportDto();
            htmlExportDto.setName(invoiceDetail.getDescription());
            htmlExportDto.setUnit("1");
            htmlExportDto.setQuantity(1);
            htmlExportDto.setPrice(invoiceDetail.getFee());
            htmlExportDto.setTotal(invoiceDetail.getFee());
            htmlExportDTOs.add(htmlExportDto);
        });
        params.put("medicalServices", htmlExportDTOs);
        params.put("subtotal", invoice.getTotalAmount());
        params.put("totalAmount", invoice.getTotalAmount());
        params.put("medicalServicesTotal", invoice.getTotalAmount());

        params.put("paidAmount", invoice.getPaidAmount());
        params.put("remainingAmount", invoice.getTotalAmount().subtract(invoice.getPaidAmount()));

        params.put("insuranceRate", 0);
        params.put("insuranceAmount", BigDecimal.ZERO);

        params.put("discountPercent", 0);
        params.put("discount", BigDecimal.ZERO);
        params.put("paymentMethod", invoice.getPaymentMethod().toString());
        User user = userService.getCurrentUser();
        params.put("cashierName", user != null ? user.getName() : "Le tan");
        Context ctx = new Context();
        ctx.setVariables(params);

        return templateEngine.process("pdfs/hoadon.html", ctx);
    }



}

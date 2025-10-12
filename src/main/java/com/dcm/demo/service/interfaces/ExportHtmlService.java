package com.dcm.demo.service.interfaces;

public interface ExportHtmlService {
    String exportMedicalRecordHtml(Integer id);
    String exportInvoiceHtml(Integer medicalRecordId);

}

package com.dcm.demo.controller;

import com.dcm.demo.service.interfaces.ExportHtmlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/html")
@RequiredArgsConstructor
public class HtmlController {
    private final ExportHtmlService exportHtmlService;

    @GetMapping("/medical-record/{id}")
    public ResponseEntity<?> getMedicalRecordPdf(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(exportHtmlService.exportMedicalRecordHtml(id));
    }

    @GetMapping("/invoice/{medicalRecordId}")
    public ResponseEntity<?> getInvoicePdf(@PathVariable Integer medicalRecordId) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(exportHtmlService.exportInvoiceHtml(medicalRecordId));
    }
}

package com.cyber.playdetector.controller;

import com.cyber.playdetector.service.ReportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> downloadPdfReport() throws IOException {
        ByteArrayInputStream bis = reportService.generatePdfReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=app_risk_audit_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/csv")
    public ResponseEntity<String> downloadCsvReport() {
        String csvContent = reportService.generateCsvReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=app_risk_audit_report.csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent);
    }
}

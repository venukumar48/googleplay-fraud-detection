package com.cyber.playdetector.service;

import com.cyber.playdetector.model.App;
import com.cyber.playdetector.repository.AppRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final AppRepository appRepository;

    public ReportService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public ByteArrayInputStream generatePdfReport() throws IOException {
        List<App> apps = appRepository.findAll();
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Document Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Google Play App Search Fraud & Malware Detection System", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph timestamp = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + 
                    " | Classification: RESTRICTED", subFont);
            timestamp.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(timestamp);
            document.add(new Paragraph(" "));

            Paragraph intro = new Paragraph("This audit document reports the computed security posture and risk indices for registered mobile applications. Flags indicate potential search rank fraud or embedded malicious behaviors.");
            document.add(intro);
            document.add(new Paragraph(" "));

            // PDF Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.0f, 2.5f, 1.2f, 1.2f, 1.2f, 1.5f});

            Font tableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            table.addCell(new Paragraph("App Title", tableHeader));
            table.addCell(new Paragraph("Package Name", tableHeader));
            table.addCell(new Paragraph("Fraud Score", tableHeader));
            table.addCell(new Paragraph("Malware Score", tableHeader));
            table.addCell(new Paragraph("Risk Score", tableHeader));
            table.addCell(new Paragraph("Level", tableHeader));

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (App app : apps) {
                table.addCell(new Paragraph(app.getTitle(), cellFont));
                table.addCell(new Paragraph(app.getPackageName(), cellFont));
                table.addCell(new Paragraph(String.format("%.2f", app.getFraudResult() != null ? app.getFraudResult().getFraudProbability() : 0.0), cellFont));
                table.addCell(new Paragraph(String.format("%.2f", app.getMalwareResult() != null ? app.getMalwareResult().getMalwareProbability() : 0.0), cellFont));
                table.addCell(new Paragraph(String.format("%.2f", app.getOverallRiskScore()), cellFont));
                table.addCell(new Paragraph(app.getRiskLevel(), cellFont));
            }

            document.add(table);
            
            document.add(new Paragraph(" "));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);
            Paragraph footer = new Paragraph("Disclaimer: Calculations are executed via dynamic heuristics and ML anomalies.", footerFont);
            document.add(footer);

            document.close();
        } catch (Exception ex) {
            System.err.println("PDF generation error: " + ex.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public String generateCsvReport() {
        List<App> apps = appRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,PackageName,Title,Downloads,Rating,RatingCount,FraudProbability,MalwareProbability,OverallRiskScore,RiskLevel\n");

        for (App app : apps) {
            csv.append(app.getId()).append(",")
               .append("\"").append(app.getPackageName()).append("\",")
               .append("\"").append(app.getTitle().replace("\"", "\"\"")).append("\",")
               .append(app.getDownloads()).append(",")
               .append(app.getRating()).append(",")
               .append(app.getRatingCount()).append(",")
               .append(app.getFraudResult() != null ? app.getFraudResult().getFraudProbability() : 0.0).append(",")
               .append(app.getMalwareResult() != null ? app.getMalwareResult().getMalwareProbability() : 0.0).append(",")
               .append(app.getOverallRiskScore()).append(",")
               .append(app.getRiskLevel()).append("\n");
        }

        return csv.toString();
    }
}

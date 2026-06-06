package com.cyber.playdetector.service;

import com.cyber.playdetector.dto.AppScanDto;
import com.cyber.playdetector.dto.MlScanRequest;
import com.cyber.playdetector.dto.MlScanResponse;
import com.cyber.playdetector.model.*;
import com.cyber.playdetector.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppAnalysisService {

    private final AppRepository appRepository;
    private final FraudResultRepository fraudResultRepository;
    private final MalwareResultRepository malwareResultRepository;
    private final ReviewRepository reviewRepository;
    private final AuditLogRepository auditLogRepository;
    private final MlIntegrationService mlIntegrationService;

    public AppAnalysisService(
            AppRepository appRepository,
            FraudResultRepository fraudResultRepository,
            MalwareResultRepository malwareResultRepository,
            ReviewRepository reviewRepository,
            AuditLogRepository auditLogRepository,
            MlIntegrationService mlIntegrationService) {
        this.appRepository = appRepository;
        this.fraudResultRepository = fraudResultRepository;
        this.malwareResultRepository = malwareResultRepository;
        this.reviewRepository = reviewRepository;
        this.auditLogRepository = auditLogRepository;
        this.mlIntegrationService = mlIntegrationService;
    }

    public List<App> getAllApps() {
        return appRepository.findAll();
    }

    public Optional<App> getAppById(Long id) {
        return appRepository.findById(id);
    }

    public App scanAndSaveApp(AppScanDto dto, String operator) {
        // Find existing app or create a new one
        App app = appRepository.findByPackageName(dto.getPackageName())
                .orElse(new App());

        app.setPackageName(dto.getPackageName());
        app.setTitle(dto.getTitle());
        app.setDownloads(dto.getDownloads());
        app.setRating(dto.getRating());
        app.setRatingCount(dto.getRatingCount());
        app.setPrice(dto.getPrice());
        app.setPermissions(dto.getPermissions());

        // Save app first to generate IDs
        app = appRepository.save(app);

        // Delete existing reviews to reload
        app.getReviews().clear();

        App finalApp = app;
        List<Review> reviews = dto.getReviews().stream()
                .map(r -> new Review(finalApp, r.getAuthor(), r.getText(), r.getRating()))
                .collect(Collectors.toList());

        app.getReviews().addAll(reviews);

        // Call python ML microservice
        MlScanRequest mlRequest = new MlScanRequest();
        mlRequest.setPackage_name(dto.getPackageName());
        mlRequest.setTitle(dto.getTitle());
        mlRequest.setDownloads(dto.getDownloads());
        mlRequest.setRating(dto.getRating());
        mlRequest.setRating_count(dto.getRatingCount());
        mlRequest.setPrice(dto.getPrice());
        mlRequest.setPermissions(dto.getPermissions());
        mlRequest.setReviews(dto.getReviews().stream()
                .map(r -> new MlScanRequest.MlReviewInput(r.getAuthor(), r.getText(), r.getRating()))
                .collect(Collectors.toList()));

        MlScanResponse mlResponse = mlIntegrationService.scanApp(mlRequest);

        // Update App risk scores
        app.setOverallRiskScore(mlResponse.getOverall_risk_score());
        app.setRiskLevel(mlResponse.getRisk_level());

        // Update Fraud details
        FraudResult fraud = app.getFraudResult();
        if (fraud == null) {
            fraud = new FraudResult();
            fraud.setApp(app);
        }
        fraud.setDownloadSpikeFlag(mlResponse.getFraud_details().isDownload_spike_flag());
        fraud.setRankManipulationFlag(mlResponse.getFraud_details().isRank_manipulation_flag());
        fraud.setRatingAnomaly(mlResponse.getFraud_details().isRating_anomaly());
        fraud.setReviewSentimentAnomaly(mlResponse.getFraud_details().isReview_sentiment_anomaly());
        fraud.setFraudProbability(mlResponse.getFraud_details().getFraud_probability());
        app.setFraudResult(fraud);

        // Update Malware details
        MalwareResult malware = app.getMalwareResult();
        if (malware == null) {
            malware = new MalwareResult();
            malware.setApp(app);
        }
        malware.setFlaggedPermissions(mlResponse.getMalware_details().getDangerous_permissions());
        malware.setSuspiciousBehaviors(mlResponse.getMalware_details().getSuspicious_behaviors());
        malware.setMalwareProbability(mlResponse.getMalware_details().getMalware_probability());
        app.setMalwareResult(malware);

        // Log to Audit table
        auditLogRepository.save(new AuditLog(operator, "APP_SCAN", 
                "Successfully analyzed package " + app.getPackageName() + " with overall risk score " + app.getOverallRiskScore()));

        return appRepository.save(app);
    }

    public int parseAndProcessCsv(InputStream is, String operator) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String headerLine = reader.readLine();
        if (headerLine == null) {
            throw new IllegalArgumentException("Uploaded CSV is empty");
        }

        int count = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            // Basic CSV splitter that respects quotes
            List<String> tokens = parseCsvLine(line);
            if (tokens.size() < 7) continue;

            try {
                AppScanDto dto = new AppScanDto();
                dto.setPackageName(tokens.get(0).replace("\"", "").trim());
                dto.setTitle(tokens.get(1).replace("\"", "").trim());
                dto.setDownloads(Integer.parseInt(tokens.get(2).replace("\"", "").trim()));
                dto.setRating(Double.parseDouble(tokens.get(3).replace("\"", "").trim()));
                dto.setRatingCount(Integer.parseInt(tokens.get(4).replace("\"", "").trim()));
                dto.setPrice(Double.parseDouble(tokens.get(5).replace("\"", "").trim()));

                // Permissions split by semicolon
                String permStr = tokens.get(6).replace("\"", "").trim();
                List<String> permissions = permStr.isEmpty() ? new ArrayList<>() : Arrays.asList(permStr.split(";"));
                dto.setPermissions(permissions);

                // Add synthetic review
                List<AppScanDto.ReviewDto> reviews = new ArrayList<>();
                if (tokens.size() >= 9) {
                    AppScanDto.ReviewDto r = new AppScanDto.ReviewDto();
                    r.setAuthor(tokens.size() > 9 ? tokens.get(9).replace("\"", "").trim() : "GooglePlayUser");
                    r.setText(tokens.get(7).replace("\"", "").trim());
                    r.setRating(Integer.parseInt(tokens.get(8).replace("\"", "").trim()));
                    reviews.add(r);
                } else {
                    AppScanDto.ReviewDto r = new AppScanDto.ReviewDto();
                    r.setAuthor("SystemVerifier");
                    r.setText("Standard generic review verification for play store integrity.");
                    r.setRating((int) Math.round(dto.getRating()));
                    reviews.add(r);
                }
                dto.setReviews(reviews);

                scanAndSaveApp(dto, operator);
                count++;
            } catch (Exception e) {
                // Log and ignore individual line corruption
                System.err.println("CSV parsing skipped line: " + line + ". Error: " + e.getMessage());
            }
        }
        
        auditLogRepository.save(new AuditLog(operator, "DATASET_UPLOAD", 
                "Uploaded and processed CSV dataset containing " + count + " applications."));

        return count;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }
}

package com.cyber.playdetector.service;

import com.cyber.playdetector.dto.MlScanRequest;
import com.cyber.playdetector.dto.MlScanResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class MlIntegrationService {

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;

    public MlIntegrationService(@Value("${ml.service.url}") String mlServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.mlServiceUrl = mlServiceUrl;
    }

    public MlScanResponse scanApp(MlScanRequest request) {
        try {
            String endpoint = mlServiceUrl + "/api/ml/scan";
            return restTemplate.postForObject(endpoint, request, MlScanResponse.class);
        } catch (Exception ex) {
            // Log warning and fallback to a local rule-based scoring engine
            System.err.println("Warning: Python ML service unreachable. Falling back to local scoring. " + ex.getMessage());
            return getFallbackScanResponse(request);
        }
    }

    private MlScanResponse getFallbackScanResponse(MlScanRequest request) {
        MlScanResponse response = new MlScanResponse();
        response.setPackage_name(request.getPackage_name());

        // Simple local rules
        boolean downloadSpike = request.getDownloads() > 100000 && request.getRating_count() < 10;
        boolean rankManipulation = request.getRating() > 4.7 && request.getRating_count() > 500 && request.getDownloads() < 1000;
        
        long fakeReviews = request.getReviews().stream()
                .filter(r -> r.getText().toLowerCase().contains("scam") || r.getText().toLowerCase().contains("fake") || r.getText().length() < 10)
                .count();

        boolean reviewSentimentAnomaly = (double) fakeReviews / Math.max(1, request.getReviews().size()) > 0.3;
        double fraudProb = (downloadSpike ? 0.4 : 0.0) + (rankManipulation ? 0.3 : 0.0) + (reviewSentimentAnomaly ? 0.2 : 0.0);
        fraudProb = Math.min(1.0, fraudProb);

        List<String> dangerousPerms = new ArrayList<>();
        List<String> behaviors = new ArrayList<>();
        
        for (String perm : request.getPermissions()) {
            if (perm.contains("SEND_SMS") || perm.contains("RECEIVE_SMS") || perm.contains("READ_SMS")) {
                dangerousPerms.add(perm);
                if (!behaviors.contains("SMS fraud potential")) {
                    behaviors.add("SMS fraud potential");
                }
            }
            if (perm.contains("FINE_LOCATION") || perm.contains("COARSE_LOCATION")) {
                dangerousPerms.add(perm);
            }
            if (perm.contains("SYSTEM_ALERT_WINDOW")) {
                dangerousPerms.add(perm);
                behaviors.add("Overlay hijacking potential");
            }
        }

        double malwareProb = Math.min(1.0, dangerousPerms.size() * 0.2 + behaviors.size() * 0.2);
        double overallScore = (fraudProb + malwareProb) / 2.0;

        String riskLevel = "SAFE";
        if (overallScore >= 0.65) {
            riskLevel = "HIGH_RISK";
        } else if (overallScore >= 0.35) {
            riskLevel = "MEDIUM_RISK";
        }

        MlScanResponse.FraudDetailsDto fraudDto = new MlScanResponse.FraudDetailsDto();
        fraudDto.setDownload_spike_flag(downloadSpike);
        fraudDto.setRank_manipulation_flag(rankManipulation);
        fraudDto.setRating_anomaly(false);
        fraudDto.setReview_sentiment_anomaly(reviewSentimentAnomaly);
        fraudDto.setFraud_probability(fraudProb);
        response.setFraud_details(fraudDto);

        MlScanResponse.MalwareDetailsDto malwareDto = new MlScanResponse.MalwareDetailsDto();
        malwareDto.setDangerous_permissions(dangerousPerms);
        malwareDto.setSuspicious_behaviors(behaviors);
        malwareDto.setMalware_probability(malwareProb);
        response.setMalware_details(malwareDto);

        MlScanResponse.SentimentSummaryDto sentimentDto = new MlScanResponse.SentimentSummaryDto();
        sentimentDto.setPositive_count(request.getReviews().size() - (int)fakeReviews);
        sentimentDto.setNegative_count((int)fakeReviews);
        sentimentDto.setNeutral_count(0);
        sentimentDto.setFake_review_count((int)fakeReviews);
        sentimentDto.setToxic_review_count(0);
        response.setSentiment_analysis(sentimentDto);

        response.setFraud_score(fraudProb);
        response.setMalware_score(malwareProb);
        response.setOverall_risk_score(overallScore);
        response.setRisk_level(riskLevel);

        return response;
    }
}

package com.cyber.playdetector.dto;

import java.util.List;

public class MlScanResponse {
    private String package_name;
    private double fraud_score;
    private double malware_score;
    private double overall_risk_score;
    private String risk_level;
    private FraudDetailsDto fraud_details;
    private MalwareDetailsDto malware_details;
    private SentimentSummaryDto sentiment_analysis;

    public MlScanResponse() {}

    public String getPackage_name() { return package_name; }
    public void setPackage_name(String package_name) { this.package_name = package_name; }
    public double getFraud_score() { return fraud_score; }
    public void setFraud_score(double fraud_score) { this.fraud_score = fraud_score; }
    public double getMalware_score() { return malware_score; }
    public void setMalware_score(double malware_score) { this.malware_score = malware_score; }
    public double getOverall_risk_score() { return overall_risk_score; }
    public void setOverall_risk_score(double overall_risk_score) { this.overall_risk_score = overall_risk_score; }
    public String getRisk_level() { return risk_level; }
    public void setRisk_level(String risk_level) { this.risk_level = risk_level; }
    public FraudDetailsDto getFraud_details() { return fraud_details; }
    public void setFraud_details(FraudDetailsDto fraud_details) { this.fraud_details = fraud_details; }
    public MalwareDetailsDto getMalware_details() { return malware_details; }
    public void setMalware_details(MalwareDetailsDto malware_details) { this.malware_details = malware_details; }
    public SentimentSummaryDto getSentiment_analysis() { return sentiment_analysis; }
    public void setSentiment_analysis(SentimentSummaryDto sentiment_analysis) { this.sentiment_analysis = sentiment_analysis; }

    public static class FraudDetailsDto {
        private boolean rating_anomaly;
        private boolean review_sentiment_anomaly;
        private boolean rank_manipulation_flag;
        private boolean download_spike_flag;
        private double fraud_probability;

        public FraudDetailsDto() {}

        public boolean isRating_anomaly() { return rating_anomaly; }
        public void setRating_anomaly(boolean rating_anomaly) { this.rating_anomaly = rating_anomaly; }
        public boolean isReview_sentiment_anomaly() { return review_sentiment_anomaly; }
        public void setReview_sentiment_anomaly(boolean review_sentiment_anomaly) { this.review_sentiment_anomaly = review_sentiment_anomaly; }
        public boolean isRank_manipulation_flag() { return rank_manipulation_flag; }
        public void setRank_manipulation_flag(boolean rank_manipulation_flag) { this.rank_manipulation_flag = rank_manipulation_flag; }
        public boolean isDownload_spike_flag() { return download_spike_flag; }
        public void setDownload_spike_flag(boolean download_spike_flag) { this.download_spike_flag = download_spike_flag; }
        public double getFraud_probability() { return fraud_probability; }
        public void setFraud_probability(double fraud_probability) { this.fraud_probability = fraud_probability; }
    }

    public static class MalwareDetailsDto {
        private List<String> dangerous_permissions;
        private List<String> suspicious_behaviors;
        private double malware_probability;

        public MalwareDetailsDto() {}

        public List<String> getDangerous_permissions() { return dangerous_permissions; }
        public void setDangerous_permissions(List<String> dangerous_permissions) { this.dangerous_permissions = dangerous_permissions; }
        public List<String> getSuspicious_behaviors() { return suspicious_behaviors; }
        public void setSuspicious_behaviors(List<String> suspicious_behaviors) { this.suspicious_behaviors = suspicious_behaviors; }
        public double getMalware_probability() { return malware_probability; }
        public void setMalware_probability(double malware_probability) { this.malware_probability = malware_probability; }
    }

    public static class SentimentSummaryDto {
        private int positive_count;
        private int negative_count;
        private int neutral_count;
        private int toxic_review_count;
        private int fake_review_count;

        public SentimentSummaryDto() {}

        public int getPositive_count() { return positive_count; }
        public void setPositive_count(int positive_count) { this.positive_count = positive_count; }
        public int getNegative_count() { return negative_count; }
        public void setNegative_count(int negative_count) { this.negative_count = negative_count; }
        public int getNeutral_count() { return neutral_count; }
        public void setNeutral_count(int neutral_count) { this.neutral_count = neutral_count; }
        public int getToxic_review_count() { return toxic_review_count; }
        public void setToxic_review_count(int toxic_review_count) { this.toxic_review_count = toxic_review_count; }
        public int getFake_review_count() { return fake_review_count; }
        public void setFake_review_count(int fake_review_count) { this.fake_review_count = fake_review_count; }
    }
}

package com.cyber.playdetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "fraud_results")
public class FraudResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    @JsonIgnore
    private App app;

    private boolean ratingAnomaly;
    private boolean reviewSentimentAnomaly;
    private boolean rankManipulationFlag;
    private boolean downloadSpikeFlag;
    private double fraudProbability;

    public FraudResult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public App getApp() { return app; }
    public void setApp(App app) { this.app = app; }
    public boolean isRatingAnomaly() { return ratingAnomaly; }
    public void setRatingAnomaly(boolean ratingAnomaly) { this.ratingAnomaly = ratingAnomaly; }
    public boolean isReviewSentimentAnomaly() { return reviewSentimentAnomaly; }
    public void setReviewSentimentAnomaly(boolean reviewSentimentAnomaly) { this.reviewSentimentAnomaly = reviewSentimentAnomaly; }
    public boolean isRankManipulationFlag() { return rankManipulationFlag; }
    public void setRankManipulationFlag(boolean rankManipulationFlag) { this.rankManipulationFlag = rankManipulationFlag; }
    public boolean isDownloadSpikeFlag() { return downloadSpikeFlag; }
    public void setDownloadSpikeFlag(boolean downloadSpikeFlag) { this.downloadSpikeFlag = downloadSpikeFlag; }
    public double getFraudProbability() { return fraudProbability; }
    public void setFraudProbability(double fraudProbability) { this.fraudProbability = fraudProbability; }
}

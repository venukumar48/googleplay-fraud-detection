package com.cyber.playdetector.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apps")
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String packageName;

    @Column(nullable = false)
    private String title;

    private int downloads;
    private double rating;
    private int ratingCount;
    private double price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_permissions", joinColumns = @JoinColumn(name = "app_id"))
    @Column(name = "permission")
    private List<String> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true)
    private FraudResult fraudResult;

    @OneToOne(mappedBy = "app", cascade = CascadeType.ALL, orphanRemoval = true)
    private MalwareResult malwareResult;

    private double overallRiskScore;
    private String riskLevel; // SAFE, MEDIUM_RISK, HIGH_RISK

    public App() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDownloads() { return downloads; }
    public void setDownloads(int downloads) { this.downloads = downloads; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public FraudResult getFraudResult() { return fraudResult; }
    public void setFraudResult(FraudResult fraudResult) { this.fraudResult = fraudResult; }
    public MalwareResult getMalwareResult() { return malwareResult; }
    public void setMalwareResult(MalwareResult malwareResult) { this.malwareResult = malwareResult; }
    public double getOverallRiskScore() { return overallRiskScore; }
    public void setOverallRiskScore(double overallRiskScore) { this.overallRiskScore = overallRiskScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}

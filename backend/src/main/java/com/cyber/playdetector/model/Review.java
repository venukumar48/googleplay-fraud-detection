package com.cyber.playdetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    @JsonIgnore
    private App app;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String text;

    private int rating;

    public Review() {}

    public Review(App app, String author, String text, int rating) {
        this.app = app;
        this.author = author;
        this.text = text;
        this.rating = rating;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public App getApp() { return app; }
    public void setApp(App app) { this.app = app; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}

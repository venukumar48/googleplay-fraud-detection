package com.cyber.playdetector.dto;

import java.util.List;

public class MlScanRequest {
    private String package_name;
    private String title;
    private int downloads;
    private double rating;
    private int rating_count;
    private double price;
    private List<String> permissions;
    private List<MlReviewInput> reviews;

    public MlScanRequest() {}

    public String getPackage_name() { return package_name; }
    public void setPackage_name(String package_name) { this.package_name = package_name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDownloads() { return downloads; }
    public void setDownloads(int downloads) { this.downloads = downloads; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getRating_count() { return rating_count; }
    public void setRating_count(int rating_count) { this.rating_count = rating_count; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<MlReviewInput> getReviews() { return reviews; }
    public void setReviews(List<MlReviewInput> reviews) { this.reviews = reviews; }

    public static class MlReviewInput {
        private String author;
        private String text;
        private int rating;

        public MlReviewInput() {}

        public MlReviewInput(String author, String text, int rating) {
            this.author = author;
            this.text = text;
            this.rating = rating;
        }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
    }
}

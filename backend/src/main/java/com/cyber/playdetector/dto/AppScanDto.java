package com.cyber.playdetector.dto;

import java.util.List;

public class AppScanDto {
    private String packageName;
    private String title;
    private int downloads;
    private double rating;
    private int ratingCount;
    private double price;
    private List<String> permissions;
    private List<ReviewDto> reviews;

    public AppScanDto() {}

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
    public List<ReviewDto> getReviews() { return reviews; }
    public void setReviews(List<ReviewDto> reviews) { this.reviews = reviews; }

    public static class ReviewDto {
        private String author;
        private String text;
        private int rating;

        public ReviewDto() {}

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
    }
}

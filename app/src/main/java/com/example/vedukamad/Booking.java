package com.example.vedukamad;

import java.io.Serializable;

public class Booking implements Serializable {
    private String plannerName;
    private String plannerLocation;
    private float plannerRating;
    private int plannerImage;
    private String packageType;
    private int totalPrice;
    private String eventDate;
    private boolean isCompleted;

    // Required empty constructor for serialization
    public Booking() {
    }

    public Booking(String plannerName, String plannerLocation, float plannerRating,
                   int plannerImage, String packageType, int totalPrice, String eventDate) {
        this.plannerName = plannerName;
        this.plannerLocation = plannerLocation;
        this.plannerRating = plannerRating;
        this.plannerImage = plannerImage;
        this.packageType = packageType;
        this.totalPrice = totalPrice;
        this.eventDate = eventDate;
        this.isCompleted = false;  // Default to upcoming
    }

    // Getters and setters
    public String getPlannerName() {
        return plannerName;
    }

    public void setPlannerName(String plannerName) {
        this.plannerName = plannerName;
    }

    public String getPlannerLocation() {
        return plannerLocation;
    }

    public void setPlannerLocation(String plannerLocation) {
        this.plannerLocation = plannerLocation;
    }

    public float getPlannerRating() {
        return plannerRating;
    }

    public void setPlannerRating(float plannerRating) {
        this.plannerRating = plannerRating;
    }

    public int getPlannerImage() {
        return plannerImage;
    }

    public void setPlannerImage(int plannerImage) {
        this.plannerImage = plannerImage;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

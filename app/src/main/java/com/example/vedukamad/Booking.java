package com.example.vedukamad;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Booking implements Serializable {
    private String plannerName;
    private String plannerLocation;
    private float plannerRating;
    private int plannerImage;
    private String packageType;
    private int totalPrice;
    private String eventDate;
    private boolean isCompleted;
    private boolean isApproved;  // New field to track approval status

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
        this.isApproved = false;   // Default to not approved
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    /**
     * Check if the event date has passed
     * @return true if the event is in the past
     */
    public boolean isEventInPast() {
        try {
            // Parse the event date (format: "15 May 2025 at 14:30")
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm", Locale.ENGLISH);
            Date eventDateTime = dateFormat.parse(eventDate);
            Date currentDate = new Date();

            return eventDateTime.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get formatted date without time
     * @return event date in format "15 May 2025"
     */
    public String getFormattedDate() {
        try {
            return eventDate.split(" at ")[0];
        } catch (Exception e) {
            return eventDate;
        }
    }

    /**
     * Get just the time part of the event date
     * @return time in format "14:30"
     */
    public String getEventTime() {
        try {
            return eventDate.split(" at ")[1];
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get short month and day (for calendar display)
     * @return date in format "15 Feb"
     */
    public String getShortDate() {
        try {
            String[] parts = getFormattedDate().split(" ");
            if (parts.length >= 2) {
                return parts[0] + " " + parts[1];
            }
            return getFormattedDate();
        } catch (Exception e) {
            return eventDate;
        }
    }

    /**
     * Get event type (Marriage, Reception, etc.)
     * @return event type extracted from package type
     */
    public String getEventType() {
        if (packageType.toLowerCase().contains("marriage")) {
            return "Marriage";
        } else if (packageType.toLowerCase().contains("reception")) {
            return "Reception";
        } else if (packageType.toLowerCase().contains("engagement")) {
            return "Engagement";
        } else if (packageType.toLowerCase().contains("sangeet")) {
            return "Sangeet";
        } else {
            return "Event";
        }
    }

    @Override
    public String toString() {
        return "Booking{" +
                "plannerName='" + plannerName + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", packageType='" + packageType + '\'' +
                ", totalPrice=" + totalPrice +
                ", isCompleted=" + isCompleted +
                ", isApproved=" + isApproved +
                '}';
    }
}
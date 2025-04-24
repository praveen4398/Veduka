// IntroItem.java
package com.example.vedukamad;

public class IntroItem {
    private String title;
    private String description;
    private int imageRes;
    public IntroItem(String title, String description, int imageRes) {
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}

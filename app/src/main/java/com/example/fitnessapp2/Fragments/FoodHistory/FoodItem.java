package com.example.fitnessapp2.Fragments.FoodHistory;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Map;

public class FoodItem {
    @PropertyName("dish_name")
    private String dish_name;
    private List<Map<String, Object>> Foods; // List of ingredients with macronutrients
    private String imageUri; // Single image URI
    private boolean isExpanded; // Tracks if item is expanded


    public FoodItem() {
        // Empty constructor required for Firestore
    }

    public FoodItem(String dish_name, List<Map<String, Object>> Foods, String imageUri) {
        this.dish_name = dish_name;
        this.Foods = Foods;
        this.isExpanded = false; // Default: collapsed
        this.imageUri = imageUri;
    }

    @PropertyName("dish_name")
    public String getDishName() {
        return dish_name;
    }

    public List<Map<String, Object>> getFoods() {
        return Foods;
    }

    public String getImageUri() {
        return imageUri;
    }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}

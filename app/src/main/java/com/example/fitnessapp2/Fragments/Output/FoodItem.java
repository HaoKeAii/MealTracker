package com.example.fitnessapp2.Fragments.Output;

public class FoodItem {
    private String name;
    private double calories, protein, carbs, fats;

    public FoodItem() {
        // Empty constructor required for Firestore & JSON parsing
    }

    public FoodItem(String name, double calories, double protein, double carbs, double fats) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    public String getName() { return name; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFats() { return fats; }
}

package com.example.fitnessapp2.Fragments.FoodDb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_table")
public class Food {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public float calories, protein, carbs, fats;
    private boolean isExpanded; // Tracks if item is expanded


    public Food() {
        // Empty constructor for Room
    }

    public Food(String name, /*float calories,*/ float protein, float carbs, float fats) {
        this.name = name;
        //this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.isExpanded = false; // Default: collapsed

    }

    public String getName() {
        return name;
    }

    public float getProtein(){
        return protein;
    }

    public float getCarbs(){
        return carbs;
    }

//    public float getCalories(){
//        return calories;
//    }

    public float getFats(){
        return fats;
    }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }

}

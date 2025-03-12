package com.example.fitnessapp2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class JsonFoodManager {
    private Context context; // Store context
    private FirebaseFirestore db;
    private String userId;


    public JsonFoodManager(Context context) { // Constructor to receive context
        this.context = context;
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    private String cleanJsonString(String json) {
        return json.replaceAll("```json", "").replaceAll("```", "").trim();
    }

    private void parseAndStoreFoodJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String dishName = jsonObject.getString("dish_name");
            JSONArray detectedFoods = jsonObject.getJSONArray("Foods");

            //final double[] proteinTotal = {0}, caloriesTotal = {0}, carbsTotal = {0}, fatsTotal = {0};

            List<Map<String, Object>> ingredients = new ArrayList<>();
            for (int i = 0; i < detectedFoods.length(); i++) {
                JSONObject foodObject = detectedFoods.getJSONObject(i);
                String name = foodObject.getString("name");

                JSONObject macros = foodObject.getJSONObject("macronutrients");
                double calories = macros.getDouble("calories");
                double protein = macros.getDouble("protein");
                double carbs = macros.getDouble("carbs");
                double fats = macros.getDouble("fats");

//                proteinTotal[0] += protein;
//                caloriesTotal[0] += calories;
//                carbsTotal[0] += carbs;
//                fatsTotal[0] += fats;

                ingredients.add(createFood(name, calories, protein, carbs, fats));
            }

            // Create meal document
//            Map<String, Object> meal = new HashMap<>();
//            meal.put("dish_name", dishName);
//            meal.put("Foods", ingredients);
//
//            db.collection("users")
//                    .document(userId)
//                    .collection("food")
//                    .add(meal)
//                    .addOnSuccessListener(documentReference -> {
//                        Log.d(TAG, "Meal stored successfully! ID: " + documentReference.getId());
//
//                        Intent intent = new Intent(CameraActivity.this, MainActivity.class);
//                        intent.putExtra("openFragment", "ScanFragment"); // Send flag
//
////                        intent.putExtra("protein", proteinTotal[0]);
////                        intent.putExtra("carbs", carbsTotal[0]);
////                        intent.putExtra("fats", fatsTotal[0]);
////                        intent.putExtra("calories", caloriesTotal[0]);
////                        Log.d(TAG, "Calories: " + caloriesTotal);
////                        startActivity(intent);
////                        finish();
//                        //backHome();
//                    })
//                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
    }

    private Map<String, Object> createFood(String name, double calories, double protein, double carbs, double fats) {
        Map<String, Object> macronutrients = new HashMap<>();
        macronutrients.put("calories", calories);
        macronutrients.put("protein", protein);
        macronutrients.put("carbs", carbs);
        macronutrients.put("fats", fats);

        Map<String, Object> food = new HashMap<>();
        food.put("name", name);
        food.put("macronutrients", macronutrients);

        return food;
    }

}

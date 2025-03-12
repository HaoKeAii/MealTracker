package com.example.fitnessapp2.Fragments.Scan;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.fitnessapp2.Fragments.Output.OutputDetailFragment;
import com.example.fitnessapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputFragment extends Fragment {
    private FirebaseFirestore db;
    private String userId;
    ImageView scannedImageView;
    FirebaseStorage storage;
    StorageReference storageRef;
    DecimalFormat df;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meal_output_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        df = new DecimalFormat("0.00");

        scannedImageView = view.findViewById(R.id.food_image);
        Button detailBtn = view.findViewById(R.id.detail_button);
        Button saveBtn = view.findViewById(R.id.save_button);
        TextView dishNameTextView = view.findViewById(R.id.dish_name_display);
        TextView caloriesTextView = view.findViewById(R.id.calorie_display);
        TextView proteinTextView = view.findViewById(R.id.protein_display);
        TextView carbsTextView = view.findViewById(R.id.carbs_display);
        TextView fatsTextView = view.findViewById(R.id.fats_display);
        // Get arguments
        Bundle args = getArguments();
        String output = args.getString("output");
        String cleanedJson = cleanJsonString(output);

        if (args != null) {
            String imageUri = args.getString("imageUri");
            if (imageUri != null) {
                Uri uri = Uri.parse(imageUri);
                loadImageFromUri(uri); // Display image
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(cleanedJson);
            String dishName = jsonObject.getString("dish_name");
            //String calories = jsonObject.getString("calories");
            JSONArray detectedFoods = jsonObject.getJSONArray("Foods");

            double proteinTotal = 0, carbsTotal = 0, fatsTotal = 0, caloriesTotal = 0;

            List<Map<String, Object>> ingredients = new ArrayList<>();
            for (int i = 0; i < detectedFoods.length(); i++) {
                JSONObject foodObject = detectedFoods.getJSONObject(i);
                String name = foodObject.getString("name");

                JSONObject macros = foodObject.getJSONObject("macronutrients");
                double calories = macros.getDouble("calories");
                double protein = macros.getDouble("protein");
                double carbs = macros.getDouble("carbs");
                double fats = macros.getDouble("fats");

                ingredients.add(createFood(name, calories, protein, carbs, fats));

                caloriesTotal += calories;
                proteinTotal += protein;
                carbsTotal += carbs;
                fatsTotal += fats;
            }

            double finalCaloriesTotal = caloriesTotal;
            double finalProteinTotal = proteinTotal;
            double finalCarbsTotal = carbsTotal;
            double finalFatsTotal = fatsTotal;

            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                dishNameTextView.setText(dishName);
                caloriesTextView.setText(df.format(finalCaloriesTotal) + "kcal");
                proteinTextView.setText(df.format(finalProteinTotal) + "g");
                carbsTextView.setText(df.format(finalCarbsTotal) + "g");
                fatsTextView.setText(df.format(finalFatsTotal) + "g");
            });

            Log.d(TAG, (proteinTotal + " " + carbsTotal + " " + fatsTotal));

            Map<String, Object> meal = new HashMap<>();
            meal.put("dish_name", dishName);
            meal.put("Foods", ingredients);

            // Create meal document
            saveBtn.setOnClickListener(v -> {
                String imageUriStr = args.getString("imageUri");
                if (imageUriStr != null) {
                    Uri imageUri = Uri.parse(imageUriStr);
                    uploadImageToFirebase(imageUri, meal);
                } else {
                    Log.e(TAG, "Image URI is null, cannot upload or save meal.");
                }
            });

            // Put into Bundle
            detailBtn.setOnClickListener(v -> {
                Bundle bundle = new Bundle();

                bundle.putString("food", cleanedJson);

                OutputDetailFragment outputDetailFragment = new OutputDetailFragment();
                outputDetailFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, outputDetailFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
    }

    private String cleanJsonString(String json) {
        return json.replaceAll("```json", "").replaceAll("```", "").trim();
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

    private void loadImageFromUri(Uri imageUri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            scannedImageView.setImageBitmap(bitmap); // Set image manually
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageToFirebase(Uri imageUri, Map<String, Object> meal) {
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null, cannot upload");
            return;
        }

        // Generate a unique filename
        String fileName = "images/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        // Upload file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "Image uploaded successfully: " + uri.toString());
                        // Update meal object with image URL
                        meal.put("imageUri", uri.toString());
                        // Save meal to Firestore after getting image URL
                        saveMealToFirestore(meal);
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Image upload failed", e));
    }

    private void saveMealToFirestore(Map<String, Object> meal) {
        db.collection("users")
                .document(userId)
                .collection("food")
                .add(meal)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Meal stored successfully! ID: " + documentReference.getId());
                    Toast.makeText(getActivity(), "Meal saved!", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}

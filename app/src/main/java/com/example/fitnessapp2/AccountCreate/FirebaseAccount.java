package com.example.fitnessapp2.AccountCreate;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitnessapp2.Admin.AdminActivity;
import com.example.fitnessapp2.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAccount {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FirebaseAccount() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Logout function
    public void logoutUser() {
        mAuth.signOut();
        Log.d("Firebase", "User logged out.");
    }

    // Submit user info function
    public void submitUserInfo(String name, String age, String gender, String height, String bw, String dietPref, Callback callback) {
        // Ensure the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        String userId = currentUser.getUid(); // Get current user ID

        // Create a HashMap to store user data
        Map<String, Object> user = new HashMap<>();

        user.put("name", name);
        user.put("age", age);
        user.put("gender", gender);
        user.put("height", height);
        user.put("body_weight", bw);
        user.put("role", "user");

        if (dietPref != null && !dietPref.trim().isEmpty()) {
            user.put("diet_preference", dietPref);
        }

        // Save data to Firestore
        db.collection("users").document(userId)
                .set(user, SetOptions.merge()) // ✅ Merges existing data instead of overwriting
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firebase", "Document successfully written!");
                    callback.onSuccess("User info added successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error writing document", e);
                    callback.onFailure("Failed to add user info: " + e.getMessage());
                });
    }

    public void submitUserImage(@Nullable String imageUri, Callback callback) {
        // Ensure the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        String userId = currentUser.getUid(); // Get current user ID

        // Create a HashMap to store user data
        Map<String, Object> user = new HashMap<>();

        if (imageUri != null) {
            user.put("imageUri", imageUri);  // Save image URI if available
        }

        // Save data to Firestore
        db.collection("users").document(userId)
                .set(user, SetOptions.merge()) // ✅ Merges existing data instead of overwriting
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firebase", "Document successfully written!");
                    callback.onSuccess("User info added successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error writing document", e);
                    callback.onFailure("Failed to add user info: " + e.getMessage());
                });
    }

    // Login function
    public void loginUser(String email, String password, Callback callback){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "LoginUserWithEmail:success");
                            callback.onSuccess("Account Login successfully.");
                        } else {
                            Log.w("Firebase", "LoginUserWithEmail:failed", task.getException());
                            callback.onFailure("Login failed: " + task.getException().getMessage());
                        }
                    }
        });
    }

    // Register function
    public void registerUser(String email, String password, Callback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "createUserWithEmail:success");
                            callback.onSuccess("Account created successfully.");
                        } else {
                            Log.w("Firebase", "createUserWithEmail:failed", task.getException());
                            callback.onFailure("Account creation failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    // Callback interface to handle success/failure
    public interface Callback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }
}

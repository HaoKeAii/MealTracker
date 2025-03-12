package com.example.fitnessapp2.Admin.UserProfile;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp2.AccountCreate.FirebaseAccount;
import com.example.fitnessapp2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailPage extends AppCompatActivity {
    TextView nameDisplay, ageDisplay, genderDisplay, heightDisplay, bwDisplay, dietPrefDisplay;
    EditText nameInput, ageInput, genderInput, heightInput, bwInput, dietPrefInput;
    Button editButton, saveButton;
    LinearLayout mainLayout, editLayout;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_user_detail_page);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String height = intent.getStringExtra("height");
        String bodyWeight = intent.getStringExtra("body_weight");
        String dietPref = intent.getStringExtra("dietPreference");

        nameDisplay = findViewById(R.id.name);
        ageDisplay = findViewById(R.id.age);
        genderDisplay = findViewById(R.id.gender);
        heightDisplay = findViewById(R.id.height);
        bwDisplay = findViewById(R.id.bw);
        dietPrefDisplay = findViewById(R.id.diet_pref);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        mainLayout = findViewById(R.id.main_layout);
        editLayout = findViewById(R.id.edit_layout);
        nameInput = findViewById(R.id.name_input);
        ageInput = findViewById(R.id.age_input);
        genderInput = findViewById(R.id.gender_input);
        heightInput = findViewById(R.id.height_input);
        bwInput = findViewById(R.id.bw_input);
        dietPrefInput = findViewById(R.id.diet_pref_input);

        mainLayout.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.GONE);

        nameDisplay.setText(name);
        ageDisplay.setText(age);
        genderDisplay.setText(gender);
        heightDisplay.setText(height);
        bwDisplay.setText(bodyWeight);
        dietPrefDisplay.setText(dietPref);

        editButton.setOnClickListener(v -> {
            mainLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);

            nameInput.setText(name);
            ageInput.setText(age);
            genderInput.setText(gender);
            heightInput.setText(height);
            bwInput.setText(bodyWeight);
            dietPrefInput.setText(dietPref);
        });

        saveButton.setOnClickListener(v -> {
            String newName = nameInput.getText().toString();
            String newAge = ageInput.getText().toString();
            String newGender = genderInput.getText().toString();
            String newHeight = heightInput.getText().toString();
            String newBodyWeight = bwInput.getText().toString();
            String newDietPref = dietPrefInput.getText().toString();

            nameDisplay.setText(newName);
            ageDisplay.setText(newAge);
            genderDisplay.setText(newGender);
            heightDisplay.setText(newHeight);
            bwDisplay.setText(newBodyWeight);
            dietPrefDisplay.setText(newDietPref);

            mainLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);

            Map<String, Object> user = new HashMap<>();

            user.put("name", newName);
            user.put("age", newAge);
            user.put("gender", newGender);
            user.put("height", newHeight);
            user.put("body_weight", newBodyWeight);
            user.put("diet_preference", newDietPref);

            db.collection("users").document(userId)
                    .set(user, SetOptions.merge()) // ✅ Merges existing data instead of overwriting
                    .addOnSuccessListener(aVoid -> {
                        Log.i("Firebase", "Document successfully written!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error writing document", e);
                    });
        });
    }
}

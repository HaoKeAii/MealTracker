package com.example.fitnessapp2.Fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitnessapp2.AccountCreate.FirebaseAccount;
import com.example.fitnessapp2.AccountCreate.Register;
import com.example.fitnessapp2.MainActivity;
import com.example.fitnessapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    TextView nameDisplay, ageDisplay, bwDisplay, heightDisplay, bmiDisplay, dietPrefDisplay, genderDisplay;
    EditText nameInput, ageInput, heightInput, bwInput, dietPrefInput;
    Button editButton, saveButton;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;
    GridLayout displayConstraint, editConstraint;
    FirebaseAccount firebaseAccount;
    public String gender;
    ImageView pfpDisplay, editPlaceholder;
    Uri selectedImageUri;
    String imageUrl;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.profile_page, container, false);

        // Initialize Firebase Auth (✅ Fix: Ensure FirebaseAuth is set up before using it)
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e("ProfileFragment", "User not logged in!");
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return view; // ✅ Prevents further execution if the user is null
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        nameDisplay = view.findViewById(R.id.name_display);
        ageDisplay = view.findViewById(R.id.age_display);
        bwDisplay = view.findViewById(R.id.bw_display);
        heightDisplay = view.findViewById(R.id.height_display);
        bmiDisplay = view.findViewById(R.id.bmi_display);
        dietPrefDisplay = view.findViewById(R.id.diet_pref_display);
        genderDisplay = view.findViewById(R.id.gender_display);
        editButton = view.findViewById(R.id.edit_button);
        saveButton = view.findViewById(R.id.save_button);
        displayConstraint = view.findViewById(R.id.display_constraint);
        editConstraint = view.findViewById(R.id.edit_constraint);
        nameInput = view.findViewById(R.id.name_input);
        ageInput = view.findViewById(R.id.age_input);
        heightInput = view.findViewById(R.id.height_input);
        bwInput = view.findViewById(R.id.bw_input);
        dietPrefInput = view.findViewById(R.id.diet_pref_input);
        pfpDisplay = view.findViewById(R.id.pfp_display);
        editPlaceholder = view.findViewById(R.id.pfp_edit);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        pfpDisplay.setClickable(false);
        firebaseAccount = new FirebaseAccount();
        
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        gender = documentSnapshot.getString("gender");
                        String age = documentSnapshot.getString("age");
                        String bw = documentSnapshot.getString("body_weight");
                        String height = documentSnapshot.getString("height");
                        String dietPref = documentSnapshot.getString("diet_preference");
                        String imageUri = documentSnapshot.getString("imageUri");

                        // Set the retrieved data to the TextViews
                        nameDisplay.setText(name);
                        ageDisplay.setText(age);
                        genderDisplay.setText(gender);
                        bwDisplay.setText(bw);
                        heightDisplay.setText(height);
                        bmiDisplay.setText(calculateBmi(height, bw));
                        dietPrefDisplay.setText(dietPref);

                        if (imageUri != null && !imageUri.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUri) // Load the single image URI
                                    //.circleCrop()
                                    .placeholder(R.drawable.placeholder) // Default image
                                    .error(R.drawable.img_error) // If loading fails
                                    .into(pfpDisplay);
                        }
                    } else {
                        Log.d("ProfileFragment", "No such document!");
                        Toast.makeText(getActivity(), "No data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error retrieving document", e);
                    Toast.makeText(getActivity(), "Error retrieving data.", Toast.LENGTH_SHORT).show();
                });

        editButton.setOnClickListener(v -> {
            editButton.setVisibility(View.GONE);
            pfpDisplay.setClickable(true);
            pfpDisplay.setOnClickListener(vv ->{
                Log.d(TAG, "onViewCreated: clicked ");

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });

            displayConstraint.setVisibility(View.GONE);
            editConstraint.setVisibility(View.VISIBLE);
            editPlaceholder.setVisibility(View.VISIBLE);

            String name = nameDisplay.getText().toString();
            String age = ageDisplay.getText().toString();
            String bw = bwDisplay.getText().toString();
            String height = heightDisplay.getText().toString();
            String dietPref = dietPrefDisplay.getText().toString();

            nameInput.setText(name);
            ageInput.setText(age);
            bwInput.setText(bw);
            heightInput.setText(height);
            dietPrefInput.setText(dietPref);

            saveButton.setVisibility(View.VISIBLE);
        });

        saveButton.setOnClickListener(v -> {
            editConstraint.setVisibility(View.GONE);
            editPlaceholder.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            displayConstraint.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            pfpDisplay.setClickable(false);

            String name = nameInput.getText().toString();
            String age = ageInput.getText().toString();
            String bw = bwInput.getText().toString();
            String height = heightInput.getText().toString();
            String dietPref = dietPrefInput.getText().toString();

            if (selectedImageUri != null){
                uploadImageToFirebase(selectedImageUri);
            }

            firebaseAccount.submitUserInfo(name, age, gender, height, bw, dietPref, new FirebaseAccount.Callback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    nameDisplay.setText(name);
                    ageDisplay.setText(age);
                    genderDisplay.setText(gender);
                    bwDisplay.setText(bw);
                    heightDisplay.setText(height);
                    dietPrefDisplay.setText(dietPref);
                }
                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

    private String calculateBmi(String height, String bw) {
        double cmHeight = Double.parseDouble(height);
        double heightM = cmHeight / 100.0; // Convert cm to meters
        double bmi = Double.parseDouble(bw) / (heightM * heightM); // Correct formula
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(bmi);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                Log.d(TAG, "openGallery: " + selectedImageUri);
                pfpDisplay.setImageBitmap(selectedImage);
            } catch (IOException e) {
                Log.e(TAG, "Error loading image", e);
            }
        }
    }

    private String uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null, cannot upload");
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
                        imageUrl = uri.toString();
                        firebaseAccount.submitUserImage(imageUrl, new FirebaseAccount.Callback() {
                            @Override
                            public void onSuccess(String message) {
                                Log.d(TAG, "(Firestore)Image uploaded successfully: " + imageUrl);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e(TAG, "Image failed to upload: " + errorMessage);
                            }
                        });
                        Log.d(TAG, "(FirebaseStorage)Image uploaded successfully: " + imageUrl);
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Image upload failed", e));
        return imageUrl;
    }
}



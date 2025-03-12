package com.example.fitnessapp2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitnessapp2.AccountCreate.FirebaseAccount;
import com.example.fitnessapp2.AccountCreate.Login;
import com.example.fitnessapp2.CameraActivity;
import com.example.fitnessapp2.Fragments.FoodHistory.HistoryFragment;
import com.example.fitnessapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {
    LinearLayout scanBtn, historyBtn, feedbackBtn;
    ImageButton logoutBtn;
    TextView emailDisplay;
    FirebaseAuth mAuth;
    String userId;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.home_page, container, false);

        // Initialize Firebase
        FirebaseAccount firebase = new FirebaseAccount();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        emailDisplay = view.findViewById(R.id.email_display);
        logoutBtn = view.findViewById(R.id.logout_button);
        historyBtn = view.findViewById(R.id.history_button);
        feedbackBtn = view.findViewById(R.id.feedback_button);
        scanBtn = view.findViewById(R.id.scan_button);

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("HomeFragment", "Logged in user ID: " + userId);
//            emailDisplay.setText(currentUser.getEmail());
        } else {
            Toast.makeText(getActivity(), "User not authenticated. Redirecting to login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
            return view;
        }

        db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        emailDisplay.setText("Welcome back, " + name);
                    } else {
                        Log.d("HomeFragment", "No such document!");
                        Toast.makeText(getActivity(), "No data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Error retrieving document", e);
                    Toast.makeText(getActivity(), "Error retrieving data.", Toast.LENGTH_SHORT).show();
                });

        historyBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HistoryFragment()); // Make sure fragment_container exists
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Logout button logic
        logoutBtn.setOnClickListener(v -> {
            firebase.logoutUser();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        });

        // Scan button logic
        scanBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        // Feedback button logic
        feedbackBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new FeedbackFragment()); // Make sure fragment_container exists
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}

package com.example.fitnessapp2.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FeedbackFragment extends Fragment {
    FirebaseFirestore db;
    String userId;
    EditText feedbackInput;
    Button submitBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_page, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        feedbackInput = view.findViewById(R.id.feedback_input);
        submitBtn = view.findViewById(R.id.submit_button);

        submitBtn.setOnClickListener(v -> {
            String feedback = feedbackInput.getText().toString();


            long timestamp = System.currentTimeMillis(); // Your Firestore timestamp

            // Convert timestamp to Date
            Date date = new Date(timestamp);

            // Format the date as a readable string
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
            String formattedDate = sdf.format(date);

            //System.out.println("Formatted Date: " + formattedDate);

            Map<String, Object> feedbackData = new HashMap<>();
            feedbackData.put("message", feedback);
            feedbackData.put("timestamp", System.currentTimeMillis());
            feedbackData.put("date", formattedDate);

            db.collection("users").document(userId).collection("feedback")
                    .add(feedbackData)
                    .addOnSuccessListener(aVoid -> {
                        Log.i("Firebase", "Document successfully written!");
                        Toast.makeText(getActivity(), "Document successfully written!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error writing document", e);
                        Toast.makeText(getActivity(), "Error writing document", Toast.LENGTH_SHORT).show();
                    });
        });


    }
}

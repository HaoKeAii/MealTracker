package com.example.fitnessapp2.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp2.AccountCreate.FirebaseAccount;
import com.example.fitnessapp2.AccountCreate.Login;
import com.example.fitnessapp2.Admin.UserProfile.UserDbPage;
import com.example.fitnessapp2.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {
    Button logoutBtn;
    LinearLayout userDbBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        mAuth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logout_button);
        userDbBtn = findViewById(R.id.userdb_layout);

        FirebaseAccount firebase = new FirebaseAccount();

        logoutBtn.setOnClickListener(v -> {
            firebase.logoutUser();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        userDbBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UserDbPage.class);
            startActivity(intent);
        });
    }
}

package com.example.fitnessapp2;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import com.example.fitnessapp2.AccountCreate.Login;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        mAuth = FirebaseAuth.getInstance();
        // Delay for SPLASH_TIME then start MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
            finish(); // Close SplashActivity so user can't go back to it
        }, SPLASH_TIME);
    }
    }
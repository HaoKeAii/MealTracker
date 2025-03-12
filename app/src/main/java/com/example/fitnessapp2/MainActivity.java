package com.example.fitnessapp2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitnessapp2.Fragments.*;
import com.example.fitnessapp2.Fragments.FoodDb.DatabaseFragment;
import com.example.fitnessapp2.Fragments.Scan.OutputFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        String fragmentToOpen = getIntent().getStringExtra("openFragment");

        // Load HomeFragment when the activity starts
        loadFragment(new HomeFragment());

        if ("ScanFragment".equals(fragmentToOpen)) {
            Intent intent = getIntent();
            String output = intent.getStringExtra("output");
            String imageUri = intent.getStringExtra("imageUri");

            Log.d(TAG, "Output: " + output);
            Log.d(TAG, "Image URI: " + imageUri);

            OutputFragment outputFragment = new OutputFragment();
            Bundle bundle = new Bundle();
            bundle.putString("output", output);
            bundle.putString("imageUri", imageUri);
            outputFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, outputFragment)
                    .addToBackStack(null)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_foodlist) {
                selectedFragment = new DatabaseFragment();
            } else if (itemId == R.id.nav_scan){
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                finish();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}

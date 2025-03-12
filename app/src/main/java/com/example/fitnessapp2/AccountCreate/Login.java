package com.example.fitnessapp2.AccountCreate;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp2.Admin.AdminActivity;
import com.example.fitnessapp2.MainActivity;
import com.example.fitnessapp2.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    TextView registerPage, forgotPwBtn;
    EditText emailInput, passwordInput;
    Button loginBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            checkUserRole();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_button);
        registerPage = findViewById(R.id.create_account_text);
        forgotPwBtn = findViewById(R.id.forgot_pw_text);
        progressBar = findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(GONE);
        loginBtn.setEnabled(false);

        registerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the button only if both inputs are not empty
                boolean isEmailFilled = !emailInput.getText().toString().trim().isEmpty();
                boolean isPasswordFilled = !passwordInput.getText().toString().trim().isEmpty();

                loginBtn.setEnabled(isPasswordFilled && isEmailFilled);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        emailInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(emailInput.getText());
                password = String.valueOf(passwordInput.getText());

                FirebaseAccount firebase = new FirebaseAccount();
                firebase.loginUser(email, password, new FirebaseAccount.Callback(){
                    @Override
                    public void onSuccess(String message) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        checkUserRole();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        forgotPwBtn.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, ForgotPassword.class));
        });

    }
    private void checkUserRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role"); // ✅ Correct Firestore field name

                            if ("admin".equals(role)) {
                                startActivity(new Intent(Login.this, AdminActivity.class));
                                Log.d(TAG, "Login as admin");
                            } else {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                Log.d(TAG, "Login as user");
                            }
                            finish(); // Close login activity after redirecting
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user role", e));
        }
    }


}

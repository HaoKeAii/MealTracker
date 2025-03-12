package com.example.fitnessapp2.AccountCreate;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp2.Fragments.HomeFragment;
import com.example.fitnessapp2.MainActivity;
import com.example.fitnessapp2.R;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    TextView loginPage;
    EditText emailInput, passwordInput, nameInput, heightInput, bwInput, dietPrefInput;
    RadioGroup genderGroup;
    NumberPicker agePicker, heightPicker, bwPicker;
    RadioButton selectedGender;
    Button registerBtn;
//    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        loginPage = findViewById(R.id.create_account_text);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerBtn = findViewById(R.id.register_button);
        nameInput = findViewById(R.id.name_input);
        dietPrefInput = findViewById(R.id.diet_pref_input);
        genderGroup = findViewById(R.id.radio_group);
        heightPicker = findViewById(R.id.height_input);
        heightPicker.setMinValue(100);
        heightPicker.setMaxValue(300);
        bwPicker = findViewById(R.id.bw_input);
        bwPicker.setMinValue(30);
        bwPicker.setMaxValue(600);
        agePicker = findViewById(R.id.age_input);
        agePicker.setMinValue(12);
        agePicker.setMaxValue(100);
        progressBar = findViewById(R.id.progress_bar);

        registerBtn.setEnabled(false);
        progressBar.setVisibility(GONE);

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
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
                boolean isNameFilled = !nameInput.getText().toString().trim().isEmpty();
                boolean isEmailFilled = !emailInput.getText().toString().trim().isEmpty();
                boolean isPasswordFilled = !passwordInput.getText().toString().trim().isEmpty();

                registerBtn.setEnabled(isNameFilled && isPasswordFilled && isEmailFilled);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        nameInput.addTextChangedListener(textWatcher);
        emailInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email = String.valueOf(emailInput.getText());
                String password = String.valueOf(passwordInput.getText());
                String name = String.valueOf(nameInput.getText());
                String age = String.valueOf(agePicker.getValue());
                String height = String.valueOf(heightPicker.getValue());
                String bw = String.valueOf(bwPicker.getValue());
                String dietPref = (dietPrefInput != null) ? dietPrefInput.getText().toString() : "-";
                int selectedId = genderGroup.getCheckedRadioButtonId();
                selectedGender = findViewById(selectedId);
                String gender = (selectedGender != null) ? selectedGender.getText().toString() : "-";

                FirebaseAccount firebase = new FirebaseAccount();
                firebase.registerUser(email, password, new FirebaseAccount.Callback(){
                    @Override
                    public void onSuccess(String message) {
                        progressBar.setVisibility(View.GONE);

                        FirebaseAccount firebaseAccount = new FirebaseAccount();
                        firebaseAccount.submitUserInfo(name, age, gender, height, bw, dietPref, new FirebaseAccount.Callback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, MainActivity.class)); // Navigate to Home
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(Register.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Register.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
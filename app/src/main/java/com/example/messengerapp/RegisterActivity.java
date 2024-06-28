package com.example.messengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText email, password, firstName, lastName, nickname;
    private Button registerButton;
    private TextView loginLink;

    private FirebaseAuth auth;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        firstName = findViewById(R.id.registerFirstName);
        lastName = findViewById(R.id.registerLastName);
        nickname = findViewById(R.id.registerNickname);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        registerButton.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userFirstName = firstName.getText().toString().trim();
        String userLastName = lastName.getText().toString().trim();
        String userNickname = nickname.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Enter your email");
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Enter your password");
            return;
        }

        if (userPassword.length() < 6) {
            password.setError("Password should be at least 6 characters");
            return;
        }

        if (TextUtils.isEmpty(userFirstName)) {
            firstName.setError("Enter your first name");
            return;
        }

        if (TextUtils.isEmpty(userLastName)) {
            lastName.setError("Enter your last name");
            return;
        }

        if (TextUtils.isEmpty(userNickname)) {
            nickname.setError("Enter your nickname");
            return;
        }

        firebaseHelper.registerUser(userEmail, userPassword, userFirstName, userLastName, userNickname);

        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}

package com.mahad.shoppinglist;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Signup extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.signupEmailEditText);
        passwordEditText = findViewById(R.id.signupPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.signupConfirmPasswordEditText);
        Button signupButton = findViewById(R.id.signupButton);

        auth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(v -> createUser());
    }

    private void createUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Signup Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

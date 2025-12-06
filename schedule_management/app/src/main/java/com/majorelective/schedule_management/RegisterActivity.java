package com.majorelective.schedule_management;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPass;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Bind UI
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPass = findViewById(R.id.etRegConfirmPass);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvGoToLogin);

        // Handle Register Logic
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirm = etConfirmPass.getText().toString().trim();

            if(name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Save to DB
                boolean success = dbHelper.registerStudent(name, email, pass);
                if(success) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                } else {
                    Toast.makeText(this, "Registration Failed (ID may already exist)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle "Back to Login" link
        tvLoginLink.setOnClickListener(v -> finish());
    }
}
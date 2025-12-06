package com.majorelective.schedule_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // 1. Declare the variables for UI components
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Handle Window Insets (Status bar padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Initialize the UI components using the IDs from activity_login.xml
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 3. Set the OnClickListener for the Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture Inputs
                String inputId = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                // Perform Logic Checks
                performLoginCheck(inputId, inputPassword);
            }
        });
    }

    private void performLoginCheck(String id, String password) {
        // --- Check 1: The Admin (Master Key) ---
        if (id.equals("admin") && password.equals("admin123")) {
            Toast.makeText(MainActivity.this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();

            // Navigate to Admin Dashboard
            Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        }

        // --- Check 2: The Student (Hardcoded Check) ---
        else if (id.equals("student") && password.equals("12345")) {
            Toast.makeText(MainActivity.this, "Welcome, Student!", Toast.LENGTH_SHORT).show();

            // Navigate to Student Dashboard
            Intent intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
            startActivity(intent);
        }

        // --- Check 3: The Instructor (Hardcoded Check) ---
        else if (id.equals("instructor") && password.equals("12345")) {
            Toast.makeText(MainActivity.this, "Welcome, Instructor!", Toast.LENGTH_SHORT).show();

            // Navigate to Instructor Dashboard
            Intent intent = new Intent(MainActivity.this, InstructorDashboardActivity.class);
            startActivity(intent);
        }

        // --- Else: Failure ---
        else {
            Toast.makeText(MainActivity.this, "Login Failed: Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
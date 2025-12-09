package com.majorelective.schedule_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Import TextView
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister; // Declare TextView for register link
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvGoToRegister);

        // [NEW] Bind the Forgot Password Text
        // We declare it locally here since we only use it in this method
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Login Button Logic
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();
                performLoginCheck(inputId, inputPassword);
            }
        });

        // Register Link Logic
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // [NEW] Forgot Password Link Logic
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPassActivity.class);
            startActivity(intent);
        });
    }


    private void performLoginCheck(String id, String password) {
        String role = dbHelper.checkLogin(id, password);

        if (role != null) {
            Toast.makeText(MainActivity.this, "Welcome, " + role + "!", Toast.LENGTH_SHORT).show();

            if (role.equals("admin")) {
                startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            } else if (role.equals("student")) {
                Intent intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                intent.putExtra("USERNAME", id);
                startActivity(intent);
            } else if (role.equals("instructor")) {
                Intent intent = new Intent(MainActivity.this, InstructorDashboardActivity.class);
                intent.putExtra("USERNAME", id);
                startActivity(intent);
            }
        } else {
            Toast.makeText(MainActivity.this, "Login Failed: Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
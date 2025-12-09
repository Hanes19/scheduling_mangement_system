package com.majorelective.schedule_management;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etCourse, etYear, etSection, etPassword, etConfirmPass;
    // [NEW] Field for Security Answer
    private EditText etSecurityAnswer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Bind UI Elements
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etCourse = findViewById(R.id.etRegCourse);
        etYear = findViewById(R.id.etRegYear);
        etSection = findViewById(R.id.etRegSection);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPass = findViewById(R.id.etRegConfirmPass);

        // [NEW] Bind Security Answer Field (Make sure this ID exists in your XML)
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvGoToLogin);

        // Handle Register Logic
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String course = etCourse.getText().toString().trim();
            String year = etYear.getText().toString().trim();
            String section = etSection.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirm = etConfirmPass.getText().toString().trim();

            // [NEW] Get Security Answer Text
            String securityAnswer = "";
            if (etSecurityAnswer != null) {
                securityAnswer = etSecurityAnswer.getText().toString().trim();
            }

            // Validate all fields (Added check for securityAnswer)
            if(name.isEmpty() || email.isEmpty() || course.isEmpty() ||
                    year.isEmpty() || section.isEmpty() || pass.isEmpty() || confirm.isEmpty() || securityAnswer.isEmpty()) {
                Toast.makeText(this, "Please fill all fields including the security answer", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Save to DB with all details (Including Security Answer)
                boolean success = dbHelper.registerStudent(name, email, course, year, section, pass, securityAnswer);

                if(success) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                } else {
                    Toast.makeText(this, "Registration Failed (ID/Email may already exist)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle "Back to Login" link
        tvLoginLink.setOnClickListener(v -> finish());
    }
}
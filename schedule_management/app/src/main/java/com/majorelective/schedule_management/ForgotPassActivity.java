package com.majorelective.schedule_management;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassActivity extends AppCompatActivity {


        private EditText etEmail, etSecurityCheck, etNewPass, etConfirmPass; // Added etSecurityCheck
        private Button btnReset;
        private DatabaseHelper dbHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_pass);

            dbHelper = new DatabaseHelper(this);

            etEmail = findViewById(R.id.etEmail);
            etSecurityCheck = findViewById(R.id.etSecurityAnswer); // Bind View
            etNewPass = findViewById(R.id.etNewPass);
            etConfirmPass = findViewById(R.id.etConfirmPass);
            btnReset = findViewById(R.id.btnReset);

            btnReset.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String securityAnswer = etSecurityCheck.getText().toString().trim();
                String newPass = etNewPass.getText().toString().trim();
                String confirmPass = etConfirmPass.getText().toString().trim();

                if (email.isEmpty() || securityAnswer.isEmpty() || newPass.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. VERIFY USER IDENTITY
                if (dbHelper.verifySecurityAnswer(email, securityAnswer)) {
                    // 2. IDENTITY VERIFIED -> UPDATE PASSWORD
                    boolean isUpdated = dbHelper.updatePassword(email, newPass);
                    if (isUpdated) {
                        Toast.makeText(this, "Password Reset Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error updating password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 3. VERIFICATION FAILED
                    Toast.makeText(this, "Incorrect Security Answer or Email!", Toast.LENGTH_LONG).show();
                }
            });
        }
}


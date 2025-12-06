package com.majorelective.schedule_management;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links to your activity_add_class.xml file
        setContentView(R.layout.activity_add_class);

        setupUI();
    }

    private void setupUI() {
        // 1. Find all the input fields by ID
        EditText etSubject = findViewById(R.id.etSubject);
        EditText etSection = findViewById(R.id.etSection);
        EditText etDay = findViewById(R.id.etDay);
        EditText etStartTime = findViewById(R.id.etStartTime);
        EditText etEndTime = findViewById(R.id.etEndTime);
        EditText etRoom = findViewById(R.id.etRoom);
        EditText etInstructor = findViewById(R.id.etInstructor);
        Button btnSave = findViewById(R.id.btnSaveClass);

        // 2. Add functionality to the "Save Class" button
        btnSave.setOnClickListener(v -> {
            // For now, just show a message and close the screen
            String subject = etSubject.getText().toString();

            if (subject.isEmpty()) {
                Toast.makeText(this, "Please enter a subject", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Class " + subject + " Saved!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the Dashboard
            }
        });
    }
}
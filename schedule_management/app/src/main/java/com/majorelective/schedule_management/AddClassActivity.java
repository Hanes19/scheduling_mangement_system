package com.majorelective.schedule_management;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddClassActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper; // Declare DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Initialize DB Helper
        dbHelper = new DatabaseHelper(this);

        // --- ADD THIS BLOCK HERE ---
        // Make Back Button Work
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
        // ---------------------------

        setupUI();
    }

    private void setupUI() {
        EditText etSubject = findViewById(R.id.etSubject);
        EditText etSection = findViewById(R.id.etSection);
        EditText etDay = findViewById(R.id.etDay);
        EditText etStartTime = findViewById(R.id.etStartTime);
        EditText etEndTime = findViewById(R.id.etEndTime);
        EditText etRoom = findViewById(R.id.etRoom);
        EditText etInstructor = findViewById(R.id.etInstructor);
        Button btnSave = findViewById(R.id.btnSaveClass);

        btnSave.setOnClickListener(v -> {
            String subject = etSubject.getText().toString();
            String section = etSection.getText().toString();
            String day = etDay.getText().toString();
            String start = etStartTime.getText().toString();
            String end = etEndTime.getText().toString();
            String room = etRoom.getText().toString();
            String instructor = etInstructor.getText().toString();

            if (subject.isEmpty() || section.isEmpty() || day.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else {
                // Save to Database
                boolean isInserted = dbHelper.addClass(subject, section, day, start, end, room, instructor);

                if (isInserted) {
                    Toast.makeText(this, "Class Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Dashboard
                } else {
                    Toast.makeText(this, "Error Saving Class", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
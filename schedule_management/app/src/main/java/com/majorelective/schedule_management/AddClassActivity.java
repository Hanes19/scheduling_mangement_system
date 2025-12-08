package com.majorelective.schedule_management;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddClassActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Initialize DB Helper
        dbHelper = new DatabaseHelper(this);

        // Make Back Button Work
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

        setupUI();
    }

    private void setupUI() {
        EditText etSubject = findViewById(R.id.etSubject);
        EditText etSection = findViewById(R.id.etSection);
        EditText etDay = findViewById(R.id.etDay);
        EditText etStartTime = findViewById(R.id.etStartTime);
        EditText etEndTime = findViewById(R.id.etEndTime);
        EditText etRoom = findViewById(R.id.etRoom);

        // Changed from EditText to Spinner
        Spinner spinnerInstructor = findViewById(R.id.spinnerInstructor);

        Button btnSave = findViewById(R.id.btnSaveClass);

        // --- POPULATE INSTRUCTOR SPINNER ---
        ArrayList<String> instructorsList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllInstructors();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Index 1 is KEY_NAME based on your DatabaseHelper structure
                    String name = cursor.getString(1);
                    instructorsList.add(name);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (instructorsList.isEmpty()) {
            instructorsList.add("No instructors found");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, instructorsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstructor.setAdapter(adapter);
        // -----------------------------------

        btnSave.setOnClickListener(v -> {
            String subject = etSubject.getText().toString();
            String section = etSection.getText().toString();
            String day = etDay.getText().toString();
            String start = etStartTime.getText().toString();
            String end = etEndTime.getText().toString();
            String room = etRoom.getText().toString();

            // Get selected instructor from Spinner
            String instructor = "";
            if (spinnerInstructor.getSelectedItem() != null) {
                instructor = spinnerInstructor.getSelectedItem().toString();
            }

            if (subject.isEmpty() || section.isEmpty() || day.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else if (instructor.equals("No instructors found") || instructor.isEmpty()) {
                Toast.makeText(this, "Please register an instructor first", Toast.LENGTH_SHORT).show();
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
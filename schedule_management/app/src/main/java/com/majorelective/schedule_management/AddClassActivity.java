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
import java.util.List;

public class AddClassActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    // Changed to Spinner
    private Spinner spinnerStartTime, spinnerEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        setupUI();
    }

    private void setupUI() {
        EditText etSubject = findViewById(R.id.etSubject);
        EditText etSection = findViewById(R.id.etSection);
        EditText etDay = findViewById(R.id.etDay);
        EditText etRoom = findViewById(R.id.etRoom);
        Spinner spinnerInstructor = findViewById(R.id.spinnerInstructor);

        // Bind the new Time Spinners
        spinnerStartTime = findViewById(R.id.spinnerStartTime);
        spinnerEndTime = findViewById(R.id.spinnerEndTime);

        Button btnSave = findViewById(R.id.btnSaveClass);

        // --- 1. POPULATE TIME SPINNERS (24 Hour Format) ---
        List<String> timeSlots = generateTimeSlots();
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeSlots);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerStartTime.setAdapter(timeAdapter);
        spinnerEndTime.setAdapter(timeAdapter);
        // Set a default "End Time" slightly ahead of start just for UX (optional)
        spinnerEndTime.setSelection(2);

        // --- 2. POPULATE INSTRUCTOR SPINNER ---
        ArrayList<String> instructorsList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllInstructors();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                instructorsList.add(cursor.getString(1)); // KEY_NAME
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (instructorsList.isEmpty()) instructorsList.add("No instructors found");

        ArrayAdapter<String> instAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, instructorsList);
        instAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstructor.setAdapter(instAdapter);

        // --- 3. SAVE BUTTON LOGIC ---
        btnSave.setOnClickListener(v -> {
            String subject = etSubject.getText().toString();
            String section = etSection.getText().toString();
            String day = etDay.getText().toString();
            String room = etRoom.getText().toString();

            // Get selected times from spinners
            String start = spinnerStartTime.getSelectedItem().toString();
            String end = spinnerEndTime.getSelectedItem().toString();

            String instructor = "";
            if (spinnerInstructor.getSelectedItem() != null) {
                instructor = spinnerInstructor.getSelectedItem().toString();
            }

            // Basic Empty Validation
            if (subject.isEmpty() || section.isEmpty() || day.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logic: End time must be after Start time
            if (convertTimeToInt(start) >= convertTimeToInt(end)) {
                Toast.makeText(this, "End time must be after Start time", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- 4. CHECK FOR OVERLAP ---
            if (dbHelper.isScheduleConflict(day, start, end, room)) {
                Toast.makeText(this, "Conflict detected! The room is already booked for this time.", Toast.LENGTH_LONG).show();
            } else {
                // No conflict, proceed to save
                boolean isInserted = dbHelper.addClass(subject, section, day, start, end, room, instructor);
                if (isInserted) {
                    Toast.makeText(this, "Class Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error Saving Class", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Helper to generate 24h time slots (07:00 to 22:00)
    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 7; hour <= 22; hour++) {
            slots.add(String.format("%02d:00", hour));
            if (hour != 22) { // Don't add 22:30 if 22:00 is the limit
                slots.add(String.format("%02d:30", hour));
            }
        }
        return slots;
    }

    // Helper for validation comparison
    private int convertTimeToInt(String time) {
        String[] parts = time.split(":");
        return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
    }
}
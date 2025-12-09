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
import java.util.Arrays;
import java.util.List;

public class AddClassActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spinnerStartTime, spinnerEndTime, spinnerStudentYear; // Added spinnerStudentYear

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

        // [CHANGED] Initialize Student Year Spinner
        spinnerStudentYear = findViewById(R.id.spinnerStudentYear);

        spinnerStartTime = findViewById(R.id.spinnerStartTime);
        spinnerEndTime = findViewById(R.id.spinnerEndTime);

        Button btnSave = findViewById(R.id.btnSaveClass);

        // 1. Setup Time Spinners
        List<String> timeSlots = generateTimeSlots();
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeSlots);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerStartTime.setAdapter(timeAdapter);
        spinnerEndTime.setAdapter(timeAdapter);
        spinnerEndTime.setSelection(2);

        // [CHANGED] 2. Setup Year Spinner (1 to 4)
        List<String> years = Arrays.asList("1", "2", "3", "4");
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudentYear.setAdapter(yearAdapter);

        // 3. Setup Instructor Spinner
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

        // 4. Save Button Logic
        btnSave.setOnClickListener(v -> {
            String subject = etSubject.getText().toString();
            String section = etSection.getText().toString();
            String day = etDay.getText().toString();
            String room = etRoom.getText().toString();

            // [CHANGED] Get Year
            String year = "";
            if(spinnerStudentYear.getSelectedItem() != null) {
                year = spinnerStudentYear.getSelectedItem().toString();
            }

            String start = spinnerStartTime.getSelectedItem().toString();
            String end = spinnerEndTime.getSelectedItem().toString();

            String instructor = "";
            if (spinnerInstructor.getSelectedItem() != null) {
                instructor = spinnerInstructor.getSelectedItem().toString();
            }

            if (subject.isEmpty() || section.isEmpty() || day.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (convertTimeToInt(start) >= convertTimeToInt(end)) {
                Toast.makeText(this, "End time must be after Start time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isScheduleConflict(day, start, end, room)) {
                Toast.makeText(this, "Conflict detected! The room is already booked for this time.", Toast.LENGTH_LONG).show();
            } else {
                // [CHANGED] Pass 'year' to addClass
                boolean isInserted = dbHelper.addClass(subject, section, year, day, start, end, room, instructor);
                if (isInserted) {
                    Toast.makeText(this, "Class Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error Saving Class", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 7; hour <= 22; hour++) {
            slots.add(String.format("%02d:00", hour));
            if (hour != 22) {
                slots.add(String.format("%02d:30", hour));
            }
        }
        return slots;
    }

    private int convertTimeToInt(String time) {
        String[] parts = time.split(":");
        return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
    }
}
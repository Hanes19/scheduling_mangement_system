package com.majorelective.schedule_management;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Import added
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudentDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout container;
    private Button btnLogout;
    private String studentId; // To store logged-in ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llClassContainer); // Ensure you have this ID in XML or remove this line if not used

        // [CHANGED] Get the User ID passed from MainActivity
        studentId = getIntent().getStringExtra("USERNAME");

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

        loadStudentSchedule();

        // Optional: Logout button logic if the button exists in your XML
        // btnLogout.setOnClickListener(v -> finish());
    }

    private void loadStudentSchedule() {
        container.removeAllViews(); // Clear previous views

        // 1. Get Student's Section first
        String section = dbHelper.getStudentSection(studentId);

        if (section == null) {
            showEmptyMessage("Error: Could not find student record.");
            return;
        }

        // 2. Fetch classes ONLY for that section
        Cursor cursor = dbHelper.getClassesBySection(section);

        if (cursor == null || cursor.getCount() == 0) {
            showEmptyMessage("No classes found for Section " + section);
            return;
        }

        while (cursor.moveToNext()) {
            // Get data from database
            String subject = cursor.getString(1);
            String classSection = cursor.getString(2); // Should match student section
            String day = cursor.getString(3);
            String time = cursor.getString(4) + " - " + cursor.getString(5);
            String room = cursor.getString(6);
            String instructor = cursor.getString(7);

            // Create CardView
            CardView card = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 24);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.WHITE);
            card.setRadius(16f);
            card.setContentPadding(32, 32, 32, 32);
            card.setCardElevation(8f);

            // Create Content Layout
            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);

            // Add details
            contentLayout.addView(createTextView(subject, 18, true, Color.parseColor("#333333")));
            contentLayout.addView(createTextView("Section: " + classSection, 14, false, Color.DKGRAY));
            contentLayout.addView(createTextView(day + " | " + time, 14, false, Color.parseColor("#A685FA")));
            contentLayout.addView(createTextView("Room: " + room, 14, false, Color.DKGRAY));
            contentLayout.addView(createTextView("Instructor: " + instructor, 14, false, Color.DKGRAY));

            card.addView(contentLayout);
            container.addView(card);
        }
        cursor.close();
    }

    private void showEmptyMessage(String message) {
        TextView emptyView = new TextView(this);
        emptyView.setText(message);
        emptyView.setPadding(20, 20, 20, 20);
        emptyView.setTextSize(16);
        emptyView.setTextColor(Color.GRAY);
        container.addView(emptyView);
    }

    private TextView createTextView(String text, float size, boolean isBold, int color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(color);
        if (isBold) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setPadding(0, 0, 0, 8);
        return tv;
    }
}
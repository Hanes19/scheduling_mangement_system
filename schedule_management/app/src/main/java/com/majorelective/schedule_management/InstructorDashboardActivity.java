package com.majorelective.schedule_management;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class InstructorDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout container;
    private TextView tvWelcome;
    private String instructorLoginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_dashboard);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llInstructorClassContainer);
        tvWelcome = findViewById(R.id.tvWelcomeInstructor);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Get the Login ID passed from MainActivity
        instructorLoginId = getIntent().getStringExtra("USERNAME");

        loadMySchedule();

        btnLogout.setOnClickListener(v -> {
            finish(); // Returns to Login
        });
    }

    private void loadMySchedule() {
        // 1. Get the Instructor's Name using their Login ID
        String instructorName = dbHelper.getInstructorNameById(instructorLoginId);

        if (instructorName == null) {
            tvWelcome.setText("Welcome, Instructor");
            showEmptyMessage("Could not load profile.");
            return;
        }

        tvWelcome.setText("Welcome, " + instructorName);

        // 2. Fetch classes assigned specifically to this instructor
        Cursor cursor = dbHelper.getClassesByInstructor(instructorName);

        container.removeAllViews();

        if (cursor == null || cursor.getCount() == 0) {
            showEmptyMessage("You have no classes assigned yet.");
            return;
        }

        while (cursor.moveToNext()) {
            String subject = cursor.getString(1);
            String section = cursor.getString(2);
            String day = cursor.getString(3);
            String time = cursor.getString(4) + " - " + cursor.getString(5);
            String room = cursor.getString(6);

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
            LinearLayout content = new LinearLayout(this);
            content.setOrientation(LinearLayout.VERTICAL);

            // Add Text
            content.addView(createTextView(subject, 20, true, Color.parseColor("#333333")));
            content.addView(createTextView(section + " • " + room, 16, false, Color.DKGRAY));
            content.addView(createTextView(day + " | " + time, 14, true, Color.parseColor("#A685FA")));

            card.addView(content);
            container.addView(card);
        }
    }

    private void showEmptyMessage(String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setPadding(20, 20, 20, 20);
        tv.setTextSize(16);
        tv.setTextColor(Color.GRAY);
        container.addView(tv);
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
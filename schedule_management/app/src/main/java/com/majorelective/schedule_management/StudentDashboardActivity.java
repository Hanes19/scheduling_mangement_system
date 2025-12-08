package com.majorelective.schedule_management;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudentDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout container;
    private Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llClassContainer);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });


        loadStudentSchedule();

        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadStudentSchedule() {
        Cursor cursor = dbHelper.getAllClasses();

        // Clear any placeholder text (like "No classes added yet...")
        container.removeAllViews();

        if (cursor.getCount() == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No classes available.");
            emptyView.setPadding(20, 20, 20, 20);
            container.addView(emptyView);
            return;
        }

        while (cursor.moveToNext()) {
            // 1. Get data from database
            String subject = cursor.getString(1);
            String section = cursor.getString(2);
            String day = cursor.getString(3);
            String time = cursor.getString(4) + " - " + cursor.getString(5);
            String room = cursor.getString(6);
            String instructor = cursor.getString(7);

            // 2. Create the CardView
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

            // 3. Create Content Layout
            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);

            // 4. Add details (Subject, Time, Room)
            contentLayout.addView(createTextView(subject + " (" + section + ")", 18, true, Color.parseColor("#333333")));
            contentLayout.addView(createTextView(day + " | " + time, 14, false, Color.parseColor("#A685FA"))); // Purple accent
            contentLayout.addView(createTextView("Room: " + room, 14, false, Color.DKGRAY));
            contentLayout.addView(createTextView("Instructor: " + instructor, 14, false, Color.DKGRAY));

            // Add content to card, and card to main list
            card.addView(contentLayout);
            container.addView(card);
        }
    }

    // Helper method to create styled TextViews quickly
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
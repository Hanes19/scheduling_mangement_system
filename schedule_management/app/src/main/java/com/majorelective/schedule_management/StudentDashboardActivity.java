package com.majorelective.schedule_management;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudentDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout container;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llClassContainer);
        studentId = getIntent().getStringExtra("USERNAME");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadStudentSchedule();
    }

    private void loadStudentSchedule() {
        container.removeAllViews();

        // 1. Get Student's Section
        String section = dbHelper.getStudentSection(studentId);

        // [CHANGED] 2. Get Student's Year
        String year = dbHelper.getStudentYear(studentId);

        if (section == null || year == null) {
            showEmptyMessage("Error: Could not find student record.");
            return;
        }

        // [CHANGED] 3. Fetch classes using both Section and Year
        Cursor cursor = dbHelper.getClassesForStudent(section, year);

        if (cursor == null || cursor.getCount() == 0) {
            showEmptyMessage("No classes found for Year " + year + ", Section " + section);
            return;
        }

        while (cursor.moveToNext()) {
            // Note: Update index if column order changed.
            // In onCreate: ID(0), SUBJECT(1), SECTION(2), YEAR(3), DAY(4), START(5), END(6), ROOM(7), INST(8)
            String subject = cursor.getString(1);
            String classSection = cursor.getString(2);
            // Index 3 is year, we don't necessarily need to display it since it matches
            String day = cursor.getString(4);
            String time = cursor.getString(5) + " - " + cursor.getString(6);
            String room = cursor.getString(7);
            String instructor = cursor.getString(8);

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

            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);

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
package com.majorelective.schedule_management;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MasterScheduleActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_schedule);

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llMasterScheduleContainer);


        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });


        loadSchedule();
    }

    private void loadSchedule() {
        // 1. CLEAR existing hardcoded XML views so we only see DB items
        container.removeAllViews();

        Cursor cursor = dbHelper.getAllClasses();

        // 2. Handle empty state
        if (cursor == null || cursor.getCount() == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No classes scheduled yet.");
            emptyView.setTextSize(16);
            emptyView.setPadding(32, 32, 32, 32);
            emptyView.setTextColor(Color.GRAY);
            container.addView(emptyView);
            return;
        }

        // 3. Loop through DB results
        while (cursor.moveToNext()) {
            // Retrieve data (indices based on your DatabaseHelper columns)
            String subject = cursor.getString(1);     // KEY_SUBJECT
            String section = cursor.getString(2);     // KEY_SECTION
            String day = cursor.getString(3);         // KEY_DAY
            String start = cursor.getString(4);       // KEY_START_TIME
            String end = cursor.getString(5);         // KEY_END_TIME
            String room = cursor.getString(6);        // KEY_ROOM
            String instructor = cursor.getString(7);  // KEY_INSTRUCTOR_NAME

            // --- BUILD THE CARD PROGRAMMATICALLY ---

            CardView card = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 24); // Add spacing between cards
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.WHITE);
            card.setRadius(20f);
            card.setCardElevation(8f);
            card.setContentPadding(40, 40, 40, 40);

            // Vertical Layout for text inside the card
            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.VERTICAL);

            // Subject (Header)
            TextView tvSubject = new TextView(this);
            tvSubject.setText(subject);
            tvSubject.setTextSize(20);
            tvSubject.setTextColor(Color.parseColor("#333333"));
            tvSubject.setTypeface(null, android.graphics.Typeface.BOLD);
            textLayout.addView(tvSubject);

            // Section & Room (Subtitle)
            TextView tvDetails = new TextView(this);
            tvDetails.setText("Section " + section + " | " + room);
            tvDetails.setTextSize(14);
            tvDetails.setTextColor(Color.parseColor("#666666"));
            tvDetails.setPadding(0, 8, 0, 8);
            textLayout.addView(tvDetails);

            // Instructor
            TextView tvInstructor = new TextView(this);
            tvInstructor.setText("Instructor: " + instructor);
            tvInstructor.setTextSize(14);
            tvInstructor.setTextColor(Color.parseColor("#666666"));
            textLayout.addView(tvInstructor);

            // Time (Highlighted)
            TextView tvTime = new TextView(this);
            tvTime.setText(day + " " + start + " - " + end);
            tvTime.setTextSize(14);
            tvTime.setTextColor(Color.parseColor("#A685FA")); // Purple accent
            tvTime.setTypeface(null, android.graphics.Typeface.ITALIC);
            tvTime.setPadding(0, 16, 0, 0);
            textLayout.addView(tvTime);

            // Add text layout to card, and card to main container
            card.addView(textLayout);
            container.addView(card);
        }
    }
}
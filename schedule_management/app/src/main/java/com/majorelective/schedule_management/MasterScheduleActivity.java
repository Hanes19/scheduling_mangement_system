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

        loadSchedule();
    }

    private void loadSchedule() {
        Cursor cursor = dbHelper.getAllClasses();

        if (cursor.getCount() == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No classes scheduled yet.");
            emptyView.setPadding(20, 20, 20, 20);
            container.addView(emptyView);
            return;
        }

        while (cursor.moveToNext()) {
            // Get data from database columns (indices match your DB helper)
            String subject = cursor.getString(1);
            String section = cursor.getString(2);
            String day = cursor.getString(3);
            String time = cursor.getString(4) + " - " + cursor.getString(5);
            String room = cursor.getString(6);
            String instructor = cursor.getString(7);

            // Create a CardView programmatically to display the class
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

            // Content Layout inside Card
            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);

            // Add Text Views
            contentLayout.addView(createTextView(subject + " (" + section + ")", 18, true));
            contentLayout.addView(createTextView(day + " | " + time, 14, false));
            contentLayout.addView(createTextView("Room: " + room, 14, false));
            contentLayout.addView(createTextView("Instructor: " + instructor, 14, false));

            card.addView(contentLayout);
            container.addView(card);
        }
    }

    private TextView createTextView(String text, float size, boolean isBold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(Color.DKGRAY);
        if (isBold) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setPadding(0, 0, 0, 8);
        return tv;
    }
}
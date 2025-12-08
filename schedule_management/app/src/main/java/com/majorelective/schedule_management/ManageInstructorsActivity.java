package com.majorelective.schedule_management;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ManageInstructorsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout listContainer;

    // Define all input fields
    private EditText etName, etDept, etSubject, etYear, etSection, etId, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_instructors);

        dbHelper = new DatabaseHelper(this);

        // 1. Bind ALL views (Make sure IDs match your XML)
        etName = findViewById(R.id.etInstName);
        etDept = findViewById(R.id.etInstDept);
        etSubject = findViewById(R.id.etInstSubject);  // Added
        etYear = findViewById(R.id.etInstYear);        // Added
        etSection = findViewById(R.id.etInstSection);  // Added
        etId = findViewById(R.id.etInstID);
        etPassword = findViewById(R.id.etInstPassword); // Added

        Button btnAdd = findViewById(R.id.btnAddInstructor);
        listContainer = findViewById(R.id.llInstructorListContainer);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Load list initially
        loadInstructors();

        // 2. Updated Add Button Logic
        btnAdd.setOnClickListener(v -> {
            // Get text from ALL fields
            String name = etName.getText().toString();
            String dept = etDept.getText().toString();
            String subject = etSubject.getText().toString();
            String year = etYear.getText().toString();
            String section = etSection.getText().toString();
            String id = etId.getText().toString();
            String pass = etPassword.getText().toString();

            // Simple validation
            if(name.isEmpty() || id.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Name, ID, and Password are required", Toast.LENGTH_SHORT).show();
            } else {
                // 3. Call addInstructor with ALL 7 ARGUMENTS
                boolean success = dbHelper.addInstructor(name, dept, subject, year, section, id, pass);

                if(success) {
                    Toast.makeText(this, "Instructor Added!", Toast.LENGTH_SHORT).show();
                    // Clear fields
                    etName.setText(""); etDept.setText(""); etSubject.setText("");
                    etYear.setText(""); etSection.setText(""); etId.setText(""); etPassword.setText("");

                    // Refresh the list
                    loadInstructors();
                } else {
                    Toast.makeText(this, "Error adding instructor (ID might be taken)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInstructors(); // Refresh list when returning from Edit screen
    }

    private void loadInstructors() {
        listContainer.removeAllViews();
        Cursor cursor = dbHelper.getAllInstructors();

        if (cursor == null) return;

        while (cursor.moveToNext()) {
            // Retrieve data from cursor
            // Note: Adjust indices if your table structure changes
            String name = cursor.getString(1); // name
            String dept = cursor.getString(2); // department
            String loginId = cursor.getString(3); // login_id

            // Check if column exists before accessing (safety check)
            String subject = "";
            if (cursor.getColumnCount() > 4) {
                subject = cursor.getString(4);
            }

            // --- Create CardView Programmatically ---
            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(dpToPx(4), 0, dpToPx(4), dpToPx(16));
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(Color.WHITE);
            card.setRadius(dpToPx(15));
            card.setCardElevation(dpToPx(4));

            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
            horizontalLayout.setGravity(Gravity.CENTER_VERTICAL);
            card.addView(horizontalLayout);

            // --- Left Side: Info ---
            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setOrientation(LinearLayout.VERTICAL);
            infoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            TextView tvName = new TextView(this);
            tvName.setText(name);
            tvName.setTextSize(18);
            tvName.setTextColor(Color.parseColor("#333333"));
            tvName.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
            infoLayout.addView(tvName);

            TextView tvDetails = new TextView(this);
            tvDetails.setText(dept + (subject.isEmpty() ? "" : " • " + subject));
            tvDetails.setTextSize(14);
            tvDetails.setTextColor(Color.parseColor("#666666"));
            infoLayout.addView(tvDetails);

            TextView tvId = new TextView(this);
            tvId.setText("ID: " + loginId);
            tvId.setTextSize(12);
            tvId.setTextColor(Color.parseColor("#A685FA"));
            infoLayout.addView(tvId);

            horizontalLayout.addView(infoLayout);

            // --- Right Side: Actions (Edit/Delete) ---
            LinearLayout actionsLayout = new LinearLayout(this);
            actionsLayout.setOrientation(LinearLayout.VERTICAL);

            // Edit Button
            Button btnEdit = new Button(this);
            btnEdit.setText("Edit");
            btnEdit.setTextSize(12);
            btnEdit.setAllCaps(false);
            btnEdit.setTextColor(Color.WHITE);
            btnEdit.setBackgroundResource(R.drawable.btn_rounded_purple);
            LinearLayout.LayoutParams btnEditParams = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(35));
            btnEditParams.setMargins(0, 0, 0, dpToPx(8));
            btnEdit.setLayoutParams(btnEditParams);
            btnEdit.setStateListAnimator(null);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(ManageInstructorsActivity.this, EditInstructorActivity.class);
                intent.putExtra("INSTRUCTOR_ID", loginId);
                startActivity(intent);
            });
            actionsLayout.addView(btnEdit);

            // Delete Button
            Button btnDelete = new Button(this);
            btnDelete.setText("Delete");
            btnDelete.setTextSize(12);
            btnDelete.setAllCaps(false);
            btnDelete.setTextColor(Color.WHITE);
            btnDelete.setBackgroundResource(R.drawable.btn_rounded_red);
            btnDelete.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(80), dpToPx(35)));
            btnDelete.setStateListAnimator(null);

            btnDelete.setOnClickListener(v -> {
                boolean deleted = dbHelper.deleteInstructor(loginId);
                if (deleted) {
                    Toast.makeText(this, "Instructor deleted", Toast.LENGTH_SHORT).show();
                    loadInstructors(); // Reload list
                } else {
                    Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show();
                }
            });
            actionsLayout.addView(btnDelete);

            horizontalLayout.addView(actionsLayout);
            listContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
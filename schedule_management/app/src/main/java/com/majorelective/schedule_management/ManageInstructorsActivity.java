package com.majorelective.schedule_management;

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
import androidx.core.content.ContextCompat;

public class ManageInstructorsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout listContainer;
    private EditText etName, etDept, etId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_instructors);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etInstName);
        etDept = findViewById(R.id.etInstDept);
        etId = findViewById(R.id.etInstID);
        Button btnAdd = findViewById(R.id.btnAddInstructor);
        listContainer = findViewById(R.id.llInstructorListContainer);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

        loadInstructors();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String dept = etDept.getText().toString();
            String id = etId.getText().toString();

            if(name.isEmpty() || id.isEmpty()) {
                Toast.makeText(this, "Name and Login ID are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.addInstructor(name, dept, id);
                if(success) {
                    Toast.makeText(this, "Instructor Added!", Toast.LENGTH_SHORT).show();
                    etName.setText(""); etDept.setText(""); etId.setText("");
                    // Load the updated list
                    loadInstructors();
                } else {
                    Toast.makeText(this, "Error adding instructor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadInstructors() {
        listContainer.removeAllViews();
        Cursor cursor = dbHelper.getAllInstructors();

        if (cursor == null) return;

        while (cursor.moveToNext()) {
            String name = cursor.getString(1); // Index 1 is Name
            String dept = cursor.getString(2); // Index 2 is Dept
            String loginId = cursor.getString(3); // Index 3 is Login ID

            // --- 1. Create CardView ---
            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(dpToPx(4), 0, dpToPx(4), dpToPx(16));
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(Color.WHITE);
            card.setRadius(dpToPx(15));
            card.setCardElevation(dpToPx(4));

            // --- 2. Create Horizontal Container (Inside Card) ---
            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
            horizontalLayout.setGravity(Gravity.CENTER_VERTICAL);
            card.addView(horizontalLayout);

            // --- 3. Create Info Layout (Left Side) ---
            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            );
            infoLayout.setLayoutParams(infoParams);

            // Name TextView
            TextView tvName = new TextView(this);
            tvName.setText(name);
            tvName.setTextSize(18);
            tvName.setTextColor(Color.parseColor("#333333"));
            tvName.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
            LinearLayout.LayoutParams tvNameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvNameParams.setMargins(0, 0, 0, dpToPx(4));
            tvName.setLayoutParams(tvNameParams);
            infoLayout.addView(tvName);

            // Dept TextView
            TextView tvDept = new TextView(this);
            tvDept.setText(dept);
            tvDept.setTextSize(14);
            tvDept.setTextColor(Color.parseColor("#666666"));
            LinearLayout.LayoutParams tvDeptParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvDeptParams.setMargins(0, 0, 0, dpToPx(2));
            tvDept.setLayoutParams(tvDeptParams);
            infoLayout.addView(tvDept);

            // ID TextView
            TextView tvId = new TextView(this);
            tvId.setText("ID: " + loginId);
            tvId.setTextSize(12);
            // Use the purple color resource if possible, or parse hex
            tvId.setTextColor(Color.parseColor("#A685FA"));
            infoLayout.addView(tvId);

            horizontalLayout.addView(infoLayout);

            // --- 4. Create Actions Layout (Right Side) ---
            LinearLayout actionsLayout = new LinearLayout(this);
            actionsLayout.setOrientation(LinearLayout.VERTICAL);
            actionsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));

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
            // Remove state list animator to match XML style
            btnEdit.setStateListAnimator(null);
            actionsLayout.addView(btnEdit);

            // Delete Button
            Button btnDelete = new Button(this);
            btnDelete.setText("Delete");
            btnDelete.setTextSize(12);
            btnDelete.setAllCaps(false);
            btnDelete.setTextColor(Color.WHITE);
            btnDelete.setBackgroundResource(R.drawable.btn_rounded_red);
            LinearLayout.LayoutParams btnDeleteParams = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(35));
            btnDelete.setLayoutParams(btnDeleteParams);
            btnDelete.setStateListAnimator(null);
            actionsLayout.addView(btnDelete);

            horizontalLayout.addView(actionsLayout);

            // Add the constructed card to the main list container
            listContainer.addView(card);
        }
    }

    // Helper to convert dp to pixels
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}
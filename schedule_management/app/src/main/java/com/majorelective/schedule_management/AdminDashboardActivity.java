package com.majorelective.schedule_management;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard); // Links to your XML file

        setupButtons();
    }

    private void setupButtons() {
        // 1. Link Java variables to XML components
        Button btnAddClass = findViewById(R.id.btnAddClass);
        Button btnManageInstructors = findViewById(R.id.btnManageInstructors);
        Button btnViewSchedule = findViewById(R.id.btnViewSchedule);
        Button btnLogout = findViewById(R.id.btnLogout);

        // 2. Set Listeners

        // Add Class Button
        btnAddClass.setOnClickListener(v -> {
            // Toast.makeText(this, "Add Class Clicked", Toast.LENGTH_SHORT).show();
            // Uncomment the line below when you are ready to make the Add Class page:
            startActivity(new Intent(this, AddClassActivity.class));
        });

        // Manage Instructors Button
        btnManageInstructors.setOnClickListener(v -> {
            Toast.makeText(this, "Manage Instructors Clicked", Toast.LENGTH_SHORT).show();
            // Add navigation logic here later
        });

        // View Schedule Button
        btnViewSchedule.setOnClickListener(v -> {
            Toast.makeText(this, "View Schedule Clicked", Toast.LENGTH_SHORT).show();
            // Add navigation logic here later
        });

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            // Close this activity and return to the previous one (Login)
            finish();
        });
    }
}
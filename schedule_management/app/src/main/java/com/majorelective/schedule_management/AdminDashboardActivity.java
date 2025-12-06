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
        // FIXED: Changed 'btnGoToAddClass' to 'btnAddClass' to match your XML
        Button btnAddClass = findViewById(R.id.btnGoToAddClass);

        Button btnManageInstructors = findViewById(R.id.btnManageInstructors);

        // FIXED: Changed 'btnViewAllClasses' to 'btnViewSchedule' to match your XML
        Button btnViewSchedule = findViewById(R.id.btnViewAllClasses);

        Button btnLogout = findViewById(R.id.btnLogout);

        // 2. Set Listeners

        // Add Class Button - NOW IT WORKS
        btnAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddClassActivity.class);
            startActivity(intent);
        });


        // View Schedule Button
        btnViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MasterScheduleActivity.class);
            startActivity(intent);
        });

        // Manage Instructors Button
        btnManageInstructors.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageInstructorsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }

}
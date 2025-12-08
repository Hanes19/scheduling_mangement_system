package com.majorelective.schedule_management;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditInstructorActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etName, etDept, etSubject, etYear, etSection, etId, etPassword;
    private String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instructor);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        etName = findViewById(R.id.etEditName);
        etDept = findViewById(R.id.etEditDept);
        etSubject = findViewById(R.id.etEditSubject);
        etYear = findViewById(R.id.etEditYear);
        etSection = findViewById(R.id.etEditSection);
        etId = findViewById(R.id.etEditID);
        etPassword = findViewById(R.id.etEditPassword);
        Button btnSave = findViewById(R.id.btnSaveChanges);

        // Get ID passed from Intent
        currentId = getIntent().getStringExtra("INSTRUCTOR_ID");

        // Load Data
        loadInstructorData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Save Button Logic
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String dept = etDept.getText().toString();
            String subject = etSubject.getText().toString();
            String year = etYear.getText().toString();
            String section = etSection.getText().toString();
            String password = etPassword.getText().toString(); // Optional

            if (name.isEmpty() || dept.isEmpty()) {
                Toast.makeText(this, "Name and Department are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.updateInstructor(currentId, name, dept, subject, year, section, password);
                if (success) {
                    Toast.makeText(this, "Changes Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadInstructorData() {
        Cursor cursor = dbHelper.getInstructorById(currentId);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(1)); // name
            etDept.setText(cursor.getString(2)); // dept
            etId.setText(cursor.getString(3));   // login_id

            // Note: Indices based on DatabaseHelper table columns
            // Assuming we update DatabaseHelper to include these columns
            if(cursor.getColumnCount() > 4) {
                etSubject.setText(cursor.getString(4));
                etYear.setText(cursor.getString(5));
                etSection.setText(cursor.getString(6));
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Error loading instructor", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
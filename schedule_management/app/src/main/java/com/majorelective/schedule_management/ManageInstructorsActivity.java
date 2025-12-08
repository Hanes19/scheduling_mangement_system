package com.majorelective.schedule_management;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
                    listContainer.removeAllViews(); // Clear list
                    loadInstructors(); // Refresh list
                } else {
                    Toast.makeText(this, "Error adding instructor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadInstructors() {
        Cursor cursor = dbHelper.getAllInstructors();
        while (cursor.moveToNext()) {
            String name = cursor.getString(1); // Index 1 is Name
            String dept = cursor.getString(2); // Index 2 is Dept

            TextView tv = new TextView(this);
            tv.setText("• " + name + " (" + dept + ")");
            tv.setTextSize(16f);
            tv.setPadding(16, 16, 16, 16);
            listContainer.addView(tv);
        }
    }
}
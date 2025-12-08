package com.majorelective.schedule_management;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ScheduleManager.db";
    // INCREMENTED VERSION TO 4 TO ADD NEW INSTRUCTOR FIELDS
    private static final int DATABASE_VERSION = 5;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_INSTRUCTORS = "instructors";
    private static final String TABLE_STUDENTS = "students";

    // Common Column Names
    private static final String KEY_ID = "id";

    // USERS Table Columns (Authentication)
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ROLE = "role";

    // STUDENTS Table Columns
    private static final String KEY_STUDENT_NAME = "name";
    private static final String KEY_STUDENT_EMAIL = "email";
    private static final String KEY_STUDENT_COURSE = "course";
    private static final String KEY_STUDENT_YEAR = "year_level";
    private static final String KEY_STUDENT_SECTION = "section";

    // INSTRUCTORS Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_DEPT = "department";
    private static final String KEY_LOGIN_ID = "login_id";
    // NEW COLUMNS
    private static final String KEY_INST_SUBJECT = "subject";
    private static final String KEY_INST_YEAR = "year_level";
    private static final String KEY_INST_SECTION = "section";


    // CLASSES Table Columns
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_SECTION = "section";
    private static final String KEY_DAY = "day";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_ROOM = "room";
    private static final String KEY_INSTRUCTOR_NAME = "instructor_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Users Table (Auth)
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // 2. Classes Table
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SUBJECT + " TEXT,"
                + KEY_SECTION + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_START_TIME + " TEXT,"
                + KEY_END_TIME + " TEXT,"
                + KEY_ROOM + " TEXT,"
                + KEY_INSTRUCTOR_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CLASSES_TABLE);

        // 3. Instructors Table (UPDATED)
        String CREATE_INSTRUCTORS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTORS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_DEPT + " TEXT,"
                + KEY_LOGIN_ID + " TEXT,"
                + KEY_INST_SUBJECT + " TEXT,"
                + KEY_INST_YEAR + " TEXT,"
                + KEY_INST_SECTION + " TEXT" + ")";
        db.execSQL(CREATE_INSTRUCTORS_TABLE);

        // 4. Students Table
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_STUDENT_NAME + " TEXT,"
                + KEY_STUDENT_EMAIL + " TEXT,"
                + KEY_STUDENT_COURSE + " TEXT,"
                + KEY_STUDENT_YEAR + " TEXT,"
                + KEY_STUDENT_SECTION + " TEXT" + ")";
        db.execSQL(CREATE_STUDENTS_TABLE);

        // Insert Default Admin
        insertDefaultUser(db, "admin", "admin123", "admin");
    }

    private void insertDefaultUser(SQLiteDatabase db, String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_ROLE, role);
        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // --- AUTH OPERATIONS ---
    public String checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ROLE},
                KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        return null;
    }

    public boolean registerStudent(String name, String email, String course, String year, String section, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(KEY_USERNAME, email);
            userValues.put(KEY_PASSWORD, password);
            userValues.put(KEY_ROLE, "student");
            long result1 = db.insert(TABLE_USERS, null, userValues);

            ContentValues studentValues = new ContentValues();
            studentValues.put(KEY_STUDENT_NAME, name);
            studentValues.put(KEY_STUDENT_EMAIL, email);
            studentValues.put(KEY_STUDENT_COURSE, course);
            studentValues.put(KEY_STUDENT_YEAR, year);
            studentValues.put(KEY_STUDENT_SECTION, section);
            long result2 = db.insert(TABLE_STUDENTS, null, studentValues);

            if (result1 != -1 && result2 != -1) {
                db.setTransactionSuccessful();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }

    // --- CLASS OPERATIONS ---
    public boolean addClass(String subject, String section, String day, String start, String end, String room, String instructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, subject);
        values.put(KEY_SECTION, section);
        values.put(KEY_DAY, day);
        values.put(KEY_START_TIME, start);
        values.put(KEY_END_TIME, end);
        values.put(KEY_ROOM, room);
        values.put(KEY_INSTRUCTOR_NAME, instructor);
        long result = db.insert(TABLE_CLASSES, null, values);
        return result != -1;
    }

    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
    }

    public Cursor getClassesByInstructor(String instructorName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " WHERE " + KEY_INSTRUCTOR_NAME + " = ?", new String[]{instructorName});
    }

    // --- INSTRUCTOR OPERATIONS (UPDATED) ---

    // Updated to take all fields
    public boolean addInstructor(String name, String department, String subject, String year, String section, String loginId, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Add details to Instructors Table
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name);
            values.put(KEY_DEPT, department);
            values.put(KEY_LOGIN_ID, loginId);
            values.put(KEY_INST_SUBJECT, subject);
            values.put(KEY_INST_YEAR, year);
            values.put(KEY_INST_SECTION, section);
            long result1 = db.insert(TABLE_INSTRUCTORS, null, values);

            // 2. Add login credential to Users Table
            ContentValues userValues = new ContentValues();
            userValues.put(KEY_USERNAME, loginId);
            userValues.put(KEY_PASSWORD, password); // Use provided password
            userValues.put(KEY_ROLE, "instructor");
            long result2 = db.insertWithOnConflict(TABLE_USERS, null, userValues, SQLiteDatabase.CONFLICT_REPLACE);

            if (result1 != -1 && result2 != -1) {
                db.setTransactionSuccessful();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }

    // Get specific instructor by ID (Primary Key) for editing
    public Cursor getInstructorById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INSTRUCTORS + " WHERE " + KEY_LOGIN_ID + " = ?", new String[]{id});
    }

    // Delete Instructor
    public boolean deleteInstructor(String loginId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Delete from Instructors table
            int result1 = db.delete(TABLE_INSTRUCTORS, KEY_LOGIN_ID + "=?", new String[]{loginId});
            // 2. Delete login credentials
            int result2 = db.delete(TABLE_USERS, KEY_USERNAME + "=?", new String[]{loginId});

            if (result1 > 0) { // Should delete at least the profile
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    // Update Instructor
    public boolean updateInstructor(String originalId, String name, String dept, String subject, String year, String section, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name);
            values.put(KEY_DEPT, dept);
            values.put(KEY_INST_SUBJECT, subject);
            values.put(KEY_INST_YEAR, year);
            values.put(KEY_INST_SECTION, section);

            // Update profile
            int result1 = db.update(TABLE_INSTRUCTORS, values, KEY_LOGIN_ID + "=?", new String[]{originalId});

            // Update password if provided
            if (newPassword != null && !newPassword.isEmpty()) {
                ContentValues userValues = new ContentValues();
                userValues.put(KEY_PASSWORD, newPassword);
                db.update(TABLE_USERS, userValues, KEY_USERNAME + "=?", new String[]{originalId});
            }

            if (result1 > 0) {
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public Cursor getAllInstructors() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INSTRUCTORS, null);
    }

    public String getInstructorNameById(String loginId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INSTRUCTORS, new String[]{KEY_NAME},
                KEY_LOGIN_ID + "=?", new String[]{loginId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        return null;
    }
    // [NEW] Get Student's Section by their Email/ID
    public String getStudentSection(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_STUDENT_SECTION},
                KEY_STUDENT_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String section = cursor.getString(0);
                cursor.close();
                return section;
            }
            cursor.close();
        }
        return null; // Return null if student not found
    }

    // [NEW] Get Classes only for a specific Section
    public Cursor getClassesBySection(String section) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Filters the classes table where the 'section' column matches the student's section
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " WHERE " + KEY_SECTION + " = ?", new String[]{section});
    }

    // Check for scheduling conflicts (Overlaps)
    public boolean isScheduleConflict(String day, String startTime, String endTime, String room) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Select all classes happening on the same day in the same room
        Cursor cursor = db.rawQuery("SELECT " + KEY_START_TIME + ", " + KEY_END_TIME +
                        " FROM " + TABLE_CLASSES +
                        " WHERE " + KEY_DAY + "=? AND " + KEY_ROOM + "=?",
                new String[]{day, room});

        int newStart = convertTimeToInt(startTime);
        int newEnd = convertTimeToInt(endTime);

        if (cursor.moveToFirst()) {
            do {
                String dbStartStr = cursor.getString(0);
                String dbEndStr = cursor.getString(1);

                int dbStart = convertTimeToInt(dbStartStr);
                int dbEnd = convertTimeToInt(dbEndStr);

                // LOGIC: Check for Overlap
                // (NewStart < ExistingEnd) AND (NewEnd > ExistingStart)
                if (newStart < dbEnd && newEnd > dbStart) {
                    cursor.close();
                    return true; // Conflict found!
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return false; // No conflict
    }

    // Helper to convert "HH:mm" string to minutes (e.g., "01:30" -> 90)
    private int convertTimeToInt(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return (hours * 60) + minutes;
        } catch (Exception e) {
            return 0;
        }
    }
}
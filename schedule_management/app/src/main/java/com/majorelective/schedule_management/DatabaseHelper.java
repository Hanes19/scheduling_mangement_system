package com.majorelective.schedule_management;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ScheduleManager.db";
    // INCREMENTED VERSION TO 3 TO APPLY NEW SCHEMA CHANGES
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_INSTRUCTORS = "instructors";
    private static final String TABLE_STUDENTS = "students";

    // Common Column Names
    private static final String KEY_ID = "id";

    // USERS Table Columns (Authentication)
    private static final String KEY_USERNAME = "username"; // This is the Email/ID
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ROLE = "role";

    // STUDENTS Table Columns (Profile Data)
    private static final String KEY_STUDENT_NAME = "name";
    private static final String KEY_STUDENT_EMAIL = "email"; // Foreign key link to users
    private static final String KEY_STUDENT_COURSE = "course";
    private static final String KEY_STUDENT_YEAR = "year_level";
    private static final String KEY_STUDENT_SECTION = "section";

    // INSTRUCTORS Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_DEPT = "department";
    private static final String KEY_LOGIN_ID = "login_id";

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

        // 3. Instructors Table
        String CREATE_INSTRUCTORS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTORS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_DEPT + " TEXT,"
                + KEY_LOGIN_ID + " TEXT" + ")";
        db.execSQL(CREATE_INSTRUCTORS_TABLE);

        // 4. Students Table (Detailed Profile)
        // This table is separate for finer data access as requested
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_STUDENT_NAME + " TEXT,"
                + KEY_STUDENT_EMAIL + " TEXT,"
                + KEY_STUDENT_COURSE + " TEXT,"
                + KEY_STUDENT_YEAR + " TEXT,"
                + KEY_STUDENT_SECTION + " TEXT" + ")";
        db.execSQL(CREATE_STUDENTS_TABLE);

        // Insert Defaults
        insertDefaultUser(db, "admin", "admin123", "admin");
    }

    private void insertDefaultUser(SQLiteDatabase db, String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_ROLE, role);
        // Ignore conflict if default admin already exists
        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        // Create new ones
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

    // Updated to include Course, Year, and Section
    public boolean registerStudent(String name, String email, String course, String year, String section, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Start Transaction for safety
        try {
            // 1. Create User Login (Auth)
            ContentValues userValues = new ContentValues();
            userValues.put(KEY_USERNAME, email);
            userValues.put(KEY_PASSWORD, password);
            userValues.put(KEY_ROLE, "student");
            long result1 = db.insert(TABLE_USERS, null, userValues);

            // 2. Create Student Profile (Data)
            ContentValues studentValues = new ContentValues();
            studentValues.put(KEY_STUDENT_NAME, name);
            studentValues.put(KEY_STUDENT_EMAIL, email);
            studentValues.put(KEY_STUDENT_COURSE, course);
            studentValues.put(KEY_STUDENT_YEAR, year);
            studentValues.put(KEY_STUDENT_SECTION, section);
            long result2 = db.insert(TABLE_STUDENTS, null, studentValues);

            if (result1 != -1 && result2 != -1) {
                db.setTransactionSuccessful(); // Commit
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

    // --- INSTRUCTOR OPERATIONS ---

    public boolean addInstructor(String name, String department, String loginId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DEPT, department);
        values.put(KEY_LOGIN_ID, loginId);

        // Add login for instructor (default pass 12345)
        insertDefaultUser(db, loginId, "12345", "instructor");

        long result = db.insert(TABLE_INSTRUCTORS, null, values);
        return result != -1;
    }

    public Cursor getAllInstructors() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INSTRUCTORS, null);
    }
}
package com.majorelective.schedule_management;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ScheduleManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_INSTRUCTORS = "instructors";

    // Common Column Names
    private static final String KEY_ID = "id";

    // USERS Table Columns
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ROLE = "role"; // admin, student, instructor

    // CLASSES Table Columns
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_SECTION = "section";
    private static final String KEY_DAY = "day";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_ROOM = "room";
    private static final String KEY_INSTRUCTOR_NAME = "instructor_name";

    // INSTRUCTORS Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_DEPT = "department";
    private static final String KEY_LOGIN_ID = "login_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Classes Table
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

        // Create Instructors Table
        String CREATE_INSTRUCTORS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTORS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_DEPT + " TEXT,"
                + KEY_LOGIN_ID + " TEXT" + ")";
        db.execSQL(CREATE_INSTRUCTORS_TABLE);

        // Insert Default Admin User
        insertDefaultUser(db, "admin", "admin123", "admin");
        insertDefaultUser(db, "student", "12345", "student");
        insertDefaultUser(db, "instructor", "12345", "instructor");
    }

    private void insertDefaultUser(SQLiteDatabase db, String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_ROLE, role);
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTORS);
        onCreate(db);
    }

    // --- USER OPERATIONS ---

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
        return null; // Login failed
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

    // Helper to get all classes (for displaying later)
    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
    }
}
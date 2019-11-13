package com.capstone.autofix.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper{
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FNAME = "fullname";
    private static final String KEY_ADDR = "address";
    private static final String KEY_PHONE = "contact";
    private static final String KEY_USER = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "cid";
    private static final String KEY_PHOTO = "photo";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_FNAME + " TEXT,"+ KEY_ADDR +" TEXT,"
                + KEY_PHONE + " TEXT, " + KEY_USER + " TEXT, " + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_PHOTO + " VARCHAR(255)" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String fname, String addr, String contact, String username, String email, String cid, String photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FNAME, fname); // Name
        values.put(KEY_ADDR, addr);
        values.put(KEY_PHONE, contact);
        values.put(KEY_USER, username);
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, cid); // Email
        values.put(KEY_PHOTO, photo); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("fullname", cursor.getString(1));
            user.put("address", cursor.getString(2));
            user.put("contact", cursor.getString(3));
            user.put("username", cursor.getString(4));
            user.put("email", cursor.getString(5));
            user.put("cid", cursor.getString(6));
            user.put("photo", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public void updateDetails(String fname, String addr, String contact, String username, String email, String cid, String photo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_FNAME, fname); // Name
        cv.put(KEY_ADDR, addr);
        cv.put(KEY_PHONE, contact);
        cv.put(KEY_USER, username);
        cv.put(KEY_EMAIL, email); // Email
        cv.put(KEY_UID, cid); // Email
        cv.put(KEY_PHOTO, photo); // Created At

        long id = db.update(TABLE_USER, cv, "cid = ?", new String[]{cid});
        db.close(); // Closing database connection

        Log.d(TAG, "User updated in sqlite:" + id);
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}

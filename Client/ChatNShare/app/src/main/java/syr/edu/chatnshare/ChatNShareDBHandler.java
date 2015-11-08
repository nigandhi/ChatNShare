/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 17-Apr-15.
 *
 * File: ChatNShareDBHandler.java
 * Functionalities:
 * Creates database and provides functions for adding,deleting and retrieving content from the database.
 *
 */
package syr.edu.chatnshare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

//DB Handler Class - using database for storing the chat history locally
public class ChatNShareDBHandler extends SQLiteOpenHelper {

    //Variable Declarations
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "chatHistory";
    private static final String TABLE_HISTORY = "history";

    //Column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TEXT = "text";

    public ChatNShareDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method for Creating History Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_TEXT + " TEXT" + ")";
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    // Upgrading DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);

        // Create tables again
        onCreate(db);
    }

    // Adding Chat messages to DB
    public void addMessage(ChatNShareDBMethods message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, message.getName()); // Chat User
        values.put(KEY_TEXT, message.getText()); // Chat Message

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    // Retrieving History
    public List<ChatNShareDBMethods> getHistory() {
        List<ChatNShareDBMethods> history = new ArrayList<ChatNShareDBMethods>();
        // Fetching History from table
        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ChatNShareDBMethods hist = new ChatNShareDBMethods();
                hist.setID(Integer.parseInt(cursor.getString(0)));
                hist.setName(cursor.getString(1));
                hist.setText(cursor.getString(2));
                // Adding message to list
                history.add(hist);
            } while (cursor.moveToNext());
        }

        // return history
        return history;
    }

    // Deleting History
    public void deleteHistory() {

        getWritableDatabase().execSQL("Delete from "+TABLE_HISTORY+";");

    }

}

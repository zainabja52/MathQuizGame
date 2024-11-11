package com.example.myapplication2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizapp.db";
    private static final int DATABASE_VERSION = 1;

    // User table and columns
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_BIRTHDATE = "birthdate";

    // Scores table and columns
    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_SCORE_ID = "id";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_BIRTHDATE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NICKNAME + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_SCORES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public long addUser(String username, String email, String birthdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_BIRTHDATE, birthdate);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public boolean isNicknameExists(String nickname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?", new String[]{nickname}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public long insertScore(String nickname, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, nickname);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        long result = db.insert(TABLE_SCORES, null, values);
        db.close();
        return result;
    }

    public List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES + " ORDER BY " + COLUMN_SCORE + " DESC LIMIT " + limit, null);

        if (cursor.moveToFirst()) {
            do {
                int scoreIdIndex = cursor.getColumnIndex(COLUMN_SCORE_ID);
                int nicknameIndex = cursor.getColumnIndex(COLUMN_NICKNAME);
                int scoreIndex = cursor.getColumnIndex(COLUMN_SCORE);
                int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

                if (scoreIdIndex != -1 && nicknameIndex != -1 && scoreIndex != -1 && timestampIndex != -1) {
                    Score score = new Score(
                            cursor.getInt(scoreIdIndex),
                            cursor.getString(nicknameIndex),
                            cursor.getInt(scoreIndex),
                            cursor.getString(timestampIndex)
                    );
                    scores.add(score);
                } else {
                    Log.e("DBHelper", "One or more columns are missing in the cursor.");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scores;
    }

    public List<Score> getPlayerScores(String playerName) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES + " WHERE " + COLUMN_NICKNAME + " = ? ORDER BY " + COLUMN_TIMESTAMP + " DESC", new String[]{playerName});

        if (cursor.moveToFirst()) {
            do {
                int scoreIdIndex = cursor.getColumnIndex(COLUMN_SCORE_ID);
                int nicknameIndex = cursor.getColumnIndex(COLUMN_NICKNAME);
                int scoreIndex = cursor.getColumnIndex(COLUMN_SCORE);
                int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

                if (scoreIdIndex != -1 && nicknameIndex != -1 && scoreIndex != -1 && timestampIndex != -1) {
                    Score score = new Score(
                            cursor.getInt(scoreIdIndex),
                            cursor.getString(nicknameIndex),
                            cursor.getInt(scoreIndex),
                            cursor.getString(timestampIndex)
                    );
                    scores.add(score);
                } else {
                    Log.e("DBHelper", "One or more columns are missing in the cursor.");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scores;
    }

    public int getTotalPlayers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT " + COLUMN_USERNAME + ") FROM " + TABLE_USERS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public double getAverageScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(" + COLUMN_SCORE + ") FROM " + TABLE_SCORES, null);
        double average = 0;
        if (cursor.moveToFirst()) {
            average = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return average;
    }

    public Score getHighestScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES + " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 1", null);
        Score score = null;
        if (cursor.moveToFirst()) {
            int scoreIdIndex = cursor.getColumnIndex(COLUMN_SCORE_ID);
            int nicknameIndex = cursor.getColumnIndex(COLUMN_NICKNAME);
            int scoreIndex = cursor.getColumnIndex(COLUMN_SCORE);
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

            if (scoreIdIndex != -1 && nicknameIndex != -1 && scoreIndex != -1 && timestampIndex != -1) {
                score = new Score(
                        cursor.getInt(scoreIdIndex),
                        cursor.getString(nicknameIndex),
                        cursor.getInt(scoreIndex),
                        cursor.getString(timestampIndex)
                );
            } else {
                Log.e("DBHelper", "Failed to retrieve the highest score due to missing column in cursor.");
            }
        }
        cursor.close();
        db.close();
        return score;
    }
}

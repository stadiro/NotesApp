package com.example.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.Nullable;

public class NotesDatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes_db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_REMIND_TIME = "remind_time";

    public NotesDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONTENT + " TEXT, "
                + COLUMN_CREATED_AT + " DATETIME,"
                + COLUMN_REMIND_TIME + " LONG)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Метод для добавления заметки в БД с текущим временем устройства
    public void addNote(String content,  long remindTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, content);

        // Получаем текущее время с устройства
        String currentTime = getCurrentTime();
        values.put(COLUMN_CREATED_AT, currentTime);

        values.put(COLUMN_REMIND_TIME, remindTime);

        db.insert(TABLE_NOTES, null, values);
        db.close();
    }

    // Метод для получения всех заметок
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_CREATED_AT + " DESC", null);
    }

    // Метод для получения заметки по ID
    public Cursor getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTES, null, COLUMN_ID + " = ?", new String[]{String.valueOf(noteId)}, null, null, null);
    }

    // Метод для обновления заметки
    public void updateNote(int noteId, String content, long remindTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, content);  // Обновляем содержание заметки
        values.put(COLUMN_REMIND_TIME, remindTime);  // Обновляем время напоминания

        // Обновляем заметку по ID
        db.update(TABLE_NOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    // Метод для удаления заметки
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Метод для получения текущего времени с устройства
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}
package com.example.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {

    private NotesDatabaseHelper dbHelper;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private Button createNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация базы данных
        dbHelper = new NotesDatabaseHelper(this);

        // Инициализация RecyclerView
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация кнопки создания заметки
        createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        // Создание канала уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Канал уведомлений";
            String description = "Канал для уведомлений";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("reminder_channel", name, importance);
            channel.setDescription(description);

            // Регистрируем канал уведомлений
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Обновление списка заметок
        updateNotesList();
    }

    // Метод для обновления списка заметок
    private void updateNotesList() {
        Cursor cursor = dbHelper.getAllNotes();
        notesAdapter = new NotesAdapter(cursor, this);
        notesRecyclerView.setAdapter(notesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем список заметок каждый раз при возвращении на главный экран
        updateNotesList();
    }

    @Override
    public void onEditClick(int noteId) {
        // Получаем данные заметки по ID
        Cursor cursor = dbHelper.getNoteById(noteId);
        if (cursor != null && cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_CONTENT));
            // Отправляем данные в AddNoteActivity для редактирования
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("NOTE_ID", noteId);  // Передаем ID заметки
            intent.putExtra("NOTE_CONTENT", content);  // Передаем текст заметки
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteClick(int noteId) {
        dbHelper.deleteNote(noteId);
        // Обновляем список заметок после удаления
        updateNotesList();
        Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
    }
}

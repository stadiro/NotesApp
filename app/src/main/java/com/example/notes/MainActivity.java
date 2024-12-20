package com.example.notes;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {

    private NotesDatabaseHelper dbHelper;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private Button createNoteButton;
    private EditText searchField; // Поле для поиска

    private List<Note> notesList; // Список всех заметок

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new NotesDatabaseHelper(this);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        searchField = findViewById(R.id.searchField); // Инициализируем поле поиска

        // Обработчик изменений в поле поиска
        searchField.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterNotes(charSequence.toString()); // Фильтруем заметки при изменении текста
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {
            }
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

        updateNotesList(); // Загружаем все заметки из базы данных
    }

    // Метод для обновления списка заметок
    private void updateNotesList() {
        Cursor cursor = dbHelper.getAllNotes();
        notesList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_ID));
                String content = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_CONTENT));
                String createdAt = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_CREATED_AT));
                long remindTime = cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_REMIND_TIME));

                notesList.add(new Note(id, content, createdAt, remindTime));
            }
            cursor.close();
        }

        if (notesAdapter == null) {
            notesAdapter = new NotesAdapter(notesList, this);
            notesRecyclerView.setAdapter(notesAdapter);
        } else {
            notesAdapter.setNotesList(notesList);
        }
    }

    // Метод для фильтрации заметок по содержимому
    private void filterNotes(String query) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : notesList) {
            if (note.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(note);
            }
        }
        notesAdapter.setNotesList(filteredList); // Обновляем адаптер с отфильтрованным списком
    }

    @Override
    public void onEditClick(int noteId) {
        Cursor cursor = dbHelper.getNoteById(noteId);
        if (cursor != null && cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_CONTENT));
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("NOTE_ID", noteId);
            intent.putExtra("NOTE_CONTENT", content);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotesList(); // Обновляем список при возвращении в MainActivity
    }

    @Override
    public void onDeleteClick(int noteId) {
        // Создаем диалог для подтверждения удаления
        new AlertDialog.Builder(this)
                .setMessage("Вы уверены, что хотите удалить эту заметку?")
                .setCancelable(false)
                .setPositiveButton("Да", (dialog, id) -> {
                    // Если пользователь подтверждает, удаляем заметку
                    dbHelper.deleteNote(noteId);
                    updateNotesList();
                    Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Нет", (dialog, id) -> {
                    // Если пользователь отменяет, просто закрываем диалог
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}

package com.example.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddNoteActivity extends AppCompatActivity {

    private EditText noteContentEditText;
    private Button saveButton;
    private Button addReminderButton;  // Кнопка для добавления напоминания
    private NotesDatabaseHelper dbHelper;
    private int noteId = -1;  // Значение по умолчанию для нового элемента
    private long remindTime = -1;  // Время напоминания в миллисекундах

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Инициализация базы данных
        dbHelper = new NotesDatabaseHelper(this);

        // Инициализация компонентов
        noteContentEditText = findViewById(R.id.noteContentEditText);
        saveButton = findViewById(R.id.saveButton);
        addReminderButton = findViewById(R.id.addReminderButton);  // Кнопка для добавления напоминания

        // Проверка на редактирование: если передан ID заметки, загрузить данные
        Intent intent = getIntent();
        if (intent != null) {
            noteId = intent.getIntExtra("NOTE_ID", -1);
            String content = intent.getStringExtra("NOTE_CONTENT");
            if (noteId != -1 && content != null) {
                noteContentEditText.setText(content);  // Загружаем содержимое для редактирования
            }
        }

        // Обработчик кнопки "Сохранить"
        saveButton.setOnClickListener(v -> {
            String noteContent = noteContentEditText.getText().toString().trim();
            if (!noteContent.isEmpty()) {
                if (noteId == -1) {
                    // Создание новой заметки с напоминанием
                    dbHelper.addNote(noteContent, remindTime);
                    Toast.makeText(AddNoteActivity.this, "Заметка добавлена", Toast.LENGTH_SHORT).show();
                } else {
                    // Редактирование существующей заметки с обновленным напоминанием
                    dbHelper.updateNote(noteId, noteContent, remindTime);
                    Toast.makeText(AddNoteActivity.this, "Заметка обновлена", Toast.LENGTH_SHORT).show();
                }
                finish();  // Закрыть активность и вернуться на главный экран
            } else {
                Toast.makeText(AddNoteActivity.this, "Содержание не может быть пустым", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик кнопки "Добавить напоминание"
        addReminderButton.setOnClickListener(v -> {
            // Открыть диалог для выбора даты
            openDatePicker();
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddNoteActivity.this,
                (view, year, month, dayOfMonth) -> {
                    // Обработка выбора даты
                    calendar.set(year, month, dayOfMonth);
                    openTimePicker(calendar);  // После выбора даты откроем выбор времени
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Запрещаем выбор прошлого дня
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void openTimePicker(Calendar calendar) {
        // Сохраняем текущие время и дату для сравнения
        long currentTimeMillis = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Если выбранная дата - сегодняшняя, проверяем минимальное время
        if (isSameDay(calendar, now)) {
            hour = now.get(Calendar.HOUR_OF_DAY);
            minute = now.get(Calendar.MINUTE) + 1; // Следующая минута
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddNoteActivity.this,
                (view, selectedHour, selectedMinute) -> {
                    // Устанавливаем выбранное время
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    // Проверяем, чтобы выбранное время не было в прошлом
                    if (calendar.getTimeInMillis() < currentTimeMillis) {
                        Toast.makeText(AddNoteActivity.this, "Выбранное время уже прошло. Выберите заново.", Toast.LENGTH_SHORT).show();
                        openTimePicker(calendar); // Повторно открываем выбор времени
                    } else {
                        remindTime = calendar.getTimeInMillis();
                        // Устанавливаем напоминание с помощью ReminderHelper
                        ReminderHelper.setReminder(AddNoteActivity.this, remindTime, noteContentEditText.getText().toString());
                        Toast.makeText(AddNoteActivity.this, "Напоминание установлено", Toast.LENGTH_SHORT).show();
                    }
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    // Метод для проверки, совпадают ли две даты (без учета времени)
    private boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }
}

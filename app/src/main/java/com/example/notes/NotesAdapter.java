package com.example.notes;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private Cursor cursor;
    private OnNoteClickListener onNoteClickListener;

    public NotesAdapter(Cursor cursor, OnNoteClickListener listener) {
        this.cursor = cursor;
        this.onNoteClickListener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            String content = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_CONTENT));
            String createdAt = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_CREATED_AT));
            long remindTime = cursor.getLong(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_REMIND_TIME)); // Получаем время напоминания

            holder.noteContent.setText(content);
            holder.noteDate.setText(createdAt);

            if (remindTime > 0) {
                // Форматируем время напоминания и показываем текст
                SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy 'г' в HH:mm", Locale.getDefault());
                String formattedTime = sdf.format(new Date(remindTime));
                holder.reminderText.setText("Напомним " + formattedTime);
                holder.reminderText.setVisibility(View.VISIBLE);
            } else {
                // Скрываем текст напоминания, если время не задано
                holder.reminderText.setVisibility(View.GONE);
            }

            holder.editButton.setOnClickListener(v ->
                    onNoteClickListener.onEditClick(cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_ID)))
            );
            holder.deleteButton.setOnClickListener(v ->
                    onNoteClickListener.onDeleteClick(cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_ID)))
            );
        }
    }


    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteContent, noteDate, reminderText; // Добавлено reminderText
        Button editButton, deleteButton;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.noteContent);
            noteDate = itemView.findViewById(R.id.noteDate);
            reminderText = itemView.findViewById(R.id.reminderText); // Инициализация reminderText
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }


    public interface OnNoteClickListener {
        void onEditClick(int noteId);
        void onDeleteClick(int noteId);
    }
}

package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notesList; // Список заметок
    private OnNoteClickListener onNoteClickListener;

    public NotesAdapter(List<Note> notesList, OnNoteClickListener listener) {
        this.notesList = notesList;
        this.onNoteClickListener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notesList.get(position);

        holder.noteContent.setText(note.getContent());
        holder.noteDate.setText(note.getCreatedAt());

        if (note.getRemindTime() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy 'г' в HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(new Date(note.getRemindTime()));
            holder.reminderText.setText("Напомним " + formattedTime);
            holder.reminderText.setVisibility(View.VISIBLE);
        } else {
            holder.reminderText.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(v -> onNoteClickListener.onEditClick(note.getId()));
        holder.deleteButton.setOnClickListener(v -> onNoteClickListener.onDeleteClick(note.getId()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteContent, noteDate, reminderText;
        Button editButton, deleteButton;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.noteContent);
            noteDate = itemView.findViewById(R.id.noteDate);
            reminderText = itemView.findViewById(R.id.reminderText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnNoteClickListener {
        void onEditClick(int noteId);
        void onDeleteClick(int noteId);
    }
}

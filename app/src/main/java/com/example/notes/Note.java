package com.example.notes;

public class Note {
    private int id;
    private String content;
    private String createdAt;
    private long remindTime;

    public Note(int id, String content, String createdAt, long remindTime) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.remindTime = remindTime;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getRemindTime() {
        return remindTime;
    }
}

package com.example.menote.model.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity (tableName = "note_table")
public class Note {
    @SerializedName("id")
    private Integer id;

    @PrimaryKey
    @NonNull
    @SerializedName("noteId")
    private String noteId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("priority")
    private int priority;


    public Note(String title, String description, int priority) {
        this.noteId = System.currentTimeMillis()+"";
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public String getNoteId() {
        return noteId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}

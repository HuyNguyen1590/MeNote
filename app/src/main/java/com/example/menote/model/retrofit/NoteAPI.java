package com.example.menote.model.retrofit;

import com.example.menote.model.roomdatabase.Note;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NoteAPI {
    @GET("notes")
    Call<List<Note>> getNotes();

    @POST("notes")
    Call<Note> createNote(@Body Note note);

    @PUT("notes/{noteId}")
    Call<Note> updateNote(@Query("noteId") String id,@Body Note note);

    @DELETE("notes")
    Call<Note> deleteNote(@Query("id") int noteId);
}

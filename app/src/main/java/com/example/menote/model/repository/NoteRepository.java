package com.example.menote.model.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.menote.model.retrofit.NoteAPI;
import com.example.menote.model.roomdatabase.Note;
import com.example.menote.model.roomdatabase.NoteDAO;
import com.example.menote.model.roomdatabase.NoteDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoteRepository {
    private NoteDAO noteDAO;
    private LiveData<List<Note>> allNotes;
    private NoteAPI noteAPI;

    public NoteRepository (Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDAO = database.noteDAO();
        allNotes = noteDAO.getAllNotes();

        //NoteAPI with retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.84:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        noteAPI = retrofit.create(NoteAPI.class);
    }

    public void insert (Note note){
        new InsertNoteAsyncTask(noteDAO).execute(note);
        try {
            postToServer(note);
        }catch (Exception e){
            Log.i("Exception","Cannot connect to server");
        }
    }
    public void update (Note note){
        new UpdateNoteAsyncTask(noteDAO).execute(note);
        try {
            putToServer(note);
        }catch (Exception e){
            Log.i("Exception","Cannot connect to server");
        }
    }
    public void delete (Note note){
        new DeleteNoteAsyncTask(noteDAO).execute(note);
        try {
            deleteToServer(note);
        }catch (Exception e){
            Log.i("Exception","Cannot connect to server or "+e.getMessage());
        }
    }
    public void deleteAllNotes (){
        new DeleteAllNotesAsyncTask(noteDAO).execute();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDAO noteDAO;

        private InsertNoteAsyncTask (NoteDAO noteDAO){
            this.noteDAO = noteDAO;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.insert(notes[0]);
            return null;
        }
    }
    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDAO noteDAO;

        private UpdateNoteAsyncTask (NoteDAO noteDAO){
            this.noteDAO = noteDAO;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.update(notes[0]);
            return null;
        }
    }
    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDAO noteDAO;

        private DeleteNoteAsyncTask (NoteDAO noteDAO){
            this.noteDAO = noteDAO;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.delete(notes[0]);
            return null;
        }
    }
    private static class DeleteAllNotesAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDAO noteDAO;

        private DeleteAllNotesAsyncTask(NoteDAO noteDAO){
            this.noteDAO = noteDAO;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.deleteAllNotes();
            return null;
        }
    }
    private void postToServer(Note note){
    //Post <Note> request
        Call<Note> listCall = noteAPI.createNote(note);
        listCall.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (!response.isSuccessful()){
                    Log.e("PostRequest", "Error code: "+response.code());
                    return;
                }
                    Log.i("PostRequest", "Post success "+ response.body().getTitle());
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Log.e("PostRequest", "onFailure: "+t.getMessage());
            }
        });
    }
    private void putToServer(Note note){
        //Put <Note> request to update
        Call<Note> listCall = noteAPI.updateNote(note.getNoteId(), note);
        listCall.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (!response.isSuccessful()){
                    Log.e("PutRequest", "Error code: "+response.code());
                    return;
                }
                Log.i("PutRequest", "Put success "+ response.body().getTitle());
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Log.e("PutRequest", "onFailure: "+t.getMessage());
            }
        });
    }
    public void deleteToServer(final Note note){
        //Delete note from server
        Call<Note> listCall = noteAPI.deleteNote(1);
        listCall.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (!response.isSuccessful()){
                    Log.e("DeleteRequest", "Error code: "+response.code());
                    return;
                }
                Log.i("DeleteRequest", "Delete success: "+ response.message() +"//"+note.getNoteId());
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Log.e("DeleteRequest", "onFailure: "+t.getMessage());

            }
        });
    }
}

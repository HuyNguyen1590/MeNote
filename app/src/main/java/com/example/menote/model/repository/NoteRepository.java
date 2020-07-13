package com.example.menote.model.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.menote.model.roomdatabase.Note;
import com.example.menote.model.roomdatabase.NoteDAO;
import com.example.menote.model.roomdatabase.NoteDatabase;

import java.util.List;

public class NoteRepository {
    private NoteDAO noteDAO;
    private LiveData<List<Note>> allNotes;

    public NoteRepository (Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDAO = database.noteDAO();
        allNotes = noteDAO.getAllNotes();
    }

    public void insert (Note note){
        new InsertNoteAsyncTask(noteDAO).execute(note);
    }
    public void update (Note note){
        new UpdateNoteAsyncTask(noteDAO).execute(note);
    }
    public void delete (Note note){
        new DeleteNoteAsyncTask(noteDAO).execute(note);
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
}

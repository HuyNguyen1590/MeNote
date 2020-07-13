package com.example.menote.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.menote.R;
import com.example.menote.model.roomdatabase.Note;
import com.example.menote.model.roomdatabase.NoteDatabase;
import com.example.menote.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    NoteAdapter adapter = new NoteAdapter();

    private FloatingActionButton addFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.list_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });
        //Add dialog when click on Floating Action Button
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View addNoteView = inflater.inflate(R.layout.add_note_dialog, null);

        final EditText titleDialog = addNoteView.findViewById(R.id.title);
        final EditText descriptionDialog = addNoteView.findViewById(R.id.description);
        final NumberPicker numberPickerDialog = addNoteView.findViewById(R.id.priority);

        numberPickerDialog.setMinValue(1);
        numberPickerDialog.setMaxValue(10);

        builder.setView(addNoteView)
                .setTitle("Add note")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = new Note(titleDialog.getText().toString(),
                                descriptionDialog.getText().toString(),
                                numberPickerDialog.getValue());
                        if (note.getTitle().trim().isEmpty() || note.getDescription().trim().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Make sure to fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        noteViewModel.insert(note);
                        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();

        addFAB = findViewById(R.id.fab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleDialog.setText("");
                descriptionDialog.setText("");
                numberPickerDialog.setValue(1);
                dialog.show();
            }
        });

        //Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        //OnItemCLick to edit item
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                final int id = note.getId();
                titleDialog.setText(note.getTitle());
                descriptionDialog.setText(note.getDescription());
                numberPickerDialog.setValue(note.getPriority());
                AlertDialog.Builder editNoteBuilder = new AlertDialog.Builder(MainActivity.this);
                editNoteBuilder.setTitle("Edit note")
                        .setView(addNoteView)
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note noteEdited = new Note(titleDialog.getText().toString(),
                                        descriptionDialog.getText().toString(),
                                        numberPickerDialog.getValue());
                                if (noteEdited.getTitle().trim().isEmpty() || noteEdited.getDescription().trim().isEmpty()) {
                                    Toast.makeText(MainActivity.this, "Make sure to fill all fields", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                noteEdited.setId(id);
                                noteViewModel.update(noteEdited);
                                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog editDialog = editNoteBuilder.create();
                if (addNoteView.getParent() != null) {
                    ((ViewGroup) addNoteView.getParent()).removeView(addNoteView);
                }
                editDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_all_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("DELETE ALL!!!")
                        .setMessage("All notes will be deleted")
                        .setPositiveButton("DELETE ALL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteViewModel.deleteAllNotes();
                                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

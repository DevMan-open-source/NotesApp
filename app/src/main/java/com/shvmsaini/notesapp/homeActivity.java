package com.shvmsaini.notesapp;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class homeActivity extends AppCompatActivity {
    public FloatingActionButton addNote;
    public database db;
    public List<Note> notesList = new ArrayList<>();
    public customAdapter adapter;
    public RecyclerView recyclerView;
    public ItemTouchHelper itemTouchHelper;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        recyclerView = findViewById(R.id.recyclerView);
        addNote = findViewById(R.id.add_note);
        addNote.setOnClickListener(v -> showNoteDialog(false, null, -1));

        db = new database(this);
        notesList.addAll(db.getAllNotes());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new customAdapter(this, notesList);

        recyclerView.setAdapter(adapter);
        toggleEmptyNotes();
        Drawable trashBinIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_delete_24, null);
        Drawable editIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_edit_24, null);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    db.deleteNote(notesList.get(viewHolder.getAdapterPosition()));
                    notesList.remove(notesList.get(viewHolder.getAdapterPosition()));
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    toggleEmptyNotes();

                } else {
                    showNoteDialog(true, adapter.notes.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    itemTouchHelper.startSwipe(viewHolder);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Paint p = new Paint();
                p.setColor(Color.GRAY);
                int textMargin = (int) getResources().getDimension(R.dimen.list_padding);
                customAdapter.customViewHolder vh = (customAdapter.customViewHolder) viewHolder;
                View ll = vh.itemView;
                if (dX < 0) {
                    if (dX < -200) {
                        p.setColor(Color.RED);
                    }
                    c.drawRect(ll.getLeft(), ll.getTop(), ll.getRight(), ll.getBottom(), p);
                    assert trashBinIcon != null;
                    int itemHeight = ll.getBottom() - ll.getTop();
                    int intrinsicWidth = trashBinIcon.getIntrinsicWidth();
                    int intrinsicHeight = trashBinIcon.getIntrinsicWidth();
                    int Left = ll.getRight() - textMargin - intrinsicWidth;
                    int Right = ll.getRight() - textMargin;
                    int Top = ll.getTop() + (itemHeight - intrinsicHeight)/2;
                    int Bottom = Top + intrinsicHeight;
                    trashBinIcon.setBounds(Left,Top,Right,Bottom);
                    trashBinIcon.draw(c);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                } else {
                    if(dX > 550){
                        p.setColor(Color.parseColor("#0077b6"));
                    }
                    c.drawRect(ll.getLeft(), ll.getTop(), ll.getRight(), ll.getBottom(), p);
                    assert editIcon != null;
                    editIcon.setBounds(
                            textMargin,
                            viewHolder.itemView.getTop() + textMargin,
                            textMargin + editIcon.getIntrinsicWidth(),
                            viewHolder.itemView.getTop() + editIcon.getIntrinsicHeight() + textMargin
                    );
                    editIcon.draw(c);
                    super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);

                }

            }

        });


        itemTouchHelper.attachToRecyclerView(recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(homeActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", (dialogBox, id) -> {

                })
                .setNegativeButton("cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputNote.getText().toString())) {
                Toast.makeText(homeActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                alertDialog.dismiss();
            }

            // check if user updating note
            if (shouldUpdate && note != null) {
                // update note by it's id
                    updateNote(inputNote.getText().toString(), position);

            } else {
                // create new note
                createNote(inputNote.getText().toString());
            }
        });
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);
        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);
            // refreshing the list
            adapter.notifyDataSetChanged();
            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        adapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }
    public void toggleEmptyNotes(){
        if (notesList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

}

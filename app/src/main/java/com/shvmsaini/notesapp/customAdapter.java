package com.shvmsaini.notesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kotlin.UByte;

public class customAdapter extends RecyclerView.Adapter<customAdapter.customViewHolder> {
    private final String LOG_TAG = getClass().toString();
    private Context context;
    List<Note> notes;

    public customAdapter(Context context,List<Note> noteList){
        Log.d(LOG_TAG,"Constructor created");
        this.context = context;
        this.notes = noteList;
    }
    @NonNull
    @Override
    public customViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.list_item,parent,false);
        customViewHolder viewHolder = new customViewHolder(view);
        Log.d(LOG_TAG,"customViewHolder created");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull customViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.animalView.setText(note.getNote());
        Log.d(LOG_TAG,"bindViewHolder");
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    public class customViewHolder extends RecyclerView.ViewHolder {
        public TextView animalView;

        public customViewHolder(@NonNull View itemView) {
            super(itemView);
            animalView = itemView.findViewById(R.id.content);
        }
    }
}

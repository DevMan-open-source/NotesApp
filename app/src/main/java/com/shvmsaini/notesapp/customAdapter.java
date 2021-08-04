package com.shvmsaini.notesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.widget.TextView;
import kotlin.UByte;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        holder.content.setText(note.getNote());

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));

        Log.d(LOG_TAG,"bindViewHolder");
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
    public class customViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView timestamp;

        public customViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.content);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }
}


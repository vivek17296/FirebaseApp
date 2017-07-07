package com.example.vivek.firebaseapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by VIVEK on 7/4/2017.
 */

public class TrackList extends ArrayAdapter<Track>{
    private Activity context;
    List<Track> tracks;

    public TrackList(Activity context,List <Track> tracks){
        super(context,R.layout.list_layout,tracks);
        this.context=context;
        this.tracks=tracks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewRating = listViewItem.findViewById(R.id.textViewGenre);

        Track track = tracks.get(position);
        textViewName.setText(track.getTrackName());
        textViewRating.setText(String.valueOf(track.getRating()));

        return listViewItem;
    }

}

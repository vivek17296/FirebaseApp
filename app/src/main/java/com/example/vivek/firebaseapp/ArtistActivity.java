package com.example.vivek.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VIVEK on 7/4/2017.
 */

public class ArtistActivity extends AppCompatActivity {
    Button buttonAddtrack;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    TextView textViewRating,textViewArtist;
    ListView listViewTracks;

    DatabaseReference databaseTracks;

    List<Track> tracks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        Intent intent = getIntent();
        //Imp for accessing the database

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(intent.getStringExtra(MainActivity.ARTIST_ID));
        buttonAddtrack = (Button) findViewById(R.id.buttonAddTrack);
        editTextTrackName = (EditText) findViewById(R.id.editTextName);
        seekBarRating = (SeekBar) findViewById(R.id.seekbarRating);
        textViewRating = (TextView) findViewById(R.id.textViewRating);
        textViewArtist = (TextView) findViewById(R.id.textViewArtist);
        listViewTracks = (ListView) findViewById(R.id.listviewtracks);

        //Update the tracks

        tracks = new ArrayList<>();

        textViewArtist.setText(intent.getStringExtra(MainActivity.ARTIST_NAME));
        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                textViewRating.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        buttonAddtrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tracks.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Track track = postSnapshot.getValue(Track.class);
                    tracks.add(track);
                }
               // Track track = new TrackList(ArtistActivity.this,tracks);
                TrackList trackListAdapter = new TrackList(ArtistActivity.this,tracks);
                listViewTracks.setAdapter(trackListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveTrack() {
        String trackName = editTextTrackName.getText().toString().trim();
        int rating = seekBarRating.getProgress();
        if(!TextUtils.isEmpty(trackName)){
            String id = databaseTracks.push().getKey();
            Track track = new Track(id,trackName,rating);
            databaseTracks.child(id).setValue(track);
            Toast.makeText(this,"Track saved",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Please enter track name",Toast.LENGTH_LONG).show();
        }
    }



}


package com.example.vivek.firebaseapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //we will use this constant later to pass the artist name and id to another activity
    public static final String ARTIST_NAME = "com.example.vivek.firebaseapp.artistname";
    public static final String ARTIST_ID = "com.example.vivek.firebaseapp.artistid";
    EditText editTextName;
    Button buttonAddArtist;
    Spinner spinnerGenres;

    //database reference
    DatabaseReference databaseArtist;

    //ListView to display Artists
    ListView listViewArtists;

    List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerGenres = (Spinner) findViewById(R.id.SpinnerGenres);
        buttonAddArtist = (Button) findViewById(R.id.buttonAddartist);
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);
        artistList = new ArrayList<>();
        //get the firebase insttance
        databaseArtist = FirebaseDatabase.getInstance().getReference("artists");

        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        //attaching the listener to the listView
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent , View view ,int position,long id){
                // getting the selected artist list
                Artist artist = artistList.get(position);

                //creating Intent
                Intent intent = new Intent(getApplicationContext(),ArtistActivity.class);

                //adding artist name and id to the intent
                intent.putExtra(ARTIST_ID,artist.getArtistId());
                intent.putExtra(ARTIST_NAME,artist.getArtistName());

                //start the activity with intent
                startActivity(intent);

            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist= artistList.get(i) ;
                showUpdateDeleteDialog(artist.getArtistId(),artist.getArtistName());
                return true;

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        //add the value event listener
        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear the artist list if t contain any artist
                artistList.clear();

                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }
                ArtistList adapter = new ArtistList(MainActivity.this,artistList);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addArtist(){

        String name = editTextName.getText().toString();
        String genre = spinnerGenres.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name)){
            //get th eunique id
            String id = databaseArtist.push().getKey();
            //now create the new arrtiist
            Artist artist = new Artist(id,name,genre);
            //store the value to  he  daatabase using set value

            databaseArtist.child(id).setValue(artist);
            Toast.makeText(getApplicationContext(),"Artist added",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"You should enter a name",Toast.LENGTH_LONG).show();

        }
        editTextName.setText(null);
    }

    private boolean updateArtist(String id,String name,String genre) {

        //getting the specified artist reference
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("artists").child(id);
        //updatting artist
        Artist artist = new Artist(id, name, genre);
        dr.setValue(artist);
        Toast.makeText(getApplicationContext(), "Artist updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private void showUpdateDeleteDialog(final String artistId, String artistName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog,null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final  Spinner spinnerGenre = dialogView.findViewById(R.id.SpinnerGenres);
        final  Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateArtist);
        final  Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteArtist);

        dialogBuilder.setTitle(artistName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editTextName.getText().toString();
                String genre = spinnerGenre.getSelectedItem().toString();

                if(!TextUtils.isEmpty(name)){
                    updateArtist(artistId,name,genre);
                    b.dismiss();
                }

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteArtist(artistId);
                b.dismiss();

            }
        });
    }
    private boolean deleteArtist(String id) {

        //getting the specified artist reference
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("artists").child(id);

        //remove the artist
        dr.removeValue();

        //getting the track reference for the specified artist
        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        //removing the tracks
        drTracks.removeValue();
        Toast.makeText(getApplicationContext(),"Artists/Tracks deleted",Toast.LENGTH_LONG).show();

        return true;

    }
}

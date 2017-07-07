package com.example.vivek.firebaseapp;

/**
 * Created by VIVEK on 7/4/2017.
 */

public class Artist {
    String artistId,artistName,artistGenre;

    public Artist(){}
    public Artist(String artistId, String artistName, String artistGenre) {

        this.artistId = artistId;
        this.artistName = artistName;
        this.artistGenre = artistGenre;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistGenre() {
        return artistGenre;
    }
}

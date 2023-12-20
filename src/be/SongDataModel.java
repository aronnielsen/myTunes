package be;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SongDataModel {
    private final SimpleIntegerProperty songID;
    private final SimpleStringProperty songTitle;
    private final SimpleStringProperty songArtist;
    private final SimpleStringProperty songGenre;
    private final SimpleStringProperty songLength;
    private final SimpleStringProperty songPath;

    public SongDataModel(int id, String title, String artist, String genre, String length, String path) {
        this.songID = new SimpleIntegerProperty(id);
        this.songTitle = new SimpleStringProperty(title);
        this.songArtist = new SimpleStringProperty(artist);
        this.songGenre = new SimpleStringProperty(genre);
        this.songLength = new SimpleStringProperty(length);
        this.songPath = new SimpleStringProperty(path);
    }

    // Getters and setters for each property
    public int getSongID() { return songID.get(); }
    public void setSongID(int value) { songID.set(value); }
    public String getSongTitle() { return songTitle.get(); }
    public void setSongTitle(String value) { songTitle.set(value); }

    public String getSongGenre() { return songGenre.get(); }
    public void setSongGenre(String value) { songGenre.set(value); }

    public String getSongArtist() { return songArtist.get(); }
    public void setSongArtist(String value) { songArtist.set(value); }

    public String getSongLength() { return songLength.get(); }
    public void setSongLength(String value) { songLength.set(value); }

    public String getSongPath() { return songPath.get(); }
    public void setSongPath(String value) { songPath.set(value); }
}


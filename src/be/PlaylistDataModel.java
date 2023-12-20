package be;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlaylistDataModel {
    private final SimpleIntegerProperty playlistID;
    private final SimpleStringProperty playlistTitle;
    private final SimpleIntegerProperty playlistSongCount;
    private final SimpleStringProperty playlistLength;

    public PlaylistDataModel(int id, String title, int songCount, String length) {
        this.playlistID = new SimpleIntegerProperty(id);
        this.playlistTitle = new SimpleStringProperty(title);
        this.playlistSongCount = new SimpleIntegerProperty(songCount);
        this.playlistLength = new SimpleStringProperty(length);
    }

    // Getters and setters for each property
    public int getPlaylistID() { return playlistID.get(); }
    public void setPlaylistID(int value) { playlistID.set(value); }
    public String getPlaylistTitle() { return playlistTitle.get(); }
    public void setPlaylistTitle(String value) { playlistTitle.set(value); }

    public int getPlaylistSongCount() { return playlistSongCount.get(); }
    public void setPlaylistSongCount(int value) { playlistSongCount.set(value); }

    public String getPlaylistLength() { return playlistLength.get(); }
    public void setPlaylistLength(String value) { playlistLength.set(value); }

}


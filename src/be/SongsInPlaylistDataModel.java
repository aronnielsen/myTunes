package be;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SongsInPlaylistDataModel {
    private final SimpleIntegerProperty songID;
    private final SimpleStringProperty songTitle;
    private final SimpleStringProperty songPath;

    public SongsInPlaylistDataModel(int id, String title, String path) {
        this.songID = new SimpleIntegerProperty(id);
        this.songTitle = new SimpleStringProperty(title);
        this.songPath = new SimpleStringProperty(path);
    }

    public int getSongID() { return songID.get(); }
    public void setSongID(int value) { songID.set(value); }
    public String getSongTitle() { return songTitle.get(); }
    public void setSongTitle(String value) { songTitle.set(value); }

    public String getSongPath() { return songPath.get(); }
    public void setSongPath(String value) { songPath.set(value); }
}

package bl;

import be.PlaylistDataModel;
import be.SongDataModel;
import be.SongsInPlaylistDataModel;
import dal.Playlist;
import dal.Song;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Logic {
    /**
     * This handles the input from the form that adds a new song. Currently only basic validation required for the file.
     * If it succeeds in moving the file, then it will add the song to the database.
     * @param title of the song
     * @param artist of the song
     * @param genre of the song
     * @param length of the song
     * @param file for the song
     */
    public static void HandleAddNewSong(String title, String artist, String genre, String length, File file) {
        if (file != null) {
            String destinationDirectoryPath = "src/songs";
            File destinationDirectory = new File(destinationDirectoryPath);

            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            File destinationFile = new File(destinationDirectory, file.getName().replace(" ", "_"));

            if (Song.MoveSongFile(file.toPath(), destinationFile.toPath())) {
                Song.AddSong(title, artist, genre, length, destinationFile.toPath().toString().replace(" ", "_"));
            }
        } else {
            System.out.println("No file was selected");
        }
    }

    /**
     * Handles the editing of a song. If the file succeeds being moved then the edits are sent to the data layer.
     * @param id of the song
     * @param title of the song
     * @param artist of the song
     * @param genre of the song
     * @param length of the song
     * @param file for the song
     */
    public static void HandleEditSong(int id, String title, String artist, String genre, String length, File file) {
        if (file != null) {
            String destinationDirectoryPath = "src/songs";
            File destinationDirectory = new File(destinationDirectoryPath);

            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            File destinationFile = new File(destinationDirectory, file.getName().replace(" ", "_"));

            if (Song.MoveSongFile(file.toPath(), destinationFile.toPath())) {
                Song.EditSong(id, title, artist, genre, length, destinationFile.toPath().toString().replace(" ", "_"));
            }
        } else {
            System.out.println("No file was selected");
        }
    }

    /**
     * Handles the deletion of a song and it's file. If deleteFile is true both the database entry and the local file
     * are deleted, if false, then only the database entry. It also has to delete the songs from all playlists.
     * @param item the song being deleted
     * @param deleteFile should the file be deleted aswell
     */
    public static void HandleDeleteSong(SongDataModel item, boolean deleteFile) {
        if (deleteFile) {
            Path path = Paths.get(System.getProperty("user.dir") + "/" + item.getSongPath());

            try {
                Files.delete(path);
            } catch (IOException e) {
                System.out.println("Failed to delete the file.");
                e.printStackTrace();
            }
        }

        Playlist.RemoveSongFromAllPlaylists(item.getSongID());
        Song.DeleteSong(item.getSongID());
    }

    /**
     * Checks the name of a new playlist. So long as it is not empty it will go to the data layer.
     * @param title of the playlist
     */
    public static void HandleAddPlaylist(String title) {
        if (!title.isEmpty()) {
            Playlist.AddPlaylist(title);
        }
    }

    /**
     * Checks the new name for a playlist. So long as it is not empty it will go to the data layer.
     * @param id of the playlist being edited
     * @param title the new title
     */
    public static void HandleEditPlaylist(int id, String title) {
        if (!title.isEmpty()) {
            Playlist.EditPlaylist(id, title);
        }
    }

    /**
     * Handles the deletion of a playlist on the business logic, is sent directly to the data layer.
     * @param playlistID of the playlist being deleted
     */
    public static void HandleDeletePlaylist(int playlistID) {
        Playlist.DeletePlaylist(playlistID);
    }

    /**
     * Handles adding a new song to a playlist. Only checks if it is a positive int.
     * @param song id of the song to be added
     * @param playlist id of the playlist being added to
     * @param order of the song in the playlist (NOT USED ATM)
     */
    public static void HandleAddSongToPlaylist(SongDataModel song, PlaylistDataModel playlist, int order) {
        if (song.getSongID() > -1) {
            Playlist.AddSongToPlaylist(song.getSongID(), playlist.getPlaylistID(), order);
        }
    }

    /**
     * Handles the removal of a song on the business layer. Checks if the song id is a positive int.
     * @param song to be removed from playlist
     * @param playlist that the song is being removed from
     */
    public static void HandleRemoveSongFromPlaylist(SongsInPlaylistDataModel song, PlaylistDataModel playlist) {
        if (song.getSongID() > -1) {
            Playlist.RemoveSongFromPlaylist(song.getSongID(), playlist.getPlaylistID());
        }
    }

    /**
     * Talks to the data layer to get all the songs.
     * @return the list of songs
     */
    public static ObservableList<SongDataModel> GetSongList() {
        return Song.GetAllSongs();
    }

    /**
     * Talks to the data layer to get all the playlists.
     * @return the list of songs
     */
    public static ObservableList<PlaylistDataModel> GetPlaylistList() {
        return Playlist.GetAllPlaylists();
    }

    /**
     * Talks to the data layer to get all the songs in the given playlist.
     * @param playlistId of the selected playlist.
     * @return the list of songs in the given playlist.
     */
    public static ObservableList<SongsInPlaylistDataModel> GetSongsInPlaylist(int playlistId) {
        return Playlist.GetSongsInPlaylist(playlistId);
    }
}
package dal;

import be.SongDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.io.IOException;

public class Song {
    /**
     * Handles moving the file when a new song is being added or edited.
     * @param from From destination of the file being moved
     * @param to To destination of the file being moved
     * @return true if it was successfully moved, otherwise false.
     */
    public static boolean MoveSongFile(Path from, Path to) {
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets all the song entries in the database.
     * @return A list of all songs.
     */
    public static ObservableList<SongDataModel> GetAllSongs() {
        try {
            Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
            Statement stmt = conn.createStatement();

            String sql = "SELECT * FROM song";
            ResultSet rs = stmt.executeQuery(sql);

            ObservableList<SongDataModel> data = FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("song_id");
                String name = rs.getString("song_title");
                String artist = rs.getString("song_artist");
                String genre = rs.getString("song_genre");
                String length = rs.getString("song_length");
                String path = rs.getString("song_path");

                data.add(new SongDataModel(id, name, artist, genre, length, path));
            }

            rs.close();
            stmt.close();
            conn.close();

            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a new song to the database.
     * Since data is being sent to the database. It is set up as a prepared statement to prevent SQLInjection.
     * @param title The title of the song
     * @param artist The artist of the song
     * @param genre The genre of the song
     * @param length The length of the song
     * @param path The filepath for the song
     */
    public static void AddSong(String title, String artist, String genre, String length, String path) {
        String query = "INSERT INTO song (song_title,song_artist, song_genre, song_length, song_path) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, genre);
            pstmt.setString(4, length);
            pstmt.setString(5, path);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error inserting song into the database.");
        }
    }

    /**
     * Updates an existing song in the database.
     * Since data is being sent to the database. It is set up as a prepared statement to prevent SQLInjection.
     * @param id The ID of the song to update.
     * @param title The new title
     * @param artist The new artist
     * @param genre The new genre
     * @param length The new length
     * @param path The new filepath
     */
    public static void EditSong(int id, String title, String artist, String genre, String length, String path) {
        String query = "UPDATE song SET song_title=?,song_artist=?, song_genre=?, song_length=?, song_path=? WHERE song_id=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, genre);
            pstmt.setString(4, length);
            pstmt.setString(5, path);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error updating song in the database.");
        }
    }

    /**
     * Deletes a song from the database.
     * @param id ID of the song to be deleted.
     */
    public static void DeleteSong(int id) {
        String query = "DELETE FROM song WHERE song_id=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error deleting song from the database.");
        }
    }
}

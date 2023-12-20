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
    public static boolean MoveSongFile(Path from, Path to) {
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static ObservableList<SongDataModel> GetAllSongs() {
        try {
            // Establishing a connection
            Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
            System.out.println("Connected to the MySQL server successfully.");

            // Creating a statement
            Statement stmt = conn.createStatement();

            // Executing a query
            String sql = "SELECT * FROM song";
            ResultSet rs = stmt.executeQuery(sql);

            ObservableList<SongDataModel> data = FXCollections.observableArrayList();


            // Processing the result set
            while (rs.next()) {
                // Retrieve by column name
                int id = rs.getInt("song_id");
                String name = rs.getString("song_title");
                String artist = rs.getString("song_artist");
                String genre = rs.getString("song_genre");
                String length = rs.getString("song_length");
                String path = rs.getString("song_path");
                // You can retrieve other columns as needed.

                System.out.println("ID: " + id + ", Name: " + name);

                data.add(new SongDataModel(id, name, artist, genre, length, path));
            }

            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();

            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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

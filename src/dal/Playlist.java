package dal;

import be.PlaylistDataModel;
import be.SongsInPlaylistDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Playlist {
    public static ObservableList<PlaylistDataModel> GetAllPlaylists() {
        try {
            // Establishing a connection
            Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
            System.out.println("Connected to the MySQL server successfully.");

            // Creating a statement
            Statement stmt = conn.createStatement();

            // Executing a query
            String sql = "SELECT * FROM playlist";
            ResultSet rs = stmt.executeQuery(sql);

            ObservableList<PlaylistDataModel> data = FXCollections.observableArrayList();


            // Processing the result set
            while (rs.next()) {
                // Retrieve by column name
                int id = rs.getInt("playlist_id");
                String name = rs.getString("playlist_title");
                int count = rs.getInt("playlist_song_count");
                String length = rs.getString("playlist_length");

                System.out.println("ID: " + id + ", Name: " + name);

                data.add(new PlaylistDataModel(id, name, count, length));
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

    public static void AddPlaylist(String title) {
        String query = "INSERT INTO playlist (playlist_title) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error inserting song into the database.");
        }
    }

    public static void EditPlaylist(int id, String title) {
        String query = "UPDATE playlist SET playlist_title=? WHERE playlist_id=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error updating song in the database.");
        }
    }

    public static void DeleteAllSongsFromPlaylist(int playlistID) {
        String query = "DELETE FROM song_in_playlist WHERE sip_playlist=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, playlistID);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error inserting song into the database.");
        }
    }

    public static void DeletePlaylist(int playlistID) {
        DeleteAllSongsFromPlaylist(playlistID);

        String query = "DELETE FROM playlist WHERE playlist_id=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, playlistID);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error inserting song into the database.");
        }
    }

    public static void AddSongToPlaylist(int songId, int playlistId, int order) {
        String query = "INSERT INTO song_in_playlist (sip_song, sip_playlist, sip_order) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, songId);
            pstmt.setInt(2, playlistId);
            pstmt.setInt(3, order);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error inserting song into the database.");
        }
    }

    public static void RemoveSongFromPlaylist(int songId, int playlistId) {
        String query = "DELETE FROM song_in_playlist WHERE sip_song=? AND sip_playlist=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.setInt(2, playlistId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error deleting song from the database.");
        }
    }

    public static ObservableList<SongsInPlaylistDataModel> GetSongsInPlaylist(int id) {
        String query = "SELECT * FROM song_in_playlist JOIN song ON song_in_playlist.sip_song = song.song_id WHERE sip_playlist=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            ObservableList<SongsInPlaylistDataModel> data = FXCollections.observableArrayList();


            // Processing the result set
            while (rs.next()) {
                // Retrieve by column name
                int songID = rs.getInt("song_id");
                String name = rs.getString("song_title");
                String path = rs.getString("song_path");

                System.out.println("ID: " + id + ", Name: " + name);

                data.add(new SongsInPlaylistDataModel(songID, name, path));
            }

            // Clean-up environment
            rs.close();
            pstmt.close();
            conn.close();

            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error getting songs in playlist from the database.");
            return null;
        }
    }
}

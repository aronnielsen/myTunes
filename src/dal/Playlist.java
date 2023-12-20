package dal;

import be.PlaylistDataModel;
import be.SongsInPlaylistDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Playlist {
    /**
     * Gets all the playlists in the database
     * @return a list of the playlists
     */
    public static ObservableList<PlaylistDataModel> GetAllPlaylists() {
        try {
            Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
            Statement stmt = conn.createStatement();

            String sql = "SELECT * FROM playlist";
            ResultSet rs = stmt.executeQuery(sql);

            ObservableList<PlaylistDataModel> data = FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("playlist_id");
                String name = rs.getString("playlist_title");
                int count = rs.getInt("playlist_song_count");
                String length = rs.getString("playlist_length");
                data.add(new PlaylistDataModel(id, name, count, length));
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
     * Adds a new playlist to the database.
     * Since data is being sent to the database. It is set up as a prepared statement to prevent SQLInjection.
     * @param title the title of the playlist
     */
    public static void AddPlaylist(String title) {
        String query = "INSERT INTO playlist (playlist_title) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error inserting playlist into the database.");
        }
    }

    /**
     * Updates an existing playlist in the database.
     * Since data is being sent to the database. It is set up as a prepared statement to prevent SQLInjection.
     * @param id
     * @param title
     */
    public static void EditPlaylist(int id, String title) {
        String query = "UPDATE playlist SET playlist_title=? WHERE playlist_id=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error updating playlist in the database.");
        }
    }

    /**
     * This function removes all the connected songs to a playlist.
     * Is mainly used to prepare a playlist for deletion.
     * @param playlistID id of the playlist to empty
     */
    public static void DeleteAllSongsFromPlaylist(int playlistID) {
        String query = "DELETE FROM song_in_playlist WHERE sip_playlist=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, playlistID);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error deleting all songs from a playlist.");
        }
    }

    /**
     * Deletes a playlist from the database. Starts by removing all connected songs from the playlist.
     * @param playlistID id of the playlist to delete
     */
    public static void DeletePlaylist(int playlistID) {
        DeleteAllSongsFromPlaylist(playlistID);

        String query = "DELETE FROM playlist WHERE playlist_id=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, playlistID);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error deleting playlist from the database.");
        }
    }

    /**
     * Connects a song to a playlist.
     * @param songId ID of the song to connect
     * @param playlistId ID of the playlist connecting to
     * @param order The order of the song in the playlist.
     */
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
            System.out.println("Error adding song to playlist.");
        }
    }

    /**
     * Removes a connected song from a playlist.
     * @param songId ID of the song to remove from playlist.
     * @param playlistId ID of the playlist to remove the song from.
     */
    public static void RemoveSongFromPlaylist(int songId, int playlistId) {
        String query = "DELETE FROM song_in_playlist WHERE sip_song=? AND sip_playlist=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.setInt(2, playlistId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error removing song from a playlist.");
        }
    }

    /**
     * Removes the song from all playlists. This is mainly used when preparing a song for deletion.
     * @param songId ID of the song that is being removed.
     */
    public static void RemoveSongFromAllPlaylists(int songId) {
        String query = "DELETE FROM song_in_playlist WHERE sip_song=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error removing song from a playlist.");
        }
    }

    /**
     * Gets all the songs connected to a specific playlist.
     * @param id ID of the playlist that songs are being gotten for
     * @return A list the songs in the specified playlist.
     */
    public static ObservableList<SongsInPlaylistDataModel> GetSongsInPlaylist(int id) {
        String query = "SELECT * FROM song_in_playlist JOIN song ON song_in_playlist.sip_song = song.song_id WHERE sip_playlist=?";

        try (Connection conn = DriverManager.getConnection(Config.getUrl(), Config.getUser(), Config.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            ObservableList<SongsInPlaylistDataModel> data = FXCollections.observableArrayList();

            while (rs.next()) {
                int songID = rs.getInt("song_id");
                String name = rs.getString("song_title");
                String path = rs.getString("song_path");
                data.add(new SongsInPlaylistDataModel(songID, name, path));
            }

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

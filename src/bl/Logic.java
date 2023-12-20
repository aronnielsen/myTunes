package bl;

import be.PlaylistDataModel;
import be.SongDataModel;
import be.SongsInPlaylistDataModel;
import dal.Playlist;
import dal.Song;
import gui.MainController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Logic {
    public static void handleSubmit(String title, String artist, String genre, String length, File file, Stage popup) {
        if (file != null) {
            System.out.println("Selected file: " + file.getAbsolutePath());

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

    public static void handleEditSubmit(int id, String title, String artist, String genre, String length, File file, Stage popup) {
        if (file != null) {
            System.out.println("Selected file: " + file.getAbsolutePath());

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

    public static void handleDeleteSong(SongDataModel item, boolean deleteFile) throws IOException {
        if (deleteFile) {
            Path path = Paths.get(System.getProperty("user.dir") + "/" + item.getSongPath());

            System.out.println(path);
            try {
                Files.delete(path);
                System.out.println("File deleted successfully.");
            } catch (IOException e) {
                System.out.println("Failed to delete the file.");
                e.printStackTrace();
            }
        }

        Song.DeleteSong(item.getSongID());
    }

    public static void handleAddPlaylistSubmit(String title) {
        if (!title.isEmpty()) {
            Playlist.AddPlaylist(title);
        }
    }

    public static void generateSongTable(TableView songTable, String filter, MainController main) {
        ObservableList<SongDataModel> data = Song.GetAllSongs();
        FilteredList<SongDataModel> filteredData = new FilteredList<>(data, p -> true);

        TableColumn<SongDataModel, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(new PropertyValueFactory<>("songTitle"));
        TableColumn<SongDataModel, String> col2 = new TableColumn<>("Artist");
        col2.setCellValueFactory(new PropertyValueFactory<>("songArtist"));
        TableColumn<SongDataModel, String> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(new PropertyValueFactory<>("songGenre"));
        TableColumn<SongDataModel, String> col4 = new TableColumn<>("Length");
        col4.setCellValueFactory(new PropertyValueFactory<>("songLength"));

        if (songTable.getColumns().isEmpty()) {
            songTable.getColumns().addAll(col1, col2, col3, col4);
        }

        songTable.setItems(filteredData);
        filteredData.setPredicate(myDataType -> {
            if (filter == null || filter.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = filter.toLowerCase();

            if (myDataType.getSongArtist().toLowerCase().contains(lowerCaseFilter) || myDataType.getSongTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });

        songTable.setRowFactory(new Callback<TableView<SongDataModel>, TableRow<SongDataModel>>() {
            public TableRow<SongDataModel> call(TableView<SongDataModel> tableView) {
                TableRow<SongDataModel> row = new TableRow<>();
                ContextMenu contextMenu = new ContextMenu();
                MenuItem item1 = new MenuItem("Play");

                item1.setOnAction(e -> {
                    SongDataModel rowData = row.getItem();
                    main.playSong(rowData.getSongPath().replace('\\', '/'), rowData);
                });

                contextMenu.getItems().add(item1);

                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );

                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        SongDataModel clickedRow = row.getItem();
                        // Perform your action with the clicked row's data
                        System.out.println("Clicked on: " + clickedRow);
                        main.playSong(row.getItem().getSongPath().replace('\\', '/'), clickedRow);
                    }

                    if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                        contextMenu.show(row, event.getScreenX(), event.getScreenY());
                    }
                });

                return row;
            }
        });
    }

    public static void generatePlaylistTable(TableView playlistTable, MainController main) {
        ObservableList<PlaylistDataModel> data = Playlist.GetAllPlaylists();

        TableColumn<PlaylistDataModel, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(new PropertyValueFactory<>("playlistTitle"));
        TableColumn<PlaylistDataModel, String> col2 = new TableColumn<>("Songs");
        col2.setCellValueFactory(new PropertyValueFactory<>("playlistSongCount"));
        TableColumn<PlaylistDataModel, String> col3 = new TableColumn<>("Length");
        col3.setCellValueFactory(new PropertyValueFactory<>("playlistLength"));

        if (playlistTable.getColumns().isEmpty()) {
            playlistTable.getColumns().addAll(col1, col2, col3);
        }

        playlistTable.setItems(data);

        playlistTable.setRowFactory(new Callback<TableView<PlaylistDataModel>, TableRow<PlaylistDataModel>>() {
            public TableRow<PlaylistDataModel> call(TableView<PlaylistDataModel> tableView) {
                TableRow<PlaylistDataModel> row = new TableRow<>();

                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        PlaylistDataModel clickedRow = row.getItem();
                        main.GetSongsInPlaylist(clickedRow);
                    }
                });

                return row;
            }
        });
    }

    public static void generateSongsInPlaylistList(int id, ListView songsInPlaylistList, MainController main) {
        ObservableList<SongsInPlaylistDataModel> data = Playlist.GetSongsInPlaylist(id);

        songsInPlaylistList.setCellFactory(param -> new ListCell<SongsInPlaylistDataModel>() {
            @Override
            protected void updateItem(SongsInPlaylistDataModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getSongTitle());
                    setOnMouseClicked(event -> {
                        if (!isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            main.playSongFromPlaylist(item.getSongPath().replace('\\', '/'), item);
                        }
                    });
                }
            }
        });

        songsInPlaylistList.setItems(data);
    }

    public static void HandleAddSongToPlaylist(SongDataModel song, PlaylistDataModel playlist, int order) {
        if (song.getSongID() > -1) {
            Playlist.AddSongToPlaylist(song.getSongID(), playlist.getPlaylistID(), order);
        }
    }

    public static void HandleRemoveSongFromPlaylist(SongsInPlaylistDataModel song, PlaylistDataModel playlist) {
        if (song.getSongID() > -1) {
            Playlist.RemoveSongFromPlaylist(song.getSongID(), playlist.getPlaylistID());
        }
    }

    public static void HandleDeletePlaylist(int playlistID) {
        Playlist.DeletePlaylist(playlistID);
    }

    public static void handleEditPlaylistSubmit(int id, String title) {
        if (!title.isEmpty()) {
            Playlist.EditPlaylist(id, title);
        }
    }

    public static void emptySongsInPlaylistList(ListView songsInPlaylistList, MainController main) {
        ObservableList<SongsInPlaylistDataModel> items = FXCollections.observableArrayList();

        songsInPlaylistList.setItems(items);
    }
}

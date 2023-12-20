package gui;

import be.PlaylistDataModel;
import be.SongDataModel;
import be.SongsInPlaylistDataModel;
import bl.Logic;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController {
    public TableView playlistTable;
    public TextField filterValue;
    public Slider volumeSlider;
    public ListView songsInPlaylistList;
    public TableView songTable;
    public Label nowPlaying;

    private File selectedFile;

    private MediaPlayer mediaPlayer;

    /**
    * At the start of the controller. Load data into the song and playlist table and makes the volume slider work.
    * */
    public void initialize() {
        PopulateSongTable();
        PopulatePlaylistTable();

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume((double) newValue);
        });
    }

    /**
    * Plays the given song and updates the "Now Playing" label
    * */
    public void playSong(String path, String title, String artist) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        String audioFile = "file://" + System.getProperty("user.dir") + "/" + path;
        Media media = new Media(audioFile);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setVolume(volumeSlider.getValue());

        if (Objects.equals(artist, "")) {
            nowPlaying.setText(title + " is Playing");
        } else {
            nowPlaying.setText(title + " by " + artist + " is Playing");
        }
    }

    /**
    * This functions talks to the business logic that then talks with the data access layer and
    * then update the TableView in the FXML that handles displaying the song list.
    * It adds different event listeners so that if you double-click an item in the TableView you play the song.
    * And can also right-click to open a context menu.
    * */
    public void PopulateSongTable() {
        ObservableList<SongDataModel> data = Logic.GetSongList();
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
            if (filterValue.getText() == null || filterValue.getText().isEmpty()) {
                return true;
            }

            String lowerCaseFilter = filterValue.getText().toLowerCase();

            return myDataType.getSongArtist().toLowerCase().contains(lowerCaseFilter) || myDataType.getSongTitle().toLowerCase().contains(lowerCaseFilter);
        });

        songTable.setRowFactory(new Callback<TableView<SongDataModel>, TableRow<SongDataModel>>() {
            public TableRow<SongDataModel> call(TableView<SongDataModel> tableView) {
                TableRow<SongDataModel> row = new TableRow<>();
                ContextMenu contextMenu = new ContextMenu();
                MenuItem item1 = new MenuItem("Play");

                item1.setOnAction(e -> {
                    SongDataModel rowData = row.getItem();
                    playSong(rowData.getSongPath().replace('\\', '/'), rowData.getSongTitle(), rowData.getSongArtist());
                });

                MenuItem item2 = new MenuItem("Edit");

                item2.setOnAction(e -> EditSong());

                MenuItem item3 = new MenuItem("Delete");

                item3.setOnAction(e -> {
                    try {
                        DeleteSong();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                contextMenu.getItems().add(item1);
                contextMenu.getItems().add(item2);
                contextMenu.getItems().add(item3);

                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );

                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        SongDataModel clickedRow = row.getItem();
                        playSong(row.getItem().getSongPath().replace('\\', '/'), clickedRow.getSongTitle(), clickedRow.getSongArtist());
                    }

                    if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                        contextMenu.show(row, event.getScreenX(), event.getScreenY());
                    }
                });

                return row;
            }
        });
    }

    /**
     * This functions talks to the business logic that then talks with the data access layer and
     * then updates the TableView in the FXML that handles displaying the playlists.
     * It adds an event listener so that if you click a playlist the songs that are in the
     * playlist are displayed in the list to the right.
     * */
    public void PopulatePlaylistTable() {
        ObservableList<PlaylistDataModel> data = Logic.GetPlaylistList();

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
                        PopulateSongsInPlaylist();
                    }
                });

                return row;
            }
        });
    }

    /**
     * This functions talks to the business logic that then talks with the data access layer and
     * then update the TableView in the FXML that handles displaying the songs that are in the selected playlist.
     * It adds an event listener so that if you double-click an item in the ListView you play the song.
     * */
    public void PopulateSongsInPlaylist() {
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();

        ObservableList<SongsInPlaylistDataModel> data = Logic.GetSongsInPlaylist(playlist.getPlaylistID());

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
                            playSong(item.getSongPath().replace('\\', '/'), item.getSongTitle(), "");
                        }
                    });
                }
            }
        });

        songsInPlaylistList.setItems(data);
    }

    /**
    * Creates a popup and populates it with the required input field and
    * then attaches a function to the submit button to send the data to the business logic to handle the form.
    * Then it refreshes the song list and closes the popup.
    * */
    public void NewSong() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add Song");

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        TextField songTitle = new TextField();
        songTitle.setPromptText("Enter song title");

        TextField songArtist = new TextField();
        songArtist.setPromptText("Enter song artist");

        TextField songGenre = new TextField();
        songGenre.setPromptText("Enter song genre");

        TextField songLength = new TextField();
        songLength.setPromptText("Enter song length");

        Button fileChooserButton = new Button("Choose File");
        FileChooser fileChooser = new FileChooser();

        fileChooserButton.setOnAction(e -> selectedFile = fileChooser.showOpenDialog(popupStage));

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            Logic.HandleAddNewSong(songTitle.getText(), songArtist.getText(), songGenre.getText(), songLength.getText(), selectedFile);
            PopulateSongTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(songTitle, songArtist, songGenre, songLength, fileChooserButton, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);
        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    /**
     * Creates a popup and populates it with the required input field and gets the selected song to populate the input fields
     * then attaches a function to the submit button to send the data to the business logic to handle the form.
     * Then it refreshes the song list and closes the popup.
    * */
    public void EditSong() {
        SongDataModel model = (SongDataModel) songTable.getSelectionModel().getSelectedItem();

        if (model == null) {
            return;
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("Edit Song");

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        int itemID = model.getSongID();

        TextField songTitle = new TextField();
        songTitle.setPromptText("Enter song title");
        songTitle.setText(model.getSongTitle());

        TextField songArtist = new TextField();
        songArtist.setPromptText("Enter song artist");
        songArtist.setText(model.getSongArtist());

        TextField songGenre = new TextField();
        songGenre.setPromptText("Enter song genre");
        songGenre.setText(model.getSongGenre());

        TextField songLength = new TextField();
        songLength.setPromptText("Enter song length");
        songLength.setText(model.getSongLength());


        Button fileChooserButton = new Button("Choose File");
        FileChooser fileChooser = new FileChooser();

        fileChooserButton.setOnAction(e -> selectedFile = fileChooser.showOpenDialog(popupStage));

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            Logic.HandleEditSong(itemID, songTitle.getText(), songArtist.getText(), songGenre.getText(), songLength.getText(), selectedFile);
            PopulateSongTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(songTitle, songArtist, songGenre, songLength, fileChooserButton, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);

        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    /**
     * Starts deletion of the selected song. Get the selected song and sends it to the business logic to start deletion.
     * Then it refreshed the song list.
     * @throws IOException
     */
    public void DeleteSong() throws IOException {
        SongDataModel model = (SongDataModel) songTable.getSelectionModel().getSelectedItem();

        if (model != null && DeletionAlert()) {
            Logic.HandleDeleteSong(model, true);
            PopulateSongTable();
        }
    }

    /**
     * This function is connected to the play button. So long as a song is loaded in the media player.
     * It either plays or pauses the currently loaded media.
     */
    public void ToggleSong() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    /**
     * This functions forcefully empties the songs in playlist list. To manually refresh it when a playlist is deleted.
     * Since it is always the current playlist that is deleted.
     */
    public void EmptySongsInPlaylistList() {
        ObservableList<SongsInPlaylistDataModel> items = FXCollections.observableArrayList();
        songsInPlaylistList.setItems(items);
    }

    /**
     * Adds the currently selected song to the currently selected playlist as long as both are selected.
     * The logic is sent to the business layer which handles the rest. Then the ListView for the songs in playlist is refreshed.
     */
    public void AddSongToPlaylist() {
        SongDataModel song = (SongDataModel) songTable.getSelectionModel().getSelectedItem();
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();

        if (song != null && playlist != null) {
            Logic.HandleAddSongToPlaylist(song, playlist, playlist.getPlaylistSongCount());
            PopulateSongsInPlaylist();
        }
    }

    /**
     * Removes the currently selected song to the currently selected playlist as long as both are selected.
     * The logic is sent to the business layer which handles the rest. Then the ListView for the songs in playlist is refreshed.
     */
    public void RemoveSongFromPlaylist() {
        SongsInPlaylistDataModel song = (SongsInPlaylistDataModel) songsInPlaylistList.getSelectionModel().getSelectedItem();
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();

        if (song != null && playlist != null) {
            Logic.HandleRemoveSongFromPlaylist(song, playlist);
            PopulateSongsInPlaylist();
        }
    }

    /**
     * Creates a popup and populates it with the required input field and
     * then attaches a function to the submit button to send the data to the business logic to handle the form.
     * Then it refreshes the playlist list and closes the popup when the submit button is pressed.
     * */
    public void NewPlaylist() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add playlist");

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        TextField playlistTitle = new TextField();
        playlistTitle.setPromptText("Enter playlist title");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            Logic.HandleAddPlaylist(playlistTitle.getText());
            PopulatePlaylistTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(playlistTitle, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);
        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    /**
     * Creates a popup and populates it with the required input field and gets the selected playlist to populate the input field
     * then attaches a function to the submit button to send the data to the business logic to handle the form.
     * Then it refreshes the playlist list and closes the popup.
     * */
    public void EditPlaylist() {
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();

        if (playlist == null) {
            return;
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("Add playlist");

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        TextField playlistTitle = new TextField();
        playlistTitle.setPromptText("Enter playlist title");
        playlistTitle.setText(playlist.getPlaylistTitle());

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            Logic.HandleEditPlaylist(playlist.getPlaylistID(), playlistTitle.getText());
            PopulatePlaylistTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(playlistTitle, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);
        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    /**
     * Starts the deletion of a playlist. An alert popups and the user has to press OK to continue with the deletion.
     * Gets the currently selected playlist and tells the business logic to start delection of the playlist.
     * Then the playlist TableView is refreshed and the ListView that shows songs in the playlist is emptied.
     */
    public void DeletePlaylist() {
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();

        if (playlist != null && DeletionAlert()) {
            Logic.HandleDeletePlaylist(playlist.getPlaylistID());
            PopulatePlaylistTable();
            EmptySongsInPlaylistList();
        }
    }

    /**
     * Creates a generic alert window using the Java Alert system. To notify of a destructive action.
     * @return Returns true if the OK button is clicked and returns false if the cancel button is clicked
     */
    public boolean DeletionAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Confirm Deletion?");
        alert.setContentText("This action cannot be undone!");

        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        var result = alert.showAndWait();

        return result.isEmpty() || result.get() != ButtonType.CANCEL;
    }

    /**
     * Closes the JavaFX platform which closes the application
     */
    public void CloseApplication() {
        Platform.exit();
    }
}

package gui;

import be.PlaylistDataModel;
import be.SongDataModel;
import be.SongsInPlaylistDataModel;
import bl.Logic;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class MainController {
    public TableView playlistTable;
    public TextField filterValue;
    public Slider volumeSlider;
    public ListView songsInPlaylistList;
    public TableView songTable;
    public Label nowPlaying;

    private File selectedFile;

    private MediaPlayer mediaPlayer;

    public void initialize() throws IOException {
        populateSongTable();
        PopulatePlaylistTable();

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume((double) newValue);
        });
    }

    public void playSong(String path, SongDataModel item) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        String audioFile = "file://" + System.getProperty("user.dir") + "/" + path;
        Media media = new Media(audioFile);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setVolume(volumeSlider.getValue());
        nowPlaying.setText(item.getSongTitle() + " by " + item.getSongArtist() + " is Playing");
    }

    public void playSongFromPlaylist(String path, SongsInPlaylistDataModel item) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        String audioFile = "file://" + System.getProperty("user.dir") + "/" + path;
        Media media = new Media(audioFile);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setVolume(volumeSlider.getValue());
        nowPlaying.setText(item.getSongTitle() + " is Playing");
    }

    public void populateSongTable() {
        Logic.generateSongTable(songTable, filterValue.getText(),this);
    }

    public void FilterSongList(ActionEvent actionEvent) {
        Logic.generateSongTable(songTable, filterValue.getText(), this);
    }

    public void NewSong(ActionEvent actionEvent) {
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

        fileChooserButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(popupStage);
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            bl.Logic.handleSubmit(songTitle.getText(), songArtist.getText(), songGenre.getText(), songLength.getText(), selectedFile, popupStage);
            populateSongTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(songTitle, songArtist, songGenre, songLength, fileChooserButton, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);
        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public void EditSong(ActionEvent actionEvent) {
        System.out.println(songTable.getSelectionModel().getSelectedItem().toString());
        SongDataModel model = (SongDataModel) songTable.getSelectionModel().getSelectedItem();
        System.out.println(model.getSongID());

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

        fileChooserButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(popupStage);
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            bl.Logic.handleEditSubmit(itemID, songTitle.getText(), songArtist.getText(), songGenre.getText(), songLength.getText(), selectedFile, popupStage);
            populateSongTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(songTitle, songArtist, songGenre, songLength, fileChooserButton, submitButton);


        Scene popupScene = new Scene(formLayout, 250, 350);

        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();

    }

    public void DeleteSong(ActionEvent actionEvent) throws IOException {
        SongDataModel model = (SongDataModel) songTable.getSelectionModel().getSelectedItem();
        Logic.handleDeleteSong(model, true);
    }

    public void CloseApplication(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void ToggleSong(ActionEvent actionEvent) {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public void NewPlaylist(ActionEvent actionEvent) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add playlist");

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        TextField playlistTitle = new TextField();
        playlistTitle.setPromptText("Enter playlist title");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            bl.Logic.handleAddPlaylistSubmit(playlistTitle.getText());
            PopulatePlaylistTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(playlistTitle, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);
        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public void PopulatePlaylistTable() {
        Logic.generatePlaylistTable(playlistTable, this);
    }

    public void EmptySongsInPlaylistList() {
        Logic.emptySongsInPlaylistList(songsInPlaylistList, this);
    }

    public void PopulateSongsInPlaylist() {
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();
        Logic.generateSongsInPlaylistList(playlist.getPlaylistID(), songsInPlaylistList, this);
    }

    public void GetSongsInPlaylist(PlaylistDataModel playlist) {
        Logic.generateSongsInPlaylistList(playlist.getPlaylistID(), songsInPlaylistList, this);
    }

    public void AddSongToPlaylist(ActionEvent actionEvent) {
        SongDataModel song = (SongDataModel) songTable.getSelectionModel().getSelectedItem();
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();
        Logic.HandleAddSongToPlaylist(song, playlist, playlist.getPlaylistSongCount());
        PopulateSongsInPlaylist();
    }

    public void RemoveSongFromPlaylist(ActionEvent actionEvent) {
        SongsInPlaylistDataModel song = (SongsInPlaylistDataModel) songsInPlaylistList.getSelectionModel().getSelectedItem();
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();
        Logic.HandleRemoveSongFromPlaylist(song, playlist);
        PopulateSongsInPlaylist();
    }

    public void EditPlaylist(ActionEvent actionEvent) {
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();

        Stage popupStage = new Stage();
        popupStage.setTitle("Add playlist");

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        TextField playlistTitle = new TextField();
        playlistTitle.setPromptText("Enter playlist title");
        playlistTitle.setText(playlist.getPlaylistTitle());

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            bl.Logic.handleEditPlaylistSubmit(playlist.getPlaylistID(), playlistTitle.getText());
            PopulatePlaylistTable();
            popupStage.close();
        });

        formLayout.getChildren().addAll(playlistTitle, submitButton);

        Scene popupScene = new Scene(formLayout, 250, 350);
        popupStage.setScene(popupScene);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public void DeletePlaylist(ActionEvent actionEvent) {
        PlaylistDataModel playlist = (PlaylistDataModel) playlistTable.getSelectionModel().getSelectedItem();
        Logic.HandleDeletePlaylist(playlist.getPlaylistID());
        PopulatePlaylistTable();
        EmptySongsInPlaylistList();
    }
}

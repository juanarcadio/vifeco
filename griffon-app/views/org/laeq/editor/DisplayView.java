package org.laeq.editor;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.laeq.model.Icon;
import org.laeq.model.icon.IconSVG;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class DisplayView extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull private DisplayController controller;
    @MVCMember @Nonnull private DisplayModel model;
    @MVCMember @Nonnull File file;
    @MVCMember @Nonnull Duration currentTime;
    @MVCMember @Nonnull Controls controls;

    private MediaPlayer mediaPlayer;
    @FXML private Pane playerPane;
    @FXML private MediaView mediaView;
    @FXML private Button volumeActionTarget;

    public Stage stage;

    private Boolean isPlaying = false;
;

    private Icon volumeOn = new Icon(IconSVG.volumeOn, org.laeq.model.icon.Color.white);
    private Icon volumeOff = new Icon(IconSVG.volumeOff, org.laeq.model.icon.Color.white);
    public Duration videoDuration;

    @Override
    public void initUI() {
        stage = (Stage) getApplication().createApplicationContainer(Collections.<String,Object>emptyMap());
        stage.setTitle(getApplication().getMessageSource().getMessage("editor.window.title"));
        stage.getIcons().add( getImage("favicon-32x32.png"));
        stage.setScene(init());
        stage.sizeToScene();
        stage.setAlwaysOnTop(false);
        initPlayer();

        getApplication().getWindowManager().attach("display", stage);
        getApplication().getWindowManager().show("display");

        stage.setOnCloseRequest(event -> {
            runInsideUISync(()->{
                mediaPlayer.stop();
            });

            getApplication().getEventRouter().publishEventAsync("mvc.clean", Arrays.asList("display"));
        });
    }

    @Override
    public void mvcGroupDestroy(){
        mediaPlayer.stop();
    }

    private void initPlayer(){
        try {
            Media media = new Media(file.getCanonicalFile().toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            volumeActionTarget.setText("");

            mediaPlayer.setOnReady(() -> {
                controller.isReady();
                mediaPlayer.play();
                mediaPlayer.pause();
                runOutsideUIAsync(() -> mediaPlayer.seek(currentTime));
                videoDuration = mediaPlayer.getTotalDuration();
                mediaPlayer.setRate(controls.speed.getValue());
            });

            mediaPlayer.setOnError(() -> {
                getApplication().getEventRouter().publishEventOutsideUI("status.error", Arrays.asList("video.metadata.error"));
            });

            this.volumeOff();
        } catch (Exception e) {

        }
    }

    public void volumeOff(){
        volumeActionTarget.getStyleClass().clear();
        volumeActionTarget.getStyleClass().addAll("btn", "btn-danger");
        volumeActionTarget.setGraphic(volumeOff);
        mediaPlayer.setVolume(0);
    }

    public void volumeOn(){
        volumeActionTarget.getStyleClass().clear();
        volumeActionTarget.getStyleClass().addAll("btn", "btn-success");
        volumeActionTarget.setGraphic(volumeOn);
        mediaPlayer.setVolume(1);
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        Node node = loadFromFXML();

        if (node instanceof Parent) {
            scene.setRoot((Parent) node);
        } else {
            ((Group) scene.getRoot()).getChildren().addAll(node);
        }
        connectActions(node, controller);
        connectMessageSource(node);

        return scene;
    }

    public void pause() {
        isPlaying = false;
        Platform.runLater(() -> {
            mediaPlayer.pause();
        });
    }

    public void play() {
        isPlaying = true;
        Platform.runLater(() -> {
            mediaPlayer.play();
        });
    }

    public void refreshRate(Double rate) {
        Platform.runLater(()->{
            mediaPlayer.setRate(rate);
        });
    }

    private Image getImage(String path) {
        return new Image(getClass().getClassLoader().getResourceAsStream(path));
    }


    //Slider events
    public void sliderReleased(Duration now){
        Platform.runLater(() -> {
            mediaPlayer.seek(now);
            if(isPlaying){
                mediaPlayer.play();
            }
        });
    }

    public void sliderPressed() {
        mediaPlayer.pause();
    }

    public void sliderCurrentTime(Double object) {
        Duration now = videoDuration.multiply(object / 100);
        mediaPlayer.seek(now);
    }

    public void rowCurrentTime(Duration now) {
        mediaPlayer.seek(now);
    }

    public void elapsedCurrentTime(Duration now) {
        mediaPlayer.seek(now);
    }

    public void rewind(Duration now) {
        mediaPlayer.seek(now);
    }
}

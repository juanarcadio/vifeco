package org.laeq;

import griffon.core.RunnableWithArgs;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ControllerAction;
import griffon.core.mvc.MVCGroup;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.transform.Threading;
import javafx.scene.control.TableColumn;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.laeq.model.Collection;
import org.laeq.model.Point;
import org.laeq.model.User;
import org.laeq.model.Video;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ArtifactProviderFor(GriffonController.class)
public class VideoController extends AbstractGriffonController implements CRUDInterface<Video>{
    @MVCMember @Nonnull private VideoModel model;
    @MVCMember @Nonnull private VideoView view;

    @Inject private DatabaseService dbService;
    @Inject private ExportService exportService;

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        runInsideUIAsync(() -> {
            try{
                model.videoList.addAll(dbService.videoDAO.findAll());
                model.getUserSet().addAll(dbService.userDAO.findAll());
                model.getCollectionSet().addAll(dbService.collectionDAO.findAll());
                model.categorySet.addAll(dbService.categoryDAO.findAll());
                model.videoList.forEach(v -> setVideoDuration(v));

                view.initForm();

                getApplication().getEventRouter().publishEventAsync("status.info", Arrays.asList("db.video.fetch.success"));
            } catch (Exception e){
                getApplication().getEventRouter().publishEventAsync("status.error", Arrays.asList("db.video.fetch.error"));
            }
        });

        //@todo add BiDirectionalBinding to remove this hack

        getApplication().getEventRouter().addEventListener(listeners());
    }

    @Override
    public void mvcGroupDestroy(){

    }

    private void setVideoDuration(Video video){
        if(video.getDuration().equals(Duration.UNKNOWN) == false){
            return;
        }

        runOutsideUI(() -> {
            File file = new File(video.getPath());

            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                video.setDuration(media.getDuration());
                try {
                    dbService.videoDAO.create(video);
                    getApplication().getEventRouter().publishEvent("videolist.refresh");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mediaPlayer.setOnError(() -> getApplication().getEventRouter().publishEventOutsideUI("status.error", Arrays.asList("video.metadata.error", video.getPath())));
        });

    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    public void clear(){
        getApplication().getEventRouter().publishEventOutsideUI("status.reset");
        model.clear();
    }

    @Override
    public void save() {
        //noop
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    public void delete(Video video){
        if(dbService.videoDAO.delete(video)){
            model.videoList.remove(video);
            view.refresh();
            getApplication().getEventRouter().publishEvent("status.success.parametrized", Arrays.asList("video.delete.success", video.pathToName()));
        }  else {
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("video.delete.error"));
        }
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    public void edit(){
        if(model.selectedVideo == null){
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("video.edit.error"));
        } else {

            File file = new File(model.selectedVideo.getPath());

            if(file.exists()){
                getApplication().getEventRouter().publishEvent("status.info.parametrized", Arrays.asList("video.edit.success",model.selectedVideo.getPath()));
                createDisplay();
            } else {
                getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("video.edit.file_not_found"));
            }
        }
    }

    /**
     * Clean and destroy
     * @param name
     */
    private void cleanAndDestroy(String name){
        destroyMVC(name);
        closeWindow(name);
    }

    private void closeWindow(String name){
        try{
            Stage window = (Stage) getApplication().getWindowManager().findWindow(name);
            getApplication().getWindowManager().detach(name);
            window.close();
        } catch (Exception e){

        }
    }

    private void destroyMVC(String name){
        try{
            MVCGroup group = getApplication().getMvcGroupManager().findGroup(name);
            if(group != null){
                group.destroy();
            }
        }catch (Exception e){

        }
    }

    private void  createDisplay(){
        if(model.currentVideo != null){
            cleanAndDestroy("editor");
        }

        model.currentVideo = "editor";
        try{
            if(model.selectedVideo.getDuration() != Duration.UNKNOWN && model.selectedVideo.getDuration() != Duration.ZERO){
                Map<String, Object> args = new HashMap<>();
                args.put("video",model.selectedVideo);
                createMVCGroup("editor", args);
            } else {
                getApplication().getEventRouter().publishEvent("status.error.parametrized", Arrays.asList("video.media.unsupported", model.selectedVideo.pathToName()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @ControllerAction
    @Threading(Threading.Policy.OUTSIDE_UITHREAD_ASYNC)
    public void export(Video video){
        try {
            String filename = exportService.export(video);

            getApplication().getEventRouter().publishEvent("status.success.parametrized", Arrays.asList("video.export.success", filename));
        } catch (IOException e) {
            e.printStackTrace();
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("video.export.error"));
        }
    }

    public void updateUser(TableColumn.CellEditEvent<Video, User> event) {
        try {
            Video video = event.getRowValue();
            video.setUser(event.getNewValue());
            dbService.videoDAO.create(video);
            getApplication().getEventRouter().publishEvent("status.success", Arrays.asList("video.user.updated.success"));
        } catch (Exception e) {
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("video.user.updated.error"));
        }

        runInsideUISync(() -> {
            view.refresh();
        });
    }

    public void updateCollection(Video video, Collection newValue) {
        try{
            clear();
            video.updateCollection(newValue);
            dbService.videoDAO.create(video);

            getApplication().getEventRouter().publishEvent("status.success", Arrays.asList("video.collection.updated.success"));
        } catch (Exception e){
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("video.collection.updated.error"));
        }

        runInsideUISync(() -> {
            view.refresh();
        });
    }

    public void select(Video video) {
        model.clear();

        if(video.getDuration().equals(Duration.UNKNOWN) || video.getDuration().equals(Duration.ZERO)){
            runOutsideUI(() -> {
                File file = new File(video.getPath());

                Media media = new Media(file.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    video.setDuration(media.getDuration());
                    model.setSelectedVideo(video);
                    getApplication().getEventRouter().publishEventOutsideUI("status.info.parametrized", Arrays.asList("video.details.success", video.pathToName()));

                    try {
                        dbService.videoDAO.create(video);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                mediaPlayer.setOnError(() -> getApplication().getEventRouter().publishEventOutsideUI("status.error", Arrays.asList("video.metadata.error", video.getPath())));
            });
        } else {
            model.setSelectedVideo(video);
            getApplication().getEventRouter().publishEventOutsideUI("status.info.parametrized", Arrays.asList("video.details.success", video.pathToName()));
        }
    }

    private void refreshVideoList(){
        try {
            List<Video> list = dbService.videoDAO.findAll();

            list.forEach(v -> {
                if(model.videoList.contains(v) == false){
                    model.videoList.add(v);
                }
            });

            model.videoList.forEach(v -> setVideoDuration(v));
        } catch (Exception e) {
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("db.error.fetch"));
        }
    }

    private Map<String, RunnableWithArgs> listeners(){
        Map<String, RunnableWithArgs> list = new HashMap<>();

        list.put("video.created", objects -> refreshVideoList());
        list.put("videolist.refresh", objects -> view.refresh());

        list.put("point.added", objects -> {
            model.addPoint((Point) objects[0]);
            view.refresh();
        });

        list.put("timeline.point.deleted", objects -> {
            model.removePoint((Point) objects[0]);
            view.refresh();
        });

        list.put("player.point.deleted", objects -> {
            model.removePoint((Point) objects[0]);
            view.refresh();
        });

        return list;
    }



    @ControllerAction
    @Threading(Threading.Policy.OUTSIDE_UITHREAD_ASYNC)
    public void save(Video video) {
        if(dbService.videoDAO.create(video)){
            view.refresh();
            getApplication().getEventRouter().publishEvent("status.success.parametrized", Arrays.asList("video.update.success", video.pathToName()));
        } else {
            getApplication().getEventRouter().publishEvent("status.error.parametrized", Arrays.asList("video.update.error", video.pathToName()));
        }
    }
}

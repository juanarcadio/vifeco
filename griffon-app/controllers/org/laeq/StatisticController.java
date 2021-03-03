package org.laeq;

import griffon.core.RunnableWithArgs;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ControllerAction;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.transform.Threading;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.laeq.model.Video;
import org.laeq.model.statistic.MatchedPoint;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ArtifactProviderFor(GriffonController.class)
public class
StatisticController extends AbstractGriffonController {
    @MVCMember @Nonnull private StatisticModel model;
    @MVCMember @Nonnull private StatisticView view;

    @Inject private DatabaseService dbService;

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        try{
            List<Video> list = dbService.videoDAO.findAll();
            model.videos.addAll(list);

            Video video1 = list.get(list.size() - 1);
            Video video2 = list.get(list.size() - 2);

            StatisticService service = new StatisticService();
            service.execute(Arrays.asList(video1, video2), 10);

            model.tarjans.addAll(service.getTarjanDiff());

            MatchedPoint matchedPoint = service.getTarjanDiff().get(0).matchedPoints.get(0);
            displayMatchedPoint(matchedPoint);

            getApplication().getEventRouter().publishEvent("status.info", Arrays.asList("db.success.fetch"));
        } catch (Exception e){
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("db.error.fetch"));
        }

        getApplication().getEventRouter().addEventListener(listeners());
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    public void compare(){
        closeStatisticDisplay();
        model.reset();

        List<Video> videos = model.videos.stream().filter(v -> v.getSelected()).collect(Collectors.toList());

        if(videos.size() != 2 || videos.get(0).getCollection().equals(videos.get(1).getCollection()) == false){
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("statistic.video.selection.error"));
            return;
        }

        try {
            StatisticService service = new StatisticService();
            System.out.println(model.durationStep.get());
            service.execute(videos, model.durationStep.get());

            runInsideUISync(() -> {
                model.tarjans.addAll(service.getTarjanDiff());
                model.videoName.set(service.getVideo1().pathToName());
                model.user1.set(service.getVideo1().getUser().toString());
                model.user2.set(service.getVideo2().getUser().toString());
                model.collection.set(service.getVideo1().getCollection().toString());
                model.duration.set(service.getVideo1().getDurationFormatted());
            });

            getApplication().getEventRouter()
                    .publishEvent("status.info.parameterized",
                    Arrays.asList("statistic.video.selection.success",
                            service.getVideo2().pathToName(),
                            model.durationStep.toString()));

        }catch (Exception e){
            e.printStackTrace();
            getApplication().getEventRouter().publishEvent("status.error", Arrays.asList("statistic.video.selection.error"));
        }
    }

    private void closeStatisticDisplay(){
        try{
            Stage statistic_display = (Stage) getApplication().getWindowManager().findWindow("statistic_display");
            getApplication().getWindowManager().detach("statistic_display");
            statistic_display.close();
            getApplication().getMvcGroupManager().findGroup("statistic_display").destroy();
        } catch (Exception e){

        }
    }

    private Map<String, RunnableWithArgs> listeners(){
        Map<String, RunnableWithArgs> list = new HashMap<>();

        return list;
    }

    public void displayMatchedPoint(MatchedPoint mp) {
        Object statistic_display = getApplication().getWindowManager().findWindow("statistic_display");
        if(statistic_display == null){
            Map<String, Object> args = new HashMap<>();
            args.put("matchedPoint", mp);
            createMVCGroup("statistic_display", args);
        } else {
            getApplication().getEventRouter().publishEventOutsideUI("statistic.mapped_point.display", Arrays.asList(mp));
        }
    }
}
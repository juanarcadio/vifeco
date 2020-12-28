import griffon.util.AbstractMapResourceBundle;

import javax.annotation.Nonnull;
import java.util.Map;

import static griffon.util.CollectionUtils.map;
import static java.util.Arrays.asList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        map(entries)
            .e("application", map()
                .e("title", "vifeco")
                .e("startupGroups", asList("vifeco"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                .e("vifeco", map()
                    .e("view", "org.laeq.VifecoView")
                )
                .e("menu", map()
                    .e("model", "org.laeq.menu.MenuModel")
                    .e("view", "org.laeq.menu.MenuView")
                    .e("controller", "org.laeq.menu.MenuController")
                )
                .e("middle", map()
                        .e("model", "org.laeq.template.MiddlePaneModel")
                        .e("view", "org.laeq.template.MiddlePaneView")
                        .e("controller", "org.laeq.template.MiddlePaneController")
                )
                .e("bottom", map()
                        .e("model", "org.laeq.menu.BottomModel")
                        .e("view", "org.laeq.menu.BottomView")
                        .e("controller", "org.laeq.menu.BottomController")
                )
                .e("status", map()
                        .e("model", "org.laeq.system.StatusModel")
                        .e("view", "org.laeq.system.StatusView")
                        .e("controller", "org.laeq.system.StatusController")
                )
                .e("user_container", map()
                        .e("model", "org.laeq.UserModel")
                        .e("view", "org.laeq.UserView")
                        .e("controller", "org.laeq.UserController")
                )
                .e("video_container", map()
                        .e("model", "org.laeq.VideoModel")
                        .e("view", "org.laeq.VideoView")
                        .e("controller", "org.laeq.VideoController")
                )
                .e("category_container", map()
                        .e("model", "org.laeq.CategoryModel")
                        .e("view", "org.laeq.CategoryView")
                        .e("controller", "org.laeq.CategoryController")
                )
                .e("collection_container", map()
                        .e("model", "org.laeq.CollectionModel")
                        .e("view", "org.laeq.CollectionView")
                        .e("controller", "org.laeq.CollectionController")
                )
                .e("statistic_container", map()
                        .e("model", "org.laeq.statistic.ContainerModel")
                        .e("view", "org.laeq.statistic.ContainerView")
                        .e("controller", "org.laeq.statistic.ContainerController")
                )
                .e("player", map()
                        .e("model", "org.laeq.video.player.ContainerModel")
                        .e("view", "org.laeq.video.player.ContainerView")
                        .e("controller", "org.laeq.video.player.ContainerController")
                )
                .e("video_player", map()
                        .e("model", "org.laeq.video.player.PlayerModel")
                        .e("view", "org.laeq.video.player.PlayerView")
                        .e("controller", "org.laeq.video.player.PlayerController")
                )
                .e("controls", map()
                        .e("model", "org.laeq.video.ControlsModel")
                        .e("view", "org.laeq.video.ControlsView")
                        .e("controller", "org.laeq.video.ControlsController")
                )
                .e("category", map()
                        .e("model", "org.laeq.video.CategoryModel")
                        .e("view", "org.laeq.video.category.CategoryView")
                        .e("controller", "org.laeq.video.CategoryController")
                )
                .e("timeline", map()
                        .e("model", "org.laeq.video.player.TimelineModel")
                        .e("view", "org.laeq.video.player.TimelineView")
                        .e("controller", "org.laeq.video.player.TimelineController")
                )
                .e("database", map()
                        .e("controller", "org.laeq.db.DatabaseController")
                )
                .e("about_container", map()
                        .e("model", "org.laeq.info.AboutModel")
                        .e("view", "org.laeq.info.AboutView")
                        .e("controller", "org.laeq.info.AboutController")
                )
                .e("test", map()
                        .e("model", "org.laeq.editor.PlayerModel")
                        .e("view", "org.laeq.editor.PlayerView")
                        .e("controller", "org.laeq.editor.PlayerController")
                )
                .e("display", map()
                        .e("model", "org.laeq.editor.DisplayModel")
                        .e("view", "org.laeq.editor.DisplayView")
                        .e("controller", "org.laeq.editor.DisplayController")
                )
                .e("test2", map()
                        .e("model", "org.laeq.editor.ControlsModel")
                        .e("view", "org.laeq.editor.ControlsView")
                        .e("controller", "org.laeq.editor.ControlsController")
                )
            );
    }
}
package org.laeq.db;

import griffon.core.artifact.GriffonService;
import griffon.metadata.ArtifactProviderFor;
import javafx.scene.media.Media;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;
import org.laeq.model.CategoryCollection;
import org.laeq.model.User;
import org.laeq.model.Video;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

@javax.inject.Singleton
@ArtifactProviderFor(GriffonService.class)
public class DatabaseService extends AbstractGriffonService {
    private final DatabaseManager manager;

    public DatabaseService() {
        DatabaseConfigBean configBean = new DatabaseConfigBean("jdbc:hsqldb:hsql://localhost/vifecodb", "SA", "");

        manager = new DatabaseManager(configBean);
    }

    public DatabaseManager getManager() {
        return manager;
    }

    public void init() {
        try {
            manager.getTableStatus();
        } catch (DAOException | SQLException e) {
            initTables();
        }
    }

    private void initTables(){
        UserDAO userDAO = new UserDAO(manager, UserDAO.sequence_name);
        CategoryCollectionDAO categoryCollectionDAO = new CategoryCollectionDAO(manager, CategoryCollectionDAO.sequence_name);

        try{
            URL tableQuery = getClass().getClassLoader().getResource("sql/create_tables.sql");
            manager.loadFixtures(tableQuery);

            URL sequenceQuery = getClass().getClassLoader().getResource("sql/create_sequences.sql");
            manager.loadFixtures(sequenceQuery);

            getLog().info("DatabaseService: tables and sequences created");

        } catch (Exception e) {
            getLog().error("DatabaseService init: cannot load create_table/create_sequence.");
        }

        try{
            categoryCollectionDAO.init();
            getLog().info("DatabaseService: default category collection created");
        } catch (Exception e){
            getLog().error("DatabaseService: cannot create default category collection");
        }
    }

    public Video createVideo(File file) throws IOException, SQLException, DAOException {
        UserDAO userDAO = getUserDAO();
        CategoryCollectionDAO categoryCollectionDAO = getCategoryCollectionDAO();
        VideoDAO videoDAO = getVideDAO();

        User defaultUser = userDAO.findDefault();
        CategoryCollection defaultCategoryCollection = categoryCollectionDAO.findDefault();

        Media media = new Media(file.getCanonicalFile().toURI().toString());

        Video video = new Video(file.getPath(), media.getDuration(), defaultUser, defaultCategoryCollection);
        videoDAO.insert(video);

        return video;
    }

    public UserDAO getUserDAO() {
        return new UserDAO(manager, UserDAO.sequence_name);
    }
    public CategoryDAO getCategoryDAO() {
        return new CategoryDAO(manager, CategoryDAO.sequence_name);
    }
    public CategoryCollectionDAO getCategoryCollectionDAO() {
        return new CategoryCollectionDAO(manager, CategoryCollectionDAO.sequence_name);
    }
    public PointDAO getPointDAO() {
        return new PointDAO(manager, PointDAO.sequence_name);
    }
    public VideoDAO getVideDAO() {
        return new VideoDAO(manager, VideoDAO.sequence_name);
    }

    public void getTableStatus() throws SQLException, DAOException {
        manager.getTableStatus();
    }
}
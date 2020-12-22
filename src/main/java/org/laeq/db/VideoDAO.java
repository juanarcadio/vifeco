package org.laeq.db;

import org.laeq.model.Collection;
import org.laeq.model.User;
import org.laeq.model.Video;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class VideoDAO extends AbstractDAO implements DAOInterface<Video>{
    private CollectionDAO collectionDAO;
    private String insertQuery = "INSERT INTO VIDEO (PATH, DURATION, USER_ID, COLLECTION_ID) VALUES (?, ?, ?, ?);";

    public VideoDAO(@Nonnull DatabaseManager manager, CollectionDAO collectionDAO) {
        super(manager);
        this.collectionDAO = collectionDAO;
    }

    @Override
    public void insert(Video video) throws DAOException {
        int result = 0;

        try(Connection connection = getManager().getConnection();
            PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, video.getPath());
            statement.setDouble(2, video.getDuration());
            statement.setInt(3, video.getUser().getId());
            statement.setInt(4, video.getCollection().getId());

            result = statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();


        } catch (Exception e){
            String message = String.format("VideoDAO: insert - %s - $s", video, e.getMessage());
            getLogger().error(message);
        }

        if(result != 1)
            throw new DAOException("Error during DAO insert video");
    }

    @Override
    public List<Video> findAll() {
        String query = "select V.id as video_id,  V.path as path,  V.duration as duration, V.user_id AS user_id, \n" +
                        "U.first_name AS first_name, " +
                        "U.last_name AS last_name, CC.ID AS cc_id, CC.name AS cc_name, " +
                        "count(P.video_id) as total_point, CC.Name as c_name from VIDEO AS V \n" +
                        "left join USER AS U on V.user_id = U.id \n" +
                        "left join POINT as P ON P.video_id = V.id  \n" +
                        "left join COLLECTION AS CC on CC.id = V.collection_id \n" +
                        "GROUP BY P.video_id, V.user_id, V.id, CC.id, U.first_name, U.last_name, CC.ID, CC.name ORDER BY V.id DESC;";

        List<Video> result = new ArrayList<>();

        try(Connection connection = getManager().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);){

            ResultSet queryResult = statement.executeQuery();
            result = getResult(queryResult);

        } catch (SQLException | DAOException e){
            getLogger().error(e.getMessage());
        }

        return result;
    }



    private List<Video> getResult(ResultSet datas) throws SQLException, DAOException {
        List<Video> result = new ArrayList<>();

        while(datas.next()){
            result.add(generateVideo(datas));
        }

        return result;
    }

    private Video generateVideo(ResultSet datas) throws SQLException, DAOException {
        Video video = new Video();





        return video;
    }


    @Override
    public void delete(Video video) throws DAOException {
        int result = 0;
        String query = "DELETE FROM VIDEO WHERE ID=?";

        try(Connection connection = getManager().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);)
        {

            result = statement.executeUpdate();
        } catch (Exception e){
            getLogger().error(e.getMessage());
        }

        if(result !=1)
            throw new DAOException("Error deleting a video");
    }

    public void updateDuration(Video video) throws SQLException, DAOException {


    }

    public void update(Video video) throws SQLException, DAOException {


    }

    public void updateUser(Video video, User user) throws SQLException, DAOException {

    }

    public void updateCollection(Video video, Collection collection) throws SQLException, DAOException {

    }
}

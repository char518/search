package common;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DataSourceUtils {

    private static final String DATA_SOURCE_URL = "jdbc:mysql://192.168.77.129:3306/search";

    private static final String PASSWORD = "charl123456";

    private static final String USERNAME = "root";

    public static Connection getConn() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DATA_SOURCE_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void refreshDataToEs(int id) {
        Connection conn = getConn();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("select * from news where id > " + id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> param = new HashMap<String, Object>();
                long aLong = resultSet.getLong(1);
                param.put("id", aLong);
                param.put("title", resultSet.getString(2));
                param.put("content", resultSet.getString(3));
                param.put("auther", resultSet.getString(4));
                param.put("key_word", resultSet.getString(5));
                param.put("reply", resultSet.getLong(6));
                param.put("source", resultSet.getString(7));
                param.put("url", resultSet.getString(8));
                System.out.println(param);
                IndexRequest request = new IndexRequest("anews", "news", aLong + "");
                request.source(param);
                try {
                    IndexResponse index = EsUtils.esClient().index(request);
                    System.out.println("index response status:" + index.status());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        refreshDataToEs(1);
        System.out.println("刷新完成");
    }

}

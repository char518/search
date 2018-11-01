package controller;

import common.DataSourceUtils;
import common.EsUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.get.GetResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "operate", urlPatterns = "/operate")
public class OperateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String deleteIds = req.getParameter("deleteIds");
        String[] split = deleteIds.split(",");
        updateIndex(Arrays.asList(split), req);
        req.setAttribute("response", "success");
    }

    public void updateIndex(List<String> ids, HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from news where id in (");
        for (String id : ids) {
            sb.append(id).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        try {
            PreparedStatement preparedStatement = DataSourceUtils.getConn().prepareStatement(sb.toString());
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

                UpdateRequest request = new UpdateRequest("anews", "news", aLong + "");
                request.doc(param);
                UpdateResponse update = EsUtils.esClient().update(request);

                //批量操作，一般尽量都是批量操作
//                BulkRequest bulkRequest = new BulkRequest();
//                bulkRequest.add(request);
//                bulkRequest.add();
//                EsUtils.esClient().bulk(bulkRequest);

                GetResult getResult = update.getGetResult();
                System.out.println(getResult);
            }
        } catch (SQLException e) {
            req.setAttribute("response", "failed");
            e.printStackTrace();
        } catch (IOException e) {
            req.setAttribute("response", "failed");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}

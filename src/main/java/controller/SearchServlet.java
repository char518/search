package controller;

import common.DataSourceUtils;
import common.EsUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "search", urlPatterns = "/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String query = req.getParameter("query");
        System.out.println("request param is:" + query);
        String pageNumStr = req.getParameter("pageNum");
        int pageNum = 1;
        if (null != pageNumStr && Integer.parseInt(pageNumStr) > 1) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        searchNews(pageNum, query, req);
        req.setAttribute("queryBack", query);
        req.getRequestDispatcher("result.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void searchNews(int pageNum, String query, HttpServletRequest req) {
        long startTime = System.currentTimeMillis();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        if(null != query && query.length() > 0) {
            builder.query(QueryBuilders.multiMatchQuery(query, "title", "content"));
        } else {
            builder.query(QueryBuilders.matchAllQuery());
        }
        builder.from(5 * (pageNum - 1));
        builder.size(5);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field field = new HighlightBuilder.Field("title");
        highlightBuilder.field(field);
        highlightBuilder.field(new HighlightBuilder.Field("content"));
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        builder.highlighter(highlightBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("anews").types("news").source(builder);

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        int total = 0;
        try {
            SearchResponse response = EsUtils.esClient().search(searchRequest);
            SearchHits hits = response.getHits();
            total = (int) hits.totalHits;

            for (SearchHit hit : hits) {
                Map<String, Object> news = hit.getSourceAsMap();

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField title = highlightFields.get("title");
                if (null != title) {
                    Text[] fragments = title.getFragments();
                    StringBuilder sb = new StringBuilder();
                    for (Text fragment : fragments) {
                        sb.append(fragment);
                    }
                    news.put("title", sb.toString());
                    System.out.println("title is:" + sb.toString());
                }

                HighlightField content = highlightFields.get("content");
                if (null != content) {
                    Text[] fragments = content.fragments();
                    StringBuilder sb = new StringBuilder();
                    for (Text fragment : fragments) {
                        sb.append(fragment);
                    }
                    news.put("content", sb.toString());
                    System.out.println("content is:" + sb.toString());
                }
                mapList.add(news);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        req.setAttribute("newslist", mapList);
        req.setAttribute("totalHits", total + "");
        req.setAttribute("totalTime", (end - startTime) + "");
    }

    @Override
    public void destroy() {
        try {
            DataSourceUtils.getConn().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.destroy();
    }
}

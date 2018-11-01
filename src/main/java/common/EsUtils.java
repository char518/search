package common;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

public class EsUtils {

    private static volatile RestHighLevelClient restHighLevelClient = null;

    private static final String HOST = "192.168.77.129";

    private static final int PORT = 9200;

    public static RestHighLevelClient esClient() {
        if (restHighLevelClient == null) {
            synchronized (EsUtils.class) {
                try {
                    restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(HOST, PORT, "http")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return restHighLevelClient;
    }

    public static IndicesClient getIndexClient() {
        return esClient().indices();
    }

    /**
     * 创建索引
     * @param index
     * @param shards
     * @param replicas
     * @return
     */
    public static boolean createIndex(String index, int shards, int replicas) {
        Settings settings = Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)
                .build();

        CreateIndexRequest request = new CreateIndexRequest(index, settings);

        boolean acknowledged = false;
        try {
            CreateIndexResponse response = getIndexClient().create(request);
            acknowledged = response.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acknowledged;
    }

    /**
     * 创建映射
     * @param index
     * @param type
     * @param mapping
     * @return
     */
    public static boolean createMapping(String index, String type, XContentBuilder mapping) {
        PutMappingRequest source = new PutMappingRequest()
                .indices(index)
                .type(type)
                .source(mapping);
        boolean acknowledged = false;
        try {
            PutMappingResponse putMappingResponse = getIndexClient().putMapping(source);
            acknowledged = putMappingResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acknowledged;
    }

}




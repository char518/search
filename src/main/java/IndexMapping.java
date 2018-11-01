import common.EsUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

public class IndexMapping {

    public static void main(String[] args) {
        boolean news = EsUtils.createIndex("anews", 3, 1);

        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.startObject("properties");

            builder .startObject("id");
            builder .field("type","long");
            builder .endObject();

            builder .startObject("title");
            builder .field("type","text");
            builder .field("analyzer","ik_max_word");
            builder .field("search_analyzer","ik_max_word");
            builder .endObject();

            builder .startObject("content");
            builder .field("type","text");
            builder .field("analyzer","ik_max_word");
            builder .field("search_analyzer","ik_max_word");
            builder .endObject();

            builder .startObject("auther");
            builder .field("type","keyword");
            builder .endObject();

            builder .startObject("key_word");
            builder .field("type","text");
            builder .field("analyzer","ik_max_word");
            builder .field("search_analyzer","ik_max_word");
            builder .endObject();

            builder .startObject("reply");
            builder .field("type","long");
            builder .endObject();

            builder .startObject("source");
            builder .field("type","keyword");
            builder .endObject();

            builder .endObject();
            builder .endObject();

            String s = builder.toString();
            boolean mapping = EsUtils.createMapping("anews", "news", builder);
            System.out.println(mapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

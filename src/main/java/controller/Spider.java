package controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Spider {

    public static void main(String[] aggs) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response;
        String url = "https://www.bbc.com/zhongwen/simp/world";
        Request request = new Request.Builder().url(url).get().build();
        try {
            response = okHttpClient.newCall(request).execute();
            String string = response.body().string();

            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

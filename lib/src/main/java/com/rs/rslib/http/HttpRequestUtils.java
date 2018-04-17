package com.rs.rslib.http;

import android.content.Context;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * author: xiecong
 * create time: 2017/11/20 10:58
 * lastUpdate time: 2017/11/20 10:58
 * 网络请求工具类
 */

public class HttpRequestUtils {
    /**
     * post请求
     * @param url
     * @param jsonEntity json格式数据
     * @return
     */
    public static Response httpPost(String url, String jsonEntity) {
        if (url == null || url.length() == 0) {
            return null;
        }
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonEntity);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            return OkHttpManager.get().execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Response httpPost(String url, String key, String value) throws Exception{
        RequestBody requestBody = new FormBody.Builder()
                .add(key, value).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return OkHttpManager.get().execute(request);
    }
    /**
     * 不缓存
     * @param url
     * @return
     */
    public static Response httpGet(String url) throws Exception {

        Request request = new  Request.Builder()
                .url(url)
                .build();
        return OkHttpManager.get().execute(request);
    }

    /**
     *
     * @param context
     * @param url
     * @param cacheFile 缓存文件名
     * @param cacheMinute 缓存时间  单位分钟
     * @return
     * @throws Exception
     */
    public static Response httpGet(Context context,String url,String cacheFile,int cacheMinute) throws Exception {
        Request request = new  Request.Builder()
                .url(url)
                .build();
        return OkHttpManager.get().buildCachedHttpClientIfNotExist(context,cacheFile,cacheMinute).execute(request,true);
    }

    /**
     *
     * @param url
     * @param cacheFile
     * @param interceptor 自定义缓存拦截器
     * @return
     * @throws Exception
     */
    public static Response httpGet( String url,String cacheFile, Interceptor interceptor) throws Exception {

        Request request = new  Request.Builder()
                .url(url)
                .build();
        return OkHttpManager.get().buildCachedHttpClient(cacheFile,interceptor).execute(request,true);
    }


}


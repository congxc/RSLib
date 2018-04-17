package com.rs.rslib.http;

import android.content.Context;
import android.support.annotation.NonNull;
import com.rs.rslib.utils.NetWorkUtils;
import com.rs.rslib.utils.SDCardUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求管理
 */

public class OkHttpManager {

    private OkHttpClient mDefaultOkHttpClient;
    private OkHttpClient mCacheOkHttpClient;

    private static final long cacheSize = 1024 * 1024 * 20;// 缓存文件最大限制大小20M
    private  String cacheDirectory = SDCardUtils.getRootPath() +File.separator+"httpCache" ; // 设置缓存文件路径
    private  Cache cache = new Cache(new File(cacheDirectory), cacheSize);  //
    private int cacheTime;
    private CacheControlInterceptor mCacheControlInterceptor;

    private OkHttpManager(){
        buildHttpClient();
    }

    private static final class ClassHolder{
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public static OkHttpManager get(){
        return ClassHolder.instance;
    }

    public OkHttpManager buildHttpClient(){
        mDefaultOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8,TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build();
        return this;
    }

    public OkHttpManager buildCachedHttpClient(Context context, String cacheFile, int cacheMinute){
        this.cacheDirectory = SDCardUtils.getRootPath() + File.separator +cacheFile;
        this.cacheTime = cacheMinute;
        this.cache = new Cache(new File(cacheDirectory),cacheSize);
        if (mCacheControlInterceptor == null) {
            mCacheControlInterceptor = new CacheControlInterceptor(context);
        }
        mCacheOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8,TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .cache(cache)//设置缓存
                .addInterceptor(mCacheControlInterceptor)
                .build();
        return this;
    }
    public OkHttpManager buildCachedHttpClientIfNotExist(Context context, String cacheFile, int cacheMinute){
        if (mCacheOkHttpClient != null) {
            return this;
        }

        this.cacheDirectory = SDCardUtils.getRootPath() + File.separator +cacheFile;
        this.cacheTime = cacheMinute;
        this.cache = new Cache(new File(cacheDirectory),cacheSize);
        if (mCacheControlInterceptor == null) {
            mCacheControlInterceptor = new CacheControlInterceptor(context);
        }
        mCacheOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8,TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .cache(cache)//设置缓存
                .addInterceptor(mCacheControlInterceptor)
                .build();
        return this;
    }

    public OkHttpManager buildCachedHttpClient(String cacheFile, Interceptor cacheInterceptor){
        this.cacheDirectory = SDCardUtils.getRootPath() + File.separator +cacheFile;
        this.cache = new Cache(new File(cacheDirectory),cacheSize);
        mCacheOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8,TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .cache(cache)//设置缓存
                .addInterceptor(cacheInterceptor)
                .build();
        return this;
    }
    private class CacheControlInterceptor implements Interceptor {

        private Context context;

        public CacheControlInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            // TODO: 2017/12/16  判断当前网络状态
            boolean isConnected = NetWorkUtils.isNetConnected(context);
            if (isConnected) {
                /** 如果没有缓存，进行get请求获取服务器数据并缓存起来
                 * 如果缓存已经存在：不超过maxAge---->不进行请求,直接返回缓存数据
                 *                    超出了maxAge--->发起请求获取数据更新
                  */
                 CacheControl cacheControl = new CacheControl.Builder().maxAge(cacheTime, TimeUnit.MINUTES)
                        .maxStale(0, TimeUnit.SECONDS).build();
                request = request.newBuilder().cacheControl(cacheControl).build();
                try {
                    Response response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //if request failed. always load in cache
            //网络未连接或网络请求失败 强制使用缓存
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
//            cacheControl = new CacheControl.Builder().maxAge(Integer.MAX_VALUE, TimeUnit.SECONDS).maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();
//            request = builder.cacheControl(cacheControl).build();
            return chain.proceed(request);
        }
    }

    /**
     * 同步访问网络
     *
     * @param request
     * @return
     */
    public Response execute(Request request,boolean cache) throws Exception {
        if(cache){
            return mCacheOkHttpClient.newCall(request).execute();
        }else{
            return mDefaultOkHttpClient.newCall(request).execute();
        }
    }
    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public  void enqueue(Request request,boolean cache, Callback responseCallback) {
        if (cache) {
            mCacheOkHttpClient.newCall(request).enqueue(responseCallback);
        }else{
            mDefaultOkHttpClient.newCall(request).enqueue(responseCallback);
        }
    }
    /**
     * 同步访问网络
     *
     * @param request
     * @return
     */
    public Response execute(Request request) throws Exception {

        return mDefaultOkHttpClient.newCall(request).execute();
    }
    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public  void enqueue(Request request, Callback responseCallback) {

        mDefaultOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }
}

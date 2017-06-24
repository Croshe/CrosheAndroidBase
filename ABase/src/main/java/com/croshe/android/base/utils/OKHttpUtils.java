package com.croshe.android.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.FileNameMap;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * http请求工具类
 */
public class OKHttpUtils {

    public static String TAG = "OKHttp";
    private static final Map<String, String> headers = new HashMap<>();

    public static MediaType JSONMediaType = MediaType.parse("application/json;charset=utf-8");


    /**
     * 签名的key
     */
    public static String SIGNKEY = null;

    /**
     * 用来配置全局默认的提参数
     */
    private static final Map<String, Object> finalParams = new HashMap<>();

    /**
     * 添加全局头部
     *
     * @param key
     * @param value
     */
    public static void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 移除全局头部
     *
     * @param key
     */
    public static void removeHeader(String key) {
        headers.remove(key);
    }


    /**
     * 添加全局参数
     *
     * @param key
     * @param value
     */
    public static void addFinalParams(String key, Object value) {
        finalParams.put(key, value);
    }


    /**
     * 移除全局参数
     *
     * @param key
     */
    public static void removeFinalParams(String key) {
        finalParams.remove(key);
    }


    private static OKHttpUtils okHttpUtils;

    /**
     * 可设置缓存
     *
     * @param context
     * @return
     */
    public static OKHttpUtils getInstance(Context context) {
        if (okHttpUtils == null) {
            okHttpUtils = new OKHttpUtils();
        }
        if (okHttpUtils.memoryDataInfo == null) {
            okHttpUtils.memoryDataInfo = context.getSharedPreferences("httpLocalData", 0);
        }
        return okHttpUtils;
    }

    /**
     * 不可设置缓存
     *
     * @return
     */
    public static OKHttpUtils getInstance() {
        if (okHttpUtils == null) {
            okHttpUtils = new OKHttpUtils();
        }
        return okHttpUtils;
    }


    private Handler mHandler;
    private OkHttpClient mOkHttpClient;
    public SharedPreferences memoryDataInfo;

    private OKHttpUtils() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.MINUTES)
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url, cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url);
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();
        }
        return mOkHttpClient;
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, false, false, false, false, true, null);
    }

    /**
     * 执行post 异步请求json格式
     *
     * @param url
     * @param params
     */
    public void postJSON(final String url, final Map<String, Object> params, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, false, false, false, false, true, JSONMediaType);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback, boolean isCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, false, false, false, true, null);
    }

    /**
     * 执行post 异步请求json格式
     *
     * @param url
     * @param params
     */
    public void postJSON(final String url, final Map<String, Object> params, final HttpCallBack callback, boolean isCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, false, false, false, true, JSONMediaType);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback,
                     boolean isCache, final boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, isDoRefresh, false, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback,
                     boolean isCache, final boolean isDoRefresh, final boolean isRefreshCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, isDoRefresh, false, false, isRefreshCache, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void post(final String url, final AsyncMakeParams asyncMakeParams, final HttpCallBack callback) {
        doRequest(url, asyncMakeParams, callback, false, false, false, false, true, null);
    }


    /**
     * 执行post 异步请求 json格式
     *
     * @param url
     */
    public void postJSON(final String url, final AsyncMakeParams asyncMakeParams, final HttpCallBack callback) {
        doRequest(url, asyncMakeParams, callback, false, false, false, false, true, JSONMediaType);
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void postJSON(final String url, final Map<String, Object> params, final HttpCallBack callback, boolean isCache, final boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, isDoRefresh, false, false, true, JSONMediaType);
    }


    /**
     * 执行get 异步请求
     *
     * @param url
     * @param params 自动转换成get参数拼接模式
     */
    public void get(final String url, final Map<String, Object> params, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, false, false, true, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void get(final String url, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return null;
            }
        }, callback, false, false, true, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void get(final String url, final HttpCallBack callback, boolean isSynchronize) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return null;
            }
        }, callback, false, false, true, isSynchronize, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void get(final String url, final HttpCallBack callback, boolean isCache, boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return null;
            }
        }, callback, isCache, isDoRefresh, true, false, true, null);
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void doRequest(final String url, final AsyncMakeParams asyncMakeParams, final HttpCallBack callback, final boolean isCache, final boolean isDoRefresh, final boolean isGet, boolean isSynchronize, final boolean isRefreshCache, MediaType mediaType) {
        if (callback == null) {
            throw new RuntimeException("Callback must not null.");
        }
        if (mediaType == null) {//默认为表单格式
            mediaType = MediaType.parse("application/x-www-form-urlencoded");
        }
        if (isCache && memoryDataInfo == null) {
            throw new RuntimeException("You must execute 'OKHttpUtils.getInstance(context)' to get Instance! ");
        }
        final MediaType finalMediaType = mediaType;
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, Object> params = asyncMakeParams.doCreateParams();
                    if (params == null) params = new HashMap<>();

                    for (String key :
                            finalParams.keySet()) {
                        if (!params.containsKey(key)) {
                            params.put(key, finalParams.get(key));
                        }
                    }

                    Request.Builder request = null;
                    final String cacheKey = MD5Encrypt.MD5(url + params.toString());
                    if (memoryDataInfo != null) {
                        final String cacheHtml = memoryDataInfo.getString(cacheKey, null);
                        if (isCache) {
                            if (cacheHtml != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onResult(true, cacheHtml);
                                    }
                                });
                                if (!isRefreshCache) {
                                    return;
                                }
                            }
                        }
                    }

                    params.put("timestamp", System.currentTimeMillis());
                    String sign = sign(params, url);
                    if (StringUtils.isNotEmpty(SIGNKEY)) {
                        Random random = new Random();
                        int crc = Math.max(1, random.nextInt(9));
                        for (int i = 0; i < crc; i++) {
                            sign = MD5Encrypt.MD5(sign + SIGNKEY + i);
                        }
                        params.put("crc", crc);
                    }
                    params.put("sign", sign);

                    if (isGet) {
                        if (params.size() > 0) {
                            if (url.contains("?")) {
                                request = new Request.Builder()
                                        .url(url + "&" + convertToGet(params))
                                        .tag(cacheKey);
                            } else {
                                request = new Request.Builder()
                                        .url(url + "?" + convertToGet(params))
                                        .tag(cacheKey);
                            }

                        } else {
                            request = new Request.Builder()
                                    .url(url)
                                    .tag(cacheKey);
                        }
                    } else {
                        if (params.size() > 0) {
                            if (finalMediaType == JSONMediaType) {
                                request = new Request.Builder()
                                        .url(url)
                                        .tag(cacheKey)
                                        .post(RequestBody.create(JSONMediaType, JSON.toJSONString(params)));
                            } else {
                                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                                FormBody.Builder formEncodingBuilder = new FormBody.Builder();
                                Set<String> keys = params.keySet();

                                boolean haveFile = false;
                                for (String key : keys) {
                                    Object value = params.get(key);
                                    if (value == null) continue;
                                    if (value instanceof File) {
                                        File file = (File) value;
                                        if (file.exists()) {
                                            multipartBuilder.addFormDataPart(key, file.getName(),
                                                    RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                                            haveFile = true;
                                        }
                                    } else if (value instanceof File[]) {
                                        File[] files = (File[]) value;
                                        for (File file : files) {
                                            if (file.exists()) {
                                                multipartBuilder.addFormDataPart(key, file.getName(),
                                                        RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                                                haveFile = true;
                                            }
                                        }
                                    } else {
                                        multipartBuilder.addFormDataPart(key, value.toString());
                                        formEncodingBuilder.add(key, value.toString());
                                    }
                                }

                                if (haveFile) {//如果有附件必须使用 multipart/form-data
                                    request = new Request.Builder()
                                            .url(url)
                                            .tag(cacheKey)
                                            .post(multipartBuilder.build());

                                } else {
                                    request = new Request.Builder()
                                            .url(url)
                                            .tag(cacheKey)
                                            .post(formEncodingBuilder.build());

                                }
                            }
                        } else {
                            request = new Request.Builder()
                                    .url(url)
                                    .tag(cacheKey);
                        }
                    }

                    Set<String> headerKeys = headers.keySet();
                    for (String string : headerKeys) {
                        request.addHeader(string, headers.get(string));
                    }

                    Log.d(TAG, "请求类型：" + (isGet ? "GET" : "POST"));
                    Log.d(TAG, "提交类型：" + finalMediaType.toString());
                    Log.d(TAG, "请求地址：" + url);
                    Log.d(TAG, "请求参数：" + params.toString());
                    Log.d(TAG, "==================================================");


                    final Response response = getOkHttpClient().newCall(request.build()).execute();
                    final String html = response.body().string();

                    response.body().close();
                    if (response.isSuccessful()) {
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                if (isCache) {
                                    if (memoryDataInfo != null) {
                                        final String cacheHtml = memoryDataInfo.getString(cacheKey, null);
                                        if (StringUtils.isEmpty(cacheHtml)) {
                                            callback.onResult(true, html);
                                        }
                                        SharedPreferences.Editor editor = memoryDataInfo.edit();
                                        editor.putString(cacheKey, html);
                                        editor.commit();
                                    }
                                    if (isDoRefresh) {
                                        callback.onResult(true, html);
                                    }
                                } else {
                                    callback.onResult(true, html);
                                }
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                callback.onResult(false, "请求失败，请稍后重试！" + response.message());
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (e instanceof ConnectException) {
                                callback.onResult(false, "请求断开，请检查您的网络连接是否正常！"+ e.getMessage());
                            } else if (e instanceof SocketTimeoutException || e instanceof SocketException) {
                                callback.onResult(false, "请求超时，请稍后重试！"+ e.getMessage());
                            } else {
                                callback.onResult(false, "请求失败，发生未知错误！"+ e.getMessage());
                            }
                        }
                    });
                }
            }
        };
        if (isSynchronize) {
            runnable.run();
        } else {
            new Thread(runnable).start();
        }
    }


    /**
     * 下载文件支持断点下载
     *
     * @param url
     * @param callback
     */
    public void downFile(Context context, String url, final HttpDownFileCallBack callback) {
        downFile(context, url, null, callback);
    }

    /**
     * 下载文件支持断点下载
     *
     * @param url
     * @param callback
     */
    public void downFile(Context context, String url, String localPath, final HttpDownFileCallBack callback) {
        if (localPath == null) {
            localPath = context.getFilesDir() + "/DownFiles/" + MD5Encrypt.MD5(url);
        }
        final File doneFile = new File(localPath);
        if (doneFile.exists()) {//如果存在已经下载完成的文件
            if (callback != null) {
                callback.onDownLoad(doneFile.length(), doneFile.length(), doneFile.getPath());
            }
            return;
        }

        final File tempFile = new File(localPath + "temp");
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }

        Request.Builder builder = new Request.Builder().url(url);
        Set<String> headerKeys = headers.keySet();
        for (String string : headerKeys) {
            builder.addHeader(string, headers.get(string));
        }
        //断点下载
        builder.addHeader("RANGE", "bytes=" + String.valueOf(tempFile.length()) + "-");
        getOkHttpClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof ConnectException) {
                    callback.onDownFail("请求断开，请检查您的网络连接是否正常！");
                } else if (e instanceof SocketTimeoutException || e instanceof SocketException) {
                    callback.onDownFail("请求超时，请稍后重试！");
                } else {
                    callback.onDownFail("请求失败，发生未知错误！");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final long tempLength = tempFile.length();
                final long countLength = response.body().contentLength() + tempLength;

                RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
                randomAccessFile.seek(tempLength);
                InputStream inputStream = response.body().byteStream();
                byte[] buffer = new byte[512];
                int readLength = -1;
                while ((readLength = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, readLength);
                    //下载完毕
                    if (randomAccessFile.length() >= countLength) {
                        tempFile.renameTo(doneFile);
                    }
                    //是否继续下载
                    if (callback != null && !callback.onDownLoad(countLength, randomAccessFile.length(), doneFile.getPath())) {
                        break;
                    }
                }
                randomAccessFile.close();
                inputStream.close();
            }
        });
    }

    /**
     * 删除指定缓存
     */
    public void deleteCache(String url, Map<String, Object> params) {
        if (memoryDataInfo != null) {

            for (String key :
                    finalParams.keySet()) {
                if (!params.containsKey(key)) {
                    params.put(key, finalParams.get(key));
                }
            }

            String cacheKey = MD5Encrypt.MD5(url + params.toString());
            Log.d("STAG", "缓存的Key：" + cacheKey);
            SharedPreferences.Editor editor = memoryDataInfo.edit();
            editor.remove(cacheKey);
            editor.commit();
        }
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 将参数转换成get形式
     *
     * @param params
     * @return
     */
    private String convertToGet(Map<String, Object> params) {
        List<String> list = new ArrayList<String>();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            list.add(key + "=" + params.get(key));
        }
        return StringUtils.join(list, "&");
    }


    /**
     * 签名
     *
     * @param params
     * @return
     */
    public static String sign(Map<String, Object> params, String key) {
        StringBuilder builder = new StringBuilder();
        TreeSet<String> paramKeys = new TreeSet(params.keySet());

        for (String string : paramKeys) {
            if (params.get(string) != null
                    && StringUtils.isNotEmpty(params.get(string).toString())
                    && !(params.get(string) instanceof File)
                    && !(params.get(string) instanceof File[])) {
                builder.append(params.get(string));
            }
        }
        builder.append(key);//密钥 key
        return MD5Encrypt.MD5(builder.toString());
    }


    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }


    public interface HttpCallBack {
        void onResult(boolean success, String response);
    }

    public interface HttpDownFileCallBack {
        boolean onDownLoad(long countLength, long downLength, String localPath);

        void onDownFail(String message);
    }

    /**
     * 异步生成参数,适用于文件处理，在主线程的话，会有卡顿现象
     *
     * @author Janesen
     */
    public interface AsyncMakeParams {
        Map<String, Object> doCreateParams();
    }

}


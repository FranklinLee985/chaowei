package com.classroomsdk.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.params.ClientPNames;

/**
 * date 2018/11/16
 * version
 * describe 网络请求工具类
 *
 * @author hxd
 */
public class HttpHelp {

    private AsyncHttpClient client = new AsyncHttpClient();

    private HttpHelp() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(64), new ThreadPoolExecutor.DiscardOldestPolicy());
        client.setThreadPool(threadPoolExecutor);
    }
    private static class SingletonHolder {
        private static HttpHelp INSTANCE = new HttpHelp();
    }
    public static HttpHelp getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public void post(String url, final ResponseCallBack callBack) {
        post(url, new RequestParams(), callBack);
    }

    /**
     * 普通请求
     *
     * @param url
     * @param params
     * @param callBack
     */
    public void post(String url, RequestParams params, final ResponseCallBack callBack) {
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        sendPost(url, params, callBack);
    }

    /**
     * 重定向请求
     *
     * @param url
     * @param params
     * @param callBack
     */
    public void postRedirects(String url, RequestParams params, final ResponseCallBack callBack) {
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        sendPost(url, params, callBack);
    }

    public void downLoad(String url, String[] allowedContentTypes, final DownLoadCallBack downLoadCallBack) {
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                if (downLoadCallBack != null) {
                    downLoadCallBack.onSuccess(statusCode, bytes);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                if (downLoadCallBack != null) {
                    downLoadCallBack.onFailure(statusCode, bytes, throwable);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (downLoadCallBack != null) {
                    downLoadCallBack.onProgress(bytesWritten, totalSize);
                }
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     * @param file
     * @param downLoadFileCallBack
     */
    public void downLoadFile(String url, File file, final DownLoadFileCallBack downLoadFileCallBack) {
        client.get(url, new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                if (downLoadFileCallBack != null) {
                    downLoadFileCallBack.onFailure(statusCode, file, throwable);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (downLoadFileCallBack != null) {
                    downLoadFileCallBack.onSuccess(statusCode, file);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (downLoadFileCallBack != null) {
                    downLoadFileCallBack.onProgress(bytesWritten, totalSize);
                }
            }
        });

    }




    /**
     * 执行请求
     *
     * @param url
     * @param params
     * @param callBack
     */
    private void sendPost(String url, RequestParams params, final ResponseCallBack callBack) {
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (callBack != null) {
                    callBack.success(statusCode, response);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (callBack != null) {
                    callBack.failure(statusCode, throwable, errorResponse);
                }
            }
        });
    }
}

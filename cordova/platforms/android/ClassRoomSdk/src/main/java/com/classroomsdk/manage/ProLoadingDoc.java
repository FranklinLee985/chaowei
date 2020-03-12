package com.classroomsdk.manage;

import android.content.Context;
import android.os.Looper;

import com.classroomsdk.Config;
import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.http.IProgress;
import com.classroomsdk.interfaces.IWBStateCallBack;
import com.classroomsdk.utils.FileUtils;
import com.classroomsdk.utils.ZipUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.talkcloud.room.TKNotificationCenter;
import com.talkcloud.room.TKNotificationName;
import com.talkcloud.room.TKRoomManagerImpl;
import com.talkcloud.utils.AsyncHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * 预加载单例类
 * Created by Mrleeys
 * on 2019/5/21
 */
public class ProLoadingDoc {

    public volatile boolean exit = false;
    private static ProLoadingDoc mInstance;

    private DownLoadThread downLoadThread;

    /* 文件预下载和解压所需变量 */
    private static String targetFilePath;           //目标文件保存路径
    private static long fromLength = 0;             //请求前，本地已有的字节大小
    public File mDocfileAbs;  //下载文件缓存路径
    public String docName;   //下载文件名
    /* 文件预下载解压所需变量结束 */
    private Context mContext;
    //单独使用异步 不使用HttpHelp
    private AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    public RequestHandle getRequestHandle = null;  //使用它取消具体某个请求

    public ShareDoc defaultFileDoc;

    private IWBStateCallBack callBack;

    private boolean isGetFileList = false;

    public static ProLoadingDoc getInstance() {
        if (mInstance == null) {
            synchronized (ProLoadingDoc.class) {
                if (mInstance == null) {
                    mInstance = new ProLoadingDoc();
                }
            }
        }
        return mInstance;
    }

    public void resetInstance() {
        callBack = null;
        if (downLoadThread != null) {
            downLoadThread.stopRun();
            downLoadThread.clean();
            downLoadThread = null;
        }
        mInstance = null;
    }

    /**
     * 开始下载
     *
     * @param context
     */
    public void startDownload(Context context) {
        this.defaultFileDoc = WhiteBoradManager.getInstance().getDefaultFileDoc();
        this.mContext = context;
        if (defaultFileDoc != null && defaultFileDoc.getPreloadingzip() != null && !"".equals(defaultFileDoc.getPreloadingzip())) {
            exit = false;
            if (callBack != null) {
                callBack.hideDownload(true);
            }
            downLoadThread = new DownLoadThread(defaultFileDoc);
            downLoadThread.start();
        } else {
            postTksdk();
        }
    }

    //跳过或预加载成功，通知sdk发送数据
    public void postTksdk() {
        getFileList();
    }

    //设置界面监听
    public void setWBCallBack(IWBStateCallBack wbCallBack) {
        this.callBack = wbCallBack;

    }

    class DownLoadThread extends Thread {


        private ShareDoc defaultFileDoc;
        private Context mContext;

        public DownLoadThread(ShareDoc defaultFileDoc) {
            this.defaultFileDoc = defaultFileDoc;
        }

        public void clean() {
            cancelRequest();
        }

        public void stopRun() {
            exit = true;
        }

        @Override
        public void run() {
            //先去请求网速cdn 获取最近的dns
            Looper.prepare();
//            httpDns(defaultFileDoc);
            downloadDefaultFileDoc(defaultFileDoc, null);
            Looper.loop();
            //下载和解压会阻塞ui线程，异步线程操作
            //downloadDefaultFileDoc(defaultFileDoc);
        }
    }

    public void httpDns(final ShareDoc defaultFileDoc) {

        //网宿请求
        client.get(Config.url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (callBack != null) {
                    callBack.onDownloadProgress(0, 1);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    if (response != null) {
                        try {
                            String msg = response.optString("msg");
                            String retConde = response.optString("retCode");
                            JSONObject data = response.optJSONObject("data");
                            JSONObject talkcloud = data.optJSONObject("www.chinanetcenter.com");
                            int ttl = talkcloud.optInt("ttl");
                            JSONArray ips = talkcloud.optJSONArray("ips");
                            if (ips.length() > 0) {
                                String ip = ips.getString(0);
                                if (ip != null) {
                                    downloadDefaultFileDoc(defaultFileDoc, ip);
                                } else {
                                    downloadDefaultFileDoc(defaultFileDoc, null);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            downloadDefaultFileDoc(defaultFileDoc, null);
                        }

                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //请求失败 超时各种原因等 都走默认下载
                downloadDefaultFileDoc(defaultFileDoc, null);
            }
        });
    }

    /**
     * 下载默认文档到本地
     *
     * @param defaultFileDoc
     */
    private void downloadDefaultFileDoc(ShareDoc defaultFileDoc, String ip) {
        //如果默认文件是动态ppt或者H5课件 去下载
        if (defaultFileDoc != null && (defaultFileDoc.isDynamicPPT() || defaultFileDoc.isH5Docment())) {

            //创建文件夹 位置在 /mnt/sdcard/android/data/包名/files/DocCaches
            mDocfileAbs = mContext.getExternalFilesDir("DocCaches");
            if (mDocfileAbs == null) {
                mDocfileAbs = mContext.getFilesDir();
            }
        }
        //下载URL
        final String url = defaultFileDoc.getPreloadingzip();
        //下载文件名 默认全是zip文件 docname 后期从下载文件中截取
        docName = "test.zip";
        if (url.startsWith("http://") || url.startsWith("https://")) {
            String[] spliturl = url.split("/");
            if (spliturl.length > 0) {
                if (spliturl[spliturl.length - 1].endsWith(".zip")) {
                    String[] splitName = spliturl[spliturl.length - 1].split("\\.");
                    docName = splitName[0] + "-" + defaultFileDoc.getFileid();
                }
            }
        }


        //下载后地址
        final File mDocfile = new File(mDocfileAbs, docName + ".zip");
        /* 将原始源URL转为Titan加速URL */
        //        String p2PUrl = BusinessManager.getInstance().getP2PUrl("TalkCloud", url, "download");
        //取消请求
        cancelGet();
        //保存当前路径
        targetFilePath = mDocfile.getAbsolutePath();
        //获取文件大小
        fromLength = 0;
        if (mDocfile.exists()) {
            fromLength = mDocfile.length();
        }

        //设置请求头
        client.addHeader("Range", "bytes=" + fromLength + "-");
        if (ip != null) {
            client.addHeader("Host", ip);
        }
        //开启下载
        getRequestHandle = client.get(mContext, url, new FileAsyncHttpResponseHandler(mDocfile, true) {

            @Override
            public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                if (response.getStatusLine().getStatusCode() == 416) {
                    if (callBack != null) {
                        callBack.onDownloadProgress(100, 1);
                    }
                    ZipProcess();
                    cancelGet();
                } else if (response.getStatusLine().getStatusCode() == 403) {
                    cancelGet();
                    if (callBack != null) {
                        callBack.hideDownload(false);
                    }
                    postTksdk();
                }
            }

            @Override
            public void onStart() {
                if (callBack != null) {
                    callBack.onDownloadProgress(0, 1);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                //下载失败 传递默认课件给H5
                if (callBack != null) {
                    callBack.hideDownload(false);
                }
                if (statusCode != 416) {
                    postTksdk();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (statusCode == 206) {
                    ZipProcess();
                } else {
                    if (callBack != null) {
                        callBack.hideDownload(false);
                    }
                    postTksdk();
                }
            }

            int num = -1;

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                int progres = (int) (totalSize > 0 ? (bytesWritten + fromLength) * 1.0 / (totalSize + fromLength) * 100 : -1);
                if (num == progres) {
                    return;
                }
                num = progres;
                if (callBack != null) {
                    callBack.onDownloadProgress(progres, 1);
                }
            }
        });
        getRequestHandle.setTag("downdoc");

    }


    /**
     * Zip处理
     */
    private void ZipProcess() {
        try {
            //创建解压文件夹 位置在 /mnt/sdcard/android/data/包名/files/DecompressionFileDoc
            File mDecompressionFile = mContext.getExternalFilesDir("DecompressionFileDoc");
            if (mDecompressionFile == null) {
                mDecompressionFile = mContext.getFilesDir();
            }
            //解压 文件地址  解压后地址
            final File finalMDecompressionFile = mDecompressionFile;
            String[] files = finalMDecompressionFile.list();
            if (files.length != 0) {
                boolean isFlage = false;
                for (String file1 : files) {
                    //如果两个文件相等 再不去下载
                    if (file1.equals(docName + "-success")) {
                        isFlage = true;
                        if (callBack != null) {
                            callBack.hideDownload(false);
                        }
                        setDecompressionFileDoc(finalMDecompressionFile);
                        break;
                    }
                }
                if (isFlage) {
                    return;
                }
            }
            final File file2 = new File(finalMDecompressionFile + "/" + docName);
            if (file2.exists()) {
                file2.mkdir();
            }
            ZipUtils.UnZipFolder(targetFilePath, file2.getAbsolutePath(), new IProgress() {
                //解压进度
                int num = -1;

                @Override
                public void onProgress(int progress) {
                    if (num == progress) {
                        return;
                    }
                    num = progress;
                    if (callBack != null) {
                        callBack.onDownloadProgress(progress, 2);
                    }
                }

                //解压失败
                @Override
                public void onError(String msg) {
                    if (callBack != null) {
                        callBack.hideDownload(false);
                    }

                    file2.renameTo(new File(finalMDecompressionFile + "/" + docName + "-error"));
                    deleteDir(finalMDecompressionFile, docName + "success");
                    postTksdk();
                }

                //成功
                @Override
                public void onDone() {
                    //解压成功对文件夹做个标识
                    file2.renameTo(new File(finalMDecompressionFile + "/" + docName + "-success"));
                    setDecompressionFileDoc(finalMDecompressionFile);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消下载请求
     */
    public void cancelGet() {
        if (downLoadThread != null && downLoadThread.isAlive()) {
            downLoadThread.clean();
        }
    }

    /**
     * 设置默认文件
     *
     * @param finalMDecompressionFile 解压成功后文件家绝对路径
     */
    public void setDecompressionFileDoc(File finalMDecompressionFile) {
        //解压成功后 获取默认课件
        //默认课件参数设置解压成功后文件路径 + "文件夹中 index.html" 绝对路径
        ShareDoc shareDoc = WhiteBoradManager.getInstance().getDefaultFileDoc();
        //下载后需判断默认课件是否有变化
        if (shareDoc != null && shareDoc.getFileid() == defaultFileDoc.getFileid()) {
            //设置本地文件地址
            if (shareDoc.isDynamicPPT()) {
                shareDoc.setBaseurl("file://" + finalMDecompressionFile.getAbsolutePath() + "/" + docName + "-success" + "/" + "newppt.html");
            }
            if (shareDoc.isH5Docment()) {
                shareDoc.setBaseurl("file://" + finalMDecompressionFile.getAbsolutePath() + "/" + docName + "-success" + "/" + "index.html");
            }
            //设置默认本地文件，在每次给H5发送showpage时，判断当前课件是否是默认课件，如果是默认课件，都要设置baseurl
            WhiteBoradManager.getInstance().DefaultBaseurl = shareDoc.getBaseurl();
        }
        WhiteBoradManager.getInstance().setDefaultFileDoc(shareDoc);
        WBSession.getInstance().addFileList();
        //添加缓存赤
        postTksdk();
        //删除下载目录文件
        deleteFile(mDocfileAbs, docName + ".zip");
        //删除解压目录下文件及目录
        deleteDir(finalMDecompressionFile, docName + "-success");
    }

    /**
     * 删除文件夹下不同与name的所有文件
     *
     * @param file
     * @param name
     */
    public void deleteFile(File file, String name) {
        //判断是否是目录
        if (FileUtils.isDir(file.getAbsolutePath())) {
            String[] files = file.list();
            if (files.length > 1) {
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].equals(name)) {
                        FileUtils.deleteFile(file.getAbsolutePath() + "/" + files[i]);
                    }
                }
            }
        }
    }

    /**
     * 删除目录下所有不同与docName文件目录
     *
     * @param file
     * @param docName
     */
    private void deleteDir(File file, String docName) {
        if (FileUtils.isDir(file.getAbsolutePath())) {
            String[] files = file.list();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].equals(docName)) {
                        if (FileUtils.deleteFilesInDir(file.getAbsolutePath() + "/" + files[i])) {
                            File file1 = FileUtils.getFileByPath(file.getAbsolutePath() + "/" + files[i]);
                            if (file1 != null) {
                                file1.delete();
                            }
                        }
                    }
                }
            }
        }
    }

    public void cancel() {
        //关闭下载
        cancelGet();
        //关闭解压
        if (downLoadThread != null && downLoadThread.isAlive()) {
            downLoadThread.stopRun();
        }

        mInstance = null;
    }

    public void cancelRequest() {
        if (getRequestHandle != null) {
            client.cancelRequestsByTAG("downdoc", true);
            //getRequestHandle.cancel(true);
            getRequestHandle = null;
        }
    }

    //预加载结束需重新获取文件列表
    private void getFileList() {
        if (isGetFileList) {
            return;
        }
        isGetFileList = true;
        String url = TKRoomManagerImpl.getInstance().get_port() == 443 ? "https://" : "http://" + TKRoomManagerImpl.getInstance().get_host() + ":" + TKRoomManagerImpl.getInstance().get_port() + "/ClientAPI/getroomfile";

        AsyncHttpURLConnection httpConnection = new AsyncHttpURLConnection("POST", url, "serial=" + WBSession.getInstance().serial, new AsyncHttpURLConnection.AsyncHttpEvents() {
            public void onHttpError(final String errorMessage) {
                WBSession.getInstance().addFileList();
                TKNotificationCenter.getInstance().postNotificationName(TKNotificationName.onGetReadyEnterRoom);
            }

            public void onHttpComplete(final String response) {
                JSONObject response_json = null;

                try {
                    response_json = new JSONObject(response);
                    int nRet = response_json.getInt("result");
                    Object roomfile = null;
                    if (nRet == 0 && response_json.has("roomfile")) {
                        roomfile = response_json.get("roomfile");
                    }
                    TKNotificationCenter.getInstance().postNotificationName(1003, new Object[]{roomfile});
                } catch (JSONException var4) {
                    var4.printStackTrace();
                }
                WBSession.getInstance().addFileList();
                TKNotificationCenter.getInstance().postNotificationName(TKNotificationName.onGetReadyEnterRoom);
            }
        });
        httpConnection.send();
    }
}

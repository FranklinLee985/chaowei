package com.classroomsdk.utils;

import android.content.Context;
import android.os.Environment;

import com.classroomsdk.bean.CaptureImg;
import com.classroomsdk.bean.ShowPageBean;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.SyncHttpClient;
import com.talkcloud.utils.DispatchQueue;

import java.io.File;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class FileDownLoad {

    public FileDownLoadDelegate delegate;
    private final static SyncHttpClient client = new SyncHttpClient(true, 80, 443);
    public static PersistentCookieStore myCookieStore = null;
    public static int externalCacheNotAvailableState = 0;
    private RequestHandle request = null;
    private static volatile FileDownLoad Instance = null;

    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");

    public interface FileDownLoadDelegate {
        void didFinishLoadingFile(String key, File file, ShowPageBean showPageBean);

        void didStartDownloadFile(CaptureImg captureImg, File file);

        void didFinishLoadingFile(CaptureImg captureImg, File file);

        void didFailedLoadingFile();

        void didFailedLoadingFile(String key);

        void didChangedLoadProgress(float progress);
    }

    public static FileDownLoad getInstance() {
        FileDownLoad localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileDownLoad.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileDownLoad();
                }
            }
        }
        return localInstance;
    }

    public void resetInstance() {
        Instance = null;
    }

    public void start(Map<String, String> mPath, Context context, ShowPageBean mCurrentShareDoc, String address) {
        if (myCookieStore != null)
            client.setCookieStore(myCookieStore);

        //图片地址
        String file = mCurrentShareDoc.getFiledata().getSwfpath();

        String key = mCurrentShareDoc.getFiledata().getFileid() + "-" + mCurrentShareDoc.getFiledata().getCurrpage();
        String[] files = file.split("\\.");
        if (files.length != 2) return;
        String phonefile = files[0] + "-" + mCurrentShareDoc.getFiledata().getCurrpage() + "." + files[1];

        File cacheFileFinal = new File(getCacheDir(context), phonefile);

        File dirFile = context.getExternalCacheDir();
        if (dirFile == null) {
            dirFile = context.getCacheDir();
        }
        if (dirFile != null) {
            String strSaveDir = dirFile.getPath() + "/" + "cancleImage";
            File dirFileShare = new File(strSaveDir);
            if (dirFileShare.exists()) {
                dirFileShare.delete();
            }
        }

        if (address != null) {
            startDownloadHTTPRequest(cacheFileFinal, address, key, mCurrentShareDoc);
        }
    }

    /**
     * 截图下载
     *
     * @param context
     * @param mCaptureImg
     * @param address
     */
    public void start(Context context, CaptureImg mCaptureImg, String address) {
        if (myCookieStore != null)
            client.setCookieStore(myCookieStore);
        //文件位置
        File cacheFileFinal = new File(getCacheDir(context), mCaptureImg.getCaptureImgInfo().getSwfpath());

        if (cacheFileFinal.exists()) {
            delegate.didFinishLoadingFile(mCaptureImg, cacheFileFinal);
            return;
        } else {
            delegate.didStartDownloadFile(mCaptureImg, cacheFileFinal);
        }

        File dirFile = context.getExternalCacheDir();
        if (dirFile == null) {
            dirFile = context.getCacheDir();
        }
        if (dirFile != null) {
            String strSaveDir = dirFile.getPath() + "/" + "cancleImage";
            File dirFileShare = new File(strSaveDir);
            if (dirFileShare.exists()) {
                dirFileShare.delete();
            }
        }

        if (address != null) {
            //区分是正常文档下载还是截图下载
            startDownloadHTTPRequest(cacheFileFinal, address, mCaptureImg);
        }
    }

    public class AsyncHandler extends FileAsyncHttpResponseHandler {

        public AsyncHandler(File name) {
            super(name);
        }

        public RequestHandle innerRequest = null;
        //正常下载文档key
        public String key = null;
        //客户端截图下载
        public CaptureImg captureImg = null;
        public ShowPageBean mCurrentShareDoc = null;

        public void setKey(String key) {
            this.key = key;
        }

        public void setCaptureImg(CaptureImg mcaptureImg) {
            this.captureImg = mcaptureImg;
        }

        public void setCurrentShareDoc(ShowPageBean mCurrentShareDoc) {
            this.mCurrentShareDoc = mCurrentShareDoc;
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
            stageQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    //cleanup();
                    if (key != null) {
                        delegate.didFailedLoadingFile(key);
                    }
                    if (captureImg != null) {
                        delegate.didFailedLoadingFile();
                    }
                }
            });
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            final float totalBytesCount = totalSize;
            final float progress = bytesWritten;
            stageQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (totalBytesCount > 0) {
                        delegate.didChangedLoadProgress(Math.min(1.0f, (float) (progress) / (float) totalBytesCount));
                    }
                }
            });
        }

        @Override
        public void onSuccess(int i, Header[] headers, File file) {
            final File jpgFilePath = file;
            try {
                stageQueue.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //如果key不为空 证明是非截图文档
                            if (key != null) {
                                delegate.didFinishLoadingFile(key, jpgFilePath, mCurrentShareDoc);
                            }
                            //截图图片返回截图实体类
                            if (captureImg != null) {
                                delegate.didFinishLoadingFile(captureImg, jpgFilePath);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                stageQueue.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        //cleanup();
                        delegate.didFailedLoadingFile(key);
                    }
                });
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

    }

    /**
     * 下载前参数设置
     *
     * @param cacheFileFinal
     * @param httpUrl
     */
    private void startDownloadHTTPRequest(final File cacheFileFinal, final String httpUrl, final String key, final ShowPageBean mCurrentShareDoc) {
        if (request != null) {
            request.cancel(false);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AsyncHandler handler = new AsyncHandler(cacheFileFinal);
                handler.setKey(key);
                handler.setCurrentShareDoc(mCurrentShareDoc);
                request = client.get(httpUrl, handler);
                handler.innerRequest = request;
            }
        }).start();
    }

    /**
     * 截图下载前参数设置
     *
     * @param cacheFileFinal
     * @param httpUrl
     */
    private void startDownloadHTTPRequest(final File cacheFileFinal, final String httpUrl, final CaptureImg captureImg) {
        if (request != null) {
            request.cancel(false);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AsyncHandler handler = new AsyncHandler(cacheFileFinal);
                handler.setCaptureImg(captureImg);
                request = client.get(httpUrl, handler);
                handler.innerRequest = request;
            }
        }).start();
    }

    /**
     * ����
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        if (externalCacheNotAvailableState == 1 || externalCacheNotAvailableState == 0 && Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            externalCacheNotAvailableState = 1;
            return context.getExternalCacheDir();
        }
        externalCacheNotAvailableState = 2;
        return context.getCacheDir();
    }
}

package com.classroomsdk.utils;
import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.classroomsdk.manage.WhiteBoradManager;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

public class UploadFile {

    private onUploadListener mOnUploadListener;
    public UpLoadFileDelegate delegate;
    public int state = 0;
    private String httpUrl;
    private String remoteFilename;
    private String uploadingFilePath;
    private String uploadserial;
    private String uploaduserid;
    private String uploadsender;
    RequestParams fileParams = null;
    private int count = 0;
    private int mType; // 1 --> 上传课件  0 --> 上传聊天图片

    public static interface UpLoadFileDelegate {
        public abstract void didFinishUploadingFile(UploadFile operation, String fileName, String result);

        public abstract void didFailedUploadingFile(UploadFile operation, int code);

        public abstract void didChangedUploadProgress(UploadFile operation, float progress);
    }

    public interface onUploadListener {
        void onUploadSuccess(JSONObject object);
    }

    public void setOnUploadListener(onUploadListener listener) {
        mOnUploadListener = listener;
    }

    public void UploadOperation(String url) {
        httpUrl = url;
    }


    public void start() {
        if (state != 0) {
            return;
        }
        state = 1;
        if (httpUrl != null) {
            startUploadHTTPRequest();
        }
    }

    public void cancel() {

    }

    private void startUploadHTTPRequest() {
        if (state != 1) {
            return;
        }
        try {
            HttpHelp.getInstance().postRedirects(httpUrl, fileParams, new ResponseCallBack() {
                @Override
                public void success(int statusCode, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                        if (nRet == 0) {
                            if (mType == 1) {
                                WhiteBoradManager.getInstance().onUploadPhotos(response);
                            } else if (mType == 0) {
                                if (mOnUploadListener != null) {
                                    mOnUploadListener.onUploadSuccess(response);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                }
            });
        } catch (Exception e) {
            delegate.didFailedUploadingFile(UploadFile.this, 999);
            return;
        }
    }

    public void packageFile(String path, String strserial, String peerid, String username, int writedb) {
        mType = writedb;
        count++;
        uploadingFilePath = path;
        this.remoteFilename = path.substring(path.lastIndexOf("/") + 1);
        RequestParams params = new RequestParams();
        try {
            uploadserial = strserial;
            uploaduserid = peerid;
            uploadsender = username;
            String fileOldName = path.substring(path.lastIndexOf("/") + 1);
            String fileType = path.substring(path.lastIndexOf(".") + 1);

            File file = new File(path);

            params.put("filedata", file);
            params.put("serial", uploadserial);
            params.put("userid", uploaduserid);
            params.put("sender", uploadsender);
            params.put("conversion", "1");
            params.put("isconversiondone", "0");
            params.put("fileoldname", fileOldName);
            params.put("filename", path);
            params.put("filetype", fileType);
            params.put("alluser", "1");
            params.put("writedb", writedb);
            params.put("filenewname", username + "_" + "mobile" + "_" + fileOldName);

            this.fileParams = params;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPath() {
        return uploadingFilePath;
    }

    public String getserial() {
        return uploadserial;
    }


    public String getuserid() {
        return uploaduserid;
    }


    public String getsender() {
        return uploadsender;
    }

    public int getCount() {
        return count;
    }
}

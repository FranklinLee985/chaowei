package com.eduhdsdk.tools;

import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.eduhdsdk.interfaces.TranslateCallback;
import com.eduhdsdk.room.RoomControler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/6/5.
 */

public class Translate {

    static private Translate mInstance = null;
    //    private static final String APP_ID = "20170605000052251";//自己注册的
    private static final String APP_ID = "20180130000119815";//斌哥注册的

    //    private static final String SECURITY_KEY = "sYlf3rTdnEGTOKr1FuT1";
    private static final String SECURITY_KEY = "MeLC5NI37txuT_wtTd0B";
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private TranslateCallback callback;

    public void setCallback(TranslateCallback callback) {
        this.callback = callback;
    }

    static public Translate getInstance() {
        synchronized (Translate.class) {
            if (mInstance == null) {
                mInstance = new Translate();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    public void translate(final int index, String query) {
        RequestParams params = new RequestParams();
        params.put("q", query);
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(query);
        String result = "";

        //是否是中日翻译
        if (RoomControler.isChineseJapaneseTranslation()) {
            if (matcher.find()) {
                params.put("from", "zh");
                params.put("to", "jp");
            } else {
                params.put("from", "jp");
                params.put("to", "zh");
            }
        } else {
            if (matcher.find()) {
                params.put("from", "zh");
                params.put("to", "en");
            } else {
                params.put("from", "en");
                params.put("to", "zh");
            }
        }
        params.put("appid", APP_ID);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = APP_ID + query + salt + SECURITY_KEY; // 加密前的原文
        params.put("sign", MD5.md5(src));

        HttpHelp.getInstance().post(TRANS_API_HOST, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    JSONArray arr = response.optJSONArray("trans_result");
                    String result = "";
                    if (arr.length() > 0) {
                        for (int x = 0; x < arr.length(); x++) {
                            JSONObject resultObj = arr.optJSONObject(x);
                            String src = resultObj.optString("src");
                            result = result + " " + resultObj.optString("dst");
                        }
                    }
                    if (callback != null) {
                        callback.onResult(index, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }
}

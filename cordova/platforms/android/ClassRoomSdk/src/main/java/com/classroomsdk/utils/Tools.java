package com.classroomsdk.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/17.
 */

public class Tools {

    public static ProgressDialog progressDialog;

    public static boolean isTure(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof Number) {
            Number n = (Number) o;
            return n.longValue() != 0;
        } else {
            return false;
        }
    }

    public static long toLong(Object o) {
        long temLong = 0;
        if (o instanceof Integer) {
            int tem = (Integer) o;
            temLong = tem;
        } else if (o instanceof String) {
            String temstr = (String) o;
            temLong = Long.valueOf(temstr);
        } else {
            temLong = (Long) o;
        }
        return temLong;
    }

    public static void ShowProgressDialog(final Activity activity, final String message) {
        if (!activity.isFinishing()) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(activity, android.R.style.Theme_Holo_Light_Dialog);
            }
            if (message != null) {
                progressDialog.setMessage(message);
            }
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.show();
        }
    }

    public static void HideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static boolean isMp4(String filename) {
        if (TextUtils.isEmpty(filename) || "".equals(filename)) {
            return false;
        } else {
            if (filename.toLowerCase().endsWith("mp4") || filename.toLowerCase().endsWith("webm")) {
                return true;
            } else {
                return false;
            }
        }
    }

    /***
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static HashMap<String, Object> toHashMap(String str) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        if (TextUtils.isEmpty(str)) {
            return null;
        } else {
            // 将json字符串转换成jsonObject
            try {
                JSONObject jsonObject = new JSONObject(str);
                Iterator it = jsonObject.keys();
                // 遍历jsonObject数据，添加到Map对象
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    Object value = jsonObject.get(key);
                    data.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /***
     *
     * @return 回去系统语言
     */
    public static String getSystemLanguage() {
        String lan = null;
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        if (locale.equals(Locale.TAIWAN)) {
            lan = "tw";
        } else if (locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            lan = "ch";
        } else if (locale.equals(Locale.ENGLISH) || locale.toString().equals("en_US".toString())) {
            lan = "en";
        }
        if (TextUtils.isEmpty(lan)) {
            if (locale.toString().endsWith("#Hant")) {
                lan = "tw";
            }
            if (locale.toString().endsWith("#Hans")) {
                lan = "ch";
            }
        }
        return lan;
    }

    /**
     * Map转json
     *
     * @param map
     * @return
     */
    public static JSONObject mapToJson(Map<String, Object> map) {
        JSONObject jsb = new JSONObject(map);
        return jsb;
    }

    /*
    * 毫秒转化
    */
    public static String formatTime(long ms) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

//        return strMinute + " 分钟 " + strSecond + " 秒";
        return strHour + ":" + strMinute + ":" + strSecond;
    }
}

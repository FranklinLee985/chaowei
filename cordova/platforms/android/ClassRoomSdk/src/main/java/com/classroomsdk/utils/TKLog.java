package com.classroomsdk.utils;

import android.util.Log;

/**
 * Created by zhe on 2019-05-28.
 * 日志工具类
 * <p>
 * 通过enableLog设置是否打印日志，默认不打印
 * <p>
 * 通过setGlobalTag设置全局tag，有全局tag的情况下，使用全局tag, 传入的tag将不生效
 * <p>
 * 调用print，直接打印error类型日志
 */
public class TKLog {

    // 是否打印日志，默认不打印
    private static boolean enableLog = false;

    // 全局tag，有全局tag的情况下，使用全局tag, 传入的tag将不生效
    private static String globalTag = "";

    /**
     * 是否打印日志
     *
     * @param enableLog true-> 打印日志 false-> 不打印日志
     */
    public static void enableLog(boolean enableLog) {
        TKLog.enableLog = enableLog;
    }

    /**
     * 设置全局tag
     *
     * @param tag 全局tag
     */
    public static void setGlobalTag(String tag) {
        globalTag = tag;
    }

    public static void v(String msg) {
        if (enableLog) {
            print(Log.VERBOSE, "", msg);
        }
    }

    public static void v(String tag, String msg) {
        if (enableLog) {
            print(Log.VERBOSE, tag, msg);
        }
    }

    public static void d(String msg) {
        if (enableLog) {
            print(Log.DEBUG, "", msg);
        }
    }

    public static void d(String tag, String msg) {
        if (enableLog) {
            print(Log.DEBUG, tag, msg);
        }
    }

    public static void i(String msg) {
        if (enableLog) {
            print(Log.INFO, "", msg);
        }
    }

    public static void i(String tag, String msg) {
        if (enableLog) {
            print(Log.INFO, tag, msg);
        }
    }

    public static void w(String msg) {
        if (enableLog) {
            print(Log.WARN, "", msg);
        }
    }

    public static void w(String tag, String msg) {
        if (enableLog) {
            print(Log.WARN, tag, msg);
        }
    }

    public static void e(String msg) {
        if (enableLog) {
            print(Log.ERROR, "", msg);
        }
    }

    public static void e(String tag, String msg) {
        if (enableLog) {
            print(Log.ERROR, tag, msg);
        }
    }

    public static void print(String tag, String msg) {
        if (enableLog) {
            print(Log.ERROR, tag, msg);
        }
    }

    public static void print(String msg) {
        if (enableLog) {
            print(Log.ERROR, "", msg);
        }
    }

    /**
     * 打印日志
     * <p>
     * LoginActivity: hello   [ Thread:main, at com.talkplus.LoginActivity.onCreate(LoginActivity.java:91) ]
     */
    private static void print(int logType, String tag, String msg) {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        for (StackTraceElement st : sts) {
            if (st == null) {
                return;
            }
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(TKLog.class.getName())) {
                continue;
            }
            String printTag = (globalTag == null || "".equals(globalTag))
                    ? ((tag == null || "".equals(tag)) ? getSimpleName(st.getFileName()) : tag)
                    : globalTag;
            String printMsg = st.getMethodName() + ": " + msg + "   [ Thread:" + Thread.currentThread().getName()
                    + ", at " + st.getClassName() + "." + st.getMethodName()
                    + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
            switch (logType) {
                case Log.VERBOSE:
                    Log.v(printTag, printMsg);
                    break;
                case Log.DEBUG:
                    Log.d(printTag, printMsg);
                    break;
                case Log.INFO:
                    Log.i(printTag, printMsg);
                    break;
                case Log.WARN:
                    Log.w(printTag, printMsg);
                    break;
                case Log.ERROR:
                    Log.e(printTag, printMsg);
                    break;
                default:
                    Log.e(printTag, printMsg);
                    break;
            }
            return;
        }
    }

    /**
     * 通过文件名获取simplename
     *
     * @param fileName filename
     * @return simplename
     * <p>
     * MainActivity.java --> MainActivity
     */
    private static String getSimpleName(String fileName) {
        if (fileName == null || "".equals(fileName)) {
            return "";
        }
        String[] strings = fileName.split("\\.");
        if (strings.length < 1) {
            return "";
        }
        return strings[0];
    }

}

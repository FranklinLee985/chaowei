package com.eduhdsdk.room;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/11/16/016.
 */

public class RoomVariable {

    public static String finalnickname;
    public static String clientType;

    public static int Kickout_ChairmanKickout = 0;
    public static int Kickout_Repeat = 1;

    public static HashMap<String, Object> params = new HashMap<String, Object>();
    public static String path = "";
    public static String param = "";
    public static String domain = "";
    public static boolean mobilenameNotOnList = true;
    public static String servername;
    // 设备名称   手机/PAD/TV
    public static String mobilename = "";
    //用户角色属性   0是老师   2是学生  4是巡课  -1是回放
    public static int userrole = -1;

    // 地址个端口号
    public static String host = "";
    public static int port = 80;
    //登陆密码
    public static String password = "";
    //用户IP
    public static String userid = "";
    //用户昵称
    public static String nickname = "";

    //房间号
    public static String serial;

    static private RoomVariable mInstance = null;

    static public RoomVariable getInstance() {
        synchronized (RoomVariable.class) {
            if (mInstance == null) {
                mInstance = new RoomVariable();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    /***
     *    重置变量
     */
    public void resetRoomVariable() {

        finalnickname = "";
        clientType = "";
        path = "";
        param = "";
        domain = "";
        mobilenameNotOnList = true;
        servername = "";
        mobilename = "";
        userrole = -1;
        host = "";
        port = 80;
        password = "";
        userid = "";
        nickname = "";
        serial = "";
        params.clear();
    }
}

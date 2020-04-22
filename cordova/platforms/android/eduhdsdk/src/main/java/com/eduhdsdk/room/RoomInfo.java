package com.eduhdsdk.room;

import com.classroomsdk.manage.WhiteBoradManager;
import com.eduhdsdk.entity.Trophy;
import com.talkcloud.room.TKRoomManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/15/015.  房间信息
 */

public class RoomInfo {

    static private RoomInfo mInstance = null;

    //视频的宽高比例值
    private int wid_ratio = 4;
    private int hid_ratio = 3;
    //房间最大视频数
    private int maxVideo = -1;
    //房间视频布局
    private int videoSize = 7;

    // 房间类型  0为一对一   非0为一对多
    private int roomType = -1;
    //房间号
    private String serial;
    // 自定义奖杯数据集合
    private List<Trophy> trophyList = new ArrayList<>();
    //自定义单个奖杯铃声的 地址
    private String _MP3Url;
    //房间名称
    private String roomName;
    //房间模板，皮肤信息，皮肤地址
    private String _tplId, _skinId, _skinResource;
    //企业ID
    private String companyid;
    //皮肤颜色值  (purple:为紫色) (black:为黑色)
    private String colourid;
    // 白板自定义底色 色值的索引
    private String whiteboardcolor;
    // 房间结束时间
    private Long endtime;

    /***
     *  51常规   52双师  53纯视频 （一对一）
     * (1-5)视频置顶 6 主讲视频  7 自由视频（一对多）
     */
    //房间默认布局
    private int roomlayout = 1;

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    public int getRoomlayout() {
        return roomlayout;
    }

    public String getWhiteboardcolor() {
        return whiteboardcolor;
    }

    public void setWhiteboardcolor(String whiteboardcolor) {
        this.whiteboardcolor = whiteboardcolor;
    }

    static public RoomInfo getInstance() {
        synchronized (RoomInfo.class) {
            if (mInstance == null) {
                mInstance = new RoomInfo();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    public int getWid_ratio() {
        return wid_ratio;
    }

    public int getHid_ratio() {
        return hid_ratio;
    }

    public String getCompanyid() {
        return companyid;
    }

    /***
     *     获取最大视频数
     * @return
     */
    public int getMaxVideo() {
        return maxVideo;
    }

    /***
     *   获取房间类型    0为一对一   非0为一对多
     * @return
     */
    public int getRoomType() {
        return roomType;
    }

    public String getColourid() {
        return colourid;
    }

    /**
     * 获取房间号
     *
     * @return
     */
    public String getSerial() {
        return serial;
    }

    /***
     *     获取自定义奖杯数据集合
     * @return
     */
    public List<Trophy> getTrophyList() {
        return trophyList;
    }

    /***
     *     获取单个单被地址
     * @return
     */
    public String get_MP3Url() {
        return _MP3Url;
    }

    /***
     *     获取房间名称
     * @return
     */
    public String getRoomName() {
        return roomName;
    }

    /***
     *   房间videoitem布局数
     * @return
     */
    public int getVideoSize() {
        return videoSize;
    }

    /***
     *
     * @return 获取房间模板
     */
    public String get_tplId() {
        return _tplId;
    }

    /***
     *       获取皮肤IP
     * @return
     */
    public String get_skinId() {
        return _skinId;
    }

    /***
     *    皮肤下载地址
     * @return
     */
    public String get_skinResource() {
        return _skinResource;
    }

    /***
     *    获取房间信息
     */
    public void getRoomInformation() {
        JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
        if (jsonRoomInfo != null) {

            if (jsonRoomInfo.has("videoheight") && jsonRoomInfo.has("videowidth")) {
                wid_ratio = jsonRoomInfo.optInt("videowidth");
                hid_ratio = jsonRoomInfo.optInt("videoheight");
            }

            maxVideo = jsonRoomInfo.optInt("maxvideo");
            roomlayout = jsonRoomInfo.optInt("roomlayout");

            String chairmancontrol = jsonRoomInfo.optString("chairmancontrol");
            if (chairmancontrol != null && !chairmancontrol.isEmpty()) {
                RoomControler.chairmanControl = chairmancontrol;
            }

            roomType = jsonRoomInfo.optInt("roomtype");
            serial = jsonRoomInfo.optString("serial");
            WhiteBoradManager.getInstance().setSerial(serial);
            WhiteBoradManager.getInstance().setPeerId(TKRoomManager.getInstance().getMySelf().peerId);
            if (jsonRoomInfo.has("voicefile")) {
                _MP3Url = jsonRoomInfo.optString("voicefile", "");
            }

            if (jsonRoomInfo.has("trophy")) {
                JSONArray trophyArray = jsonRoomInfo.optJSONArray("trophy");

                if (trophyArray != null && RoomControler.isCustomTrophy()) {
                    setDataTrophy(trophyArray);
                }
            }

            if (jsonRoomInfo.has("roomname")) {
                roomName = jsonRoomInfo.optString("roomname");
                roomName = StringEscapeUtils.unescapeHtml4(roomName);
            }

            if (jsonRoomInfo.has("tplId") && jsonRoomInfo.has("skinId") &&
                    jsonRoomInfo.has("skinResource")) {
                _tplId = jsonRoomInfo.optString("tplId");
                _skinId = jsonRoomInfo.optString("skinId");
                _skinResource = jsonRoomInfo.optString("skinResource");
            }

            if (jsonRoomInfo.has("roomname")) {
                roomName = jsonRoomInfo.optString("roomname");
                roomName = StringEscapeUtils.unescapeHtml4(roomName);
            }

            if (jsonRoomInfo.has("whiteboardcolor")) {
                whiteboardcolor = jsonRoomInfo.optString("whiteboardcolor");
            }

            companyid = jsonRoomInfo.optString("companyid");
            colourid = jsonRoomInfo.optString("colourid");

            endtime = jsonRoomInfo.optLong("endtime");

            if (roomType == 0 && !RoomControler.isShowAssistantAV()) {
                if (roomlayout == 52) {   //双师
                    roomlayout = 2;
                } else if (roomlayout == 53) { //纯视频
                    roomlayout = 3;
                } else {
                    roomlayout = 1;
                }
            } else {
                if (roomlayout == 6) {  //主讲视频
                    roomlayout = 2;
                } else if (roomlayout == 7 || roomlayout == 53) { //自由视频
                    roomlayout = 3;
                } else {
                    roomlayout = 1;   // 视频置顶
                }
            }
        }
    }

    public void setDataTrophy(JSONArray trophyArray) {
        trophyList.clear();
        for (int x = 0; x < trophyArray.length(); x++) {
            Trophy trophy = new Trophy();
            try {
                trophy.setCompanyid(trophyArray.getJSONObject(x).optString("companyid"));
                trophy.setTrophyname(trophyArray.getJSONObject(x).optString("trophyname"));
                trophy.setTrophyimg(trophyArray.getJSONObject(x).optString("trophyimg"));
                trophy.setTrophyvoice(trophyArray.getJSONObject(x).optString("trophyvoice"));
                trophy.setTrophyIcon(trophyArray.getJSONObject(x).optString("trophyIcon"));
                trophy.setTrophyeffect(trophyArray.getJSONObject(x).optString("trophyeffect"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            trophyList.add(trophy);
        }
    }

    /***
     *    注销房间信息
     */
    public void cancellRoomInformation() {
        wid_ratio = 4;
        hid_ratio = 3;
        maxVideo = -1;
        roomType = -1;
        serial = null;
        trophyList.clear();
        _MP3Url = null;
        roomName = null;
        _tplId = null;
        _skinId = null;
        _skinResource = null;
        companyid = null;
    }
}

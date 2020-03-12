package com.classroomsdk.manage;

import android.app.Activity;

import com.classroomsdk.bean.RoomCacheMessage;
import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.common.Packager;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.utils.NotificationCenter;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKNotificationCenter;
import com.talkcloud.room.TKNotificationName;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/15.
 */

public class WBSession implements TKNotificationCenter.NotificationCenterDelegate {

    //获取房间信息成功
    public static final int onCheckRoom = 101;
    //文档信息
    public static final int onFileList = 102;
    //信令信息
    public static final int onRemoteMsg = 103;
    //房间连接成功
    public static final int onRoomConnected = 104;
    public static final int onUserJoined = 105;
    //其他用户
    public static final int onUserLeft = 106;
    public static final int onUserChanged = 107;
    public static final int onRoomLeaved = 108;
    public static final int onRoomJoin = 101;
    //回放时清楚所有信令
    public static final int onPlayBackClearAll = 109;
    //房间连接失败
    public static final int onRoomConnectFaild = 110;
    //提出房间
    public static final int participantEvicted = 111;
    //回放时间
    public static final int duration = 112;
    //回放结束
    public static final int playbackEnd = 113;
    //回放更新进度
    public static final int playback_updatetime = 114;
    //大并发用户上台
    public static final int participantPublished = 115;
    //回放进教室
    public static final int onPlayBackRoomJson = 116;

    //白板参数
    public static final int onWhiteBoardParam = 117;

    //MP3 MP4
    public static final int onShareMediaState = 118;
    //进度
    public static final int onUpdateAttributeStream = 119;

    public static final int onFirstVideoFrame = 200;

    public static final int msgList = 201;

    //白板加载成功
    public static boolean isPageFinish = false;
    //文档加载成功
    public static boolean isdocumentFinish = false;

    //进入课堂是否在上课途中，正在播放白板标注
    public static boolean isShowVideoWB = false;

    //mp3 播放控制
    public static boolean isPublish = false;
    //是否上课
    public static boolean isClassBegin = false;

    public static long serviceTime;//系统的时间
//    //课件加载成功
//    private boolean isFileListLoadSuccess = false;
//    //预加载回调
//    private boolean isGetConfigLoadSuccess = false;

    /**
     * 白板加载成功发送教室信息和文件信息
     */
    private List<RoomCacheMessage> checkRoomAndfileListBuffer = Collections.synchronizedList(new ArrayList<RoomCacheMessage>());

    /**
     * 文件加载成功发送缓存消息
     */
    private List<RoomCacheMessage> messageBuffer = Collections.synchronizedList(new ArrayList<RoomCacheMessage>());
    private List<RoomCacheMessage> roomConnectBuffer = Collections.synchronizedList(new ArrayList<RoomCacheMessage>());


    public static JSONArray jsVideoWBTempMsg = new JSONArray();

    public static ArrayList<String> DocServerAddrBackupList;
    public static String DocServerAddr;
    public static String DocServerAddrBackup;
    public static String host;
    public static int port;

    static private WBSession mInstance = null;

    public static String tplId;
    public static String skinId;
    public static String skinResource;
    public static String whiteboardcolorIndex;

    public static int roomtype;
    private Activity mContext;
    public String serial;

    static public WBSession getInstance() {
        synchronized (WBSession.class) {
            if (mInstance == null) {
                mInstance = new WBSession();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    public void onPlayBackRoomJson(int code, String response) {
        try {
            //设置教室数据
            setRoomData(new JSONObject(response));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onPlayBackRoomJson, code, response);
        } else {
            addMessageToBuffer(onPlayBackRoomJson, code, response);
        }
    }

    public void onCheckRoom(JSONObject jsonObject) {
        //设置教室数据
        setRoomData(jsonObject);
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onCheckRoom, jsonObject);
        } else {
            addCheckFileBuffer(onCheckRoom, jsonObject);
        }
    }

    /**
     * 设置教室数据
     *
     * @param jsonObject
     */
    private void setRoomData(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.has("room")) {
            JSONObject room = jsonObject.optJSONObject("room");
            serial = room.optString("serial");
            skinId = room.optString("skinId");
            tplId = room.optString("tplId");
            skinResource = room.optString("skinResource");
            roomtype = Integer.parseInt(room.optString("roomtype"));
            //企业教室配置项
            String chairmancontrol = room.optString("chairmancontrol");
            if (!"".equals(chairmancontrol)) {
                RoomControler.chairmanControl = chairmancontrol;
            }
            whiteboardcolorIndex = room.optString("whiteboardcolor");
        }
    }

    private void addCheckFileBuffer(int key, Object... args) {
        synchronized (WBSession.class) {
            if (checkRoomAndfileListBuffer != null) {
                RoomCacheMessage roomCacheMessage = new RoomCacheMessage();
                roomCacheMessage.setKey(key);
                roomCacheMessage.setObjects(args);
                checkRoomAndfileListBuffer.add(roomCacheMessage);
            }
        }
    }

    public void onCheckedFileRoom() {
        isPageFinish = true;
        if (checkRoomAndfileListBuffer != null && checkRoomAndfileListBuffer.size() > 0) {
            synchronized (WBSession.class) {
                for (int x = 0; x < checkRoomAndfileListBuffer.size(); x++) {
                    NotificationCenter.getInstance().postNotificationName(checkRoomAndfileListBuffer.get(x).getKey(),
                            checkRoomAndfileListBuffer.get(x).getObjects());
                }
                checkRoomAndfileListBuffer.clear();
            }
        }
    }

    public void onFileList(Object o) {

        ShareDoc mBlankShareDoc = new ShareDoc();
        mBlankShareDoc.setFileid(0);
        mBlankShareDoc.setCurrentPage(1);
        mBlankShareDoc.setPagenum(1);
        mBlankShareDoc.setFilename("白板");
        mBlankShareDoc.setFiletype("whiteboard");
        mBlankShareDoc.setSwfpath("");
        mBlankShareDoc.setPptslide(1);
        mBlankShareDoc.setPptstep(0);
        mBlankShareDoc.setSteptotal(0);
        mBlankShareDoc.setGeneralFile(true);
        mBlankShareDoc.setDynamicPPT(false);
        mBlankShareDoc.setH5Docment(false);
        mBlankShareDoc.setMedia(false);
        mBlankShareDoc.setIsContentDocument(0);
        WhiteBoradManager.getInstance().setmBlankShareDoc(mBlankShareDoc);
        WhiteBoradManager.getInstance().addDocList(mBlankShareDoc);
        WhiteBoradManager.getInstance().setDefaultFileDoc(mBlankShareDoc);

        if (o != null && !(o instanceof JSONObject)) {
            try {
                JSONArray jsobjs = new JSONArray(o.toString());
                for (int i = 0; i < jsobjs.length(); i++) {
                    JSONObject jsobj = jsobjs.optJSONObject(i);
                    ShareDoc doc = new ShareDoc();

                    doc.setCospdfpath(jsobj.optString("cospdfpath"));
                    doc.setFilepath(jsobj.optString("filepath"));
                    doc.setAnimation(jsobj.optInt("animation"));
                    doc.setStatus(jsobj.optInt("status"));
                    doc.setDownloadpath(jsobj.optString("downloadpath"));
                    doc.setPagenum(jsobj.optInt("pagenum"));
                    doc.setUploadusername(jsobj.optString("uploadusername"));
                    doc.setNewfilename(jsobj.optString("newfilename"));
                    doc.setUploaduserid(jsobj.optInt("uploaduserid"));
                    doc.setSwfpath(jsobj.optString("swfpath"));
                    doc.setPdfpath(jsobj.optString("pdfpath"));
                    doc.setFileid(jsobj.optInt("fileid"));
                    doc.setIsconvert(jsobj.optInt("isconvert"));
                    doc.setSize(jsobj.optInt("size"));
                    doc.setCompanyid(jsobj.optInt("companyid"));
                    doc.setFileserverid(jsobj.optInt("fileserverid"));
                    doc.setUploadtime(jsobj.optString("uploadtime"));
                    doc.setActive(jsobj.optInt("active"));
                    doc.setFilename(jsobj.optString("filename"));
                    doc.setFiletype(jsobj.optString("filetype"));
                    doc.setCurrentPage(1);
                    doc.setCurrentTime(jsobj.optDouble("currenttime"));
                    doc.setType(jsobj.optInt("type"));
                    doc.setMedia(getIsMedia(doc.getFilename()));
                    doc.setFileprop(jsobj.optInt("fileprop"));
                    doc.setFilecategory(jsobj.optInt("filecategory"));
                    doc.setDynamicPPT(doc.getFileprop() == 2);
                    doc.setGeneralFile(doc.getFileprop() == 0);
                    doc.setH5Docment(doc.getFileprop() == 3);
                    doc.setFilesetup(jsobj.optInt("filesetup"));
                    doc.setPreloadingzip(jsobj.getString("preloadingzip"));

                    doc.setIsContentDocument(jsobj.optInt("isContentDocument"));
//                if(doc.getFileprop() != 1){
                    WhiteBoradManager.getInstance().addDocList(doc);
//                }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (WhiteBoradManager.getInstance().getDefaultFileDoc().getPreloadingzip() == null
                || "".equals(WhiteBoradManager.getInstance().getDefaultFileDoc().getPreloadingzip())) {
            addFileList();
        }
//        if (!isGetConfigLoadSuccess) {
//            ShareDoc shareDoc = WhiteBoradManager.getInstance().getDefaultFileDoc();
//            if (shareDoc != null) {
//                ProLoadingDoc.getInstance().startDownload(shareDoc, mContext);
//                isFileListLoadSuccess = true;
//                isGetConfigLoadSuccess = true;
//            }
//        }
    }

    //预加载课件回调
    private void onGetConfigReady() {
        ProLoadingDoc.getInstance().startDownload(mContext.getApplicationContext());
    }

    //预加载缓存onfilelist
    public void addFileList() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onFileList);
        } else {
            addCheckFileBuffer(onFileList);
        }
    }

    public boolean onRemoteMsg(boolean add, String id, String name, long ts, Object data, boolean inList, String fromID, String associatedMsgID, String associatedUserID, JSONObject serverDate) {
        if (add) {
            if (name.equals("ClassBegin")) {
                isClassBegin = true;
            }
        } else {
            if (name.equals("ClassBegin")) {
                isClassBegin = false;
            }
        }
        if (isPageFinish && isdocumentFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRemoteMsg, add, id, name, ts, data,
                    inList, fromID, associatedMsgID, associatedUserID, serverDate);
        } else {
            addMessageToBuffer(onRemoteMsg, add, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID, serverDate);
        }
        if (name.equals("UpdateTime")) {
            if (isClassBegin) {
                serviceTime = ts;
            }
        }
        if (name.equals("WBPageCount") || name.equals("SharpsChange")) {
            return true;
        }
        return false;
    }


    public void onRoomConnected(JSONArray jsonArray, List list, JSONObject jsonObject) {

        for (int i = 0; i < list.size(); i++) {
            JSONObject js = new JSONObject((Map<String, Object>) list.get(i));
            String id = js.optString("id");
            Object data = js.opt("data");
            try {
                if (id.equals("DocumentFilePage_ShowPage")) {
                    ShareDoc currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
                    JSONObject jsmdata = null;
                    if (data instanceof JSONObject) {
                        jsmdata = (JSONObject) data;
                    } else if (data instanceof String) {
                        String strdata = (String) data;
                        jsmdata = new JSONObject(strdata);
                    }
                    currentFile = Packager.pageDoc(jsmdata);
                    WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                    WhiteBoradManager.getInstance().getDocList();
                } else if (id.equals("WBPageCount")) {
                    JSONObject jsmdata = null;
                    if (data instanceof JSONObject) {
                        jsmdata = (JSONObject) data;
                    } else if (data instanceof String) {
                        String strdata = (String) data;
                        jsmdata = new JSONObject(strdata);
                    }
                    if (WhiteBoradManager.getInstance() != null)
                        WhiteBoradManager.getInstance().getmBlankShareDoc().setPagenum(jsmdata.optInt("totalPage"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (isPageFinish && isdocumentFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRoomConnected, jsonArray, list, jsonObject);
        } else {
            if (!isdocumentFinish) {
                addConnectBuffer(onRoomConnected, jsonArray, list, jsonObject);
            } else {
                addMessageToBuffer(onRoomConnected, jsonArray, list, jsonObject);
            }
        }
    }

    private void addConnectBuffer(int key, Object... args) {
        synchronized (WBSession.class) {
            if (roomConnectBuffer != null) {
                RoomCacheMessage roomCacheMessage = new RoomCacheMessage();
                roomCacheMessage.setKey(key);
                roomCacheMessage.setObjects(args);
                roomConnectBuffer.add(roomCacheMessage);
            }
        }
    }

    public void onRoomConnected() {
        isdocumentFinish = true;
        if (roomConnectBuffer != null && roomConnectBuffer.size() > 0) {
            synchronized (WBSession.class) {
                for (int x = 0; x < roomConnectBuffer.size(); x++) {
                    NotificationCenter.getInstance().postNotificationName(roomConnectBuffer.get(x).getKey(),
                            roomConnectBuffer.get(x).getObjects());
                }
                roomConnectBuffer.clear();
            }
        }
    }

    public void onUserJoined(RoomUser roomUser, boolean inList, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUserJoined, roomUser, inList, jsonObject);
        } else {
            addMessageToBuffer(onUserJoined, roomUser, inList, jsonObject);
        }
    }

    public void onUserLeft(RoomUser roomUser, String peerid) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUserLeft, roomUser, peerid);
        } else {
            addMessageToBuffer(onUserLeft, roomUser, peerid);
        }
    }

    public void onUserChanged(RoomUser roomUser, Map<String, Object> map, String fromId, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUserChanged, roomUser, map, fromId, jsonObject);
        } else {
            addMessageToBuffer(onUserChanged, roomUser, map, fromId, jsonObject);
        }
    }

    public void onRoomLeaved() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRoomLeaved);
        } else {
            addMessageToBuffer(onRoomLeaved);
        }
    }

    public void onPlayBackClearAll() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onPlayBackClearAll);
        } else {
            addMessageToBuffer(onPlayBackClearAll);
        }
    }

    public void onRoomConnectFaild() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRoomConnectFaild);
        } else {
            addMessageToBuffer(onRoomConnectFaild);
        }
    }

    public void participantEvicted(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(participantEvicted, jsonObject);
        } else {
            addMessageToBuffer(participantEvicted, jsonObject);
        }
    }

    public void duration(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(duration, jsonObject);
        } else {
            addMessageToBuffer(duration, jsonObject);
        }
    }

    public void playbackEnd() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(playbackEnd);
        } else {
            addMessageToBuffer(playbackEnd);
        }
    }

    public void playback_updatetime(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(playback_updatetime, jsonObject);
        } else {
            addMessageToBuffer(playback_updatetime, jsonObject);
        }
    }

    public void participantPublished(RoomUser roomUser, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(participantPublished, jsonObject);
        } else {
            addMessageToBuffer(participantPublished, jsonObject);
        }
    }

    public void onWhiteBoardUrl(ArrayList<String> docServerAddrBackupList, String docServerAddr, String docServerAddrBackup, String host, int port) {
        this.DocServerAddrBackupList = docServerAddrBackupList;
        this.DocServerAddr = docServerAddr;
        this.DocServerAddrBackup = docServerAddrBackup;
        this.host = host;
        this.port = port;
        WhiteBoradManager.getInstance().setFileServierUrl(host);
        WhiteBoradManager.getInstance().setFileServierPort(port);
        NotificationCenter.getInstance().postNotificationName(onWhiteBoardParam, DocServerAddrBackupList, DocServerAddr, DocServerAddrBackup, host, port);
        //        if (isPageFinish) {
        //            onPageFinished();
        //            NotificationCenter.getInstance().postNotificationName(onWhiteBoardParam, DocServerAddrBackupList, DocServerAddr, DocServerAddrBackup, host, port);
        //        } else {
        //            addMessageToBuffer(onWhiteBoardParam,DocServerAddrBackupList,DocServerAddr,DocServerAddrBackup,host,port);
        //        }
    }

    private boolean getIsMedia(String filename) {
        boolean isMedia = false;
        if (filename.toLowerCase().endsWith(".mp3")
                || filename.toLowerCase().endsWith("mp4")
                || filename.toLowerCase().endsWith("webm")
                || filename.toLowerCase().endsWith("ogg")
                || filename.toLowerCase().endsWith("wav")) {
            isMedia = true;
        }
        return isMedia;
    }

    private void addMessageToBuffer(int key, Object... args) {
        synchronized (WBSession.class) {
            if (messageBuffer != null) {
                RoomCacheMessage roomCacheMessage = new RoomCacheMessage();
                roomCacheMessage.setKey(key);
                roomCacheMessage.setObjects(args);
                messageBuffer.add(roomCacheMessage);
            }
        }
    }

    private void onShareMediaState(String peerId, int state, Map<String, Object> attrs) {
        if (state == 0) {
            isPublish = false;
            TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard", "__all", null);
        } else if (state == 1) {
            isPublish = true;
        }
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onShareMediaState, peerId, state, attrs);
        } else {
            addMessageToBuffer(onShareMediaState, peerId, state, attrs);
        }
    }

    private void onUpdateAttributeStream(String peerid, int pos, boolean isPlay, HashMap<String, Object> hashMap) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUpdateAttributeStream, peerid, pos, isPlay, hashMap);
        } else {
            addMessageToBuffer(onUpdateAttributeStream, peerid, pos, isPlay, hashMap);
        }
    }

    public void onPageFinished() {
        onRoomConnected();
        isPageFinish = true;
        if (messageBuffer != null && messageBuffer.size() > 0) {
            synchronized (WBSession.class) {
                for (int x = 0; x < messageBuffer.size(); x++) {
                    NotificationCenter.getInstance().postNotificationName(messageBuffer.get(x).getKey(),
                            messageBuffer.get(x).getObjects());
                }
                messageBuffer.clear();
            }
        }
    }

    public void onRelease() {
        isPageFinish = false;
        isdocumentFinish = false;
        checkRoomAndfileListBuffer.clear();
        roomConnectBuffer.clear();
        messageBuffer.clear();

        isClassBegin = false;
        isPublish = false;
//        isFileListLoadSuccess = false;
//        isGetConfigLoadSuccess = false;

        TKNotificationCenter.getInstance().removeObserver(this);
        WhiteBoradManager.getInstance().clear();
    }

    /**
     *
     */
    public void addobservers(Activity context) {
        WhiteBoradManager.getInstance().setContext(context);
        this.mContext = context;
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onPlayBackRoomJson);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onCheckRoom);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onFileList);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onRemoteMsg);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onRoomConnected);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onUserJoined);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onUserLeft);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onUserChanged);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onRoomLeaved);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onPlayBackClearAll);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onRoomConnectFaild);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.participantEvicted);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.duration);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.playbackEnd);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.playback_updatetime);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.participantPublished);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onWhiteBoardUrl);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.msgList);
        //预加载
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onGetConfigReady);

        /*TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onShareMediaState);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onUpdateAttributeStream);
        TKNotificationCenter.getInstance().addObserver(this, TKNotificationName.onFirstVideoFrame);*/
    }


    @Override
    public void didReceivedNotification(final int id, final Object... objects) {

        if (objects == null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (id) {

                    case TKNotificationName.onGetConfigReady:
                        onGetConfigReady();
                        break;

                    case TKNotificationName.onPlayBackRoomJson:
                        int code = (int) objects[0];
                        String response = (String) objects[1];
                        onPlayBackRoomJson(code, response);
                        break;

                    case TKNotificationName.onCheckRoom:
                        onCheckRoom((JSONObject) objects[0]);
                        break;

                    case TKNotificationName.onFileList:
                        onFileList(objects[0]);
                        break;

                    case TKNotificationName.onRemoteMsg:
                        boolean add = (boolean) objects[0];
                        String id = (String) objects[1];
                        String name = (String) objects[2];
                        long ts = (long) objects[3];
                        Object data = objects[4];
                        boolean inList1 = (boolean) objects[5];
                        String fromID = (String) objects[6];
                        String associatedMsgID = (String) objects[7];
                        String associatedUserID = (String) objects[8];
                        JSONObject serverDate = (JSONObject) objects[9];
                        onRemoteMsg(add, id, name, ts, data, inList1, fromID, associatedMsgID, associatedUserID, serverDate);
                        break;

                    case TKNotificationName.onRoomConnected:
                        JSONArray jsonArray = (JSONArray) objects[0];
                        List list = (List) objects[1];
                        JSONObject jsonObje = (JSONObject) objects[2];
                        onRoomConnected(jsonArray, list, jsonObje);
                        break;

                    case TKNotificationName.onUserJoined:
                        RoomUser roomUser = (RoomUser) objects[0];
                        boolean inList = (boolean) objects[1];
                        JSONObject jsonObject = (JSONObject) objects[2];
                        onUserJoined(roomUser, inList, jsonObject);
                        break;

                    case TKNotificationName.onUserLeft:
                        RoomUser roomUserd = (RoomUser) objects[0];
                        String peerid = (String) objects[1];
                        onUserLeft(roomUserd, peerid);
                        break;

                    case TKNotificationName.onUserChanged:
                        RoomUser roomUser1 = (RoomUser) objects[0];
                        Map<String, Object> map = (Map<String, Object>) objects[1];
                        String fromId = (String) objects[2];
                        JSONObject jsonObject1 = (JSONObject) objects[3];
                        onUserChanged(roomUser1, map, fromId, jsonObject1);
                        break;

                    case TKNotificationName.onRoomLeaved:
                        onRoomLeaved();
                        break;

                    case TKNotificationName.onPlayBackClearAll:
                        onPlayBackClearAll();
                        break;

                    case TKNotificationName.onRoomConnectFaild:
                        onRoomConnectFaild();
                        break;

                    case TKNotificationName.participantEvicted:
                        participantEvicted((JSONObject) objects[0]);
                        break;

                    case TKNotificationName.duration:
                        duration((JSONObject) objects[0]);
                        break;

                    case TKNotificationName.playbackEnd:
                        playbackEnd();
                        break;

                    case TKNotificationName.playback_updatetime:
                        playback_updatetime((JSONObject) objects[0]);
                        break;

                    case TKNotificationName.participantPublished:
                        RoomUser roomUser2 = (RoomUser) objects[0];
                        JSONObject jsonObject2 = (JSONObject) objects[1];
                        participantPublished(roomUser2, jsonObject2);
                        break;

                    case TKNotificationName.onWhiteBoardUrl:
                        ArrayList<String> DocServerAddrBackupList = (ArrayList<String>) objects[0];
                        String DocServerAddr = (String) objects[1];
                        String DocServerAddrBackup = (String) objects[2];
                        String host = (String) objects[3];
                        int port = (int) objects[4];
                        onWhiteBoardUrl(DocServerAddrBackupList, DocServerAddr, DocServerAddrBackup, host, port);
                        break;

                    case TKNotificationName.onShareMediaState:
                        String peerId = (String) objects[0];
                        int state = (int) objects[1];
                        Map<String, Object> attrs = (Map<String, Object>) objects[2];
                        onShareMediaState(peerId, state, attrs);
                        break;

                    case TKNotificationName.onUpdateAttributeStream:
                        String peerid1 = (String) objects[0];
                        int pos = (int) objects[1];
                        boolean isPlay = (boolean) objects[2];
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) objects[3];
                        onUpdateAttributeStream(peerid1, pos, isPlay, hashMap);
                        break;

                    case TKNotificationName.msgList:
                        MsgList(objects);
                        break;
                }
            }
        }).start();
    }


    //前进后退时数据
    private void MsgList(Object[] objects) {
        if (objects.length > 0) {
            JSONArray jsonArray = (JSONArray) objects[0];
            if (isPageFinish) {
                onPageFinished();
                NotificationCenter.getInstance().postNotificationName(msgList, jsonArray);
            } else {
                addMessageToBuffer(onPlayBackClearAll, jsonArray);
            }
        }
    }

    public void addTempVideoWBRemoteMsg(boolean add, String id, String name, long ts, Object data, String fromID, String associatedMsgID, String associatedUserID) {
        if (add) {
            if (name.equals("VideoWhiteboard")) {
                isShowVideoWB = true;
            }
            JSONObject jsobj = new JSONObject();
            try {
                jsobj.put("id", id);
                jsobj.put("ts", ts);
                jsobj.put("data", data == null ? null : data.toString());
                jsobj.put("name", name);
                jsobj.put("fromID", fromID);
                if (!associatedMsgID.equals("")) {
                    jsobj.put("associatedMsgID", associatedMsgID);
                }
                if (!associatedUserID.equals("")) {
                    jsobj.put("associatedUserID", associatedUserID);
                }

                if (associatedMsgID.equals("VideoWhiteboard") || id.equals("VideoWhiteboard")) {
                    jsVideoWBTempMsg.put(jsobj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            isShowVideoWB = false;
        }
    }
}

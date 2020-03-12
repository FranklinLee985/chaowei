package com.classroomsdk.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.classroomsdk.Config;
import com.classroomsdk.R;
import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.bean.ShowPageBean;
import com.classroomsdk.bean.WhiteBoardFinshBean;
import com.classroomsdk.common.JSWhitePadInterface;
import com.classroomsdk.common.Packager;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.classroomsdk.interfaces.ILocalControl;
import com.classroomsdk.interfaces.IWBCallback;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.manage.WBSession;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.manage.WhiteBoradManager;
import com.classroomsdk.utils.NotificationCenter;
import com.classroomsdk.utils.Tools;
import com.loopj.android.http.RequestParams;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class WBFragment extends Fragment implements NotificationCenter.NotificationCenterDelegate, IWBCallback, ILocalControl, SharePadMgr.ShowWbFragmentViewListener {

    private View fragmentView;
    private WebView x5WebView;
    private boolean isClassBegin = false;
    private ShareDoc currentFile;
    private boolean isPlayBack = false;
    private SharedPreferences spkv = null;
    private SharedPreferences.Editor editor = null;

    public void setPlayBack(boolean playBack) {
        this.isPlayBack = playBack;
    }

    public boolean setHide;

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.tk_fragment_white_pad, null);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                android.webkit.WebView.setWebContentsDebuggingEnabled(true);
                WebView.setWebContentsDebuggingEnabled(true);
            }*/

            x5WebView = (WebView) fragmentView.findViewById(R.id.xwalkWebView);
            x5WebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            x5WebView.setHorizontalScrollBarEnabled(false);
            //WebView.setWebContentsDebuggingEnabled(true);
            JSWhitePadInterface.getInstance().setWBCallBack(this);
            x5WebView.addJavascriptInterface(JSWhitePadInterface.getInstance(), "JSWhitePadInterface");
            x5WebView.requestFocus();
            x5WebView.setBackgroundColor(0);
            x5WebView.getBackground().setAlpha(0);
            x5WebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                    sslErrorHandler.proceed();
                }
            });

            if (Config.isWhiteBoardTest) {
                x5WebView.loadUrl("http://192.168.4.162:9251/publish/index.html#/mobileApp?languageType=" + Tools.getSystemLanguage());//建行
                //xWalkView.loadUrl("http://192.168.1.220:9251/publish/index.html#/mobileApp?languageType=" + lan);
            } else {
                //x5WebView.loadUrl("http://debugtbs.qq.com/");
                x5WebView.loadUrl("file:///android_asset/react_mobile_new_publishdir/index.html#/mobileApp?languageType=" + Tools.getSystemLanguage());
            }

        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NotificationCenter.getInstance().removeObserver(this);
        WBSession.getInstance().onRelease();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            x5WebView.requestFocus();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    //白板显示文件变化
    @Override
    public void localChangeDoc() {
        final JSONObject jsobj = new JSONObject();
        currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
        if (currentFile != null) {
            if (((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()).equals((Long) currentFile.getFileid())) {
                currentFile.setBaseurl(WhiteBoradManager.getInstance().DefaultBaseurl);
            }
            JSONObject data = Packager.pageSendData(currentFile);
            try {
                jsobj.put("data", data.toString());
                jsobj.put("name", "ShowPage");
                jsobj.put("id", "DocumentFilePage_ShowPage");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (x5WebView != null) {
                                x5WebView.loadUrl("javascript:JsSocket.pubMsg(" + jsobj.toString() + ")");
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //发送信令
    @Override
    public void pubMsg(String js) {
        try {
            JSONObject jsobj = new JSONObject(js);
            String msgName = jsobj.optString("name");
            String msgId = jsobj.optString("id");
            String toId = jsobj.optString("toID");
            String data = jsobj.optString("data");
            String associatedMsgID = jsobj.optString("associatedMsgID");
            String associatedUserID = jsobj.optString("associatedUserID");
            boolean save = jsobj.optBoolean("do_not_save", false);
            if (jsobj.has("do_not_save")) {
                save = false;
            } else {
                save = true;
            }
            JSONObject jsdata = new JSONObject(data);
            if (msgId.equals("DocumentFilePage_ShowPage")) {
                currentFile = Packager.pageDoc(jsdata);
                //                WhiteBoradManager.getInstance().addDocList(currentFile);
                WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                WhiteBoradManager.getInstance().getDocList();
                //                WhiteBoradManager.getInstance().getAdminDocList();
                if (currentFile.getBaseurl() != null || !"".equals(currentFile.getBaseurl())) {
                    currentFile.setBaseurl(null);
                }

            }
            TKRoomManager.getInstance().pubMsg(msgName, msgId, toId, data, save, associatedMsgID, associatedUserID, Tools.toHashMap(jsobj.optString("expandParams")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //删除信令
    @Override
    public void delMsg(String js) {
        try {
            JSONObject jsobj = new JSONObject(js);
            String msgName = jsobj.optString("name");
            String msgId = jsobj.optString("id");
            String toId = jsobj.optString("toID");
            String data = jsobj.optString("data");
            TKRoomManager.getInstance().delMsg(msgName, msgId, toId, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //白板加载结束
    @Override
    public void onPageFinished() {
        //sendXinLing();
        final JSONObject j = new JSONObject();
        try {
            if (Tools.isTablet(getActivity())) {
                j.put("deviceType", "pad");
            } else {
                j.put("deviceType", "phone");
            }
            j.put("clientType", "android");
            j.put("isSendLogMessage", true);
            j.put("playback", isPlayBack);
            j.put("debugLog", true);//白板debug，h5程序可以做debug用

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (x5WebView != null) {
                        x5WebView.loadUrl("javascript:JsSocket.updateFakeJsSdkInitInfo(" + j.toString() + ")");
                        changeWBUrlAndPort();
                    }
                }
            });
        }
        /**
         * 白板加载成功后发送教室和文件缓存信息
         */
        WhiteBoradManager.getInstance().onPageFinished();
        WBSession.getInstance().onCheckedFileRoom();
    }

    /**
     * 白板加载结束发送信令
     */
    public void sendXinLing(){
        JSONObject jsobj = new JSONObject();
        try {
            jsobj.put("action","nativeUpdateDoc");
            WhiteBoardFinshBean whiteBoardFinshBean=new WhiteBoardFinshBean(WBSession.host,WBSession.DocServerAddrBackup,WBSession.port+"");
            jsobj.put("docAddress",whiteBoardFinshBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TKRoomManager.getInstance().pubMsg("RemoteControl","RemoteControl",TKRoomManager.getInstance().getMySelf().peerId,jsobj.toString(),false,null,null);
    }

    /**
     * 改变白板全屏
     *
     * @param isFull true 全屏，false 不全屏
     */
    @Override
    public void changePageFullScreen(String isFull) {
        if (!TextUtils.isEmpty(isFull)) {
            try {
                JSONObject object = new JSONObject(isFull);
                boolean full = object.optBoolean("fullScreen");
                WhiteBoradManager.getInstance().fullScreenToLc(full);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 白板全屏
     *
     * @param isFull true 全屏，false 不全屏
     */

    public void sendJSPageFullScreen(boolean isFull) {
        try {
            final JSONObject js = new JSONObject();
            js.put("isFullScreen", isFull);
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (x5WebView != null) {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'fullScreenChangeCallback'" + "," + js.toString() + ")");
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存value
     *
     * @param key
     * @param value
     */
    @Override
    public void saveValueByKey(String key, String value) {
        if (editor != null) {
            editor.putString(key, value);
            editor.commit();
        }
    }

    /**
     * 通过key获取value
     *
     * @param key
     * @param callbackId 回调id
     */
    @Override
    public void getValueByKey(String key, final int callbackId) {
        if (spkv != null) {
            final String value = spkv.getString(key, "");
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (x5WebView != null) {
                            x5WebView.loadUrl("javascript:JsSocket.JsSocketCallback(" + callbackId + ",'" + value + "')");
                        }
                    }
                });
            }
        }
    }

    //白板PPT预加载成功
    @Override
    public void documentPreloadingEnd() {
        WBSession.isdocumentFinish = true;
        /**
         * 等白板预加载文件信息后 再去发送所有缓存信息
         */
        WBSession.getInstance().onPageFinished();
    }

    /***
     *   加载成功和失败上传
     * @param stateJson
     */
    @Override
    public void documentLoadSuccessOrFailure(String stateJson) {
        String url = Config.REQUEST_HEADER + WhiteBoradManager.getInstance().getFileServierUrl() + ":"
                + WhiteBoradManager.getInstance().getFileServierPort() + "/ClientAPI/addcoursewareinfo";
        RequestParams params = new RequestParams();
        params.put("serial", WhiteBoradManager.getInstance().getSerial());
        params.put("userid", WhiteBoradManager.getInstance().getPeerId());
        params.put("ts", System.currentTimeMillis() / 1000L);
        params.put("detail", Base64.encodeToString(stateJson.getBytes(), Base64.DEFAULT));
        HttpHelp.getInstance().post(url, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                if (response != null && response.optInt("result") == 0) {
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    /**
     * 全屏  退出全屏  WebPage
     *
     * @param isFull true 全屏 false 退出全屏
     */
    public void changeWebPageFullScreen(final boolean isFull) {
        WhiteBoradManager.getInstance().fullScreenToLc(isFull);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFull) {
                    if (x5WebView != null) {
                        x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand (" + "'whiteboardSDK_fullScreen'" + ")");
                    }
                } else {
                    if (x5WebView != null) {
                        x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand (" + "'whiteboardSDK_exitFullScreen'" + ")");
                    }
                }
            }
        });
    }

    /***
     * JS调用我们的方法View状态
     */
    @Override
    public void receiveJSActionCommand(String stateJson) {
        ShowPageBean showPageBean = Packager.getShowPageBean(WhiteBoradManager.getInstance().getCurrentFileDoc());
        if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT() ||
                WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment() ||
                showPageBean.isSvg() || showPageBean.isGif()) {//动态ppt h5课件
            WhiteBoradManager.getInstance().onWhiteBoradReceiveActionCommand(stateJson);
        }
    }

    /***
     * JS调用我们的方法改变用户属性
     */
    @Override
    public void setProperty(String jsonProperty) {
        WhiteBoradManager.getInstance().onWhiteBoradSetProperty(jsonProperty);
    }

    /**
     * 修改白板端口号
     */
    public void changeWBUrlAndPort() {
        final JSONObject js = new JSONObject();
        try {
            if (WBSession.host == null) {
                return;
            }
            if (WBSession.host.endsWith("neiwang")) {
                String addres = WBSession.DocServerAddrBackup;
                if (!TextUtils.isEmpty(addres) && addres.length() > 1) {
                    if (addres.substring(0, 1).equals("[") && addres.substring(addres.length() - 1, addres.length()).equals("]")) {
                        js.put("backup_doc_host", addres.substring(1, addres.length() - 1));
                    } else {
                        js.put("backup_doc_host", WBSession.DocServerAddrBackup);
                    }
                }
                js.put("backup_doc_protocol", "http");
                js.put("backup_doc_port", 80);

                js.put("doc_protocol", "http");
                js.put("doc_host", WBSession.DocServerAddr);
                js.put("doc_port", 80);

                js.put("web_protocol", "http");
                js.put("web_host", WBSession.host);
                js.put("web_port", 80);

                JSONArray jsobjs = new JSONArray(WBSession.DocServerAddrBackupList);
                js.put("backup_doc_host_list", jsobjs);

            } else {
                if (Config.isWhiteBoardTest || Config.isWhiteVideoBoardTest) {
                    String addres = WBSession.DocServerAddrBackup;
                    if (!TextUtils.isEmpty(addres) && addres.length() > 1) {
                        if (addres.substring(0, 1).equals("[") && addres.substring(addres.length() - 1, addres.length()).equals("]")) {
                            js.put("backup_doc_host", addres.substring(1, addres.length() - 1));
                        } else {
                            js.put("backup_doc_host", WBSession.DocServerAddrBackup);
                        }
                    }
                    js.put("backup_doc_protocol", "http");
                    js.put("backup_doc_port", 80);

                    js.put("doc_protocol", "http");
                    js.put("doc_host", WBSession.DocServerAddr);
                    js.put("doc_port", 80);

                    js.put("web_protocol", "http");
                    js.put("web_host", WBSession.host);
                    js.put("web_port", 80);

                    JSONArray jsobjs = new JSONArray(WBSession.DocServerAddrBackupList);
                    js.put("backup_doc_host_list", jsobjs);

                } else {
                    String addres = WBSession.DocServerAddrBackup;
                    if (!TextUtils.isEmpty(addres) && addres.length() > 1) {
                        if (addres.substring(0, 1).equals("[") && addres.substring(addres.length() - 1, addres.length()).equals("]")) {
                            js.put("backup_doc_host", addres.substring(1, addres.length() - 1));
                        } else {
                            js.put("backup_doc_host", WBSession.DocServerAddrBackup);
                        }
                    }
                    js.put("backup_doc_protocol", "https");
                    js.put("backup_doc_port", 443);

                    js.put("doc_protocol", "https");
                    js.put("doc_host", WBSession.DocServerAddr);
                    js.put("doc_port", 443);

                    js.put("web_protocol", "https");
                    js.put("web_host", WBSession.host);
                    js.put("web_port", 443);


                    JSONArray jsobjs = new JSONArray(WBSession.DocServerAddrBackupList);
                    js.put("backup_doc_host_list", jsobjs);
                }
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (x5WebView != null) {
                            //sendXinLing();
                            x5WebView.loadUrl("javascript:JsSocket.updateWebAddressInfo(" + js.toString() + ")");
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 关闭PPT
     */
    public void closeNewPptVideo() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (x5WebView != null) {
                        x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand("
                                + "'closeDynamicPptWebPlay'" + ")");
                    }
                }
            });
        }
    }

    /**
     * 隐藏白板
     *
     * @param isHide
     */
    public void hideWalkView(boolean isHide) {
        if (x5WebView != null) {
            setHide = isHide;
            if (isHide) {
                x5WebView.setVisibility(View.INVISIBLE);
            } else {
                if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT() || WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment()) {//动态ppt h5课件
                    x5WebView.setVisibility(View.VISIBLE);
                } else {
                    //当后缀名为svg和gif也是走xwalkview
                    String swfpath = WhiteBoradConfig.getsInstance().getCurrentFileDoc().getSwfpath();
                    if (!TextUtils.isEmpty(swfpath) && (swfpath.endsWith(".svg") || swfpath.endsWith(".gif"))) {
                        x5WebView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * 播放暂停媒体
     *
     * @param isplay
     */
    @Override
    public void playbackPlayAndPauseController(boolean isplay) {
        try {
            final JSONObject jsmsg = new JSONObject();
            jsmsg.put("play", isplay);
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (x5WebView != null) {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'playbackPlayAndPauseController'" + "," + jsmsg.toString() + ")");
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改白板大小
     *
     * @param wid 宽度
     * @param hid 高度
     */
    public void transmitWindowSize(int wid, int hid) {
        final JSONObject js = new JSONObject();
        try {
            JSONObject jsmsg = new JSONObject();
            jsmsg.put("windowWidth", wid);
            jsmsg.put("windowHeight", hid);
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (x5WebView != null) {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_updateWhiteboardSize'" + "," + js.toString() + ")");
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *  跟JS交互
     */
    public void interactiveJS(final String name, final String json) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (x5WebView != null) {
                        if (TextUtils.isEmpty(json)) {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + name + ")");
                        } else {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + name + "," + json.toString() + ")");
                        }
                    }
                }
            });
        }
    }

    /***
     *  跟JS交互 翻页  b  ture 代表右翻页   false 左翻页
     */
    public void interactiveJSPaging(ShareDoc shareDoc, final boolean b, final boolean nextOrAdd) {
        if (shareDoc.getFileid() == 0) {  // 白板加页
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (b) {
                            if (x5WebView != null) {
                                if (nextOrAdd) {
                                    x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_nextPage'" + ")");
                                } else {
                                    x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_addPage'" + ")");
                                }
                            }
                        } else {
                            if (x5WebView != null) {
                                x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_prevPage'" + ")");
                            }
                        }
                    }
                });
            }
        } else {
            if (shareDoc.isDynamicPPT()) {  //  PPT文档
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (b) {
                                if (x5WebView != null) {
                                    x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_nextStep'" + ")");
                                }
                            } else {
                                if (x5WebView != null) {
                                    x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_prevStep'" + ")");
                                }
                            }
                        }
                    });
                }
            } else {    // H5和普通文档
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (b) {
                                if (x5WebView != null) {
                                    x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_nextPage'" + ")");
                                }
                            } else {
                                if (x5WebView != null) {
                                    x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_prevPage'" + ")");
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    /***
     *
     * @param number   文档指定放到哪一页
     */
    public void interactiveJSSelectPage(int number) {
        final JSONObject js = new JSONObject();
        try {
            js.put("number", number);
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (x5WebView != null) {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_skipPage'" + "," + js.toString() + ")");
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        spkv = context.getSharedPreferences("dataphone", MODE_PRIVATE);
        editor = spkv.edit();
        SharePadMgr.getInstance().setShowWbFragmentViewListener(this);

        NotificationCenter.getInstance().addObserver(this, WBSession.onCheckRoom);
        NotificationCenter.getInstance().addObserver(this, WBSession.onFileList);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRemoteMsg);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomConnected);
        NotificationCenter.getInstance().addObserver(this, WBSession.onUserJoined);
        NotificationCenter.getInstance().addObserver(this, WBSession.onUserLeft);
        NotificationCenter.getInstance().addObserver(this, WBSession.onUserChanged);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomLeaved);
        NotificationCenter.getInstance().addObserver(this, WBSession.onPlayBackClearAll);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomConnectFaild);
        NotificationCenter.getInstance().addObserver(this, WBSession.participantEvicted);
        NotificationCenter.getInstance().addObserver(this, WBSession.duration);
        NotificationCenter.getInstance().addObserver(this, WBSession.playbackEnd);
        NotificationCenter.getInstance().addObserver(this, WBSession.playback_updatetime);
        NotificationCenter.getInstance().addObserver(this, WBSession.participantPublished);
        NotificationCenter.getInstance().addObserver(this, WBSession.onPlayBackRoomJson);
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {

        if (args == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (id) {

                    //获取房间信息成功
                    case WBSession.onCheckRoom:
                        JSONObject jsonObjectCheckRoom = (JSONObject) args[0];
                        onCheckRoom(jsonObjectCheckRoom);
                        break;

                    //回放进教室
                    case WBSession.onPlayBackRoomJson:
                        int code = (int) args[0];
                        String response = (String) args[1];
                        onPlayBackRoomJson(code, response);
                        break;

                    //文档信息（课件库）
                    case WBSession.onFileList:
                        onFileList();
                        break;

                    //信令信息
                    case WBSession.onRemoteMsg:
                        boolean addRemoteMsg = (boolean) args[0];
                        String idRemoteMsg = (String) args[1];
                        String nameRemoteMsg = (String) args[2];
                        long tsRemoteMsg = (long) args[3];
                        Object dataRemoteMsg = (Object) args[4];
                        boolean inList = (boolean) args[5];
                        String fromIDRemoteMsg = (String) args[6];
                        String associatedMsgIDRemoteMsg = (String) args[7];
                        String associatedUserIDRemoteMsg = (String) args[8];
                        JSONObject jsonObjectRemoteMsg = (JSONObject) args[9];
                        onRemoteMsg(addRemoteMsg, idRemoteMsg, nameRemoteMsg, tsRemoteMsg, dataRemoteMsg, inList, fromIDRemoteMsg, associatedMsgIDRemoteMsg, associatedUserIDRemoteMsg, jsonObjectRemoteMsg);
                        break;

                    //房间连接成功
                    case WBSession.onRoomConnected:
                        JSONArray jsonArrayRoomConnected = (JSONArray) args[0];
                        List listRoomConnected = (List) args[1];
                        JSONObject jsonObjectRoomConnected = (JSONObject) args[2];
                        onRoomConnected(jsonArrayRoomConnected, listRoomConnected, jsonObjectRoomConnected);
                        break;

                    //其他用户进入房间
                    case WBSession.onUserJoined:
                        RoomUser roomUserJoined = (RoomUser) args[0];
                        boolean inListUserJoined = (boolean) args[1];
                        JSONObject jsonObjectUserJoined = (JSONObject) args[2];
                        onUserJoined(roomUserJoined, inListUserJoined, jsonObjectUserJoined);
                        break;

                    //其他用户离开房间
                    case WBSession.onUserLeft:
                        RoomUser roomUserLeft = (RoomUser) args[0];
                        String peeridUserLeft = (String) args[1];
                        onUserLeft(roomUserLeft, peeridUserLeft);
                        break;

                    //用户属性改变
                    case WBSession.onUserChanged:
                        RoomUser roomUserChanged = (RoomUser) args[0];
                        Map<String, Object> mapUserChanged = (Map<String, Object>) args[1];
                        String sUserChanged = (String) args[2];
                        JSONObject jsonObjectUserChanged = (JSONObject) args[3];
                        onUserChanged(roomUserChanged, mapUserChanged, sUserChanged, jsonObjectUserChanged);
                        break;

                    //离开房间
                    case WBSession.onRoomLeaved:
                        onRoomLeaved();
                        break;

                    //回放时清除所有信令
                    case WBSession.onPlayBackClearAll:
                        onPlayBackClearAll();
                        break;

                    //房间连接失败
                    case WBSession.onRoomConnectFaild:
                        onRoomConnectFaild();
                        break;

                    //踢出房间
                    case WBSession.participantEvicted:
                        JSONObject participant = (JSONObject) args[0];
                        participantEvicted(participant);
                        break;

                    //回放时间
                    case WBSession.duration:
                        JSONObject durationJSONO = (JSONObject) args[0];
                        duration(durationJSONO);
                        break;

                    //回放结束
                    case WBSession.playbackEnd:
                        playbackEnd();
                        break;

                    //回放更新进度
                    case WBSession.playback_updatetime:
                        JSONObject playback_updatetimeJSONO = (JSONObject) args[0];
                        playback_updatetime(playback_updatetimeJSONO);
                        break;

                    //大并发用户上台回调
                    case WBSession.participantPublished:
                        JSONObject participantJSONO = (JSONObject) args[0];
                        participantPublished(participantJSONO);
                        break;
                }
            }
        });
    }

    /**
     * 文档信息（课件库）
     */
    @Override
    public void onFileList() {
        if (getActivity() != null && WBSession.isPageFinish) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsobj = new JSONObject();
                        ShareDoc shareDoc = WhiteBoradManager.getInstance().getDefaultFileDoc();
                        if (shareDoc != null) {
                            JSONObject data = Packager.pageSendData(shareDoc);
                            jsobj.put("cmd", data.toString());
                        } else {
                            jsobj.put("cmd", "");
                        }
                        if (x5WebView != null) {
                            x5WebView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'preLoadingFile'" + "," + jsobj.toString() + ")");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 回放进教室
     *
     * @param code     返回码
     * @param response 返回参数
     */
    private void onPlayBackRoomJson(final int code, final String response) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (x5WebView != null) {
                        if (code == 0) {
                            x5WebView.loadUrl("javascript:JsSocket.checkroom(" + response.toString() + ")");
                        }
                    }
                }
            });
        }
    }

    /**
     * 大并发用户上台回调
     *
     * @param participantJSONO 携带的参数
     */
    private void participantPublished(final JSONObject participantJSONO) {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.participantPublished(" + participantJSONO.toString() + ")");
                }
            });
        }
    }

    /**
     * 回放更新进度
     *
     * @param playback_updatetimeJSONO 携带的参数
     */
    private void playback_updatetime(final JSONObject playback_updatetimeJSONO) {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.playback_updatetime(" + playback_updatetimeJSONO.toString() + ")");
                }
            });
        }
    }

    /**
     * 回放结束
     */
    private void playbackEnd() {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.playbackEnd(" + ")");
                }
            });
        }
    }

    /**
     * 回放时间
     *
     * @param durationJSONO 携带的参数
     */
    private void duration(final JSONObject durationJSONO) {
        if (getActivity() != null && x5WebView != null && durationJSONO != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.duration(" + durationJSONO.toString() + ")");
                }
            });
        }
    }

    /**
     * 踢出房间
     *
     * @param participant
     */
    private void participantEvicted(final JSONObject participant) {
        if (getActivity() != null && x5WebView != null && participant != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.participantEvicted(" + participant.toString() + ")");
                }
            });
        }
    }

    /**
     * 房间连接失败
     */
    private void onRoomConnectFaild() {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject js = new JSONObject();
                    x5WebView.loadUrl("javascript:JsSocket.disconnect(" + js.toString() + ")");
                }
            });
        }
    }

    /**
     * 回放时清除所有信令
     */
    private void onPlayBackClearAll() {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject js = new JSONObject();
                    x5WebView.loadUrl("javascript:JsSocket.playback_clearAll(" + js.toString() + ")");
                }
            });
        }
    }

    /**
     * 离开房间
     */
    private void onRoomLeaved() {
        SharePadMgr.getInstance().reset();
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.leaveroom(" + ")");
                }
            });
        }
    }

    /**
     * 用户属性改变
     *
     * @param roomUser   改变属性的用户
     * @param map        改变的属性集合
     * @param s
     * @param jsonObject 消息携带的参数
     */
    private void onUserChanged(final RoomUser roomUser, final Map<String, Object> map, String s, final JSONObject jsonObject) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (x5WebView != null && jsonObject != null) {
                        x5WebView.loadUrl("javascript:JsSocket.setProperty(" + jsonObject.toString() + ")");
                    }
                }
            });
        }
    }

    /**
     * 其他用户离开房间
     *
     * @param roomUser       离开的用户
     * @param peeridUserLeft 离开的用户的id
     */
    private void onUserLeft(RoomUser roomUser, final String peeridUserLeft) {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.participantLeft(" + "'" + peeridUserLeft + "'" + ")");
                }
            });
        }
    }

    /**
     * 其他用户进入房间
     *
     * @param roomUser   进入的用户
     * @param inList     是否在我之前进入房间，true—之前，false—之后
     * @param jsonObject
     */
    private void onUserJoined(RoomUser roomUser, boolean inList, final JSONObject jsonObject) {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.participantJoined(" + jsonObject.toString() + ")");
                }
            });
        }
    }

    /**
     * 房间连接成功
     *
     * @param jsonArray
     * @param list       消息携带参数
     * @param jsonObject 消息携带参数
     */
    private void onRoomConnected(JSONArray jsonArray, List list, final JSONObject jsonObject) {

        changeWBUrlAndPort();

        JSONObject jsdata = new JSONObject();
        for (int i = 0; i < list.size(); i++) {
            JSONObject js = Tools.mapToJson((Map<String, Object>) list.get(i));
            String id = js.optString("id");
            Object data = js.opt("data");
            try {
                if (!js.optString("associatedMsgID", "").equals("VideoWhiteboard")) {
                    jsdata.put(id, js);
                }
                if ("ClassBegin".equals(js.optString("name"))) {
                    isClassBegin = true;
                    JSWhitePadInterface.isClassbegin = true;
                } else if (id.equals("DocumentFilePage_ShowPage")) {
                    currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
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
                    WhiteBoradManager.getInstance().getmBlankShareDoc().setPagenum(jsmdata.optInt("totalPage"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            JSONObject myselfProperties = Packager.myPropertie(TKRoomManager.getInstance().getMySelf().toJson());
            hashMap.put("properties", myselfProperties);
            hashMap.put("id", TKRoomManager.getInstance().getMySelf().peerId);
            jsonObject.put("myself", Tools.mapToJson(hashMap));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if (WhiteBoradManager.getInstance().getDefaultFileDoc() != null && WhiteBoradManager.getInstance().DefaultBaseurl != null) {
                String data = jsonObject.getJSONObject("msglist").getJSONObject("DocumentFilePage_ShowPage").getString("data");
                JSONObject jsonObject1 = new JSONObject(data);
                Long id = jsonObject1.getJSONObject("filedata").getLong("fileid");
                if (((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()) != null && id.equals(WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid())) {
                    jsonObject1.getJSONObject("filedata").put("baseurl", WhiteBoradManager.getInstance().DefaultBaseurl);
                    jsonObject.getJSONObject("msglist").getJSONObject("DocumentFilePage_ShowPage").remove("data");
                    jsonObject.getJSONObject("msglist").getJSONObject("DocumentFilePage_ShowPage").put("data", jsonObject1);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.roomConnected(" + 0 + "," + jsonObject.toString() + ")");
                }
            });
        }

        if (!jsdata.has("DocumentFilePage_ShowPage")) {
            if (WhiteBoradManager.getInstance().getDefaultFileDoc() == null) {
                if (WhiteBoradManager.getInstance().getDocList().size() > 1) {
                    currentFile = WhiteBoradManager.getInstance().getDocList().get(1);
                } else {
                    if (WhiteBoradManager.getInstance().getDocList().size() > 0) {
                        currentFile = WhiteBoradManager.getInstance().getDocList().get(0);
                    }
                }
            } else {
                currentFile = WhiteBoradManager.getInstance().getDefaultFileDoc();
            }
            WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
            if (currentFile != null) {
                if ((WhiteBoradManager.getInstance().getDefaultFileDoc()) != null
                        && ((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()) != null
                        && ((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()).equals((Long) currentFile.getFileid())) {
                    if (WhiteBoradManager.getInstance().DefaultBaseurl != null) {
                        currentFile.setBaseurl(WhiteBoradManager.getInstance().DefaultBaseurl);
                    }
                }
                final JSONObject jsobj = new JSONObject();
                JSONObject data = Packager.pageSendData(currentFile);
                try {
                    jsobj.put("data", data.toString());
                    jsobj.put("name", "ShowPage");
                    jsobj.put("id", "DocumentFilePage_ShowPage");
                    if (getActivity() != null && x5WebView != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                x5WebView.loadUrl("javascript:JsSocket.pubMsg(" + jsobj.toString() + ")");
                            }
                        });
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
        TKRoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime", TKRoomManager.getInstance().getMySelf().peerId, new JSONObject(), false, null, null);
    }

    /**
     * 信令信息
     *
     * @param add              true信令添加，false信令删除
     * @param id               信令id
     * @param name             信令名称
     * @param ts               消息发送时间戳
     * @param data             信令参数
     * @param inList           信令是否在消息列表中
     * @param fromID           发送者id
     * @param associatedMsgID  消息关联消息的 id （该消息删除时会跟随删除）
     * @param associatedUserID 消息关联用户的 id （该用户退出时会跟随删除）
     * @param jsonObject       消息携带数据
     */
    private void onRemoteMsg(boolean add, String id, String name, long ts, Object data, boolean inList, String fromID, String associatedMsgID, String associatedUserID, final JSONObject jsonObject) {
        if (add) {
            onRemotePubMsg(id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID, jsonObject);
        } else {
            onRemoteDelMsg(id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID, jsonObject);
        }
    }

    private void onRemoteDelMsg(String id, String name, long ts, Object data, boolean inList, String fromID, String associatedMsgID, String associatedUserID, final JSONObject jsonObject) {
        switch (name) {
            case "ClassBegin":  // 下课
                acceptDelClassBegin();
                break;
        }

        if (getActivity() != null && x5WebView != null && jsonObject != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.delMsg(" + jsonObject.toString() + ")");
                }
            });
        }
    }

    /***
     *   下课
     */
    private void acceptDelClassBegin() {
        isClassBegin = false;
        JSWhitePadInterface.isClassbegin = false;
        if (!RoomControler.isNotLeaveAfterClass()) {
            WhiteBoradManager.getInstance().resumeFileList();
            if (WhiteBoradManager.getInstance().getDefaultFileDoc() == null) {
                if (WhiteBoradManager.getInstance().getDocList().size() > 1) {
                    currentFile = WhiteBoradManager.getInstance().getDocList().get(1);
                } else {
                    if (WhiteBoradManager.getInstance().getDocList().size() > 0) {
                        currentFile = WhiteBoradManager.getInstance().getDocList().get(0);
                    } else {
                        currentFile = WhiteBoradManager.getInstance().getmBlankShareDoc();
                    }
                }
            } else {
                currentFile = WhiteBoradManager.getInstance().getDefaultFileDoc();
            }
            WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
            if (WhiteBoradManager.getInstance().getDefaultFileDoc() != null) {
                if (((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()) != null && ((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()).equals((Long) currentFile.getFileid())) {
                    if (WhiteBoradManager.getInstance().DefaultBaseurl != null) {
                        currentFile.setBaseurl(WhiteBoradManager.getInstance().DefaultBaseurl);
                    }
                }
            }

            final JSONObject jsobj = new JSONObject();
            JSONObject resumedasta = Packager.pageSendData(currentFile);
            try {
                jsobj.put("data", resumedasta.toString());
                jsobj.put("name", "ShowPage");
                jsobj.put("id", "DocumentFilePage_ShowPage");
                if (getActivity() != null && x5WebView != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            x5WebView.loadUrl("javascript:JsSocket.pubMsg(" + jsobj.toString() + ")");
                        }
                    });
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void onRemotePubMsg(String id, String name, long ts, Object data, boolean inList, String fromID, String associatedMsgID, String associatedUserID, final JSONObject jsonObject) {

        String strdata = null;
        if (data instanceof String) {
            strdata = (String) data;
        } else if (data instanceof Map) {
            strdata = new JSONObject((Map) data).toString();
        }

        switch (name) {
            case "ClassBegin":  // 上课
                acceptClassBegin(inList);
                break;

            case "ShowPage":
                acceptDocumentFilePage_ShowPage(strdata);
                break;

            case "DocumentChange":
                acceptDocumentChange(strdata);
                break;
        }
        //        movePopupwindow(name, data);//拖动

        try {
            if (getActivity() != null && x5WebView != null && jsonObject != null) {
                if (strdata == null || !new JSONObject(strdata).optBoolean("isMedia")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject json = jsonObject;

                                String name = json.getString("name");
                                if (name.equals("ShowPage")) {
                                    String data1 = json.getString("data");
                                    JSONObject dataconv = new JSONObject(data1);
                                    JSONObject filedata = dataconv.getJSONObject("filedata");

                                    long fileid = filedata.getInt("fileid");
                                    if (WhiteBoradManager.getInstance().getDefaultFileDoc() != null && ((Long) WhiteBoradManager.getInstance().getDefaultFileDoc().getFileid()).equals((Long) fileid)) {
                                        if (WhiteBoradManager.getInstance().DefaultBaseurl != null) {
                                            json.remove("data");
                                            filedata.put("baseurl", WhiteBoradManager.getInstance().DefaultBaseurl);
                                            json.put("data", dataconv);
                                        }
                                    }
                                }

                                x5WebView.loadUrl("javascript:JsSocket.pubMsg(" + json.toString() + ")");

                                /*if (name.equals("switchLayout")) {
                                    if (currentFile.isH5Docment()) {
                                        //x5WebView.loadUrl("javascript:JsSocket.pubMsg(" + "{\"name\":\"ShowPage\",\"id\":\"DocumentFilePage_ShowPage\",\"toID\":\"__all\",\"fromID\":\"afa0023d-b867-eab8-8047-65d83236f171\",\"seq\":191,\"ts\":1569231574,\"data\":{\"sourceInstanceId\":\"default\",\"isGeneralFile\":false,\"isMedia\":false,\"isDynamicPPT\":false,\"isH5Document\":true,\"action\":\"show\",\"mediaType\":\"\",\"filedata\":{\"currpage\":1,\"pptslide\":1,\"pptstep\":0,\"steptotal\":0,\"fileid\":57581,\"pagenum\":10,\"filename\":\"20171113_122801_dvzzojqb.zip\",\"filetype\":\"zip\",\"isContentDocument\":0,\"swfpath\":\"\\/cospath\\/20190923_103301_tvaodfyf\\/index.html\",\"baseurl\":\"file:\\/\\/\\/storage\\/emulated\\/0\\/Android\\/data\\/com.talkcloud.plus\\/files\\/DecompressionFileDoc\\/h5-57581-success\\/index.html\"}}}" + ")");
                                    }
                                }*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *    文档列表改变
     * @param strdata
     */
    private void acceptDocumentChange(String strdata) {
        if (strdata != null) {
            ShareDoc doc = new ShareDoc();
            try {
                JSONObject jsmdata = new JSONObject(strdata);
                boolean isdel = Tools.isTure(jsmdata.get("isDel"));
                doc = Packager.pageDoc(jsmdata);
                if (!isClassBegin && doc.getFileid() == WhiteBoradConfig.getsInstance().getCurrentMediaDoc().getFileid()) {
                    TKRoomManager.getInstance().stopShareMedia();
                }
                WhiteBoradManager.getInstance().onRoomFileChange(doc, isdel, false, isClassBegin);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *    文档切换
     * @param strdata
     */
    private void acceptDocumentFilePage_ShowPage(String strdata) {
        currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
        try {
            JSONObject jsmdata = new JSONObject(strdata);
            currentFile = Packager.pageDoc(jsmdata);
            WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
            WhiteBoradManager.getInstance().refreshFileList(currentFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *    上課信令
     */
    private void acceptClassBegin(boolean inList) {
        isClassBegin = true;
        JSWhitePadInterface.isClassbegin = true;

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            if (RoomControler.isDocumentClassification()) {
                WhiteBoradManager.getInstance().getClassDocList();
                WhiteBoradManager.getInstance().getAdminDocList();
                WhiteBoradManager.getInstance().getClassMediaList();
                WhiteBoradManager.getInstance().getAdminmMediaList();
            }

            if (!RoomControler.isAutoClassBegin() && !inList) {
                if (RoomControler.isNotLeaveAfterClass()) {
                    WhiteBoradManager.getInstance().resumeFileList();
                }
                currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
                if (currentFile != null) {
                    JSONObject jsdata = Packager.pageSendData(currentFile);
                    TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", jsdata.toString(), true, null, null);
                }
            }
        }
    }

    /**
     * 获取房间信息成功
     *
     * @param jsonObjectCheckRoom 返回参数
     */
    private void onCheckRoom(final JSONObject jsonObjectCheckRoom) {
        if (getActivity() != null && x5WebView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x5WebView.loadUrl("javascript:JsSocket.checkroom(" + jsonObjectCheckRoom.toString() + ")");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (this.x5WebView != null) {
            x5WebView.destroy();
        }
        super.onDestroy();
    }

    /**
     * 显示课件
     *
     * @param mCurrentShareDoc
     */
    @Override
    public void onShow(final ShowPageBean mCurrentShareDoc) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (x5WebView != null && !setHide) {
                    x5WebView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // 表示是普通文档  pdf和xwalk 都隐藏
    @Override
    public void onHide() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (x5WebView != null) {
                    x5WebView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void scrollXWalkView(final MotionEvent event) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (x5WebView != null) {
                    x5WebView.onTouchEvent(event);
                }
            }
        });
    }
}

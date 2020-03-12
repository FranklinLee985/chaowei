package com.classroomsdk.manage;

import android.content.Context;
import android.text.TextUtils;

import com.classroomsdk.Config;
import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.common.Packager;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.classroomsdk.interfaces.ILocalControl;
import com.classroomsdk.interfaces.IWBStateCallBack;
import com.classroomsdk.utils.SortFileUtil;
import com.classroomsdk.utils.Tools;
import com.classroomsdk.utils.UploadFile;
import com.loopj.android.http.RequestParams;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/5/19.
 */

public class WhiteBoradManager {


    private IWBStateCallBack callBack;
    private ILocalControl control;
    static private WhiteBoradManager mInstance = null;

    private ShareDoc currentMediaDoc;
    private ShareDoc currentFileDoc;
    private ShareDoc defaultFileDoc;

    ShareDoc mBlankShareDoc;
    private boolean isPhotoClassBegin = false;
    private Context mContext;
    //默认课件本地Url；
    public String DefaultBaseurl = null;

    public ShareDoc getmBlankShareDoc() {
        return mBlankShareDoc;
    }

    public void setmBlankShareDoc(ShareDoc mBlankShareDoc) {
        this.mBlankShareDoc = mBlankShareDoc;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private String peerId = "";
    private String serial = "";
    private String fileServierUrl = "";
    private int fileServierPort = 80;
    private ConcurrentHashMap<Long, ShareDoc> docMap = new ConcurrentHashMap<Long, ShareDoc>();
    private ConcurrentHashMap<Long, ShareDoc> mediaMap = new ConcurrentHashMap<Long, ShareDoc>();

    /*private ArrayList<ShareDoc> docList = new ArrayList<ShareDoc>();*/
    private List<ShareDoc> docList = Collections.synchronizedList(new ArrayList<ShareDoc>());

    private ArrayList<ShareDoc> classDocList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> adminDocList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> mediaList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> classMediaList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> adminMediaList = new ArrayList<ShareDoc>();
    private int userrole = -1;

    public ShareDoc getDefaultFileDoc() {
        return defaultFileDoc;
    }

    public void setDefaultFileDoc(ShareDoc defaultFileDoc) {
        //设置默认课件时预下载的文件路径设置到baseurl上
        if (ProLoadingDoc.getInstance().defaultFileDoc != null &&
                defaultFileDoc.getFileid() == ProLoadingDoc.getInstance().defaultFileDoc.getFileid()
                && TextUtils.isEmpty(defaultFileDoc.getBaseurl())
                && !TextUtils.isEmpty(WhiteBoradManager.getInstance().DefaultBaseurl)) {
            defaultFileDoc.setBaseurl(WhiteBoradManager.getInstance().DefaultBaseurl);
        }
        this.defaultFileDoc = defaultFileDoc;
    }

    public void setUserrole(int userrole) {
        this.userrole = userrole;
    }

    public void setLocalControl(ILocalControl control) {
        this.control = control;
    }

    public List<ShareDoc> getDocList() {
        docList.clear();
        if (docMap != null && docMap.size() > 0) {
            for (ShareDoc d : docMap.values()) {
                if (d.getFileid() != 0 && d != null) {
                    docList.add(d);
                }
            }
            try {
                SortFileUtil.getInstance().toSort(docList, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        docList.add(0, mBlankShareDoc);
        return docList;
    }

    public ArrayList<ShareDoc> getMediaList() {
        mediaList.clear();
        if (mediaMap != null && mediaMap.size() > 0) {
            for (ShareDoc d : mediaMap.values()) {
                mediaList.add(d);
            }
            SortFileUtil.getInstance().toSort(mediaList, false);
        }
        return mediaList;
    }

    public ArrayList<ShareDoc> getClassDocList() {
        classDocList.clear();
        if (docMap != null && docMap.size() > 0) {
            for (ShareDoc d : docMap.values()) {
                if (d.getFilecategory() == 0 && d.getFileid() != 0) {
                    classDocList.add(d);
                }
            }
            SortFileUtil.getInstance().toSort(classDocList, false);
        }
        classDocList.add(0, mBlankShareDoc);
        return classDocList;
    }

    public ArrayList<ShareDoc> getAdminDocList() {
        adminDocList.clear();
        if (docMap != null && docMap.size() > 0) {
            for (ShareDoc d : docMap.values()) {
                if (d.getFilecategory() == 1 && d.getFileid() != 0) {
                    adminDocList.add(d);
                }
            }
            SortFileUtil.getInstance().toSort(adminDocList, false);
        }
        //教室文件没有白板
        //adminDocList.add(0, mBlankShareDoc);
        return adminDocList;
    }

    public ArrayList<ShareDoc> getClassMediaList() {
        classMediaList.clear();
        if (mediaMap != null && mediaMap.size() > 0) {
            for (ShareDoc d : mediaMap.values()) {
                if (d.getFilecategory() == 0) {
                    classMediaList.add(d);
                }
            }
            SortFileUtil.getInstance().toSort(classMediaList, false);
        }
        return classMediaList;
    }

    public ArrayList<ShareDoc> getAdminmMediaList() {
        adminMediaList.clear();
        if (mediaMap != null && mediaMap.size() > 0) {
            for (ShareDoc d : mediaMap.values()) {
                if (d.getFilecategory() == 1) {
                    adminMediaList.add(d);
                }
            }
            SortFileUtil.getInstance().toSort(adminMediaList, false);
        }
        return adminMediaList;
    }

    public void addDocList(ShareDoc doc) {
        if (doc.isMedia()) {
            mediaMap.put(doc.getFileid(), doc);
        } else {
            if (doc.getType() == 1) {
                defaultFileDoc = doc.clone();
            }
            if (doc.getFileid() == 0) {
                setmBlankShareDoc(doc);
            }
            docMap.put(doc.getFileid(), doc);
        }
    }

    public ShareDoc getCurrentMediaDoc() {
        if (currentMediaDoc == null) {
            return currentMediaDoc = new ShareDoc();
        }
        return currentMediaDoc;
    }


    public String getFileServierUrl() {
        return fileServierUrl;
    }

    public void setFileServierUrl(String fileServierUrl) {
        this.fileServierUrl = fileServierUrl;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getPeerId() {
        return peerId;
    }

    public int getFileServierPort() {
        return fileServierPort;
    }

    public void setFileServierPort(int fileServierPort) {
        this.fileServierPort = fileServierPort;
    }

    public ShareDoc getCurrentFileDoc() {
        if (currentFileDoc == null) {
            return currentFileDoc = new ShareDoc();
        }
        return currentFileDoc;
    }

    public void setCurrentFileDoc(ShareDoc doc) {
        if (doc != null) {
            this.currentFileDoc = doc.clone();
            if (doc.getFileid() == 0) {
                mBlankShareDoc = doc.clone();
            } else {
                if (docMap.containsKey(doc.getFileid())) {
                    doc.setFilecategory(docMap.get(doc.getFileid()).getFilecategory());
                    docMap.replace(doc.getFileid(), doc.clone());
                }
            }
        }
    }

    public void setCurrentMediaDoc(ShareDoc doc) {
        this.currentMediaDoc = doc;
    }

    private WhiteBoradManager() {
    }

    static public WhiteBoradManager getInstance() {
        synchronized (WhiteBoradManager.class) {
            if (mInstance == null) {
                mInstance = new WhiteBoradManager();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mContext = null;
        mInstance = null;
        callBack = null;
    }

    public void clear() {
        currentMediaDoc = null;
        currentFileDoc = null;

        classDocList.clear();
        classMediaList.clear();
        adminDocList.clear();
        adminMediaList.clear();

        docList.clear();
        mediaList.clear();
        docMap.clear();
        mediaMap.clear();
        defaultFileDoc = null;
        DefaultBaseurl = null;
        ProLoadingDoc.getInstance().cancel();

    }

    public void setWBCallBack(IWBStateCallBack wbCallBack) {
        this.callBack = wbCallBack;
        ProLoadingDoc.getInstance().setWBCallBack(wbCallBack);
    }

    public void onPageFinished() {
        if (callBack != null) {
            /*callBack.onPageFinished();*/
        }
    }

    public void fullScreenToLc(boolean isFull) {
        if (callBack != null) {
            callBack.onWhiteBoradZoom(isFull);
        }
    }

    /**
     * 刷新课件库列表
     */
    public void refreshFileList(ShareDoc doc) {
        if (callBack != null) {
            callBack.onRoomDocChange(false, false, doc);
        }
    }

    /**
     * 教室文件发生变化时
     *
     * @param sdoc         课件
     * @param isdel        课件是否删除
     * @param islocal      触发的是否本地行为
     * @param isClassBegin 是否上课
     */
    public void onRoomFileChange(ShareDoc sdoc, boolean isdel, boolean islocal, boolean isClassBegin) {
        //是否删除
        if (isdel) {
            if (!sdoc.isMedia()) {
                if (docMap.containsKey(sdoc.getFileid())) {
                    if (currentFileDoc != null && sdoc.getFileid() == currentFileDoc.getFileid()) {
                        callGetNextDoc(sdoc);
                        WhiteBoradConfig.getsInstance().localChangeDoc(currentFileDoc);
                        //删除当前课件且是上课后
                        if (isClassBegin) {
                            JSONObject data = Packager.pageSendData(currentFileDoc);
                            TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
                        }
                    }
                    docMap.remove(sdoc.getFileid());
                }
            } else if (mediaMap.containsKey(sdoc.getFileid())) {
                mediaMap.remove(sdoc.getFileid());
            }
        } else {
            //添加课件 php会发showpage信令
            addDocList(sdoc);
            if (!isClassBegin && islocal) {
                WhiteBoradConfig.getsInstance().localChangeDoc(sdoc);
            }
        }
        if (callBack != null) {
            callBack.onRoomDocChange(isdel, sdoc.isMedia(), sdoc);
        }
    }

    /**
     * 删除当前课件显示课件列表上一个课件
     *
     * @param doc
     */
    private void callGetNextDoc(ShareDoc doc) {
        if (RoomControler.isDocumentClassification()) {
            //对教室文件排序
            List<ShareDoc> stepClassArrayList = SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TIME, false, classDocList, true);
            if (getIndexByDocid(doc.getFileid(), stepClassArrayList) >= 0) {
                getNextDoc(doc.getFileid(), stepClassArrayList);
            } else {
//                getNextDoc(doc.getFileid(), adminDocList);
                getNextDoc(doc.getFileid(), SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TIME, false, adminDocList, true));
            }
        } else {
//            getNextDoc(doc.getFileid(), docList);
            getNextDoc(doc.getFileid(), SortFileUtil.getInstance().toSort(SortFileUtil.SORT_TYPE_TIME, false, docList, true));
        }
        //删除缺省课件时 设置默认课件为白板
        if (defaultFileDoc.getFileid() == doc.getFileid()) {
            defaultFileDoc = mBlankShareDoc;
        }
        docMap.remove(doc.getFileid());
    }

    private int getIndexByDocid(long docid, List<ShareDoc> list) {
        for (int i = 0; i < list.size(); i++) {
            ShareDoc dc = list.get(i);
            if (dc.getFileid() == docid) {
                return i;
            }
        }
        return -1;
    }

    public void getNextDoc(long docid, List<ShareDoc> list) {
        synchronized (currentFileDoc) {
            int removeIndex = getIndexByDocid(docid, list);
            int size = list.size();
            if (removeIndex > 0 && removeIndex < size && currentFileDoc.getFileid() == docid) {
                currentFileDoc = list.get(removeIndex - 1);
            } else {
                currentFileDoc = mBlankShareDoc;
            }
        }
    }

    public void delRoomFile(final String roomID, final long docid, final boolean isMedia, final boolean isClassBegin) {
        String url = Config.REQUEST_HEADER + fileServierUrl + ":" + fileServierPort + "/ClientAPI/" + "delroomfile";
        RequestParams params = new RequestParams();
        params.put("serial", roomID + "");
        params.put("fileid", docid + "");

        HttpHelp.getInstance().post(url, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    ShareDoc doc = new ShareDoc();
                    doc.setFileid(docid);
                    doc.setMedia(isMedia);

                    JSONObject data = Packager.pageSendData(doc);
                    data.put("isDel", true);

                    TKRoomManager.getInstance().pubMsg("DocumentChange", "DocumentChange", "__allExceptSender", data.toString(), false, null, null);
                    onRoomFileChange(doc, true, true, isClassBegin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    public void uploadRoomFile(final String roomID, final String path, boolean isClassBegin, int writedb) {
        isPhotoClassBegin = isClassBegin;
        String url = Config.REQUEST_HEADER + fileServierUrl + ":" + fileServierPort + "/ClientAPI/" + "uploaddocument";
        UploadFile uf = new UploadFile();
        uf.UploadOperation(url);
        uf.packageFile(path, roomID + "", TKRoomManager.getInstance().getMySelf().peerId, TKRoomManager.getInstance().getMySelf().nickName, writedb);
        uf.start();
    }

    public void localChangeDoc(ShareDoc doc) {
        setCurrentFileDoc(doc);
        if (control != null) {
            control.localChangeDoc();
        }
    }

    public void playbackPlayAndPauseController(boolean isplay) {
        if (control != null) {
            control.playbackPlayAndPauseController(isplay);
        }
    }

    public void resumeFileList() {
        if (RoomControler.isDocumentClassification()) {
            for (int i = 0; i < classDocList.size(); i++) {
                ShareDoc dc = classDocList.get(i);
                dc.setCurrentPage(1);
                dc.setPptstep(0);
                dc.setSteptotal(0);
                dc.setPptslide(1);
                if (dc.getFileid() == 0) {
                    dc.setPagenum(1);
                }
                if (currentFileDoc != null && currentFileDoc.getFileid() == dc.getFileid() && dc.getFileid() == 0) {
                    currentFileDoc = dc;
                }
            }
            for (int i = 0; i < adminDocList.size(); i++) {
                ShareDoc dc = adminDocList.get(i);
                dc.setCurrentPage(1);
                dc.setPptstep(0);
                dc.setSteptotal(0);
                dc.setPptslide(1);
                if (currentFileDoc != null && currentFileDoc.getFileid() == dc.getFileid() && dc.getFileid() == 0) {
                    currentFileDoc = dc;
                }
            }
        } else {
            for (int i = 0; i < docList.size(); i++) {
                ShareDoc dc = docList.get(i);
                dc.setCurrentPage(1);
                dc.setPptstep(0);
                dc.setSteptotal(0);
                dc.setPptslide(1);
                if (dc.getFileid() == 0) {
                    dc.setPagenum(1);
                }
                if (currentFileDoc != null && currentFileDoc.getFileid() == dc.getFileid() && dc.getFileid() == 0) {
                    currentFileDoc = dc;
                }
            }
        }
    }

    public void onUploadPhotos(JSONObject response) {
        ShareDoc docPhoto = null;
        JSONObject data = null;
        try {
            docPhoto = new ShareDoc();
            docPhoto.setSwfpath(response.getString("swfpath"));
            docPhoto.setPagenum(response.getInt("pagenum"));
            docPhoto.setFileid(response.getLong("fileid"));
            docPhoto.setDownloadpath(response.getString("downloadpath"));
            docPhoto.setSize(response.getLong("size"));
            docPhoto.setStatus(response.getInt("status"));
            docPhoto.setFilename(response.getString("filename"));
            docPhoto.setFileprop(response.getInt("fileprop"));
            docPhoto.setDynamicPPT(false);
            docPhoto.setGeneralFile(true);
            docPhoto.setH5Docment(false);
            /* docPhoto.setType(response.getInt("type"));*/
            String fileType = response.getString("filename").substring(response.getString("filename").lastIndexOf(".") + 1);
            if (!TextUtils.isEmpty(fileType)) {
                docPhoto.setFiletype(fileType);
            } else {
                docPhoto.setFiletype("jpg");
            }
            data = Packager.pageSendData(docPhoto);
            data.put("isDel", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TKRoomManager.getInstance().pubMsg("DocumentChange", "DocumentChange", "__allExceptSender", data.toString(), false, null, null);
        onRoomFileChange(docPhoto, false, true, isPhotoClassBegin);

        if (isPhotoClassBegin) {
            TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
        }
    }

    public void onWhiteBoradReceiveActionCommand(String stateJson) {
        if (callBack != null) {
            callBack.onWhiteBoradAction(stateJson);
        }
    }

    /**
     * 改变用户属性
     *
     * @param jsonProperty
     */
    public void onWhiteBoradSetProperty(String jsonProperty) {

        try {
            JSONObject json = new JSONObject(jsonProperty);
            String peerId = json.optString("id");
            String toID = json.optString("toID");
            JSONObject properties = json.optJSONObject("properties");

            HashMap<String, Object> map = (HashMap<String, Object>) Tools.toMap(properties);
            TKRoomManager.getInstance().changeUserProperty(peerId, toID, map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.classroomsdk.common;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.bean.ShowPageBean;
import com.classroomsdk.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/8/29.
 */

public class Packager {
    public static ShareDoc pageDoc(JSONObject jsdata) {
        ShareDoc doc = new ShareDoc();
        JSONObject jsfiledata = jsdata.optJSONObject("filedata");
        doc.setGeneralFile(Tools.isTure(jsdata.opt("isGeneralFile")));
        doc.setMedia(Tools.isTure(jsdata.opt("isMedia")));
        doc.setDynamicPPT(Tools.isTure(jsdata.opt("isDynamicPPT")));
        doc.setH5Docment(Tools.isTure(jsdata.opt("isH5Document")));
        doc.setFileid(jsfiledata.optInt("fileid"));
        doc.setCurrentPage(jsfiledata.optInt("currpage"));
        doc.setFiletype(jsfiledata.optString("filetype"));
        doc.setPagenum(jsfiledata.optInt("pagenum"));
        doc.setFilename(jsfiledata.optString("filename"));
        doc.setSwfpath(jsfiledata.optString("swfpath"));
        doc.setPptslide(jsfiledata.optInt("pptslide", 1));
        doc.setPptstep(jsfiledata.optInt("pptstep"));
        doc.setSteptotal(jsfiledata.optInt("steptotal"));
        doc.setFilecategory(jsfiledata.optInt("filecategory"));
        doc.setCospdfpath(jsfiledata.optString("cospdfpath"));
        doc.setBaseurl(jsfiledata.optString("baseurl"));

        doc.setIsContentDocument(jsfiledata.optInt("isContentDocument"));

        return doc;
    }

    public static ShowPageBean getShowPageBean(ShareDoc doc) {

        ShowPageBean.FiledataBean filedataBean = new ShowPageBean.FiledataBean();

        filedataBean.setCospdfpath(doc.getCospdfpath());
        filedataBean.setFileid(String.valueOf(doc.getFileid()));
        filedataBean.setCurrpage(doc.getCurrentPage());
        filedataBean.setFilename(doc.getFilename());
        filedataBean.setFiletype(doc.getFiletype());
        filedataBean.setIsContentDocument(doc.getIsContentDocument());
        filedataBean.setPagenum(doc.getPagenum());
        filedataBean.setPptslide(doc.getPptslide());
        filedataBean.setSteptotal(doc.getSteptotal());
        filedataBean.setSwfpath(doc.getSwfpath());
        filedataBean.setPptstep(doc.getPptstep());


        ShowPageBean showPageBean = new ShowPageBean();
        showPageBean.setDynamicPPT(doc.isDynamicPPT());
        showPageBean.setGeneralFile(doc.isGeneralFile());
        showPageBean.setH5Document(doc.isH5Docment());
        showPageBean.setMedia(doc.isMedia());
        showPageBean.setFiledata(filedataBean);

        return showPageBean;
    }


    public static JSONObject pageSendData(ShareDoc doc) {
        JSONObject jsdata = new JSONObject();
        JSONObject filedata = new JSONObject();
        try {

            jsdata.put("downloadpath", doc.getDownloadpath());

            jsdata.put("isGeneralFile", doc.isGeneralFile());
            jsdata.put("isMedia", doc.isMedia());
            jsdata.put("isDynamicPPT", doc.isDynamicPPT());
            jsdata.put("isH5Document", doc.isH5Docment());

            jsdata.put("action", doc.isDynamicPPT() || doc.isH5Docment() ? "show" : "");

            jsdata.put("mediaType", doc.isMedia() ? Tools.isMp4(doc.getFilename()) ? "video" : "audio" : "");
            filedata.put("fileid", doc.getFileid());
            filedata.put("currpage", doc.getCurrentPage());
            filedata.put("pagenum", doc.getPagenum());
            filedata.put("filecategory", doc.getFilecategory());
            filedata.put("filetype", doc.getFiletype());
            filedata.put("filename", doc.getFilename());
            filedata.put("swfpath", doc.getSwfpath());
            filedata.put("pptslide", doc.getPptslide());
            filedata.put("pptstep", doc.getPptstep());
            filedata.put("pptstep", doc.getPptstep());
            filedata.put("baseurl", doc.getBaseurl());

            /*filedata.put("pdfpath", doc.getPdfpath());
            filedata.put("cospdfpath", doc.getCospdfpath());*/

            filedata.put("isContentDocument", doc.getIsContentDocument());

           /* filedata.put("type", doc.getType());*/
            if (doc.isDynamicPPT()) {
                filedata.put("swfpath", doc.getDownloadpath() == null ? doc.getSwfpath() : doc.getDownloadpath());
            } else {
                filedata.put("swfpath", doc.getSwfpath());
            }
            filedata.put("steptotal", doc.getSteptotal());
            jsdata.put("filedata", filedata);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsdata;
    }

    public static JSONObject myPropertie(JSONObject roomUser) {

        JSONObject jsdata = new JSONObject();
        try {
            jsdata.put("nickname", roomUser.optString("nickname"));
            jsdata.put("role", roomUser.optInt("role"));
            jsdata.put("hasaudio", roomUser.optBoolean("hasaudio"));
            jsdata.put("hasvideo", roomUser.optBoolean("hasvideo"));
            jsdata.put("candraw", roomUser.optBoolean("candraw"));
            jsdata.put("publishstate", roomUser.optInt("rolpublishstatee"));
            jsdata.put("systemversion", roomUser.optString("systemversion"));
            jsdata.put("disablevideo", roomUser.optBoolean("disablevideo"));
            jsdata.put("version", roomUser.optString("version"));
            jsdata.put("devicetype", roomUser.optString("devicetype"));
            jsdata.put("roomtype", roomUser.optInt("roomtype"));
            jsdata.put("volume", roomUser.optInt("volume"));
            jsdata.put("isInBackGround", roomUser.optBoolean("isInBackGround"));
            jsdata.put("udpstate", roomUser.optInt("udpstate"));
            jsdata.put("appType", roomUser.optString("appType"));
            jsdata.put("disableaudio", roomUser.optBoolean("disableaudio"));
            jsdata.put("servername", roomUser.optString("democn"));
            jsdata.put("tk_ip", roomUser.optString("tk_ip"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsdata;
    }
}

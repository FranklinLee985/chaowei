package com.classroomsdk.bean;


import java.io.Serializable;

/**
 * 白板文档bean
 */
public class ShowPageBean implements Cloneable {

    private String sourceInstanceId = "default";
    private boolean isGeneralFile;
    private boolean isMedia;
    private boolean isDynamicPPT;
    private boolean isH5Document;
    private String action;
    private String mediaType;
    private FiledataBean filedata;

    public ShowPageBean() {
    }

    public ShowPageBean(String sourceInstanceId, boolean isGeneralFile, boolean isMedia, boolean isDynamicPPT,
                        boolean isH5Document, String action, String mediaType, FiledataBean filedata) {
        this.sourceInstanceId = sourceInstanceId;
        this.isGeneralFile = isGeneralFile;
        this.isMedia = isMedia;
        this.isDynamicPPT = isDynamicPPT;
        this.isH5Document = isH5Document;
        this.action = action;
        this.mediaType = mediaType;
        this.filedata = filedata;
    }

    public static class FiledataBean implements Serializable {

        private int currpage;
        private int pptslide;
        private int pptstep;
        private int steptotal;
        private String fileid;
        private int pagenum;
        private String filename;
        private String filetype;
        private int isContentDocument;
        private String swfpath;
        private String cospdfpath; //PDF文档地址

        public String getCospdfpath() {
            return cospdfpath;
        }


        public void setCospdfpath(String cospdfpath) {
            this.cospdfpath = cospdfpath;
        }

        public FiledataBean() {
        }

        public FiledataBean(int currpage, int pptslide, int pptstep, int steptotal, String fileid,
                            int pagenum, String filename, String filetype, String swfpath, String cospdfpath) {

            this.currpage = currpage;
            this.pptslide = pptslide;
            this.pptstep = pptstep;
            this.steptotal = steptotal;
            this.fileid = fileid;
            this.pagenum = pagenum;
            this.filename = filename;
            this.filetype = filetype;
            this.swfpath = swfpath;
            this.cospdfpath = cospdfpath;
        }

        public int getCurrpage() {
            return currpage;
        }

        public void setCurrpage(int currpage) {
            this.currpage = currpage;
        }

        public String getFileid() {
            return fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }

        public int getPptslide() {
            return pptslide;
        }

        public void setPptslide(int pptslide) {
            this.pptslide = pptslide;
        }

        public int getPptstep() {
            return pptstep;
        }

        public void setPptstep(int pptstep) {
            this.pptstep = pptstep;
        }

        public int getSteptotal() {
            return steptotal;
        }

        public void setSteptotal(int steptotal) {
            this.steptotal = steptotal;
        }


        public int getPagenum() {
            return pagenum;
        }

        public void setPagenum(int pagenum) {
            this.pagenum = pagenum;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFiletype() {
            return filetype;
        }

        public void setFiletype(String filetype) {
            this.filetype = filetype;
        }

        public int getIsContentDocument() {
            return isContentDocument;
        }

        public void setIsContentDocument(int isContentDocument) {
            this.isContentDocument = isContentDocument;
        }

        public String getSwfpath() {
            return swfpath;
        }

        public void setSwfpath(String swfpath) {
            this.swfpath = swfpath;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"currpage\":" + currpage +
                    ", \"pptslide\":" + pptslide +
                    ", \"pptstep\":" + pptstep +
                    ", \"steptotal\":" + steptotal +
                    ", \"fileid\":\"" + fileid + "\"" +
                    ", \"pagenum\":" + pagenum +
                    ", \"filename\":\"" + filename + "\"" +
                    ", \"filetype\":\"" + filetype + "\"" +
                    ", \"isContentDocument\":" + isContentDocument +
                    ", \"swfpath\":\"" + swfpath + "\"" +
                    ", \"cospdfpath\":\"" + cospdfpath + "\"" +
                    "}";
        }
    }


    public String getSourceInstanceId() {
        return sourceInstanceId;
    }

    public void setSourceInstanceId(String sourceInstanceId) {
        this.sourceInstanceId = sourceInstanceId;
    }

    public boolean isGeneralFile() {
        return isGeneralFile;
    }

    public void setGeneralFile(boolean isGeneralFile) {
        this.isGeneralFile = isGeneralFile;
    }

    public boolean isMedia() {
        return isMedia;
    }

    public void setMedia(boolean isMedia) {
        this.isMedia = isMedia;
    }

    public boolean isDynamicPPT() {
        return isDynamicPPT;
    }

    public void setDynamicPPT(boolean isDynamicPPT) {
        this.isDynamicPPT = isDynamicPPT;
    }

    public boolean isH5Document() {
        return isH5Document;
    }

    public void setH5Document(boolean isH5Document) {
        this.isH5Document = isH5Document;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public FiledataBean getFiledata() {
        return filedata;
    }

    public void setFiledata(FiledataBean filedata) {
        this.filedata = filedata;
    }

    // 判断 svg 和 gif ，svg 和 gif 交给H5处理，翻页时，调用H5方法翻页
    public boolean isSvg() {
        if (filedata == null || filedata.swfpath == null || "".equals(filedata.swfpath)) {
            return false;
        }
        return filedata.swfpath.endsWith("svg");
    }

    public boolean isGif() {
        if (filedata == null || filedata.swfpath == null || "".equals(filedata.swfpath)) {
            return false;
        }
        return filedata.swfpath.endsWith("gif");
    }

    @Override
    public String toString() {
        return "{" +
                "\"sourceInstanceId\":\"" + sourceInstanceId + "\"" +
                ", \"isGeneralFile\":" + isGeneralFile +
                ", \"isMedia\":" + isMedia +
                ", \"isDynamicPPT\":" + isDynamicPPT +
                ", \"isH5Document\":" + isH5Document +
                ", \"action\":\"" + action + "\"" +
                ", \"mediaType\":\"" + mediaType + "\"" +
                ", \"filedata\":" + filedata.toString() +
                "}";
    }


    @Override
    public ShowPageBean clone() {
        ShowPageBean showPageBean = null;
        try {
            showPageBean = (ShowPageBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return showPageBean;
    }
}

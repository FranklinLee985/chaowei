package com.classroomsdk.bean;

/**
 * 客户端截图 返回数据保存
 */
public class CaptureImg {


    /**
     * captureImgInfo : {"result":0,"swfpath":"/cospath/20190218_163542_xvbahytz.jpg","pagenum":1,"fileid":"20190218163540","downloadpath":"/cospath/20190218_163542_xvbahytz.jpg","size":88290,"status":1,"filename":"TK20190218163536.jpg","dynamicppt":0,"fileprop":0,"cospdfpath":null,"cospath":"https://demodoc-1253417915.cos.ap-guangzhou.myqcloud.com","realUrl":"","isContentDocument":0}
     * remSize : {"width":5.625,"height":3.2}
     */

    private CaptureImgInfoBean captureImgInfo;
    private RemSizeBean remSize;

    public CaptureImgInfoBean getCaptureImgInfo() {
        return captureImgInfo;
    }

    public void setCaptureImgInfo(CaptureImgInfoBean captureImgInfo) {
        this.captureImgInfo = captureImgInfo;
    }

    public RemSizeBean getRemSize() {
        return remSize;
    }

    public void setRemSize(RemSizeBean remSize) {
        this.remSize = remSize;
    }

    public static class CaptureImgInfoBean {
        /**
         * result : 0
         * swfpath : /cospath/20190218_163542_xvbahytz.jpg
         * pagenum : 1
         * fileid : 20190218163540
         * downloadpath : /cospath/20190218_163542_xvbahytz.jpg
         * size : 88290
         * status : 1
         * filename : TK20190218163536.jpg
         * dynamicppt : 0
         * fileprop : 0
         * cospdfpath : null
         * cospath : https://demodoc-1253417915.cos.ap-guangzhou.myqcloud.com
         * realUrl :
         * isContentDocument : 0
         */

        private int result;
        private String swfpath;
        private int pagenum;
        private String fileid;
        private String downloadpath;
        private int size;
        private int status;
        private String filename;
        private int dynamicppt;
        private int fileprop;
        private Object cospdfpath;
        private String cospath;
        private String realUrl;
        private int isContentDocument;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getSwfpath() {
            return swfpath;
        }

        public void setSwfpath(String swfpath) {
            this.swfpath = swfpath;
        }

        public int getPagenum() {
            return pagenum;
        }

        public void setPagenum(int pagenum) {
            this.pagenum = pagenum;
        }

        public String getFileid() {
            return fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }

        public String getDownloadpath() {
            return downloadpath;
        }

        public void setDownloadpath(String downloadpath) {
            this.downloadpath = downloadpath;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public int getDynamicppt() {
            return dynamicppt;
        }

        public void setDynamicppt(int dynamicppt) {
            this.dynamicppt = dynamicppt;
        }

        public int getFileprop() {
            return fileprop;
        }

        public void setFileprop(int fileprop) {
            this.fileprop = fileprop;
        }

        public Object getCospdfpath() {
            return cospdfpath;
        }

        public void setCospdfpath(Object cospdfpath) {
            this.cospdfpath = cospdfpath;
        }

        public String getCospath() {
            return cospath;
        }

        public void setCospath(String cospath) {
            this.cospath = cospath;
        }

        public String getRealUrl() {
            return realUrl;
        }

        public void setRealUrl(String realUrl) {
            this.realUrl = realUrl;
        }

        public int getIsContentDocument() {
            return isContentDocument;
        }

        public void setIsContentDocument(int isContentDocument) {
            this.isContentDocument = isContentDocument;
        }
    }

    public static class RemSizeBean {
        /**
         * width : 5.625
         * height : 3.2
         */

        private double width;
        private double height;

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }
}

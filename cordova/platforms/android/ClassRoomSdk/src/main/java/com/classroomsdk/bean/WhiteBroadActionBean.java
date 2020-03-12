package com.classroomsdk.bean;

import java.io.Serializable;

/**
 * date 2019/1/19
 * version
 * describe 白板交互行为类
 *
 * @author hxd
 */
public class WhiteBroadActionBean implements Serializable {
    private Page page;
    private String fileTypeMark;
    private int scale;
    private double irregular;

    public WhiteBroadActionBean(Page page, String fileTypeMark, int scale, int irregular) {
        this.page = page;
        this.fileTypeMark = fileTypeMark;
        this.scale = scale;
        this.irregular = irregular;
    }

    public WhiteBroadActionBean() {
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getFileTypeMark() {
        return fileTypeMark;
    }

    public void setFileTypeMark(String fileTypeMark) {
        this.fileTypeMark = fileTypeMark;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public double getIrregular() {
        return irregular;
    }

    public void setIrregular(double irregular) {
        this.irregular = irregular;
    }

    public static class Page implements Serializable {
        private boolean nextPage;
        private boolean prevPage;
        private boolean skipPage;
        private boolean addPage;
        private boolean nextStep;
        private boolean prevStep;
        private int currentPage;
        private int totalPage;

        public Page() {
        }

        public Page(boolean nextPage, boolean prevPage, boolean skipPage, boolean addPage, boolean nextStep, boolean prevStep, int currentPage, int totalPage) {
            this.nextPage = nextPage;
            this.prevPage = prevPage;
            this.skipPage = skipPage;
            this.addPage = addPage;
            this.nextStep = nextStep;
            this.prevStep = prevStep;
            this.currentPage = currentPage;
            this.totalPage = totalPage;
        }

        public boolean isNextPage() {
            return nextPage;
        }

        public void setNextPage(boolean nextPage) {
            this.nextPage = nextPage;
        }

        public boolean isPrevPage() {
            return prevPage;
        }

        public void setPrevPage(boolean prevPage) {
            this.prevPage = prevPage;
        }

        public boolean isSkipPage() {
            return skipPage;
        }

        public void setSkipPage(boolean skipPage) {
            this.skipPage = skipPage;
        }

        public boolean isAddPage() {
            return addPage;
        }

        public void setAddPage(boolean addPage) {
            this.addPage = addPage;
        }

        public boolean isNextStep() {
            return nextStep;
        }

        public void setNextStep(boolean nextStep) {
            this.nextStep = nextStep;
        }

        public boolean isPrevStep() {
            return prevStep;
        }

        public void setPrevStep(boolean prevStep) {
            this.prevStep = prevStep;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"nextPage\":" + nextPage +
                    ",\"prevPage\":" + prevPage +
                    ",\"skipPage\":" + skipPage +
                    ",\"addPage\":" + addPage +
                    ",\"nextStep\":" + nextStep +
                    ",\"prevStep\":" + prevStep +
                    ",\"currentPage\":" + currentPage +
                    ",\"totalPage\":" + totalPage +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"page\":" + page +
                ",\"fileTypeMark\":\"" + fileTypeMark + '\"' +
                ",\"scale\":" + scale +
                ",\"irregular\":" + irregular +
                '}';
    }
}

package com.classroomsdk.bean;

/**
 * 小白板文档bean
 */
public class SmallPaintBean {

    //小白板文件key
    private String currentTapKey;

    //小白板状态
    private String blackBoardState;

    //当前页 小白板就一页
    private int currentTapPage;


    public String getCurrentTapKey() {
        return currentTapKey;
    }

    public void setCurrentTapKey(String currentTapKey) {
        this.currentTapKey = currentTapKey;
    }

    public String getBlackBoardState() {
        return blackBoardState;
    }

    public void setBlackBoardState(String blackBoardState) {
        this.blackBoardState = blackBoardState;
    }

    public int getCurrentTapPage() {
        return currentTapPage;
    }

    public void setCurrentTapPage(int currentTapPage) {
        this.currentTapPage = currentTapPage;
    }

}

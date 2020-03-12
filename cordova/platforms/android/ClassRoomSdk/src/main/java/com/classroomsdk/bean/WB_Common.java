package com.classroomsdk.bean;

//import com.alibaba.fastjson.annotation.JSONField;

/**
 * 外层包装
 * @param <T>
 */
public class WB_Common<T> {

    //自定义id
    private String codeID;
    //动作类型
    private String eventType;
    //动作名称
    private String actionName;
    //消息ID
    private String shapeId;
    //白板id
    private String whiteboardID;
    //发出者名称
    private String nickname;
    //是否是基础白板
    private boolean isBaseboard;


    private T data;

    public WB_Common() {

    }


    public String getCodeID() {
        return codeID;
    }

    public void setCodeID(String codeID) {
        this.codeID = codeID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getWhiteboardID() {
        return whiteboardID;
    }

    public void setWhiteboardID(String whiteboardID) {
        this.whiteboardID = whiteboardID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public boolean isBaseboard() {
        return isBaseboard;
    }

    public void setBaseboard(boolean baseboard) {
        isBaseboard = baseboard;
    }

    @Override
    public String toString() {
        return "{" +
                "\"codeID\":\"" + codeID + '\"' +
                ",\"eventType\":\"" + eventType + '\"' +
                ",\"actionName\":\"" + actionName + '\"' +
                ",\"shapeId\":\"" + shapeId + '\"' +
                ",\"whiteboardID\":\"" + whiteboardID + '\"' +
                ",\"nickname\":\"" + nickname + '\"' +
                ",\"isBaseboard\":" + isBaseboard +
                ",\"data\":" + data +
                '}';
    }

}

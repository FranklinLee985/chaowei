package com.classroomsdk.bean;

import java.util.List;

/**
 * 钢笔 等
 */
public class WB_LinePathBean {

    /**
     * order : 3
     * tailSize : 3
     * smooth : true
     * pointCoordinatePairs : [[1081,805],[1081,814],[1093,833],[1113,857],[1140,881],[1171,905],[1205,928],[1221,937],[1231,941],[1235,943],[1237,945]]
     * pointSize : 5
     * pointColor : #eb070e
     */

    private int order;
    private int tailSize;
    private boolean smooth;
    private int pointSize;
    private String pointColor;
    private List<List<Float>> pointCoordinatePairs;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getTailSize() {
        return tailSize;
    }

    public void setTailSize(int tailSize) {
        this.tailSize = tailSize;
    }

    public boolean isSmooth() {
        return smooth;
    }

    public void setSmooth(boolean smooth) {
        this.smooth = smooth;
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        this.pointSize = pointSize;
    }

    public String getPointColor() {
        return pointColor;
    }

    public void setPointColor(String pointColor) {
        this.pointColor = pointColor;
    }


    public List<List<Float>> getPointCoordinatePairs() {
        return pointCoordinatePairs;
    }

    public void setPointCoordinatePairs(List<List<Float>> pointCoordinatePairs) {
        this.pointCoordinatePairs = pointCoordinatePairs;
    }

    @Override
    public String toString() {
        return "{" +
                "\"order\":" + order +
                ",\"tailSize\":" + tailSize +
                ",\"smooth\":" + smooth +
                ",\"pointSize\":" + pointSize +
                ",\"pointColor\":\"" + pointColor + '\"' +
                ",\"pointCoordinatePairs\":" + pointCoordinatePairs +
                '}';
    }

}

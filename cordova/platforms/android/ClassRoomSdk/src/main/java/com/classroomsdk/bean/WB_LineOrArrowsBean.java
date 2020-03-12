package com.classroomsdk.bean;

import java.util.Arrays;

/**
 * 箭头和直线
 */
public class WB_LineOrArrowsBean {

    /**
     * 坐标
     */
    private double x1;
    private double x2;
    private double y1;
    private double y2;

    /**
     * 线条
     */
    private int strokeWidth;

    /**
     * 颜色
     */
    private String color;

    /**
     *
     */
    private String capStyle;


    private String dash;

    /**
     * 结尾类型
     */
    private String[] endCapShapes;


    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCapStyle() {
        return capStyle;
    }

    public void setCapStyle(String capStyle) {
        this.capStyle = capStyle;
    }

    public String getDash() {
        return dash;
    }

    public void setDash(String dash) {
        this.dash = dash;
    }

    public String[] getEndCapShapes() {
        return endCapShapes;
    }

    public void setEndCapShapes(String[] endCapShapes) {
        this.endCapShapes = endCapShapes;
    }

    @Override
    public String toString() {
        return "{" +
                "\"x1\":" + x1 +
                ",\"x2\":" + x2 +
                ",\"y1\":" + y1 +
                ",\"y2\":" + y2 +
                ",\"strokeWidth\":" + strokeWidth +
                ",\"color\":\"" + color + '\"' +
                ",\"capStyle\":\"" + capStyle + '\"' +
                ",\"dash\":\"" + dash + '\"' +
                ",\"endCapShapes\":" + Arrays.toString(endCapShapes) +
                '}';
    }
}

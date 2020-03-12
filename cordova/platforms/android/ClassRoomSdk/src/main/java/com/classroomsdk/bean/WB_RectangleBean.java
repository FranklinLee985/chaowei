package com.classroomsdk.bean;

/**
 * 矩形等
 */
public class WB_RectangleBean {


    /**
     * x : 422.54163814738763
     * y : 162.3089264006531
     * width : 1171.8001368925393
     * height : 481.8617385352498
     * strokeWidth : 5
     * strokeColor : #ED3E3A
     * fillColor : #ED3E3A
     */

    private double x;
    private double y;
    private double width;
    private double height;
    private int strokeWidth;
    private String strokeColor;
    private String fillColor;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

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

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    @Override
    public String toString() {
        return "{" +
                "\"x\":" + x +
                ",\"y\":" + y +
                ",\"width\":" + width +
                ",\"height\":" + height +
                ",\"strokeWidth\":" + strokeWidth +
                ",\"strokeColor\":\"" + strokeColor + '\"' +
                ",\"fillColor\":\"" + fillColor + '\"' +
                '}';
    }
}

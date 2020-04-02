package com.classroomsdk.bean;

public class WB_TextBean {


    /**
     * x : 442.61921058635636
     * y : 217.0659421432951
     * text : 测试

     * color : #c12723
     * font : normal normal 18px 微软雅黑
     * forcedWidth : 0
     * forcedHeight : 0
     * v : 1
     */

    private float x;
    private float y;
    private String text;
    private String color;
    private String font;
    private int forcedWidth;
    private int forcedHeight;
    private int v;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getForcedWidth() {
        return forcedWidth;
    }

    public void setForcedWidth(int forcedWidth) {
        this.forcedWidth = forcedWidth;
    }

    public int getForcedHeight() {
        return forcedHeight;
    }

    public void setForcedHeight(int forcedHeight) {
        this.forcedHeight = forcedHeight;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "{" +
                "\"x\":" + x +
                ",\"y\":" + y +
                ",\"text\":\"" + text + '\"' +
                ",\"color\":\"" + color + '\"' +
                ",\"font\":\"" + font + '\"' +
                ",\"forcedWidth\":" + forcedWidth +
                ",\"forcedHeight\":" + forcedHeight +
                ",\"v\":" + v +
                '}';
    }

}

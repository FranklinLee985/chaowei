package com.classroomsdk.utils;


public class ColorUtils {

    /**
     * 0xff000000 类型转 RGB
     * @param color
     * @return
     */
    public static String toRGB(int color){
        int alpha = (color & 0xff000000) >>> 24;
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);
        String hex = String.format("#%02X%02X%02X", red, green, blue);
        return hex;
    }


    public static Integer[] RGB(int color){
        int alpha = (color & 0xff000000) >>> 24;
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);
        Integer[] rgb = new Integer[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
        return rgb;
    }

    /**
     * RGB转16进制
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static String toHex(int red, int green, int blue){
        String hr = Integer.toHexString(red);
        String hg = Integer.toHexString(green);
        String hb = Integer.toHexString(blue);
        return "#" + hr + hg + hb;
    }


    public static String toHexArgb(int alpha,int red, int green, int blue){
        String hex = String.format("#%d%02X%02X%02X",alpha, red, green, blue);
//        String a = Integer.toHexString(alpha);
//        String hr = Integer.toHexString(red);
//        String hg = Integer.toHexString(green);
//        String hb = Integer.toHexString(blue);
        return hex;
    }

}

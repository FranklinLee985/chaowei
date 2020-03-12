package com.classroomsdk;

/**
 * Created by Administrator on 2017/8/29.
 */

public class Config {

    public static boolean isWhiteBoardTest = false;   //H5调试指向
    public static boolean isWhiteVideoBoardTest = false;  //mp4视频标注

    public static String REQUEST_HEADER = "http://";

    //画笔颜色
    public static String[] mColor = new String[]{"#000000", "#9B9B9B", "#FFFFFF", "#FF87A3", "#FF515F", "#FF0000", "#E18838",
            "#AC6B00", "#864706", "#FF7E0B", "#FFD33B", "#FFF52B", "#B3D330", "#88BA44", "#56A648", "#53B1A4",
            "#68C1FF", "#058CE5", "#0B48FF", "#C1C7FF", "#D25FFA", "#6E3087", "#3D2484", "#142473"};


    //Titan提供的iD 和端口
    public static final int CustomerID = 0xA5CD213A;
    public static final int port = 29987;

    //网宿CDN
    public static final String url = "http://61.54.85.18/v1/httpdns/clouddns?ws_domain=www.chinanetcenter.com&ws_ret_type=json";


}

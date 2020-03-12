package com.classroomsdk.bean;


import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;

import java.util.ArrayList;

/**画笔类
 * @author
 */
public class TL_PadAction {


    public enum factoryType {
        ft_markerPen,//标记
        ft_arrowLine,//箭头
        ft_line,     //线
        ft_Rectangle,//矩形
        ft_Ellipse, //椭圆
        ft_Text,  //文本
        ft_Eraser;  //橡皮擦

        public static factoryType valueOf(int optInt) {
            // TODO Auto-generated method stub
            switch (optInt) {
                case 0:
                    return ft_markerPen;
                case 1:
                    return ft_arrowLine;
                case 2:
                    return ft_line;
                case 3:
                    return ft_Rectangle;
                case 4:
                    return ft_Ellipse;
                case 5:
                    return ft_Text;
                case 6:
                    return ft_Eraser;
            }
            return null;
        }

    }

    //hashcode
    public String sID;
    //画笔能绘制
    public boolean isDraw;

	//是否是小白板基础数据
	public boolean baseboard;

	//0: default  1: 小白板  2 ： 视频标注
	public int boardType;
	//清除
    public int mClear;

    //白板类型
    public String whiteboardID;


    //消息id
    public String id;

    //用户id
    public String fromID = "";

    //全屏清除动作标识
    public String clearActionId = "";


    //文档id
    public String nDocID;

    //页码id
    public String nPage;

    //模式
    public factoryType nActionMode;

    //模型宽度
    public int nPenWidth;
    //模型颜色
    public int nPenColor;

    //文字显示区域宽度  可能存在换行
    public int nTextWidth;
    //画笔用户
    public String nickname;

    //用于是否判断该画笔是否显示画画人名称
    public boolean inList;

    /**
     * alActionPoint
     */
    //画笔在当前view比值
    public ArrayList<PointF> alActionPoint = new ArrayList<PointF>();
    //三端统一坐标值
    public ArrayList<PointF> points = new ArrayList<PointF>();

    public String sText = "";

    //模型类型 （空心 实心）
    public boolean bIsFill = true;


    public boolean bIsRelative;

    /////////////////////////////////////////////////////
    public PointF ptSizingEnd;

    public PointF ptSizingEndPointf;

    public Rect CoverArea;

    public Region HotRegion;//区域(由一个或多个Rect组成)

    public boolean bSelect;

    public Path LinePath;  // nActionMode = 1
    ///////////////////////////////////////////////////////////////////

    //判断是否是新的笔画
    public boolean isNew = true;
}

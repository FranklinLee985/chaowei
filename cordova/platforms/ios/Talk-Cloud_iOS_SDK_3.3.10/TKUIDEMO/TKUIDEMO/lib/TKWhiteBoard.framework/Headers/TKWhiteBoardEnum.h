//
//  TKWhiteBoardEnum.h
//  TKWhiteBoard
//
//  Created by Yi on 2019/3/17.
//  Copyright © 2019年 MAC-MiNi. All rights reserved.
//

#ifndef TKWhiteBoardEnum_h
#define TKWhiteBoardEnum_h

typedef enum
{
    TKEventShowPage        = 0,    //增加文档
    TKEventShapeAdd        = 2,    //增加画笔
    TKEventShapeClean      = 5,    //清屏
    TKEventShapeUndo       = 6,    //撤回
    TKEventShapeRedo       = 7,    //重做
} TKEvent;
typedef NS_ENUM(NSInteger, TKNativeToolType)
{
    TKNativeToolTypeMouse   = 100,
    TKNativeToolTypeLine    = 10,
    TKNativeToolTypeText    = 20,
    TKNativeToolTypeShape   = 30,
    TKNativeToolTypeEraser  = 50,
};
enum selectButtonIndex
{
    Draw_Pen            = 10,       //画笔
    Draw_MarkPen        = 11,       //记号笔
    Draw_Line           = 12,       //直线
    Draw_Arrow          = 13,       //带箭头的直线
    
    Draw_EmptyRect      = 30,       //空心矩形
    Draw_SolidRect      = 31,       //实心矩形
    Draw_EmptyCircle    = 32,       //空心圆
    Draw_SolidCircle    = 33,       //实心圆
    
    Draw_Text_Size      = 20, //文字
    Draw_Text_Color     = 21,
    
    Draw_Edite_Select   = 40,//
    Draw_Edite_Delete   = 41,//
    Draw_Edite_Move     = 42,
    Draw_Edite_Clear    = 43,//
    
    Draw_Eraser         = 50,       //橡皮擦
    
    
    Draw_Undo           = 26,       //撤销
    Draw_Redo           = 27,       //重做
};

typedef enum
{
    TKWorkModeViewer = 0,       //只能观看 不能标记 隐藏工具条
    TKWorkModeControllor = 1,   //操作状态
} TKWorkMode;

typedef NS_ENUM(NSInteger, TKDrawType)
{
    TKDrawTypePen               = 10,    //钢笔
    TKDrawTypeMarkPen           = 11,    //记号笔
    TKDrawTypeLine              = 12,    //直线
    TKDrawTypeArrowLine         = 13,    //箭头
    
    TKDrawTypeTextMS            = 20,    //微软雅黑字
    TKDrawTypeTextSong          = 21,    //宋体字
    TKDrawTypeTextArial         = 22,    //Arial字
    
    TKDrawTypeEmptyRectangle    = 30,    //空心矩形
    TKDrawTypeFilledRectangle   = 31,    //实心矩形
    TKDrawTypeEmptyEllipse      = 32,    //空心圆
    TKDrawTypeFilledEllipse     = 33,    //实心圆
    
    TKDrawTypeEraser            = 50,    //橡皮擦
};

typedef NS_ENUM(NSInteger, TKBrushToolType)
{
    TKBrushToolTypeMouse   = 100,
    TKBrushToolTypeLine    = 10,
    TKBrushToolTypeText    = 20,
    TKBrushToolTypeShape   = 30,
    TKBrushToolTypeEraser  = 50,
};

typedef NS_ENUM(NSUInteger, TKWhiteBoardErrorCode) {
    TKError_OK,
    TKError_Bad_Parameters,
};
#endif /* TKWhiteBoardEnum_h */

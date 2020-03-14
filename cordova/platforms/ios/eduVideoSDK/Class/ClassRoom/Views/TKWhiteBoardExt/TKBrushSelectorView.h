//
//  TKNativeWBSelectorView.h
//  WhiteBoardTools
//
//  Created by 周洁 on 2018/12/25.
//  Copyright © 2018 周洁. All rights reserved.
//	画笔

#import <UIKit/UIKit.h>
#import "TKBrushToolView.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, TKSelectorShowType)
{
    TKSelectorShowTypeHigh,         //包含类型选择，颜色选择，大小选择
    TKSelectorShowTypeMiddle,       //包含颜色选择，大小选择
    TKSelectorShowTypeLow,          //只有大小选择
    TKSelectorShowTypeSpecial,      //只有颜色选择(文字的特殊配置项)
};

//typedef NS_ENUM(NSInteger, TKSelectorType)
//{
//    TKSelectorTypePen               = 10,    //钢笔
//    TKSelectorTypeMarkPen           = 11,    //记号笔
//    TKSelectorTypeLine              = 12,    //直线
//    TKSelectorTypeArrowLine         = 13,    //箭头
//
//    TKSelectorTypeTextMS            = 20,    //微软雅黑字
//    TKSelectorTypeTextSong          = 21,    //宋体字
//    TKSelectorTypeTextArial         = 22,    //Arial字
//    
//    TKSelectorTypeEmptyRectangle    = 30,    //空心矩形
//    TKSelectorTypeFilledRectangle   = 31,    //实心矩形
//    TKSelectorTypeEmptyEllipse      = 32,   //空心圆
//    TKSelectorTypeFilledEllipse     = 33,   //实心圆
//    
//    TKSelectorTypeEraser            = 50,   //橡皮擦
//};

@protocol TKBrushSelectorViewDelegate <NSObject>

- (void)brushSelectorViewDidSelectDrawType:(TKDrawType)type
                                     color:(NSString *)hexColor
                             widthProgress:(float)progress;

@end

@interface TKBrushSelectorView : UIView

@property (nonatomic, weak) id<TKBrushSelectorViewDelegate> delegate;

@property (nonatomic, assign) TKDrawType drawType;
@property (nonatomic, strong) NSString *currentColor;
@property (nonatomic, assign) CGFloat progress1;
@property (nonatomic, assign) TKBrushToolType type;

- (void)showType:(TKSelectorShowType)type;

- (void)setType:(TKBrushToolType)type;
//- (TKBrushToolType)getType;

// 显示在白板
- (void)showWithTKNativeWBToolView:(TKBrushToolView *)view type:(TKBrushToolType)type;

// 小白板
- (void)showOnMiniWhiteBoardAboveView:(UIView *)view type:(TKBrushToolType)type;

// 视频标注
- (void)showOnMediaMarkViewRightToView:(UIView *)view type:(TKBrushToolType)type;

- (void)hide;

- (instancetype)initWithDefaultColor:(nullable NSString *)color;
@end

NS_ASSUME_NONNULL_END

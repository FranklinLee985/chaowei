//
//  TKVideoPopupMenu.h
//  EduClass
//
//  Created by lyy on 2018/4/23.
//  Copyright © 2018年 talkcloud. All rights reserved.
//
#import <UIKit/UIKit.h>
#import "TKPopupMenuPath.h"

// 过期提醒
#define YBDeprecated(instead) NS_DEPRECATED(2_0, 2_0, 2_0, 2_0, instead)

NS_ASSUME_NONNULL_BEGIN

/**
 箭头方向优先级

 当控件超出屏幕时会自动调整成反方向
 */
typedef NS_ENUM(NSInteger , YBPopupMenuPriorityDirection) {
    TKPopupMenuPriorityDirectionTop = 0,  //Default
    TKPopupMenuPriorityDirectionBottom,
    TKPopupMenuPriorityDirectionLeft,
    TKPopupMenuPriorityDirectionRight,
    TKPopupMenuPriorityDirectionNone      //不自动调整
};

@class TKVideoPopupMenu;
@protocol TKVideoPopupMenuDelegate <NSObject>

@optional
- (void)ybPopupMenuBeganDismiss;//menu关闭开始
- (void)ybPopupMenuDidDismiss;//menu关闭完成
- (void)ybPopupMenuBeganShow;//menu展示开始
- (void)ybPopupMenuDidShow;//menu展示完成


-(void)videoPopupMenuCanDraw:(UIButton *)aButton;//控制画笔
-(void)videoPopupMenuUnderPlatform:(UIButton *)aButton;//关闭音频 下讲台
-(void)videoPopupMenuControlAudio:(UIButton *)aButton;//关闭音频
-(void)videoPopupMenuSendGif:(UIButton *)aButton;//发送奖杯
-(void)videoPopupMenuControlVideo:(UIButton *)aButton;//关闭视频
-(void)videoSplitScreenVideo:(UIButton *)aButton;//演讲
-(void)videoPopupMenuControlRestorePosition:(UIButton *)aButton;//恢复位置
-(void)videoPopupMenuControlRestoreAll:(UIButton *)aButton;//全部恢复
-(void)didPressChangeButton;


@end

@interface TKVideoPopupMenu : UIView

/** *  当前的用户 */
@property (strong, nonatomic) TKRoomUser *_Nullable iRoomUser;
@property (assign, nonatomic) BOOL isSplit;
@property (assign, nonatomic) BOOL isSpeaker;

/**
 圆角半径 Default is 5.0
 */
@property (nonatomic, assign) CGFloat cornerRadius;

/**
 自定义圆角 Default is UIRectCornerAllCorners
 
 当自动调整方向时corner会自动转换至镜像方向
 */
@property (nonatomic, assign) UIRectCorner rectCorner;

/**
 是否显示阴影 Default is YES
 */
@property (nonatomic, assign , getter=isShadowShowing) BOOL isShowShadow;

/**
 是否显示灰色覆盖层 Default is YES
 */
@property (nonatomic, assign) BOOL showMaskView;

/**
 选择菜单项后消失 Default is YES
 */
@property (nonatomic, assign) BOOL dismissOnSelected;

/**
 点击菜单外消失  Default is YES
 */
@property (nonatomic, assign) BOOL dismissOnTouchOutside;


/**
 设置偏移距离 (>= 0) Default is 0.0
 */
@property (nonatomic, assign) CGFloat offset;

/**
 边框宽度 Default is 0.0
 
 设置边框需 > 0
 */
@property (nonatomic, assign) CGFloat borderWidth;

/**
 边框颜色 Default is LightGrayColor
 
 borderWidth <= 0 无效
 */
@property (nonatomic, strong) UIColor * borderColor;

/**
 箭头宽度 Default is 15
 */
@property (nonatomic, assign) CGFloat arrowWidth;

/**
 箭头高度 Default is 10
 */
@property (nonatomic, assign) CGFloat arrowHeight;

/**
 箭头位置 Default is center
 
 只有箭头优先级是YBPopupMenuPriorityDirectionLeft/YBPopupMenuPriorityDirectionRight/YBPopupMenuPriorityDirectionNone时需要设置
 */
@property (nonatomic, assign) CGFloat arrowPosition;

/**
 箭头方向 Default is YBPopupMenuArrowDirectionTop
 */
@property (nonatomic, assign) YBPopupMenuArrowDirection arrowDirection;

/**
 箭头优先方向 Default is YBPopupMenuPriorityDirectionTop
 
 当控件超出屏幕时会自动调整箭头位置
 */
@property (nonatomic, assign) YBPopupMenuPriorityDirection priorityDirection;


/**
 menu背景色 自定义cell时忽略 Default is WhiteColor
 */
@property (nonatomic, strong) UIColor * backColor;

/**
 item的高度 Default is 44;
 */
@property (nonatomic, assign) CGFloat itemHeight;

/**
 popupMenu距离最近的Screen的距离 Default is 10
 */
@property (nonatomic, assign) CGFloat minSpace;

/**
 代理
 */
@property (nonatomic, weak) id <TKVideoPopupMenuDelegate> delegate;


/**
  依赖指定view弹出

 @param view 指定view
 @param roomUser 用户信息
 @param delegate 代理
 @return 视图
 */

+ (TKVideoPopupMenu *)showRelyOnView:(UIView *)view
                            aRoomUer:(TKRoomUser*)roomUser
                             isSplit:(BOOL)isSplit
                           isSpeaker:(BOOL)isSpeaker
                            delegate:(id<TKVideoPopupMenuDelegate>)delegate;


/**
 消失
 */
- (void)dismiss;

NS_ASSUME_NONNULL_END

@end

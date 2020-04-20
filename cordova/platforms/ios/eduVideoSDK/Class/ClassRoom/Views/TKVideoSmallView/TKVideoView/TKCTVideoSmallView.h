//
//  TKCTVideoSmallView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/12.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN
@class TKEduSessionHandle,RoomUser ,TKCTVideoSmallView;

@interface TKCTVideoSmallView : UIView

@property (assign,nonatomic) TKVideoViewMode  videoMode;
@property (assign,nonatomic) TKMaskViewLayout maskLayout;

//用于播放视频的view
@property (strong,nonatomic) UIView *videoView;

@property(assign,nonatomic)BOOL isDrag;/** *  是否拖拽出去了 */
@property(assign,nonatomic)BOOL isSplit;/** *  是否分屏 */
@property(assign,nonatomic)BOOL isPicInPic;/** * 是否是画中画 */
@property(assign,nonatomic)BOOL isNeedFunctionButton;/** *  授权等点击事件 */
@property(assign,nonatomic)BOOL isSpeaker;/** *  当前是否是主讲者 */
@property(weak,nonatomic) NSArray *videoArray;

/** *  当前的用户 */
@property(strong,nonatomic)TKRoomUser *_Nullable iRoomUser;
/** *  当前看的peerid */
@property(copy,nonatomic)NSString *_Nonnull iPeerId;

/** *  授权等点击事件 */
@property(strong,nonatomic)UIButton *_Nonnull iFunctionButton;

@property (nonatomic, copy) void(^ _Nullable splitScreenClickBlock)(void);//分屏回调

@property (nonatomic, assign) CGFloat originalWidth;			//设置原始宽度
@property (nonatomic, assign) CGFloat originalHeight;			//原始高度
@property (nonatomic, assign) CGFloat currentWidth;				//当前宽度
@property (nonatomic, assign) CGFloat currentHeight;			//当前高度
@property (nonatomic, assign) CGRect  whiteBoardViewFrame;		// 此属性可控制视图改变尺寸的范围
@property (nonatomic, assign) NSInteger iVideoViewTag;			/** *  视频tag */

// 初始化
-(nonnull instancetype)initWithFrame:(CGRect)frame;

/**
 更改用户名
 
 @param aName 用户名
 */
-(void)changeName:(NSString *_Nullable)aName;

/**
 隐藏弹出框
 */

- (void)clearVideoData;


/**
 进入后台

 @param isInBackground 是否进入后台
 */
- (void)endInBackGround:(BOOL)isInBackground;

/**
 缩放视频窗口
 
 @param scale 缩放比例
 */
- (void)changeVideoSize:(CGFloat)scale inFrame:(CGRect)rect;

/**
 隐藏视频菜单弹出框
 */
- (void)hidePopMenu;

/**
 隐藏小视频上的按钮
 
 @param isShow 是否显示
 */
- (void)maskViewChangeForPicInPicWithisShow:(BOOL)isShow;


/**
 清理
 */
- (void)removeAllObserver;


/**
 隐藏遮罩

 @param state 状态
 */
- (void)hideMaskView:(BOOL)state;
@end

NS_ASSUME_NONNULL_END

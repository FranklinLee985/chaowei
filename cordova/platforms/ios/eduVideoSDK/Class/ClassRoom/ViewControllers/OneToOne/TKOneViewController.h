//
//  TKCTOneViewController.h
//  EduClass
//
//  Created by talkcloud on 2018/10/9.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKBaseViewController.h"
#import "TKImagePickerController.h"

#import "TKEduSessionHandle.h"
#import "TKMediaDocModel.h"

#import "TKPlaybackMaskView.h"
#import "TKUploadImageView.h"
#import "TKCTNavView.h"
#import "TKCTBaseMediaView.h"
#import "TKCTVideoSmallView.h"
#import "TKNativeWBPageControl.h"
//相册/相机
#import <AssetsLibrary/AssetsLibrary.h>
#import <Photos/Photos.h>
#import "TKNewChatView.h"
#import "TKMiniWhiteBoardView.h"
#import "TKScreenShotFactory.h"

@interface TKOneViewController : TKBaseViewController
{
     // 需要移动的视频
    TKCTVideoSmallView *moveView;
}

//Handle处理类
@property (nonatomic, strong) TKEduSessionHandle *iSessionHandle;

/** controller */
@property (nonatomic, strong) TKImagePickerController *iPickerController;

/** View */
//白板视图
@property (nonatomic, strong) UIView *iTKEduWhiteBoardView;
@property (nonatomic, strong) UIView *whiteboardBackView;

//回放
@property (nonatomic, strong) TKPlaybackMaskView *playbackMaskView;
//图片上传view
@property (nonatomic, strong) TKUploadImageView  *uploadImageView;
//导航
@property (nonatomic, strong) TKCTNavView        *navbarView;
//媒体流
@property (nonatomic, strong) TKCTBaseMediaView	*iMediaView;
//共享电影
@property (nonatomic, strong) TKCTBaseMediaView	*iFileView;
//共享桌面
@property (nonatomic, strong) TKCTBaseMediaView *iScreenView;
//房间属性
@property (nonatomic, strong) TKRoomJsonModel     *roomJson;

//当前身份
@property (nonatomic, assign) TKUserRoleType iUserType;

// 收到远端的信令  全屏
@property (nonatomic, assign) BOOL isRemoteFullScreen;
/** 其他 */
//课件的高宽比 如：3/4.0  9/16.0  注意是课件高：宽
@property (nonatomic, assign) CGFloat whiteBoardRatio;

// 课件的高宽比
@property (nonatomic, assign) CGFloat coursewareRatio;


//navbar高度
@property (nonatomic, assign) NSInteger navbarHeight;
//图片上传进度
@property (nonatomic, assign) float   progress;
@property (nonatomic, strong) TKNewChatView *chatViewNew;//新聊天视图
@property (nonatomic, strong) TKNativeWBPageControl *pageControl;
@property (nonatomic, strong) TKMiniWhiteBoardView *miniWB;//小白板
@property (nonatomic, strong) TKBrushToolView *brushToolView; // 画笔工具
@property (assign , nonatomic) int mediaID;
@property (strong , nonatomic) NSString *currentMediaURL;
/************************************************************************/
//初始化直播对象
- (instancetype)initWithDelegate:(id<TKEduRoomDelegate>)aRoomDelegate
                       aParamDic:(NSDictionary *)aParamDic;

//初始化点播对象
- (instancetype)initPlaybackWithDelegate:(id<TKEduRoomDelegate>)aRoomDelegate
                               aParamDic:(NSDictionary *)aParamDic;

- (void)prepareForLeave:(BOOL)aQuityourself;


/**UI*/
-(void)refreshUI;

- (void)refreshVideoViewFrame;

/**清理所有数据*/
- (void)clearAllData;

/**画中画*/
- (void)changeVideoFrame:(BOOL)isFull;

/**图片上传取消*/
- (void)cancelUpload;

/**隐藏花名册和课件库*/
- (void)tapOnViewToHide;

//布局视图
- (void)layoutViews;

@end

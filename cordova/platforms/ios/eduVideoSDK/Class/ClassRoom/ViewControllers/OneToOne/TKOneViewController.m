//
//  TKCTOneViewController.m
//  EduClass
//
//  Created by talkcloud on 2018/10/9.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKOneViewController.h"
#import "TKEduSessionHandle.h"
#import "TKOneViewController+ImagePicker.h"
#import "TKOneViewController+Media.h"
#import "TKOneViewController+MediaMarkView.h"
#import "TKOneViewController+MiniWhiteBoard.h"
#import "TKOneViewController+Playback.h"
#import "TKOneViewController+WhiteBoard.h"
#import <objc/message.h>

#import "TKCTListView.h"     //文档、媒体、用户列表切换视图
#import "TKCTUserListView.h" //用户列表视图

// reconnection
#import "TKTimer.h"

#import "TKEduSessionHandle.h"

#import "TKChatMessageModel.h"

#import "sys/utsname.h"

#import "TKDocmentDocModel.h"
#import "TKMediaDocModel.h"
#import "TKProgressSlider.h"
#import <AVFoundation/AVFoundation.h>
#pragma mark 上传图片
#import "TKAnswerSheetView.h"
#import "TKBrushToolView.h"
#import "TKCTNetDetailView.h"
#import "TKDialView.h"
#import "TKPopView.h"
#import "TKStuTimerView.h"
#import "TKStylePopView.h"
#import "TKTimerView.h"
#import "TKUploadImageView.h"
#import "UIView+Drag.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <Photos/Photos.h>
#import <PhotosUI/PhotosUI.h>
#import "TKPopViewHelper.h"

@interface TKOneViewController () <
    TKEduBoardDelegate, TKEduSessionDelegate, UIGestureRecognizerDelegate, UIScrollViewDelegate,
    CAAnimationDelegate, UIImagePickerControllerDelegate, TKEduNetWorkDelegate,
    UINavigationControllerDelegate, TKPopViewDelegate>

{
    CGFloat _sStudentVideoViewHeigh;
    CGFloat _sStudentVideoViewWidth;
    CGFloat _whiteBoardTopBottomSpace; // 白板的上下间隔
    CGFloat _whiteBoardLeftRightSpace; // 左右间隔
    CGFloat _viewX;                    // 横屏x坐标(适配x+)
    CGRect videoOriginFrame;           // 画中画视频初始frame
    BOOL   _oneToOneDoubleDivisionModeTeacherOnLeft;//记录双师视频布局下老师视频的位置，YES为左边大视频位置，NO则为右上角小视频位置，避免切换左右位置后开关摄像头刷新布局
}
@property (nonatomic, assign) BOOL isConnect;
@property (nonatomic, assign) TKUIRoomType iRoomType; //当前房间类型

@property (nonatomic, strong) UIView *dimView; // 作用:点击空白视图 消失课件库 花名册

@property (nonatomic, strong) TKCTListView *listView;           //课件库
@property (nonatomic, strong) TKCTUserListView *userListView;   //控制按钮视图
@property (nonatomic, strong) TKCTNetDetailView *netDetailView; //网络质量

@property (nonatomic, assign) NSDictionary *iParamDic; // 加入房间参数

@property (nonatomic, strong) NSMutableDictionary *iPlayVideoViewDic; //播放的视频view的字典

@property (nonatomic, strong) TKCTVideoSmallView *iTeacherVideoView; //老师视频
@property (nonatomic, strong) TKCTVideoSmallView *iOurVideoView;     //自己的视频
@property (nonatomic, strong) TKCTVideoSmallView *sVideoView;        //双师小视频

@property (nonatomic, assign) BOOL isLocalPublish;
@property (nonatomic, assign) BOOL isRemindClassEnd;

@property (nonatomic, copy) NSString *currentServer;

@property (nonatomic, assign) BOOL isQuiting;

// 发生断线重连设置为YES，恢复后设置为NO
@property (nonatomic, assign) BOOL networkRecovered;

@property (nonatomic, assign) NSTimeInterval iLocalTime;
@property (nonatomic, assign) NSTimeInterval iClassStartTime;
@property (nonatomic, assign) NSTimeInterval iServiceTime;
@property (nonatomic, assign) NSTimeInterval iCurrentTime;                   // 当前时间
@property (nonatomic, assign) NSTimeInterval iHowMuchTimeServerFasterThenMe; // 时间差

@property (nonatomic, copy) NSString *iRoomName;

@property (nonatomic, strong) NSTimer *iAfterClassTimer;// 下课计时器
@property (nonatomic, strong) NSTimer *iClassBeginTimer;// 上课计时器
@property (nonatomic, strong) TKTimer *iCheckPlayVideotimer;
//视频的宽高属性
@property (nonatomic, strong) UILongPressGestureRecognizer *longPressGesture; // 本地移动视频手势

//视频
@property (nonatomic, weak) id<TKEduRoomDelegate> iRoomDelegate;
@property (nonatomic, assign) CGPoint iStrtCrtVideoViewP;

@property (nonatomic, strong) UILabel *replyText;
@property (nonatomic, assign) CGFloat knownKeyboardHeight;
@property (nonatomic, strong) NSArray *iMessageList;
@property (nonatomic, strong) TKStuTimerView *stuTimer; // 学生端计时器
@property (nonatomic, strong) TKTimerView *timerView;   // 计时器选择器
@property (nonatomic, strong) TKDialView *dialView;

@property (nonatomic, strong) UIView *splitScreenView; //分屏背景视图
                                                       //页面布局
@property (nonatomic, assign) TKRoomLayout viewStyle;
//交换视频按钮
@property (nonatomic, strong) UIButton *swapButton;
@property (nonatomic, strong) UIButton *showHideVideoBtn; // 显示隐藏小视频按钮
@property (nonatomic, strong) TKStylePopView *popview;    // 视频布局弹窗
@end

@implementation TKOneViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initCommon];

    //初始化导航栏
    [self initNavigation];

    //初始化聊天界面
    [self initMessageView];

    //初始化白板
    [self initWhiteBoardView];

    //初始化视频视图
    [self initVideoView];

    [self initTapGesTureRecognizer];

    [self.backgroundImageView bringSubviewToFront:_iTKEduWhiteBoardView];

    [self initAudioSession];

    //初始化白板控件
    [self initWhiteBoardNativeTool];

    [TKScreenShotFactory sharedFactory].contentView = _whiteboardBackView;
    [TKScreenShotFactory sharedFactory].brush       = self.brushToolView;

    // 如果是回放，那么放上遮罩页
    if (_iSessionHandle.isPlayback == YES) { [self initPlaybackMaskView]; }

}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];

    [self addNotification];

    [self createTimer];

    [self setNeedsStatusBarAppearanceUpdate];
}

- (void)viewDidAppear:(BOOL)animated {

    _iSessionHandle.UIDidAppear = YES;
    NSArray *array              = [_iSessionHandle.cacheMsgPool copy];

    for (NSDictionary *dic in array) {

        NSString *func = [TKUtil optString:dic Key:kTKMethodNameKey];

        SEL funcSel = NSSelectorFromString(func);

        NSMutableArray *params = [NSMutableArray array];
        if ([[dic allKeys] containsObject:kTKParameterKey]) { params = dic[kTKParameterKey]; }

        if ([func isEqualToString:NSStringFromSelector(@selector(sessionManagerRoomJoined))] ||
            [func isEqualToString:NSStringFromSelector(@selector(sessionManagerRoomLeft))] ||
            [func isEqualToString:NSStringFromSelector(@selector(networkTrouble))] ||
            [func isEqualToString:NSStringFromSelector(@selector(networkChanged))] ||
            [func isEqualToString:NSStringFromSelector(@selector(sessionManagerMediaLoaded))] ||
            [func
                isEqualToString:NSStringFromSelector(@selector(sessionManagerPlaybackClearAll))] ||
            [func isEqualToString:NSStringFromSelector(@selector(sessionManagerPlaybackEnd))]) {

            ((void (*)(id, SEL))objc_msgSend)(self, funcSel);

        } else if ([func isEqualToString:NSStringFromSelector(@selector
                                                              (sessionManagerDidOccuredWaring:))]) {

            TKRoomWarningCode code = (TKRoomWarningCode)[params.firstObject intValue];
            ((void (*)(id, SEL, TKRoomWarningCode))objc_msgSend)(self, funcSel, code);

        } else if ([func isEqualToString:NSStringFromSelector(@selector
                                                              (sessionManagerSelfEvicted:))]) {

            NSDictionary *dict = params.firstObject;
            ((void (*)(id, SEL, NSDictionary *))objc_msgSend)(self, funcSel, dict);

        } else if ([func
                       isEqualToString:NSStringFromSelector(@selector(
                                           sessionManagerPublishStateWithUserID:publishState:))]) {

            NSString *str        = params.firstObject;
            TKPublishState state = (TKPublishState)[params.lastObject intValue];
            ((void (*)(id, SEL, NSString *, TKPublishState))objc_msgSend)(self, funcSel, str,
                                                                          state);

        } else if ([func isEqualToString:NSStringFromSelector(
                                             @selector(sessionManagerUserJoined:InList:))]) {

            NSString *str = params.firstObject;
            BOOL inList   = [params.lastObject boolValue];
            ((void (*)(id, SEL, NSString *, BOOL))objc_msgSend)(self, funcSel, str, inList);

        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerAudioStateWithUserID:publishState:))]) {

            NSString *str        = params.firstObject;
            TKPublishState state = (TKPublishState)[params.lastObject intValue];
            ((void (*)(id, SEL, NSString *, TKPublishState))objc_msgSend)(self, funcSel, str,
                                                                          state);

        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerVideoStateWithUserID:publishState:))]) {

            NSString *str        = params.firstObject;
            TKPublishState state = (TKPublishState)[params.lastObject intValue];
            ((void (*)(id, SEL, NSString *, TKPublishState))objc_msgSend)(self, funcSel, str,
                                                                          state);

        } else if ([func
                       isEqualToString:NSStringFromSelector(@selector(sessionManagerUserLeft:))]) {

            NSString *str = params.firstObject;
            ((void (*)(id, SEL, NSString *))objc_msgSend)(self, funcSel, str);

        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerUserChanged:Properties:fromId:))]) {

            NSString *peerID         = params[0];
            NSDictionary *properties = params[1];
            NSString *fromId         = params[2];

            ((void (*)(id, SEL, NSString *, NSDictionary *, NSString *))objc_msgSend)(
                self, funcSel, peerID, properties, fromId);

        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerMessageReceived:fromID:extension:))]) {
            NSString *message    = params[0];
            NSString *peerID     = params[1];
            NSDictionary *fromId = params[2];

            ((void (*)(id, SEL, NSString *, NSString *, NSDictionary *))objc_msgSend)(
                self, funcSel, message, peerID, fromId);

        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerRoomManagerPlaybackMessageReceived:
                                             fromID:ts:extension:))]) {

            NSString *message  = params[0];
            TKRoomUser *user   = params[1];
            NSTimeInterval ts  = [params[2] doubleValue];
            NSDictionary *dict = params[3];
            ((void (*)(id, SEL, NSString *, TKRoomUser *, NSTimeInterval,
                       NSDictionary *))objc_msgSend)(self, funcSel, message, user, ts, dict);

        } else if ([func isEqualToString:NSStringFromSelector(@selector
                                                              (sessionManagerDidFailWithError:))]) {

            NSError *error = params[0];

            ((void (*)(id, SEL, NSError *))objc_msgSend)(self, funcSel, error);

        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerOnRemoteMsg:ID:Name:TS:Data:InList:))]) {
            BOOL add          = [params[0] boolValue];
            NSString *msgID   = params[1];
            NSString *msgName = params[2];
            unsigned long ts  = [params[3] unsignedIntValue];
            NSObject *data    = params[4];
            BOOL inlist       = [params[5] boolValue];

            ((void (*)(id, SEL, BOOL, NSString *, NSString *, unsigned long, NSObject *,
                       BOOL))objc_msgSend)(self, funcSel, add, msgID, msgName, ts, data, inlist);
        } else if ([func isEqualToString:NSStringFromSelector(@selector
                                                              (sessionManagerGetGiftNumber:))]) {
            dispatch_block_t completion = params[0];

            ((void (*)(id, SEL, dispatch_block_t))objc_msgSend)(self, funcSel, completion);
        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerOnShareMediaState:state:extensionMessage
                                                                            :))]) {
            NSString *peerId      = params[0];
            TKMediaState state    = (TKMediaState)[params[1] intValue];
            NSDictionary *message = params[2];
            ((void (*)(id, SEL, NSString *, TKMediaState, NSDictionary *))objc_msgSend)(
                self, funcSel, peerId, state, message);
        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerUpdateMediaStream:pos:isPlay:))]) {
            NSTimeInterval duration = [params[0] doubleValue];
            NSTimeInterval pos      = [params[1] doubleValue];
            BOOL isPlay             = [params[2] boolValue];

            ((void (*)(id, SEL, NSTimeInterval, NSTimeInterval, BOOL))objc_msgSend)(
                self, funcSel, duration, pos, isPlay);
        } else if ([func isEqualToString:NSStringFromSelector(@selector(
                                             sessionManagerOnShareFileState:state:extensionMessage
                                                                           :))]) {

            NSString *peerId      = params[0];
            TKMediaState state    = (TKMediaState)[params[1] intValue];
            NSDictionary *message = params[2];

            ((void (*)(id, SEL, NSString *, TKMediaState, NSDictionary *))objc_msgSend)(
                self, funcSel, peerId, state, message);
        }
        else if ([func isEqualToString:NSStringFromSelector(@selector(sessionManagerOnShareScreenState:state:))]) {
            NSString *peerId      = params[0];
            TKMediaState state    = (TKMediaState)[params[1] intValue];
            ((void (*)(id, SEL, NSString *, TKMediaState)) objc_msgSend)(self, funcSel, peerId, state);
            
        }
        else if ([func isEqualToString:NSStringFromSelector(
                                             @selector(sessionManagerReceivePlaybackDuration:))] ||
                   [func isEqualToString:NSStringFromSelector(
                                             @selector(sessionManagerPlaybackUpdateTime:))]) {
            NSTimeInterval duration = [params[0] doubleValue];

            ((void (*)(id, SEL, NSTimeInterval))objc_msgSend)(self, funcSel, duration);
        }
    }

    _iSessionHandle.cacheMsgPool = nil;
}

- (void)viewWillDisappear:(BOOL)animated {

    [super viewWillDisappear:animated];
    [self.chatViewNew removeSubviews];
    if (!_iPickerController) { [self invalidateTimer]; }
    [self removeNotificaton];
}

#pragma mark Pad 初始化

- (void)initAudioSession {

    //    AVAudioSession* session = [AVAudioSession sharedInstance];
    //    NSError* error;
    //    [session setCategory:AVAudioSessionCategoryPlayAndRecord
    //    withOptions:AVAudioSessionCategoryOptionMixWithOthers |
    //    AVAudioSessionCategoryOptionAllowBluetooth  error:&error]; [session
    //    setMode:AVAudioSessionModeVoiceChat error:nil]; [session setActive:YES
    //    withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:&error];
    //    [session overrideOutputAudioPort:AVAudioSessionPortOverrideNone error:&error];
    AVAudioSessionRouteDescription *route = [[AVAudioSession sharedInstance] currentRoute];
    for (AVAudioSessionPortDescription *desc in [route outputs]) {

        if ([[desc portType] isEqualToString:AVAudioSessionPortBuiltInReceiver]) {
            _iSessionHandle.isHeadphones = NO;
            _iSessionHandle.iVolume      = 1;
            //            [session overrideOutputAudioPort:AVAudioSessionPortOverrideSpeaker
            //            error:&error];
        } else {
            _iSessionHandle.isHeadphones = YES;
            _iSessionHandle.iVolume      = 0.5;
        }
    }
}

- (void)initNavigation {
    self.navigationController.navigationBar.hidden = YES;

    self.navbarView =
        [[TKCTNavView alloc] initWithFrame:CGRectMake(0, 0, ScreenW, self.navbarHeight)
                                 aParamDic:_iParamDic];
    [self.backgroundImageView addSubview:self.navbarView];
	
    tk_weakify(self);
    self.navbarView.leaveButtonBlock = ^{ //离开课堂 （返回)
        [weakSelf.view endEditing:YES];
        [weakSelf leftButtonPress];

    };
    self.navbarView.classBeginBlock = ^{ [weakSelf hiddenNavAlertView]; };
    self.navbarView.classoverBlock  = ^{ //下课
        TKLog(@"下课");
        [weakSelf hiddenNavAlertView];
        [weakSelf.iClassBeginTimer invalidate]; // 下课后计时器销毁
        weakSelf.iClassBeginTimer = nil;
    };
    // 花名册
    self.navbarView.memberButtonClickBlock = ^(UIButton *sender) {

        if (sender.selected) {

            weakSelf.navbarView.showRedDot = NO;
            [weakSelf hiddenNavAlertView];

            //花名册：宽 7/10  高 9/10
            CGFloat showHeight = ScreenH - TKNavHeight;
            CGFloat showWidth  = fmaxf(ScreenW * (6 / 10.0), 485);
            CGFloat x          = (ScreenW - showWidth) / 2.0;
            CGFloat y = weakSelf.navbarView.height;

            weakSelf.dimView = [[UIView alloc]
                initWithFrame:CGRectMake(0, y, weakSelf.view.width - showWidth, showHeight)];
            weakSelf.dimView.backgroundColor = UIColor.clearColor;
            UITapGestureRecognizer *tapG =
                [[UITapGestureRecognizer alloc] initWithTarget:weakSelf
                                                        action:@selector(tapOnViewToHide)];
            [weakSelf.dimView addGestureRecognizer:tapG];
            [weakSelf.view addSubview:weakSelf.dimView];

            weakSelf.userListView =
                [[TKCTUserListView alloc] initWithFrame:CGRectMake(x, y, showWidth, showHeight)
                                               userList:nil];
            [weakSelf.userListView show:weakSelf.view];
            weakSelf.userListView.dismissBlock = ^{
                weakSelf.navbarView.memberButton.selected = NO;
                weakSelf.userListView                     = nil;
            };

        } else {

            [weakSelf tapOnViewToHide];
        }
    };
    

    self.navbarView.coursewareButtonClickBlock = ^(UIButton *sender) {
        //课件库按钮
        if (sender.selected) {
            if (!weakSelf.listView) {

                [weakSelf hiddenNavAlertView];

                //文件列表：            宽 7/10  高 9/10
                CGFloat showHeight = ScreenH - TKNavHeight;
                CGFloat showWidth  = fmaxf(ScreenW * (6 / 10.0), 500);
                CGFloat x          = (ScreenW - showWidth) / 2.0;
                //                CGFloat y = (ScreenH-showHeight)/2.0;
                CGFloat y = weakSelf.navbarView.height;

                weakSelf.dimView = [[UIView alloc]
                    initWithFrame:CGRectMake(0, y, weakSelf.view.width - showWidth, showHeight)];
                weakSelf.dimView.backgroundColor = UIColor.clearColor;
                UITapGestureRecognizer *tapG =
                    [[UITapGestureRecognizer alloc] initWithTarget:weakSelf
                                                            action:@selector(tapOnViewToHide)];
                [weakSelf.dimView addGestureRecognizer:tapG];
                [weakSelf.view addSubview:weakSelf.dimView];

                weakSelf.listView =
                    [[TKCTListView alloc] initWithFrame:CGRectMake(x, y, showWidth, showHeight)
                                               andTitle:@"dd"
                                                   from:nil];
                [weakSelf.listView show:weakSelf.view];
                weakSelf.listView.dismissBlock = ^{

                    weakSelf.navbarView.coursewareButton.selected = NO;
                    weakSelf.listView                             = nil;
                };
            }
        } else {
            [weakSelf tapOnViewToHide];
        }
    };

    self.navbarView.controlButtonClickBlock = ^(UIButton *sender) { //控制视图

        [weakSelf hiddenNavBarViewActionView];

        TKPopView *popview  = [TKPopView showPopViewAddedTo:weakSelf.view pointingAtView:sender];
        popview.popViewType = TKPopViewType_AllControl;
        popview.delegate    = weakSelf;
    };

    self.navbarView.toolBoxButtonClickBlock = ^(UIButton *sender) {
        //点击工具箱不隐藏聊天
        [weakSelf hiddenNavBarViewActionView];
        TKPopView *popview  = [TKPopView showPopViewAddedTo:weakSelf.view pointingAtView:sender];
        popview.popViewType = TKPopViewType_ToolBox;
        popview.delegate    = weakSelf;
    };

    self.navbarView.netStateBlock = ^(CGFloat centerX) {

        [weakSelf hiddenNavBarViewActionView];
        weakSelf.netDetailView = [TKCTNetDetailView
            showDetailViewWithPoint:CGPointMake(centerX, TKNavHeight)
                               diss:^{

                                   [weakSelf.navbarView.netTipView changeDetailSignImage:NO];
                                   [weakSelf.netDetailView removeFromSuperview];
                                   weakSelf.netDetailView = nil;
                               }];
        [weakSelf.netDetailView changeDetailData:weakSelf.navbarView.netTipView.netState];
    };

    self.navbarView.styleButtonClickBlock = ^(UIButton *sender) {

        [weakSelf hiddenNavBarViewActionView];

        TKStylePopView *popview =
            [TKStylePopView showPopViewAddedTo:weakSelf.view pointingAtView:sender];
        popview.viewStyle = weakSelf.viewStyle;

    };
     
    
}
     
- (instancetype)initWithDelegate:(id<TKEduRoomDelegate>)aRoomDelegate
                       aParamDic:(NSDictionary *)aParamDic

{
    if (self = [self init]) {
        _iRoomDelegate = aRoomDelegate;
        _iParamDic     = aParamDic;
        _currentServer = [aParamDic objectForKey:@"server"];

        _iSessionHandle                  = [TKEduSessionHandle shareInstance];
        _iSessionHandle.isPlayback       = NO;
        _iSessionHandle.isSendLogMessage = YES;

        [_iSessionHandle setSessionDelegate:self aBoardDelegate:self];
    }
    return self;
}

// 回放初始化接口
- (instancetype)initPlaybackWithDelegate:(id<TKEduRoomDelegate>)aRoomDelegate
                               aParamDic:(NSDictionary *)aParamDic

{
    if (self = [self init]) {
        _iRoomDelegate = aRoomDelegate;
        _iParamDic     = aParamDic;
        _currentServer = [aParamDic objectForKey:@"server"];

        _iSessionHandle                  = [TKEduSessionHandle shareInstance];
        _iSessionHandle.isPlayback       = YES;
        _iSessionHandle.isSendLogMessage = NO;

        [_iSessionHandle configurePlaybackSession:aParamDic
                                    aRoomDelegate:aRoomDelegate
                                 aSessionDelegate:self
                                   aBoardDelegate:self];
    }
    return self;
}

//初始化
- (void)initCommon {
    self.backgroundImageView.contentMode = UIViewContentModeScaleToFill;
    self.backgroundImageView.sakura.image(@"ClassRoom.backgroundImageOne");

    _oneToOneDoubleDivisionModeTeacherOnLeft = YES;
    _isConnect        = NO;
    _networkRecovered = YES;
    _roomJson         = [TKEduClassRoom shareInstance].roomJson;
    _iUserType        = _roomJson.roomrole;
    _iRoomType        = _roomJson.roomtype;
    _viewX            = [TKUtil isiPhoneX] ? 44 : 0;
    //    self.viewStyle        = oneToOne;

    //课堂中的视频分辨率
    self.whiteBoardRatio = 3 / 4.0; // 视频框固定尺寸

    [TKHelperUtil setVideoFormat];

    self.navbarHeight = IS_IPHONE ? 45 : 60; // sH * 0.4;

    _longPressGesture = [[UILongPressGestureRecognizer alloc]
        initWithTarget:self
                action:@selector(fullScreenVideoLongPressClick:)];
    _longPressGesture.minimumPressDuration = 0.2f;

    // 位置计算
    [self calculateWhiteBoardVideoViewFrame];
    // tabbar view 去掉了
    _iSessionHandle.bottomHeight = 0;
}
// MARK: 初始化聊天界面
- (void)initMessageView {
    float _height = fmaxf(ScreenH * (6 / 10.0), 300);
    float _width  = ScreenW * (1 / 3.0);

    self.chatViewNew = [[TKNewChatView alloc] initWithFrame:CGRectMake(0, 0, _width, _height)];
    self.chatViewNew.x = 15;
    self.chatViewNew.y = self.view.height - _height - 30.0f / 768 * ScreenH;
    [self.view addSubview:self.chatViewNew];
    [self.chatViewNew setBadgeNumber:_iSessionHandle.unReadMessagesArray.count];
    __weak __typeof(self) __self            = self;
    self.chatViewNew.messageBtnClickBlock = ^(UIButton *_Nonnull sender) {
        if (sender.selected) {
            [__self hiddenNavAlertView];
            if (_iSessionHandle.unReadMessagesArray.count > 0) {
                [__self.iSessionHandle.unReadMessagesArray removeAllObjects];
            }
            [__self.chatViewNew setBadgeNumber:__self.iSessionHandle.unReadMessagesArray.count];

            // 解决录制问题 聊天窗口问题
            if (__self.iSessionHandle.localUser.role == TKUserType_Teacher &&
                __self.iSessionHandle.isClassBegin == YES) {

                [__self.iSessionHandle sessionHandlePubMsg:@"ChatShow"
                                                        ID:@"ChatShow"
                                                        To:sTellNone
                                                      Data:@{}
                                                      Save:YES
                                                completion:^(NSError *_Nonnull error){}];
            }
        } else {
            // 解决录制问题 聊天窗口问题
            if (__self.iSessionHandle.localUser.role == TKUserType_Teacher &&
                __self.iSessionHandle.isClassBegin == YES) {
                [__self.iSessionHandle sessionHandleDelMsg:@"ChatShow"
                                                        ID:@"ChatShow"
                                                        To:sTellNone
                                                      Data:@{}
                                                completion:nil];
            }
        }
    };
    //进教室的时候聊天窗口打开
    [self.chatViewNew hide:NO];

    [self.chatViewNew setUserRoleType:TKUserType_Student];
}
- (void)initVideoView {

    CGFloat tVideoX = ScreenW - _sStudentVideoViewWidth - (_viewX)-_whiteBoardLeftRightSpace;
    //    CGFloat tVideoX = CGRectGetMaxX(_whiteboardBackView.frame);

    CGFloat tVideoY = self.navbarHeight + _whiteBoardTopBottomSpace;

    //老师
    self.iTeacherVideoView = ({

        TKCTVideoSmallView *tTeacherVideoView = [[TKCTVideoSmallView alloc]
            initWithFrame:CGRectMake(tVideoX, tVideoY, _sStudentVideoViewWidth,
                                     _sStudentVideoViewHeigh)];
        tTeacherVideoView.whiteBoardViewFrame = _iTKEduWhiteBoardView.frame;
        tTeacherVideoView.iVideoViewTag       = -1;

        tTeacherVideoView;

    });

    [self.view addSubview:self.iTeacherVideoView];
    //学生
    self.iOurVideoView = ({

        TKCTVideoSmallView *tOurVideoView = [[TKCTVideoSmallView alloc]
            initWithFrame:CGRectMake(tVideoX, CGRectGetMaxY(self.iTeacherVideoView.frame),
                                     _sStudentVideoViewWidth, _sStudentVideoViewHeigh)];
        tOurVideoView.whiteBoardViewFrame = _iTKEduWhiteBoardView.frame;
        tOurVideoView.iVideoViewTag       = -2;

        tOurVideoView;

    });
    [self.view addSubview:self.iOurVideoView];

    // 1v1 显示对方, 1vM 显示老师, (巡课 1v1学生 1vM老师)
    moveView = _iUserType == TKUserType_Student ? _iTeacherVideoView : _iOurVideoView;

    //设置教室视频
    TKRoomJsonModel *model = [TKEduClassRoom shareInstance].roomJson;
    if (model.roomlayout == 52) {
        self.viewStyle = oneToOneDoubleDivision;
    } else if (model.roomlayout == 53) {
        self.viewStyle = oneToOneDoubleVideo;
    } else {
        self.viewStyle = oneToOne;
    }
}

- (void)initWhiteBoardView {

    CGFloat x      = _viewX + _whiteBoardLeftRightSpace;
    CGFloat y      = self.navbarHeight + _whiteBoardTopBottomSpace;
    CGFloat width  = ScreenW - _viewX - _sStudentVideoViewWidth - 2 * _whiteBoardLeftRightSpace;
    CGFloat height = 2 * _sStudentVideoViewHeigh;

    CGRect tFrame = CGRectMake(x, y, width, height);

    // 白板背景图
    _whiteboardBackView = [[UIView alloc] initWithFrame:tFrame];

    tFrame = CGRectMake(0, 0, width, height);

    _iTKEduWhiteBoardView =
        [_iSessionHandle.whiteBoardManager createWhiteBoardWithFrame:tFrame
                                                   loadComponentName:TKWBMainContentComponent
                                                   loadFinishedBlock:^{

                                                       // 白板加载完成可放处理方法
                                                   }];
    _iTKEduWhiteBoardView.backgroundColor = [UIColor clearColor];
    _iSessionHandle.whiteboardView = _iTKEduWhiteBoardView;

    [_whiteboardBackView addSubview:_iTKEduWhiteBoardView];
    [self.backgroundImageView addSubview:_whiteboardBackView];
}
// 创建翻页工具
- (void)initWhiteBoardNativeTool {
    _pageControl =
        [[TKNativeWBPageControl alloc] initWithHidePaging:_roomJson.configuration.isHiddenPageFlip
                                              allowPaging:_roomJson.configuration.canPageTurningFlag
                                                     role:_iUserType];
    _pageControl.whiteBoardControl = self;

    _whiteboardBackView.userInteractionEnabled = YES;
    [_whiteboardBackView addSubview:_pageControl];

    [_pageControl mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(_whiteboardBackView.mas_bottom).offset(0);
        make.centerX.equalTo(_whiteboardBackView.mas_centerX);
    }];

    [_pageControl setTotalPage:1 currentPage:1];

    tk_weakify(self);

    // 是否显示课件备注
    _pageControl.showMark =
        (_iUserType == TKUserType_Teacher && _roomJson.configuration.coursewareRemarkFlag &&
         _iSessionHandle.isPlayback == NO);

    if (_pageControl.showMark) {

        [_iSessionHandle.whiteBoardManager setPageControlMarkBlock:^(NSDictionary *dict) {
            weakSelf.pageControl.remarkDict = dict;
        }];
    }
}

- (TKMiniWhiteBoardView *)miniWB {
    if (!_miniWB) {
        _miniWB        = [[TKMiniWhiteBoardView alloc] init];
        _miniWB.hidden = YES;
        [_whiteboardBackView addSubview:_miniWB];
        if ((_whiteboardBackView.frame.size.width / _whiteboardBackView.frame.size.height) >=
            (16 / 9.0f)) {
            [_miniWB mas_makeConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(_whiteboardBackView.mas_height)
                    .multipliedBy(IS_PAD ? 447 / 567.0f : 0.8f)
                    .priorityHigh();
                make.centerX.equalTo(_whiteboardBackView.mas_centerX);
                make.centerY.equalTo(_whiteboardBackView.mas_centerY);
                //                make.top.equalTo(_whiteboardBackView.mas_top).offset(45 * (IS_PAD
                //                ? 1 : 0));
            }];
        } else {
            [_miniWB mas_makeConstraints:^(MASConstraintMaker *make) {
                make.width.equalTo(_whiteboardBackView.mas_width)
                    .multipliedBy(IS_PAD ? 447 / 567.0f : 0.8f)
                    .priorityHigh();
                make.centerX.equalTo(_whiteboardBackView.mas_centerX);
                make.centerY.equalTo(_whiteboardBackView.mas_centerY);
                //                make.top.equalTo(_whiteboardBackView.mas_top).offset(45 * (IS_PAD
                //                ? 1 : 0));
            }];
        }
        _miniWB.clipsToBounds = NO;
    }
    return _miniWB;
}

- (void)addNotification {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(whiteBoardFullScreen:)
                                                 name:sChangeWebPageFullScreen
                                               object:nil];
    /** 1.先设置为外放 */
    //    dispatch_async(dispatch_get_main_queue(), ^{
    //        [[AVAudioSession sharedInstance]
    //        overrideOutputAudioPort:AVAudioSessionPortOverrideSpeaker error:nil];
    //    });
    /** 2.判断当前的输出源 */
    // [self routeChange:nil];

    [[UIApplication sharedApplication] addObserver:self
                                        forKeyPath:@"idleTimerDisabled"
                                           options:NSKeyValueObservingOptionNew
                                           context:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(tapTable:)
                                                 name:sTapTableNotification
                                               object:nil];

    //拍摄照片、选择照片上传
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(uploadPhotos:)
                                                 name:sTakePhotosUploadNotification
                                               object:sTakePhotosUploadNotification];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(uploadPhotos:)
                                                 name:sChoosePhotosUploadNotification
                                               object:sChoosePhotosUploadNotification];

    //收到远端pubMsg消息通知
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(roomWhiteBoardOnRemotePubMsg:)
                                                 name:TKWhiteBoardOnRemotePubMsgNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(roomWhiteBoardOnRemoteDelMsg:)
                                                 name:TKWhiteBoardOnRemoteDelMsgNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(showPageBeforeClass)
                                                 name:sShowPageBeforeClass
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(recoveryAfterGetinClass:)
                                                 name:TKWhiteBoardOnRoomConnectedNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(leftButtonPress)
                                                 name:TKWhiteBoardPreloadExit
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(outPlayMedia:)
                                                 name:@"OUTPLAYMEDIA"
                                               object:nil];

}

- (void)recoveryAfterGetinClass:(NSNotification *)notification {
    [self miniWBRecoveryAfterGetinClass:notification];

    [self screenShotRecoveryAfterGetinClass:notification];

    [self mediaMarkRecoveryAfterGetinClass:notification];
}

- (void)showPageBeforeClass {
    [_pageControl resetBtnStates];
}

#pragma mark - fullScreen 白板全屏
- (void)whiteBoardFullScreen:(NSNotification *)aNotification {

    // 隐藏工具栏
    [self hiddenNavAlertView];

    bool isFull = [aNotification.object boolValue];

    //白板恢复时聊天界面收起状态

    _iSessionHandle.iIsFullState = isFull;

    // 视频全屏
    if (self.iMediaView || self.iFileView) {
        if (!self.iMediaView.hasVideo) {
            self.iMediaView.hidden = isFull;
        }

        else {
            return;
        }
    }
    self.iOurVideoView.hidden     = isFull;
    self.iTeacherVideoView.hidden = isFull;

    if (_iSessionHandle.roomLayout == oneToOneDoubleDivision) { _showHideVideoBtn.hidden = isFull; }
    // 白板全屏
    if (isFull) {

        _whiteboardBackView.frame =
            CGRectMake(_viewX, 0, [TKUtil isiPhoneX] ? ScreenW - 44 : ScreenW, ScreenH);
        _iTKEduWhiteBoardView.frame =
            CGRectMake(0, 0, _whiteboardBackView.width, _whiteboardBackView.height);

        [self.backgroundImageView bringSubviewToFront:self.whiteboardBackView];
        [self.iSessionHandle.whiteBoardManager refreshWhiteBoard];

        // 5月20日 余家峰要求更改：白板全屏时，将聊天内容显示出来
        [self.chatViewNew.superview bringSubviewToFront:self.chatViewNew];

    } else {

        [self.backgroundImageView sendSubviewToBack:self.whiteboardBackView];

        [self refreshUI];
    }

    [_navbarView hideAllButton:isFull];
}
- (void)restoreMp3ViewFrame {
    if (!self.iMediaView.hasVideo) {

        // mp3 view 老师带有进度条 frame不同
        if (CGRectGetWidth(_iMediaView.frame) == CGRectGetHeight(_iMediaView.frame)) {
            self.iMediaView.x = CGRectGetMinX(self.view.frame) + 10;
            self.iMediaView.y = self.whiteboardBackView.y + 5;
        } else {
            //老师
            self.iMediaView.x = CGRectGetMinX(self.view.frame) + 10;
            self.iMediaView.y = self.whiteboardBackView.y + 5;
        }
    }
}

- (void)changeMp3ViewFrame {

    // mp3 view 老师带有进度条 frame不同
    if (CGRectGetWidth(_iMediaView.frame) == CGRectGetHeight(_iMediaView.frame)) {
        self.iMediaView.frame =
            CGRectMake(CGRectGetMinX(self.view.frame) + 10,
                       CGRectGetMaxY(self.whiteboardBackView.frame) -
                           CGRectGetHeight(_iMediaView.frame) - (IS_PAD ? 60 : 40),
                       CGRectGetWidth(_iMediaView.frame), CGRectGetHeight(_iMediaView.frame));
    } else {
        //老师
        self.iMediaView.frame =
            CGRectMake(CGRectGetMinX(self.whiteboardBackView.frame) +
                           (CGRectGetWidth(self.iTKEduWhiteBoardView.frame) -
                            CGRectGetWidth(_iMediaView.frame)) /
                               2,
                       CGRectGetMaxY(self.whiteboardBackView.frame) -
                           CGRectGetHeight(_iMediaView.frame) - (IS_PAD ? 80 : 60),
                       CGRectGetWidth(_iMediaView.frame), CGRectGetHeight(_iMediaView.frame));
    }
}

- (void)refreshUI {

    if (self.iPickerController) { return; }
    [self refreshWhiteBoard:YES];
    [self refreshVideoViewFrame];
}

- (void)refreshVideoViewFrame {

    [self layoutViews];
}

- (void)calculateWhiteBoardVideoViewFrame {
    //白板的实际宽，高
    CGFloat whiteboardHeight = ScreenH - self.navbarHeight - 2 * 10;
    CGFloat whiteboardWidth  = whiteboardHeight / self.whiteBoardRatio;
    _sStudentVideoViewHeigh  = whiteboardHeight / 2;
    _sStudentVideoViewWidth  = _sStudentVideoViewHeigh / 0.75;

    if (whiteboardWidth + _sStudentVideoViewWidth > ScreenW) {

        whiteboardWidth         = ScreenW / (1 + 2 * self.whiteBoardRatio / 3);
        whiteboardHeight        = whiteboardWidth * self.whiteBoardRatio;
        _sStudentVideoViewHeigh = whiteboardHeight / 2;
        _sStudentVideoViewWidth = _sStudentVideoViewHeigh / 0.75;
    }

    CGFloat width             = ([TKUtil isiPhoneX] ? ScreenW - 44 : ScreenW);
    _whiteBoardLeftRightSpace = (width - whiteboardWidth - _sStudentVideoViewWidth) / 2;
    _whiteBoardTopBottomSpace = (ScreenH - whiteboardHeight - self.navbarHeight) / 2;
}

#pragma mark - 工具栏 点击事件
- (void)popView:(TKPopView *)popView didSelectRowAtIndexPath:(nonnull NSString *)actionID {
    [TKPopView dismissForView:self.view];

    switch (popView.popViewType) {
        case TKPopViewType_ToolBox: {
            if (actionID.integerValue == 1) { // 答题器

                NSString *msgID = [NSString stringWithFormat:@"Question_%@", _roomJson.roomid];
                [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"Question"
                                                                     ID:msgID
                                                                     To:sTellAll
                                                                   Data:@"{\"action\":\"open\"}"
                                                                   Save:YES
                                                             completion:nil];

                [[TKEduSessionHandle shareInstance]
                    sessionHandlePubMsg:@"AnswerDrag"
                                     ID:@"AnswerDrag"
                                     To:sTellAllExpectSender
                                   Data:@"{\"percentLeft\":0.5,\"percentTop\":0,\"isDrag\":true}"
                                   Save:YES
                             completion:nil];

            } else if (actionID.integerValue == 2) { // 转盘

                [[TKEduSessionHandle shareInstance]
                    sessionHandlePubMsg:@"dial"
                                     ID:@"dialMesg"
                                     To:sTellAll
                                   Data:@{
                                       @"rotationAngle" :
                                           [NSString stringWithFormat:@"rotate(0deg)"],
                                       @"isShow" : @"true"
                                   }
                                   Save:YES
                             completion:nil];

            } else if (actionID.integerValue == 3) { // 计时器

                // 计时器
                NSArray *timerArray = @[ @0, @5, @0, @0 ];

                NSDictionary *dataDic = @{
                    @"isStatus" : @NO,
                    @"sutdentTimerArry" : timerArray,
                    @"isShow" : @YES,
                    @"isRestart" : @NO
                };

                NSString *str = [TKUtil dictionaryToJSONString:dataDic];
                [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sTimer
                                                                     ID:@"timerMesg"
                                                                     To:sTellAll
                                                                   Data:str
                                                                   Save:true
                                                        AssociatedMsgID:nil
                                                       AssociatedUserID:nil
                                                                expires:0
                                                             completion:nil];

            } else if (actionID.integerValue == 5) { // 小白板

                //老师端点击小白板，prepare状态下老师直接显示
                NSDictionary * pubDict = @{
                                           @"blackBoardState" : @"_prepareing",
                                           @"currentTapKey" : @"blackBoardCommon",
                                           @"currentTapPage" : @(1)
                                           };
                [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"BlackBoard_new"
                                                                     ID:@"BlackBoard_new"
                                                                     To:sTellAll
                                                                   Data:pubDict Save:YES
                                                        AssociatedMsgID:sClassBegin
                                                       AssociatedUserID:nil
                                                                expires:0 completion:nil];
            }
            break;
        }
        case TKPopViewType_AllControl: {
            if (actionID.integerValue == 105) {
                // 纯音频教室
                [_iSessionHandle sessionHandleChangeAudioOnlyRoom];
            }
            break;
        }
    }
}

- (void)popViewWillHidden:(TKPopView *)popView {
    self.navbarView.toolBoxButton.selected = NO;
    self.navbarView.controlButton.selected = NO;
}

//- (void)popViewWasHidden:(TKPopView *)popView
//{
//    self.navbarView.toolBoxButton.selected = !self.navbarView.toolBoxButton.selected;
//}

- (void)tapOnViewToHide {
    if (self.listView) {
        [self.view addSubview:self.listView];
        self.navbarView.coursewareButton.selected = YES;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5f * NSEC_PER_SEC)),
                       dispatch_get_main_queue(),
                       ^{ self.navbarView.coursewareButton.selected = NO; });
        [self.listView hidden];
        self.listView = nil;
    }
    if (self.userListView) {
        [self.view addSubview:self.userListView];
        self.navbarView.memberButton.selected = YES;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5f * NSEC_PER_SEC)),
                       dispatch_get_main_queue(), ^{ self.navbarView.memberButton.selected = NO; });
        [self.userListView hidden];
        self.userListView = nil;
    }

    [_dimView removeFromSuperview];
    _dimView = nil;
}

- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    if (self.iMediaView) { [self.view bringSubviewToFront:self.iMediaView]; }
    if (self.iFileView) { [self.view bringSubviewToFront:self.iFileView]; }
    // fix bug:mp3挡住媒体库 花名册
    if (self.dimView) { [self.view bringSubviewToFront:self.dimView]; }

    if (self.listView) { [self.view bringSubviewToFront:self.listView]; }

    if (self.userListView) { [self.view bringSubviewToFront:self.userListView]; }

    if (_iSessionHandle.isPlayback) { [self.view bringSubviewToFront:self.playbackMaskView]; }
}

- (void)hiddenNavAlertView {

    // 隐藏弹框页
    [self hiddenNavBarViewActionView];
    // 隐藏工具箱
    [self controlTabbarToolBoxBtn:NO];
}

- (void)hiddenNavBarViewActionView {

    [self tapOnViewToHide];
}

- (void)controlTabbarToolBoxBtn:(BOOL)isSelected {

    if (isSelected == self.navbarView.toolBoxButton.selected) { return; }

    self.navbarView.toolBoxButton.selected = isSelected;
}

- (void)refreshWhiteBoard:(BOOL)hasAnimate {

    if (_iSessionHandle.isPicInPic) { [self changeVideoFrame:NO]; }

    [self layoutViews];

    // MP3图标位置变化,但是MP4的位置不需要变化
    if (self.iMediaView && !self.iMediaView.hasVideo) { [self restoreMp3ViewFrame]; }
}

- (void)createTimer {

    if (!_iCheckPlayVideotimer) {
        __weak __typeof(self) weekSelf = self;
        _iCheckPlayVideotimer        = [[TKTimer alloc] initWithTimeout:0.5
                                                                 repeat:YES
                                                             completion:
                                        ^{
                                            __strong __typeof(self) strongSelf = weekSelf;
                                            
                                            [strongSelf checkPlayVideo];
                                            
                                        }
                                                                  queue:dispatch_get_main_queue()];
        
        [_iCheckPlayVideotimer start];
    }
}

- (TKAnswerSheetView *)answerSheetForView:(UIView *)view {
    TKAnswerSheetView *answerSheet = nil;
    view                           = _iSessionHandle.whiteBoardManager.contentView;

    NSEnumerator *subviewsEnum = [view.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:[TKAnswerSheetView class]]) {
            answerSheet = (TKAnswerSheetView *)subview;
            [view bringSubviewToFront:answerSheet];
            return answerSheet;
        }
    }

    if (!answerSheet) {
        answerSheet = [[TKAnswerSheetView alloc] init];
        [view addSubview:answerSheet];
        [answerSheet mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(view.mas_centerX);
            make.centerY.equalTo(view.mas_centerY);
        }];
    }

    return answerSheet;
}
#pragma mark - 布局样式

- (void)setViewStyle:(TKRoomLayout)viewStyle {
    if (_viewStyle != viewStyle) {

        _viewStyle = viewStyle;

        [self layoutViews];
    }
}
- (void)defaultLayout:(CGFloat)width x:(CGFloat)x {
    self.whiteboardBackView.hidden = NO;
    _iSessionHandle.roomLayout     = oneToOne;
    /**默认布局*/
    // 1.白板宽度 + 视频宽度 = 屏幕宽度
    // 2.白板高度 = 2 * 视频高度
    // 3.如果是phone X 左右分别空出 44pt , 33pt
    // 4.用户视频 宽：高 === 4:3

    x              = _viewX + _whiteBoardLeftRightSpace;
    CGFloat y      = self.navbarHeight + _whiteBoardTopBottomSpace;
    width          = ScreenW - _viewX - _sStudentVideoViewWidth - 2 * _whiteBoardLeftRightSpace;
    CGFloat height = 2 * _sStudentVideoViewHeigh;
    self.whiteboardBackView.frame = CGRectMake(x, y, width, height);

    self.iTKEduWhiteBoardView.frame = self.whiteboardBackView.bounds;

    self.iTeacherVideoView.frame =
        CGRectMake(self.whiteboardBackView.rightX, self.whiteboardBackView.y,
                   _sStudentVideoViewWidth, _sStudentVideoViewHeigh);
    self.iOurVideoView.frame =
        CGRectMake(self.whiteboardBackView.rightX, self.iTeacherVideoView.bottomY,
                   _sStudentVideoViewWidth, _sStudentVideoViewHeigh);

    if (_iSessionHandle.iIsCanDraw && _iUserType == TKUserType_Student) {
        self.navbarView.upLoadButton.hidden = NO;
    }
}

- (void)doubleDivision:(CGFloat)width x:(CGFloat)x {
    self.whiteboardBackView.hidden = NO;
    _iSessionHandle.roomLayout     = oneToOneDoubleDivision;
    /**布局1*/
    // 1.白板宽度 = 老师视频宽度 = 0.5 *屏幕宽度
    // 2.白板高度 = 老师视频高度
    // 3.如果是phone X 左右分别空出 44pt , 33pt
    // 4.用户视频 宽：高 === 4:3
    // 5.学生占老师高度 0.36
    CGFloat whiteboardWidth  = 0.5 * width;
    CGFloat whiteboardHeight = 0.75 * 0.5 * width;
    CGFloat y                = (ScreenH - _navbarHeight - whiteboardHeight) / 2 + _navbarHeight;

    self.whiteboardBackView.frame   = CGRectMake(x, y, whiteboardWidth, whiteboardHeight);
    self.iTKEduWhiteBoardView.frame = self.whiteboardBackView.bounds;

    self.iTeacherVideoView.frame =
    CGRectMake(CGRectGetMaxX(self.whiteboardBackView.frame),
               CGRectGetMinY(self.whiteboardBackView.frame), whiteboardWidth, whiteboardHeight);
    
    self.iOurVideoView.height = (whiteboardHeight * 0.36);
    self.iOurVideoView.width  = self.iOurVideoView.height * (4.0 / 3);
    self.iOurVideoView.x      = ScreenW - self.iOurVideoView.width;
    self.iOurVideoView.y      = self.iTeacherVideoView.y;
    _sVideoView               = self.iOurVideoView;
    

    if (_iSessionHandle.isPlayMedia == NO) { [self.view bringSubviewToFront:self.iOurVideoView]; }

    _showHideVideoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _showHideVideoBtn.frame =
        CGRectMake(self.iOurVideoView.x - 20, self.iOurVideoView.y, 20, self.iOurVideoView.height);
    _showHideVideoBtn.sakura.backgroundColor(@"layoutView.tk_shouqi_bg");
    _showHideVideoBtn.sakura.alpha(@"layoutView.tk_shouqi_bg_alpha");
    _showHideVideoBtn.sakura.image(@"layoutView.tk_shouqi_jt", UIControlStateNormal);
    _showHideVideoBtn.sakura.image(@"layoutView.tk_zhankai_jt", UIControlStateSelected);
    _showHideVideoBtn.alpha = 0.7;
    [self.view bringSubviewToFront:self.showHideVideoBtn];

    UIBezierPath *maskPath =
        [UIBezierPath bezierPathWithRoundedRect:_showHideVideoBtn.bounds
                              byRoundingCorners:UIRectCornerTopLeft | UIRectCornerBottomLeft
                                    cornerRadii:CGSizeMake(12, _showHideVideoBtn.height)];

    CAShapeLayer *maskLayer      = [[CAShapeLayer alloc] init];
    maskLayer.frame              = _showHideVideoBtn.bounds;
    maskLayer.path               = maskPath.CGPath;
    _showHideVideoBtn.layer.mask = maskLayer;

    [_showHideVideoBtn addTarget:self
                          action:@selector(showHideVideo:)
                forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_showHideVideoBtn];

    tk_weakify(self);
    void (^_Nullable splitScreenClickBlock)() = ^() {
        //交换老师跟学生视频frame
        CGRect frame                     = weakSelf.iOurVideoView.frame;
        weakSelf.iOurVideoView.frame     = weakSelf.iTeacherVideoView.frame;
        weakSelf.iTeacherVideoView.frame = frame;

        weakSelf.sVideoView = weakSelf.iTeacherVideoView.width > weakSelf.iOurVideoView.width
                                  ? weakSelf.iOurVideoView
                                  : weakSelf.iTeacherVideoView;
        [weakSelf.view bringSubviewToFront:weakSelf.sVideoView];
        [weakSelf.view bringSubviewToFront:weakSelf.showHideVideoBtn];

        weakSelf.showHideVideoBtn.x = weakSelf.sVideoView.x - (weakSelf.showHideVideoBtn.selected ? 25 : 20);
        weakSelf.showHideVideoBtn.y = weakSelf.sVideoView.y;
        
        _oneToOneDoubleDivisionModeTeacherOnLeft = CGRectGetWidth(weakSelf.iTeacherVideoView.frame) > CGRectGetWidth(weakSelf.iOurVideoView.frame);
    };
    self.iOurVideoView.splitScreenClickBlock = self.iTeacherVideoView.splitScreenClickBlock =
        splitScreenClickBlock;

    if (!_oneToOneDoubleDivisionModeTeacherOnLeft) {
        splitScreenClickBlock();
    }
    if (_iSessionHandle.iIsCanDraw && _iUserType == TKUserType_Student) {
        self.navbarView.upLoadButton.hidden = NO;
    }
}

- (void)doubleVideo:(CGFloat)width x:(CGFloat)x {
    _iSessionHandle.roomLayout = oneToOneDoubleVideo;
    /**布局2*/
    // 1.学生视频宽度 = 老师视频宽度 = 0.5 *屏幕宽度
    // 2.学生视频高度 = 老师视频高度
    // 3.如果是phone X 左右分别空出 44pt , 33pt
    // 4.用户视频 宽：高 === 4:3
    CGFloat videoWidth  = 0.5 * width;
    CGFloat videoHeight = 0.75 * 0.5 * width;
    CGFloat y           = (ScreenH - _navbarHeight - videoHeight) / 2 + _navbarHeight;

    self.whiteboardBackView.hidden = YES;

    self.iTeacherVideoView.frame = CGRectMake(x, y, videoWidth, videoHeight);
    self.iOurVideoView.frame =
        CGRectMake(CGRectGetMaxX(self.iTeacherVideoView.frame), y, videoWidth, videoHeight);

    self.swapButton.hidden = NO;
    self.swapButton.frame  = CGRectMake(CGRectGetMidX(self.view.frame) - 30,
                                       CGRectGetMidY(self.iTeacherVideoView.frame) - 30, 60, 60);
    self.swapButton.sakura.image(@"layoutView.tk_move_video", UIControlStateNormal);

    if (_iSessionHandle.iIsCanDraw && _iUserType == TKUserType_Student) {
        self.navbarView.upLoadButton.hidden = YES;
    }
}

//布局白板和视频的位置
- (void)layoutViews {
    if (self.iTeacherVideoView.isSplit) {
        //分屏的frame计算 在onRemoteMsgWithVideoSplitScreen 方法中
        return;
    }

    //调整view层级
    [self.view bringSubviewToFront:self.chatViewNew];
    [self.view bringSubviewToFront:self.swapButton];

    self.iOurVideoView.hidden     = NO;
    self.iTeacherVideoView.hidden = NO;

    self.iOurVideoView.splitScreenClickBlock     = nil;
    self.iTeacherVideoView.splitScreenClickBlock = nil;

    self.iOurVideoView.isSpeaker     = self.viewStyle == oneToOneDoubleVideo ?: NO;
    self.iTeacherVideoView.isSpeaker = self.viewStyle == oneToOneDoubleVideo ?: NO;

    self.swapButton.hidden = YES;
    [_showHideVideoBtn removeFromSuperview];

    CGFloat width = [TKUtil isiPhoneX] ? ScreenW - 44 - 33 : ScreenW;
    CGFloat x     = [TKUtil isiPhoneX] ? 44 : 0;
    switch (self.viewStyle) {
        case oneToOne:

            [self defaultLayout:width x:x];

            break;

        case oneToOneDoubleDivision: {
            [self doubleDivision:width x:x];

        } break;

        case oneToOneDoubleVideo: {
            [self doubleVideo:width x:x];

        } break;

        default:
            break;
    }
    [_iSessionHandle.whiteBoardManager refreshWhiteBoard];
}

- (void)showHideVideo:(UIButton *)button {
    button.enabled = NO;

    // 收起
    if (button.selected == NO) {
        [UIView animateWithDuration:0.5
            animations:^{
                self.sVideoView.x       = ScreenW;
                self.showHideVideoBtn.x = ScreenW - 25;

            }
            completion:^(BOOL finished) {
                button.enabled  = YES;
                button.selected = !button.selected;
            }];
    } else {
        [UIView animateWithDuration:0.5
            animations:^{
                self.sVideoView.x       = ScreenW - self.sVideoView.width;
                self.showHideVideoBtn.x = ScreenW - self.sVideoView.width - 20;

            }
            completion:^(BOOL finished) {
                button.enabled  = YES;
                button.selected = !button.selected;
            }];
    }
}
- (void)swapButtonAction:(UIButton *)button {
    button.enabled = NO;
    CGRect frame1  = self.iTeacherVideoView.frame;
    CGRect frame2  = self.iOurVideoView.frame;

    [UIView animateWithDuration:0.5
        animations:^{
            self.iTeacherVideoView.frame = frame2;
            self.iOurVideoView.frame     = frame1;

        }
        completion:^(BOOL finished) { button.enabled = YES; }];
}

#pragma mark - 播放
- (void)playVideo:(TKRoomUser *)user {

    //    [self.iSessionHandle delUserPlayAudioArray:user];

    TKCTVideoSmallView *viewToSee = nil;
    if (user.role == TKUserType_Teacher)
        viewToSee = self.iTeacherVideoView;

    else if ((_iRoomType == TKRoomType_One && user.role == TKUserType_Student) ||
             (_iRoomType == TKRoomType_One && user.role == TKUserType_Patrol)) {
        viewToSee = self.iOurVideoView;
    }

    if (viewToSee) {

        if (viewToSee.iRoomUser && ![user.peerID isEqualToString:viewToSee.iRoomUser.peerID]) {
            //如果是挤掉的其他用户 得需要将之前的unplay掉 才能play成功
            __weak __typeof(self) weekSelf           = self;
            NSMutableDictionary *tPlayVideoViewDic = self.iPlayVideoViewDic;
            
            [self myUnPlayVideo:viewToSee.iRoomUser
                     aVideoView:viewToSee
                     completion:^(NSError *error) {
                         
                         [tPlayVideoViewDic removeObjectForKey:viewToSee.iRoomUser.peerID];
                         __strong __typeof(weekSelf) strongSelf = weekSelf;
                         
                 if (!self.iSessionHandle.iIsFullState) { [strongSelf refreshUI]; }

                       [self
                           myPlayVideo:user
                            aVideoView:viewToSee
                            completion:^(NSError *error) {

                                if (!error) {
                                    [self.iPlayVideoViewDic setObject:viewToSee forKey:user.peerID];
                                    if (self.iSessionHandle
                                            .iIsFullState) { //如果文档处于全屏模式下则不进行刷新界面
                                        return;
                                    }
                                    [self refreshUI];
                                }
                            }];

                   }];

            return;
        }

        [self myPlayVideo:user
               aVideoView:viewToSee
               completion:^(NSError *error) {

                   if (!error) {
                       [self.iPlayVideoViewDic setObject:viewToSee forKey:user.peerID];
                       if (self.iSessionHandle
                               .iIsFullState) { //如果文档处于全屏模式下则不进行刷新界面
                           return;
                       }
                       [self refreshUI];
                   }
               }];
    }
}

- (void)unPlayVideo:(TKRoomUser *)user {

    TKCTVideoSmallView *viewToSee = nil;
    if (user.role == TKUserType_Teacher)
        viewToSee = self.iTeacherVideoView;

    else if (_iRoomType == TKRoomTypeOneToOne && user.role == TKUserType_Student) {
        viewToSee = _iOurVideoView;
    }

    if (viewToSee && viewToSee.iRoomUser != nil &&
        [viewToSee.iRoomUser.peerID isEqualToString:user.peerID]) {

        __weak __typeof(self) weekSelf           = self;
        NSMutableDictionary *tPlayVideoViewDic = self.iPlayVideoViewDic;

        [self myUnPlayVideo:user
                 aVideoView:viewToSee
                 completion:^(NSError *error) {

                     [tPlayVideoViewDic removeObjectForKey:user.peerID];

                     __strong __typeof(weekSelf) strongSelf = weekSelf;

                     if (!self.iSessionHandle.iIsFullState) { [strongSelf refreshUI]; }

                 }];
    }
}

- (void)myUnPlayVideo:(TKRoomUser *)aRoomUser
           aVideoView:(TKCTVideoSmallView *)aVideoView
           completion:(void (^)(NSError *error))completion {
    [self.iSessionHandle sessionHandleUnPlayVideo:aRoomUser.peerID
                                       completion:^(NSError *error) {

                                           //更新uiview
                                           [aVideoView clearVideoData];
                                           completion(error);
                                       }];
}
- (void)myPlayVideo:(TKRoomUser *)aRoomUser
         aVideoView:(TKCTVideoSmallView *)aVideoView
         completion:(void (^)(NSError *error))completion {

    [_iSessionHandle
        sessionHandlePlayVideo:aRoomUser.peerID
                    renderType:(TKRenderMode_adaptive)window:aVideoView
                    completion:^(NSError *error) {

                        aVideoView.iPeerId   = aRoomUser.peerID;
                        aVideoView.iRoomUser = aRoomUser;
                        [aVideoView changeName:aRoomUser.nickName];
                        completion(error);
                    }];
}

- (void)initTapGesTureRecognizer {
    UITapGestureRecognizer *tapTableGesture =
        [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapTable:)];
    tapTableGesture.delegate = self;
    [self.backgroundImageView addGestureRecognizer:tapTableGesture];
}

- (void)leftButtonPress {
    
    if (_isQuiting) { return; }
    
    [self tapTable:nil];

    tk_weakify(self);
    TKAlertView *alert = [[TKAlertView alloc] initForWarningWithTitle:TKMTLocalized(@"Prompt.prompt")
                                                          contentText:TKMTLocalized(@"Prompt.Quite")
                                                            leftTitle:TKMTLocalized(@"Prompt.Cancel")
                                                           rightTitle:TKMTLocalized(@"Prompt.OK")];
    [alert show];
    alert.rightBlock = ^{
        weakSelf.isQuiting = YES;
        [weakSelf.iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error)
         {
            if (error) {
                TKLog(@"退出房间错误: %@", error);
            }
        }];

    };
    alert.lelftBlock = ^{ weakSelf.isQuiting = NO; };
}

//如果是自己退出，则先掉leftroom。否则，直接退出。
- (void)prepareForLeave:(BOOL)aQuityourself {

    [self tapTable:nil];
    [self.navbarView destory];
    self.navbarView = nil;
    [self.listView dismissAlert];
    [self.chatViewNew hide:YES];

    [self invalidateTimer];

#if TARGET_IPHONE_SIMULATOR
#else
    //建议在播放之前设置yes，播放结束设置NO，这个功能是开启红外感应
    [[UIDevice currentDevice] setProximityMonitoringEnabled:NO];
#endif
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];

    [self dismissViewControllerAnimated:YES
                             completion:^{
        // change openurl
        if (self.networkRecovered == NO) {
            [TKUtil showMessage:TKMTLocalized(@"Error.WaitingForNetwork")];
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:sTKRoomViewControllerDisappear object:nil];
        
    }];
}

#pragma mark - TKEduSessionDelegate
- (void)sessionManagerDidOccuredWaring:(TKRoomWarningCode)code {

    if (_iSessionHandle.isPlayback == YES) return;

    switch (code) {
        case TKRoomWarning_RequestAccessForVideo_Failed: {
            TKAlertView *alert =
                [[TKAlertView alloc] initWithTitle:@""
                                       contentText:TKMTLocalized(@"Prompt.NeedCamera")
                                      confirmTitle:TKMTLocalized(@"Prompt.Sure")];
            [alert show];
        } break;
        case TKRoomWarning_RequestAccessForAudio_Failed:

            break;
        case TKRoomWarning_ReConnectSocket_ServerChanged: {

            self.networkRecovered = NO;
            self.currentServer    = nil;

            [_iSessionHandle configureHUD:TKMTLocalized(@"State.Reconnecting") aIsShow:YES];

            [_iSessionHandle configureDraw:false
                                    isSend:NO
                                        to:sTellAll
                                    peerID:_iSessionHandle.localUser.peerID];

            [_iSessionHandle clearAllClassData];

            [self quitClearData];
            break;
        }
        default:
            break;
    }
}

- (void)sessionManagerDidFailWithError:(NSError *)error {
}
// 获取礼物数
- (void)sessionManagerGetGiftNumber:(void (^)())completion {

    // 老师断线重连不需要获取礼物
    if (_iSessionHandle.localUser.role == TKUserType_Teacher ||
        _iSessionHandle.localUser.role == TKUserType_Assistant ||
        _iSessionHandle.isPlayback == YES) {
        if (completion) { completion(); }
        return;
    }

    // 学生断线重连需要获取礼物
    [TKEduNetManager getGiftinfo:_roomJson.roomid
        aParticipantId:_roomJson.thirdid
        aHost:sHost
        aPort:sPort
        aGetGifInfoComplete:^(id _Nullable response) {
            dispatch_async(dispatch_get_main_queue(), ^{
                int result = 0;
                result     = [[response objectForKey:@"result"] intValue];
                if (!result || result == -1) {

                    NSArray *tGiftInfoArray = [response objectForKey:@"giftinfo"];
                    int giftnumber          = 0;
                    for (int i = 0; i < [tGiftInfoArray count]; i++) {
                        if (_iSessionHandle.localUser.peerID.length) {
                            NSDictionary *tDicInfo = [tGiftInfoArray objectAtIndex:i];
                            if ([[tDicInfo objectForKey:@"receiveid"]
                                    isEqualToString:_iSessionHandle.localUser.peerID]) {
                                giftnumber = [tDicInfo objectForKey:@"giftnumber"]
                                                 ? [[tDicInfo objectForKey:@"giftnumber"] intValue]
                                                 : 0;
                                break;
                            }
                        }
                    }

                    self.iSessionHandle.localUser.properties[sGiftNumber] = @(giftnumber);

                    if (completion) { completion(); }

                    //[_iSessionHandle  joinEduClassRoomWithParam:_iParamDic
                    //aProperties:@{sGiftNumber:@(giftnumber)}];
                }
            });

        }
        aGetGifInfoError:^int(NSError *_Nullable aError) {
            dispatch_async(dispatch_get_main_queue(), ^{

                if (completion) { completion(); }

                //[_iSessionHandle  joinEduClassRoomWithParam:_iParamDic aProperties:nil];
            });
            return 1;
        }];
}

//自己进入课堂
- (void)sessionManagerRoomJoined {

    //回放直接隐藏聊天界面
    //    self.chatViewNew.hidden = _iSessionHandle.isPlayback;
    
    _navbarView.beginAndEndClassButton.userInteractionEnabled = YES;
    //根据角色类型选择隐藏聊天按钮
    [self.chatViewNew setUserRoleType:[TKEduSessionHandle shareInstance].localUser.role];

    self.isConnect = NO;
    [_iSessionHandle sessionHandleSetDeviceOrientation:(UIDeviceOrientationLandscapeLeft)];

    // 主动获取奖杯数目
    [self getTrophyNumber];

    self.networkRecovered = YES;

    bool isConform = [TKUtil deviceisConform];
    if (_iSessionHandle.localUser.role == TKUserType_Teacher) {
        if (!isConform) {
            NSString *str = [TKUtil dictionaryToJSONString:@{
                @"lowconsume" : @YES,
                @"maxvideo" : @(2)
            }];
            [_iSessionHandle sessionHandlePubMsg:sLowConsume
                                              ID:sLowConsume
                                              To:sTellAll
                                            Data:str
                                            Save:true
                                 AssociatedMsgID:nil
                                AssociatedUserID:nil
                                         expires:0
                                      completion:nil];
        } else {
            NSString *str = [TKUtil dictionaryToJSONString:@{
                @"lowconsume" : @NO,
                @"maxvideo" : _roomJson.maxvideo
            }];
            [_iSessionHandle sessionHandlePubMsg:sLowConsume
                                              ID:sLowConsume
                                              To:sTellAll
                                            Data:str
                                            Save:true
                                 AssociatedMsgID:nil
                                AssociatedUserID:nil
                                         expires:0
                                      completion:nil];
        }
    }

    // 低能耗老师进入一对多房间，进行提示
    if (!isConform && _iSessionHandle.localUser.role == TKUserType_Teacher &&
        _iRoomType != TKRoomTypeOneToOne) {
        NSString *message = [NSString stringWithFormat:@"%@", TKMTLocalized(@"Prompt.devicetPrompt")];
        TKChatMessageModel *chatMessageModel =
            [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                   role:TKChatRoleTypeMe
                                                message:message cospath:nil
                                               userName:_iSessionHandle.localUser.nickName
                                                 fromid:_iSessionHandle.localUser.peerID
                                                   time:[TKUtil currentTimeToSeconds]];
        [_iSessionHandle addOrReplaceMessage:chatMessageModel];
    }

    // 如果断网之前在后台，回到前台时的时候需要发送回到前台的信令
    if ([_iSessionHandle.localUser.properties objectForKey:@"isInBackGround"] &&
        [[_iSessionHandle.localUser.properties objectForKey:@"isInBackGround"] boolValue] == YES &&
        _iSessionHandle.localUser.role == TKUserType_Student &&
        _iSessionHandle.roomMgr.inBackground == NO) {

        [_iSessionHandle sessionHandleChangeUserProperty:_iSessionHandle.localUser.peerID
                                                TellWhom:sTellAll
                                                     Key:sIsInBackGround
                                                   Value:@(NO)
                                              completion:nil];
    }

    _iSessionHandle.iIsJoined = YES;

    //    bool tIsTeacherOrAssis  = (_iSessionHandle.localUser.role ==TKUserType_Teacher ||
    //    _iSessionHandle.localUser.role ==TKUserType_Assistant);

    TKLog(@"tlm-----myjoined 时间: %@", [TKUtil currentTimeToSeconds]);
#if TARGET_IPHONE_SIMULATOR
#else
    [[UIDevice currentDevice]
        setProximityMonitoringEnabled:
            NO]; //建议在播放之前设置yes，播放结束设置NO，这个功能是开启红外感应
#endif
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    _iTKEduWhiteBoardView.hidden = NO;

    _iUserType = _iSessionHandle.localUser.role;
    _iRoomType = _roomJson.roomtype;
    _isQuiting = NO;

    _iCurrentTime = [[NSDate date] timeIntervalSince1970];

    BOOL meHasVideo = _iSessionHandle.localUser.hasVideo;
    BOOL meHasAudio = _iSessionHandle.localUser.hasAudio;

    [_iSessionHandle configureHUD:@"" aIsShow:NO];
    if (!meHasVideo) { TKLog(@"没有视频"); }
    if (!meHasAudio) { TKLog(@"没有音频"); }

    [_iSessionHandle addUserStdntAndTchr:_iSessionHandle.localUser];
    [_iSessionHandle addUser:_iSessionHandle.localUser];

    TKLog(@"tlm----- 课堂加载完成时间: %@", [TKUtil currentTimeToSeconds]);

    [_iSessionHandle sessionHandlePubMsg:sUpdateTime
                                      ID:sUpdateTime
                                      To:_iSessionHandle.localUser.peerID
                                    Data:@""
                                    Save:NO
                         AssociatedMsgID:nil
                        AssociatedUserID:nil
                                 expires:0
                              completion:nil];

    //是否是自动上课
    if (_roomJson.configuration.autoStartClassFlag == YES && _iSessionHandle.isClassBegin == NO &&
        _iSessionHandle.localUser.role == TKUserType_Teacher) {

        // 只有手动点击上下课时传 userid roleid
        [TKEduNetManager classBeginStar:_roomJson.roomid
            companyid:_roomJson.companyid
            aHost:sHost
            aPort:sPort
            userid:nil
            roleid:nil
            aComplete:^int(id _Nullable response) {

                NSString *str = [TKUtil dictionaryToJSONString:@{ @"recordchat" : @YES }];
                //[_iSessionHandle sessionHandlePubMsg:sClassBegin ID:sClassBegin To:sTellAll
                //Data:str Save:true completion:nil];
                [_iSessionHandle sessionHandlePubMsg:sClassBegin
                                                  ID:sClassBegin
                                                  To:sTellAll
                                                Data:str
                                                Save:true
                                     AssociatedMsgID:nil
                                    AssociatedUserID:nil
                                             expires:0
                                          completion:nil];

                return 0;
            }
            aNetError:^int(id _Nullable response) {

                return 0;
            }];
    }
    //如果是上课的状态则不进行推流的任何操作
    if (_iSessionHandle.isClassBegin && _iUserType != TKUserType_Teacher) { return; }
    // 进入房间就可以播放自己的视频
    if (_iUserType != TKUserType_Patrol && _iSessionHandle.isPlayback == NO) {
        _isLocalPublish = false;
        if (_roomJson.configuration.beforeClassPubVideoFlag) { //发布视频
            PublishState status =
                _iSessionHandle.isOnlyAudioRoom ? TKPublishStateAUDIOONLY : TKPublishStateBOTH;

            [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                    Publish:(status)completion:^(NSError *error){}];
        } else { //显示本地视频不发布
            _isLocalPublish                        = true;
            _iSessionHandle.localUser.publishState = (TKPublishState)TKPublishStateLocalNONE;
            [_iSessionHandle addPublishUser:_iSessionHandle.localUser];

            [self playVideo:_iSessionHandle.localUser];
        }
    }

    [[NSNotificationCenter defaultCenter] postNotificationName:tkUserListNotification object:nil];
    
}

//自己离开课堂
- (void)sessionManagerRoomLeft {
    
    [self unPlayVideo: _iSessionHandle.localUser];
    
    _isQuiting                = NO;
    _iSessionHandle.iIsJoined = NO;
    [_iSessionHandle delUserStdntAndTchr:_iSessionHandle.localUser];
    [_iSessionHandle delUser:_iSessionHandle.localUser];

    //清理数据
    [self quitClearData];

    [_iSessionHandle.whiteBoardManager resetWhiteBoardAllData];
    [_iSessionHandle.whiteBoardManager clearAllData];

    [_iSessionHandle clearAllClassData];
	[_iSessionHandle configureHUD:@"" aIsShow:NO];
    
    _iSessionHandle.roomMgr = nil;
    [TKEduSessionHandle destroy];
    _iSessionHandle = nil;

    [self prepareForLeave:YES];
}

//用户进入
- (void)sessionManagerUserJoined:(TKRoomUser *)user InList:(BOOL)inList {
    
    TKLog(@"1------otherJoined:%@ peerID:%@", user.nickName, user.peerID);

    if (inList) {
        // 角色相同 踢人
        if ((_iSessionHandle.localUser.role == user.role && user.role == TKUserType_Teacher) ||
            (_iSessionHandle.localUser.role == user.role && user.role == TKUserType_Student)) {

            if (_iRoomType == TKRoomTypeOneToOne) {

                [_iSessionHandle sessionHandleEvictUser:user.peerID evictReason:nil completion:nil];
            }
        }
    }

    [_iSessionHandle addUser:user];

    //巡课不提示
    NSString *userRole;
    switch (user.role) {
        case TKUserType_Teacher:
            userRole = TKMTLocalized(@"Role.Teacher");
            break;
        case TKUserType_Student:
            userRole = TKMTLocalized(@"Role.Student");
            break;
        case TKUserType_Assistant:
            userRole = TKMTLocalized(@"Role.Assistant");
            break;
        default:
            break;
    }
    if ((user.role != TKUserType_Patrol &&
     user.role != TKUserType_Assistant) ||
    (user.role == TKUserType_Assistant && _roomJson.configuration.isPromptAssistantJoinRoom == YES)) {
        
        TKChatMessageModel *tModel =
            [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                   role:TKChatRoleTypeMe
                                                message:[NSString stringWithFormat:@"%@(%@)%@", user.nickName, userRole,                                                          TKMTLocalized(@"Action.EnterRoom")]
                                                cospath:nil userName:nil fromid:nil time:[TKUtil currentTime]];
        [_iSessionHandle addOrReplaceMessage:tModel];
    }
    BOOL tISpclUser = (user.role != TKUserType_Student && user.role != TKUserType_Teacher);
    if (tISpclUser) {
        [_iSessionHandle addSecialUser:user];

    } else {
        [_iSessionHandle addUserStdntAndTchr:user];
    }

    // 提示在后台的学生
    if (_iUserType == TKUserType_Teacher || _iUserType == TKUserType_Assistant ||
        _iUserType == TKUserType_Patrol) {
        if ([user.properties objectForKey:sIsInBackGround] != nil &&
            [[user.properties objectForKey:sIsInBackGround] boolValue] == YES) {
            NSString *deviceType = [user.properties objectForKey:@"devicetype"];
            NSString *message =
                [NSString stringWithFormat:@"%@ (%@) %@", user.nickName, deviceType,
                                           TKMTLocalized(@"Prompt.HaveEnterBackground")];
            TKChatMessageModel *chatMessageModel =
                [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                       role:TKChatRoleTypeMe
                                                    message:message cospath:nil
                                                   userName:user.nickName
                                                     fromid:user.peerID time:[TKUtil currentTime]];
            [_iSessionHandle addOrReplaceMessage:chatMessageModel];
        }
    }

    [[NSNotificationCenter defaultCenter] postNotificationName:tkUserListNotification object:nil];
    
    // 一对一 如果未发布音视频但是在台上 需要play 显示占位图
    if (user.role == TKUserType_Teacher || user.role == TKUserType_Student) {
        if (user.publishState == 4) {
            [self playVideo:user];
        }
    }
}
//用户离开
- (void)sessionManagerUserLeft:(NSString *)peerId {

    TKRoomUser *user = [_iSessionHandle.roomMgr getRoomUserWithUId:peerId];

    if (!peerId || !user) { return; }

    [self unPlayVideo:user];

    BOOL tIsMe = [[NSString stringWithFormat:@"%@", user.peerID]
        isEqualToString:[NSString stringWithFormat:@"%@", _iSessionHandle.localUser.peerID]];

    NSString *userRole;
    switch (user.role) {
        case TKUserType_Teacher:
            userRole = TKMTLocalized(@"Role.Teacher");
            break;
        case TKUserType_Student:
            userRole = TKMTLocalized(@"Role.Student");
            break;
        case TKUserType_Assistant:
            userRole = TKMTLocalized(@"Role.Assistant");
            break;
        default:
            break;
    }

    if ((user.role != TKUserType_Patrol && !tIsMe && user.role != TKUserType_Assistant) ||
    (user.role == TKUserType_Assistant && _roomJson.configuration.isPromptAssistantJoinRoom)) {
        
        TKChatMessageModel * tModel =
            [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                   role:TKChatRoleTypeMe
                                                message:[NSString stringWithFormat:@"%@(%@)%@", user.nickName, userRole, TKMTLocalized(@"Action.ExitRoom")]
                                                cospath:nil userName:nil fromid:nil time:[TKUtil currentTime]];
        [_iSessionHandle addOrReplaceMessage:tModel];
    }

    //去掉助教等特殊身份
    BOOL tISpclUser = (user.role != TKUserType_Student && user.role != TKUserType_Teacher);
    if (tISpclUser) {
        [_iSessionHandle delSecialUser:user];
    } else {
        [_iSessionHandle delUserStdntAndTchr:user];
    }
    [_iSessionHandle delUser:user];

    [[NSNotificationCenter defaultCenter] postNotificationName:tkUserListNotification object:nil];
}
// 被踢
- (void)sessionManagerSelfEvicted:(NSDictionary *)reason {
    int rea;
    reason = [TKUtil processDictionaryIsNSNull:reason];

    if ([[reason allKeys] containsObject:@"reason"]) {
        rea = [reason[@"reason"] intValue];
    } else {
        rea = 0;
    }

    NSString *reaStr =
        rea == 1 ? TKMTLocalized(@"Prompt.stuHasKicked") : TKMTLocalized(@"KickOut.Repeat");

    TKAlertView *alter = [[TKAlertView alloc] initWithTitle:TKMTLocalized(@"Prompt.prompt")
                                                contentText:reaStr
                                               confirmTitle:TKMTLocalized(@"Prompt.OK")];
    [alter show];
    alter.rightBlock = ^{

        if (_iPickerController) {
            [_iPickerController dismissViewControllerAnimated:YES
                                                   completion:^{

                                                       _iPickerController = nil;

                                                       [self sessionManagerRoomLeft];
                                                   }];
        } else {

            [self sessionManagerRoomLeft];
            TKLog(@"---------SelfEvicted");
        }
    };
}

- (void)sessionManagerOnAudioVolumeWithPeerID:(NSString *)peeID volume:(int)volume {
    NSDictionary *dict = @{ @"volume" : @(volume) };
    [[NSNotificationCenter defaultCenter]
        postNotificationName:[NSString stringWithFormat:@"%@%@", sVolume, peeID]
                      object:dict];
}

// 网络状态
- (void)sessionManagerOnAVStateWithPeerID:(NSString *)peerID state:(id)state {

    if (self.navbarView.netTipView && [peerID isEqualToString:_iSessionHandle.localUser.peerID]) {
        [self.navbarView.netTipView changeNetTipState:state];
    }

    if (self.netDetailView && [peerID isEqualToString:_iSessionHandle.localUser.peerID]) {
        [self.netDetailView changeDetailData:state];
    }
}

//观看视频
- (void)sessionManagerVideoStateWithUserID:(NSString *)peerID publishState:(TKMediaState)state {

    if (_iSessionHandle.isPicInPic && [peerID isEqualToString:moveView.iRoomUser.peerID]) {
        moveView.hidden = !state;
    }

    TKRoomUser *user = [_iSessionHandle.roomMgr getRoomUserWithUId:peerID];

    if (!user) { return; }

    if (state == TKMedia_Unpulished) {

        if (user.publishState == TKUser_PublishState_NONE) {

            [_iSessionHandle delePublishUser:user];

            if (!(_iSessionHandle.localUser.role == TKUserType_Teacher &&
                  _iSessionHandle.isClassBegin == NO && user.role == TKUserType_Teacher)) {
                // 老师发布的视频下课不取消播放
                [self unPlayVideo:user];
            }
        }

        //        if ((_iSessionHandle.localUser.role == TKUserType_Teacher) && _iMvVideoDic) {
        //            NSDictionary *tMvVideoDic = @{@"otherVideoStyle":_iMvVideoDic};
        //            [_iSessionHandle  publishVideoDragWithDic:tMvVideoDic
        //            To:sTellAllExpectSender];
        //        }

        if (_iSessionHandle.iHasPublishStd == NO && !_iSessionHandle.iIsFullState) {
            [self refreshUI];
        }
    } else {

        if (user.publishState > TKUser_PublishState_NONE &&
            ![self.iPlayVideoViewDic objectForKey:user.peerID] && user.role != TKUserType_Patrol) {

            [self playVideo:user];

            //            if (user.publishState == 1) {
            //                [self.iSessionHandle addOrReplaceUserPlayAudioArray:user];
            //            }
        }
    }
}

//播放音频
- (void)sessionManagerAudioStateWithUserID:(NSString *)peerID publishState:(TKMediaState)state {
    TKRoomUser *user = [_iSessionHandle.roomMgr getRoomUserWithUId:peerID];

    if (!user) { return; }
    if (state == TKMedia_Unpulished) {

        if (user.publishState == TKUser_PublishState_NONE) {

            [_iSessionHandle delePublishUser:user];

            if (!(_iSessionHandle.localUser.role == TKUserType_Teacher &&
                  _iSessionHandle.isClassBegin == NO && user.role == TKUserType_Teacher)) {
                // 老师发布的视频下课不取消播放
                [self unPlayVideo:user];
            }
        }
        [_iSessionHandle sessionHandleUnPlayAudio:peerID
                                       completion:^(NSError *_Nonnull error){

                                       }];
        //
        //        if ((_iSessionHandle.localUser.role == TKUserType_Teacher) && _iMvVideoDic) {
        //            NSDictionary *tMvVideoDic = @{@"otherVideoStyle":_iMvVideoDic};
        //            [_iSessionHandle  publishVideoDragWithDic:tMvVideoDic
        //            To:sTellAllExpectSender];
        //        }

        if (_iSessionHandle.iHasPublishStd == NO && !_iSessionHandle.iIsFullState) {
            [self refreshUI];
        }
    } else {

        if (user.publishState > TKUser_PublishState_NONE &&
            ![self.iPlayVideoViewDic objectForKey:user.peerID] && user.role != TKUserType_Patrol) {

            [self playVideo:user];
            // 播放音频
            [_iSessionHandle sessionHandlePlayAudio:user.peerID completion:nil];
        }
    }
}

//用户信息变化
- (void)sessionManagerUserChanged:(TKRoomUser *)user
                       Properties:(NSDictionary *)properties
                           fromId:(NSString *)fromId {

    TKLog(@"sessionManagerUserChanged_%@", properties);

    NSInteger tGiftNumber = 0;
    if ([properties objectForKey:sGiftNumber]) {
        tGiftNumber = [[properties objectForKey:sGiftNumber] integerValue];
    }
    if ([properties objectForKey:sCandraw]) {

        bool canDraw = [[properties objectForKey:sCandraw] boolValue];

        if ([_iSessionHandle.localUser.peerID isEqualToString:user.peerID] &&
            _iSessionHandle.localUser.role == TKUserType_Student) {

            if (_iSessionHandle.iIsCanDraw != canDraw) {

                [_iSessionHandle configureDraw:canDraw isSend:NO to:sTellAll peerID:user.peerID];
            }
        }
        if (canDraw) {
            if (user.role == TKUserType_Student &&
                ![self.iPlayVideoViewDic objectForKey:user.peerID]) {
                [self playVideo:user];
            }
        }

        if ([_iSessionHandle.localUser.peerID isEqualToString:user.peerID]) {
            // 授权翻页
            self.pageControl.canDraw = canDraw;
            // 授权画笔
            // 下课隐藏画笔工具
            self.brushToolView.hidden = _iSessionHandle.isClassBegin ? !canDraw : YES;
            
            [self.brushToolView hideSelectorView];
            [TKScreenShotFactory sharedFactory].canDraw = _iSessionHandle.isClassBegin ? canDraw : NO;
        }

        // 播放媒体中 需要把画笔工具 放到媒体层下边
        if (self.iSessionHandle.isPlayMedia == YES) {
            [self.whiteboardBackView bringSubviewToFront:self.iMediaView];
        }
    }
    BOOL isRaiseHand = NO;
    if ([properties objectForKey:sRaisehand]) {
        //如果没做改变的话，就不变化

        isRaiseHand = [[properties objectForKey:sRaisehand] boolValue];

        // 当用户状态发生变化，用户列表状态也要发生变化
        for (TKRoomUser *u in [_iSessionHandle userListExpecPtrlAndTchr]) {
            if ([u.peerID isEqualToString:user.peerID]) {
                [u.properties setValue:@(isRaiseHand) forKey:sRaisehand];

                break;
            }
        }
        // 如果是上课 并且花名册显示中 更新
        if ([_iSessionHandle isClassBegin] && _userListView) {
            [[NSNotificationCenter defaultCenter] postNotificationName:tkUserListNotification
                                                                object:nil];
        }
    }

    if ([properties objectForKey:sPublishstate]) {

        PublishState tPublishState =
            (PublishState)[[properties objectForKey:sPublishstate] integerValue];

        if (tPublishState == TKPublishStateNONE) {

            if ([self.iPlayVideoViewDic objectForKey:user.peerID]) { [self unPlayVideo:user]; }

        } else if (user.role != TKUserType_Patrol &&
                   ![self.iPlayVideoViewDic objectForKey:user.peerID]) {
            [self playVideo:user];
        }
    }

    //更改上台后的举手按钮样式
    if (_iUserType == TKUserType_Student &&
        [_iSessionHandle.localUser.peerID isEqualToString:user.peerID]) {

        if (isRaiseHand) {
            if (_iSessionHandle.localUser.publishState > 0) {
                [self.navbarView setHandButtonState:YES];
            }
        } else {
            [self.navbarView setHandButtonState:NO];
        }
    }

    if ([properties objectForKey:sDisableAudio]) {
        // 修改TKEduSessionHandle中iUserList中用户的属性
        for (TKRoomUser *u in [_iSessionHandle userListExpecPtrlAndTchr]) {
            if ([u.peerID isEqualToString:user.peerID]) {
                u.disableAudio = [[properties objectForKey:sDisableAudio] boolValue];

                break;
            }
        }
    }

    if ([properties objectForKey:sDisableVideo]) {
        for (TKRoomUser *u in [_iSessionHandle userListExpecPtrlAndTchr]) {
            if ([u.peerID isEqualToString:user.peerID]) {
                u.disableVideo = [[properties objectForKey:sDisableVideo] boolValue];

                break;
            }
        }
    }

    if ([properties objectForKey:sUdpState]) {
        NSInteger updState = [[properties objectForKey:sUdpState] integerValue];
        // 用户列表的属性进行变更
        for (TKRoomUser *u in [_iSessionHandle userListExpecPtrlAndTchr]) {
            if ([u.peerID isEqualToString:user.peerID]) {
                [u.properties setObject:@(updState) forKey:sUdpState];

                break;
            }
        }
    }

    if ([properties objectForKey:sServerName]) {

        if ([user.peerID isEqualToString:_iSessionHandle.localUser.peerID] &&
            ![fromId isEqualToString:_iSessionHandle.localUser.peerID]) {

            // 其他用户修改自己的服务器
            NSString *serverName =
                [NSString stringWithFormat:@"%@", [properties objectForKey:sServerName]];
            if (serverName != nil) {
                TKLog(@"助教协助修改了服务器地址:%@", serverName);
                [self changeServer:serverName];

                NSError *error =
                    [NSError errorWithDomain:@""
                                        code:TKRoomWarning_ReConnectSocket_ServerChanged
                                    userInfo:nil];

                [self sessionManagerDidFailWithError:error];
            }
        }
    }
    if ([properties objectForKey:sPrimaryColor]) { //画笔颜色

        // 当用户状态发生变化，用户列表状态也要发生变化
        for (TKRoomUser *u in [_iSessionHandle userListExpecPtrlAndTchr]) {
            if ([u.peerID isEqualToString:user.peerID]) {
                [u.properties setValue:[properties objectForKey:sPrimaryColor]
                                forKey:sPrimaryColor];

                break;
            }
        }
    }

    if ([properties objectForKey:sDisablechat]) {

        if ([_iSessionHandle.localUser.peerID isEqualToString:user.peerID] ||
            _iSessionHandle.localUser.role ==
                TKUserType_Patrol) { // 学生过多时，会导致巡课刷新多次而影响性能，后续需处理

            BOOL disableChat   = [properties[sDisablechat] boolValue];
            NSDictionary *dict = @{ @"isBanSpeak" : @(disableChat) };
            [[NSNotificationCenter defaultCenter] postNotificationName:sEveryoneBanChat
                                                                object:dict];
        }
    }

    NSDictionary *dict = @{
        sRaisehand : [properties objectForKey:sRaisehand] ? [properties objectForKey:sRaisehand]
                                                          : @(isRaiseHand),
        sPublishstate : [properties objectForKey:sPublishstate]
                            ? [properties objectForKey:sPublishstate]
                            : @(user.publishState),
        sCandraw : [properties objectForKey:sCandraw] ? [properties objectForKey:sCandraw]
                                                      : @(user.canDraw),
        sGiftNumber : @(tGiftNumber),
        sDisableAudio : [properties objectForKey:sDisableAudio]
                            ? @([[properties objectForKey:sDisableAudio] boolValue])
                            : @(user.disableAudio),
        sDisableVideo : [properties objectForKey:sDisableVideo]
                            ? @([[properties objectForKey:sDisableVideo] boolValue])
                            : @(user.disableVideo),
        sPrimaryColor : [properties objectForKey:sPrimaryColor]
                            ? [properties objectForKey:sPrimaryColor]
                            : [UIColor blackColor],
        sFromId : fromId
    };
    NSMutableDictionary *tDic = [NSMutableDictionary dictionaryWithDictionary:dict];
    [tDic setValue:[properties objectForKey:sPrimaryColor] forKey:sPrimaryColor];
    [tDic setValue:user forKey:sUser];
    [self.navbarView buttonRefreshUI];

    [[NSNotificationCenter defaultCenter]
        postNotificationName:[NSString stringWithFormat:@"%@%@", sRaisehand, user.peerID]
                      object:tDic];
    [[NSNotificationCenter defaultCenter] postNotificationName:sDocListViewNotification object:nil];

    if ([properties objectForKey:sIsInBackGround]) {
        BOOL isInBackground = [[properties objectForKey:sIsInBackGround] boolValue];

        // 发送通知告诉视频控件后台状态
        NSDictionary *dict = @{sIsInBackGround : [properties objectForKey:sIsInBackGround]};
        [[NSNotificationCenter defaultCenter]
            postNotificationName:[NSString stringWithFormat:@"%@%@", sIsInBackGround, user.peerID]
                          object:nil
                        userInfo:dict];

        // 当用户发生前后台切换，用户列表状态也要发生变化
        for (TKRoomUser *u in [_iSessionHandle userListExpecPtrlAndTchr]) {
            if ([u.peerID isEqualToString:user.peerID]) {
                [u.properties setObject:[properties objectForKey:sIsInBackGround]
                                 forKey:sIsInBackGround];

                break;
            }
        }

        if (_iUserType == TKUserType_Teacher || _iUserType == TKUserType_Assistant ||
            _iUserType == TKUserType_Patrol) {
            NSString *deviceType = [user.properties objectForKey:@"devicetype"];
            NSString *content;
            if (isInBackground) {
                content = TKMTLocalized(@"Prompt.HaveEnterBackground");
            } else {
                content = TKMTLocalized(@"Prompt.HaveBackForground");
            }
            NSString *message =
                [NSString stringWithFormat:@"%@ (%@) %@", user.nickName, deviceType, content];
           
            TKChatMessageModel *chatMessageModel =
                [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                       role:TKChatRoleTypeMe
                                                    message:message
                                                    cospath:nil userName:user.nickName
                                                     fromid:user.peerID time:[TKUtil currentTime]];
            [_iSessionHandle addOrReplaceMessage:chatMessageModel];
        }
    }

    // yibo:可以实时看到人员离开进入消息，不知道要不要
    if (self.chatViewNew) { [self.chatViewNew reloadData]; }
}

- (void)sessionManagerMessageReceived:(NSString *)message
                               fromID:(NSString *)peerID
                            extension:(NSDictionary *)extension {
    //当聊天视图存在的时候，显示聊天内容。否则存储在未读列表中
    if (self.chatViewNew.leftBtn.selected) {

        [self.chatViewNew messageReceived:message fromID:peerID extension:extension];

    } else {

        NSString *tDataString = [NSString stringWithFormat:@"%@", message];
        NSData *tJsData       = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *tDataDic =
            [NSJSONSerialization JSONObjectWithData:tJsData
                                            options:NSJSONReadingMutableContainers
                                              error:nil];

        // 问题信息不显示 0 聊天， 1 提问
        NSNumber *type = [tDataDic objectForKey:@"type"];
        if ([type integerValue] != 0) { return; }
        
        TKChatMessageType msgType = TKChatMessageTypeText;
        if ([[tDataDic allKeys] containsObject: @"msgtype"] &&
            [[tDataDic objectForKey:@"msgtype"] isEqualToString:@"onlyimg"]) {
            msgType = TKChatMessageTypeOnlyImage;
//            return;
        }
        
        NSString *time = [tDataDic objectForKey:@"time"];
        NSString *msg = [tDataDic objectForKey:@"msg"];
        NSString *cospath = [tDataDic objectForKey:@"cospath"];
        NSString *tMyPeerId = _iSessionHandle.localUser.peerID;
        
        //自己发送的收不到
        if (!peerID) { peerID = _iSessionHandle.localUser.peerID; }
        BOOL isMe      = [peerID isEqualToString:tMyPeerId];
        BOOL isTeacher = [extension[@"role"] intValue] == TKUserType_Teacher ? YES : NO;
        TKChatRoleType roleType = (isMe)?TKChatRoleTypeMe:(isTeacher?TKChatRoleTypeTeacher:TKChatRoleTypeOtherUer);

        TKChatMessageModel * tChatMessageModel =
            [[TKChatMessageModel alloc] initWithMsgType:msgType role:roleType message:msg cospath:cospath userName:extension[@"nickname"] fromid:peerID time:time];
        [_iSessionHandle.unReadMessagesArray addObject:tChatMessageModel];

        [_iSessionHandle addOrReplaceMessage:tChatMessageModel];
        [self.chatViewNew setBadgeNumber:_iSessionHandle.unReadMessagesArray.count];
    }

    [self.navbarView buttonRefreshUI];
}

//进入会议失败,重连
- (void)sessionManagerOnConnectionLost {
    self.networkRecovered = NO;
    self.currentServer    = nil;

    [_iSessionHandle configureHUD:TKMTLocalized(@"State.Reconnecting") aIsShow:YES];
    [_iSessionHandle configureDraw:false
                            isSend:NO
                                to:sTellAll
                            peerID:_iSessionHandle.localUser.peerID];

    [self.iSessionHandle clearAllClassData];

    if (self.isConnect) { return; }

    self.isConnect = YES;
    [_iSessionHandle.whiteBoardManager roomWhiteBoardOnDisconnect:nil];
    [self.iSessionHandle.whiteBoardManager resetWhiteBoardAllData];

    [self clearVideoViewData:self.iTeacherVideoView];
    [self clearVideoViewData:self.iOurVideoView];

    [self.iPlayVideoViewDic removeAllObjects];
    _iSessionHandle.onPlatformNum = 0;

    // 播放的MP4前，先移除掉上一个MP4窗口
    _iSessionHandle.iCurrentMediaDocModel = nil;
    if (self.iMediaView) {
        [self.iMediaView deleteWhiteBoard];
        [self.iMediaView removeFromSuperview];
        self.iMediaView = nil;
    }
    if (self.iFileView) {
        //        [self.iMediaView deleteWhiteBoard];
        [self.iFileView removeFromSuperview];
        self.iFileView = nil;
    }
    if (self.iScreenView) {
        [self.iScreenView removeFromSuperview];
        self.iScreenView = nil;
    }

    if (self.navbarView.netTipView) { [self.navbarView.netTipView changeDetailSignImage:NO]; }
    if (self.netDetailView) {
        [self.netDetailView removeFromSuperview];
        self.netDetailView = nil;
    }

    // fix bug:全员奖励页面是显示在windows上的，如果网断了，退出教室，view还没消失
    UIWindow *window           = [UIApplication sharedApplication].keyWindow;
    NSEnumerator *subviewsEnum = [window.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:NSClassFromString(@"TKTrophyView")]) {

            [subview removeFromSuperview];
        }
    }

    [_pageControl resetBtnStates];

    //上下课按钮设置为“上课”
    [_navbarView.beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassBegin")
                                        forState:UIControlStateNormal];
}

- (void)sessionManagerIceStatusChanged:(NSString *)state ofUser:(TKRoomUser *)user {
    TKLog(@"------IceStatusChanged:%@ nickName:%@", state, user.nickName);
}

// 共享屏幕
- (void)sessionManagerOnShareScreenState:(NSString *)peerId state:(TKMediaState)state {

    _iSessionHandle.isPlayMedia = (state == TKMedia_Pulished);
    if (state) {
        if (self.iScreenView) {
            [self.iScreenView removeFromSuperview];
            self.iScreenView = nil;
        }

//        CGRect frame =
//            CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame));
        CGRect frame = self.whiteboardBackView.frame;

        TKCTBaseMediaView *tScreenView = [[TKCTBaseMediaView alloc] initScreenShare:frame];
        _iScreenView                   = tScreenView;

        if (_iSessionHandle.isPlayback == YES) {
            [self.view insertSubview:_iScreenView belowSubview:self.playbackMaskView];
        } else {
            [self.view addSubview:_iScreenView];
        }

        [_iSessionHandle sessionHandlePlayScreen:peerId
                                      renderType:TKRenderMode_fit
                                          window:_iScreenView
                                      completion:nil];

    } else {
        __weak __typeof(self) wself = self;
        [_iSessionHandle sessionHandleUnPlayScreen:peerId
                                        completion:^(NSError *error) {

                                            [wself.iScreenView removeFromSuperview];
                                            wself.iScreenView = nil;
                                        }];
    }
}

// 共享文件
- (void)sessionManagerOnShareFileState:(NSString *)peerId
                                 state:(TKMediaState)state
                      extensionMessage:(NSDictionary *)message {
    if (state) {
        if (self.iFileView) {
            [self.iFileView removeFromSuperview];
            self.iFileView = nil;
        }
        CGRect frame =
            CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame));
        TKCTBaseMediaView *tFilmView = [[TKCTBaseMediaView alloc] initFileShare:frame];
        _iFileView                   = tFilmView;
        [_iFileView loadLoadingView];

        if (_iSessionHandle.isPlayback == YES) {
            [self.view insertSubview:_iFileView belowSubview:self.playbackMaskView];
        } else {
            [self.view addSubview:_iFileView];
        }
        [_iSessionHandle sessionHandlePlayFile:peerId
                                    renderType:TKRenderMode_fit
                                        window:_iFileView
                                    completion:^(NSError *error) {
                                        if (_iSessionHandle.localUser.role != TKUserType_Teacher) {
                                            [_iFileView loadWhiteBoard];
                                        }
                                    }];

    } else {
        //媒体流停止后需要删除sVideoWhiteboard
        [_iSessionHandle sessionHandleDelMsg:sVideoWhiteboard
                                          ID:sVideoWhiteboard
                                          To:sTellAll
                                        Data:@{}
                                  completion:nil];

        __weak __typeof(self) wself = self;

        [_iSessionHandle sessionHandleUnPlayFile:peerId
                                      completion:^(NSError *error) {

                                          [wself.iFileView deleteWhiteBoard];
                                          [wself.iFileView removeFromSuperview];
                                          wself.iFileView = nil;

                                      }];
    }
}

//相关信令 pub
- (void)sessionManagerOnRemoteMsg:(BOOL)add
                               ID:(NSString *)msgID
                             Name:(NSString *)msgName
                               TS:(unsigned long)ts
                             Data:(NSObject *)data
                           InList:(BOOL)inlist {

    //添加
    if ([msgName isEqualToString:sClassBegin]) {

        _iSessionHandle.isClassBegin                   = add;
        [self.navbarView refreshUI:add];

        // 上课
        if (add) {

            [self onRemoteMsgWithClassBegin:add
                                         ID:msgID
                                       Name:msgName
                                         TS:ts
                                       Data:data
                                     InList:inlist];

        }
        // 下课
        else {
            [self onRemoteMsgWithClassEnd:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];
        }
        [self.navbarView buttonRefreshUI];

    }
    // 更新时间
    else if ([msgName isEqualToString:sUpdateTime]) {

        [self onRemoteMsgWithUpdateTime:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];

    }
    //翻页
    else if ([msgName isEqualToString:sShowPage] || [msgName isEqualToString:sDocumentChange] ) {
        [_pageControl resetBtnStates];
        
        [self closeMediaView];
    }    // 全体静音
    else if ([msgName isEqualToString:sMuteAudio]) {

        [self onRemoteMsgWithMuteAudio:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];

    }
    // 流错误
    else if ([msgName isEqualToString:sStreamFailure]) {

        [self onRemoteMsgWithStreamFailure:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];

    }
    // 更改服务器
    else if ([msgName isEqualToString:sChangeServerArea]) {

    }
    // 视频标注回调
    else if ([msgName isEqualToString:sVideoWhiteboard]) {

        [self onRemoteMsgWithVideoWhiteboard:add
                                          ID:msgID
                                        Name:msgName
                                          TS:ts
                                        Data:data
                                      InList:inlist];
    }
    // 全体禁言
    else if ([msgName isEqualToString:sEveryoneBanChat]) {

        [self onRemoteMsgWithEveryoneBanChat:add
                                          ID:msgID
                                        Name:msgName
                                          TS:ts
                                        Data:data
                                      InList:inlist];

    }

    // 音频教室
    else if ([msgName isEqualToString:sOnlyAudioRoom]) {
        _iSessionHandle.isOnlyAudioRoom = add;
        [[NSNotificationCenter defaultCenter] postNotificationName:sOnlyAudioRoom object:nil];

    }
    // 白板全屏(同步)
    else if ([msgName isEqualToString:sWBFullScreen]) {

        //        NSDictionary *dic = [self convertWithData:data];

        //        if ([[dic objectForKey:@"fullScreenType"] isEqualToString:@"stream_video"]) {
        //分屏
        //            [self onRemoteMsgWithVideoSplitScreen:add ID:msgID Name:msgName TS:ts
        //            Data:data InList:inlist];
        //        }
        [self onRemoteMsgWithWBFullScreen:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];

    }
    // 工具箱 转盘
    else if ([msgName isEqualToString:@"dial"]) { // || [msgName isEqualToString:@"DialDrag"] 不响应拖拽
        [TKPopViewHelper sharedInstance].isTurntableToolOn = add;
        NSDictionary *dataDic = [self convertWithData:data];
        if (add) {
            if (!_dialView) {

                UIView *toolBV = _iSessionHandle.whiteBoardManager.contentView;
                _dialView      = [[TKDialView alloc] init];
                [_dialView setAngle:dataDic[@"rotationAngle"]];
                [toolBV addSubview:_dialView];
                [_dialView mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.centerX.equalTo(toolBV.mas_centerX);
                    make.centerY.equalTo(toolBV.mas_centerY);
                }];
            } else if (![dataDic[@"isShow"] boolValue]) { // 开始旋转转盘

                [_dialView startWithAngle:dataDic[@"rotationAngle"]];
            }
        } else {
            [_dialView removeFromSuperview];
            _dialView = nil;
        }

    } else if ([msgName isEqualToString:@"Question"]) {
        //答题器
        [TKPopViewHelper sharedInstance].isAnswerToolOn = add;
        NSDictionary *dict = [self convertWithData:data];

        if ([[dict valueForKey:@"action"] isEqualToString:@"open"]) {

            if (_iSessionHandle.localUser.role != TKUserType_Student) {
                //老师
                TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
                answerSheet.viewType           = TKAnswerSheetType_Setup;
            } else {
                TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
                [answerSheet removeFromSuperview];
            }

        } else if ([[dict valueForKey:@"action"] isEqualToString:@"start"]) {

            [[TKAnswerSheetData shareInstance] resetData];

            if (_iSessionHandle.localUser.role != TKUserType_Student) {
                //老师
                TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
                answerSheet.viewType           = TKAnswerSheetType_Detail;
                answerSheet.dict               = dict;
                [answerSheet showTimeWithTimeStamp:[NSString stringWithFormat:@"%lu", ts]];
                answerSheet.state = TKAnswerSheetState_Start;

            } else {
                //学生
                TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
                answerSheet.viewType           = TKAnswerSheetType_Submit;
                answerSheet.dict               = dict;
            }

        } else if ([[dict valueForKey:@"action"] isEqualToString:@"end"]) {
            //答题结束 清理数据
            //            [[TKAnswerSheetData shareInstance] resetData];
            [TKAnswerSheetData shareInstance].quesID = [dict objectForKey:@"quesID"];
        }

        if (!add) {
            TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
            [answerSheet removeFromSuperview];
            //答题结束 清理数据
            //            [[TKAnswerSheetData shareInstance] resetData];
        }

    } else if ([msgName isEqualToString:@"PublishResult"]) {
        //答题器公布的结果
        NSDictionary *dict = [self convertWithData:data];
        if (_iSessionHandle.localUser.role != TKUserType_Student) {
            //老师
            TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
            // 2、3 是 结果页 如果有值就保持不变 如果没有就设置成2 TKAnswerSheetType_Detail
            if (answerSheet.viewType != TKAnswerSheetType_Detail &&
                answerSheet.viewType != TKAnswerSheetType_Record) {
                answerSheet.viewType  = TKAnswerSheetType_Detail;
            }
            answerSheet.dict               = dict;
            NSString *time = [NSString stringWithFormat:@"%lld", [dict[@"ansTime"] longLongValue]];
            [answerSheet showTimeWithTimeStamp:time];
            
            if ([[dict objectForKey:@"hasPub"] boolValue]) {
                answerSheet.state = TKAnswerSheetState_Release;
            } else {
                answerSheet.state = TKAnswerSheetState_End;
            }

        } else {
            //学生
            TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
            if (answerSheet.viewType != TKAnswerSheetType_Detail &&
                answerSheet.viewType != TKAnswerSheetType_Record) {
                answerSheet.viewType  = TKAnswerSheetType_Detail;
            }
            if ([[dict objectForKey:@"hasPub"] boolValue]) {
                answerSheet.dict               = dict;
                answerSheet.state = TKAnswerSheetState_Release;
            } else {
                answerSheet.dict               = dict;
                answerSheet.state = TKAnswerSheetState_End;
            }
        }
        if (!add) {
            TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
            [answerSheet removeFromSuperview];
            //答题结束 清理数据
            [[TKAnswerSheetData shareInstance] resetData];
        }
    } else if ([msgName isEqualToString:@"GetQuestionCount"]) {
        //答题器获取答案
        //因为这个信令缺少 value 字段所以GetQuestionCount的数据只能通过通知获取
        //roomWhiteBoardOnRemotePubMsg
    }

    else if ([msgName isEqualToString:@"ChatShow"] && _iSessionHandle.isPlayback == YES) {
        
        [self.chatViewNew hide:!add];

    } else if ([msgName isEqualToString:@"BlackBoard_new"]) {
        [TKPopViewHelper sharedInstance].isMiniWhiteboardToolOn = add;
        if (add) {
            [self.chatViewNew hide:YES];
        }
    } else if ([msgName isEqualToString:sTimer]) { // 计时器
        [TKPopViewHelper sharedInstance].isTimerToolOn = add;
        [self showTimerWithAdd:add andData:data receiveMsgTime:ts];
    } else if ([msgName isEqualToString:sSwitchLayout]) { // 布局切换
        
        NSDictionary *param = [self convertWithData:data];
        if (param) {
            NSString *style = param[@"nowLayout"];

            if ([style isEqualToString:@"oneToOne"]) {
                self.viewStyle = oneToOne;
            } else if ([style isEqualToString:@"oneToOneDoubleDivision"]) {
                self.viewStyle = oneToOneDoubleDivision;
            } else if ([style isEqualToString:@"oneToOneDoubleVideo"]) {
                self.viewStyle = oneToOneDoubleVideo;
            }

            [self.navbarView buttonRefreshUI];
        }
    }
}

#pragma mark - 计时器
- (void)showTimerWithAdd:(BOOL)add andData:(NSObject *)data receiveMsgTime:(long)time {

    if (add) {

        NSDictionary *dataDic = @{};
        if ([data isKindOfClass:[NSString class]]) {
            NSString *tDataString = [NSString stringWithFormat:@"%@", data];
            NSData *tJsData       = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
            dataDic               = [NSJSONSerialization JSONObjectWithData:tJsData
                                                      options:NSJSONReadingMutableContainers
                                                        error:nil];
        }
        if ([data isKindOfClass:[NSDictionary class]]) { dataDic = (NSDictionary *)data; }
        BOOL isStatus       = [dataDic[@"isStatus"] boolValue];
        BOOL isRestart      = [dataDic[@"isRestart"] boolValue];
        BOOL isShow         = [dataDic[@"isShow"] boolValue];
        NSArray *timerArray = dataDic[@"sutdentTimerArry"];
        NSInteger minute =
            [[NSString stringWithFormat:@"%@%@", timerArray[0], timerArray[1]] integerValue];
        NSInteger second =
            [[NSString stringWithFormat:@"%@%@", timerArray[2], timerArray[3]] integerValue];

        if (_iSessionHandle.localUser.role == TKUserType_Teacher ||
            _iSessionHandle.localUser.role == TKUserType_Patrol) {

            if (!_timerView) {
                // 计时器

                UIView *toolBV = _iSessionHandle.whiteBoardManager.contentView;

                _timerView = [[TKTimerView alloc] init];
                [toolBV addSubview:_timerView];

                [_timerView mas_remakeConstraints:^(MASConstraintMaker *make) {
                    make.centerX.equalTo(toolBV.mas_centerX);
                    make.centerY.equalTo(toolBV.mas_centerY);
                }];
            }

            if (isStatus && !isRestart) { // 开始倒计时
                [self.timerView startTimerCountdownWithMinute:(long)minute
                                                       second:(long)second
                                               receiveMsgTime:time];
            } else if (!isStatus && !isRestart) { //暂停
                [self.timerView pauseTimerWithTimerArray:timerArray];
            } else if (!isStatus && isRestart) { // 重新开始
                [self.timerView stopCountDown];
            }
        }

        if (_iSessionHandle.localUser.role == TKUserType_Student && !isShow) {
            if (!_stuTimer) {

                UIView *contentView = _iSessionHandle.whiteBoardManager.contentView;
                _stuTimer           = [[TKStuTimerView alloc] init];
                [contentView addSubview:_stuTimer];

                [_stuTimer mas_remakeConstraints:^(MASConstraintMaker *make) {
                    make.centerX.equalTo(contentView.mas_centerX);
                    make.centerY.equalTo(contentView.mas_centerY);
                }];
            }

            if (isStatus && !isRestart) { // 开始倒计时
                [self.stuTimer startCountdownWithMinute:(long)minute
                                                 second:(long)second
                                         receiveMsgTime:time];
            } else if (!isStatus && !isRestart) { //暂停
                [self.stuTimer pauseTimerWithTimerArray:timerArray];
            } else if (!isStatus && isRestart) { // 重新开始
                [self.stuTimer startCountdownWithMinute:(long)minute
                                                 second:(long)second
                                         receiveMsgTime:time];
                [self.stuTimer pauseTimerWithTimerArray:timerArray];
            }
        }
    } else {
        if (_stuTimer) {
            // 关闭y计时器
            [_stuTimer removeFromSuperview];
            _stuTimer = nil;
        }
        if (_timerView) {
            // 关闭y计时器
            [_timerView removeFromSuperview];
            _timerView = nil;
        }
    }
}

#pragma mark - 远程信令处理方法
- (void)onRemoteMsgWithClassBegin:(BOOL)add
                               ID:(NSString *)msgID
                             Name:(NSString *)msgName
                               TS:(unsigned long)ts
                             Data:(NSObject *)data
                           InList:(BOOL)inlist {

    [self invalidateClassCurrentTime];

    if (self.iOurVideoView.hidden) { self.iOurVideoView.hidden = NO; }

    if (self.iTeacherVideoView.hidden) { self.iTeacherVideoView.hidden = NO; }

    // 白板退出全屏
    [[NSNotificationCenter defaultCenter] postNotificationName:sChangeWebPageFullScreen
                                                        object:@(NO)];

    _iSessionHandle.isClassBegin = YES;
    _iClassStartTime             = ts;

    // 上课之前将自己的音视频关掉
    if (!_roomJson.configuration.autoOpenAudioAndVideoFlag && _isLocalPublish) {
        _iSessionHandle.localUser.publishState = TKUser_PublishState_NONE;
        [self unPlayVideo:_iSessionHandle.localUser];
    }

    if (_iUserType == TKUserType_Student && _roomJson.configuration.beforeClassPubVideoFlag &&
        !_roomJson.configuration.autoOpenAudioAndVideoFlag) {

        if (_iSessionHandle.localUser.publishState != TKUser_PublishState_NONE) {
            _isLocalPublish = false;
            [_iSessionHandle
                sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                       Publish:(TKUser_PublishState_NONE)completion:nil];
        }
    } else if (_iUserType == TKUserType_Student && !_isLocalPublish &&
               !_roomJson.configuration.autoOpenAudioAndVideoFlag) {
        if (_iSessionHandle.localUser.publishState != TKUser_PublishState_NONE) {
            _isLocalPublish = false;
            [_iSessionHandle
                sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                       Publish:(TKUser_PublishState_NONE)completion:nil];
        }
    }

    if (_iSessionHandle.isPlayMedia && _iUserType == TKUserType_Teacher) {

        [_iSessionHandle sessionHandleUnpublishMedia:nil];
    }

    if (_iUserType == TKUserType_Teacher && _iSessionHandle.isPlayback == NO) {

        if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
            _isLocalPublish = false;
            [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                    Publish:(TKPublishStateBOTH)completion:nil];
        }
    }

    if ((self.playbackMaskView.iProgressSlider.value < 0.01 && _iSessionHandle.isPlayback == YES &&
         self.playbackMaskView.playButton.isSelected == YES) ||
        _iSessionHandle.isPlayback == NO) {
        [TKUtil showMessage:TKMTLocalized(@"Class.Begin")];
    }

    if (!_iSessionHandle.isPlayback && !_roomJson.configuration.beforeClassPubVideoFlag &&
        _iSessionHandle.isPlayback == NO) {
        if (_iUserType == TKUserType_Teacher ||
            (_iUserType == TKUserType_Student &&
             _roomJson.configuration.autoOpenAudioAndVideoFlag)) {

            if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
                _isLocalPublish = false;
                [_iSessionHandle
                    sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                           Publish:(TKPublishStateBOTH)completion:^(NSError *error){

                                                   }];
            }
        }
    } else if (_iUserType == TKUserType_Teacher &&
               _roomJson.configuration.autoOpenAudioAndVideoFlag &&
               _iSessionHandle.isPlayback == NO) {
        if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
            _isLocalPublish = false;
            [_iSessionHandle
                sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                       Publish:(TKPublishStateBOTH)completion:^(NSError *error){

                                               }];
        }
    } else if (_iUserType == TKUserType_Student &&
               _roomJson.configuration.autoOpenAudioAndVideoFlag &&
               _iSessionHandle.isPlayback == NO) {
        if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
            _isLocalPublish = false;
            [_iSessionHandle
                sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                       Publish:(TKPublishStateBOTH)completion:^(NSError *error){

                                               }];
        }
    }

    //  涂鸦权限:
    //	1. 1v1学生根据配置项设置
    //	2. 其他情况，没有涂鸦权限
    //	3. 非老师断线重连不可涂鸦。
    //  4. 发送:1 1v1 学生发送 2 学生发送，老师不发送

    //如果是1v1并且是学生角色
    BOOL tIsTeacherOrAssis = _iUserType == TKUserType_Teacher || _iUserType == TKUserType_Assistant;
    BOOL isStdAndRoomOne =
        _iRoomType == TKRoomType_One && _iSessionHandle.localUser.role == TKUserType_Student;
    BOOL isCanDraw = isStdAndRoomOne ? _roomJson.configuration.canDrawFlag : tIsTeacherOrAssis;
    [_iSessionHandle configureDraw:isCanDraw
                            isSend:YES // isStdAndRoomOne ? YES : !tIsTeacherOrAssis
                                to:sTellAll
                            peerID:_iSessionHandle.localUser.peerID];

    [_iSessionHandle sessionHandlePubMsg:sUpdateTime
                                      ID:sUpdateTime
                                      To:_iSessionHandle.localUser.peerID
                                    Data:@""
                                    Save:false
                         AssociatedMsgID:nil
                        AssociatedUserID:nil
                                 expires:0
                              completion:^(NSError *error){

                              }];
    [self.pageControl setup];
    [self startClassBeginTimer];

    [self refreshUI];

    //上课后需要同步老师的布局样式
    if (_iSessionHandle.localUser.role == TKUserType_Teacher && inlist == NO) {
        //发送信令
        NSString *style = nil;
        if (self.viewStyle == oneToOne) {
            style = @"oneToOne";
        } else if (self.viewStyle == oneToOneDoubleDivision) {
            style = @"oneToOneDoubleDivision";
        } else if (self.viewStyle == oneToOneDoubleVideo) {
            style = @"oneToOneDoubleVideo";
        }

        [TKStylePopView publishStyleSignalingWithStyle:style tellID:sTellAll];
    }
}

- (void)onRemoteMsgWithClassEnd:(BOOL)add
                             ID:(NSString *)msgID
                           Name:(NSString *)msgName
                             TS:(unsigned long)ts
                           Data:(NSObject *)data
                         InList:(BOOL)inlist {

     [TKUtil showMessage:TKMTLocalized(@"Class.Over")];
    
    // 重置距离下课还有5分钟的提醒项
    _isRemindClassEnd = NO;
    
    // 下课后时间归零
    [self invalidateClassBeginTime];
    [self.navbarView setTime:0];
    
    // 隐藏画笔工具
    self.brushToolView.hidden = YES;
    [self.brushToolView hideSelectorView];
    [TKScreenShotFactory sharedFactory].canDraw = NO;
    
    // 刷新页面
    [self refreshUI];
    [self tapTable:nil];
    
    if (self.iOurVideoView.hidden) { self.iOurVideoView.hidden = NO; }
    if (self.iTeacherVideoView.hidden) { self.iTeacherVideoView.hidden = NO; }
    
    // 未到下课时间： 老师点下课 —> 下课后不离开教室
    // 下课时间到，课程结束，一律离开
    if (_roomJson.configuration.forbidLeaveClassFlag && _roomJson.configuration.endClassTimeFlag) {
        _iAfterClassTimer = [NSTimer scheduledTimerWithTimeInterval:1
                                                               target:self
                                                             selector:@selector(onClassCurrentTimer)
                                                             userInfo:nil
                                                              repeats:YES];
        [_iAfterClassTimer setFireDate:[NSDate date]];
        [[NSRunLoop currentRunLoop] addTimer:_iAfterClassTimer forMode:NSRunLoopCommonModes];
    }

    // 是否取消授权
    BOOL isStdAndRoomOne = (_roomJson.roomtype == TKRoomTypeOneToOne &&
                            _iSessionHandle.localUser.role == TKUserType_Student);
    bool tIsTeacherOrAssis = (_iSessionHandle.localUser.role == TKUserType_Teacher ||
                              _iSessionHandle.localUser.role == TKUserType_Assistant);
    [_iSessionHandle configureDraw:isStdAndRoomOne ? _roomJson.configuration.canDrawFlag : false
                            isSend:isStdAndRoomOne ? YES : !tIsTeacherOrAssis
                                to:sTellAll
                            peerID:_iSessionHandle.localUser.peerID];

    /*
    [[TKEduSessionHandle shareInstance] configureDraw:NO
                                               isSend:YES
                                                   to:sTellAll
                                               peerID:_iSessionHandle.localUser.peerID];
     */
    
    if (_iSessionHandle.localUser.role == TKUserType_Teacher) {
        
        if (_iSessionHandle.isOnlyAudioRoom) {
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeAudioOnlyRoom];
        }
        
        [_iSessionHandle sessionHandleDelMsg:sAllAll
                                          ID:sAllAll
                                          To:sTellNone
                                        Data:@{}
                                  completion:nil];
        
        if (!_roomJson.configuration.forbidLeaveClassFlag && !_roomJson.configuration.beforeClassPubVideoFlag) {
            _isLocalPublish = false;
            [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                    Publish:TKPublishStateNONE
                                                 completion:^(NSError *error){}];
        }
    }
    else {
        // 非老师身份下课后退出教室
        // 下课是否允许离开教室
        if (_roomJson.configuration.forbidLeaveClassFlag == NO) {
            [self.iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error)
             {
                if (error) {
                    TKLog(@"退出房间错误: %@", error);
                }
            }];
        }
        
    }

}

- (void)onRemoteMsgWithUpdateTime:(BOOL)add
                               ID:(NSString *)msgID
                             Name:(NSString *)msgName
                               TS:(unsigned long)ts
                             Data:(NSObject *)data
                           InList:(BOOL)inlist {

    if (add) {
        //防止ts是毫秒单位
        if (ts / 10000000000 > 0) { ts = ts / 1000; }

        _iServiceTime = ts;
        _iLocalTime   = _iServiceTime - _iClassStartTime;

        _iHowMuchTimeServerFasterThenMe = ts - [[NSDate date] timeIntervalSince1970];

        if ([_iClassBeginTimer isValid] == NO) {
            _iClassBeginTimer = [NSTimer scheduledTimerWithTimeInterval:1
                                                                target:self
                                                              selector:@selector(onClassTimer)
                                                              userInfo:nil
                                                               repeats:YES];
            [_iClassBeginTimer setFireDate:[NSDate date]];
            [[NSRunLoop currentRunLoop] addTimer:_iClassBeginTimer forMode:NSRunLoopCommonModes];
        }
    }
}

- (void)onRemoteMsgWithMuteAudio:(BOOL)add
                              ID:(NSString *)msgID
                            Name:(NSString *)msgName
                              TS:(unsigned long)ts
                            Data:(NSObject *)data
                          InList:(BOOL)inlist {

    int tPublishState              = _iSessionHandle.localUser.publishState;
    NSString *tPeerId              = _iSessionHandle.localUser.peerID;
    _iSessionHandle.isAllMuteAudio = add ? true : false;
    _isLocalPublish                = false;
    if (tPublishState != TKPublishStateVIDEOONLY) {
        [_iSessionHandle sessionHandleChangeUserPublish:tPeerId
                                                Publish:(tPublishState) +
                                                        (_iSessionHandle.isAllMuteAudio
                                                             ? (-TKPublishStateAUDIOONLY)
                                                             : (TKPublishStateAUDIOONLY))completion
                                                       :^(NSError *error){

                                                       }];
    } else {
        [_iSessionHandle sessionHandleChangeUserPublish:tPeerId
                                                Publish:(_iSessionHandle.isAllMuteAudio
                                                             ? (TKPublishStateNONE)
                                                             : (TKPublishStateAUDIOONLY))completion
                                                       :^(NSError *error){

                                                       }];
    }
}

- (void)onRemoteMsgWithStreamFailure:(BOOL)add
                                  ID:(NSString *)msgID
                                Name:(NSString *)msgName
                                  TS:(unsigned long)ts
                                Data:(NSObject *)data
                              InList:(BOOL)inlist {

    // 收到用户发布失败的消息
    NSDictionary *tDataDic = @{};
    if ([data isKindOfClass:[NSString class]]) {
        NSString *tDataString = [NSString stringWithFormat:@"%@", data];
        NSData *tJsData       = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
        tDataDic              = [NSJSONSerialization JSONObjectWithData:tJsData
                                                   options:NSJSONReadingMutableContainers
                                                     error:nil];
    }
    if ([data isKindOfClass:[NSDictionary class]]) { tDataDic = (NSDictionary *)data; }

    NSString *tPeerId     = [tDataDic objectForKey:@"studentId"];
    NSInteger failureType = [tDataDic objectForKey:@"failuretype"]
                                ? [[tDataDic objectForKey:@"failuretype"] integerValue]
                                : 0;

    // 如果这个发布失败的用户是自己点击上台的，需要对自己进行上台失败错误原因进行提示。
    //        if ([[[_iSessionHandle pendingUserDic] allKeys] containsObject:tPeerId]) {
    if ([_iSessionHandle getUserWithPeerId:tPeerId].role == TKUserType_Teacher) {

        switch (failureType) {
            case 1:
                [TKUtil showMessage:TKMTLocalized(@"Prompt.StudentUdpOnStageError")];
                break;

            case 2:
                [TKUtil showMessage:TKMTLocalized(@"Prompt.StudentTcpError")];
                break;

            case 3:
                [TKUtil showMessage:TKMTLocalized(@"Prompt.exceeds")];
                break;

            case 4: {

                [TKUtil
                    showMessage:[NSString
                                    stringWithFormat:
                                        @"%@%@", [_iSessionHandle localUser].nickName,
                                        TKMTLocalized(
                                            @"Prompt.BackgroundCouldNotOnStage")]]; //拼接上用户名
                break;
            }
            case 5:
                [TKUtil showMessage:TKMTLocalized(@"Prompt.StudentUdpError")];
                break;

            default:
                break;
        }
    }
}

- (void)onRemoteMsgWithVideoSplitScreen:(BOOL)add
                                     ID:(NSString *)msgID
                                   Name:(NSString *)msgName
                                     TS:(unsigned long)ts
                                   Data:(NSObject *)data
                                 InList:(BOOL)inlist {

    //白板全屏状态下不执行分屏回调
    if (_iSessionHandle.iIsFullState) { return; }

    //    [self tapTable:nil];
    NSDictionary *tDataDic = [self convertWithData:data];

    if (![tDataDic objectForKey:@"isTeacher"]) { return; }

    if (add) {
        [self.view addSubview:self.splitScreenView];

        //分屏视频 按4 ； 3显示
        CGRect frame;
        CGFloat width  = self.splitScreenView.frame.size.width;
        CGFloat height = self.splitScreenView.frame.size.height;

        if (height / width >= 3.0 / 4) {
            frame = CGRectMake(0, (height - width * 3.0 / 4) / 2, width, width * 3.0 / 4);
        } else {
            frame = CGRectMake((width - height * 4.0 / 3) / 2, 0, height * 4.0 / 3, height);
        }
        self.iTeacherVideoView.isSplit = YES;
        self.iTeacherVideoView.frame   = frame;
        [self.view bringSubviewToFront:self.iTeacherVideoView];

    } else {
        [self.splitScreenView removeFromSuperview];
        self.splitScreenView           = nil;
        self.iTeacherVideoView.isSplit = NO;

        [self layoutViews];
    }
}

- (void)onRemoteMsgWithVideoWhiteboard:(BOOL)add
                                    ID:(NSString *)msgID
                                  Name:(NSString *)msgName
                                    TS:(unsigned long)ts
                                  Data:(NSObject *)data
                                InList:(BOOL)inlist {

    if (add) {
        if (_iMediaView) { //媒体

            [_iMediaView loadWhiteBoard];
        }
        if (_iFileView) { //电影

            [_iFileView loadWhiteBoard];
        }
    } else {
        if (_iMediaView) { //媒体

            [_iMediaView hiddenVideoWhiteBoard];
        }
        if (_iFileView) { [_iFileView hiddenVideoWhiteBoard]; }
    }
}

- (void)onRemoteMsgWithEveryoneBanChat:(BOOL)add
                                    ID:(NSString *)msgID
                                  Name:(NSString *)msgName
                                    TS:(unsigned long)ts
                                  Data:(NSObject *)data
                                InList:(BOOL)inlist {

    _iSessionHandle.isAllShutUp = add;
    if (add && inlist &&
        _iSessionHandle.localUser.role == TKUserType_Student) { //如果是全体禁言并且后进入课堂

        [_iSessionHandle sessionHandleChangeUserProperty:_iSessionHandle.localUser.peerID
                                                TellWhom:sTellAll
                                                     Key:sDisablechat
                                                   Value:@(true)
                                              completion:nil];
    }

    NSMutableDictionary *tDic = [NSMutableDictionary dictionary];
    [tDic setValue:@(add) forKey:@"isBanSpeak"];

    [[NSNotificationCenter defaultCenter] postNotificationName:sEveryoneBanChat object:tDic];

    TKChatMessageModel * chatMessageModel =
        [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                               role:TKChatRoleTypeMe
                                            message:add ? TKMTLocalized(@"Prompt.BanChatInView")
                                                        : TKMTLocalized(@"Prompt.CancelBanChatInView")
                                            cospath:nil
                                           userName:_iSessionHandle.localUser.nickName
                                             fromid:nil time:[TKUtil currentTime]];
    if (add) chatMessageModel.iMessageTypeColor = [TKTheme colorWithPath:@"ClassRoom.TKChatViews.chatEveryoneBanChat"];
    [_iSessionHandle addOrReplaceMessage:chatMessageModel];
    [self.chatViewNew reloadData];
}

- (void)onRemoteMsgWithWBFullScreen:(BOOL)add
                                 ID:(NSString *)msgID
                               Name:(NSString *)msgName
                                 TS:(unsigned long)ts
                               Data:(NSObject *)data
                             InList:(BOOL)inlist {

    if (_roomJson.configuration.coursewareFullSynchronize) {

        /* stream_video 不处理 */
        NSDictionary *dic = [self convertWithData:data];

        if ([dic[@"fullScreenType"] isEqualToString:@"courseware_file"] ||
            [dic[@"fullScreenType"] isEqualToString:@"stream_media"]) {

            _pageControl.fullScreen.selected = self.isRemoteFullScreen = add;
            [[NSNotificationCenter defaultCenter] postNotificationName:sChangeWebPageFullScreen
                                                                object:@(add)];
            [self changeVideoFrame:add];
        }
        
        if ([dic[@"fullScreenType"] isEqualToString:@"courseware_file"]) {
            [_pageControl resetBtnStates];
        }
    }
}
#pragma mark - 屏幕截屏
- (void)screenShotRecoveryAfterGetinClass:(NSNotification *)notification {
    NSDictionary *dict     = notification.userInfo;
    NSDictionary *response = [dict objectForKey:TKWhiteBoardOnRoomConnectedRoomMsgKey];

    NSString *tJsonDataJsonString;
    if (response) {
        NSData *tJsonData = [NSJSONSerialization dataWithJSONObject:response
                                                            options:NSJSONWritingPrettyPrinted
                                                              error:nil];
        tJsonDataJsonString =
            [[NSString alloc] initWithData:tJsonData encoding:NSUTF8StringEncoding];
    } else {
        tJsonDataJsonString = @"";
    }

    if (!response || response.allKeys.count == 0) { return; }

    NSDictionary *msglist         = [response objectForKey:@"msglist"];
    NSMutableArray *screenShotMSG = [NSMutableArray array];

    for (NSString *key in msglist.allKeys) {
        NSDictionary *dictionary  = [msglist objectForKey:key];
        NSString *associatedMsgID = [dictionary objectForKey:@"associatedMsgID"];
        NSString *name            = [dictionary objectForKey:@"name"];
        if ([associatedMsgID isEqualToString:sClassBegin] ||
            [associatedMsgID hasPrefix:@"CaptureImg"]) {
            // 教室截屏 桌面截屏
            if ([key hasPrefix:@"CaptureImg"]) {
                [screenShotMSG addObject:[msglist objectForKey:key]];
                
            }
        }
        else if ([associatedMsgID hasPrefix:@"CaptureImg"]) {
            if ([name isEqualToString:sSharpsChange]) {
                [screenShotMSG addObject:[msglist objectForKey:key]];
            }
        }
        else {
            if ([key hasPrefix:@"CaptureImg"]) {
                [screenShotMSG addObject:[msglist objectForKey:key]];
            }
        }
        
        // 绘制数据
        if ([key containsString:@"SharpsChange_captureImgBoardvideoCapture"]) {
            NSDictionary *data = [msglist objectForKey:key];
            NSString *associatedMsgID = [data objectForKey:@"associatedMsgID"];
            if ([associatedMsgID isEqualToString:@"CaptureImg_videoCapture"]) {
                [screenShotMSG addObject: data];
            }
        }
    }

    if (screenShotMSG.count != 0) {
        screenShotMSG = [[screenShotMSG sortedArrayUsingComparator:^NSComparisonResult(id _Nonnull obj1, id _Nonnull obj2){
            return [[obj1 objectForKey:@"seq"] compare:[obj2 objectForKey:@"seq"]];
        }] mutableCopy];
        
        [screenShotMSG enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
             [TKScreenShotFactory captureImgWithParam:obj msgName:obj[@"name"] delete:NO];
         }];
        
        
    }
    
    
}
// 画中画
- (void)changeVideoFrame:(BOOL)isFull {

    if (_iSessionHandle.isOnlyAudioRoom == YES) return;
    if (!moveView) return;
    if (isFull == _iSessionHandle.isPicInPic) return; //相同消息不执行

    // 有 视频才响应画中画
    if (moveView.iRoomUser.publishState != TKUser_PublishState_VIDEOONLY &&
        moveView.iRoomUser.publishState != TKUser_PublishState_BOTH) {

        return;
    }

    _iSessionHandle.isPicInPic = isFull;
    moveView.hidden            = NO;
    // 双师隐藏按钮
    if (_iSessionHandle.roomLayout == oneToOneDoubleDivision) { _showHideVideoBtn.hidden = isFull; }

    if (isFull) {
        // 缓存尺寸
        videoOriginFrame = moveView.frame;

        moveView.width  = ScreenW * 0.25;
        moveView.height = moveView.width / 4 * 3;
        moveView.x      = ScreenW - moveView.width - 5.;
        moveView.y      = ScreenH - moveView.height - 5.;

        [[UIApplication sharedApplication].keyWindow addSubview:moveView];

        [moveView addGestureRecognizer:_longPressGesture];

        if (!moveView.iRoomUser.hasVideo ||
            (moveView.iRoomUser.publishState != TKUser_PublishState_BOTH &&
             moveView.iRoomUser.publishState != TKUser_PublishState_VIDEOONLY) ||
            _iSessionHandle.isOnlyAudioRoom) {
            // 无视频不显示画中画
            moveView.hidden = YES;
        }

    } else {

        moveView.frame = videoOriginFrame;
        
        
        [self.view addSubview:moveView];
        [moveView removeGestureRecognizer:_longPressGesture];

        if (self.iMediaView) { [self.backgroundImageView bringSubviewToFront:self.iMediaView]; }
        if (self.iFileView) { [self.backgroundImageView bringSubviewToFront:self.iFileView]; }
    }

    [_navbarView hideAllButton:isFull];
    // 隐藏小视频上的按钮 屏蔽操作
    [moveView maskViewChangeForPicInPicWithisShow:isFull];
}
#pragma mark - 设备检测
- (void)noCamera {

    TKAlertView *alert = [[TKAlertView alloc] initWithTitle:@""
                                                contentText:TKMTLocalized(@"Prompt.NeedCamera")
                                               confirmTitle:TKMTLocalized(@"Prompt.Sure")];
    [alert show];
}

- (void)noMicrophone {
    TKAlertView *alert =
        [[TKAlertView alloc] initWithTitle:TKMTLocalized(@"Prompt.NeedMicrophone.Title")
                               contentText:TKMTLocalized(@"Prompt.NeedMicrophone")
                              confirmTitle:TKMTLocalized(@"Prompt.Sure")];
    [alert show];
}

- (void)noCameraAndNoMicrophone {

    TKAlertView *alert =
        [[TKAlertView alloc] initWithTitle:@""
                               contentText:TKMTLocalized(@"Prompt.NeedCameraNeedMicrophone")
                              confirmTitle:TKMTLocalized(@"Prompt.Sure")];
    [alert show];
}

#pragma mark UIGestureRecognizerDelegate
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
       shouldReceiveTouch:(UITouch *)touch {
    if ([NSStringFromClass([touch.view class]) isEqualToString:@"UITableViewCellContentView"] ||
        [NSStringFromClass([touch.view class]) isEqualToString:@"TKTextViewInternal"] ||
        [NSStringFromClass([touch.view class]) isEqualToString:@"UIButton"] ||
        [touch.view.superview isKindOfClass:[UICollectionViewCell class]]) {
        return NO;
    } else {

        [self tapTable:nil];
        return !_iSessionHandle.iIsCanDraw;
    }
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
    shouldRecognizeSimultaneouslyWithGestureRecognizer:
        (UIGestureRecognizer *)otherGestureRecognizer {
    return true;
}

- (void)tapTable:(UIGestureRecognizer *)gesture {
    [[NSNotificationCenter defaultCenter] postNotificationName:stouchMainPageNotification
                                                        object:nil];

    [_iTeacherVideoView hidePopMenu];
    [_iOurVideoView hidePopMenu];
}
// 小视频本地移动
- (void)fullScreenVideoLongPressClick:(UIGestureRecognizer *)longGes {

    TKCTVideoSmallView *currentVedioView = (TKCTVideoSmallView *)longGes.view;

    if (UIGestureRecognizerStateBegan == longGes.state) {
        self.iStrtCrtVideoViewP = [longGes locationInView:currentVedioView];
    }

    if (UIGestureRecognizerStateChanged == longGes.state) {
        //为了与老代码保持一致，在手势变化的时候判断
        //按道理来讲在手势结束后判断是否超出范围最为合理
        CGRect videoViewFrame = currentVedioView.frame;

        CGPoint point           = [longGes locationInView:self.view];
        videoViewFrame.origin.x = point.x - self.iStrtCrtVideoViewP.x;
        videoViewFrame.origin.y = point.y - self.iStrtCrtVideoViewP.y;

        if (CGRectGetMinX(videoViewFrame) < CGRectGetMinX(self.view.frame)) {
            videoViewFrame.origin.x = 0;
        }

        if (CGRectGetMaxX(videoViewFrame) > CGRectGetMaxX(self.view.frame)) {
            videoViewFrame.origin.x =
                CGRectGetMaxX(self.view.frame) - CGRectGetWidth(currentVedioView.frame);
        }

        if (CGRectGetMinY(videoViewFrame) < CGRectGetMinY(self.view.frame)) {
            videoViewFrame.origin.y = 0;
        }

        if (CGRectGetMaxY(videoViewFrame) > CGRectGetMaxY(self.view.frame)) {
            videoViewFrame.origin.y =
                CGRectGetMaxY(self.view.frame) - CGRectGetHeight(currentVedioView.frame);
        }

        currentVedioView.frame = videoViewFrame;
    }
}

#pragma mark - 计时器
- (void)checkPlayVideo {

    BOOL tHaveRaiseHand    = NO;
    BOOL tIsMuteAudioState = YES;

    for (TKRoomUser *usr in [_iSessionHandle userStdntAndTchrArray]) {
        BOOL tBool = [[usr.properties objectForKey:@"raisehand"] boolValue];
        if (tBool && !tHaveRaiseHand) { tHaveRaiseHand = YES; }
        if ((usr.publishState == TKPublishStateAUDIOONLY ||
             usr.publishState == TKPublishStateBOTH) &&
            usr.role != TKUserType_Teacher && tIsMuteAudioState) {

            tIsMuteAudioState = NO;
        }
    }

    if (_iUserType == TKUserType_Teacher) {
        _iSessionHandle.isAllMuteAudio = tIsMuteAudioState;
        _iSessionHandle.isunMuteAudio  = !tIsMuteAudioState;
    }
}

- (void)onClassCurrentTimer {

    if (!_iHowMuchTimeServerFasterThenMe) return;

    _iCurrentTime = [[NSDate date] timeIntervalSince1970] + _iHowMuchTimeServerFasterThenMe;

    NSTimeInterval interval = _roomJson.endtime - _iCurrentTime;
    NSInteger time          = interval;

    // 距下课 不足5分钟 "X分钟后课堂结束，请合理安排时间哦".  只需要提示一次！！！
    if (time <= 300) {
        
        if (_iSessionHandle.localUser.role != TKUserType_Teacher) {
            return;
        }
        if (!objc_getAssociatedObject(_iAfterClassTimer, @selector(onClassCurrentTimer))) {
            
            int minute = (int)time / 60;
            if (minute > 0) {
                [TKUtil
                 showMessage:[NSString stringWithFormat:@"%d%@", minute,
                              TKMTLocalized(@"Prompt.ClassEndTime")]];
                objc_setAssociatedObject(_iAfterClassTimer, @selector(onClassCurrentTimer),
                                         @"showOnce", OBJC_ASSOCIATION_RETAIN_NONATOMIC);
            }
            
        }
    }
    else if (time <= 0) {
        [TKUtil showMessage:TKMTLocalized(@"Prompt.ClassEnd")];
        
        [self.iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error)
         {
            if (error) {
                TKLog(@"退出房间错误: %@", error);
            }
        }];
    }
    
}

- (void)onClassTimer {

    //此处主要用于检测上课过程中进入后台后无法返回前台的状况
    BOOL isBackground = [_iSessionHandle.roomMgr.localUser.properties[sIsInBackGround] boolValue];
    if (([UIApplication sharedApplication].applicationState == UIApplicationStateActive) &&
        isBackground) {
        [_iSessionHandle sessionHandleChangeUserProperty:_iSessionHandle.localUser.peerID
                                                TellWhom:sTellAll
                                                     Key:sIsInBackGround
                                                   Value:@(NO)
                                              completion:nil];
        _iSessionHandle.roomMgr.inBackground = NO;
    }

    if (!_iHowMuchTimeServerFasterThenMe) return;

    _iCurrentTime = [[NSDate date] timeIntervalSince1970] + _iHowMuchTimeServerFasterThenMe;

    if (_roomJson.configuration.endClassTimeFlag) {

        //如果是回放 结束时间要比当前时间小
        if (_iSessionHandle.isPlayback) { return; }

        NSTimeInterval interval = _roomJson.endtime - _iCurrentTime;
        NSInteger time          = interval;
        //(2)未到下课时间： 老师未点下课->下课时间到->课程结束，一律离开
        //(3)到下课时间->提前5分钟给出提示语（老师，助教）->课程结束，一律离开
        if (time>0) {
            
            if (time <= 300 && _isRemindClassEnd == NO) {
                
                _isRemindClassEnd = YES;
                
                int minute		= (int)(time / 60);
                if (minute > 0) {
                    [TKUtil
                     showClassEndMessage:[NSString
                                          stringWithFormat:@"%d%@", minute,
                                          TKMTLocalized(@"Prompt.ClassEndTime")]];
                }
                
                else {
                    
                    int second        = time % 60;
                    [TKUtil
                     showClassEndMessage:[NSString
                                          stringWithFormat:@"%d%@", second,
                                          TKMTLocalized(
                                                      @"Prompt.ClassEndTimeseconds")]];
                }
                            }
        }
        else {
            [TKUtil showMessage:TKMTLocalized(@"Prompt.ClassEnd")];
            [self.iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error)
             {
                if (error) {
                    TKLog(@"退出房间错误: %@", error);
                }
            }];
        }
    }

    //设置当前时间
    if (!_iSessionHandle.isPlayback) { [self.navbarView setTime:_iLocalTime]; }
    _iLocalTime++;
}

- (void)invalidateClassBeginTime {

    if (_iClassBeginTimer) {
        [_iClassBeginTimer invalidate];
        _iLocalTime      = 0;
        _iClassBeginTimer = nil;
    }
}

- (void)invalidateClassCurrentTime {
    if (_iAfterClassTimer) {

        objc_setAssociatedObject(_iAfterClassTimer, @selector(onClassCurrentTimer), nil,
                                 OBJC_ASSOCIATION_RETAIN_NONATOMIC);

        [_iAfterClassTimer invalidate];
        _iAfterClassTimer = nil;
    }
}

- (void)startClassBeginTimer {
    _iLocalTime = 0;
    [_iClassBeginTimer setFireDate:[NSDate date]];
}

#pragma mark - PubMsg/DelMsg 答题器
- (void)roomWhiteBoardOnRemotePubMsg:(NSNotification *)notification {
    NSDictionary *message =
        [notification.userInfo objectForKey:TKWhiteBoardNotificationUserInfoKey];
    NSString *name = [message objectForKey:@"name"];

    if ([name isEqualToString:@"UpdateTime"]) {
        return;
    }
    if ([name isEqualToString:@"GetQuestionCount"]) {
        //老师收到学生的答题
        if (_iSessionHandle.localUser.role != TKUserType_Student) {
            //老师
            TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
            answerSheet.dict               = message;
        }
    }

    /////////////////////////////////小白板信令///////////////////////////////////
    [self.miniWB handleSignal:message isDel:NO];

    /////////////////////////////////截屏信令///////////////////////////////////
    [TKScreenShotFactory handleSignal:message isDel:NO];

    /////////////////////////////////视频标注信令///////////////////////////////////
    [[TKEduSessionHandle shareInstance].mediaMarkView handleSignal:message isDel:NO];
}

- (void)roomWhiteBoardOnRemoteDelMsg:(NSNotification *)notification {
    NSDictionary *message =
        [notification.userInfo objectForKey:TKWhiteBoardNotificationUserInfoKey];
    if (!message || ![message isKindOfClass:[NSDictionary class]]) { return; }

    /////////////////////////////////截屏信令///////////////////////////////////
    [self.miniWB handleSignal:message isDel:YES];

    /////////////////////////////////小白板信令///////////////////////////////////
    [TKScreenShotFactory handleSignal:message isDel:YES];

    /////////////////////////////////视频标注信令///////////////////////////////////
    [[TKEduSessionHandle shareInstance].mediaMarkView handleSignal:message isDel:YES];
}

#pragma mark - 收到点击相机/相册的通知
- (void)uploadPhotos:(NSNotification *)notify {
    if ([notify.object isEqualToString:sTakePhotosUploadNotification]) {
        //拍照上传
        [self chooseAction:1 delay:NO];
    } else if ([notify.object isEqualToString:sChoosePhotosUploadNotification]) {
        //从图库上传
        [self chooseAction:0 delay:YES];
    }
}

- (void)cancelUpload {
    [self removProgressView];
}

- (void)uploadProgress:(int)req
        totalBytesSent:(int64_t)totalBytesSent
            bytesTotal:(int64_t)bytesTotal {
    float progress = totalBytesSent / bytesTotal;
    [_uploadImageView setProgress:progress];
}

- (void)uploadFileResponse:(id _Nullable)Response req:(int)req {
    if (Response == nil && req == -1) {
        [TKUtil showMessage:TKMTLocalized(@"UploadPhoto.Error")];
    } else if (!req && [Response isKindOfClass:[NSDictionary class]]) {
        
        NSDictionary *tFileDic              = (NSDictionary *)Response;

        switch ([TKEduSessionHandle shareInstance].updateImageUseType) {
            case TKUpdateImageUseType_Document:
            {
                TKDocmentDocModel *tDocmentDocModel = [[TKDocmentDocModel alloc] init];
                [tDocmentDocModel setValuesForKeysWithDictionary:tFileDic];
                [tDocmentDocModel dynamicpptUpdate];
                tDocmentDocModel.filetype = @"jpeg";
                [_iSessionHandle addOrReplaceDocmentArray:tDocmentDocModel];
                [_iSessionHandle addDocMentDocModel:tDocmentDocModel To:sTellAllExpectSender];
                [_iSessionHandle publishtDocMentDocModel:tDocmentDocModel
                                                      To:sTellAllExpectSender
                                              aTellLocal:YES];
                [self removProgressView];
                break;
            }
            case TKUpdateImageUseType_Message:
            {
                NSString * cospath = [Response objectForKey:@"cospath"] ?: @"";
                NSString * swfpath = [Response objectForKey:@"swfpath"] ?: @"";
                
                NSMutableDictionary * msgDic = [[NSMutableDictionary alloc] initWithCapacity:3];
                [msgDic setObject:@"onlyimg" forKey:@"msgtype"];
                [msgDic setObject:cospath forKey:@"cospath"];
                [msgDic setObject:swfpath forKey:@"msg"];
                
                NSString *time = [TKUtil currentTime];
                NSDictionary *messageDic = @{@"type":@0,@"time":time};
                
                [[TKEduSessionHandle shareInstance] sessionHandleSendMessage:msgDic toID:sTellAll extensionJson:messageDic];
                break;
            }
        }
        
    } else {
        TKLog(@"error - image update - %@", Response);
    }
}

- (void)getMeetingFileResponse:(id _Nullable)Response {
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context {

    if ([@"idleTimerDisabled" isEqualToString:keyPath] && _iSessionHandle.iIsJoined &&
        ![[change objectForKey:@"new"] boolValue]) {
        [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    }
}

- (void)changeServer:(NSString *)server {
    if ([server isEqualToString:self.currentServer]) { return; }
    self.currentServer = server;
    [[NSUserDefaults standardUserDefaults] setObject:self.currentServer forKey:@"server"];
}

#pragma mark Private
// 获取礼物数
- (void)getTrophyNumber {
    // 老师不需要获取礼物
    if (_iSessionHandle.localUser.role != TKUserType_Student || _iSessionHandle.isPlayback == YES) {
        return;
    }

    // 学生断线重连需要获取礼物
    [TKEduNetManager getGiftinfo:_roomJson.roomid
        aParticipantId:_roomJson.thirdid
        aHost:sHost
        aPort:sPort
        aGetGifInfoComplete:^(id _Nullable response) {
            dispatch_async(dispatch_get_main_queue(), ^{
                int result = 0;
                result     = [[response objectForKey:@"result"] intValue];
                if (!result || result == -1) {

                    NSArray *tGiftInfoArray = [response objectForKey:@"giftinfo"];
                    int giftnumber          = 0;
                    for (int i = 0; i < [tGiftInfoArray count]; i++) {
                        if (_iSessionHandle.localUser.peerID.length) {
                            NSDictionary *tDicInfo = [tGiftInfoArray objectAtIndex:i];
                            if ([[tDicInfo objectForKey:@"receiveid"]
                                    isEqualToString:_iSessionHandle.localUser.peerID]) {
                                giftnumber = [tDicInfo objectForKey:@"giftnumber"]
                                                 ? [[tDicInfo objectForKey:@"giftnumber"] intValue]
                                                 : 0;
                                break;
                            }
                        }
                    }

                    self.iSessionHandle.localUser.properties[sGiftNumber] = @(giftnumber);
                    
//                    [_iSessionHandle sessionHandleChangeUserProperty:self.iSessionHandle.localUser.peerID
//                                                            TellWhom:sTellAll
//                                                                 Key:sGiftNumber
//                                                               Value:@(giftnumber)
//                                                          completion:nil];
                }
            });

        }
        aGetGifInfoError:^int(NSError *_Nullable aError) {
            TKLog(@"获取奖杯数量失败");
            return -1;
        }];
}

#pragma mark - 清理
- (void)clearAllData {
    
    [[TKPopViewHelper sharedInstance] clearAfterClass];
//    [self.iSessionHandle.whiteBoardManager
//     showDocumentWithFile:_iSessionHandle.whiteBoard
//     isPubMsg:YES];
    
    [self.iSessionHandle.whiteBoardManager disconnect:nil];
    [self.iSessionHandle.whiteBoardManager resetWhiteBoardAllData];

    [self.iPlayVideoViewDic removeAllObjects];
    _iSessionHandle.onPlatformNum = 0;

    // 播放的MP4前，先移除掉上一个MP4窗口
    _iSessionHandle.iCurrentMediaDocModel = nil;
    if (self.iMediaView) {
        [self.iMediaView deleteWhiteBoard];
        [self.iMediaView removeFromSuperview];
        self.iMediaView = nil;
    }
    if (self.iFileView) {
        [self.iFileView removeFromSuperview];
        self.iFileView = nil;
    }
    if (self.iScreenView) {
        [self.iScreenView removeFromSuperview];
        self.iScreenView = nil;
    }

    /*
     [self.splitScreenView deleteAllVideoSmallView];

     [self.iStudentSplitScreenArray removeAllObjects];

     self.splitScreenView.hidden = YES;
     */
    // fix bug:全员奖励页面是显示在windows上的，如果网断了，退出教室，view还没消失
    UIWindow *window           = [UIApplication sharedApplication].keyWindow;
    NSEnumerator *subviewsEnum = [window.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:NSClassFromString(@"TKTrophyView")]) {
            [subview removeFromSuperview];
        }
    }

    //网络断开连接 清理掉所有小工具，防止在网络断开过程中 老师关闭了小工具 导致关闭的信令没接收到

    // 1.关闭答题卡
    TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
    [answerSheet removeFromSuperview];
    answerSheet = nil;

    // 3关闭计时器
    [_stuTimer removeFromSuperview];
    _stuTimer = nil;

    [_timerView removeFromSuperview];
    _timerView = nil;

    // 4关闭转盘
    [_dialView removeFromSuperview];
    _dialView = nil;

    // 5.关闭小白板
    self.miniWB.hidden = YES;
    [self.miniWB clear];
}

- (void)quitClearData {

    [_iSessionHandle configureDraw:false
                            isSend:NO
                                to:sTellAll
                            peerID:_iSessionHandle.localUser.peerID];
    [_iSessionHandle.whiteBoardManager roomWhiteBoardOnDisconnect:nil];
    [_iSessionHandle clearAllClassData];
    

    [self clearVideoViewData:_iTeacherVideoView];
    [self clearVideoViewData:_iOurVideoView];
    [self clearVideoViewData:moveView];
    
    [_iPlayVideoViewDic removeAllObjects];

    // 播放的MP4前，先移除掉上一个MP4窗口
    if (self.iMediaView) {
        [self.iMediaView removeFromSuperview];
        self.iMediaView = nil;
    }
    if (self.iFileView) {
        [self.iFileView removeFromSuperview];
        self.iFileView = nil;
    }
    if (self.iScreenView) {
        [self.iScreenView removeFromSuperview];
        self.iScreenView = nil;
    }

    if (self.navbarView.netTipView) { [self.navbarView.netTipView changeDetailSignImage:NO]; }
    if (self.netDetailView) {
        [self.netDetailView removeFromSuperview];
        self.netDetailView = nil;
    }

    /**暂时这么解决s双重奖杯*/
    [_iTeacherVideoView removeAllObserver];
    [_iOurVideoView removeAllObserver];
}

- (void)clearVideoViewData:(TKCTVideoSmallView *)videoView {
    videoView.isDrag = NO;
    if (videoView.iRoomUser != nil) {
        [self myUnPlayVideo:videoView.iRoomUser
                 aVideoView:videoView
                 completion:^(NSError *error) { TKLog(@"清理视频窗口完成!"); }];
    } else {
        [videoView clearVideoData];
    }
}

- (void)invalidateTimer {
    if (_iCheckPlayVideotimer) {
        [_iCheckPlayVideotimer invalidate];
        _iCheckPlayVideotimer = nil;
    }
    [self invalidateClassBeginTime];
    [self invalidateClassCurrentTime];
}
- (void)removProgressView {
    if (_uploadImageView) {
        [_uploadImageView removeFromSuperview];
        _uploadImageView   = nil;
        _iPickerController = nil;
    }
}

- (NSDictionary *)convertWithData:(id)data {
    NSDictionary *dataDic = @{};
    if ([data isKindOfClass:[NSString class]]) {
        NSString *tDataString = [NSString stringWithFormat:@"%@", data];
        NSData *tJsData       = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
        dataDic               = [NSJSONSerialization JSONObjectWithData:tJsData
                                                  options:NSJSONReadingMutableContainers
                                                    error:nil];
    } else if ([data isKindOfClass:[NSDictionary class]]) {
        dataDic = (NSDictionary *)data;
    }
    return dataDic;
}

- (void)removeNotificaton {

    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[UIApplication sharedApplication] removeObserver:self forKeyPath:@"idleTimerDisabled"];
}

#pragma mark - 懒加载
//创建画笔工具
- (TKBrushToolView *)brushToolView {

    if (_iSessionHandle.isPlayback == YES) { return nil; }

    if (_brushToolView == nil) {

        _brushToolView        = [[TKBrushToolView alloc] init];
        _brushToolView.hidden = YES;
        [_whiteboardBackView addSubview:_brushToolView];
        [_brushToolView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(_whiteboardBackView.mas_right);
            make.top.equalTo(_whiteboardBackView.mas_top);
        }];
    }

    return _brushToolView;
}

- (UIButton *)swapButton {
    if (!_swapButton) {
        _swapButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_swapButton addTarget:self
                        action:@selector(swapButtonAction:)
              forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:_swapButton];
    }
    return _swapButton;
}

- (UIView *)splitScreenView {
    if (!_splitScreenView) {
        _splitScreenView                 = [[UIView alloc] init];
        _splitScreenView.backgroundColor = [UIColor blackColor];
    }
    _splitScreenView.frame = self.view.bounds;

    return _splitScreenView;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    NSLog(@"roomController----dealloc");
}
- (void)didReceiveMemoryWarning {
}

@end

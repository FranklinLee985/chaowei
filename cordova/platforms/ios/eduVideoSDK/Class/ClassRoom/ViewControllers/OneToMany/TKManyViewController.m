//
//  TKCTMoreViewController.m
//  EduClass
//
//  Created by talkcloud on 2018/10/9.
//  Copyright © 2018年 talkcloud. All rights reserved.
//
#import "sys/utsname.h"
#import <objc/message.h>
#import "TKAnswerSheetView.h"
#import "TKBrushToolView.h"
#import "TKCTControlView.h" //  控制按钮视图
#import "TKCTListView.h"    //  文档、媒体
#import "TKCTNetDetailView.h"
#import "TKCTUserListView.h" // 用户列表视图
#import "TKChatMessageModel.h"
#import "TKDialView.h"
#import "TKDocmentDocModel.h"
#import "TKManyViewController+ImagePicker.h"
#import "TKManyViewController+Media.h"
#import "TKManyViewController+MediaMarkView.h"
#import "TKManyViewController+MiniWhiteBoard.h"
#import "TKManyViewController+Playback.h"
#import "TKManyViewController+WhiteBoard.h"
#import "TKManyViewController.h"
#import "TKPopView.h"
#import "TKProgressSlider.h"
#import "TKScreenShotFactory.h"
#import "TKSplitScreenView.h"
#import "TKStuTimerView.h"
#import "TKTimer.h"
#import "TKTimerView.h"
#import "TKToolsResponderView.h"
#import "TKUploadImageView.h"
#import "UIView+Drag.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <Photos/Photos.h>
#import <PhotosUI/PhotosUI.h>

#import "TKManyFreeLayoutView.h"
#import "TKManyNormalLayoutView.h"
#import "TKManySpeakerLayoutView.h"
#import "TKManyStylePopView.h"
#import "TKPopViewHelper.h"
#import "TKEyeCareManage.h"

static const CGFloat sViewCap = 5; // 小视频间距

@interface TKManyViewController () <
    TKEduBoardDelegate, TKEduSessionDelegate, UIGestureRecognizerDelegate, UIScrollViewDelegate,
    CAAnimationDelegate, TKEduNetWorkDelegate, TKPopViewDelegate, TKVideoViewDelegate> {
    //视频的宽高属性
    CGFloat _screenWidth; // 横屏屏幕宽度(适配x+)
    CGFloat _viewX;       // 横屏x坐标(适配x+)
    CGFloat _sBottomViewHeigh;
    CGFloat _sStudentVideoViewHeigh;
    CGFloat _sStudentVideoViewWidth;
    CGRect videoOriginFrame;      // 画中画视频初始frame
    UIView *videoOriginSuperView; // 画中画视频初始父视图
}

// timer
@property (nonatomic, strong) NSTimer *iAfterClassTimer;
@property (nonatomic, strong) NSTimer *iClassBeginTimer;
@property (nonatomic, strong) TKTimer *iCheckPlayVideotimer;

@property (nonatomic, assign) NSTimeInterval iLocalTime;
@property (nonatomic, assign) NSTimeInterval iClassStartTime; // 上课开始时间
@property (nonatomic, assign) NSTimeInterval iServiceTime;
@property (nonatomic, assign) NSTimeInterval iHowMuchTimeServerFasterThenMe; // 时间差
@property (nonatomic, assign) NSTimeInterval iCurrentTime;                   // 当前时间

@property (nonatomic, strong) UIView *dimView; // 作用:点击空白视图 消失课件库 花名册
@property (nonatomic, strong) TKCTListView *listView;           //课件库
@property (nonatomic, strong) TKCTUserListView *userListView;   //控制按钮视图
@property (nonatomic, strong) TKCTControlView *controlView;     //控制按钮视图
@property (nonatomic, strong) TKCTNetDetailView *netDetailView; //网络质量

@property (nonatomic, strong) TKCTBaseMediaView *iScreenView; //共享桌面

@property (nonatomic, strong)
    NSMutableArray<TKCTVideoSmallView *> *iStudentVideoViewArray; //存放学生和老师视频数组
@property (nonatomic, strong) TKSplitScreenView *splitScreenView; //分屏背景视图
@property (nonatomic, strong) NSMutableArray<TKCTVideoSmallView *> *iStudentSplitViewArray;
@property (nonatomic, strong) TKCTVideoSmallView *splitVideoView; // 分屏视频
                                                                  //存放学生分屏视频数组
@property (nonatomic, strong)
    NSMutableArray<NSString *> *iStudentSplitScreenArray;    //存放学生分屏ID数组
@property (nonatomic, strong) NSDictionary *iScaleVideoDict; //记录缩放的视频

@property (nonatomic, assign) BOOL isLocalPublish;
@property (nonatomic, assign) BOOL playbackEnd; //回放结束
@property (nonatomic, assign) BOOL isConnect;
@property (nonatomic, assign) BOOL isQuiting;
@property (nonatomic, assign) BOOL networkRecovered;
@property (nonatomic, assign) BOOL isRemindClassEnd;

//拖动进来时的状态
@property (nonatomic, strong) NSMutableDictionary *iMvVideoDic; // 拖动视频top left 等信息

// 发生断线重连设置为YES，恢复后设置为NO
@property (nonatomic, strong) UITapGestureRecognizer *tapGesture;
@property (nonatomic, strong) UILabel *replyText;
@property (nonatomic, assign) CGFloat knownKeyboardHeight;
@property (nonatomic, strong) NSArray *iMessageList;
@property (nonatomic, strong) UILongPressGestureRecognizer *longGes; // 记录老师视频默认长按手势
@property (nonatomic, strong)
    UILongPressGestureRecognizer *fullScreenVideoLongGes; // 记录老师视频播放mp4全屏长按手势

// 工具箱
@property (nonatomic, strong) TKDialView *dialView;
@property (nonatomic, strong) TKToolsResponderView *responderView;
@property (nonatomic, strong) TKTimerView *timerView;   // 计时器选择器
@property (nonatomic, strong) TKStuTimerView *stuTimer; // 学生端计时器

@end

@implementation TKManyViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // 通用初始化
    [self initCommon];

    // 初始化导航栏
    [self initNavigation];

    // 初始化白板
    [self initWhiteBoardView];

    // 初始化聊天界面
    [self initMessageView];

    // 初始化小视频视图
    [self videoView];

    // 初始化分屏视图
    [self initSplitScreenView];

    // 初始化手势
    [self initTapGesTureRecognizer];

    // 初始化音频
    [self initAudioSession];

    // 初始化白板控件
    [self initWhiteBoardNativeTool];

    [TKScreenShotFactory sharedFactory].contentView = _whiteboardBackView;
    [TKScreenShotFactory sharedFactory].brush       = self.brushToolView;

    // 如果是回放，那么放上遮罩页
    if (_iSessionHandle.isPlayback == YES) { [self initPlaybackMaskView]; }

    [self.backgroundImageView bringSubviewToFront:_iTKEduWhiteBoardView];
}

- (void)viewWillAppear:(BOOL)animated {

    [super viewWillAppear:animated];

    [self addNotification];

    [self createTimer];
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

        } else if ([func isEqualToString:NSStringFromSelector(
                                             @selector(sessionManagerUserJoined:InList:))]) {

            NSString *str = params.firstObject;
            BOOL inList   = [params.lastObject boolValue];
            ((void (*)(id, SEL, NSString *, BOOL))objc_msgSend)(self, funcSel, str, inList);

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
        } else if ([func isEqualToString:NSStringFromSelector(@selector(sessionManagerOnShareFileState:state:extensionMessage:))]) {

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
- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    if (self.iMediaView) { [self.iMediaView.superview bringSubviewToFront:self.iMediaView]; }

    if (self.dimView) { [self.dimView.superview bringSubviewToFront:self.dimView]; }

    if (self.listView) { [self.listView.superview bringSubviewToFront:self.listView]; }

    if (self.userListView) { [self.userListView.superview bringSubviewToFront:self.userListView]; }

    if (_iSessionHandle.isPlayback) {
        [self.playbackMaskView.superview bringSubviewToFront:self.playbackMaskView];
    }
}
- (void)viewWillDisappear:(BOOL)animated {

    [super viewWillDisappear:animated];
    [self.chatViewNew removeSubviews];
    if (!_iPickerController) { [self invalidateTimer]; }

    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[UIApplication sharedApplication] removeObserver:self forKeyPath:@"idleTimerDisabled"];
}

#pragma mark - fullScreen白板全屏
- (void)whiteBoardFullScreen:(NSNotification *)aNotification {

    [self hiddenNavAlertView];

    bool isFull                                     = [aNotification.object boolValue];
    [TKEduSessionHandle shareInstance].iIsFullState = isFull;

    // 视频全屏
    if (self.iMediaView) {
        if (!self.iMediaView.hasVideo) {
            self.iMediaView.hidden = isFull;
        } else {
            return;
        }
    }

    if (isFull) {

        self.splitScreenView.hidden = YES;
        CGRect tFrame               = CGRectMake(_viewX, 0, _screenWidth, ScreenH);

        self.whiteboardBackView.frame           = tFrame;
        self.whiteboardBackView.backgroundColor = UIColor.blackColor;
        self.iTKEduWhiteBoardView.frame         = self.whiteboardBackView.bounds;

        // 隐藏白板上的小视频
        for (UIView *view in self.whiteboardBackView.subviews) {

            if ([view isKindOfClass:TKCTVideoSmallView.class]) { view.hidden = YES; }
        }
        [self.backgroundImageView bringSubviewToFront:self.whiteboardBackView];
        [_iSessionHandle.whiteBoardManager refreshWhiteBoard];

        // 5月20日 余家峰要求更改：白板全屏时，将聊天内容显示出来
        [self.chatViewNew.superview bringSubviewToFront:self.chatViewNew];
    } else {

        self.whiteboardBackView.backgroundColor = UIColor.clearColor;
        if (_iStudentSplitScreenArray.count > 0) {

            self.splitScreenView.hidden = NO;
        } else {

            self.splitScreenView.hidden = YES;
        }

        [self.backgroundImageView sendSubviewToBack:self.whiteboardBackView];
        // 隐藏白板上的小视频
        for (UIView *view in self.whiteboardBackView.subviews) {

            if ([view isKindOfClass:TKCTVideoSmallView.class]) { view.hidden = NO; }
        }
        [self refreshUI];
    }

    [_navbarView hideAllButton:isFull];
}

#pragma mark - 初始化
- (void)addNotification {
    //白板全屏的通知
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(whiteBoardFullScreen:)
                                                 name:sChangeWebPageFullScreen
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(tapTable:)
                                                 name:sTapTableNotification
                                               object:nil];

    //不自动锁屏
    [[UIApplication sharedApplication] addObserver:self
                                        forKeyPath:@"idleTimerDisabled"
                                           options:NSKeyValueObservingOptionNew
                                           context:nil];

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

- (void)showPageBeforeClass {
    [_pageControl resetBtnStates];
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
- (void)initCommon {
    self.navigationController.navigationBar.hidden = YES;

    self.backgroundImageView.contentMode = UIViewContentModeScaleToFill;
    self.backgroundImageView.sakura.image(@"ClassRoom.backgroundImageMore");

    //初始化容器
    _iStudentVideoViewArray   = [NSMutableArray array];
    _iStudentSplitScreenArray = [NSMutableArray array];
    _iStudentSplitViewArray   = [NSMutableArray array];
    _iScaleVideoDict          = [NSDictionary dictionary];

    //网络是否重新连接
    _networkRecovered = YES;

    _isConnect = NO;

    _roomJson  = [TKEduClassRoom shareInstance].roomJson;
    _iUserType = (TKUserRoleType)_roomJson.roomrole;

    //课堂中的视频分辨率
    self.whiteBoardRatio = [TKHelperUtil returnClassRoomDpi];

    [TKHelperUtil setVideoFormat];

    _viewX = [TKUtil isiPhoneX] ? 44 : 0;
    // 小视频尺寸
    _screenWidth            = [TKUtil isiPhoneX] ? ScreenW - 44 : ScreenW;
    _sStudentVideoViewWidth = (_screenWidth - sViewCap * Proportion * (sMaxVideo + 1)) / sMaxVideo;
    _sStudentVideoViewHeigh = _sStudentVideoViewWidth * 3.0 / 4;
    _sBottomViewHeigh       = _sStudentVideoViewHeigh + (2 * sViewCap) * Proportion;

    [_iSessionHandle sessionHandleSetDeviceOrientation:(UIDeviceOrientationLandscapeLeft)];
    [TKHelperUtil phontLibraryAction];
}

- (void)initAudioSession {

    AVAudioSessionRouteDescription *route = [[AVAudioSession sharedInstance] currentRoute];
    for (AVAudioSessionPortDescription *desc in [route outputs]) {

        if ([[desc portType] isEqualToString:AVAudioSessionPortBuiltInReceiver]) {
            _iSessionHandle.isHeadphones = NO;
            _iSessionHandle.iVolume      = 1;

        } else {
            _iSessionHandle.isHeadphones = YES;
            _iSessionHandle.iVolume      = 0.5;
        }
    }
}

- (void)initNavigation {

    self.navbarView = [[TKCTNavView alloc] initWithFrame:CGRectMake(0, 0, ScreenW, TKNavHeight)
                                               aParamDic:_iParamDic];
    [self.backgroundImageView addSubview:self.navbarView];

    tk_weakify(self);

    //离开课堂 （返回)
    self.navbarView.leaveButtonBlock = ^{
        [weakSelf.view endEditing:YES];
        [weakSelf leftButtonPress];
    };
    // 上课
    self.navbarView.classBeginBlock = ^{ [weakSelf hiddenNavAlertView]; };
    //下课
    self.navbarView.classoverBlock = ^{

        [weakSelf hiddenNavAlertView];

        [weakSelf.iClassBeginTimer invalidate];
        weakSelf.iClassBeginTimer = nil;
    };
    
    // 花名册
    self.navbarView.memberButtonClickBlock = ^(UIButton *sender) {

        if (sender.selected) {

            [weakSelf hiddenNavAlertView];
            //花名册：宽 7/10  高 9/10
            CGFloat showHeight = ScreenH - TKNavHeight;
            CGFloat showWidth  = fmaxf(ScreenW * (6 / 10.0), 485);
            CGFloat x          = (ScreenW - showWidth) / 2.0;
            CGFloat y          = TKNavHeight;

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

            weakSelf.userListView.dismissBlock = ^{
                weakSelf.navbarView.memberButton.selected = NO;
                weakSelf.userListView                     = nil;
            };
            [weakSelf.userListView show:weakSelf.view];

        } else {
            [weakSelf tapOnViewToHide];
        }
    };
	
    //课件库按钮
    self.navbarView.coursewareButtonClickBlock = ^(UIButton *sender) {
        if (sender.selected) {
            if (!weakSelf.listView) {

                [weakSelf hiddenNavAlertView];

                //文件列表：            宽 7/10  高 9/10
                CGFloat showHeight = ScreenH - TKNavHeight;
                CGFloat showWidth  = fmaxf(ScreenW * (6 / 10.0), 500);
                CGFloat x          = (ScreenW - showWidth) / 2.0;
                //                CGFloat y = (ScreenH-showHeight)/2.0;
                CGFloat y = TKNavHeight;

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
        if (!weakSelf.controlView) { weakSelf.controlView = [[TKCTControlView alloc] init]; }

        TKPopView *popview  = [TKPopView showPopViewAddedTo:weakSelf.view pointingAtView:sender];
        popview.popViewType = TKPopViewType_AllControl;
        popview.delegate    = weakSelf;
    };

    self.navbarView.toolBoxButtonClickBlock = ^(UIButton *sender) {

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

        TKManyStylePopView *popview =
            [TKManyStylePopView showPopViewAddedTo:weakSelf.view pointingAtView:sender];
        popview.viewStyle = weakSelf.layoutFactory.viewLayout;
    };
    
}

- (void)initMessageView {
    float _width     = IS_PAD ? ScreenW * (1 / 3.0) : ScreenW * (1 / 3.0);
    float _margin    = IS_PAD ? 10 : 0;
    self.chatViewNew = [[TKNewChatView alloc]
         initWithFrame:CGRectMake(
                           0, CGRectGetMaxY(self.navbarView.frame) + _sBottomViewHeigh - _margin,
                           _width, ScreenH - (self.navbarView.height + _sBottomViewHeigh))];
    self.chatViewNew.x = 15;
    [self.backgroundImageView addSubview:self.chatViewNew];
    [self.backgroundImageView bringSubviewToFront:self.splitScreenView];

    [self.chatViewNew setBadgeNumber:_iSessionHandle.unReadMessagesArray.count];
    tk_weakify(self);
    self.chatViewNew.messageBtnClickBlock = ^(UIButton *_Nonnull sender) {
        if (sender.selected) {
            [weakSelf hiddenNavAlertView];
            if (weakSelf.iSessionHandle.unReadMessagesArray.count > 0) {
                [weakSelf.iSessionHandle.unReadMessagesArray removeAllObjects];
            }
            [weakSelf.chatViewNew setBadgeNumber:weakSelf.iSessionHandle.unReadMessagesArray.count];
        }
    };

    self.chatViewNew.hideComplete =
        ^{ [weakSelf.view bringSubviewToFront:weakSelf.whiteboardBackView]; };
    //进教室的时候聊天窗口打开
    [self.chatViewNew hide:NO];

    [self.chatViewNew setUserRoleType:TKUserType_Student];
}

- (void)initWhiteBoardView {
    CGFloat x      = _viewX;
    CGFloat y      = CGRectGetMaxY(self.navbarView.frame);
    CGFloat width  = _screenWidth;
    CGFloat height = ScreenH - CGRectGetMaxY(self.navbarView.frame);

    CGRect tFrame = CGRectMake(x, y, width, height);

    // 白板背景图
    _whiteboardBackView = [[UIView alloc] initWithFrame:tFrame];

    _iTKEduWhiteBoardView =
        [_iSessionHandle.whiteBoardManager createWhiteBoardWithFrame:_whiteboardBackView.bounds
                                                   loadComponentName:TKWBMainContentComponent
                                                   loadFinishedBlock:^{
                                                       // 白板加载完成可放处理方法
                                                   }];
    _iTKEduWhiteBoardView.backgroundColor = [UIColor clearColor];
    _iSessionHandle.whiteboardView        = _iTKEduWhiteBoardView;

    [_whiteboardBackView addSubview:_iTKEduWhiteBoardView];
    [self.backgroundImageView addSubview:_whiteboardBackView];
}

- (void)initSplitScreenView {

    CGRect tFrame        = self.iTKEduWhiteBoardView.frame;
    self.splitScreenView = [[TKSplitScreenView alloc] initWithFrame:tFrame];
    [self.backgroundImageView addSubview:self.splitScreenView];
    self.splitScreenView.hidden = YES;
}

- (void)initTapGesTureRecognizer {
    UITapGestureRecognizer *tapTableGesture =
        [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapTable:)];
    tapTableGesture.delegate = self;
    [self.backgroundImageView addGestureRecognizer:tapTableGesture];
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

- (void)createTimer {

    if (!_iCheckPlayVideotimer) {
        __weak __typeof(self) weekSelf = self;
        _iCheckPlayVideotimer        = [[TKTimer alloc] initWithTimeout:0.5
                                                          repeat:YES
                                                      completion:^{
                                                          __strong __typeof(self) strongSelf = weekSelf;

                                                          [strongSelf checkPlayVideo];
                                                      }
                                                           queue:dispatch_get_main_queue()];

        [_iCheckPlayVideotimer start];
        
    }
}

- (TKAnswerSheetView *)answerSheetForView:(UIView *)view {
    TKAnswerSheetView *answerSheet = nil;
    view                           = _whiteboardBackView;

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
- (void)videoView {
    TKRoomJsonModel *model = [TKEduClassRoom shareInstance].roomJson;
    NSInteger i            = model.roomlayout;

    if (_roomJson.roomtype == TKRoomTypeOneToOne) {
        /*
         对应一对多的映射规则；一对一常规布局：一对多视频置顶；一对一双师布局：一对多视频置顶；一对一纯视频布局：一对多自由视频
         */
        if (i == oneToOneDoubleVideo) {
            i = OnlyVideo;
        } else {
            i = CoursewareDown;
        }
    } else {
        if (i != MainPeople && i != OnlyVideo) { i = CoursewareDown; }
    }

    if ([TKEduClassRoom shareInstance].roomJson.configuration.onlyMeAndTeacherVideo &&
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {

        i = CoursewareDown;
    }

    [self switchLayout:i];
}
- (void)switchLayout:(TKRoomLayout)layout {

    _splitVideoView = nil;

    if (layout == CoursewareDown) {
        _iSessionHandle.roomLayout = CoursewareDown;

        [self.layoutFactory setViewLayout:CoursewareDown];
        self.videoContentView          = (TKManyNormalLayoutView *)self.layoutFactory.contentView;
        self.whiteboardBackView.hidden = NO;

    } else if (layout == MainPeople) {

        _iSessionHandle.roomLayout = MainPeople;

        [self.layoutFactory setViewLayout:MainPeople];
        self.whiteboardBackView.hidden = YES;

    } else {

        _iSessionHandle.roomLayout = OnlyVideo;

        [self.layoutFactory setViewLayout:OnlyVideo];
        self.whiteboardBackView.hidden = YES;

        if (_iSessionHandle.iIsCanDraw && _iUserType == TKUserType_Student) {

            self.navbarView.upLoadButton.hidden = YES;
        }
    }

    //白板全屏状态下需要将 whiteboardBackView 的层次放在最高
    if (_iSessionHandle.iIsFullState) {
        [self.whiteboardBackView.superview bringSubviewToFront:self.whiteboardBackView];
    }

    [self.navbarView buttonRefreshUI];
    [self.backgroundImageView bringSubviewToFront:self.chatViewNew];
}

- (void)popView:(TKPopView *)popView didSelectRowAtIndexPath:(nonnull NSString *)actionID {
    [TKPopView dismissForView:self.view];

    switch (popView.popViewType) {

        case TKPopViewType_ToolBox: {
            if (actionID.integerValue == 1) {

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

            } else if (actionID.integerValue == 2) {

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

            } else if (actionID.integerValue == 3) {
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

            } else if (actionID.integerValue == 4) {

                if (self.responderView) { return; }

                //是否显示//开始抢答//如果有人抢答，显示抢到的用户名
                NSDictionary *dict = @{ @"isShow" : @YES, @"begin" : @NO, @"userAdmin" : @"" };
                NSString *str      = [TKUtil dictionaryToJSONString:dict];
                [_iSessionHandle sessionHandlePubMsg:sQiangDaQi
                                                  ID:sQiangDaQiMesg
                                                  To:sTellAll
                                                Data:str
                                                Save:YES
                                     AssociatedMsgID:sClassBegin
                                    AssociatedUserID:nil
                                             expires:0
                                          completion:nil];

            } else if (actionID.integerValue == 5) {
                
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
        }
        break;

        case TKPopViewType_AllControl: {
            //全体发言 101   全体静音 102   全体奖励 103   全体复位 104   音频授课 105
            if (actionID.integerValue == 101) {
                //全体发言
                [self.controlView speecheButtonClick:nil];

            } else if (actionID.integerValue == 102) {
                //全体静音
                [self.controlView MuteButtonClick:nil];

            } else if (actionID.integerValue == 103) {
                //全体奖励
                [self.controlView rewardButtonClick:nil];

            } else if (actionID.integerValue == 104) {
                //全体复位
                [self.controlView resetButtonClick:nil];
                [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sVideoSplitScreen
                                                                     ID:sVideoSplitScreen
                                                                     To:sTellAll
                                                                   Data:@{}
                                                             completion:nil];

                NSMutableDictionary *dict    = [NSMutableDictionary dictionary];
                NSMutableDictionary *addDict = [NSMutableDictionary dictionary];

                NSArray *users = [[TKEduSessionHandle shareInstance] userStdntAndTchrArray];

                for (TKRoomUser *user in users) {

                    NSDictionary *info =
                        @{ @"percentTop" : @(0),
                           @"percentLeft" : @(0),
                           @"isDrag" : @(NO) };
                    [addDict setValue:info forKey:user.peerID];
                }
                [dict setValue:addDict forKey:@"otherVideoStyle"];
                [[TKEduSessionHandle shareInstance] publishVideoDragWithDic:dict To:sTellAll];

            } else if (actionID.integerValue == 105) {
                // 纯音频教室
                [_iSessionHandle sessionHandleChangeAudioOnlyRoom];
            }
        } break;

        default:
            break;
    }
}
- (void)popViewWillHidden:(TKPopView *)popView {
    self.navbarView.toolBoxButton.selected = NO;
    self.navbarView.controlButton.selected = NO;
}

#pragma mark - 视频相关操作
// 分屏/取消分屏
- (void)beginTKSplitScreenView:(TKCTVideoSmallView *)videoView {

    if (!videoView.isSplit) {

        [self.backgroundImageView bringSubviewToFront:_splitScreenView];
        self.splitScreenView.frame = self.whiteboardBackView.frame;

        //在_iStudentVideoViewArray 中删除视图
        NSArray *videoArray = [NSArray arrayWithArray:_iStudentVideoViewArray];

        for (TKCTVideoSmallView *view in videoArray) {

            if (view.iVideoViewTag == videoView.iVideoViewTag) {

                [_iStudentVideoViewArray removeObject:view];
            }
        }

        [_iStudentSplitViewArray addObject:videoView];
        _splitScreenView.hidden = NO;
        videoView.isSplit       = YES;
        [_splitScreenView addVideoSmallView:videoView];
        BOOL isbool = [_iStudentSplitScreenArray containsObject:videoView.iRoomUser.peerID];
        if (!isbool) { [_iStudentSplitScreenArray addObject:videoView.iRoomUser.peerID]; }

        self.splitScreenView.backgroundColor = UIColor.blackColor;

    } else { //取消分屏

        [_iStudentVideoViewArray addObject:videoView];

        [_iStudentSplitScreenArray removeObject:videoView.iRoomUser.peerID];

        [_splitScreenView deleteVideoSmallView:videoView];

        [_iStudentSplitViewArray removeObject:videoView];

        if (_iStudentSplitScreenArray.count <= 0) { _splitScreenView.hidden = YES; }
        //        [self sendMoveVideo:_iPlayVideoViewDic];

        videoView.isSplit = NO;

        self.splitScreenView.backgroundColor = UIColor.clearColor;

        // 只看老师和自己时 学生显示老师拖到课件上的人的音视频，恢复后不在显示
        if (_roomJson.configuration.onlyMeAndTeacherVideo &&
            _iUserType == TKUserType_Student) {

            if (videoView.iRoomUser.role != TKUserType_Teacher &&
                ![videoView.iRoomUser.peerID isEqualToString:_iSessionHandle.localUser.peerID]) {

                [self unPlayVideo:videoView.iRoomUser.peerID];
            }
        }
    }

    videoView.isDrag = NO;

    if (_iUserType == TKUserType_Teacher) {
        NSString *str = [TKUtil dictionaryToJSONString:@{
            @"userIDArry" : _iStudentSplitScreenArray
        }];
        [_iSessionHandle sessionHandlePubMsg:sVideoSplitScreen
                                          ID:sVideoSplitScreen
                                          To:sTellAllExpectSender
                                        Data:str
                                        Save:true
                             AssociatedMsgID:nil
                            AssociatedUserID:nil
                                     expires:0
                                  completion:nil];
    }
}

- (void)cancelSplitScreen:(NSMutableArray *)array {
    if (_iStudentSplitScreenArray.count > array.count) {

        __block NSMutableArray *difObject = [NSMutableArray arrayWithCapacity:10];
        //找到arr2中有,arr1中没有的数据
        [_iStudentSplitScreenArray
            enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
                NSNumber *number1   = obj; //[obj objectAtIndex:idx];
                __block BOOL isHave = NO;
                [array enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx,
                                                    BOOL *_Nonnull stop) {
                    if ([number1 isEqual:obj]) {
                        isHave = YES;
                        *stop  = YES;
                    }
                }];
                if (!isHave) { [difObject addObject:obj]; }
            }];
        [array enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
            NSNumber *number1   = obj; //[obj objectAtIndex:idx];
            __block BOOL isHave = NO;
            [_iStudentSplitScreenArray
                enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
                    if ([number1 isEqual:obj]) {
                        isHave = YES;
                        *stop  = YES;
                    }
                }];
            if (!isHave) { [difObject addObject:obj]; }
        }];

        for (NSString *peerID in difObject) {

            NSArray *sArray = [NSArray arrayWithArray:_iStudentSplitViewArray];
            for (TKCTVideoSmallView *view in sArray) {

                if ([view.iRoomUser.peerID isEqualToString:peerID]) {
                    view.isSplit = YES;
                    [self beginTKSplitScreenView:view];
                }
            }
        }
    }
}

// 画中画
- (void)changeVideoFrame:(BOOL)isFull {

    if (_iSessionHandle.isOnlyAudioRoom == YES) { return; }
    // 全屏课件 需要将老师视频显示在右下角
    if (self.iMediaView.hasVideo) {
        [self.layoutFactory fullScreenShowTeacherVideoView:isFull superview:self.iMediaView];

    } else if (_iFileView && _iFileView.hasVideo) {
        [self.layoutFactory fullScreenShowTeacherVideoView:isFull superview:_iFileView];

    } else {
        TKCTVideoSmallView *teachView = [self.layoutFactory fullScreenShowTeacherVideoView:isFull
                                            superview:self.whiteboardBackView];
        
        // 取消全屏 拖拽 放大回复
        if (isFull == NO && [_iMvVideoDic objectForKey: teachView.iPeerId]) {
            
            [self moveVideo:_iMvVideoDic];
            [self sScaleVideo:_iScaleVideoDict];
        }
    }

    // 导航栏的按钮 显示/隐藏 操作
    [_navbarView hideAllButton:isFull];
}

/**
 发送视频的位置

 @param aPlayVideoViewDic 位置存储字典
 */
//-(void)sendMoveVideo:(NSDictionary *)aPlayVideoViewDic {
//
//    NSMutableDictionary *tVideosDic = @{}.mutableCopy;
//    NSDictionary *tDic =   @{@"otherVideoStyle":tVideosDic};
//
//    self.iMvVideoDic = [NSMutableDictionary dictionaryWithDictionary:tVideosDic];
//    if (_iUserType == TKUserType_Teacher) {
//        [_iSessionHandle publishVideoDragWithDic:tDic To:sTellAll];
//    }
//}

// 拖拽实现方法
- (void)moveVideo:(NSDictionary *)dict {

    if (self.layoutFactory.viewLayout == CoursewareDown) {

        NSDictionary *copyDict = [dict copy];
        for (NSString *peerId in copyDict) {

            NSDictionary *obj = [copyDict objectForKey:peerId];
            if ([[obj objectForKey:@"percentTop"] isKindOfClass:[NSNull class]] ||
                [[obj objectForKey:@"percentLeft"] isKindOfClass:[NSNull class]]) {
                return;
            }

            BOOL isDrag = [[obj objectForKey:@"isDrag"] boolValue];

            // 取消拖拽 初始视频比例
            if (isDrag == NO) {
                NSMutableDictionary *dic = [_iScaleVideoDict mutableCopy];
                [dic removeObjectForKey:peerId];
                _iScaleVideoDict = [dic copy];
            }
            
            TKCTVideoSmallView *videoView = [self.layoutFactory videoViewWithUserID:peerId];

            // 只看老师和自己时 学生显示老师拖到课件上的人的音视频，恢复后不在显示
            if ([TKEduClassRoom shareInstance].roomJson.configuration.onlyMeAndTeacherVideo &&
                [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {

                TKRoomUser *user =
                    [[TKEduSessionHandle shareInstance].roomMgr getRoomUserWithUId:peerId];

                if (user && !videoView && isDrag && user.publishState > 0) {

                    videoView = [self.layoutFactory playVideoWithUser:user];

                } else if (user && videoView && !isDrag && !videoView.isSplit) {

                    if (user.role != TKUserType_Teacher &&
                        ![peerId isEqualToString:_iSessionHandle.localUser.peerID]) {

                        videoView.isDrag = isDrag;
                        [self unPlayVideo:videoView.iRoomUser.peerID];
                        videoView = nil;
                    }
                }
            }

            if (videoView) {

                videoView.isDrag     = isDrag;
                videoView.videoMode  = TKVideoViewMode_Fill;
                videoView.maskLayout = TKMaskViewLayout_Normal;

                if ([[obj objectForKey:@"percentTop"] isKindOfClass:[NSNull class]] ||
                    [[obj objectForKey:@"percentLeft"] isKindOfClass:[NSNull class]]) {
                    //数据格式有问题
                    return;
                }

                CGFloat top  = [[obj objectForKey:@"percentTop"] floatValue];
                CGFloat left = [[obj objectForKey:@"percentLeft"] floatValue];

                [self.layoutFactory dragVideoView:videoView
                                             left:left
                                              top:top
                                        superview:self.whiteboardBackView];
            }
        }
    }
}

- (void)sScaleVideo:(NSDictionary *)peerIdToScaleDic {

    NSArray *peerIdArray = peerIdToScaleDic.allKeys;

    for (NSString *peerId in peerIdArray) {
        NSDictionary *scaleDict = [peerIdToScaleDic objectForKey:peerId];

        if (![scaleDict[@"scale"] isKindOfClass:NSNull.class]) {
            CGFloat scale = [scaleDict[@"scale"] floatValue];
            if (self.layoutFactory.viewLayout == CoursewareDown) {
                [self.layoutFactory scaleVideoViewWithZoomRatio:scale
                                                         userID:peerId
                                                      superview:self.whiteboardBackView];
            }
        }
    }
}

- (void)sVideoSplitScreen:(NSMutableArray *)array {

    NSArray *svArr = [NSArray arrayWithArray:_iStudentVideoViewArray];

    for (TKCTVideoSmallView *videoView in svArr) {
        for (NSString *peerId in array) {

            if ([peerId isEqualToString:videoView.iRoomUser.peerID]) {

                [self beginTKSplitScreenView:videoView];
            }
        }
    }
}

#pragma mark - 导航栏
- (void)hiddenNavAlertView {

    // 隐藏弹框页
    [self hiddenNavBarViewActionView];
    // 隐藏工具箱
    [self controlTabbarToolBoxBtn:NO];
}

- (void)hiddenNavBarViewActionView {

    if (self.controlView) {
        [self.controlView hidden];
        self.navbarView.controlButton.selected = NO;
        self.controlView                       = nil;
    }
    [self tapOnViewToHide];
}
- (void)controlTabbarToolBoxBtn:(BOOL)isSelected {

    if (self.navbarView.toolBoxButton.selected != isSelected) {

        self.navbarView.toolBoxButton.selected = isSelected;
    }
}

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

#pragma mark - 刷新
- (void)refreshUI {

    @autoreleasepool {
        if (self.iPickerController) { return; }

        [self refreshWhiteBoard:YES];

        [self sScaleVideo:self.iScaleVideoDict];
        [self moveVideo:self.iMvVideoDic];
        [self sVideoSplitScreen:self.iStudentSplitScreenArray];
    }
}

#pragma mark - TKVideoViewDelegate
- (void)playingVideoWithUserCount:(NSInteger)count {
    //如果白板此时是全屏状态 不需要更改frame
    if (_iSessionHandle.iIsFullState) { return; }

    if (count == 0) {
        CGRect frame = CGRectMake(_viewX, CGRectGetMaxY(self.navbarView.frame), _screenWidth,
                                  ScreenH - CGRectGetMaxY(self.navbarView.frame));
        self.whiteboardBackView.frame   = frame;
        self.iTKEduWhiteBoardView.frame = self.whiteboardBackView.bounds;

    } else {

        CGFloat width = floor((ScreenW - 8 * 3) / 7.0);
        CGFloat height =
            CGRectGetMaxY(self.navbarView.frame) + width * 3.0 / 4; //必须保证事3:4 不能取整

        CGRect frame                  = CGRectMake(_viewX, height, _screenWidth, ScreenH - height);
        self.whiteboardBackView.frame = frame;
        self.iTKEduWhiteBoardView.frame = self.whiteboardBackView.bounds;
    }

    if (_splitVideoView && _splitVideoView.isSplit) {
        _splitVideoView.frame = self.whiteboardBackView.bounds;
    }

    [_iSessionHandle.whiteBoardManager refreshWhiteBoard];
}

// 拉伸白板
- (void)stretchWhiteBoardAreaWithPlayCount:(NSInteger)playingCount {
    // 视频全部拖到课件板后 拉伸白板区域
    if (playingCount == 0) {

        CGFloat tHeight = ScreenH - self.navbarView.bottomY;

        self.videoContentView.height = 0;
        CGRect tFrame = CGRectMake(_viewX, self.navbarView.bottomY, _screenWidth, tHeight);
        self.whiteboardBackView.frame   = tFrame;
//        self.iTKEduWhiteBoardView.frame = CGRectMake(_viewX, 0, _screenWidth, tHeight);
        self.iTKEduWhiteBoardView.frame = self.whiteboardBackView.bounds;

        [_iSessionHandle.whiteBoardManager refreshWhiteBoard];
    }

    else if (self.videoContentView.height == 0) {

        self.videoContentView.height = _sBottomViewHeigh;
        CGFloat tHeight              = ScreenH - self.videoContentView.bottomY;
        CGRect tFrame =
            CGRectMake(_viewX, CGRectGetMaxY(self.videoContentView.frame), _screenWidth, tHeight);
        self.whiteboardBackView.frame = tFrame;
        self.iTKEduWhiteBoardView.frame =
            CGRectMake(tFrame.origin.x, 0, tFrame.size.width, tFrame.size.height);

        [_iSessionHandle.whiteBoardManager refreshWhiteBoard];
        _splitScreenView.frame = tFrame;
    }
    self.splitScreenView.frame = self.whiteboardBackView.frame;
}
- (void)refreshWhiteBoard:(BOOL)hasAnimate {

    if (_iSessionHandle.isPicInPic) { [self changeVideoFrame:NO]; }
    CGFloat x = _viewX;
    CGFloat y =
        self.videoContentView.hidden ? TKNavHeight : CGRectGetMaxY(self.videoContentView.frame);
    CGFloat width = _screenWidth;
    CGFloat height =
        ScreenH -
        (self.videoContentView.hidden ? TKNavHeight : CGRectGetMaxY(self.videoContentView.frame));

    CGRect tFrame = CGRectMake(x, y, width, height);

    if (self.layoutFactory.viewLayout == CoursewareDown) {
        [self.chatViewNew.superview bringSubviewToFront:self.chatViewNew];
    }

    if (hasAnimate) {
        [UIView animateWithDuration:0.1
            animations:^{

                // 白板背景图
                _whiteboardBackView.frame = tFrame;

                _iTKEduWhiteBoardView.frame = _whiteboardBackView.bounds;

                if (self.iMvVideoDic && self.iStudentSplitViewArray.count <= 0) {
                    [self moveVideo:self.iMvVideoDic]; //视频位置会乱掉所以注释掉了
                }
                [_iSessionHandle.whiteBoardManager refreshWhiteBoard];
            }
            completion:^(BOOL finished) {
                // MP3图标位置变化,但是MP4的位置不需要变化
                if (!self.iMediaView.hasVideo) { [self restoreMp3ViewFrame]; }

                //白板经过全屏——>恢复全屏后
                //需要判断pagecontrol的frame是否超出白板，如果超出重新设置约束
                if (CGRectGetMaxY(self.pageControl.frame) >
                    CGRectGetHeight(_whiteboardBackView.frame)) {
                    [self.pageControl mas_remakeConstraints:^(MASConstraintMaker *make) {
                        make.bottom.equalTo(_whiteboardBackView.mas_bottom).offset(0);
                        make.centerX.equalTo(_whiteboardBackView.mas_centerX);
                    }];
                }
            }];
    } else {

        // 白板背景图
        _whiteboardBackView.frame = tFrame;

        _iTKEduWhiteBoardView.frame = _whiteboardBackView.bounds;

        if (!self.iMediaView.hasVideo) { [self restoreMp3ViewFrame]; }
        [_iSessionHandle.whiteBoardManager refreshWhiteBoard];

        if (self.iMvVideoDic) { [self moveVideo:self.iMvVideoDic]; }
    }
    self.splitScreenView.frame = self.whiteboardBackView.frame;
}

#pragma mark - 播放视频
- (void)playVideo:(TKRoomUser *)user {

    // 低功耗设备 和配置项  只观看老师和自己的视频音频
    if ((![TKUtil deviceisConform] || _roomJson.configuration.onlyMeAndTeacherVideo) && //  配置项
        _iSessionHandle.roomMgr.localUser.role == TKUserType_Student && // 本地是学生
        user.role != TKUserType_Teacher &&                              // 不是老师
        ![user.peerID isEqualToString:_iSessionHandle.roomMgr.localUser.peerID]) { // 不是自己

        return;
    }

    [self.layoutFactory playVideoWithUser:user];
    
}

- (void)unPlayVideo:(NSString *)peerID {

    [self.layoutFactory unPlayVideoWithUserID:peerID];
}

- (void)updateMvVideoForPeerID:(NSString *)aPeerId {

    NSDictionary *tVideoViewDic = (NSDictionary *)[_iMvVideoDic objectForKey:aPeerId];
    NSMutableDictionary *tVideoViewDicNew =
        [NSMutableDictionary dictionaryWithDictionary:tVideoViewDic];
    [tVideoViewDicNew setObject:@(NO) forKey:@"isDrag"];
    [tVideoViewDicNew setObject:@(0) forKey:@"percentTop"];
    [tVideoViewDicNew setObject:@(0) forKey:@"percentLeft"];
    [_iMvVideoDic setObject:tVideoViewDicNew forKey:aPeerId];
}

- (void)myPlayVideo:(TKRoomUser *)aRoomUser
         aVideoView:(TKCTVideoSmallView *)aVideoView
         completion:(void (^)(NSError *error))completion {
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
        
        [weakSelf.iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error) {
            if (error) {
                TKLog(@"退出房间错误: %@", error);
            }
        }];

    };
    alert.lelftBlock = ^{ weakSelf.isQuiting = NO; };
}

//如果是自己退出，则先掉leftroom。否则，直接退出。
- (void)prepareForLeave:(BOOL)aQuityourself {

    _isQuiting                = NO;

    [self tapTable:nil];
    [self.navbarView destory];
    //    self.navbarView = nil;
    [self.chatViewNew hide:YES];
    [_controlView dismissAlert];
    [_listView dismissAlert];

    [self invalidateTimer];

#if TARGET_IPHONE_SIMULATOR
#else
    [[UIDevice currentDevice]
        setProximityMonitoringEnabled:
            NO]; //建议在播放之前设置yes，播放结束设置NO，这个功能是开启红外感应
#endif

    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];

    [self dismissViewControllerAnimated:YES
                             completion:^{
                                 if (self.networkRecovered == NO) {
                                     [TKUtil showMessage:TKMTLocalized(@"Error.WaitingForNetwork")];
                                 }
                                 [[NSNotificationCenter defaultCenter]
                                     postNotificationName:sTKRoomViewControllerDisappear
                                                   object:nil];
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
        }
            break;
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

            [self clearAllData];
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
    if (_iUserType == TKUserType_Teacher ||
        _iUserType == TKUserType_Assistant ||
        _iSessionHandle.isPlayback == YES) {
        if (completion) { completion(); }
        return;
    }

    // 学生断线重连需要获取礼物
    [TKEduNetManager getGiftinfo:_roomJson.roomid
        aParticipantId:_roomJson.roomid
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
                        if (_iUserType != TKUserType_Teacher && _roomJson.roomid) {
                            NSDictionary *tDicInfo = [tGiftInfoArray objectAtIndex:i];
                            if ([[tDicInfo objectForKey:@"receiveid"]
                                    isEqualToString:_roomJson.roomid]) {
                                giftnumber = [tDicInfo objectForKey:@"giftnumber"]
                                                 ? [[tDicInfo objectForKey:@"giftnumber"] intValue]
                                                 : 0;
                                break;
                            }
                        }
                    }

                    _iSessionHandle.localUser.properties[sGiftNumber] = @(giftnumber);

                    if (completion) { completion(); }
                }
            });

        }
        aGetGifInfoError:^int(NSError *_Nullable aError) {
            dispatch_async(dispatch_get_main_queue(), ^{

                if (completion) { completion(); }

            });
            return 1;
        }];
}

//自己进入课堂
- (void)sessionManagerRoomJoined {
    
    
    _isConnect                   = NO;
    _isQuiting                   = NO;
    _networkRecovered            = YES;
    _iSessionHandle.iIsJoined    = YES;
    _iTKEduWhiteBoardView.hidden = NO;
    _iCurrentTime                = [[NSDate date] timeIntervalSince1970];
    
    _navbarView.beginAndEndClassButton.userInteractionEnabled = YES;
    //根据角色类型选择隐藏聊天按钮
    [self.chatViewNew setUserRoleType:[TKEduSessionHandle shareInstance].localUser.role];

    // 主动获取奖杯数目
    [self getTrophyNumber];

    bool isConform = [TKUtil deviceisConform];
    if (_iUserType == TKUserType_Teacher) {

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
                @"maxvideo" : @(_roomJson.maxvideo.intValue)
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
    if (!isConform && _iUserType == TKUserType_Teacher &&
        _roomJson.roomtype != TKRoomTypeOneToOne) {
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
        _iUserType == TKUserType_Student &&
        _iSessionHandle.roomMgr.inBackground == NO) {

        [_iSessionHandle sessionHandleChangeUserProperty:_iSessionHandle.localUser.peerID
                                                TellWhom:sTellAll
                                                     Key:sIsInBackGround
                                                   Value:@(NO)
                                              completion:nil];
    }

    TKLog(@"tlm-----myjoined 时间: %@", [TKUtil currentTimeToSeconds]);
#if TARGET_IPHONE_SIMULATOR
#else
    [[UIDevice currentDevice]
        setProximityMonitoringEnabled:
            NO]; //建议在播放之前设置yes，播放结束设置NO，这个功能是开启红外感应
#endif
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];

    [_iSessionHandle addUserStdntAndTchr:_iSessionHandle.localUser];
    [_iSessionHandle addUser:_iSessionHandle.localUser];
    [_iSessionHandle configureHUD:@"" aIsShow:NO];
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
        _iUserType == TKUserType_Teacher) {

        // 只有手动点击上下课时传 userid roleid
        [TKEduNetManager classBeginStar:_roomJson.roomid
            companyid:_roomJson.companyid
            aHost:sHost
            aPort:sPort
            userid:nil
            roleid:nil
            aComplete:^int(id _Nullable response) {

                NSString *str = [TKUtil dictionaryToJSONString:@{ @"recordchat" : @YES }];
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

    if (_iSessionHandle.isClassBegin == NO || _iUserType == TKUserType_Teacher) {

        // 进入房间就可以播放自己的视频
        if (_iUserType != TKUserType_Patrol && _iSessionHandle.isPlayback == NO) {

            if (_iSessionHandle.isOnlyAudioRoom) {
                //纯音频教室
                [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                        Publish:TKPublishStateAUDIOONLY
                                                     completion:nil];

            } else if (_roomJson.configuration.beforeClassPubVideoFlag) {
                //发布视频(告诉所有人)
                _isLocalPublish = NO;
                PublishState status =
                    _iSessionHandle.isOnlyAudioRoom ? TKPublishStateAUDIOONLY : TKPublishStateBOTH;
                [_iSessionHandle
                    sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                           Publish:(status)completion:^(NSError *error){}];

            } else {
                //显示本地视频不发布
                _isLocalPublish                        = YES;
                _iSessionHandle.localUser.publishState = TKPublishStateLocalNONE;
                [self playVideo:_iSessionHandle.localUser];
            }
        }
    }
    // 判断上下课按钮是否需要隐藏
//    if (_iSessionHandle.isPlayback == NO) {
//        _navbarView.beginAndEndClassButton.hidden    = NO;
//    }
//    if (_roomJson.configuration.hideClassBeginEndButton    == NO ||  _roomJson.configuration.autoStartClassFlag    == NO) {
//        _navbarView.beginAndEndClassButton.hidden    = NO;
//    }
   
}

//自己离开课堂
- (void)sessionManagerRoomLeft {

    [self unPlayVideo:_iSessionHandle.localUser.peerID];

    _isQuiting                = NO;
    _iSessionHandle.iIsJoined = NO;
    [_iSessionHandle delUserStdntAndTchr:_iSessionHandle.localUser];
    [_iSessionHandle delUser:_iSessionHandle.localUser];
    
    // 清理数据
    [self quitClearData];
    
    [_iSessionHandle.whiteBoardManager resetWhiteBoardAllData];
    [_iSessionHandle.whiteBoardManager clearAllData];
    _iSessionHandle.whiteBoardManager = nil;
    
    [_iSessionHandle clearAllClassData];
    [_iSessionHandle configureHUD:@"" aIsShow:NO];
        
    _iSessionHandle.roomMgr = nil;
    [TKEduSessionHandle destroy];
    
    _iSessionHandle = nil;
    
    [self prepareForLeave:YES];
}

//用户进入
- (void)sessionManagerUserJoined:(TKRoomUser *)user InList:(BOOL)inList {

    // 提示用户进入信息(巡课不提示)
    if (user.role != TKUserType_Patrol) {
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
        TKChatMessageModel *tModel =
            [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                   role:TKChatRoleTypeMe
                                                message:[NSString stringWithFormat:@"%@(%@)%@", user.nickName, userRole, TKMTLocalized(@"Action.EnterRoom")]
                                                cospath:nil userName:nil fromid:nil
                                                   time:[TKUtil currentTime]];
        [_iSessionHandle addOrReplaceMessage:tModel];
    }

    // 提示用户进入信息(后台的学生)
    if ((user.role != TKUserType_Patrol &&
    user.role != TKUserType_Assistant) ||
    (user.role == TKUserType_Assistant && _roomJson.configuration.isPromptAssistantJoinRoom == YES)) {

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
                                                     fromid:user.peerID
                                                       time:[TKUtil currentTime]];
            [_iSessionHandle addOrReplaceMessage:chatMessageModel];
        }
    }
    if (inList) {
        // 踢人
        if (_iUserType == user.role && user.role == TKUserType_Teacher) {

            [_iSessionHandle sessionHandleEvictUser:user.peerID evictReason:nil completion:nil];
            return;
        }
        // 允许助教上台的1对1房间 ,  会创建一对多的房间显示.
        else if (_iUserType == user.role && user.role == TKUserType_Student &&
                 _roomJson.roomtype == TKRoomTypeOneToOne) {

            [_iSessionHandle sessionHandleEvictUser:user.peerID evictReason:nil completion:nil];
            return;
        }
    }
    // 房间用户
    BOOL tISpclUser = (user.role != TKUserType_Student && user.role != TKUserType_Teacher);
    if (tISpclUser) {
        [_iSessionHandle addSecialUser:user];

    } else {
        [_iSessionHandle addUserStdntAndTchr:user];
    }
    [_iSessionHandle addUser:user];

    [[NSNotificationCenter defaultCenter] postNotificationName:tkUserListNotification object:nil];

    // 上台
    if (user.publishState > TKPublishStateNONE) {
        [self playVideo:user];
        
    }
}

//用户离开
- (void)sessionManagerUserLeft:(NSString *)peerID {

    [self unPlayVideo:peerID];
    [self.layoutFactory userLeftRoom:peerID];

    TKRoomUser *user = [_iSessionHandle getUserWithPeerId:peerID];

    if (!peerID || !user) { return; }

    BOOL tIsMe = [[NSString stringWithFormat:@"%@", peerID]
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
        TKChatMessageModel *tModel =
            [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                                   role:TKChatRoleTypeMe
                                                message:[NSString stringWithFormat:@"%@(%@)%@", user.nickName, userRole,                                                          TKMTLocalized(@"Action.ExitRoom")]
                                                cospath:nil userName:nil fromid:nil
                                                   time:[TKUtil currentTime]];
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

    // 检查是否是演讲人离开
    NSArray *peerArr = [NSArray arrayWithArray:_iStudentSplitScreenArray];
    if (_iUserType == TKUserType_Teacher && [peerArr containsObject:peerID]) {
        [_iStudentSplitScreenArray removeObject:user.peerID];

        NSString *str = [TKUtil dictionaryToJSONString:@{
            @"userIDArry" : _iStudentSplitScreenArray
        }];

        [_iSessionHandle sessionHandlePubMsg:sVideoSplitScreen
                                          ID:sVideoSplitScreen
                                          To:sTellAll
                                        Data:str
                                        Save:true
                             AssociatedMsgID:nil
                            AssociatedUserID:nil
                                     expires:0
                                  completion:nil];
    }
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
//用户的音频音量大小变化的回调
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

//用户视频状态变化
- (void)sessionManagerVideoStateWithUserID:(NSString *)peerID publishState:(TKMediaState)state {

    TKRoomUser *user = [_iSessionHandle.roomMgr getRoomUserWithUId:peerID];
    if (!user || user.role == TKUserType_Patrol) { return; }

    if (state == TKMedia_Pulished) {
        // 台下用户才需要play 上台.
        [self playVideo:user];
    } else {
        if (user.publishState == TKUser_PublishState_NONE) {

            [_iSessionHandle delePublishUser:user];

            // 老师发布的视频下课不取消播放
            if (user.role != TKUserType_Teacher && _iSessionHandle.isClassBegin == YES) {

                [self unPlayVideo:peerID];
            }
        }
    }

    //    if (_iUserType == TKUserType_Teacher && _iMvVideoDic) {
    //        NSDictionary *tMvVideoDic = @{@"otherVideoStyle":_iMvVideoDic};
    //        [_iSessionHandle publishVideoDragWithDic:tMvVideoDic To:sTellAll];
    //    }

    if (_iSessionHandle.iHasPublishStd == NO && !_iSessionHandle.iIsFullState) { [self refreshUI]; }
}

//用户音频状态变化
- (void)sessionManagerAudioStateWithUserID:(NSString *)peerID publishState:(TKMediaState)state {
    TKRoomUser *user = [_iSessionHandle.roomMgr getRoomUserWithUId:peerID];

    if (!user || user.role == TKUserType_Patrol) { return; }

    if (state == TKMedia_Pulished) {
        // 台下 -> 开启音频 -> 上台
        [self playVideo:user];
    } else {
        if (user.publishState == TKUser_PublishState_NONE) {

            [_iSessionHandle delePublishUser:user];

            if (!(_iUserType == TKUserType_Teacher &&
                  _iSessionHandle.isClassBegin == NO && user.role == TKUserType_Teacher)) {
                // 老师发布的视频下课不取消播放
                [self unPlayVideo:peerID];
            }
        }

        //        if ((_iUserType == TKUserType_Teacher) && _iMvVideoDic) {
        //            NSDictionary *tMvVideoDic = @{@"otherVideoStyle":_iMvVideoDic};
        //            [_iSessionHandle  publishVideoDragWithDic:tMvVideoDic To:sTellAll];
        //        }

        if (_iSessionHandle.iHasPublishStd == NO && !_iSessionHandle.iIsFullState) {
            [self refreshUI];
        }
    }
}

//用户信息变化
- (void)sessionManagerUserChanged:(TKRoomUser *)user
                       Properties:(NSDictionary *)properties
                           fromId:(NSString *)fromId {

    TKLog(@"sessionManagerUserChanged - properties %@ - fromid - %@", [properties description],
          fromId);

    NSInteger tGiftNumber = 0;
    if ([properties objectForKey:sGiftNumber]) {

        tGiftNumber = [[properties objectForKey:sGiftNumber] integerValue];
    }
    // 画笔权限
    if ([properties objectForKey:sCandraw]) {

        BOOL canDraw = [[properties objectForKey:sCandraw] boolValue];
        if ([_iSessionHandle.localUser.peerID isEqualToString:user.peerID] &&
            _iUserType == TKUserType_Student) {

            if (_iSessionHandle.iIsCanDraw != canDraw) {

                [_iSessionHandle configureDraw:canDraw isSend:NO to:sTellAll peerID:user.peerID];
            }
        }
        if (canDraw) {

            if (user.publishState > 0) { [self playVideo:user]; }
        }
        if ([_iSessionHandle.localUser.peerID isEqualToString:user.peerID]) {
            // 授权翻页
            _pageControl.canDraw = canDraw;

            // 下课隐藏画笔工具
            self.brushToolView.hidden = _iSessionHandle.isClassBegin ? !canDraw : YES;
            [self.brushToolView hideSelectorView];
            [TKScreenShotFactory sharedFactory].canDraw =  _iSessionHandle.isClassBegin ? canDraw : NO;
        }

        // 播放媒体中 需要把画笔工具 放到媒体层下边
        if (_iSessionHandle.isPlayMedia == YES) {
            [self.whiteboardBackView bringSubviewToFront:self.iMediaView];
        }
    }
    // 举手
    BOOL isRaiseHand = NO;
    if ([properties objectForKey:sRaisehand]) {
        //如果没做改变的话，就不变化

        isRaiseHand                = [[properties objectForKey:sRaisehand] boolValue];
        self.navbarView.showRedDot = isRaiseHand;
        [self.navbarView showHandsupTips:isRaiseHand];
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
            //当为老师时
            if (_iUserType == TKUserType_Teacher) {
                // 分屏
                if ([_iStudentSplitScreenArray containsObject:user.peerID]) {
                    [_iStudentSplitScreenArray removeObject:user.peerID];

                    NSString *str = [TKUtil dictionaryToJSONString:@{
                        @"userIDArry" : _iStudentSplitScreenArray
                    }];

                    [_iSessionHandle sessionHandlePubMsg:sVideoSplitScreen
                                                      ID:sVideoSplitScreen
                                                      To:sTellAll
                                                    Data:str
                                                    Save:true
                                         AssociatedMsgID:nil
                                        AssociatedUserID:nil
                                                 expires:0
                                              completion:nil];
                }
            } else if (_iUserType == TKUserType_Student &&
                       [_iSessionHandle.localUser.peerID
                           isEqualToString:user.peerID]) { // 下台删除 抢答器
                if (self.responderView) {
                    [self.responderView removeFromSuperview];
                    self.responderView = nil;
                }
            }

            [self unPlayVideo:user.peerID];
        }
    }

    //更改上台后的举手按钮样式
    if (_iUserType == TKUserType_Student &&
        [_iSessionHandle.localUser.peerID isEqualToString:user.peerID]) {

        if (isRaiseHand) {
            if (_iSessionHandle.localUser.publishState > TKUser_PublishState_NONE) {
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

    if ([properties objectForKey:sServerName]) { //更改服务器

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

    if ([properties objectForKey:sPrimaryColor]) { //画笔颜色值

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
            _iUserType ==
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
                                                    message:message cospath:nil
                                                   userName:user.nickName
                                                     fromid:user.peerID
                                                       time:[TKUtil currentTime]];
            [_iSessionHandle addOrReplaceMessage:chatMessageModel];

//            if (self.chatViewNew) { [self.chatViewNew reloadData]; }
        }
    }
    // 可以实时看到人员离开进入消息
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
        
        NSString *msg       = [tDataDic objectForKey:@"msg"];
        NSString *time    = [tDataDic objectForKey:@"time"];
        NSString *cospath = [tDataDic objectForKey:@"cospath"];
        NSString *tMyPeerId = _iSessionHandle.localUser.peerID;
        if (!peerID) { peerID = _iSessionHandle.localUser.peerID; } //自己发送的收不到
        
        BOOL isMe      = [peerID isEqualToString:tMyPeerId];
        BOOL isTeacher = [extension[@"role"] intValue] == TKUserType_Teacher ? YES : NO;
        TKChatRoleType roleType = (isMe)?TKChatRoleTypeMe:(isTeacher?TKChatRoleTypeTeacher:TKChatRoleTypeOtherUer);

        TKChatMessageModel *tChatMessageModel =
            [[TKChatMessageModel alloc] initWithMsgType:msgType
                                                   role:roleType
                                                message:msg
                                                cospath:cospath
                                               userName:extension[@"nickname"]
                                                 fromid:peerID time:time];

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
    [_iSessionHandle clearAllClassData];

    if (self.isConnect == NO) {

        self.isConnect = YES;

        [self clearAllData];

        [_pageControl resetBtnStates];

        //上下课按钮设置为“上课”
        [_navbarView.beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassBegin")
                                            forState:UIControlStateNormal];
    }
}

// 共享屏幕
- (void)sessionManagerOnShareScreenState:(NSString *)peerId state:(TKMediaState)state {

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
                                      renderType:0
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
                                    renderType:0
                                        window:_iFileView
                                    completion:^(NSError *error) {
                                        if (_iUserType != TKUserType_Teacher) {
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
    BOOL isMedia        = [message[@"video"] boolValue];
    _iFileView.hasVideo = isMedia;
}
- (void)sessionManagerIceStatusChanged:(NSString *)state ofUser:(TKRoomUser *)user {
    TKLog(@"------IceStatusChanged:%@ nickName:%@", state, user.nickName);
}

//相关信令 pub
- (void)sessionManagerOnRemoteMsg:(BOOL)add
                               ID:(NSString *)msgID
                             Name:(NSString *)msgName
                               TS:(unsigned long)ts
                             Data:(NSObject *)data
                           InList:(BOOL)inlist {

    //    TKLog(@"TKManyView sessionManagerOnRemoteMsg======> msgName:%@ msgID:%@ add:%d
    //    data:%@",msgName,msgID,add,data);

    add = (BOOL)add;
    if ([msgName isEqualToString:sClassBegin]) {

        _iSessionHandle.isClassBegin                   = add;

        [self.navbarView refreshUI:add];

        // 上课
        if (add == YES) {

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

        // 刷新navbar
        [self.navbarView buttonRefreshUI];
        [self refreshUI];
        [self.pageControl setup];
    }
    // 更新时间
    else if ([msgName isEqualToString:sUpdateTime]) {

        if (add) {
            [self onRemoteMsgWithUpdateTime:add
                                         ID:msgID
                                       Name:msgName
                                         TS:ts
                                       Data:data
                                     InList:inlist];
        }

    }
    //翻页
    else if ([msgName isEqualToString:sShowPage] || [msgName isEqualToString:sDocumentChange] ) {
        [_pageControl resetBtnStates];
        
        [self closeMediaView];
    }

    // 全体静音
    else if ([msgName isEqualToString:sMuteAudio]) {

        [self onRemoteMsgWithMuteAudio:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];
    }
    // 流错误
    else if ([msgName isEqualToString:sStreamFailure]) {

        [self onRemoteMsgWithStreamFailure:add ID:msgID Name:msgID TS:ts Data:data InList:inlist];

    }
    // 拖拽回调
    else if ([msgName isEqualToString:sVideoDraghandle]) {

        [self onRemoteMsgWithVideoDraghandle:add
                                          ID:msgID
                                        Name:msgName
                                          TS:ts
                                        Data:data
                                      InList:inlist];
    }
    // 更改服务器
    else if ([msgName isEqualToString:sChangeServerArea]) {

    }
    // pc双击视频响应 只做响应
    else if ([msgName isEqualToString:sDoubleClickVideo]) {

        [self onRemoteMsgWithDoubleClickVideo:add
                                           ID:msgID
                                         Name:msgName
                                           TS:ts
                                         Data:data
                                       InList:inlist];

    }
    // 分屏回调
    else if ([msgName isEqualToString:sVideoSplitScreen]) {

        [self onRemoteMsgWithVideoSplitScreen:add
                                           ID:msgID
                                         Name:msgName
                                           TS:ts
                                         Data:data
                                       InList:inlist];

    }
    // 缩放回调
    else if ([msgName isEqualToString:sVideoZoom]) {

        [self onRemoteMsgWithVideoZoom:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];
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
    // 大并发教室
    else if ([msgName isEqualToString:sBigRoom]) {

        _iSessionHandle.bigRoom = YES;
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

        [self onRemoteMsgWithWBFullScreen:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];
    }
    // 小白板
    else if ([msgName isEqualToString:@"BlackBoard_new"]) {
        [TKPopViewHelper sharedInstance].isMiniWhiteboardToolOn = add;
        if (add) {
            [self.chatViewNew hide:YES];
        }
    }
    // 工具箱 转盘
    else if ([msgName isEqualToString:@"dial"]) {
        [TKPopViewHelper sharedInstance].isTurntableToolOn = add;
        NSDictionary *dataDic = [self convertWithData:data];
        if (add) {
            if (!_dialView) {

                UIView *toolBV = _whiteboardBackView;
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

    } else if ([msgName isEqualToString:sTimer]) { // 计时器
        [TKPopViewHelper sharedInstance].isTimerToolOn = add;
        [self onRemoteMsgWithShowTimerWithAdd:add andData:data receiveMsgTime:ts];

    }
    // 抢答器
    else if ([msgName isEqualToString:sQiangDaQi] || [msgName isEqualToString:sQiangDaZhe] ||
             [msgName isEqualToString:sResponderDrag]) {
        
        [self onRemoteMsgWithResponderView:add ID:msgID Name:msgName TS:ts Data:data InList:inlist];
    } else if ([msgName isEqualToString:@"Question"]) {
        //答题器
        [TKPopViewHelper sharedInstance].isAnswerToolOn = add;
        NSDictionary *dict = [self convertWithData:data];

        if ([[dict valueForKey:@"action"] isEqualToString:@"open"]) {

            if (_iUserType != TKUserType_Student) {
                //老师
                TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
                answerSheet.viewType           = TKAnswerSheetType_Setup;
            } else {
                TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
                [answerSheet removeFromSuperview];
            }

        } else if ([[dict valueForKey:@"action"] isEqualToString:@"start"]) {

            [[TKAnswerSheetData shareInstance] resetData];

            if (_iUserType != TKUserType_Student) {
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
            [[TKAnswerSheetData shareInstance] resetData];
        }

    } else if ([msgName isEqualToString:@"PublishResult"]) {
        //答题器公布的结果
        NSDictionary *dict = [self convertWithData:data];
        if (_iUserType != TKUserType_Student) {
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
        // roomWhiteBoardOnRemotePubMsg
    } else if ([msgName isEqualToString:sSwitchLayout]) {
        
        if (![TKEduSessionHandle shareInstance].isClassBegin && [TKEduSessionHandle shareInstance].localUser.role != TKUserType_Teacher) {
            return;
        }

        if ([TKEduClassRoom shareInstance].roomJson.configuration.onlyMeAndTeacherVideo &&
            [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {

            [self switchLayout:CoursewareDown];
            return;
        }

        //布局切换
        NSDictionary *dict = [self convertWithData:data];

        if (dict) {
            NSString *style = dict[@"nowLayout"];

            if ([style isEqualToString:@"MainPeople"]) {
                [self switchLayout:MainPeople];
            } else if ([style isEqualToString:@"OnlyVideo"]) {
                [self switchLayout:OnlyVideo];
            } else {
                /*
                 CoursewareDown    = 1,            //视频置顶
                 VideoDown        = 2,                //视频置底
                 Encompassment,            //视频围绕
                 Bilateral,                //多人模式
                 */
                [self switchLayout:CoursewareDown];
            }
        }
    } else if ([msgName isEqualToString:@"MainPeopleExchangeVideo"]) {
        //切换主讲人
        NSDictionary *dict = [self convertWithData:data];

        if (dict && self.layoutFactory.viewLayout == MainPeople) {

            [self.layoutFactory exchangeSpeakerWithUserID:[dict objectForKey:@"doubleId"]];
        }
    }
}

- (void)recoveryAfterGetinClass:(NSNotification *)notification {
    [self miniWBRecoveryAfterGetinClass:notification];

    [self screenShotRecoveryAfterGetinClass:notification];

    [self mediaMarkRecoveryAfterGetinClass:notification];
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
        if (_iUserType != TKUserType_Student) {
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

#pragma mark - 远程信令处理方法
- (void)onRemoteMsgWithClassBegin:(BOOL)add
                               ID:(NSString *)msgID
                             Name:(NSString *)msgName
                               TS:(unsigned long)ts
                             Data:(NSObject *)data
                           InList:(BOOL)inlist {

    //上课时 将布局恢复到教室默认的布局
    //    [self videoView];

    [self invalidateClassCurrentTime];

    // 白板退出全屏
    if (_iSessionHandle.iIsFullState == YES) {
        // 本地
        [[NSNotificationCenter defaultCenter] postNotificationName:sChangeWebPageFullScreen
                                                            object:@(NO)];
    }

    // 上课之前将自己的音视频关掉
    if (_roomJson.configuration.autoOpenAudioAndVideoFlag == NO && _isLocalPublish == YES) {

        _iSessionHandle.localUser.publishState = TKUser_PublishState_NONE;
        [self unPlayVideo:_iSessionHandle.localUser.peerID];
    }

    if (_iUserType == TKUserType_Student &&
        _roomJson.configuration.autoOpenAudioAndVideoFlag == NO) {

        if (_roomJson.configuration.beforeClassPubVideoFlag == YES || _isLocalPublish == NO) {

            if (_iSessionHandle.localUser.publishState != TKPublishStateNONE) {

                _isLocalPublish = NO;
                [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                        Publish:(TKPublishStateNONE)completion:nil];
            }
        }
    }

    if (_iUserType == TKUserType_Teacher && _iSessionHandle.isPlayback == NO) {

        if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
            _isLocalPublish = false;
            [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                    Publish:(TKPublishStateBOTH)completion:nil];
        }
    }
    if (_iSessionHandle.isPlayback == YES) {

        if ((self.playbackMaskView.iProgressSlider.value < 0.01 &&
             self.playbackMaskView.playButton.isSelected == YES) ||
            _iSessionHandle.isPlayback == NO) {
            [TKUtil showMessage:TKMTLocalized(@"Class.Begin")];
        }
    } else {
        if (_roomJson.configuration.beforeClassPubVideoFlag == NO) {
            if (_iUserType == TKUserType_Teacher ||
                (_iUserType == TKUserType_Student &&
                 _roomJson.configuration.autoOpenAudioAndVideoFlag)) {

                if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
                    _isLocalPublish = false;
                    [_iSessionHandle
                        sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                               Publish:(TKPublishStateBOTH)completion:nil];
                }
            }
        } else if (_iUserType == TKUserType_Teacher &&
                   _roomJson.configuration.autoOpenAudioAndVideoFlag) {
            if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
                _isLocalPublish = false;
                [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                        Publish:(TKPublishStateBOTH)completion:nil];
            }
        } else if (_iUserType == TKUserType_Student &&
                   _roomJson.configuration.autoOpenAudioAndVideoFlag) {
            if (_iSessionHandle.localUser.publishState != TKPublishStateBOTH) {
                _isLocalPublish = false;
                [_iSessionHandle sessionHandleChangeUserPublish:_iSessionHandle.localUser.peerID
                                                        Publish:(TKPublishStateBOTH)completion:nil];
            }
        }
    }

    _iClassStartTime = ts;
    bool tIsTeacherOrAssis =
        (_iUserType == TKUserType_Teacher || _iUserType == TKUserType_Assistant);

    //如果是1v1并且是学生角色
    BOOL isStdAndRoomOne = (_roomJson.roomtype == TKRoomTypeOneToOne &&
                            (_iUserType == TKUserType_Student));

    /*
     涂鸦权限:
     1.1v1学生根据配置项设置
     2.其他情况，没有涂鸦权限
     3 非老师断线重连不可涂鸦。
     发送:1 1v1 学生发送 2 学生发送，老师发送
     */
    [_iSessionHandle
        configureDraw:isStdAndRoomOne ? _roomJson.configuration.canDrawFlag : tIsTeacherOrAssis
               isSend:YES
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
                              completion:nil];

    [self startClassBeginTimer];

    //上课后需要同步老师的布局样式
    if (_iUserType == TKUserType_Teacher && inlist == NO) {
        //发送信令
        NSString *style = nil;
        if (self.layoutFactory.viewLayout == MainPeople) {
            style = @"MainPeople";
        } else if (self.layoutFactory.viewLayout == OnlyVideo) {
            style = @"OnlyVideo";
        } else {
            style = @"CoursewareDown";
        }
        [TKManyStylePopView publishStyleSignalingWithStyle:style tellID:sTellAll];
    }
}

- (void)onRemoteMsgWithClassEnd:(BOOL)add
                             ID:(NSString *)msgID
                           Name:(NSString *)msgName
                             TS:(unsigned long)ts
                           Data:(NSObject *)data
                         InList:(BOOL)inlist {
    
    [TKUtil showMessage:TKMTLocalized(@"Class.Over")];
    
    //下课后时间归零
    [self.navbarView setTime:0];
    [self invalidateClassBeginTime];
    
    //重置距离下课还有5分钟的提醒项
    _isRemindClassEnd = NO;
    
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
    
    BOOL isStdAndRoomOne = (_roomJson.roomtype == TKRoomTypeOneToOne &&
                            _iUserType == TKUserType_Student);
    [_iSessionHandle configureDraw:isStdAndRoomOne ? _roomJson.configuration.canDrawFlag : false
                            isSend:YES
                                to:sTellAll
                            peerID:_iSessionHandle.localUser.peerID];

    //将所有全屏的视频还原
    [self cancelSplitScreen:nil];
    //将所有拖拽的视频还原
    for (TKCTVideoSmallView *view in self.iStudentVideoViewArray) {
        [self updateMvVideoForPeerID:view.iPeerId];
        view.isDrag = NO;
    }

    // 隐藏画笔工具
    self.brushToolView.hidden = YES;
    [self.brushToolView hideSelectorView];
    [TKScreenShotFactory sharedFactory].canDraw = NO;
    
    [self refreshUI];
    [self tapTable:nil];
    
    // 屏幕截屏清理
    [TKScreenShotFactory clearAfterClass];
    //下课后需要将小工具收起
    [self removeAllToolBoxView];
    
    if (_iUserType == TKUserType_Teacher) {

        if([TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeAudioOnlyRoom];
        }
        
        //通知服务器清除 工具箱数据
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
        if (_roomJson.configuration.forbidLeaveClassFlag == NO) {
            [_iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error) {
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

    //防止ts是毫秒单位
    if (ts / 10000000000 > 0) { ts = ts / 1000; }

    _iServiceTime                   = ts;
    _iLocalTime                     = _iServiceTime - _iClassStartTime;
    _iHowMuchTimeServerFasterThenMe = ts - [[NSDate date] timeIntervalSince1970];

    if (![_iClassBeginTimer isValid]) {

        _iClassBeginTimer = [NSTimer scheduledTimerWithTimeInterval:1
                                                             target:self
                                                           selector:@selector(onClassTimer)
                                                           userInfo:nil
                                                            repeats:YES];
        [_iClassBeginTimer setFireDate:[NSDate date]];
        [[NSRunLoop currentRunLoop] addTimer:_iClassBeginTimer forMode:NSRunLoopCommonModes];
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
    NSDictionary *tDataDic = [self convertWithData:data];
    NSString *tPeerId      = [tDataDic objectForKey:@"studentId"];
    NSInteger failureType  = [tDataDic objectForKey:@"failuretype"]
                                ? [[tDataDic objectForKey:@"failuretype"] integerValue]
                                : 0;

    // 如果这个发布失败的用户是自己点击上台的，需要对自己进行上台失败错误原因进行提示。(只有助教)
    if ([_iSessionHandle getUserWithPeerId:tPeerId].role == TKUserType_Assistant) {
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
            case 4:
                [TKUtil
                    showMessage:[NSString
                                    stringWithFormat:
                                        @"%@%@", [_iSessionHandle localUser].nickName,
                                        TKMTLocalized(
                                            @"Prompt.BackgroundCouldNotOnStage")]]; //拼接上用户名
                break;
            case 5:
                [TKUtil showMessage:TKMTLocalized(@"Prompt.StudentUdpError")];
                break;
            default:
                break;
        }
    }
}

- (void)onRemoteMsgWithVideoDraghandle:(BOOL)add
                                    ID:(NSString *)msgID
                                  Name:(NSString *)msgName
                                    TS:(unsigned long)ts
                                  Data:(NSObject *)data
                                InList:(BOOL)inlist {

    // 隐藏pop弹窗
    [self.layoutFactory.videoArray makeObjectsPerformSelector:@selector(hidePopMenu)];
    
    if (_iStudentSplitScreenArray.count > 0 || _iSessionHandle.iIsFullState) { return; }

    NSDictionary *tDataDic    = [self convertWithData:data];
    NSDictionary *tMvVideoDic = [tDataDic objectForKey:@"otherVideoStyle"];
    _iMvVideoDic              = [NSMutableDictionary dictionaryWithDictionary:tMvVideoDic];
	// 学生 巡课
    if (_iUserType >= 0 && tMvVideoDic && inlist) {

        NSDictionary *dict = [tMvVideoDic objectForKey:_iSessionHandle.localUser.peerID];
        if (dict && [[dict objectForKey:@"isDrag"] boolValue]) {

            [self updateMvVideoForPeerID:_iSessionHandle.localUser.peerID];
            NSDictionary *sendDict = @{ @"otherVideoStyle" : _iMvVideoDic };
            [[TKEduSessionHandle shareInstance] publishVideoDragWithDic:sendDict To:sTellAll];
        }
    }

    [self moveVideo:_iMvVideoDic];
}

- (void)onRemoteMsgWithDoubleClickVideo:(BOOL)add
                                     ID:(NSString *)msgID
                                   Name:(NSString *)msgName
                                     TS:(unsigned long)ts
                                   Data:(NSObject *)data
                                 InList:(BOOL)inlist {

    [self tapTable:nil];
    NSDictionary *tDataDic = [self convertWithData:data];
    if (self.layoutFactory.viewLayout == CoursewareDown) {

        if (![[tDataDic objectForKey:@"doubleId"] isKindOfClass:[NSString class]]) { return; }

        //移除superview上面的其他videoView
        for (TKCTVideoSmallView *view in self.layoutFactory.videoArray) {
            if (!view.iRoomUser) { continue; }
            view.isDrag          = NO;
            view.isSplit         = NO;
            view.backgroundColor = UIColor.clearColor;
            // 只看老师和自己时 学生显示老师拖到课件上的人的音视频，恢复后不在显示
            if (_roomJson.configuration.onlyMeAndTeacherVideo &&
                _iUserType == TKUserType_Student) {

                if (view.iRoomUser.role != TKUserType_Teacher &&
                    ![view.iRoomUser.peerID isEqualToString:_iSessionHandle.localUser.peerID]) {

                    [self unPlayVideo:view.iRoomUser.peerID];
                }
            }
        }

        NSString *userID = [tDataDic objectForKey:@"doubleId"];
        BOOL isSplit     = [[tDataDic objectForKey:@"isScreen"] boolValue];

        TKCTVideoSmallView *videoView = [self.layoutFactory videoViewWithUserID:userID];

        // 只看老师和自己时 学生显示老师拖到课件上的人的音视频，恢复后不在显示
        if (_roomJson.configuration.onlyMeAndTeacherVideo &&
            _iUserType == TKUserType_Student) {

            TKRoomUser *user = [_iSessionHandle.roomMgr getRoomUserWithUId:userID];

            if (user && !videoView && isSplit) {

                videoView = [self.layoutFactory playVideoWithUser:user];
            }
        }

        _splitVideoView = isSplit ? videoView : nil;

        if (videoView) {

            videoView.isSplit         = isSplit;
            videoView.videoMode       = TKVideoViewMode_Fill;
            videoView.maskLayout      = TKMaskViewLayout_Normal;
            videoView.backgroundColor = [TKTheme colorWithPath:@"ClassRoom.TKVideoView.tk_double_click_video_bg"];
            //刷新view
            [self.layoutFactory splitScreenWithInfo:tDataDic superview:self.whiteboardBackView];

            //设置分屏frame
            if (isSplit) {
                videoView.frame = self.whiteboardBackView.bounds;
                [self.whiteboardBackView addSubview:videoView];
            }
        }
    }

    for (NSString *peerId in _iMvVideoDic.allKeys) { [self updateMvVideoForPeerID:peerId]; }
}

- (void)onRemoteMsgWithVideoSplitScreen:(BOOL)add
                                     ID:(NSString *)msgID
                                   Name:(NSString *)msgName
                                     TS:(unsigned long)ts
                                   Data:(NSObject *)data
                                 InList:(BOOL)inlist {

    [self tapTable:nil];
    NSDictionary *tDataDic = [self convertWithData:data];

    NSMutableArray *array = [NSMutableArray arrayWithArray:tDataDic[@"userIDArry"]];

    //取消全屏的操作
    [self cancelSplitScreen:array];

    _iStudentSplitScreenArray = array;
    //白板全屏状态下不执行分屏回调
    if (_iSessionHandle.iIsFullState) { return; }
    [self sVideoSplitScreen:_iStudentSplitScreenArray];
    [_splitScreenView refreshSplitScreenView];

    /**
     在分屏回调中只返回了分屏用户的 iPeerId ，而在拖拽回调中返回了isDrag，
     所以在发生拖拽行为时，进行分屏操作需要将_iMvVideoDic内的isDrag置为NO
     */
    for (NSString *peerId in _iMvVideoDic.allKeys) { [self updateMvVideoForPeerID:peerId]; }
}

- (void)onRemoteMsgWithVideoZoom:(BOOL)add
                              ID:(NSString *)msgID
                            Name:(NSString *)msgName
                              TS:(unsigned long)ts
                            Data:(NSObject *)data
                          InList:(BOOL)inlist {

    // 视频缩放
    NSDictionary *tDataDic = [self convertWithData:data];

    // 数据格式：{"ScaleVideoData":{"ffefbe63-50ae-4959-a872-3dd38397988d":{"scale":1.7285714285714286}}}
    NSDictionary *peerIdToScaleDic = [tDataDic objectForKey:@"ScaleVideoData"];
    _iScaleVideoDict               = peerIdToScaleDic;

    //白板全屏状态下不执行缩放回调
    if (_iSessionHandle.iIsFullState) { return; }
    [self sScaleVideo:peerIdToScaleDic];
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
        _iUserType == TKUserType_Student) { //如果是全体禁言并且后进入课堂

        [_iSessionHandle sessionHandleChangeUserProperty:_iSessionHandle.localUser.peerID
                                                TellWhom:sTellAll
                                                     Key:sDisablechat
                                                   Value:@(true)
                                              completion:nil];
    }

    NSMutableDictionary *tDic = [NSMutableDictionary dictionary];
    [tDic setValue:@(add) forKey:@"isBanSpeak"];
    [[NSNotificationCenter defaultCenter] postNotificationName:sEveryoneBanChat object:tDic];

    TKChatMessageModel *chatMessageModel =
        [[TKChatMessageModel alloc] initWithMsgType:TKChatMessageTypeTips
                                               role:TKChatRoleTypeMe
                                            message:add ? TKMTLocalized(@"Prompt.BanChatInView")
                                                        : TKMTLocalized(@"Prompt.CancelBanChatInView")
                                            cospath:nil userName:_iSessionHandle.localUser.nickName
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

            _pageControl.fullScreen.selected = _isRemoteFullScreen = add;
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
        if ([associatedMsgID isEqualToString:sClassBegin] || [associatedMsgID hasPrefix:@"CaptureImg"]) {
            // 教室截屏 桌面截屏
            if ([key hasPrefix:@"CaptureImg"]) {
                [screenShotMSG addObject:[msglist objectForKey:key]];
            }
        } else if ([associatedMsgID hasPrefix:@"CaptureImg"]) {
            if ([name isEqualToString:sSharpsChange]) {
                [screenShotMSG addObject:[msglist objectForKey:key]];
            }
        } else {
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
        screenShotMSG = [[screenShotMSG
            sortedArrayUsingComparator:^NSComparisonResult(id _Nonnull obj1, id _Nonnull obj2) {
                return [[obj1 objectForKey:@"seq"] compare:[obj2 objectForKey:@"seq"]];
            }] mutableCopy];

        [screenShotMSG
            enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
                [TKScreenShotFactory captureImgWithParam:obj msgName:obj[@"name"] delete:NO];
            }];
    }
}

#pragma mark - 计时器
- (void)onRemoteMsgWithShowTimerWithAdd:(BOOL)add
                                andData:(NSObject *)data
                         receiveMsgTime:(long)time {

    if (add) {

        NSDictionary *dataDic = [self convertWithData:data];

        BOOL isStatus       = [dataDic[@"isStatus"] boolValue];
        BOOL isRestart      = [dataDic[@"isRestart"] boolValue];
        BOOL isShow         = [dataDic[@"isShow"] boolValue];
        NSArray *timerArray = dataDic[@"sutdentTimerArry"];

        NSInteger minute =
            [[NSString stringWithFormat:@"%@%@", timerArray[0], timerArray[1]] integerValue];
        NSInteger second =
            [[NSString stringWithFormat:@"%@%@", timerArray[2], timerArray[3]] integerValue];

        if (_iUserType == TKUserType_Teacher ||
            _iUserType == TKUserType_Patrol ||
            _iUserType == TKUserType_Playback) {

            if (!_timerView) {

                _timerView = [[TKTimerView alloc] init];
                [_whiteboardBackView addSubview:_timerView];

                [_timerView mas_remakeConstraints:^(MASConstraintMaker *make) {
                    make.centerX.equalTo(_whiteboardBackView.mas_centerX);
                    make.centerY.equalTo(_whiteboardBackView.mas_centerY);
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

        } else if (_iUserType == TKUserType_Student && !isShow) {
            if (!_stuTimer) {

                _stuTimer = [[TKStuTimerView alloc] init];
                [_whiteboardBackView addSubview:_stuTimer];

                [_stuTimer mas_remakeConstraints:^(MASConstraintMaker *make) {
                    make.centerX.equalTo(_whiteboardBackView.mas_centerX);
                    make.centerY.equalTo(_whiteboardBackView.mas_centerY);
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
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
}

- (void)onRemoteMsgWithResponderView:(BOOL)add
                                  ID:(NSString *)msgID
                                Name:(NSString *)msgName
                                  TS:(unsigned long)ts
                                Data:(NSObject *)data
                              InList:(BOOL)inlist {

    NSDictionary *dataDic = [self convertWithData:data];

    if ([msgName isEqualToString:sQiangDaQi]) {
        [TKPopViewHelper sharedInstance].isResponderToolOn = add;
        if (add) {
            if (!self.responderView) {
                UIView *toolBV        = _whiteboardBackView;
                _responderView        = [[TKToolsResponderView alloc] init];
                _responderView.center = CGPointMake(toolBV.width / 2, toolBV.height / 2);
                [toolBV addSubview:_responderView];
                [_responderView mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.centerX.equalTo(toolBV.mas_centerX);
                    make.centerY.equalTo(toolBV.mas_centerY);
                }];
            }
            [self.responderView receiveShowResponderViewWith:dataDic];
        } else {

            self.responderView.hidden = YES;
            [self.responderView removeFromSuperview];
            self.responderView = nil;
        }

    } else if ([msgName isEqualToString:sQiangDaZhe]) {

        if (self.responderView) {
            NSArray *arr = [msgID componentsSeparatedByString:@"_"];
            [self.responderView receiveResponderUser:dataDic peerid:[arr lastObject]];
        }

    } else if ([msgName isEqualToString:sResponderDrag]) {
    }
}

- (void)restoreMp3ViewFrame {
    // mp3 view 老师带有进度条 frame不同
    if (!self.iMediaView.hasVideo) {
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
                       CGRectGetMaxY(self.iTKEduWhiteBoardView.frame) -
                           CGRectGetHeight(_iMediaView.frame) - (IS_PAD ? 60 : 40),
                       CGRectGetWidth(_iMediaView.frame), CGRectGetHeight(_iMediaView.frame));
    } else {
        //老师
        self.iMediaView.frame =
            CGRectMake(CGRectGetMinX(self.iTKEduWhiteBoardView.frame) +
                           (CGRectGetWidth(self.iTKEduWhiteBoardView.frame) -
                            CGRectGetWidth(_iMediaView.frame)) /
                               2,
                       CGRectGetMaxY(self.iTKEduWhiteBoardView.frame) -
                           CGRectGetHeight(_iMediaView.frame) - (IS_PAD ? 80 : 60),
                       CGRectGetWidth(_iMediaView.frame), CGRectGetHeight(_iMediaView.frame));
    }
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

#pragma mark - UIGestureRecognizerDelegate
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

    if (self.layoutFactory.videoArray) {
        [self.layoutFactory.videoArray makeObjectsPerformSelector:@selector(hidePopMenu)];
    }
}

- (void)pinchGestureAction:(UIPinchGestureRecognizer *)gestureRecognizer {
    TKCTVideoSmallView *smallView = (TKCTVideoSmallView *)gestureRecognizer.view;

    // 巡课不允许缩放
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) { return; }

    if (![TKEduSessionHandle shareInstance].iIsCanDraw) { return; }

    // 没有拖出去不允许缩放
    if (smallView.isDrag == NO) { return; }

    if (smallView.isPicInPic == YES) { return; }

    if (gestureRecognizer.state == UIGestureRecognizerStateBegan ||
        gestureRecognizer.state == UIGestureRecognizerStateChanged) {

        CGPoint center  = smallView.center;
        CGRect newframe = smallView.frame;
        CGFloat height  = newframe.size.height * gestureRecognizer.scale;
        CGFloat width   = newframe.size.width * gestureRecognizer.scale;

        if (width < smallView.originalWidth) {
            // 无法缩小至比初始化大小还小
            return;
        }

        // 保证不超出白板

        if (height >= CGRectGetHeight(self.iTKEduWhiteBoardView.frame)) { return; }

        if (width >= CGRectGetWidth(self.iTKEduWhiteBoardView.frame)) { return; }

        smallView.frame =
            CGRectMake(center.x - width / 2.0, center.y - height / 2.0, width, height);

        gestureRecognizer.scale = 1;

        // 只有老师发送缩放信令
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            NSDictionary *tDict = @{
                @"ScaleVideoData" :
                    @{smallView.iRoomUser.peerID : @{@"scale" : @(width / smallView.originalWidth)}}
            };
            NSData *jsonData = [NSJSONSerialization dataWithJSONObject:tDict
                                                               options:NSJSONWritingPrettyPrinted
                                                                 error:nil];
            NSString *jsonString =
                [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sVideoZoom
                                                                 ID:sVideoZoom
                                                                 To:sTellAllExpectSender
                                                               Data:jsonString
                                                               Save:true
                                                    AssociatedMsgID:nil
                                                   AssociatedUserID:nil
                                                            expires:0
                                                         completion:nil];
        }
    }

    if (gestureRecognizer.state == UIGestureRecognizerStateEnded) {

        smallView.center = self.whiteboardBackView.center;
        // 只有老师发送缩放信令
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            NSDictionary *tDict = @{
                @"ScaleVideoData" : @{
                    smallView.iRoomUser.
                    peerID : @{@"scale" : @(smallView.frame.size.width / smallView.originalWidth)}
                }
            };
            NSData *jsonData = [NSJSONSerialization dataWithJSONObject:tDict
                                                               options:NSJSONWritingPrettyPrinted
                                                                 error:nil];
            NSString *jsonString =
                [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sVideoZoom
                                                                 ID:sVideoZoom
                                                                 To:sTellAllExpectSender
                                                               Data:jsonString
                                                               Save:true
                                                    AssociatedMsgID:nil
                                                   AssociatedUserID:nil
                                                            expires:0
                                                         completion:nil];
        }
    }
}

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

        [self.controlView refreshUI];
    }
}

- (void)onClassCurrentTimer {

    if (!_iHowMuchTimeServerFasterThenMe) return;

    _iCurrentTime = [[NSDate date] timeIntervalSince1970] + _iHowMuchTimeServerFasterThenMe;

    NSTimeInterval interval = _roomJson.endtime - _iCurrentTime;
    NSInteger time          = interval;
    //未到下课时间：老师点下课
    //下课后不离开教室forbidLeaveClassFlag—>提前5分钟给出提示语（老师、助教）—>下课时间到，课程结束，一律离开
    
    if (time == 300 && _iUserType == TKUserType_Teacher) {
        [TKUtil showMessage:[NSString
                             stringWithFormat:@"5%@", TKMTLocalized(@"Prompt.ClassEndTime")]];
    }
    if (time <= 0) {
        [TKUtil showMessage:TKMTLocalized(@"Prompt.ClassEnd")];
        
        [self.iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error) {
            if (error) {
                TKLog(@"退出房间错误: %@", error);
            }
        }];

    }
    
}
- (void)onClassTimer {
    /*
     按下课时间结束课堂配置项的说明：
     1.未到下课时间,下课后离开教室的课堂,老师点击下课,就按正常的,老师留下,其他学生一律离开
	 2.未到下课时间,下课后不离开教室的课堂,老师点击下课后,所有人离开,
    	到了下课时间的前5分钟老师跟助教的页面给出提示语,下课时间到后,所有人离开
     3.未到下课时间,老师没有点击下课,下课时间一到,所有人离开
     4.未到下课时间,老师没有点击下课,老师离开教室10分钟,课程结束,所有人离开
     5.到了下课时间,提前5分钟给出提示语,时间到一律离开（点击上课）
     6.点击上课后,未到下课时间,提前5分钟给提示语,到时间所有人离开
     7.点击上课后,距离下课时间不到5分钟,剩几分钟下课就提示几分钟
     8.已经时间到期的课堂,进入课堂提示课堂过期
     9.助教也显示
     */

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

        if (time > 0) {
            
            if (time <= 300 && _isRemindClassEnd == NO) {
                
                _isRemindClassEnd = YES;
                int minute        = (int)(time / 60);
                if (minute > 0) {
                    
                    [TKUtil
                     showClassEndMessage:[NSString stringWithFormat:@"%d%@", minute,
                                          TKMTLocalized(@"Prompt.ClassEndTime")]];
                }
                else {
                    int second        = time % 60;
                    [TKUtil
                     showClassEndMessage:[NSString stringWithFormat:@"%d%@", second,
                                          TKMTLocalized(@"Prompt.ClassEndTimeseconds")]];
                }
            }
        }
        else {
            [TKUtil showMessage:TKMTLocalized(@"Prompt.ClassEnd")];
            [_iSessionHandle sessionHandleLeaveRoom:NO Completion:^(NSError *_Nonnull error) {
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
        _iLocalTime       = 0;
        _iClassBeginTimer = nil;
    }
}
- (void)invalidateClassCurrentTime {
    if (_iAfterClassTimer) {
        [_iAfterClassTimer invalidate];
        _iAfterClassTimer = nil;
    }
}

- (void)startClassBeginTimer {
    _iLocalTime = 0;
    [_iClassBeginTimer setFireDate:[NSDate date]];
}

#pragma mark - 收到点击相机/相册的通知

- (void)uploadPhotos:(NSNotification *)notify {
    if ([notify.object isEqualToString:sTakePhotosUploadNotification]) {
        //相机
        [self chooseAction:1 delay:NO];
    } else if ([notify.object isEqualToString:sChoosePhotosUploadNotification]) {
        //相册
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
// 上传
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
                //     cospath = "https://demodoc-1253417915.cos.ap-guangzhou.myqcloud.com";
                //     msg = "/cospath/20190715_095500_avuhztxl.png";
                //     msgtype = onlyimg;
                
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

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context {

    if ([@"idleTimerDisabled" isEqualToString:keyPath] && _iSessionHandle.iIsJoined &&
        ![[change objectForKey:@"new"] boolValue]) {
        // 屏幕常亮
        [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    }
}

- (void)changeServer:(NSString *)server {

    if ([server isEqualToString:self.currentServer]) { return; }
    self.currentServer = server;
    [[NSUserDefaults standardUserDefaults] setObject:self.currentServer forKey:@"server"];
}

#pragma mark Public
#pragma mark Private
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

// 获取礼物数
- (void)getTrophyNumber {

    // 老师不需要获取礼物
    if (_iUserType != TKUserType_Student || _iSessionHandle.isPlayback == YES) {
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

                    _iSessionHandle.localUser.properties[sGiftNumber] = @(giftnumber);
//                    [_iSessionHandle
//                        sessionHandleChangeUserProperty:_iSessionHandle.localUser.peerID
//                                               TellWhom:sTellAll
//                                                    Key:sGiftNumber
//                                                  Value:@(giftnumber)
//                                             completion:nil];
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
    [self.layoutFactory unPlayAllViews];

//    [_iSessionHandle.whiteBoardManager showDocumentWithFile:(TKFileModel *)_iSessionHandle.whiteBoard
//                                                   isPubMsg:YES];
    [_iSessionHandle.whiteBoardManager disconnect:nil];
    [_iSessionHandle.whiteBoardManager resetWhiteBoardAllData];

    _iSessionHandle.onPlatformNum = 0;

    // 播放的MP4前，先移除掉上一个MP4窗口
    _iSessionHandle.iCurrentMediaDocModel = nil;
    if (self.iMediaView) {
        [self.iMediaView deleteWhiteBoard];
        [self.iMediaView removeFromSuperview];
        self.iMediaView = nil;
    }

    if (self.iScreenView) {
        [self.iScreenView removeFromSuperview];
        self.iScreenView = nil;
    }
    [self.splitScreenView deleteAllVideoSmallView];

    [self.iStudentSplitScreenArray removeAllObjects];

    self.splitScreenView.hidden = YES;

    // fix bug:全员奖励页面是显示在windows上的，如果网断了，退出教室，view还没消失
    UIWindow *window           = [UIApplication sharedApplication].keyWindow;
    NSEnumerator *subviewsEnum = [window.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:NSClassFromString(@"TKTrophyView")]) {
            [subview removeFromSuperview];
        }
    }

    //网络断开连接 清理掉所有小工具，防止在网络断开过程中 老师关闭了小工具 导致关闭的信令没接收到
    [self removeAllToolBoxView];

    if (self.navbarView.netTipView) { [self.navbarView.netTipView changeDetailSignImage:NO]; }
    if (self.netDetailView) {
        [self.netDetailView removeFromSuperview];
        self.netDetailView = nil;
    }
}

- (void)removeAllToolBoxView {
    //网络断开连接 清理掉所有小工具，防止在网络断开过程中 老师关闭了小工具 导致关闭的信令没接收到

    // 1.关闭答题卡
    TKAnswerSheetView *answerSheet = [self answerSheetForView:self.view];
    [answerSheet removeFromSuperview];
    answerSheet = nil;

    // 2.关闭抢答器
    self.responderView.hidden = YES;
    [self.responderView removeFromSuperview];
    self.responderView = nil;

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
    [_iSessionHandle.whiteBoardManager resetWhiteBoardAllData];

    [_layoutFactory unPlayAllViews];

    // 播放的MP4前，先移除掉上一个MP4窗口
    if (self.iMediaView) {
        [self.iMediaView removeFromSuperview];
        self.iMediaView = nil;
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

    [_splitScreenView deleteAllVideoSmallView];
    [_iStudentSplitScreenArray removeAllObjects];

    /**暂时这么解决s双重奖杯*/
    for (TKCTVideoSmallView *samllView in self.iStudentVideoViewArray) {
        [samllView removeAllObserver];
    }

    [_iStudentVideoViewArray removeAllObjects];
    _iStudentVideoViewArray = nil;
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
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    NSLog(@"roomController----dealloc");
}
- (void)didReceiveMemoryWarning {
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

- (TKVideoViewLayoutFactory *)layoutFactory {
    if (!_layoutFactory) {
        _layoutFactory =
            [[TKVideoViewLayoutFactory alloc] initWithWhiteBoard:self.whiteboardBackView
                                                       superview:self.backgroundImageView];
        _layoutFactory.delegate = self;
    }
    return _layoutFactory;
}

@end

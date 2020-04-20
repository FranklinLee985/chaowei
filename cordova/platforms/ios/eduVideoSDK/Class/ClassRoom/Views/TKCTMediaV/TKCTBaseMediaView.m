//
//  TKCTBaseMediaView.m
//  EduClass
//
//  Created by talkcloud on 2018/11/2.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTBaseMediaView.h"
#import "TKProgressSlider.h"
#import "TKEduSessionHandle.h"
#import <UIImage+GIF.h>
#import <TKRoomSDK/TKRoomSDK.h>
#import "TKMediaPlayingHelper.h"

#define ThemePBKP(args) [@"ClassRoom.PlayBack." stringByAppendingString:args]
#define ThemeKP(args) [@"ClassRoom.TKMediaView." stringByAppendingString:args]
@interface TKCTBaseMediaView () <UIScrollViewDelegate>

@property (nonatomic, strong) NSString *peerId;//用户id
@property (nonatomic, strong) UIView *bview;//背景视图
//@property (nonatomic, strong) UIView *bottmView;//底部视图
@property (nonatomic, strong) UIButton *backButton;//关闭按钮
@property (nonatomic, strong) UIButton *playButton;//播放按钮
@property (nonatomic, strong) UIButton *fullButton;//全屏按钮
@property (nonatomic, strong) UILabel *titleLabel;//标题名称
@property (nonatomic, strong) UILabel *timeLabel;//时间
@property (nonatomic, strong) TKProgressSlider *iProgressSlider;//播放进度条
@property (nonatomic, strong) UIButton         *iAudioButton;//声音图标
@property (nonatomic, strong) TKProgressSlider *iAudioslider;//音量进度条

@property (nonatomic, strong) UITapGestureRecognizer * sliderTap;// 进度条点击

@property (nonatomic, strong) UIImageView *GIFImageView;//动画视图

@property (nonatomic, strong) UIImageView *loadingView;//loading加载页面
@property (nonatomic, strong) UIView *loadBackgroundView;//loading加载背景
@property (nonatomic, strong) UIScrollView *scrollView;

@property (nonatomic, assign) NSTimeInterval duration;
@property (nonatomic, assign) BOOL iIsPlay;
@property (nonatomic, assign) BOOL isPlayEnd;

@property (nonatomic, assign) BOOL isFileShare;//是否是电影共享
@property (nonatomic, assign) BOOL isPlay;//是否播放状态
@property (nonatomic, assign) NSInteger width;//视频显示的原始宽度
@property (nonatomic, assign) NSInteger height;//视频显示的原始高度
@property (nonatomic, strong) NSString *filename;


@property (nonatomic, strong) NSTimer *timer;//定时器
@property (nonatomic, assign) int timerCount;
@property (nonatomic) BOOL isSliding; // 是否在拖拽
@property (nonatomic, assign) NSTimeInterval current;
@end

@implementation TKCTBaseMediaView

- (void)didMoveToSuperview
{
    [super didMoveToSuperview];
    [TKMediaPlayingHelper sharedInstance].isOnScreen = YES;
}

- (void)removeFromSuperview
{
    [super removeFromSuperview];
    [TKMediaPlayingHelper sharedInstance].isOnScreen = NO;
}

//创建timer
- (void)creatTimer{
    _timerCount = 0;
    _timer = [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(timerFire) userInfo:nil repeats:true];
    [_timer setFireDate:[NSDate date]];
}
-(void)timerFire{
    _timerCount++;
    if (_timerCount>8) {
        //隐藏控件
//        self.bottmView.hidden = YES;
        self.bview.hidden = YES;
        [_timer setFireDate:[NSDate distantFuture]];
    }
}

- (void)resetTimer{
    //显示控件
//    self.bottmView.hidden = NO;
    self.bview.hidden = NO;
    _timerCount = 0;
    [_timer setFireDate:[NSDate date]];
}
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    if (_timer) {
        [self resetTimer];
    }
    
}


- (instancetype)initWithMediaPeerID:(NSString *)peerId
                   extensionMessage:(NSDictionary *)extensionMessage
                              frame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.isShowing = YES;
        _peerId = peerId;
        _isFileShare = false;
        
        _isPlayEnd      = NO;
        self.isSliding = NO;
        self.width = [TKUtil getIntegerValueFromDic:extensionMessage Key:@"width"];
        self.height = [TKUtil getIntegerValueFromDic:extensionMessage Key:@"height"];
        self.isPlay = ![TKUtil getBOOValueFromDic:extensionMessage Key:@"pause"];
        self.hasVideo = [TKUtil getBOOValueFromDic:extensionMessage Key:@"video"];
        self.filename = [TKUtil optString:extensionMessage Key:@"filename"];
        _duration       = [TKUtil getIntegerValueFromDic:extensionMessage Key:@"duration"];
        
        
        [[NSNotificationCenter defaultCenter]postNotificationName:sTapTableNotification object:nil];
        if (self.hasVideo) {
            [self ac_initVideoSubviews:frame];
            
            [self creatTimer];
            
            //开启视频标注配置项后允许加载白板
            if ([TKEduClassRoom shareInstance].roomJson.configuration.videoWhiteboardFlag) {
            
                [self ac_initMediaMarkView];
            }
            //TODO: 直接打开视频标注方便测试
//            [self ac_initMediaMarkView];
        
        }
        else{
            [self ac_initAudioSubviews:frame];
        }
        
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(unPluggingHeadSet:) name:sUnunpluggingHeadsetNotification object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(pluggInMicrophone:) name:sPluggInMicrophoneNotification object:nil];
        
    }
    return self;
}

//MARK: 创建视频标注
- (void)ac_initMediaMarkView
{
    [self addSubview:[TKEduSessionHandle shareInstance].mediaMarkView];
    [TKEduSessionHandle shareInstance].mediaMarkView.hidden = YES;
    [[TKEduSessionHandle shareInstance].mediaMarkView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_left);
        make.right.equalTo(self.mas_right);
        make.centerY.equalTo(self.mas_centerY);
        make.height.equalTo(self.mas_width).multipliedBy(9.0f / 16);
    }];
    
    [[TKEduSessionHandle shareInstance].mediaMarkView.exitBtn addTarget:self action:@selector(mediaMarkViewPlayMedia) forControlEvents:UIControlEventTouchUpInside];
}

- (void)mediaMarkViewPlayMedia
{
    [self playOrPauseAction:self.playButton];
}

- (instancetype)initScreenShare:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _isFileShare = false;
        self.scrollView = [[UIScrollView alloc] initWithFrame:frame];
        self.scrollView.delegate = self;
        self.scrollView.backgroundColor = [UIColor blackColor];
        self.scrollView.maximumZoomScale = 4.0;
        
        [self addSubview:self.scrollView];
        //开启视频标注配置项后允许加载白板
        if ([TKEduClassRoom shareInstance].roomJson.configuration.videoWhiteboardFlag) {
            
            [self ac_initMediaMarkView];
        }
        
        [[NSNotificationCenter defaultCenter]postNotificationName:sTapTableNotification object:nil];
        //[self ac_initVideoSubviews];
        [[NSNotificationCenter defaultCenter]removeObserver:self];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(unPluggingHeadSet:) name:sUnunpluggingHeadsetNotification object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(pluggInMicrophone:) name:sPluggInMicrophoneNotification object:nil];
    }
    return self;
}

- (instancetype)initFileShare:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _isFileShare = true;
        [self ac_initVideoSubviews:frame];
        //开启视频标注配置项后允许加载白板
        if ([TKEduClassRoom shareInstance].roomJson.configuration.videoWhiteboardFlag) {
            
            [self ac_initMediaMarkView];
        }
        [[NSNotificationCenter defaultCenter]postNotificationName:sTapTableNotification object:nil];
        [[NSNotificationCenter defaultCenter]removeObserver:self];
        
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(unPluggingHeadSet:) name:sUnunpluggingHeadsetNotification object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(pluggInMicrophone:) name:sPluggInMicrophoneNotification object:nil];
    }
    return self;
}

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
    return scrollView.subviews.firstObject;
}

- (void)insertViewToScrollView:(UIView *)view {
    [self.scrollView insertSubview:view atIndex:0];
}

-(void)unPluggingHeadSet:(NSNotification *)notifi{
    
    [self audioVolum: [TKEduSessionHandle shareInstance].iVolume];
}

-(void)pluggInMicrophone:(NSNotification *)notifi{
    [self audioVolum: [TKEduSessionHandle shareInstance].iVolume];
}

-(void)dealloc{
    if (_loadingView && _loadingView.isAnimating) {
        [_loadingView stopAnimating];
    }
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}

// 添加view，子类实现
- (void)ac_initAudioSubviews:(CGRect)frame {
    
    //播放动画
    self.GIFImageView = [[UIImageView alloc]initWithFrame:CGRectMake(4, 4, frame.size.height - 8, frame.size.height - 8)];
    [self addSubview:self.GIFImageView];
    [self bringSubviewToFront:self.GIFImageView];
    self.GIFImageView.sakura.image(ThemeKP(@"mp3playerDefaultImage"));
    
    if (self.isPlay) {
        [self.GIFImageView tkPlayGifAnim:[TKHelperUtil mp3PlayGif]];
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role== TKUserType_Patrol || [TKEduSessionHandle shareInstance].localUser.role== TKUserType_Student || ([TKEduSessionHandle shareInstance].localUser.role==TKUserType_Playback)) {
        
        return;
    }
    
    //bottomBar
    self.bview = ({
        UIView *bview = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        bview.layer.masksToBounds = YES;
        bview.layer.cornerRadius = (frame.size.height)/2.0;
        bview.sakura.backgroundColor(ThemePBKP(@"sliderBackGoundColor"));
        bview.sakura.alpha(ThemePBKP(@"sliderAlpha"));
        bview.backgroundColor = [bview.backgroundColor colorWithAlphaComponent:bview.alpha];
        [self addSubview:bview];
        [self sendSubviewToBack:bview];
        bview;
    });
    
    
    //返回按钮
    self.backButton = [[UIButton alloc] initWithFrame:CGRectMake(_bview.rightX-40, (frame.size.height-30)/2, 30, 30)];
    self.backButton.sakura.image(ThemeKP(@"mp3player_close"),UIControlStateNormal);
    [self.backButton addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.backButton];
    
    //播放按钮
    self.playButton = [[UIButton alloc] initWithFrame:CGRectMake(frame.size.height+10, (frame.size.height-50)/2.0, 50, 50)];
    self.playButton.center = self.GIFImageView.center;
    self.playButton.sakura.image(ThemePBKP(@"playBtnPauseImage"),UIControlStateSelected);
    self.playButton.sakura.image(ThemePBKP(@"playBtnPlayImage"),UIControlStateNormal);
    [self.playButton addTarget:self action:@selector(audioPlayOrPauseAction:) forControlEvents:UIControlEventTouchDown];
    self.playButton.selected = self.isPlay;;
    [self addSubview:self.playButton];
    
    //时间
    self.timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(frame.size.width - 86 - frame.size.height / 2, 13, 86, 14)];
    self.timeLabel.text = @"00:00/00:00";
    self.timeLabel.textAlignment = NSTextAlignmentRight;
    self.timeLabel.font = TKFont(12);
    self.titleLabel.textColor = self.timeLabel.textColor = [UIColor whiteColor];

    [self addSubview:self.timeLabel];
    
    //名称
    self.titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.GIFImageView.frame)+10, 13, frame.size.width - 86 - self.GIFImageView.width - 10 - frame.size.height / 2, 14)];
    self.titleLabel.text = self.filename;
    self.titleLabel.font = TKFont(12);
    self.titleLabel.textColor = [UIColor whiteColor];
    self.titleLabel.textAlignment = NSTextAlignmentLeft;
    [self addSubview:self.titleLabel];
    
    // 进度拖拽滑块
    self.iProgressSlider = [[TKProgressSlider alloc]initWithFrame:CGRectMake(CGRectGetMaxX(self.GIFImageView.frame)+ 10, CGRectGetMaxY(self.timeLabel.frame) + 0, frame.size.width - CGRectGetMaxX(self.GIFImageView.frame) - 10 - frame.size.height / 2, _bview.height / 4)];
    self.iProgressSlider.sakura.minimumTrackTintColor(ThemePBKP(@"sliderMinimumTrackTintColor"));
    self.iProgressSlider.sakura.maximumTrackTintColor(ThemePBKP(@"sliderMaximumTrackTintColor"));
    self.iProgressSlider.continuous = NO;
    [self.iProgressSlider setThumbImage:[TKTheme imageWithPath:ThemePBKP(@"sliderControlImage")]  forState:UIControlStateNormal];
    
    [self addSubview:self.iProgressSlider];
    
    [self.iProgressSlider addTarget:self action:@selector(progressValueChange:) forControlEvents:UIControlEventValueChanged];
    [self.iProgressSlider addTarget:self action:@selector(progressTouchDown:) forControlEvents:UIControlEventTouchDown];
    [self.iProgressSlider addTarget:self action:@selector(progressTouchUp:) forControlEvents:UIControlEventTouchUpInside];
    
    self.sliderTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(progressTap:)];
    [self.iProgressSlider addGestureRecognizer:self.sliderTap];
    
    // 声音开关按钮
    self.iAudioButton = [[UIButton alloc]initWithFrame:CGRectMake(CGRectGetMaxX(self.GIFImageView.frame) + 0, CGRectGetMaxY(self.iProgressSlider.frame), 40, 40)];
    self.iAudioButton.sakura.image(ThemePBKP(@"voiceBtnSelectedImage"),UIControlStateSelected);
    self.iAudioButton.sakura.image(ThemePBKP(@"voiceBtnNormalImage"),UIControlStateNormal);
    self.iAudioButton.imageView.contentMode = UIViewContentModeCenter;
    [self.iAudioButton addTarget:self action:@selector(audioButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.iAudioButton];
    
    //声道滑块
    self.iAudioslider = [[TKProgressSlider alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.iAudioButton.frame) + 0,
                                                                           self.iAudioButton.y,
                                                                           self.iProgressSlider.width - self.iAudioButton.width + 10,
                                                                           frame.size.height / 4)];
    //    [[TKProgressSlider alloc] initWithFrame:CGRectMake(CGRectGetMinX(backButton.frame)-frame.size.width*0.12-15, 25, frame.size.width*0.12, 25)];
    self.iAudioslider.centerY = self.iAudioButton.centerY;
    self.iAudioslider.sakura.minimumTrackTintColor(ThemePBKP(@"sliderMinimumTrackTintColor"));
    self.iAudioslider.sakura.maximumTrackTintColor(ThemePBKP(@"sliderMaximumTrackTintColor"));
    [self.iAudioslider setThumbImage:[TKTheme imageWithPath:ThemePBKP(@"sliderControlImage")] forState:UIControlStateNormal];
    
    self.iAudioslider.enabled = YES;
    self.iAudioslider.value = 1;
    [self.iAudioslider addTarget:self action:@selector(audioVolumChange:) forControlEvents:UIControlEventValueChanged];
    //    [self addSubview:selflignment = NSTextAlignmentLeft;
    [self addSubview:self.iAudioslider];
    
}
- (void)loadLoadingView{
    
    UIImage *img = [TKTheme imageWithPath:ThemeKP(@"loading_00011")];
    
    CGFloat scale = img.size.width / img.size.height;
    CGFloat zoom = IS_PAD ? 7.0 : 3.0;
    CGFloat loadWidth = scale * self.height / zoom;
    if (loadWidth>self.width) {
        loadWidth = self.width;
    }
    CGFloat loadHeight = loadWidth /scale;
    
    _loadBackgroundView = [[UIView alloc] initWithFrame:self.bounds];
    _loadBackgroundView.sakura.backgroundColor(ThemeKP(@"mp4loadingBackgroundColor"));
    [self addSubview:_loadBackgroundView];
    
    _loadingView = [[UIImageView alloc]initWithFrame:CGRectMake((self.frame.size.width-loadWidth)/2.0,(self.frame.size.height-loadHeight)/2.0, loadWidth, loadHeight)];
    [self addSubview:_loadingView];
    [self bringSubviewToFront:_loadingView];
    [_loadingView tkPlayGifAnim:[TKHelperUtil mp4PlayGif]];
    
    [_loadBackgroundView addSubview:_loadingView];
    
    [self bringSubviewToFront:_backButton];
    
}


- (void)ac_initVideoSubviews:(CGRect)frame
{
    
    if ([TKEduSessionHandle shareInstance].localUser.role== TKUserType_Patrol || [TKEduSessionHandle shareInstance].localUser.role== TKUserType_Student || ([TKEduSessionHandle shareInstance].localUser.role==TKUserType_Playback)) {
        //播放按钮
        self.playButton = [[UIButton alloc] initWithFrame:CGRectMake(1, 0, 1, 1)];
        self.playButton.sakura.image(ThemePBKP(@"playBtnPauseImage"),UIControlStateSelected);
        self.playButton.sakura.image(ThemePBKP(@"playBtnPlayImage"),UIControlStateNormal);
        [self.playButton addTarget:self action:@selector(playOrPauseAction:) forControlEvents:UIControlEventTouchUpInside];
        self.playButton.selected = self.isPlay;
        
        return;
    }
    
    //返回按钮
    self.backButton = [UIButton buttonWithType:UIButtonTypeCustom];//[[UIButton alloc] initWithFrame:CGRectMake(frame.size.width - 50 - 6, 6, 50, 50)];
    self.backButton.sakura.image(ThemeKP(@"btn_closed_normal"),UIControlStateNormal);
    [self.backButton addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.backButton];

    //bottonBar
    self.bview = ({
        UIView *bview = [UIView new];
//        [[UIView alloc] initWithFrame:CGRectMake(0, ScreenH-tBottmViewWH+(tBottmViewWH-44)/2, ScreenW, 44)];
        bview.layer.masksToBounds = YES;
        bview.layer.cornerRadius = 22;
        bview.sakura.backgroundColor(ThemePBKP(@"sliderBackGoundColor"));
        bview.sakura.alpha(ThemePBKP(@"sliderAlpha"));
        bview.backgroundColor = [bview.backgroundColor colorWithAlphaComponent:bview.alpha];
        bview.alpha = 1;
        [self addSubview:bview];
        bview;
        
    });
    
//    self.bottmView = [UIView new];//[[UIView alloc] initWithFrame:CGRectMake(0, ScreenH-tBottmViewWH, ScreenW, tBottmViewWH)];
//    self.bottmView.backgroundColor = [UIColor clearColor];
//    [self addSubview:self.bottmView];

    // 播放按钮
    self.playButton = [UIButton buttonWithType:UIButtonTypeCustom];//[[UIButton alloc] initWithFrame:CGRectMake(tViewCap, 0, tBottmViewWH, tBottmViewWH)];
    self.playButton.sakura.image(ThemePBKP(@"playBtnPauseImage"),UIControlStateSelected);
    self.playButton.sakura.image(ThemePBKP(@"playBtnPlayImage"),UIControlStateNormal);
    self.playButton.selected = self.isPlay;;
    [self.playButton addTarget:self action:@selector(playOrPauseAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.bview addSubview:self.playButton];
    
    // 全屏按钮
    self.fullButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.fullButton.sakura.image(ThemePBKP(@"tk_btn_kejianquanping_default"),UIControlStateNormal);
    self.fullButton.sakura.image(ThemePBKP(@"tk_btn_tuichuquanping_select"),UIControlStateSelected);
    [self.fullButton addTarget:self action:@selector(fullButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.bview addSubview:self.fullButton];
    
    // 声道滑块
    self.iAudioslider = [TKProgressSlider new];//[[TKProgressSlider alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.bottmView.frame)-tAudiosliderWidth-tViewCap * 2,0,tAudiosliderWidth,tBottmViewWH)];
    self.iAudioslider.sakura.minimumTrackTintColor(ThemePBKP(@"sliderMinimumTrackTintColor"));
    self.iAudioslider.sakura.maximumTrackTintColor(ThemePBKP(@"sliderMaximumTrackTintColor"));
    self.iAudioslider.sakura.alpha(ThemePBKP(@"sliderAlpha"));
    self.iAudioslider.maximumTrackTintColor = [self.iAudioslider.maximumTrackTintColor colorWithAlphaComponent:self.iAudioslider.alpha];
    self.iAudioslider.alpha = 1;
    [self.iAudioslider setThumbImage:[TKTheme imageWithPath:ThemePBKP(@"sliderControlImage")] forState:UIControlStateNormal];
    
    self.iAudioslider.enabled = YES;
    self.iAudioslider.value = 1;
    [self.iAudioslider addTarget:self action:@selector(audioVolumChange:) forControlEvents:UIControlEventValueChanged];
    [self.bview addSubview:self.iAudioslider];
    
    // 声音按钮
    self.iAudioButton = [UIButton buttonWithType:UIButtonTypeCustom];//[[UIButton alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.iAudioslider.frame)-40,(self.bottmView.height-40)/2, 40, 40)];
    self.iAudioButton.sakura.image(ThemePBKP(@"voiceBtnNormalImage"),UIControlStateNormal);
    self.iAudioButton.sakura.image(ThemePBKP(@"voiceBtnSelectedImage"),UIControlStateSelected);
    self.iAudioButton.imageView.contentMode = UIViewContentModeCenter;
    [self.iAudioButton addTarget:self action:@selector(audioButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.bview addSubview:self.iAudioButton];

    // 进度滑块
    self.iProgressSlider = [TKProgressSlider new];//[[TKProgressSlider alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.playButton.frame) + tViewCap, 35, tProgressSliderW, 25)];
    self.iProgressSlider.continuous = NO;
    self.iProgressSlider.sakura.minimumTrackTintColor(ThemePBKP(@"sliderMinimumTrackTintColor"));
    self.iProgressSlider.sakura.maximumTrackTintColor(ThemePBKP(@"sliderMaximumTrackTintColor"));
    self.iProgressSlider.sakura.alpha(ThemePBKP(@"sliderAlpha"));
        self.iProgressSlider.maximumTrackTintColor = [self.iProgressSlider.maximumTrackTintColor colorWithAlphaComponent:self.iProgressSlider.alpha];
    self.iProgressSlider.alpha = 1;
    
    [self.iProgressSlider setThumbImage:[TKTheme imageWithPath:ThemePBKP(@"sliderControlImage")] forState:UIControlStateNormal];
    [self.iProgressSlider addTarget:self action:@selector(progressValueChange:) forControlEvents:UIControlEventValueChanged];
    [self.iProgressSlider addTarget:self action:@selector(progressTouchDown:) forControlEvents:UIControlEventTouchDown];
    [self.iProgressSlider addTarget:self action:@selector(progressTouchUp:) forControlEvents:UIControlEventTouchUpInside];
    self.sliderTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(progressTap:)];
    [self.iProgressSlider addGestureRecognizer:self.sliderTap];
    [self.bview addSubview:self.iProgressSlider];
    
    // 时间
    self.timeLabel = [UILabel new];//[[UILabel alloc] initWithFrame:CGRectMake(0, 15, 100, 25)];
    self.timeLabel.text = @"00:00/00:00";
    self.timeLabel.font = TKFont(12);
    self.timeLabel.textColor = [UIColor whiteColor];
    self.timeLabel.textAlignment = NSTextAlignmentRight;
    [self.bview addSubview:self.timeLabel];

    // 名称
//    CGSize size = CGSizeMake(1000,10000);
    //计算实际frame大小，并将label的frame变成实际大小
//    NSDictionary *attribute = @{NSFontAttributeName:self.timeLabel.font};
//    CGSize labelsize = [self.timeLabel.text boundingRectWithSize:size options: NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:attribute context:nil].size;
    self.titleLabel      = [UILabel new];//[[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.playButton.frame)+tViewCap, 15, tProgressSliderW- labelsize.width-10, 25)];
    self.titleLabel.text = self.filename;
    if (!self.filename.length) {
        self.iProgressSlider.centerY = self.playButton.centerY;
    }
    self.titleLabel.textColor = [UIColor whiteColor];
    self.titleLabel.textAlignment = NSTextAlignmentLeft;
    self.titleLabel.font = TKFont(12);
    self.titleLabel.textColor = [UIColor whiteColor];
    [self.bview addSubview:self.titleLabel];

    [self layout:frame];
}

#pragma mark - 布局
- (void)layout:(CGRect)frame
{
    CGFloat tViewCap			= 8;
//    CGFloat tBottmViewWH        = 70;
    CGFloat tAudiosliderWidth	= self.frame.size.width / 10;
    
    // 背景图
    [self.bview mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(0);
        make.width.mas_equalTo(frame.size.width);
        make.height.mas_equalTo(44);
        make.bottom.equalTo(self).offset(-14);
    }];
    
    // 关闭按钮
    [self.backButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self).offset(-6);
        make.top.mas_equalTo(6);
        make.width.height.mas_equalTo(36);
    }];

//    [self.bottmView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.left.mas_equalTo(0);
//        make.width.equalTo(self.bview);
//        make.height.mas_equalTo(tBottmViewWH);
//        make.bottom.equalTo(self);
//    }];
    // 播放按钮
    [self.playButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(tViewCap);
        make.top.centerY.equalTo(self.bview);
        make.width.left.mas_equalTo(44);
        
    }];
    
    // 全屏按钮
//    [self.fullButton mas_makeConstraints:^(MASConstraintMaker *make) {
//
//        make.right.equalTo(self.bottmView).offset(-tViewCap);
//        make.centerY.equalTo(self.bottmView);
//        make.size.mas_equalTo(22,22);
//    }];
    
    // 声道滑块
    [self.iAudioslider mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.right.equalTo(self.fullButton.mas_left).offset(-tViewCap);
        make.right.equalTo(self.bview).offset(-tViewCap*2);
        make.top.equalTo(self.bview);
        make.width.mas_equalTo(tAudiosliderWidth);
        make.height.equalTo(self.bview);
    }];
    // 声音按钮
    [self.iAudioButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.iAudioslider.mas_left);
        make.centerY.equalTo(self.iAudioslider);
        make.size.mas_equalTo(40,40);
    }];
    // 进度滑块
    [self.iProgressSlider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.playButton.mas_right);
        make.right.equalTo(self.iAudioButton.mas_left).offset(-tViewCap);
        make.height.mas_equalTo(25);
        make.bottom.equalTo(self.bview.mas_bottom);
    }];
    // 时间
    [self.timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.bview);
        make.size.mas_equalTo(CGSizeMake(100, 25));
        make.right.equalTo(self.iAudioButton.mas_left).offset(-tViewCap);
        
    }];
    // 名称
    [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.playButton.mas_right);
        make.right.equalTo(self.timeLabel.mas_left);
        make.top.equalTo(self.bview.mas_top);
        make.height.mas_equalTo(25);
    }];
}
#pragma mark - 响应事件
// 点击退出
- (void)backAction:(UIButton *)button {
    
    [TKEduSessionHandle shareInstance].isPlayMedia          = NO;
    [[TKEduSessionHandle shareInstance]sessionHandleUnpublishMedia:nil];
    
}
#pragma mark - 播放 & 暂停
- (void)audioPlayOrPauseAction:(UIButton *)sender{
    
    BOOL start = !sender.selected;
    [self playAction:start hasVideo:false];
}

- (void)playOrPauseAction:(UIButton *)sender {
    
    if ([TKEduClassRoom shareInstance].roomJson.configuration.videoWhiteboardFlag &&
        [TKEduSessionHandle shareInstance].isClassBegin) {
        
//        self.backButton.hidden = sender.selected;
    }
    [self playAction:!sender.selected hasVideo:true];
}
#pragma mark - 全屏
- (void)fullButtonAction:(UIButton *)btn
{
	
}
#pragma mark - 视频标注退出
- (void) videoFlagExit {

    self.backButton.hidden = NO;
}

- (void)playAction:(BOOL)start hasVideo:(BOOL)hasVideo{
    
    [TKMediaPlayingHelper sharedInstance].isPaused = hasVideo ? !start : NO;
    if ([TKMediaPlayingHelper sharedInstance].isPaused) {
        [TKMediaPlayingHelper sharedInstance].progress = self.current;
    }
    
    
    if (self.playButton.selected == start) {
        return;
    }
    if (start) {
        
    }else{
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher && self.hasVideo &&[TKEduSessionHandle shareInstance].isClassBegin) {
            
            NSString *str = [TKUtil dictionaryToJSONString:@{@"videoRatio":@(self.width*1.0/self.height)}];
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"VideoWhiteboard" ID:@"VideoWhiteboard" To:sTellAll Data:str Save:YES completion:nil];
        }
    }
    if (!hasVideo) {
        if (start) {
            [self.GIFImageView tkPlayGifAnim:[TKHelperUtil mp3PlayGif]];
        }else{
            [self.GIFImageView tkStopGifAnim];
        }
        [TKEduSessionHandle shareInstance].iIsPlaying = start;
    }
    [[TKEduSessionHandle shareInstance] sessionHandleMediaPause:!start];
    
    
    if ([TKEduClassRoom shareInstance].roomJson.configuration.videoWhiteboardFlag &&
        [TKEduSessionHandle shareInstance].isClassBegin) {
        
        [self refreshVideoWhiteBoard:start];
    }
    
}
- (void)refreshVideoWhiteBoard:(BOOL)start{
    if (start) {//加载白板
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sVideoWhiteboard ID:sVideoWhiteboard To:sTellAll Data:@{} completion:nil];
        }
    }
}


- (void)loadWhiteBoard{
    
    //MARK:显示视频标注页  点击暂停
    [self bringSubviewToFront:[TKEduSessionHandle shareInstance].mediaMarkView];
    [self bringSubviewToFront:self.backButton];
    [[TKEduSessionHandle shareInstance].mediaMarkView recoveryMediaMark];
}

- (void)hiddenVideoWhiteBoard{
    
    //MARK: 隐藏视频标注
    [TKEduSessionHandle shareInstance].mediaMarkView.hidden = YES;
    [[TKEduSessionHandle shareInstance].mediaMarkView clear];
}

- (void)deleteWhiteBoard{
    
    //MARK: 删除视频标注
    if ([TKEduSessionHandle shareInstance].mediaMarkView) {
        [[TKEduSessionHandle shareInstance].mediaMarkView removeFromSuperview];
        [TKEduSessionHandle shareInstance].mediaMarkView = nil;
    }
}

#pragma mark - 播放进度滑块
- (void)progressValueChange:(TKProgressSlider *)slider {
    
    self.isSliding = NO;
    _sliderTap.enabled = YES;
    NSTimeInterval pos = self.iProgressSlider.value * self.duration;
    [self seekProgressToPos:pos];
}
// 播放进度滑块
- (void)progressTouchDown:(TKProgressSlider *)slider {
    self.isSliding = YES;
    _sliderTap.enabled = NO;
}
- (void)progressTouchUp:(TKProgressSlider *)slider {
    self.isSliding = NO;
    _sliderTap.enabled = YES;
}
// 进度条点击
- (void)progressTap:(UITapGestureRecognizer *) tap {
    
    CGPoint touchPoint = [tap locationInView:_iProgressSlider];
    CGFloat value = (_iProgressSlider.maximumValue - _iProgressSlider.minimumValue) * (touchPoint.x / _iProgressSlider.frame.size.width );
    
    NSTimeInterval pos = value * self.duration;
    [self seekProgressToPos:pos];
}

-(void)seekProgressToPos:(NSTimeInterval)value{
    [[TKEduSessionHandle shareInstance] sessionHandleMediaSeektoPos:value];
}

// 声音开关
-(void)audioButtonClicked:(UIButton *)aButton{
    
    BOOL tBtnSlct                   = self.iAudioslider.value ?NO:YES;
    aButton.selected                = !tBtnSlct;
    CGFloat tVolume                 = aButton.selected ? 0 : 1;
    
    [TKEduSessionHandle shareInstance].iVolume  = tVolume;
    self.iAudioslider.value = tVolume;
    [[TKEduSessionHandle shareInstance] sessionHandleSetRemoteAudioVolume:tVolume peerId:_peerId type:(TKMediaSourceType_media)];
    
}


// 音量大小滑块
- (void)audioVolumChange:(TKProgressSlider *)slider {
    
    [self audioVolum:slider.value];
    
}

-(void)audioVolum:(CGFloat)volum{
    
    _iAudioButton.selected = (volum==0);
    [TKEduSessionHandle shareInstance].iVolume = volum;
    [[TKEduSessionHandle shareInstance] sessionHandleSetRemoteAudioVolume:volum peerId:_peerId type:(TKMediaSourceType_media)];
    self.iAudioslider.value = volum;
}

#pragma mark - 更新页面
-(void)updatePlayUI:(BOOL)start{
    
    if (self.playButton.selected == start && [TKEduSessionHandle shareInstance].localUser.role==TKUserType_Teacher) {
        return;
    }
    //学生 巡课 的时候
    if (([TKEduSessionHandle shareInstance].localUser.role==TKUserType_Student) ||([TKEduSessionHandle shareInstance].localUser.role==TKUserType_Patrol)) {
        if (start ) {
            
            [self.GIFImageView tkPlayGifAnim:[TKHelperUtil mp3PlayGif]];
        }else{
            [self.GIFImageView tkStopGifAnim];
            
        }
        
        
    }
    self.playButton.selected = start;
    self.iIsPlay = start;
    self.isPlay = start;

    [self bringSubviewToFront:self.backButton];
}
- (void)update:(NSTimeInterval)current total:(NSTimeInterval)total isPlay:(BOOL)isPlay
{
    self.current = current;
    //如果用户在手动滑动滑块，则不对滑块的进度进行设置重绘
    if ((current == 0 && self.hasVideo == NO) || self.isSliding == YES) {
        return;
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role== TKUserType_Patrol || [TKEduSessionHandle shareInstance].localUser.role== TKUserType_Student || ([TKEduSessionHandle shareInstance].localUser.role==TKUserType_Playback)) {
        return;
    }
    
    self.timeLabel.text = [NSString stringWithFormat:@"%@/%@", [self formatPlayTime:current/1000], isnan(total)?@"00:00":[self formatPlayTime:total/1000]];

    if (isPlay) {
        // 播放更新
        self.iProgressSlider.value = current/total;
        
    } else {
        // 暂停 和 结束（暂停时 当前进度会返回0）
        if (current == total) {
            // 结束
            self.iProgressSlider.value = current/total;
            [[TKEduSessionHandle shareInstance] sessionHandleMediaSeektoPos:0];
        }
    }
}

- (NSString *)formatPlayTime:(NSTimeInterval)duration {
    int minute = 0, hour = 0, secend = duration;
    minute = (secend % 3600)/60;
    hour = secend / 3600;
    secend = secend % 60;
    //    return [NSString stringWithFormat:@"%02d:%02d", minute, secend];
    if (hour > 0) {
        return [NSString stringWithFormat:@"%02d:%02d:%02d",hour, minute, secend];
    }
    return [NSString stringWithFormat:@"%02d:%02d", minute, secend];
}

- (void)hiddenLoadingView
{
    if (_loadingView) {
        [_loadingView tkStopGifAnim];
        
        [_loadBackgroundView removeFromSuperview];
        [_loadingView removeFromSuperview];
        _loadingView = nil;
        
        _loadBackgroundView  = nil;
    }
}
- (void)setVideoViewToBack
{
    if (self.hasVideo) {
        if (self.bview) {
            [self bringSubviewToFront:self.bview];
        }
        if (self.backButton) {
            [self bringSubviewToFront:self.backButton];
        }
//        if (self.bottmView) {
//            [self bringSubviewToFront:self.bottmView];
//        }
    } else {
        if (self.backButton) {
            [self bringSubviewToFront:self.backButton];
        }
        if (self.playButton) {
            [self bringSubviewToFront:self.playButton];
        }
        if (self.titleLabel) {
            [self bringSubviewToFront:self.titleLabel];
        }
        if (self.timeLabel) {
            [self bringSubviewToFront:self.timeLabel];
        }
        if (self.iProgressSlider) {
            [self bringSubviewToFront:self.iProgressSlider];
        }
        if (self.iAudioButton) {
            [self bringSubviewToFront:self.iAudioButton];
        }
        if (self.iAudioslider) {
            [self bringSubviewToFront:self.iAudioslider];
        }
        
    }
}

@end

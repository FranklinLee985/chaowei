//
//  TKPlaybackMaskView.m
//  EduClassPad
//
//  Created by MAC-MiNi on 2017/9/11.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKPlaybackMaskView.h"
#import "TKEduSessionHandle.h"
#import "TKProgressSlider.h"

#define ThemeKP(args) [@"ClassRoom.PlayBack." stringByAppendingString:args]

@interface TKPlaybackMaskView () <UIGestureRecognizerDelegate>

@property (nonatomic, assign) BOOL isSlidering;

@property (nonatomic, assign) NSTimeInterval seekInterval;
@property (nonatomic, assign) NSTimeInterval acceptSeekTime;
@property (nonatomic, assign) NSTimeInterval duration; //当前时间
@property (nonatomic, assign) NSTimeInterval lastTime; //结束时间

@property (nonatomic, strong) UIView *bottmView; //工具条
@property (nonatomic, strong) UIView *toolView;

@property (nonatomic, strong) UIActivityIndicatorView *activity;

@property (nonatomic, strong) UILabel *timeLabel; //时间

@end

@implementation TKPlaybackMaskView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.isSlidering    = NO;
        self.seekInterval   = 2;
        self.acceptSeekTime = 0;
        [self setupViews:frame];
    }
    return self;
}

- (void)setupViews:(CGRect)frame {

    CGFloat tBottmViewWH = 60;
    CGFloat tViewCap     = 8;

    //控制条背景图
    self.toolView = ({
        UIView *view =
            [[UIView alloc] initWithFrame:CGRectMake(10, frame.size.height - tBottmViewWH - 10,
                                                     frame.size.width - 20, tBottmViewWH)];
        view.sakura.backgroundColor(ThemeKP(@"toolBackgroundColor"));
        view.sakura.alpha(ThemeKP(@"toolBackgroundAlpha"));
        view.layer.masksToBounds = YES;
        view.layer.cornerRadius  = tBottmViewWH / 2.0;
        [self addSubview:view];
        view;
    });

    // bottonBar
    self.bottmView =
        [[UIView alloc] initWithFrame:CGRectMake(0, frame.size.height - tBottmViewWH - 10,
                                                 self.size.width, tBottmViewWH)];
    self.bottmView.backgroundColor = [UIColor clearColor];

    [self addSubview:self.bottmView];

    // 播放按钮
    self.playButton =
        [[UIButton alloc] initWithFrame:CGRectMake(10, 0, tBottmViewWH, tBottmViewWH)];
    self.playButton.sakura.image(ThemeKP(@"playBtnPauseImage"), UIControlStateSelected);
    self.playButton.sakura.image(ThemeKP(@"playBtnPlayImage"), UIControlStateNormal);
    [self.playButton addTarget:self
                        action:@selector(playOrPauseAction:)
              forControlEvents:UIControlEventTouchUpInside];
    [self.bottmView addSubview:self.playButton];

    // 名称
    CGFloat tProgressSliderW = CGRectGetWidth(self.bottmView.frame) -
                               CGRectGetMaxX(self.playButton.frame) - tViewCap - 180;

    // 进度滑块
    self.iProgressSlider =
        [[TKProgressSlider alloc] initWithFrame:CGRectMake(self.playButton.rightX + tViewCap, 0,
                                                           tProgressSliderW, tBottmViewWH)];
    self.iProgressSlider.sakura.minimumTrackTintColor(ThemeKP(@"sliderMinimumTrackTintColor"));
    self.iProgressSlider.sakura.maximumTrackTintColor(ThemeKP(@"sliderMaximumTrackTintColor"));
    [self.iProgressSlider setThumbImage:[TKTheme imageWithPath:ThemeKP(@"sliderControlImage")]
                               forState:UIControlStateNormal];
//    self.iProgressSlider.continuous = NO;

    // 滑动
    [self.iProgressSlider addTarget:self
                             action:@selector(progressValueChange:)
                   forControlEvents:UIControlEventValueChanged];
    [self.iProgressSlider addTarget:self
                             action:@selector(progressValueEnd:)
                   forControlEvents:UIControlEventTouchUpInside];
    // 点击
    UITapGestureRecognizer *tapProgressViewGesture =
        [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapProgressView:)];
    tapProgressViewGesture.delegate = self;
    [self addGestureRecognizer:tapProgressViewGesture];
    [self.bottmView addSubview:self.iProgressSlider];

    // 时间
    self.timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.iProgressSlider.rightX + 5,
                                                               (tBottmViewWH - 25) / 2.0, 150, 25)];
    self.timeLabel.text          = @"00:00/00:00";
    self.timeLabel.textColor     = [UIColor whiteColor];
    self.timeLabel.textAlignment = NSTextAlignmentCenter;
    [self.bottmView addSubview:self.timeLabel];

    //菊花
    self.activity = [[UIActivityIndicatorView alloc]
        initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [self.activity setCenter:self.center]; //指定进度轮中心点
    [self.activity
        setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleWhiteLarge]; //设置进度轮显示类型
    self.activity.hidesWhenStopped = YES;
    [self addSubview:self.activity];
}

- (void)playOrPauseAction:(UIButton *)sender {
    if (sender.selected == NO) {
        [[TKEduSessionHandle shareInstance] playback];
        sender.selected = YES;
    } else {
        [[TKEduSessionHandle shareInstance] pausePlayback];
        sender.selected = NO;
    }
    //在播放录制件的过程中，如果有答题卡（会涉及到本地定时器），点击暂停应关闭计时器
    // object：YES 表示暂停 NO表示播放中
    [[NSNotificationCenter defaultCenter]
        postNotificationName:@"TimerStateDuringPlaybackNotification"
                      object:@(!sender.selected)];
}
//- (void)setSlideringState {
//    self.isSlidering = YES;
//}
#pragma mark - 播放 控制
// 手势
- (void)tapProgressView:(UIGestureRecognizer *)gesture {

    // 隐藏状态 直接显示
    if (self.bottmView.hidden) {
        [self showTool];
        return;
    }

    CGPoint point = [gesture locationInView:self.iProgressSlider];
    // 点击滑块
    if ([self.iProgressSlider pointInside:point withEvent:nil]) {

        float value = (self.iProgressSlider.maximumValue - self.iProgressSlider.minimumValue) *
                      (point.x / self.iProgressSlider.width);

        [self.iProgressSlider setValue:value];
        [self progressValueEnd:self.iProgressSlider];

    } else {
        [self showTool];
    }
}
// 播放进度滑块
- (void)progressValueChange:(TKProgressSlider *)slider {

    if (self.iProgressSlider.value < 0) { self.iProgressSlider.value = 0; }
    if (self.iProgressSlider.value > 1) { self.iProgressSlider.value = 1; }
    
    self.isSlidering = YES;
    NSTimeInterval pos = floor(self.iProgressSlider.value * self.duration);

    self.timeLabel.text = [NSString
                           stringWithFormat:@"%@/%@", [self formatPlayTime:pos / 1000],
                           isnan(self.duration) ? @"00:00"
                           : [self formatPlayTime:self.duration / 1000]];
}

- (void)progressValueEnd:(TKProgressSlider *)slider {
    
    if (self.iProgressSlider.value < 0) { self.iProgressSlider.value = 0; }
    if (self.iProgressSlider.value > 1) { self.iProgressSlider.value = 1; }
    
    if (NSDate.date.timeIntervalSince1970 - self.acceptSeekTime < self.seekInterval) {
        self.isSlidering = NO;
        return;
    }
    
    NSTimeInterval pos = floor(self.iProgressSlider.value * self.duration);
    self.acceptSeekTime = NSDate.date.timeIntervalSince1970;
    [[TKEduSessionHandle shareInstance] seekPlayback:pos];
    if (!self.playButton.selected) {
        
        [[TKEduSessionHandle shareInstance] playback];
        self.playButton.selected = YES;
    }
    self.isSlidering = NO;
}

- (void)update:(NSTimeInterval)current {
    if (self.playButton.selected == NO || self.isSlidering)
        { return; }

    self.iProgressSlider.value = current / self.duration;

    if (current != self.lastTime) {
        [self.activity stopAnimating];
        self.timeLabel.text = [NSString
            stringWithFormat:@"%@/%@", [self formatPlayTime:current / 1000],
                             isnan(self.duration) ? @"00:00"
                                                  : [self formatPlayTime:self.duration / 1000]];

    } else {

        if (current < self.duration) { [self.activity startAnimating]; }
    }

    self.lastTime      = current;
}

- (NSString *)formatPlayTime:(NSTimeInterval)duration {
    int minute = 0, hour = 0, secend = duration;
    minute = (secend % 3600) / 60;
    hour   = secend / 3600;
    secend = secend % 60;

    if (hour > 0) { return [NSString stringWithFormat:@"%02d:%02d:%02d", hour, minute, secend]; }
    return [NSString stringWithFormat:@"%02d:%02d", minute, secend];
}

- (void)playbackEnd {

    self.playButton.selected   = NO;
    self.iProgressSlider.value = 0;
    [[TKEduSessionHandle shareInstance] seekPlayback:0];
    [[TKEduSessionHandle shareInstance] pausePlayback];
    self.timeLabel.text =
        [NSString stringWithFormat:@"%@/%@", @"00:00", [self formatPlayTime:self.duration / 1000]];
}

- (void)getPlayDuration:(NSTimeInterval)duration {

    if (self.playButton.selected == YES) {

        if (self.iProgressSlider.value > 0) { self.duration = duration; }

        // 播放状态断线重连，进行seek
        NSTimeInterval pos = self.iProgressSlider.value * self.duration;
        [[TKEduSessionHandle shareInstance] seekPlayback:pos];
    } else {
        if (self.iProgressSlider.value > 0.001) {
            // 暂停状态断线重连，进行seek，然后再pause
            NSTimeInterval pos = self.iProgressSlider.value * self.duration;
            [[TKEduSessionHandle shareInstance] seekPlayback:pos];
            [[TKEduSessionHandle shareInstance] pausePlayback];
        } else {
            // 正常状态
            self.duration            = duration;
            self.playButton.selected = YES;
        }
    }
}

- (void)showTool {
    self.bottmView.hidden = !self.bottmView.hidden;
    self.toolView.hidden  = !self.toolView.hidden;
}

@end

//
//  TKCTVideoSmallView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/12.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTVideoSmallView.h"

#import "TKEduSessionHandle.h"
#import "TKVideoPopupMenu.h"
#import "TKManyMaskView.h"
#import "TKOneMaskView.h"
#import "TKTrophyView.h"//自定义奖杯
#import "FLAnimatedImageView.h"
#import "FLAnimatedImage.h"
#import "FLAnimatedImageView+WebCache.h"


#define ThemeKP(args) [@"ClassRoom.TKVideoView." stringByAppendingString:args]

static const CGFloat sStudentVideoViewHeigh     = 112;
static const CGFloat sStudentVideoViewWidth     = 120;



@interface TKCTVideoSmallView ()<CAAnimationDelegate,TKVideoPopupMenuDelegate>
{
    NSString *_giftWav;//记录声音地址
}

@property (nonatomic, strong) TKManyMaskView *manyMaskView;// 一对多 上层背景贴图
@property (nonatomic, strong) TKOneMaskView  *oneMaskView;//一对一 上层背景贴图

@property (nonatomic, strong) TKVideoPopupMenu *videoPopupMenu;

@property (nonatomic, assign) NSInteger iGiftCount;

//@property (nonatomic, strong) FLAnimatedImageView * gifView;
@property (nonatomic, strong) TKTrophyView *trophyView;
@property (nonatomic, strong) TKEduSessionHandle *iEduClassRoomSessionHandle;
@end


@implementation TKCTVideoSmallView

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    
    if ([self pointInside:point withEvent:event]) {
        //点击了SmallView
        
    }
    return [super hitTest:point withEvent:event];
}

-(void)removeAllObserver {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setVideoMode:(TKVideoViewMode)videoMode
{
    _videoMode = videoMode;
    if (_manyMaskView) _manyMaskView.videoMode = videoMode;
    [self setNeedsLayout];
}

- (void)hideMaskView:(BOOL)state
{
    if (_manyMaskView) self.manyMaskView.hidden = state;
    if (_oneMaskView) self.oneMaskView.hidden = state;
}

- (void)setMaskLayout:(TKMaskViewLayout)maskLayout
{
    _maskLayout = maskLayout;
    _manyMaskView.maskLayout = maskLayout;
}

-(instancetype)initWithFrame:(CGRect)frame
{
    
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor         = [UIColor clearColor];
        self.hidden 				 = NO;
        _iEduClassRoomSessionHandle  = [TKEduSessionHandle shareInstance];
        _originalWidth               = frame.size.width;
        _originalHeight              = frame.size.height;
        _iPeerId                     = @"";
        _isDrag                      = NO;
        _isSplit                     = NO;
        _isPicInPic                  = NO;
        _isNeedFunctionButton        = ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher);
        
        _videoView = [[UIView alloc] initWithFrame:self.bounds];
        _videoView.backgroundColor = [UIColor blackColor];
        [self addSubview:_videoView];
        
        if([TKEduClassRoom shareInstance].roomJson.roomtype ==TKRoomTypeOneToOne && [TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish == NO)
        {
            _oneMaskView = [[TKOneMaskView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
            [self addSubview:_oneMaskView];
        }
        else
        {
            _manyMaskView = [[TKManyMaskView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
            [self addSubview:_manyMaskView];
        }
        
        _iFunctionButton = ({
            UIButton *tButton = [UIButton buttonWithType:UIButtonTypeCustom];
            tButton.frame = CGRectMake(0, 0, CGRectGetWidth(frame), CGRectGetHeight(frame));
            [tButton addTarget:self action:@selector(functionButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
            tButton.backgroundColor = [UIColor clearColor];
            tButton;
        });
        [self addSubview:_iFunctionButton];
        
    }
    return self;
}

-(void)layoutSubviews{
    
    [self bringSubviewToFront:_iFunctionButton];
    
    if (_manyMaskView) _manyMaskView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    if (_oneMaskView)  _oneMaskView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    
    if (self.videoMode == TKVideoViewMode_Top) {
        self.videoView.frame = CGRectMake(0, 0, CGRectGetWidth(self.frame), 3.0/4 * CGRectGetWidth(self.frame));
        
    }else if(self.videoMode == TKVideoViewMode_Fit){
        
        if (CGRectGetWidth(self.frame) / CGRectGetHeight(self.frame) > 4.0 / 3) {
            CGFloat height = CGRectGetHeight(self.frame);
            CGFloat width  = 4.0/3 * height;

            self.videoView.frame = CGRectMake((CGRectGetWidth(self.frame) - width)/2, 0, width,height);
            
        }else if (CGRectGetWidth(self.frame) / CGRectGetHeight(self.frame) < 4.0 / 3){
            CGFloat width  = CGRectGetWidth(self.frame);
            CGFloat height = 3.0/4 * width;
            
            self.videoView.frame = CGRectMake(0, (CGRectGetHeight(self.frame) - height)/2, width,height);
        }else{
            self.videoView.frame = self.bounds;
        }
        if (_manyMaskView) _manyMaskView.frame = self.videoView.frame;

    }else{
        self.videoView.frame = self.bounds;
    }

    
    // 分屏下布局
    CGFloat videoSmallWidth  = CGRectGetWidth(self.frame);
    CGFloat videoSmallHeight = CGRectGetHeight(self.frame);
    
    self.currentWidth = videoSmallWidth;
    self.currentHeight = videoSmallHeight;
    
    
    _iFunctionButton.frame = CGRectMake(0, 0, videoSmallWidth, videoSmallHeight);
}

-(void)setIsNeedFunctionButton:(BOOL)isNeedFunctionButton{
    _iFunctionButton.enabled = isNeedFunctionButton;
}
-(void)setIRoomUser:(TKRoomUser *)iRoomUser{
    
    if (_manyMaskView) _manyMaskView.iRoomUser = iRoomUser;
    if (_oneMaskView)  _oneMaskView.iRoomUser = iRoomUser;

    if (iRoomUser && _iRoomUser == nil) {
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshRaiseHandUI:) name:[NSString stringWithFormat:@"%@%@",sRaisehand,iRoomUser.peerID] object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshVolume:) name:[NSString stringWithFormat:@"%@%@",sVolume,iRoomUser.peerID] object:nil];
        
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(inBackground:) name:[NSString stringWithFormat:@"%@%@",sIsInBackGround,iRoomUser.peerID] object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(inOnlyAudioRoom:) name:sOnlyAudioRoom object:nil];
    } else if (iRoomUser == nil) {
        
        //删除前一个
        [[NSNotificationCenter defaultCenter]removeObserver:self name:[NSString stringWithFormat:@"%@%@",sRaisehand,_iRoomUser.peerID] object:nil];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:[NSString stringWithFormat:@"%@%@",sVolume,_iRoomUser.peerID] object:nil];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:[NSString stringWithFormat:@"%@%@",sIsInBackGround,_iRoomUser.peerID] object:nil];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:sOnlyAudioRoom object:nil];
    }
    
    // 学生自己可以在自己的SmallView上弹出操作视图
    if ([iRoomUser.peerID isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID] && iRoomUser.role == TKUserType_Student) {
        _iFunctionButton.enabled = YES;
    }
    
    _iRoomUser = iRoomUser;
    int currentGift = 0;
    if(iRoomUser && iRoomUser.properties && [iRoomUser.properties objectForKey:sGiftNumber])
    {
        
        currentGift = [[_iRoomUser.properties objectForKey:sGiftNumber] intValue];
        
    }
    
    // 助教视频不显示奖杯
    if (iRoomUser.role == TKUserType_Assistant) {
        
    }
    //todo
    
    // 根据用户disableAudio和disableVideo去设置图片
    
    if (_manyMaskView) [self bringSubviewToFront:_manyMaskView];
    if (_oneMaskView)  [self bringSubviewToFront:_oneMaskView];

}
- (void)inBackground:(NSNotification *)aNotification{
    
    if (_iRoomUser && NO == [_iRoomUser.peerID isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID]) {
        BOOL isInBackground =[aNotification.userInfo[sIsInBackGround] boolValue];
        [self endInBackGround:isInBackground];
    }
}
- (void)endInBackGround:(BOOL)isInBackground{
    
    if (_manyMaskView) [self.manyMaskView endInBackGround:isInBackground];
    if (_oneMaskView)  [self.oneMaskView endInBackGround:isInBackground];
}
- (void) inOnlyAudioRoom:(NSNotification *) aNotification {
    if (_manyMaskView) [self.manyMaskView inOnlyAudioRoom];
    if (_oneMaskView)  [self.oneMaskView inOnlyAudioRoom];
}

#pragma mark - 状态改变 接收到的通知
-(void)refreshRaiseHandUI:(NSNotification *)aNotification{

    //打开视频关闭视频开关
    NSDictionary *tDic = (NSDictionary *)aNotification.object;
    if (_manyMaskView) [self.manyMaskView refreshRaiseHandUI:tDic];
    if (_oneMaskView)  [self.oneMaskView refreshRaiseHandUI:tDic];
    
    // 发送礼物
    if ([[tDic objectForKey:sGiftNumber] integerValue] > 0 &&
        [[tDic objectForKey:@"publishstate"] integerValue] > 0)
    {
        // 非老师 发送对象是当前
        TKRoomUser *user = tDic[@"User"];
        if (_iRoomUser.role != TKUserType_Teacher && [user.peerID isEqualToString:_iRoomUser.peerID]) {
            
            NSDictionary *giftInfo = [NSDictionary dictionaryWithDictionary:[TKUtil getDictionaryFromDic:_iRoomUser.properties Key:sGiftinfo]];

            if (self.superview) {
                
                if ([TKEduSessionHandle shareInstance].isPlayback) {
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [self potStartAnimationForView:self giftinfo:giftInfo];
                    });
                }
                else {
                    [self potStartAnimationForView:self giftinfo:giftInfo];
                }
            }
        }
    }
}

#pragma mark Action
#pragma mark - 动画
//view 视频窗口  giftinfo 奖杯信息
- (void)potStartAnimationForView:(UIView *)view giftinfo:(NSDictionary *)giftinfo
{
    NSString *wavpathDir = [[NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject] stringByAppendingPathComponent:TrophyAudioPath([TKEduClassRoom shareInstance].roomJson.companyid, giftinfo[@"trophyname"])];
    
    NSString *imgpathDir = [[NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject] stringByAppendingPathComponent:TrophyImgPath([TKEduClassRoom shareInstance].roomJson.companyid, giftinfo[@"trophyname"])];
    
    
    CGRect frame = [self.superview convertRect:self.frame toView:[UIApplication sharedApplication].keyWindow];
    __weak __typeof (self) weakSelf = self;
 
    FLAnimatedImageView *gifView = [self gifView];
    [[UIApplication sharedApplication].keyWindow addSubview:gifView];
    
    //如果设置了自定义奖杯，需要显示自定义奖杯画面
    BOOL customTrophyFlag = [TKEduClassRoom shareInstance].roomJson.configuration.customTrophyFlag;
    if (customTrophyFlag && giftinfo.count>0) {
        
        NSData * imgData = [NSData dataWithContentsOfFile:imgpathDir];
        _giftWav = wavpathDir;
        
        // 缓存了自定义图片
        if (imgData) {
            
            FLAnimatedImage *animatedImage = [FLAnimatedImage animatedImageWithGIFData:imgData];
            if (animatedImage) {
                //动态图片
                gifView.animatedImage = animatedImage;
            }else{
                //静态图片
                gifView.image = [UIImage imageWithContentsOfFile:imgpathDir];
            }
            
            CGPoint point = CGPointMake(frame.origin.x + frame.size.width / 2, frame.origin.y + frame.size.height / 2);
            [self transformForView:gifView
                      fromOldPoint:gifView.layer.position
                        toNewPoint:point];
            
        }else {
            
            NSString *trophyimg = [TKUtil optString:giftinfo Key:@"trophyimg"];
            NSString *imageName = [NSString stringWithFormat:@"%@://%@:%@/%@",sHttp,sHost,sPort,trophyimg];
            
            [gifView sd_setImageWithURL:[NSURL URLWithString:imageName] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    
                    CGPoint point = CGPointMake(frame.origin.x + frame.size.width / 2, frame.origin.y + frame.size.height / 2);
                    
                    [weakSelf transformForView:gifView
                                  fromOldPoint:gifView.layer.position
                                    toNewPoint:point];
                });
                
            }];
        }
        
    }
    else {//未设置自定义奖杯使用默认设置的奖杯
        
        _giftWav = LOADWAV(@"trophy_tones.wav");
        
        dispatch_async(dispatch_get_main_queue(), ^{
            
            gifView.image = [UIImage imageNamed:@"tk_icon_gift"];
            [self transformForView:gifView
                      fromOldPoint:gifView.layer.position
                        toNewPoint:CGPointMake(frame.origin.x + frame.size.width / 2, frame.origin.y + frame.size.height / 2)];
        });
        
    }
}
#pragma mark - 声音检测
- (void)refreshVolume:(NSNotification *)aNotification{
    //打开音频关闭音频开关
    NSDictionary *tDic = (NSDictionary *)aNotification.object;
    if (_manyMaskView) [self.manyMaskView refreshVolume:tDic];
    if (_oneMaskView)  [self.oneMaskView refreshVolume:tDic];
}
#pragma mark - 视频触摸事件
-(void)functionButtonClicked:(UIButton *)aButton{
    // 空判断
    if (!_iPeerId || [_iPeerId isEqualToString:@""]) {
        return;
    }
    //如果是文档全屏状态不显示控制视图
    if ([TKEduSessionHandle shareInstance].iIsFullState) {
        return;
    }
    // 未发布流
    if ([[TKEduSessionHandle shareInstance] localUser].publishState == TKPublishStateNONE ||
        [[TKEduSessionHandle shareInstance] localUser].publishState == TKPublishStateLocalNONE) {
        return;
    }
    //  是否允许学生关闭音视频
    if ([TKEduClassRoom shareInstance].roomJson.configuration.allowStudentCloseAV == NO &&
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        return;
    }
    // 老师未上课 点击自己
    if (![TKEduSessionHandle shareInstance].isClassBegin &&
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher &&
        ![_iPeerId isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID]) {
        return;
    }
    // 非老师 点击他人视频
    if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Teacher &&
        ![_iPeerId isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID]) {
        return;
    }
    
    // 隐藏 与 显示
    if ([TKEduSessionHandle shareInstance].dismissing) {
        return;
    }

    _videoPopupMenu = [TKVideoPopupMenu showRelyOnView:aButton
                                              aRoomUer:_iRoomUser
                                               isSplit:(self.isSplit || self.isDrag)
                                             isSpeaker:_isSpeaker
                                              delegate:self];
}

- (void)hidePopMenu{
    [_videoPopupMenu dismiss];
}


- (void)setIsSplit:(BOOL)isSplit{
    _isSplit = isSplit;
    if (_manyMaskView) _manyMaskView.isSplit = isSplit;
    if (_oneMaskView) _oneMaskView.isSplit = isSplit;
}

-(void)changeName:(NSString *)aName{
    
    if (!aName||aName.length==0) {
        return;
    }
    [self bringSubviewToFront:_iFunctionButton];
    
    if (_manyMaskView) {
        [_manyMaskView changeName:aName];
    }
    if (_oneMaskView) {
        [_oneMaskView changeName:aName];
    }
}

-(void)clearVideoData{
    
    _iPeerId	= @"";
    _isDrag		= NO;
    _isSplit	= NO;
    _isPicInPic = NO;
    _isSpeaker	= NO;
    self.iRoomUser = nil;
    [_videoPopupMenu dismiss];
    
    [self changeName:@""];
}

#pragma mark - 改变尺寸 相关方法
- (BOOL)isContains
{
    
    return CGRectContainsRect(_whiteBoardViewFrame, self.frame);
}


// 接收到调整大小的信令
- (void)changeVideoSize:(CGFloat)scale inFrame:(CGRect)rect
{
    CGFloat width = self.originalWidth * scale;
    if (width < self.originalWidth)
    {
        // 无法缩小至比初始化大小还小
        return;
    }
    
    self.frame = [self changeFrameWith:scale inFrame:rect];
    
//    [self setNeedsLayout];

}

- (CGRect)resizeVideoViewInFrame
{
    
    CGRect 	wbRect 		= _whiteBoardViewFrame;
    CGRect 	videoRect 	= self.frame;
    CGFloat height 		= 0;
    CGFloat width 		= 0;
    
    // 如果横边和竖边都相交
    if ((videoRect.origin.x + videoRect.size.width > wbRect.origin.x + wbRect.size.width || videoRect.origin.x < wbRect.origin.x) &&
        (videoRect.origin.y + videoRect.size.height > wbRect.origin.y + wbRect.size.height || videoRect.origin.y < wbRect.origin.y))
    {
        width = (self.center.x - wbRect.origin.x) <= (wbRect.origin.x + wbRect.size.width - self.center.x) ? (self.center.x - wbRect.origin.x) * 2 : (wbRect.origin.x + wbRect.size.width - self.center.x) * 2;
        height = (self.center.y - wbRect.origin.y) <= (wbRect.origin.y + wbRect.size.height - self.center.y) ? (self.center.y - wbRect.origin.y) * 2 : (wbRect.origin.y + wbRect.size.height - self.center.y) * 2;
        
        if (width <= height * sStudentVideoViewWidth / sStudentVideoViewHeigh)
        {
            height = width * sStudentVideoViewHeigh / sStudentVideoViewWidth;
            
            return CGRectMake(self.center.x - width / 2.0, self.center.y - height / 2.0, width, height);
        }
        
        if (height <= width * sStudentVideoViewHeigh / sStudentVideoViewWidth) {
            width = height * sStudentVideoViewWidth / sStudentVideoViewHeigh;
            
            return CGRectMake(self.center.x - width / 2.0, self.center.y - height / 2.0, width, height);
        }
        
        return CGRectMake(self.center.x - width / 2.0, self.center.y - height / 2.0, width, height);
    }
    
    // 如果是竖边界相交
    if (videoRect.origin.x + videoRect.size.width > wbRect.origin.x + wbRect.size.width ||
        videoRect.origin.x < wbRect.origin.x)
    {
        width = (self.center.x - wbRect.origin.x) <= (wbRect.origin.x + wbRect.size.width - self.center.x) ? (self.center.x - wbRect.origin.x) * 2 : (wbRect.origin.x + wbRect.size.width - self.center.x) * 2;
        height = width * sStudentVideoViewHeigh / sStudentVideoViewWidth;
        
        return CGRectMake(self.center.x - width / 2.0, self.center.y - height / 2.0, width, height);
    }
    
    // 如果是横边相交
    if (videoRect.origin.y + videoRect.size.height > wbRect.origin.y + wbRect.size.height ||
        videoRect.origin.y < wbRect.origin.y) {
        height = (self.center.y - wbRect.origin.y) <= (wbRect.origin.y + wbRect.size.height - self.center.y) ? (self.center.y - wbRect.origin.y) * 2 : (wbRect.origin.y + wbRect.size.height - self.center.y) * 2;
        width = height * sStudentVideoViewWidth / sStudentVideoViewHeigh;
        
        return CGRectMake(self.center.x - width / 2.0, self.center.y - height / 2.0, width, height);
    }
    
    return CGRectMake(self.center.x - width / 2.0, self.center.y - height / 2.0, width, height);
}

- (CGRect)changeFrameWith:(CGFloat)scale inFrame:(CGRect)rect {
    
    CGRect wbRect         = rect;
    CGRect videoRect      = self.frame;
    CGFloat height        = self.originalHeight * scale;
    CGFloat width         = self.originalWidth *scale;
    CGPoint oldCenter     = self.center;
    
    
    // top 1, right 2, bottom 3, left 4
    NSInteger vcrossEdge = 0;
    NSInteger hcrossEdge = 0;
    if (videoRect.origin.x <= wbRect.origin.x) {
        // 垂直边左相交
        vcrossEdge = 4;
    }
    if (videoRect.origin.x + videoRect.size.width >= wbRect.origin.x + wbRect.size.width) {
        // 垂直边右相交
        vcrossEdge = 2;
    }
    if (videoRect.origin.y <= wbRect.origin.y) {
        // 水平便顶相交
        hcrossEdge = 1;
    }
    if (videoRect.origin.y + videoRect.size.height >= wbRect.origin.y + wbRect.size.height) {
        // 水平便底相交
        hcrossEdge = 3;
    }
    
    if (vcrossEdge == 0 && hcrossEdge == 0) {
        CGRectMake(oldCenter.x - width/2.0, oldCenter.y - height/2.0, width, height);
    }
    
    if (vcrossEdge == 0 && hcrossEdge == 1) {
        CGFloat x = oldCenter.x - width / 2.0;
        CGFloat y = wbRect.origin.y;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 0 && hcrossEdge == 3) {
        CGFloat x = oldCenter.x - width / 2.0;
        CGFloat y = wbRect.origin.y + wbRect.size.height - height;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 2 && hcrossEdge == 0) {
        CGFloat x = wbRect.origin.x + wbRect.size.width - width;
        CGFloat y = oldCenter.y - height / 2.0;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 4 && hcrossEdge == 0) {
        CGFloat x = wbRect.origin.x;
        CGFloat y = oldCenter.y - height / 2.0;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 4 && hcrossEdge == 1) {
        CGFloat x = wbRect.origin.x;
        CGFloat y = wbRect.origin.y;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 4 && hcrossEdge == 3) {
        CGFloat x = wbRect.origin.x;
        CGFloat y = wbRect.origin.y + wbRect.size.height - height;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 2 && hcrossEdge == 1) {
        CGFloat x = wbRect.origin.x + wbRect.size.width - width;
        CGFloat y = wbRect.origin.y;
        return CGRectMake(x, y, width, height);
    }
    
    if (vcrossEdge == 2 && hcrossEdge == 3) {
        CGFloat x = wbRect.origin.x + wbRect.size.width - width;
        CGFloat y = wbRect.origin.y + wbRect.size.height - height;
        return CGRectMake(x, y, width, height);
    }
    
    return CGRectMake(oldCenter.x - width/2.0, oldCenter.y - height/2.0, width, height);
}

- (void)transformForView:(UIImageView *)d fromOldPoint:(CGPoint)oldPoint toNewPoint:(CGPoint)newPoint
{
    NSTimeInterval imageDuration = d.image.duration;
    if (imageDuration < 0.6) {
        imageDuration = 0.6;
    }
//    NSLog(@"maqihan==========%f",imageDuration);
    //滑动动画
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"position"];
    animation.fromValue = [NSValue valueWithCGPoint:oldPoint];
    animation.toValue = [NSValue valueWithCGPoint:newPoint];
    animation.duration = 0.3;
    animation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionLinear];
    animation.removedOnCompletion = NO;
    animation.fillMode = kCAFillModeForwards;
    [animation setBeginTime:1.5];
    
    // 缩放动画
    CABasicAnimation *animationScale2 = [CABasicAnimation animationWithKeyPath:@"transform.scale"];
    animationScale2.duration = 0.3;
    animationScale2.repeatCount = 1;
    animationScale2.removedOnCompletion = NO;
    animationScale2.fillMode = kCAFillModeForwards;
    animationScale2.fromValue = [NSNumber numberWithFloat:2.0];
    animationScale2.toValue = [NSNumber numberWithFloat:0.0];
    [animationScale2 setBeginTime:1.5];
    
    
    CABasicAnimation *animationScale3 = [CABasicAnimation animationWithKeyPath:@"transform.scale"];
    animationScale3.duration = 0.3;
    animationScale3.repeatCount = 1;
    animationScale3.removedOnCompletion = NO;
    animationScale3.fillMode = kCAFillModeForwards;
    animationScale3.fromValue = [NSNumber numberWithFloat:0.1];
    animationScale3.toValue = [NSNumber numberWithFloat:2.0];
    [animationScale3 setBeginTime:0.0];
    
    CAAnimationGroup *group = [CAAnimationGroup animation];
    group.delegate = self;
    group.duration = 2;
    group.removedOnCompletion = NO;
    group.fillMode = kCAFillModeForwards;
    
    group.animations = [NSArray arrayWithObjects:animation,animationScale3,animationScale2, nil];
    [d.layer addAnimation:group forKey:@"move-scale-layer"];

}

#pragma mark - CAAnimationDelegate
- (void)animationDidStart:(CAAnimation *)anim
{
    [[TKEduSessionHandle shareInstance] startPlayAudioFile:_giftWav loop:NO];
}

-(void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
 
    //动画结束 移除gifView
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    NSEnumerator *subviewsEnum = [window.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:[FLAnimatedImageView class]]) {
            [subview removeFromSuperview];
        }
    }
    
    
//    int currentGift = 0;
//    if(_iRoomUser && _iRoomUser.properties && [_iRoomUser.properties objectForKey:sGiftNumber]){
//
//        currentGift = [[_iRoomUser.properties objectForKey:sGiftNumber] intValue];
//    }
    
    if (_manyMaskView) [self.manyMaskView refreshUI];
    if (_oneMaskView)  [self.oneMaskView refreshUI];
}

#pragma mark - TKVideoPopupMenu Delegate 实现

-(void)didPressChangeButton
{
    [_videoPopupMenu dismiss];
    
    //发送信令
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"MainPeopleExchangeVideo"
                                                         ID:@"MainPeopleExchangeVideo"
                                                         To:sTellAll
                                                       Data:@{@"doubleId":self.iPeerId}
                                                       Save:YES
                                                 completion:nil];
}

-(void)videoPopupMenuCanDraw:(UIButton *)aButton {
    
    [_videoPopupMenu dismiss];

    if ( ![_iPeerId isEqualToString:@""]) {

        TKLog(@"授权涂鸦");
        if (_iRoomUser.publishState>1) {
            
            [[TKEduSessionHandle shareInstance]configureDraw:!_iRoomUser.canDraw isSend:YES to:sTellAll peerID:_iRoomUser.peerID];
        }
    }
}

-(void)videoPopupMenuUnderPlatform:(UIButton *)aButton {
    
    [_videoPopupMenu dismiss];

    if ( ![_iPeerId isEqualToString:@""]) {
        
        TKLog(@"下讲台");
        PublishState tPublishState = (PublishState)_iRoomUser.publishState;
        BOOL isShowVideo = (tPublishState != TKPublishStateNONE);
        if (isShowVideo) {
            [_iEduClassRoomSessionHandle sessionHandleChangeUserPublish:_iPeerId Publish:TKPublishStateNONE completion:nil];
            // 助教始终有画笔权限
            if (_iRoomUser.role != TKUserType_Assistant) {
                [[TKEduSessionHandle shareInstance]configureDraw:false isSend:true to:sTellAll peerID:_iRoomUser.peerID];
            }
            
        } else {
            [_iEduClassRoomSessionHandle sessionHandleChangeUserPublish:_iPeerId Publish:TKPublishStateBOTH completion:nil];
        }
    }
}
-(void)videoPopupMenuControlAudio:(UIButton *)aButton {
    TKLog(@"关闭音频");
    [_videoPopupMenu dismiss];

    if (![_iPeerId isEqualToString:@""]) {
        
        PublishState tPublishState = (PublishState)_iRoomUser.publishState;
        switch (tPublishState) {
            case TKPublishStateVIDEOONLY:
                tPublishState = TKPublishStateBOTH;
                break;
            case TKPublishStateAUDIOONLY:
                tPublishState = TKPublishStateNONEONSTAGE;
                break;
            case TKPublishStateBOTH:
                tPublishState = TKPublishStateVIDEOONLY;
                break;
            case TKPublishStateNONE:
            case TKPublishStateNONEONSTAGE:
                tPublishState = TKPublishStateAUDIOONLY;
                break;
            default:
                tPublishState = TKPublishStateAUDIOONLY;
                break;
        }
        
        // iPad端不通过AudioEnable来开关视频，直接发publish状态改变信令
        [_iEduClassRoomSessionHandle sessionHandleChangeUserPublish:_iPeerId Publish:tPublishState completion:nil];
        
        BOOL hasAudio = (tPublishState == TKPublishStateAUDIOONLY || tPublishState == TKPublishStateBOTH);
        if (_iRoomUser.role == TKUserType_Student && hasAudio) {
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_iRoomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
        }
    }
}

#pragma mark - 发送奖杯
-(void)videoPopupMenuSendGif:(UIButton *)aButton {
    TKLog(@"发奖励");
    [_videoPopupMenu dismiss];

    TKEduSessionHandle *tSessionHandle = [TKEduSessionHandle shareInstance];
    TKRoomUser *tRoomUser = _iRoomUser;
    
     NSArray *arr = [[TKEduClassRoom shareInstance].roomJson.trophy copy];
    BOOL customTrophyFlag = [TKEduClassRoom shareInstance].roomJson.configuration.customTrophyFlag;
    
    
    //当有一个自定义奖杯时候 不需要弹出框 直接发送奖杯
    if (arr.count==1 && customTrophyFlag) {
        __weak __typeof(self)weakSelf = self;
        
        [TKEduNetManager sendGifForRoomUser:@[tRoomUser]
                                     roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                    aMySelf:tSessionHandle.roomMgr.localUser
                                      aHost:sHost
                                      aPort:sPort
                              aSendComplete:^(id  _Nullable response) {
                                  __strong __typeof(self)strongSelf = weakSelf;
                                  int currentGift = 0;
                                  if(tRoomUser && tRoomUser.properties && [tRoomUser.properties objectForKey:sGiftNumber]){
                                      currentGift = [[_iRoomUser.properties objectForKey:sGiftNumber] intValue];
                                  }
                                  NSDictionary *dict = @{
                                                         @"giftnumber":@(currentGift + 1),
                                                         @"giftinfo":[arr firstObject],
                                                         };
                                  [strongSelf->_iEduClassRoomSessionHandle sessionHandleChangeUserProperty:strongSelf->_iRoomUser.peerID
                                                                                                  TellWhom:sTellAll
                                                                                                      data:dict
                                                                                                completion:^(NSError * _Nonnull error) { }];
                              }aNetError:nil];
    }
    // 存在自定义奖杯
    if (arr.count > 1&& customTrophyFlag) {
        
        UIView *whiteBoardView = [TKEduSessionHandle shareInstance].whiteboardView;
        
        // 白板尺寸
        CGRect wbRect = [whiteBoardView convertRect:whiteBoardView.bounds toView:[UIApplication sharedApplication].keyWindow];
        
        CGFloat wbHeight = wbRect.size.height;
        CGFloat wbWidth  = wbRect.size.width;
        
        // 白板 中心点
        CGPoint relyPoint = CGPointMake(wbRect.origin.x + wbWidth / 2, wbRect.origin.y + wbHeight/2);
        
        // 自定义奖杯弹框： 宽 5/10
        CGFloat trophyW = fmaxf(wbWidth * 0.5, 275);
        // 高 9/10(改： 根据按钮数量给高度)
        CGFloat trophyH = trophyW / 3 ;
        trophyH = (arr.count / 5 + 1) * trophyH + 20;
        
        CGFloat trophyX = relyPoint.x - trophyW/2;
        CGFloat trophyY = relyPoint.y - trophyH/2;
        
        _trophyView =[[TKTrophyView alloc] initWithFrame:CGRectMake(trophyX, trophyY, trophyW, trophyH) chatController:@""];
        
        [_trophyView showOnView:self trophyMessage:arr];
        
        __weak __typeof(self)weakSelf = self;
        _trophyView.sendTrophy = ^(NSDictionary *message) {
            
            [TKEduNetManager sendGifForRoomUser:@[tRoomUser]
                                         roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                        aMySelf:tSessionHandle.roomMgr.localUser
                                          aHost:sHost
                                          aPort:sPort
                                  aSendComplete:^(id  _Nullable response) {
                                      
                 __strong __typeof(self)strongSelf = weakSelf;
                 
                 int currentGift = 0;
                 if(tRoomUser && tRoomUser.properties && [tRoomUser.properties objectForKey:sGiftNumber]){
                     currentGift = [[_iRoomUser.properties objectForKey:sGiftNumber] intValue];
                 }
                                      
                 NSDictionary *dict = @{
                                        @"giftnumber":@(currentGift + 1),
                                        @"giftinfo":message,
                                        };
                 [strongSelf->_iEduClassRoomSessionHandle sessionHandleChangeUserProperty:strongSelf->_iRoomUser.peerID
                                                                                 TellWhom:sTellAll
                                                                                     data:dict
                                                                               completion:^(NSError * _Nonnull error) { }];
             }aNetError:nil];
            
        };
        
    }else {
        if (_iPeerId.length > 0) {
            
            [TKEduNetManager sendGifForRoomUser:@[tRoomUser]
                                         roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                        aMySelf:tSessionHandle.roomMgr.localUser
                                          aHost:sHost
                                          aPort:sPort
                                  aSendComplete:^(id  _Nullable response) {
                                      
                 int currentGift = 0;
                 NSDictionary *giftInfo;
                 if(tRoomUser && tRoomUser.properties && [tRoomUser.properties objectForKey:sGiftNumber]) {
                     
                     currentGift = [[_iRoomUser.properties objectForKey:sGiftNumber] intValue];
                     giftInfo = [NSDictionary dictionaryWithDictionary:[TKUtil getDictionaryFromDic:_iRoomUser.properties Key:sGiftinfo]];
                 }
                 
                 [_iEduClassRoomSessionHandle sessionHandleChangeUserProperty:_iRoomUser.peerID TellWhom:sTellAll Key:sGiftNumber Value:@(currentGift + 1) completion:nil];
                 
             } aNetError:nil];
        }
    }
    
}
-(void)videoPopupMenuControlVideo:(UIButton *)aButton {
    TKLog(@"关闭视频");
    [_videoPopupMenu dismiss];

    if (![_iPeerId isEqualToString:@""]) {

        PublishState tPublishState = (PublishState)_iRoomUser.publishState;
        switch (tPublishState) {
            case TKPublishStateVIDEOONLY:
                tPublishState = TKPublishStateNONEONSTAGE;
                
                break;
            case TKPublishStateAUDIOONLY:
                tPublishState = TKPublishStateBOTH;
                
                break;
            case TKPublishStateBOTH:
                tPublishState = TKPublishStateAUDIOONLY;
                
                break;
            case TKPublishStateNONE:
            case TKPublishStateNONEONSTAGE:
                tPublishState = TKPublishStateVIDEOONLY;
                
                break;
            default:
                tPublishState = TKPublishStateVIDEOONLY;
                break;
        }
        // iPad不通过VideoEnable来开关视频，直接发publish状态改变信令
        [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_iPeerId Publish:tPublishState completion:nil];
        
        BOOL hasVideo = (tPublishState == TKPublishStateBOTH || tPublishState == TKPublishStateVIDEOONLY);
        if (_iRoomUser.role == TKUserType_Student && hasVideo) {
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_iRoomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
        }
    }
}
-(void)videoSplitScreenVideo:(UIButton *)aButton {//分屏显示
    
    [_videoPopupMenu dismiss];
    
    if (self.splitScreenClickBlock) {
        self.splitScreenClickBlock();
    }
}

-(void)videoPopupMenuControlRestorePosition:(UIButton *)aButton {//恢复位置
    [_videoPopupMenu dismiss];
    
    self.isDrag = NO;

    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sVideoSplitScreen ID:sVideoSplitScreen To:sTellAll Data:@{} completion:nil];
    
    NSMutableDictionary *dict    = [NSMutableDictionary dictionary];
    NSMutableDictionary *addDict = [NSMutableDictionary dictionary];
    
    for (TKCTVideoSmallView *video in self.videoArray) {
        
        if (!video.iRoomUser) {
            continue;
        }
        
        CGFloat left = 0;
        CGFloat top  = 0;
        if (video.isDrag) {
            left = CGRectGetMinX(video.frame) / (CGRectGetWidth(video.superview.frame) - CGRectGetWidth(video.frame));
            top = CGRectGetMinY(video.frame) / (CGRectGetHeight(video.superview.frame) - CGRectGetHeight(video.frame));
        }
        
        NSDictionary *info = @{@"percentTop":@(top),@"percentLeft":@(left),@"isDrag":@(video.isDrag)};
        [addDict setValue:info forKey:video.iPeerId];
    }
    [dict setValue:addDict forKey:@"otherVideoStyle"];
    
    [[TKEduSessionHandle shareInstance] publishVideoDragWithDic:dict To:sTellAll];

}

-(void)videoPopupMenuControlRestoreAll:(UIButton *)aButton {//全部恢复
    [_videoPopupMenu dismiss];
    

    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sVideoSplitScreen ID:sVideoSplitScreen To:sTellAll Data:@{} completion:nil];
    
    NSMutableDictionary *dict    = [NSMutableDictionary dictionary];
    NSMutableDictionary *addDict = [NSMutableDictionary dictionary];
    
    NSArray *users = [[TKEduSessionHandle shareInstance] userStdntAndTchrArray];
    
    for (TKRoomUser *user in users) {
        
        NSDictionary *info = @{@"percentTop":@(0),@"percentLeft":@(0),@"isDrag":@(NO)};
        [addDict setValue:info forKey:user.peerID];
    }
    [dict setValue:addDict forKey:@"otherVideoStyle"];
    [[TKEduSessionHandle shareInstance] publishVideoDragWithDic:dict To:sTellAll];

}

#pragma mark private
- (void)setIVideoViewTag:(NSInteger)iVideoViewTag{
    _iVideoViewTag = iVideoViewTag;
    if (_manyMaskView) _manyMaskView.iVideoViewTag = iVideoViewTag;
    if (_oneMaskView)  _oneMaskView.iVideoViewTag = iVideoViewTag;
}

- (void) maskViewChangeForPicInPicWithisShow:(BOOL)isShow {
    if (_manyMaskView) [self.manyMaskView maskViewChangeForPicInPicWithisShow:isShow];
    if (_oneMaskView)  [self.oneMaskView maskViewChangeForPicInPicWithisShow:isShow];
}

#pragma mark - 懒加载
- (FLAnimatedImageView *)gifView
{
    FLAnimatedImageView *gifView= [[FLAnimatedImageView alloc] init];
    gifView.contentMode = UIViewContentModeScaleAspectFit;
    gifView.width = 100;
    gifView.height = 125;
    gifView.center = CGPointMake(ScreenW/2, ScreenH/2);
    return gifView;
}
@end

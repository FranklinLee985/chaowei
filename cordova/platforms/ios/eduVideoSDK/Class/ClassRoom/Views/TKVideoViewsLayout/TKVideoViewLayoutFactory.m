//
//  TKVideoViewLayoutFactory.m
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKVideoViewLayoutFactory.h"
#import "TKCTVideoSmallView.h"
#import "TKEduSessionHandle.h"

@interface TKVideoViewLayoutFactory()

@property (weak , nonatomic) UIView *whiteBoardView;
@property (weak , nonatomic) UIView *superview;

//记录前一刻的fame
@property (assign , nonatomic) CGRect preFrame;
//白板响应了信令的全屏
@property (assign , nonatomic) BOOL signalFullScreen;

@property (strong , nonatomic) TKManyNormalLayoutView  *normalView;	 // 视频置顶
@property (strong , nonatomic) TKManySpeakerLayoutView *speakerView; // 主讲模式
@property (strong , nonatomic) TKManyFreeLayoutView    *freeView;	 //

@end

@implementation TKVideoViewLayoutFactory

- (instancetype)initWithWhiteBoard:(UIView *)whiteBoard superview:(UIView *)superview
{
    self = [super init];
    if (self) {
        _whiteBoardView = whiteBoard;
        _superview      = superview;
        
        [self commonInit];
    }
    return self;
}

//初始化
- (void)commonInit
{
    //数组初始化
    [self arrayCapacity];
    
    //测试代码
//    __weak __typeof__ (self) wself = self;
//    [NSTimer scheduledTimerWithTimeInterval:3 repeats:YES block:^(NSTimer * _Nonnull timer) {
//
//        for (TKCTVideoSmallView *videoView in wself.videoArray) {
//            if(videoView.iRoomUser) {
//                NSLog(@"maqihan====%@",videoView.iRoomUser.nickName);
//
//                //正在播放此用户了
//                if (videoView.superview == nil) {
//
//                }
//            }
//        }
//    }];
}

#pragma mark - Publish

- (TKCTVideoSmallView *)playVideoWithUser:(TKRoomUser *)user
{
    //找到播放的小视频
    TKCTVideoSmallView *playVideo = nil;
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        
        if([videoView.iRoomUser.peerID isEqualToString:user.peerID]) {
            //正在播放此用户了
            return videoView;
        }
        else if(!playVideo && videoView.iRoomUser == nil) {
            playVideo = videoView;
        }
    }
    
    //对小视频执行play操作
    if (playVideo) {
        [[TKEduSessionHandle shareInstance] sessionHandlePlayVideo:user.peerID
                                                        renderType:(TKRenderMode_adaptive)
                                                            window:playVideo.videoView
                                                        completion:^(NSError *error) {
                                                        }];
        
        [[TKEduSessionHandle shareInstance] sessionHandlePlayAudio:user.peerID completion:^(NSError *error) {

        }];

        //保存数据
        playVideo.iPeerId        = user.peerID;
        playVideo.iRoomUser      = user;
        [playVideo changeName:user.nickName];
        
        [TKEduSessionHandle shareInstance].onPlatformNum = [self allPlayingVideoCount];
        
        //配置
        [self configContentViews];

        //分批次扩容
        NSInteger count = [self allPlayingVideoCount];
        if (count == 6 && self.videoArray.count == 8) {
            [self arrayCapacity];
        }else if (count == 14 && self.videoArray.count == 16){
            [self arrayCapacity];
        }
        
        //通知控制器 用户变化
        if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
            [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
        }
        /*这段代码添加的有重复性 以后会排查*/
        //刷新view
        if ([self.contentView isKindOfClass:self.speakerView.class]) {
            TKManySpeakerLayoutView *view = (TKManySpeakerLayoutView *)self.contentView;
            if (view.speaker == nil) {// 没有主讲
                
                if (view.peerID.length == 0) {
                    if (playVideo.iRoomUser.role == TKUserType_Teacher) {// 老师
                        
                        playVideo.isSpeaker = YES;
                    }
                }
                else if([view.peerID isEqualToString:playVideo.iPeerId]){ // 上一个是学生主讲 学生不清除peerID
                    playVideo.isSpeaker = YES;
                }
            }
            
    
        }
        self.contentView.videoArray = self.videoArray;
        
        return playVideo;
    }
    return nil;
}

- (void)unPlayVideoWithUserID:(NSString *)userID
{
    if (![userID isKindOfClass:[NSString class]] || !userID.length) {
        return;
    }
    
    //静音状态下 点击下台不走unplay? 暂未验证
    //找到播放的小视频
    TKCTVideoSmallView *unPlayVideo = nil;
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        
        if([videoView.iRoomUser.peerID isEqualToString:userID]) {
            unPlayVideo = videoView;
            break;
        }
    }
    
    //对小视频执行unplay操作
    if (unPlayVideo) {
        
        [[TKEduSessionHandle shareInstance] sessionHandleUnPlayVideo:userID
                                                          completion:^(NSError *error) {
                                                          }];
        
        [[TKEduSessionHandle shareInstance] sessionHandleUnPlayAudio:userID
                                                          completion:^(NSError *error) {
                                                          }];
        
        /*这段代码添加的有重复性 以后会排查*/
        if ([self.contentView isKindOfClass:self.speakerView.class]) {
            TKManySpeakerLayoutView *view = (TKManySpeakerLayoutView *)self.contentView;
            // 主讲unplay
            if (view.speaker != nil && [unPlayVideo.iPeerId isEqualToString:view.speaker.iPeerId]) {
                // 老师
                if (view.speaker.iRoomUser.role == TKUserType_Teacher) {
                    view.peerID = @"";
                }
                view.speaker = nil;
            }
            
        }
        
        //清除数据
        [unPlayVideo clearVideoData];
        unPlayVideo.size = [self videoViewNormalSize];
        [unPlayVideo removeFromSuperview];
        
        [TKEduSessionHandle shareInstance].onPlatformNum = [self allPlayingVideoCount];
        
        //配置
        [self configContentViews];
        
    }
    
    //通知控制器 用户变化
    if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
        [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
    }
    
    // 刷新view
    self.contentView.videoArray = self.videoArray;
}

- (void)userLeftRoom:(NSString *)peerid {
    
    if ([self.contentView isKindOfClass:self.speakerView.class]) {
        
        TKManySpeakerLayoutView *view = (TKManySpeakerLayoutView *)self.contentView;
        if ([peerid isEqualToString:view.peerID]) {
            view.peerID = @"";
        }
        if (view.speaker && ([peerid isEqualToString:view.speaker.iPeerId] || [peerid isEqualToString:view.speaker.iRoomUser.peerID])) {
            view.speaker = nil;
        }
    }
}

- (void)setViewLayout:(TKRoomLayout)viewLayout
{
    // pc切iOS 不存在的布局也需要刷新一次
//    if (_viewLayout == viewLayout) {
//        return;
//    }
   
    _viewLayout = viewLayout;
    self.contentView.videoArray = nil;
    //切换布局 重置变量
    [self resetAllVideoViews];
    
    if (viewLayout == CoursewareDown) {
        
        self.normalView.frame = CGRectMake(0, TKNavHeight, ScreenW, [self videoViewNormalSize].height);
        [self.superview addSubview:self.normalView];
        self.contentView = self.normalView;
        
        
    }else if (viewLayout == MainPeople){
        
        self.speakerView.frame = CGRectMake(0, TKNavHeight, ScreenW, ScreenH - TKNavHeight);
        [self.superview addSubview:self.speakerView];
        self.contentView = self.speakerView;
        
        // 默认 老师主讲
        TKCTVideoSmallView *tView = [self teacherVideoVideo];
        tView.isSpeaker = YES;
        self.speakerView.speaker = tView;

    }else{
        
        self.freeView.frame = CGRectMake(0, TKNavHeight, ScreenW, ScreenH - TKNavHeight);
        [self.superview addSubview:self.freeView];
        self.contentView = self.freeView;
    }

    [self configContentViews];
    self.contentView.videoArray = self.videoArray;
}


- (TKCTVideoSmallView *)videoViewWithUserID:(NSString *)userID
{
    if (!userID.length) {
        return nil;
    }
    
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if ([videoView.iPeerId isEqualToString:userID]) {
            return videoView;
        }
    }
    return nil;
}

//切换主讲
- (void)exchangeSpeakerWithUserID:(NSString *)userID
{
    if (userID.length == 0) {
        return;
    }
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        // 找出主讲 重置状态
        videoView.isSpeaker = [videoView.iPeerId isEqualToString:userID];
    }
    
    self.contentView.videoArray = self.videoArray;
}

- (void)resetAllVideoViews
{
    //切换布局 会发复位状态的信令 去恢复 isDrag isSplit，但是isSpeaker没有信令需要自己复位
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        
        if ([TKEduClassRoom shareInstance].roomJson.configuration.onlyMeAndTeacherVideo &&
            [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
            
            if (videoView.iRoomUser.role != TKUserType_Teacher &&
                [videoView.iRoomUser.peerID isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID] == NO) {
                
                [self unPlayVideoWithUserID:videoView.iRoomUser.peerID];
            }
        }
        
        videoView.isDrag = NO;
        videoView.isSplit = NO;
        videoView.isSpeaker = NO;
        videoView.isPicInPic = NO;
        [videoView hidePopMenu];
        
        videoView.videoMode  = TKVideoViewMode_Fill;
        videoView.maskLayout = TKMaskViewLayout_Normal;
    }
}

- (void)configContentViews
{
    if (self.viewLayout == CoursewareDown) {

        self.normalView.hidden = NO;
        self.speakerView.hidden = YES;
        self.freeView.hidden = YES;

        if ([self playingVideoCount] == 0) {
            self.normalView.hidden = YES;
        }else{
            self.normalView.hidden = NO;
        }

    }else if (self.viewLayout == MainPeople){
        self.normalView.hidden = YES;
        self.speakerView.hidden = NO;
        self.freeView.hidden = YES;

       

    }else{
        self.normalView.hidden = YES;
        self.speakerView.hidden = YES;
        self.freeView.hidden = NO;

    }
}

- (void)unPlayAllViews
{
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        
        [[TKEduSessionHandle shareInstance] sessionHandleUnPlayVideo:videoView.iRoomUser.peerID
                                                          completion:^(NSError * _Nonnull error) {
                                                          }];
        
        [[TKEduSessionHandle shareInstance] sessionHandleUnPlayAudio:videoView.iRoomUser.peerID completion:nil];
        
        videoView.isDrag = NO;
        videoView.isSplit = NO;
        videoView.isSpeaker = NO;
        videoView.isPicInPic = NO;
        [videoView clearVideoData];
        [videoView removeFromSuperview];
    }
    
    self.contentView.videoArray = self.videoArray;
}

//分屏
- (void)splitScreenWithInfo:(NSDictionary *)dict superview:(UIView *)superview
{
    if ([self.contentView isKindOfClass: [TKManyNormalLayoutView class]]) {
        [(TKManyNormalLayoutView *)self.contentView splitScreenWithInfo:dict superview:superview];
    }

    [self configContentViews];

    //通知控制器 用户变化
    if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
        [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
    }
}

//拖拽
- (void)dragVideoView:(TKCTVideoSmallView *)videoView left:(CGFloat)left top:(CGFloat)top superview:(UIView *)view
{
    if ([self.contentView isKindOfClass: [TKManyNormalLayoutView class]]) {
        [(TKManyNormalLayoutView *)self.contentView dragVideoView:videoView left:left top:top superview:view];
    }
    
    [self configContentViews];

    //通知控制器 用户变化
    if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
        [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
    }
}

//缩放
- (void)scaleVideoViewWithZoomRatio:(CGFloat)ratio userID:(NSString *)userID superview:(UIView *)superview
{
    if ([self.contentView isKindOfClass: [TKManyNormalLayoutView class]]) {
        [(TKManyNormalLayoutView *)self.contentView scaleVideoViewWithZoomRatio:ratio userID:userID superview:superview];
    }
    
    [self configContentViews];

    //通知控制器 用户变化
    if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
        [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
    }
}

//课件全屏 返回老师的video
- (TKCTVideoSmallView *)fullScreenShowTeacherVideoView:(BOOL)state superview:(UIView *)superview
{
    TKCTVideoSmallView *teacherVideoView = [self teacherVideoVideo];
    if (!teacherVideoView) {
        return nil;
    }
    // 有 视频才响应画中画
    if (teacherVideoView.iRoomUser.publishState == TKUser_PublishState_VIDEOONLY ||
        teacherVideoView.iRoomUser.publishState == TKUser_PublishState_BOTH ) {
        
        self.signalFullScreen = state;
        
        if (state) {
            [teacherVideoView removeFromSuperview];
            teacherVideoView.isPicInPic = YES;
            //防止在外面有人操作 隐藏白板上的视频
            teacherVideoView.hidden = NO;
            [teacherVideoView hideMaskView:YES];
            CGSize size = [self videoViewNormalSize];
            CGRect frame = CGRectMake(CGRectGetWidth(superview.frame) -size.width - 10 , CGRectGetHeight(superview.frame) -size.height - 10, size.width, size.height);
            teacherVideoView.frame = frame;
            [superview addSubview:teacherVideoView];
            [superview.superview bringSubviewToFront:superview];
        }else{

            teacherVideoView.isPicInPic = NO;
            [teacherVideoView hideMaskView:NO];
            
            //通过数据赋值 刷新collectionview
            self.contentView.videoArray = self.videoArray;
            if (superview == self.whiteBoardView) {
                [self.contentView.superview bringSubviewToFront:self.contentView];
            }
        }
        
        [self configContentViews];
        
        //通知控制器 用户变化
        if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
            [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
        }
    }

    
    return teacherVideoView;
}

#pragma mark - Action

- (void)panGestureAction:(UIPanGestureRecognizer *)panGesture
{
   TKCTVideoSmallView * videoView = (TKCTVideoSmallView *)panGesture.view;
    
    if (![TKEduSessionHandle shareInstance].isClassBegin) {
        //没上课 不能拖动
        return;
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student &&
        self.signalFullScreen == NO && (videoView.isDrag == NO || [TKEduSessionHandle shareInstance].localUser.canDraw == NO)) {
        //学生 不能拖动
        return;
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol
        && !self.signalFullScreen) {
        //巡课 不能拖动
        return;
    }
    
    //其他布局不响应手势
    if (self.viewLayout != CoursewareDown) {
        return;
    }
    
    if (videoView.isSplit) {
        return;
    }
    
    if (panGesture.state == UIGestureRecognizerStateBegan) {
        [videoView.superview bringSubviewToFront:videoView];
        
    }else if (panGesture.state == UIGestureRecognizerStateChanged) {
        
        CGPoint point = [panGesture translationInView:videoView];
        CGFloat x = panGesture.view.center.x + point.x;
        CGFloat y = panGesture.view.center.y + point.y;
        panGesture.view.center = CGPointMake(x, y);
        [panGesture setTranslation:CGPointZero inView:videoView];
        
    }else if (panGesture.state == UIGestureRecognizerStateEnded) {
        
        if (!self.signalFullScreen) {
            [self dragVideoView:videoView];
        }
        [self viewRebound:videoView];
    }
}

- (void)pinchGestureAction:(UIPinchGestureRecognizer *)pinchGesture
{
    TKCTVideoSmallView *videoView = (TKCTVideoSmallView *)pinchGesture.view;
    
    // 巡课不允许缩放
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    if (![TKEduSessionHandle shareInstance].iIsCanDraw ) {
        return;
    }

    // 没有拖出去不允许缩放
    if (videoView.isDrag == NO) {
        return;
    }
    
    if (videoView.isPicInPic == YES) {
        return;
    }
    
    if (pinchGesture.state == UIGestureRecognizerStateBegan) {
        [videoView.superview bringSubviewToFront:videoView];
        
        self.preFrame = videoView.frame;
        
    }else if (pinchGesture.state == UIGestureRecognizerStateChanged) {
        
        CGFloat newWidth  = CGRectGetWidth(self.preFrame) * pinchGesture.scale;
        CGFloat newheight = CGRectGetHeight(self.preFrame) * pinchGesture.scale;
        CGFloat width  = [self videoViewNormalSize].width;
        CGPoint center    = videoView.center;

        //不能小于原始宽度
        if (newWidth < width) {
            return;
        }
        
        // 保证不超出白板
        if (newheight > CGRectGetHeight(self.whiteBoardView.frame)) {
            newheight = CGRectGetHeight(self.whiteBoardView.frame);
            newWidth = newheight * 4.0/3;
        }
        if (newWidth > CGRectGetWidth(self.whiteBoardView.frame)) {
            newWidth  = CGRectGetWidth(self.whiteBoardView.frame);
            newheight = newWidth * 3.0/4;
        }
        
        videoView.frame = CGRectMake(center.x - newWidth/2, center.y - newheight/2, newWidth, newheight);
        
    }else if (pinchGesture.state == UIGestureRecognizerStateEnded) {
        
        if (CGRectGetHeight(videoView.frame) >= CGRectGetHeight(self.whiteBoardView.frame) || CGRectGetWidth(videoView.frame) >= CGRectGetWidth(self.whiteBoardView.frame)) {
            videoView.center = [self.whiteBoardView.superview convertPoint:self.whiteBoardView.center toView:self.whiteBoardView];
        }
        
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            
            //最初的宽度
            CGFloat width  = [self videoViewNormalSize].width;
            
            NSDictionary *dict = @{@"ScaleVideoData":
                                       @{videoView.iRoomUser.peerID:
                                             @{@"scale":@(CGRectGetWidth(videoView.frame) / width)}
                                         }
                                   };
            NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:nil];
            NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sVideoZoom ID:sVideoZoom To:sTellAll Data:jsonString Save:true AssociatedMsgID:nil AssociatedUserID:nil expires:0 completion:^(NSError * _Nonnull error) {
                [self publishDragMsg];
            }];
        }
        
        [self viewRebound:videoView];
    }
}

- (void)dragVideoView:(TKCTVideoSmallView *)videoView
{

    CGRect frame = [videoView.superview convertRect:videoView.frame toView:self.whiteBoardView.superview];
    
    //没触发拖到白板的条件，视频需要回到原位
    if (videoView.superview != self.whiteBoardView) {
        
        if (CGRectGetMinY(frame) <= CGRectGetMinY(self.whiteBoardView.frame)) {
            [UIView animateWithDuration:0.3 animations:^{
                videoView.frame = videoView.superview.bounds;
            }];
            return;
        }
    }
    
    //父视图改变
    if (videoView.superview != self.whiteBoardView) {
        
        if (CGRectGetMinY(frame) > CGRectGetMinY(self.whiteBoardView.frame)) {
            //从视频区切换到了白板
            videoView.isDrag = YES;
            //通过数据赋值 刷新collectionview
            self.contentView.videoArray = self.videoArray;

            //设置frame
            CGRect newFrame = [videoView.superview convertRect:videoView.frame toView:self.whiteBoardView];
            newFrame.size = [self videoViewNormalSize];
            videoView.frame = newFrame;
            [self.whiteBoardView addSubview:videoView];
            
            //拖到白板的小视频 需要打开音频
            [self openAudioWithUser:videoView.iRoomUser];
            [self configContentViews];

            //通知控制器 用户变化
            if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
                [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
            }
            
        }
        
    }else{
        
        if (CGRectGetMinY(frame) < CGRectGetMinY(self.whiteBoardView.frame) &&
            [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            //从白板区切换到视频区
            videoView.isDrag = NO;
            //通过数据赋值 刷新collectionview
            self.contentView.videoArray = self.videoArray;
            [self configContentViews];

            //通知控制器 用户变化
            if ([self.delegate respondsToSelector:@selector(playingVideoWithUserCount:)]) {
                [self.delegate playingVideoWithUserCount:[self playingVideoCount]];
            }
        }
    }
    
    //发送信令
    [self publishDragMsg];
}

//边界碰撞回弹
- (void)viewRebound:(TKCTVideoSmallView *)videoView
{
    //边界检测
    if (videoView.superview == self.whiteBoardView) {
        CGRect newFrame = videoView.frame;
        
        if (CGRectGetMinX(newFrame) <=  0) {
            newFrame.origin.x = 0;
        }
        
        if (CGRectGetMaxX(newFrame) >=  CGRectGetWidth(self.whiteBoardView.frame)) {
            newFrame.origin.x = CGRectGetWidth(self.whiteBoardView.frame) - CGRectGetWidth(newFrame);
        }
        
        if (CGRectGetMinY(newFrame) <=  0) {
            newFrame.origin.y = 0;
        }
        
        if (CGRectGetMaxY(newFrame) >=  CGRectGetHeight(self.whiteBoardView.frame)) {
            newFrame.origin.y = CGRectGetHeight(self.whiteBoardView.frame) - CGRectGetHeight(newFrame);
        }
        
        [UIView animateWithDuration:0.3 animations:^{
            videoView.frame = newFrame;
        }];
    }

}

#pragma mark - Private

//拖动视频 发送信令
- (void)publishDragMsg
{
    //发送信令
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
        
        if (isnan(top) || isinf(top)) {
            top = 0;
        }
        if (isnan(left) || isinf(left)) {
            left = 0;
        }
        
        if (left < 0) left = 0;
        if (left > 1) left = 1;
        if (top < 0)  top  = 0;
        if (top > 1)  top  = 1;
 
        NSDictionary *info = @{@"percentTop":@(top),@"percentLeft":@(left),@"isDrag":@(video.isDrag)};
        [addDict setValue:info forKey:video.iPeerId];
    }
    [dict setValue:addDict forKey:@"otherVideoStyle"];

    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        [[TKEduSessionHandle shareInstance] publishVideoDragWithDic:dict To:sTellAll];
    }
}

- (void)openAudioWithUser:(TKRoomUser *)user
{
    //音频关闭时打开音频
    TKPublishState state = user.publishState;
    if (state == TKUser_PublishState_BOTH || state == TKUser_PublishState_VIDEOONLY) {
        state = TKUser_PublishState_BOTH;
    } else if (state == TKUser_PublishState_AUDIOONLY ||
               state == TKUser_PublishState_NONE ||
               state == TKUser_PublishState_UNKown ||
               state == TKPublishStateLocalNONE ||
               state == TKPublishStateNONEONSTAGE) {
        state = TKUser_PublishState_AUDIOONLY;
    }
    [[TKEduSessionHandle shareInstance]  sessionHandleChangeUserPublish:user.peerID Publish:state completion:nil];
}

//数组扩容
- (void)arrayCapacity
{
    //服务器说openGL有特殊性，需要提前将小视频创建好 否则会有崩溃问题
    for (int i = 0; i < 8; i++) {
        TKCTVideoSmallView *videoView = [[TKCTVideoSmallView alloc] init];
        videoView.videoArray = self.videoArray;
        //给一个默认尺寸 防止进入教室就有拖拽的视频 没有尺寸
        videoView.frame = CGRectMake(0, 0, [self videoViewNormalSize].width, [self videoViewNormalSize].height);
        
        [self.videoArray addObject:videoView];
        
        //添加拖动手势
        UIPanGestureRecognizer * panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panGestureAction:)];
        [videoView addGestureRecognizer:panGesture];
        
        //添加捏合手势
        UIPinchGestureRecognizer *pinchGesture = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(pinchGestureAction:)];
        [videoView addGestureRecognizer:pinchGesture];

    }
}

//当前教室正在play的人数（只在视频区的）
- (NSInteger)playingVideoCount;
{
    NSInteger count = 0;
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        
        if (!videoView.isDrag && !videoView.isSplit && !videoView.isPicInPic) {
            count ++;
        }
    }
    return count;
}

//当前教室正在play的人数（包括白板内和视频区）
- (NSInteger)allPlayingVideoCount
{
    NSInteger count = 0;
    
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (videoView.iRoomUser) {
            count++;
        }
    }
    return count;
}

- (CGSize)videoViewNormalSize
{
    CGFloat width  = floor((ScreenW - 8 * 3) / 7.0);
    CGFloat height = width * 3.0/4;//必须保证事3:4 不能取整
    return CGSizeMake(width, height);
}

- (TKCTVideoSmallView *)teacherVideoVideo
{
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        
        if (videoView.iRoomUser.role == TKUserType_Teacher) {
            return videoView;
        }
    }
    
    return nil;
}

#pragma mark - Getter

- (NSMutableArray<TKCTVideoSmallView *> *)videoArray
{
    if (!_videoArray) {
        _videoArray = [NSMutableArray array];
    }
    return _videoArray;
}

- (TKManyNormalLayoutView *)normalView
{
    if (!_normalView) {
        _normalView = [[TKManyNormalLayoutView alloc] init];
        _normalView.hidden = YES;
    }
    return _normalView;
}

- (TKManySpeakerLayoutView *)speakerView
{
    if (!_speakerView) {
        _speakerView = [[TKManySpeakerLayoutView alloc] init];
        _speakerView.hidden = YES;
    }
    return _speakerView;
}

- (TKManyFreeLayoutView *)freeView
{
    if (!_freeView) {
        _freeView = [[TKManyFreeLayoutView alloc] init];
        _freeView.hidden = YES;
    }
    return _freeView;

}

@end

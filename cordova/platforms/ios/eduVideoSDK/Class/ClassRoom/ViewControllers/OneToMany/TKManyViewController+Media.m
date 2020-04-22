//
//  TKManyViewController+Media.m
//  EduClass
//
//  Created by maqihan on 2018/11/20.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import "TKManyViewController+Media.h"
#import <objc/runtime.h>
#import "TKMediaDocModel.h"
#import "Masonry.h"

@implementation TKManyViewController (Media)

/**用户媒体流发布状态 变化回调*/
- (void)sessionManagerOnShareMediaState:(NSString *)peerId state:(TKMediaState)state extensionMessage:(NSDictionary *)message{
    
    [self.view endEditing:YES];
    
    NSString *source = message[@"source"];
    BOOL isOutPlay   = source ? [source isEqualToString:@"h5Document"] : NO;
    
    //    static int mediaID ;
    
    
    if (state == TKMedia_Pulished) {
        [self.iSessionHandle configureHUD:@"" aIsShow:NO];
        [self.iSessionHandle.whiteBoardManager unpublishNetworkMedia:nil];
        self.iSessionHandle.isPlayMedia = YES;
        
        //self.whiteboardBackView.bounds;
        CGRect frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame));
        
        BOOL hasVideo = false;
        if([message objectForKey:@"video"]){
            hasVideo = [message[@"video"] boolValue];
        }
        
        objc_setAssociatedObject(self, @selector(sessionManagerOnShareMediaState:state:extensionMessage:), @(hasVideo), OBJC_ASSOCIATION_RETAIN);
        
        //进入视频隐藏聊天按钮，退出视频显示聊天按钮
        self.chatViewNew.hidden = hasVideo;
        //进入视频，未收起则收起；音频不需要收起
        if (hasVideo) {
            [self.chatViewNew.keyboardView.inputField resignFirstResponder];
            [self.chatViewNew hide:YES];
            [self tapOnViewToHide];
        }
        
        if (!hasVideo) {
            //TODO: - mp3模式下_iMediaView坐标有变化，顶部紧贴白板
            CGFloat mp3height = IS_PAD ? 91 : 91;
            CGFloat mp3Width = IS_PAD ? 363 : 363;
            //iphone或者ipad统一大小，按比例的话手机太小了
            frame = CGRectMake(CGRectGetMinX(self.view.frame) + 10,
                               /*CGRectGetMaxY(self.iTKEduWhiteBoardView.frame) - mp3height - (IS_PAD ? 60 : 45)*/self.whiteboardBackView.y + 5,
                               /*CGRectGetWidth(self.iTKEduWhiteBoardView.frame) - 20*/mp3Width, mp3height);
            if ([TKEduSessionHandle shareInstance].localUser.role== TKUserType_Patrol || [TKEduSessionHandle shareInstance].localUser.role== TKUserType_Student || ([TKEduSessionHandle shareInstance].localUser.role ==  TKUserType_Playback)) {
                
                frame.size.width = mp3height;
            }
            [TKEduSessionHandle shareInstance].iIsPlaying = YES;
            if ([TKEduSessionHandle shareInstance].iPreMediaDocModel) {
                [TKEduSessionHandle shareInstance].iCurrentMediaDocModel = [TKEduSessionHandle shareInstance].iPreMediaDocModel;
                [TKEduSessionHandle shareInstance].iPreMediaDocModel = nil;
            }
            else {
                
            }
        }
        
        // 播放的MP4前，先移除掉上一个MP4窗口
        if (self.iMediaView) {
            [self.iMediaView removeFromSuperview];
            self.iMediaView = nil;
        }
        
        //        frame = [self vidoeFrame:message[@"other"][@"position"]];
        TKCTBaseMediaView *tMediaView = [[TKCTBaseMediaView alloc] initWithMediaPeerID:peerId extensionMessage:message frame:frame];
        self.iMediaView = tMediaView;
        self.iMediaView.isOutPlay = isOutPlay;
        
        // 如果是回放，需要将播放视频窗口放在回放遮罩页下
        // 在白板区域播放
        if (self.iSessionHandle.isPlayback == YES) {
            [self.backgroundImageView insertSubview:self.iMediaView belowSubview:self.playbackMaskView];
            //            [self.whiteboardBackView addSubview:self.iMediaView];
            //            [self.view bringSubviewToFront:self.playbackMaskView];
        } else {
            [self.backgroundImageView addSubview:self.iMediaView];
            [self.backgroundImageView bringSubviewToFront: self.iMediaView];
            //            [self.whiteboardBackView addSubview:self.iMediaView];
        }
        //        [self.iMediaView mas_makeConstraints:^(MASConstraintMaker *make) {
        //            make.left.right.width.height.equalTo(self.whiteboardBackView);
        //        }];
        
        
        // 文档模型
        if ([TKEduSessionHandle shareInstance].iCurrentMediaDocModel == nil) {
            if (message[@"fileid"] && [message[@"fileid"] integerValue]) {
                
                [TKEduSessionHandle shareInstance].iCurrentMediaDocModel= [TKMediaDocModel new];
                [TKEduSessionHandle shareInstance].iCurrentMediaDocModel.fileid = [NSString stringWithFormat:@"%@",message[@"fileid"]];
            }
        }
        if (isOutPlay) {
            self.iMediaView.frame = [self vidoeFrame:message[@"other"][@"position"]];
            if (self.mediaID > 0) {
                [[TKRoomManager instance] stopPlayMediaFile: self.mediaID];
            }
            NSString *url = [NSString stringWithFormat:@"%@", message[@"url"]];
            
            tk_weakify(self);
            self.mediaID = [[TKRoomManager instance] startPlayMediaFile:url window:tMediaView loop:NO progress:^(int playID, int64_t current, int64_t total) {
                
                if (current >= total) {
                    
                    [weakSelf sessionManagerOnShareMediaState:peerId
                                                        state:TKMedia_Unpulished
                                             extensionMessage:@{@"source":@"h5Document"}
                     ];
                }
            }];
        }
        else {
            self.iMediaView.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame));
            [[TKEduSessionHandle shareInstance] sessionHandlePlayMediaFile:peerId renderType:0 window:tMediaView completion:^(NSError *error) {
                
                [self.iMediaView setVideoViewToBack];
                if (hasVideo) {
                    
                    //                    [self.iMediaView loadLoadingView];
                }
            }];
        }
        
    }
    else{
        BOOL hasVideo = ((NSNumber *)objc_getAssociatedObject(self, @selector(sessionManagerOnShareMediaState:state:extensionMessage:))).boolValue;
        if (hasVideo) {
            self.chatViewNew.hidden = NO;
            [self tapOnViewToHide];
        }
        objc_setAssociatedObject(self, @selector(sessionManagerOnShareMediaState:state:extensionMessage:), nil, OBJC_ASSOCIATION_RETAIN);
        
        //媒体流停止后需要删除sVideoWhiteboard
        [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sVideoWhiteboard ID:sVideoWhiteboard To:sTellAll Data:@{} completion:nil];
        
        [TKEduSessionHandle shareInstance].isPlayMedia = NO;
        [[TKEduSessionHandle shareInstance] configureHUD:@"" aIsShow:NO];
        
        if (isOutPlay) {
            self.mediaID = [[TKRoomManager instance] stopPlayMediaFile: self.mediaID];
            
        }
        else {
            [[TKEduSessionHandle shareInstance] sessionHandleUnPlayMediaFile:peerId completion:nil];
            
        }
        [self.iMediaView deleteWhiteBoard];
        
        if (!isOutPlay) {
            [self.iMediaView removeFromSuperview];
            self.iMediaView = nil;
        }
        
        [TKEduSessionHandle shareInstance].iCurrentMediaDocModel = nil;
        [TKEduSessionHandle shareInstance].iIsPlaying = NO;
        
        // 画中画
        if (self.roomJson.configuration.coursewareFullSynchronize &&
            self.iSessionHandle.iIsFullState == NO &&
            self.iSessionHandle.isPicInPic) {
            
            [self changeVideoFrame:NO];
        }
        self.currentMediaURL = nil;
    }
    
}

/**更新媒体流的信息回调*/
- (void)sessionManagerUpdateMediaStream:(NSTimeInterval)duration pos:(NSTimeInterval)pos isPlay:(BOOL)isPlay{
    
    [self.iMediaView updatePlayUI:isPlay];
    [self.iMediaView update:pos total:duration isPlay:isPlay];
    
    if (self.iSessionHandle.iIsPlaying != isPlay) {
        self.iSessionHandle.iIsPlaying = isPlay;
    }
}

/**媒体流加载出第一帧画面回调*/
- (void)sessionManagerMediaLoaded
{
    if (self.iMediaView) {
        [self.iMediaView hiddenLoadingView];
    }
    if (self.iFileView) {
        [self.iFileView hiddenLoadingView];
    }
}

- (void)outPlayMedia:(NSNotification *)noti {
    
    NSDictionary *dic = noti.object;
    
    //    BOOL play = [dic[@"isPlay"] boolValue];
    //    [self sessionManagerOnShareMediaState:self.iSessionHandle.localUser.peerID state:play extensionMessage:dic];
    
    NSString *status = [dic objectForKey:@"status"];
    NSString *url = [dic objectForKey:@"url"];
    if ([url isEqualToString:self.currentMediaURL]) {
        if ([status isEqualToString:@"play"]) {
            [[TKRoomManager instance] resumePlayMedia:self.mediaID];
        } else if ([status isEqualToString:@"pause"]) {
            [[TKRoomManager instance] pausePlayMedia:self.mediaID];
        } else {
            //close
            [self sessionManagerOnShareMediaState:self.iSessionHandle.localUser.peerID state:TKMedia_Unpulished extensionMessage:dic];
            [self.iMediaView removeFromSuperview];
            self.iMediaView = nil;
        }
    } else {
        if ([status isEqualToString:@"play"]) {
            [self sessionManagerOnShareMediaState:self.iSessionHandle.localUser.peerID state:TKMedia_Pulished extensionMessage:dic];
        } else if ([status isEqualToString:@"pause"]) {
            [self sessionManagerOnShareMediaState:self.iSessionHandle.localUser.peerID state:TKMedia_Pulished extensionMessage:dic];
        } else {
            //close
            [self sessionManagerOnShareMediaState:self.iSessionHandle.localUser.peerID state:TKMedia_Unpulished extensionMessage:dic];
            [self.iMediaView removeFromSuperview];
            self.iMediaView = nil;
            
        }
    }
    
    self.currentMediaURL = url;
}
- (CGRect)vidoeFrame: (NSDictionary *)position {
    // 计算课件位于白板中的位置
    CGFloat cX, cY, cW, cH;
    
    // 课件中视频的位置
    CGFloat x, y, w, h;
    
    if (self.coursewareRatio > self.whiteboardBackView.width / self.whiteboardBackView.height) {
        cW = self.whiteboardBackView.width;
        cH = cW / self.coursewareRatio;
        cX = self.whiteboardBackView.x;
        cY = (self.whiteboardBackView.height - cH) / 2;
        
        x  = cW * [position[@"left"] floatValue];
        y  = [position[@"top"] floatValue] * cH + self.whiteboardBackView.y + cY;
    }
    else {
        cH = self.whiteboardBackView.height;
        cW = cH * self.coursewareRatio;
        cY = 0;
        cX = (self.whiteboardBackView.width - cW) / 2;
        
        x  = cW * [position[@"left"] floatValue] + cX;;
        y  = cH * [position[@"top"] floatValue] + self.whiteboardBackView.y;
        
    }
    w = [position[@"width"] floatValue] * cW;
    h = [position[@"height"] floatValue] * cH;
    
    return CGRectMake(x, y, w, h);
}

- (void)closeMediaView {
    if (!self.iMediaView.isOutPlay) {
        return;
    }

    [self sessionManagerOnShareMediaState:@""
                                    state:TKMedia_Unpulished
                         extensionMessage:@{
                             @"source":@"h5Document"
                             
                         }
     ];
    [self.iMediaView removeFromSuperview];
    self.iMediaView = nil;
}
@end

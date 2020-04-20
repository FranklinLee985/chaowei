//
//  TKOneViewController+Playback.m
//  EduClass
//
//  Created by maqihan on 2018/11/21.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import "TKOneViewController+Playback.h"

@implementation TKOneViewController (Playback)

- (void)initPlaybackMaskView
{
    self.playbackMaskView = [[TKPlaybackMaskView alloc] initWithFrame:CGRectMake(0, self.navbarHeight, ScreenW, ScreenH- self.navbarHeight)];
    
    [self.view addSubview:self.playbackMaskView];
    
    //在播放录制件的时候 也能操作消息 但是不能发送消息
    CGRect frame = [self.chatViewNew convertRect:self.chatViewNew.leftBtn.frame toView:self.playbackMaskView];
    UIButton *button = [UIButton buttonWithType:UIButtonTypeSystem];
    button.frame = frame;
    [button addTarget:self action:@selector(buttonAction) forControlEvents:UIControlEventTouchUpInside];
    button.backgroundColor = [UIColor clearColor];
    [self.playbackMaskView insertSubview:button atIndex:0];

}

- (void)buttonAction
{
    [self.chatViewNew showMessageView:self.chatViewNew.leftBtn];
}

#pragma mark Playback

- (void)sessionManagerRoomManagerPlaybackMessageReceived:(NSString *)message
                                                  fromID:(NSString *)peerID
                                                      ts:(NSTimeInterval)ts
                                               extension:(NSDictionary *)extension{
    
    NSString *tDataString = [NSString stringWithFormat:@"%@",message];
    NSData *tJsData = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary * tDataDic = [NSJSONSerialization JSONObjectWithData:tJsData options:NSJSONReadingMutableContainers error:nil];
    
    NSNumber *type = [tDataDic objectForKey:@"type"];
    // 问题信息不显示 0 聊天， 1 提问
    if ([type integerValue] != 0) {
        return;
    }
    
    //自己发送的收不到
    if (!peerID) {
        peerID = self.iSessionHandle.localUser.peerID;
    }
    
    TKChatMessageType msgType = TKChatMessageTypeText;
    if ([[tDataDic allKeys] containsObject: @"msgtype"] &&
        [[tDataDic objectForKey:@"msgtype"] isEqualToString:@"onlyimg"]) {
        msgType = TKChatMessageTypeOnlyImage;
//        return;
    }
    
    NSString *msg = [tDataDic objectForKey:@"msg"];
    NSString *cospath = [tDataDic objectForKey:@"cospath"];
    NSString *tMyPeerId = self.iSessionHandle.localUser.peerID;
    NSString * nickNameStr = extension[@"nickname"];
    NSInteger role         = [extension[@"role"] intValue];
    if (nickNameStr.length == 0) {
        TKRoomUser * user = [self.iSessionHandle getUserWithPeerId:peerID];
        nickNameStr = user.nickName;
        role     = user.role;
    }
    
    BOOL isMe = [peerID isEqualToString:tMyPeerId];
    BOOL isTeacher =  role == TKUserType_Teacher?YES:NO;
    TKChatRoleType roleType = (isMe)?TKChatRoleTypeMe:(isTeacher?TKChatRoleTypeTeacher:TKChatRoleTypeOtherUer);

    TKChatMessageModel * tChatMessageModel = [[TKChatMessageModel alloc] initWithMsgType:msgType role:roleType message:msg cospath:cospath userName:nickNameStr fromid:peerID time:[TKUtil timestampToFormatString:ts]];
    [self.iSessionHandle addOrReplaceMessage:tChatMessageModel];
    
    [self.chatViewNew reloadData];
}

- (void)sessionManagerReceivePlaybackDuration:(NSTimeInterval)duration {
    //self.playbackMaskView.model.duration = duration;
    [self.playbackMaskView getPlayDuration:duration];
}

- (void)sessionManagerPlaybackUpdateTime:(NSTimeInterval)time {
    
    [self.playbackMaskView update:time];
}

- (void)sessionManagerPlaybackClearAll {
    

    [self.iSessionHandle clearMessageList];
    [self.iSessionHandle.whiteBoardManager resetWhiteBoardAllData];
    
    
    //播放回放时，进度条向回拖动，清理所有小工具
    [self clearAllData];
}

- (void)sessionManagerPlaybackEnd {
    [self.playbackMaskView playbackEnd];
}

@end

//
//  TKCTControlView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTControlView.h"
#import "TKEduSessionHandle.h"
#import "TKTrophyView.h"

#define ThemeKP(args) [@"ClassRoom.TKControlView." stringByAppendingString:args]

@interface TKCTControlView()
{
    CGFloat btnWidth;
    CGFloat btnHeight;
    CGFloat marginX;
    CGFloat marginY;
}
@property (nonatomic, strong) TKControlButton *allMuteBtn;//全体静音
@property (nonatomic, strong) TKControlButton *allSpeechesBtn;//全体发言
@property (nonatomic, strong) TKControlButton *allRewardBtn;//全体奖励
@property (nonatomic, strong) TKControlButton *allResetBtn;//全体复位

@end

@implementation TKCTControlView

- (id)initWithFrame:(CGRect)frame numberOfStuPlat:(NSInteger)numberOfStuPlat {
    
    if (self = [super initWithFrame:frame]) {
        
        // 标题
        self.titleText = TKMTLocalized(@"Title.ControlList");
        self.contentImageView.frame = CGRectMake(3, self.titleH, self.backImageView.width - 6, self.backImageView.height - self.titleH - 3);
        self.contentImageView.sakura.image(@"TKBaseView.base_bg_corner_2");

        btnHeight = self.contentImageView.frame.size.height/10.0*3.0;
        if (btnHeight<60) {
            btnHeight = 60;
        }
        btnWidth = btnHeight/10.0*6.0;
        marginY = (self.contentImageView.frame.size.height-btnHeight * 2)/3;
        marginX = (self.contentImageView.frame.size.width-btnWidth * 2)/3;
        
        
        [self loadAllBtn];
    }
    return self;
}
- (void)loadAllBtn{
    self.allMuteBtn = ({
        
        TKControlButton *button = [[TKControlButton alloc]initWithFrame:CGRectMake(marginX, marginY, btnWidth, btnHeight) imageName:ThemeKP(@"btn_jingyin") disableImageName:ThemeKP(@"btn_jingyin_unclickable") title:TKMTLocalized(@"Button.MuteAudio")];
        button.selected = NO;
        [self.contentImageView addSubview:button];
        [button controlAddTarget:self action:@selector(MuteButtonClick:)];
        button;
        
    });
    
    self.allSpeechesBtn = ({
        TKControlButton *button = [[TKControlButton alloc]initWithFrame:CGRectMake(self.allMuteBtn.rightX+ marginX, marginY, btnWidth, btnHeight) imageName:ThemeKP(@"btn_talk_all") disableImageName:ThemeKP(@"btn_talk_all_unclickable") title:TKMTLocalized(@"Button.MuteAll")];
        button.selected = NO;
        [self.contentImageView addSubview:button];
        [button controlAddTarget:self action:@selector(speecheButtonClick:)];
        button;
    });
    
    
    self.allRewardBtn = ({
        
        TKControlButton *button = [[TKControlButton alloc]initWithFrame:CGRectMake(self.allMuteBtn.leftX, self.allMuteBtn.bottomY + marginY, btnWidth, btnHeight) imageName:ThemeKP(@"btn_reward") disableImageName:ThemeKP(@"btn_reward_unclickable") title:TKMTLocalized(@"Button.Reward")];
        button.selected = NO;
        [self.contentImageView addSubview:button];
        [button controlAddTarget:self action:@selector(rewardButtonClick:)];
        button;
    });
    
    
    self.allResetBtn = ({
        
        TKControlButton *button = [[TKControlButton alloc]initWithFrame:CGRectMake(self.allRewardBtn.rightX + marginX, self.allRewardBtn.y, btnWidth, btnHeight) imageName:ThemeKP(@"button_restore") disableImageName:ThemeKP(@"button_restore_unclickable") title:TKMTLocalized(@"Button.Reset")];
        button.selected = NO;
        [self.contentImageView addSubview:button];
        [button controlAddTarget:self action:@selector(resetButtonClick:)];
        button;
    });
    
    if ([TKEduSessionHandle shareInstance].bigRoom) {
        self.allRewardBtn.enable = NO;
    }else{
        self.allRewardBtn.enable = YES;
    }
    
    [self refreshUI];
}

- (void)refreshUI{
    
    // 台上的学生人数，有一个是老师，（助教可以让老师下台，这个需要注意）
    NSInteger _numberOfStuPlat = [TKEduSessionHandle shareInstance].onPlatformNum - 1;
    if (_numberOfStuPlat <= 0) {
        self.allMuteBtn.enable = NO;
        self.allResetBtn.enable = NO;
        self.allRewardBtn.enable = NO;
        self.allSpeechesBtn.enable = NO;
    } else {
        
        self.allResetBtn.enable = YES;
        self.allRewardBtn.enable = YES;
        
        if([TKEduSessionHandle shareInstance].isAllMuteAudio){
            _allMuteBtn.enable = NO;
            _allSpeechesBtn.enable = YES;
        }else{
            _allMuteBtn.enable = YES;
            _allSpeechesBtn.enable = NO;
        }
    }
}

- (void)MuteButtonClick:(TKControlButton *)button{
    
    // 如果当前用户是老师
    if ([TKEduSessionHandle shareInstance].isAllMuteAudio == NO) {
        button.enable = NO;
        _allSpeechesBtn.enable = YES;
        for (TKRoomUser *tUser in [[TKEduSessionHandle shareInstance] userStdntAndTchrArray]) {
            
            if ((tUser.role != TKUserType_Student))
                continue;
            
            PublishState tState = (PublishState)tUser.publishState;
            if (tState == TKPublishStateBOTH) {
                tState = TKPublishStateVIDEOONLY;
            }else if(tState == TKPublishStateAUDIOONLY){
                tState = TKPublishStateNONEONSTAGE;
            }
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:tUser.peerID Publish:tState completion:nil];
        }
        [TKEduSessionHandle shareInstance].isAllMuteAudio = YES;
    }

    [self dismissAlert];
}

- (void)speecheButtonClick:(TKControlButton *)button{
    //全体发言
//    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
//        // 如果当前用户是学生
//        [[TKEduSessionHandle shareInstance] disableMyAudio:button.selected];
//
//        // 如果禁用音视频，已经举手，举起的手要放下
//        BOOL handState = [[[TKEduSessionHandle shareInstance].localUser.properties objectForKey:sRaisehand] boolValue];
//        if (handState == YES) {
//            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:[TKEduSessionHandle shareInstance].localUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(!handState) completion:nil];
//            if (!handState) {
//                if ([TKEduSessionHandle shareInstance].localUser.publishState > 0) {
//                } else {
//                }
//            } else {
//            }
//        }
//
//        button.selected = !button.selected;
//
//    } else {
    // 如果当前用户是老师
    if ([TKEduSessionHandle shareInstance].isunMuteAudio == NO) {
        
        button.enable = NO;
        _allMuteBtn.enable = YES;
        
        for (TKRoomUser *tUser in [[TKEduSessionHandle shareInstance] userStdntAndTchrArray]) {
            
            if ((tUser.role != TKUserType_Student))
                continue;
            
            TKPublishState tState = tUser.publishState;
            if (tState == TKPublishStateNONEONSTAGE) {
                tState = TKUser_PublishState_AUDIOONLY;
            }
            else if(tState == TKPublishStateVIDEOONLY){
                tState = TKUser_PublishState_BOTH;
            }
            
            if (tState > TKUser_PublishState_NONE) {
                
                [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:tUser.peerID Publish:tState completion:nil];
            }
        }
        [TKEduSessionHandle shareInstance].isunMuteAudio = YES;
        
        
    }
        
//    }
    [self dismissAlert];
}
#pragma mark - 全员奖励
- (void)rewardButtonClick:(TKControlButton *)button{
    if ([TKEduSessionHandle shareInstance].bigRoom) {
        return;
    }
    
    [self dismissAlert];

    NSArray *arr = [[TKEduClassRoom shareInstance].roomJson.trophy copy];
    BOOL customTrophyFlag = [TKEduClassRoom shareInstance].roomJson.configuration.customTrophyFlag;
    
    //当有一个自定义奖杯时候 不需要弹出框 直接发送奖杯
    if (arr.count==1 && customTrophyFlag) {
        
        [TKEduNetManager sendGifForRoomUser:[[TKEduSessionHandle shareInstance] userStdntAndTchrArray]
                                     roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                    aMySelf:[TKEduSessionHandle shareInstance].localUser
                                      aHost:sHost
                                      aPort:sPort
                              aSendComplete:^(id  _Nullable response) {
                                  
                                  for (TKRoomUser *tUser in [[TKEduSessionHandle shareInstance] userStdntAndTchrArray]) {
                                      
                                      
                                      int currentGift = 0;
                                      
                                      if(tUser && tUser.properties && [tUser.properties objectForKey:sGiftNumber]){
                                          
                                          currentGift = [[tUser.properties objectForKey:sGiftNumber] intValue];
                                          
                                          
                                      }
                                      
                                      
                                      NSDictionary *dict = @{
                                                             @"giftnumber":@(currentGift+1),
                                                             @"giftinfo":[arr firstObject],
                                                             };
                                      
                                      
                                      [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:tUser.peerID TellWhom:sTellAll data:dict completion:nil];
                                      
                                  }
                                  
                              }aNetError:nil];
        return;
        
    }
    

    
    
    if (arr.count>1 && customTrophyFlag) {
        
        UIView *whiteBoardView = [TKEduSessionHandle shareInstance].whiteboardView;
        
        CGRect wbRect = [whiteBoardView convertRect:whiteBoardView.bounds toView:[UIApplication sharedApplication].keyWindow];
        
//        CGFloat wbHeight = wbRect.size.height;
        CGFloat wbWidth = wbRect.size.width;
        
        CGPoint relyPoint = CGPointMake(wbRect.origin.x + wbRect.size.width / 2, wbRect.origin.y + wbRect.size.height/2);
        
        //自定义奖杯弹框： 宽 5/10
        CGFloat trophyW = fmaxf(wbWidth * (5.0/10.0), 275);
        
        // 高 9/10(改： 根据按钮数量给高度)
        CGFloat trophyH = trophyW / 3 ;
        trophyH = (arr.count / 5 + 1) * trophyH + 20;
        CGFloat trophyX = relyPoint.x - trophyW/2;
        CGFloat trophyY = relyPoint.y - trophyH/2;
        
        TKTrophyView *trophyView =[[TKTrophyView alloc]initWithFrame:CGRectMake(trophyX, trophyY, trophyW, trophyH) chatController:@""];
        
        [trophyView showOnView:self trophyMessage:arr];
        
        
        trophyView.sendTrophy = ^(NSDictionary *message) {
            
            // 如果当前用户是老师
            [TKEduNetManager sendGifForRoomUser:[[TKEduSessionHandle shareInstance] userStdntAndTchrArray]
                                         roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                        aMySelf:[TKEduSessionHandle shareInstance].localUser
                                          aHost:sHost
                                          aPort:sPort
                                  aSendComplete:^(id  _Nullable response) {
                
                for (TKRoomUser *tUser in [[TKEduSessionHandle shareInstance] userStdntAndTchrArray]) {
                    
                    
                    int currentGift = 0;
                    
                    if(tUser && tUser.properties && [tUser.properties objectForKey:sGiftNumber]){
                        
                        currentGift = [[tUser.properties objectForKey:sGiftNumber] intValue];
                        
                        
                    }
                    
                    
                    NSDictionary *dict = @{
                                           @"giftnumber":@(currentGift+1),
                                           @"giftinfo":message,
                                           };
                    
                    
                    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:tUser.peerID TellWhom:sTellAll data:dict completion:nil];

                }
                
            }aNetError:nil];
            
        };

        return;
    }else{
        
        // 如果当前用户是老师
        [TKEduNetManager sendGifForRoomUser:[[TKEduSessionHandle shareInstance] userStdntAndTchrArray]
                                     roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                    aMySelf:[TKEduSessionHandle shareInstance].localUser
                                      aHost:sHost
                                      aPort:sPort
                              aSendComplete:^(id  _Nullable response) {
            
            for (TKRoomUser *tUser in [[TKEduSessionHandle shareInstance] userStdntAndTchrArray]) {
                int currentGift = 0;
                if ((tUser.role != TKUserType_Student))
                    continue;
                
                if(tUser && tUser.properties && [tUser.properties objectForKey:sGiftNumber])
                    currentGift = [[tUser.properties objectForKey:sGiftNumber] intValue];
                [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:tUser.peerID TellWhom:sTellAll Key:sGiftNumber Value:@(currentGift+1) completion:nil];
            }
            
        }aNetError:nil];
        
    }
    
    
    
}

- (void)resetButtonClick:(TKControlButton *)button{
    if (self.resetBlock) {
        self.resetBlock();
    }
    
    [self dismissAlert];
}


@end



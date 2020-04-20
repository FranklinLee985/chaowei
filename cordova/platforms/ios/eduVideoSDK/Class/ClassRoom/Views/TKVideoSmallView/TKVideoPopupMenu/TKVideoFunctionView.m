//
//  TKVideoFunctionView.m
//  EduClassPad
//
//  Created by ifeng on 2017/6/15.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKVideoFunctionView.h"
#import "TKButton.h"
#import "TKEduSessionHandle.h"


#define ThemeKP(args) [@"ClassRoom.TKVideoView." stringByAppendingString:args]

@interface TKVideoFunctionView ()
@property (nonatomic,retain)TKButton *iButton1;
@property (nonatomic,retain)TKButton *iButton2;
@property (nonatomic,retain)TKButton *iButton3;
@property (nonatomic,retain)TKButton *iButton4;
@property (nonatomic,retain)TKButton *iButton5;
@property (nonatomic,retain)TKButton *iButton6;
@property (nonatomic,retain)TKButton *iButton7;// 恢复位置
@property (nonatomic,retain)TKButton *iButton8;// 全部恢复
@property (nonatomic,strong)TKButton *exchangeBtn; // 切换布局
@end

@implementation TKVideoFunctionView


-(instancetype)initWithFrame:(CGRect)frame
                    aRoomUer:(TKRoomUser*)aRoomUer
                     isSplit:(BOOL)isSplit
                   isSpeaker:(BOOL)isSpeaker
                       count:(CGFloat)count {
    
    if (self = [super initWithFrame:frame]) {
        
        _iRoomUer = aRoomUer;
        _isSpeaker = isSpeaker;
        
        CGFloat tHeight = CGRectGetHeight(frame);
        
        //默认按钮个数为0
        CGFloat tPoroFloat = count;
        CGFloat tWidth = (CGRectGetWidth(frame)-20)/tPoroFloat;
       
        // 涂鸦授权
        _iButton1 = ({
        
            TKButton *tButton = [self returninitFrame:CGRectMake(10, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"videoPopGraffitiSelectedImage") selectImageName:ThemeKP(@"videoPopGraffitiNomalImage") title:TKMTLocalized(@"Button.CancelDoodle") selectTitle:TKMTLocalized(@"Button.AllowDoodle") action:@selector(button1Clicked:) selected:aRoomUer.canDraw];
            
            tButton;
        
        });
        // 上下台控制
        _iButton2 = ({
            
            TKButton *tButton = [self returninitFrame:CGRectMake(10+tWidth, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_control_down") selectImageName:ThemeKP(@"icon_control_up") title:TKMTLocalized(@"Button.DownPlatform") selectTitle:TKMTLocalized(@"Button.UpPlatform") action:@selector(button2Clicked:) selected:(aRoomUer.publishState != TKPublishStateNONE)];
            
            
            tButton;
            
        });
        // 未启动视频
        if (aRoomUer.disableVideo == NO && [TKEduSessionHandle shareInstance].isOnlyAudioRoom == NO) {
            _iButton5 = ({
                
                BOOL isSelected = (aRoomUer.publishState == TKPublishStateBOTH) || (aRoomUer.publishState == TKPublishStateVIDEOONLY);
                
                TKButton *tButton = [self returninitFrame:CGRectMake(10+tWidth*3, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_control_camera_02") selectImageName:ThemeKP(@"icon_control_camera_01") title:TKMTLocalized(@"Button.CloseVideo") selectTitle:TKMTLocalized(@"Button.OpenVideo") action:@selector(button5Clicked:) selected:isSelected];
               
                
                tButton;
                
            });
        }
        // 未启动音频
        if (aRoomUer.disableAudio == NO) {
            
            _iButton3 = ({
                
                BOOL isSelected = (aRoomUer.publishState == TKPublishStateBOTH) || (aRoomUer.publishState == TKPublishStateAUDIOONLY);
                
                TKButton *tButton = [self returninitFrame:CGRectMake((aRoomUer.disableVideo?tWidth*1:tWidth*2)+10 , 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_close_audio") selectImageName:ThemeKP(@"icon_open_audio") title:TKMTLocalized(@"Button.CloseAudio") selectTitle:TKMTLocalized(@"Button.OpenAudio") action:@selector(button3Clicked:) selected:isSelected];

                // 不显示关闭视频按钮，减一个位置
                tButton;
                
            });
        }
		//发送奖杯
        _iButton4= ({
            
            CGFloat x = 0;
            if (aRoomUer.disableAudio == YES) {
                if (aRoomUer.disableVideo == YES || [TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
                    x = tWidth * 2;
                } else {
                    x = tWidth * 3;
                }
            } else {
                if (aRoomUer.disableVideo == YES || [TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
                    x = tWidth * 3;
                } else {
                    x = tWidth * 4;
                }
            }
            
            TKButton *tButton = [self returninitFrame:CGRectMake(10+x, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_control_gift")
                                      selectImageName:ThemeKP(@"icon_control_gift") title:TKMTLocalized(@"Button.GiveCup") selectTitle:TKMTLocalized(@"Button.GiveCup") action:@selector(button4Clicked:) selected:NO];
            
            tButton;
            
        });
       
        _iButton6 = ({//演讲
            
            TKButton *tButton = [self returninitFrame:CGRectMake(10+tWidth*5, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_screen_normal") selectImageName:ThemeKP(@"icon_reply_normal_only") title:TKMTLocalized(@"Button.Speech") selectTitle:TKMTLocalized(@"Button.Recovery") action:@selector(button6Clicked:) selected:NO];
            
            tButton;
            
        });
        
        _iButton7 = ({//恢复位置
            
            TKButton *tButton = [self returninitFrame:CGRectMake(10+tWidth*6, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"tk_reset_mine") selectImageName:ThemeKP(@"tk_reset_mine") title:TKMTLocalized(@"Button.RestorePosition") selectTitle:TKMTLocalized(@"Button.RestorePosition") action:@selector(button7Clicked:) selected:NO];
            tButton;
        });
        
        _iButton8 = ({//全部恢复
            
            TKButton *tButton = [self returninitFrame:CGRectMake(10+tWidth*7, 0, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"tk_reset_all") selectImageName:ThemeKP(@"tk_reset_all") title:TKMTLocalized(@"Button.RestoreAll") selectTitle:TKMTLocalized(@"Button.RestoreAll") action:@selector(button8Clicked:) selected:NO];
            tButton;
        });

        
        _exchangeBtn = [self returninitFrame:CGRectMake(10+tWidth*8, 0, tWidth, tHeight)
                                 buttonWidth:tWidth
                                     tHeight:tHeight
                                   imageName:ThemeKP(@"tk_video_change_default")
                             selectImageName:ThemeKP(@"tk_video_change_default")
                                       title:TKMTLocalized(@"Button.SwitchVideo")
                                 selectTitle:TKMTLocalized(@"Button.SwitchVideo")
                                      action:@selector(exchangeBtnClicked)
                                    selected:NO];
        
//        ============ 老师 ===============
        if (_iRoomUer.role == TKUserType_Teacher) {
            
            NSInteger num = 1;
            _iButton3.frame = CGRectMake(10, 0, tWidth, tHeight);
            [self addSubview:_iButton3];
            
            if ([TKEduSessionHandle shareInstance].isOnlyAudioRoom == NO) {
                _iButton5.frame = CGRectMake(10+tWidth * num, 0, tWidth, tHeight);
                [self addSubview:_iButton5];
                num += 1;
            }

            if (isSplit) {
                _iButton7.frame = CGRectMake(10+tWidth*num, 0, tWidth, tHeight);
                _iButton8.frame = CGRectMake(_iButton7.rightX, 0, tWidth, tHeight);
                [self addSubview:_iButton7];
                [self addSubview:_iButton8];
                num += 2;
            } else {
                
                if ([TKEduClassRoom shareInstance].roomJson.roomtype != TKRoomTypeOneToOne || [TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish) {
                    // 全体复位
                    _iButton8.frame = CGRectMake(10 + tWidth * num, 0, tWidth, tHeight);
                    [self addSubview:_iButton8];
                    num += 1;
                }
            }

            if ([TKEduSessionHandle shareInstance].roomLayout == MainPeople ||
                [TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision) {

                if (self.isSpeaker == NO) {

                    if (self.iRoomUer.role == TKUserType_Teacher) {
                        _exchangeBtn.x = 10+tWidth*num;
                        [self addSubview:_exchangeBtn];
                        self.width = frame.size.width + 60;

                    }else{
                        _exchangeBtn.x = 10+tWidth*num;
                        [self addSubview:_exchangeBtn];
                        self.width = frame.size.width + 60;

                    }
                }
            }
           
        }
        else if ([aRoomUer.peerID isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID]) {
            _iButton3.frame = CGRectMake(10, 0, tWidth, tHeight);
            _iButton5.frame = CGRectMake(10+tWidth, 0, tWidth, tHeight);
            [self addSubview:_iButton5];
            [self addSubview:_iButton3];

        }
        else if (aRoomUer.role == TKUserType_Assistant && _iRoomUer.role == TKUserType_Assistant) {
            _iButton2.frame = CGRectMake(10, 0, tWidth, tHeight);
            _iButton3.frame = CGRectMake(10+tWidth, 0, tWidth, tHeight);
            _iButton5.frame = CGRectMake(10+2*tWidth, 0, tWidth, tHeight);

            [self addSubview:_iButton2];
            [self addSubview:_iButton3];
            [self addSubview:_iButton5];

            if (isSplit) {
                _iButton7.frame = CGRectMake(_iButton5 ? _iButton5.rightX : _iButton3.rightX, 0, tWidth, tHeight);
                [self addSubview:_iButton7];
            }

        }
        else {
            
            [self addSubview:_iButton1];
            
            if ([TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomTypeOneToOne &&
                [TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish == NO) {
                
                _iButton3.x = _iButton1.rightX;
                _iButton5.x = _iButton3.rightX;
                _iButton4.x = _iButton5.rightX;
            } else {
                [self addSubview:_iButton2];
            }
            
            [self addSubview:_iButton3];
            [self addSubview:_iButton5];
            [self addSubview:_iButton4];
            
            _iButton4.hidden = NO;

            
            
            CGFloat x = !_iButton4.hidden ? _iButton4.rightX : (_iButton5 ? _iButton5.rightX : _iButton3.rightX);
            if (isSplit) {
                _iButton7.frame = CGRectMake(x, 0, tWidth, tHeight);
                [self addSubview:_iButton7];
                x = _iButton7.rightX;
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher)
            {
                if ([TKEduSessionHandle shareInstance].roomLayout == MainPeople ||
                    [TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision) {
                    
                    if (self.isSpeaker == NO) {
                        _exchangeBtn.x = x;
                        self.width = frame.size.width + 60;
                        [self addSubview:_exchangeBtn];
                    }
                }
            }
        }

    }
    
    
    return self;
    
}

- (TKButton *)returninitFrame:(CGRect)frame buttonWidth:(CGFloat)tWidth tHeight:(CGFloat)tHeight imageName:(NSString *)imageName selectImageName:(NSString *)selectImageName title:(NSString *)title selectTitle:(NSString *)selectTitle action:(SEL)action  selected:(BOOL)selected{
    
    TKButton *tButton = [TKButton buttonWithType:UIButtonTypeCustom];
    tButton.sakura.image(imageName,UIControlStateNormal);
    [tButton setTitle:title forState:UIControlStateNormal];
    tButton.sakura.image(selectImageName,UIControlStateSelected);
    [tButton setTitle:selectTitle forState:UIControlStateSelected];
    tButton.titleLabel.font = TKFont(9);
    tButton.sakura.titleColor(ThemeKP(@"videoToolTextColor"),UIControlStateNormal);
    
    tButton.titleLabel.textAlignment =NSTextAlignmentCenter;
    //修改部分2
    tButton.imageRect = CGRectMake((tWidth-20)/2.0, (tHeight-50)/2.0, 20, 20);
    tButton.titleRect = CGRectMake(0, tHeight-30, tWidth, 20);
    tButton.contentMode = UIViewContentModeCenter;
    [tButton addTarget:self action:action forControlEvents:(UIControlEventTouchUpInside)];
    tButton.frame = frame;
    tButton.selected = selected;
    return tButton;
    
}

-(void)button1Clicked:(UIButton *)tButton{
   
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallbutton1:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallbutton1:tButton];
    }
    
}
-(void)button2Clicked:(UIButton *)tButton{
   
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton2:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton2:tButton];
    }
    
}
-(void)button3Clicked:(UIButton *)tButton{
   
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton3:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton3:tButton];
    }
}
-(void)button4Clicked:(UIButton *)tButton{
   
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton4:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton4:tButton];
    }
    
}
-(void)button5Clicked:(UIButton *)tButton{
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton5:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton5:tButton];
    }
}
-(void)button6Clicked:(UIButton *)tButton{
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton6:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton6:tButton];
    }
}

-(void)button7Clicked:(UIButton *)tButton{
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton7:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton7:tButton];
    }
}
-(void)button8Clicked:(UIButton *)tButton{
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton8:)]) {
        [(id<VideolistProtocol>)_iDelegate videoSmallButton8:tButton];
    }
}

- (void)exchangeBtnClicked
{
    if ([self.iDelegate respondsToSelector:@selector(didPressChangeButton)]) {
        [self.iDelegate didPressChangeButton];
    }
}

@end

//
//  TKVideoVerticalFunctionView.m
//  EduClass
//
//  Created by lyy on 2018/5/10.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKVideoVerticalFunctionView.h"
#import "TKButton.h"
#import "TKEduSessionHandle.h"


#define ThemeKP(args) [@"ClassRoom.TKVideoView." stringByAppendingString:args]

@interface TKVideoVerticalFunctionView()

@property (nonatomic,retain)TKButton *iButton1;
@property (nonatomic,retain)TKButton *iButton2;
@property (nonatomic,retain)TKButton *iButton3;
@property (nonatomic,retain)TKButton *iButton4;
@property (nonatomic,retain)TKButton *iButton5;
@property (nonatomic,retain)TKButton *iButton6; // 双师切换视频位置
@end

@implementation TKVideoVerticalFunctionView

//展示从底部向上弹出的UIView（包含遮罩）
-(instancetype)initWithFrame:(CGRect)frame aRoomUer:(TKRoomUser*)aRoomUer isSplit:(BOOL)isSplit count:(CGFloat)count{
    
    if (self = [super initWithFrame:frame]) {
        
        _iRoomUer = aRoomUer;
        
        //默认按钮个数为0
        CGFloat tHeight = CGRectGetHeight(frame);
        CGFloat tWidth  = tHeight;//CGRectGetWidth(frame);
        float varY = 10;
        //涂鸦授权
        _iButton1 = ({
            
            TKButton *tButton = [self returninitFrame:CGRectMake(0, varY, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_control_tools_02") selectImageName:ThemeKP(@"icon_control_tools_01") title:TKMTLocalized(@"Button.CancelDoodle") selectTitle:TKMTLocalized(@"Button.AllowDoodle") action:@selector(button1Clicked:) selected:aRoomUer.canDraw];
            
            varY += tHeight;
            
            tButton;
            
        });
        //关闭音频 _iButton3
        if (aRoomUer.disableAudio == NO) {
            
            _iButton3 = ({
                
                BOOL isSelected = (aRoomUer.publishState == TKPublishStateBOTH) || (aRoomUer.publishState == TKPublishStateAUDIOONLY);
                
                TKButton *tButton = [self returninitFrame:CGRectMake(0 , varY, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_close_audio") selectImageName:ThemeKP(@"icon_open_audio") title:TKMTLocalized(@"Button.CloseAudio") selectTitle:TKMTLocalized(@"Button.OpenAudio") action:@selector(button3Clicked:) selected:isSelected];
                
                varY += tHeight;
                tButton;
                
            });
        }
        //关闭视频 _iButton5
        if (aRoomUer.disableVideo == NO && [TKEduSessionHandle shareInstance].isOnlyAudioRoom == NO) {
            _iButton5 = ({
                
                BOOL isSelected = (aRoomUer.publishState == TKPublishStateBOTH) || (aRoomUer.publishState == TKPublishStateVIDEOONLY);
                
                TKButton *tButton = [self returninitFrame:CGRectMake(0, varY, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_control_camera_02") selectImageName:ThemeKP(@"icon_control_camera_01") title:TKMTLocalized(@"Button.CloseVideo") selectTitle:TKMTLocalized(@"Button.OpenVideo") action:@selector(button5Clicked:) selected:isSelected];
                
                
                tButton;
                
            });
        }
        
        //发送奖杯
        _iButton4= ({
            
            CGFloat y = 0;
            if (aRoomUer.disableAudio == YES) {
                if (aRoomUer.disableVideo == YES || [TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
                    y = tHeight * 1;
                } else {
                    y = tHeight * 2;
                }
            } else {
                if (aRoomUer.disableVideo == YES || [TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
                    y = tHeight * 2;
                } else {
                    y = tHeight * 3;
                }
            }
            y += 10;
            TKButton *tButton = [self returninitFrame:CGRectMake(0, y, tWidth, tHeight) buttonWidth:tWidth tHeight:tHeight imageName:ThemeKP(@"icon_control_gift") selectImageName:ThemeKP(@"icon_control_gift") title:TKMTLocalized(@"Button.GiveCup") selectTitle:TKMTLocalized(@"Button.GiveCup") action:@selector(button4Clicked:) selected:NO];
            
            tButton;
            
        });
        // 双师切换视频
        _iButton6= ({

            float y = tHeight * 2 + 10;
            TKButton *tButton = [self returninitFrame:CGRectMake(0, y, tWidth, tHeight)
                                          buttonWidth:tWidth
                                              tHeight:tHeight
                                            imageName:ThemeKP(@"tk_video_change_default")
                                      selectImageName:ThemeKP(@"tk_video_change_default")
                                                title:TKMTLocalized(@"Button.SwitchVideo")
                                          selectTitle:TKMTLocalized(@"Button.SwitchVideo")
                                               action:@selector(button6Clicked:)
                                             selected:NO];
            
            tButton;
            
        });
        
        if (aRoomUer.role == TKUserType_Teacher) {
            
            CGFloat y = 10;
            _iButton3.frame = CGRectMake(0, 10, tWidth, tHeight);
            [self addSubview:_iButton3];
            y = _iButton3.bottomY;
            
            if ([TKEduSessionHandle shareInstance].isOnlyAudioRoom == NO) {
                _iButton5.frame = CGRectMake(0, y, tWidth, tHeight);
                [self addSubview:_iButton5];
                y = _iButton5.bottomY;
            }
            
            // 双师添加
            if ([TKEduSessionHandle shareInstance].roomLayout == MainPeople || [TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision) {
                [self addSubview:_iButton6];
                _iButton6.y = y;
            }
            
        }
        // 学生点击自己
        else if ([aRoomUer.peerID isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID] &&
                 aRoomUer.role != TKUserType_Teacher) {
            _iButton3.frame = CGRectMake(0, 10, tWidth, tHeight);
            _iButton5.frame = CGRectMake(0, 10 + tHeight, tWidth, tHeight);
            
            [self addSubview:_iButton5];
            [self addSubview:_iButton3];
 
            // 双师添加
            if ([TKEduSessionHandle shareInstance].roomLayout == MainPeople || [TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision) {
                _iButton6.y = _iButton5.bottomY;
                [self addSubview:_iButton6];
            }
        }
        else if (aRoomUer.role == TKUserType_Assistant) {
            _iButton2.frame = CGRectMake(0, 10, tWidth, tHeight);
            _iButton3.frame = CGRectMake(0, 10 + tHeight, tWidth, tHeight);
            _iButton5.frame = CGRectMake(0, 10 + 2*tHeight, tWidth, tHeight);
            
            [self addSubview:_iButton2];
            [self addSubview:_iButton3];
            [self addSubview:_iButton5];
            
        }
        else {
            
            [self addSubview:_iButton1];
            [self addSubview:_iButton2];
            [self addSubview:_iButton3];
            [self addSubview:_iButton5];
            [self addSubview:_iButton4];
            // 双师添加
            if ([TKEduSessionHandle shareInstance].roomLayout == MainPeople || [TKEduSessionHandle shareInstance].roomLayout == oneToOneDoubleDivision) {
                _iButton6.y = _iButton4.bottomY;
                [self addSubview:_iButton6];
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
        [(id<VideoVlistProtocol>)_iDelegate videoSmallbutton1:tButton];
    }
    
}
-(void)button2Clicked:(UIButton *)tButton{
    
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton2:)]) {
        [(id<VideoVlistProtocol>)_iDelegate videoSmallButton2:tButton];
    }
    
}
-(void)button3Clicked:(UIButton *)tButton{
    
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton3:)]) {
        [(id<VideoVlistProtocol>)_iDelegate videoSmallButton3:tButton];
    }
}
-(void)button4Clicked:(UIButton *)tButton{
    
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton4:)]) {
        [(id<VideoVlistProtocol>)_iDelegate videoSmallButton4:tButton];
    }
    
}
-(void)button5Clicked:(UIButton *)tButton{
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton5:)]) {
        [(id<VideoVlistProtocol>)_iDelegate videoSmallButton5:tButton];
    }
}
-(void)button6Clicked:(UIButton *)tButton{
    if (_iDelegate && [_iDelegate respondsToSelector:@selector(videoSmallButton6:)]) {
        [(id<VideoVlistProtocol>)_iDelegate videoSmallButton6:tButton];
    }
}


//移除从上向底部弹下去的UIView（包含遮罩）
- (void)dissMissView
{
    
    [UIView animateWithDuration:0.3f
                     animations:^{
                         
                         self.alpha = 0.0;
                         
                         
                     }
                     completion:^(BOOL finished){
                         
                         [self removeFromSuperview];
                         
                     }];
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

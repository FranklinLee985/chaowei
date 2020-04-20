//
//  TKVideoFunctionView.h
//  EduClassPad
//
//  Created by ifeng on 2017/6/15.
//  Copyright © 2017年 beijing. All rights reserved.
//  横向弹出

#import <UIKit/UIKit.h>
@protocol VideolistProtocol <NSObject>

-(void)videoSmallbutton1:(UIButton *)aButton;
-(void)videoSmallButton2:(UIButton *)aButton;
-(void)videoSmallButton3:(UIButton *)aButton;
-(void)videoSmallButton4:(UIButton *)aButton;
-(void)videoSmallButton5:(UIButton *)aButton;
-(void)videoSmallButton6:(UIButton *)aButton;
-(void)videoSmallButton7:(UIButton *)aButton;
-(void)videoSmallButton8:(UIButton *)aButton;

-(void)didPressChangeButton;

@end
@interface TKVideoFunctionView : UIView

@property (nonatomic,weak)id<VideolistProtocol>iDelegate;
@property (nonatomic,strong)TKRoomUser *iRoomUer;

@property (assign, nonatomic) BOOL isSpeaker;


-(instancetype)initWithFrame:(CGRect)frame
                    aRoomUer:(TKRoomUser*)aRoomUer
                     isSplit:(BOOL)isSplit
                   isSpeaker:(BOOL)isSpeaker
                       count:(CGFloat)count;


@end

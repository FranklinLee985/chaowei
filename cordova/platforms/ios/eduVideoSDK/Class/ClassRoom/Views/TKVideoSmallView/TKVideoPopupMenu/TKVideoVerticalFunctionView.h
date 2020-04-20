//
//  TKVideoVerticalFunctionView.h
//  EduClass
//
//  Created by lyy on 2018/5/10.
//  Copyright © 2018年 talkcloud. All rights reserved.
//  纵向弹出

#import <UIKit/UIKit.h>


@protocol VideoVlistProtocol <NSObject>


-(void)videoSmallbutton1:(UIButton *)aButton;
-(void)videoSmallButton2:(UIButton *)aButton;
-(void)videoSmallButton3:(UIButton *)aButton;
-(void)videoSmallButton4:(UIButton *)aButton;
-(void)videoSmallButton5:(UIButton *)aButton;
-(void)videoSmallButton6:(UIButton *)aButton;


@end
@interface TKVideoVerticalFunctionView : UIView

@property (nonatomic,weak)id<VideoVlistProtocol>iDelegate;
@property (nonatomic,strong)TKRoomUser *iRoomUer;

-(instancetype)initWithFrame:(CGRect)frame aRoomUer:(TKRoomUser*)aRoomUer isSplit:(BOOL)isSplit count:(CGFloat)count;


@end

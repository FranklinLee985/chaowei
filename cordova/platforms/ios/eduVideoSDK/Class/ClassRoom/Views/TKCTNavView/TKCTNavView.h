//
//  TKCTNavView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/10.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKCTNetTipView.h"

@interface TKCTNavView : UIView

@property (nonatomic, copy) void(^leaveButtonBlock)();
@property (nonatomic, copy) void(^classoverBlock)();
@property (nonatomic, copy) void(^classBeginBlock)();


@property (nonatomic, strong) UIButton *memberButton;//成员列表
@property (nonatomic, strong) UIButton *coursewareButton;//资料库
@property (nonatomic, strong) UIButton *toolBoxButton;// 工具箱
@property (nonatomic, strong) UIButton *controlButton;//全员操作按钮
@property (nonatomic, strong) UIButton *upLoadButton;//上传按钮
@property (nonatomic, strong) UIButton *beginAndEndClassButton;//上课按钮

@property (nonatomic, strong) TKCTNetTipView * netTipView;

@property (nonatomic, copy) void(^memberButtonClickBlock)(UIButton * sender);
@property (nonatomic, copy) void(^coursewareButtonClickBlock)(UIButton * sender);
@property (nonatomic, copy) void(^toolBoxButtonClickBlock)(UIButton * sender);
@property (nonatomic, copy) void(^controlButtonClickBlock)(UIButton * sender);
@property (nonatomic, copy) void(^styleButtonClickBlock)(UIButton * sender);

@property (nonatomic, copy) void(^netStateBlock)(CGFloat centerX);

@property (nonatomic, assign) BOOL showRedDot;

- (instancetype)initWithFrame:(CGRect)frame aParamDic:(NSDictionary *)aParamDic;

- (void)setTime:(NSTimeInterval)time;
- (void)refreshUI:(BOOL)add;
- (void)buttonRefreshUI;
- (void)setHandButtonState:(BOOL)isHandup;
/**
 隐藏所有控制按钮
 */
- (void)hideAllButton:(BOOL)hide;
- (void)updateView:(NSDictionary *)message;
- (void)destory;
- (void)showHandsupTips:(BOOL)show;
@end

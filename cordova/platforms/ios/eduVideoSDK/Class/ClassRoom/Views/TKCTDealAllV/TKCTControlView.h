//
//  TKCTControlView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//  全体控制

#import "TKCTBaseView.h"
#import "TKControlButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKCTControlView : TKCTBaseView

- (id)initWithFrame:(CGRect)frame numberOfStuPlat:(NSInteger)numberOfStuPlat;

@property(nonatomic,copy) void(^ _Nullable resetBlock)(void);//分屏回调

- (void)refreshUI;

//全体静音
- (void)MuteButtonClick:(TKControlButton *)button;

//全体发言
- (void)speecheButtonClick:(TKControlButton *)button;

//全体奖励
- (void)rewardButtonClick:(TKControlButton *)button;

//全体复位
- (void)resetButtonClick:(TKControlButton *)button;

@end

NS_ASSUME_NONNULL_END

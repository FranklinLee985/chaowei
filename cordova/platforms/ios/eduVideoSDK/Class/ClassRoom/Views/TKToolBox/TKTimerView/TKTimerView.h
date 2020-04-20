//
//  TKTimerView.h
//  EduClass
//
//  Created by Evan on 2019/1/8.
//  Copyright © 2019 talkcloud. All rights reserved.
//	老师计时器

#import "TKBaseBackgroundView.h"
@class TKTimeCountdownView;

NS_ASSUME_NONNULL_BEGIN

@interface TKTimerView : TKBaseBackgroundView

@property (nonatomic, strong) TKTimeCountdownView *timeCountdownView;
- (void)startTimerCountdownWithMinute:(long)minute second:(long)second receiveMsgTime:(long)time;
// 接受信令时的暂停
- (void)pauseTimerWithTimerArray:(NSArray *)array;
#pragma mark - 停止 -- 重新开始
- (void)stopCountDown;

@end

NS_ASSUME_NONNULL_END

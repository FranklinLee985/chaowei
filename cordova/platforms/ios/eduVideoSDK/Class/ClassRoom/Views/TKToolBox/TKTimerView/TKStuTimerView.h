//
//  TKStuTimerView.h
//  EduClass
//
//  Created by Evan on 2019/1/10.
//  Copyright © 2019 talkcloud. All rights reserved.
//  学生计时器

#import "TKBaseBackgroundView.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKStuTimerView : TKBaseBackgroundView

// 开始倒计时
- (void)startCountdownWithMinute:(long)minute second:(long)second receiveMsgTime:(long)time;
// 暂停
- (void)pauseTimerWithTimerArray:(NSArray *)array;

@end

NS_ASSUME_NONNULL_END

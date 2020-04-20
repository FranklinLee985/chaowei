//
//  TKTimeCountdownView.h
//  EduClass
//
//  Created by Evan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@protocol TKCountDownDelegate <NSObject>

- (void)stopCountDownButtonAction;

@end

@interface TKTimeCountdownView : UIView

@property (nonatomic, weak) id<TKCountDownDelegate> delegate;

@property (nonatomic) long restratMinute;// 暂停开始时的分钟
@property (nonatomic) long restartSecond;// 暂停开始时的秒

// l开始倒计时
- (void)startCountdownWithMinute:(long)minute second:(long)second receiveMsgTime:(long)time;
// 暂停
- (void)pauseTimer;
// 接受信令时的暂停
- (void)pauseTimerWithTimerArray:(NSArray *)array;

// 回放时暂停
- (void)playBackPauseWithTimerArray:(NSArray *)array;

@end

NS_ASSUME_NONNULL_END

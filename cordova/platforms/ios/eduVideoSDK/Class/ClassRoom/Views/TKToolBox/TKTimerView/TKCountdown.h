//
//  TKCountdown.h
//  EduClass
//
//  Created by Evan on 2019/1/10.
//  Copyright © 2019 talkcloud. All rights reserved.
//  

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKCountdown : NSObject


/**
 * 获取当前时间  格式 yyyy-MM-dd hh:mm:ss
 */

- (NSString *) getNowTimeString;


/**
 * 时间转时间戳
 */

- (long) timeStampWithDate:(NSDate *) timeDate;

/**
 * 时间戳转时间
 */

- (NSString *) dateWithTimeStamp:(long) longValue;

/**
 * 用时间戳倒计时
 * starTimeStamp 开始的时间戳
 * finishTimeStamp 结束的时间戳
 */
-(void)countDownWithStratTimeStamp:(long)starTimeStamp finishTimeStamp:(long)finishTimeStamp completeBlock:(void (^)(NSInteger day,NSInteger hour,NSInteger minute,NSInteger second))completeBlock;

-(void)countDownWithStratTimeData:(NSDate *)startDate finishTimeData:(NSDate *)finishDate completeBlock:(void (^)(NSInteger day,NSInteger hour,NSInteger minute,NSInteger second))completeBlock;
/**
 * 每秒走一次，回调block
 */
-(void)countDownWithPER_SECBlock:(void (^)())PER_SECBlock;

/**
 * 销毁倒计时
 */
-(void)destoryTimer;


@end

NS_ASSUME_NONNULL_END

/*
 * This is the source code of Telegram for iOS v. 1.1
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Peter Iakovlev, 2013.
 */

#import <Foundation/Foundation.h>

@interface TKTimer : NSObject
//timeout设置无效

/**
 初始化

 @param timeout 何时执行
 @param repeat 是否重复，yes时timeout设置无效
 @param completion 完成后做什么
 @param queue 线程
 @return TGTimer
 */
- (id)initWithTimeout:(NSTimeInterval)timeout repeat:(bool)repeat completion:(dispatch_block_t)completion queue:(dispatch_queue_t)queue;
- (void)start;
- (void)fireAndInvalidate;
- (void)invalidate;
- (bool)isScheduled;
- (void)fire;


/**
 设置时间

 @param timeout 何时执行
 @param repeat 是否重复，yes时timeout设置无效
 */
- (void)resetTimeout:(NSTimeInterval)timeout repeat:(BOOL)repeat;
- (NSTimeInterval)remainingTime;

@end

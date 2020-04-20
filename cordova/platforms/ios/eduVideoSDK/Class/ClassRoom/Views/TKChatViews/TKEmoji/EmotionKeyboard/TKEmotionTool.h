//
//  HMEmotionTool.h
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.
//  管理表情数据：加载表情数据、存储表情使用记录

#import <Foundation/Foundation.h>
@class TKEmotion;

@interface TKEmotionTool : NSObject
/**
 *  默认表情
 */
+ (NSArray *)defaultEmotions;
/**
 *  emoji表情
 */
+ (NSArray *)emojiEmotions;
/**
 *  浪小花表情
 */
//+ (NSArray *)lxhEmotions;
/**
 *  最近表情
 */
+ (NSArray *)recentEmotions;

/**
 *  根据表情的文字描述找出对应的表情对象
 */
+ (TKEmotion *)emotionWithDesc:(NSString *)desc;

/**
 *  保存最近使用的表情
 */
+ (void)addRecentEmotion:(TKEmotion *)emotion;

@end

//
//  HMEmotionTool.m
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.

#define HMRecentFilepath [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject] stringByAppendingPathComponent:@"recent_emotions.data"]

#import "TKEmotionTool.h"
#import "TKEmotion.h"

@implementation TKEmotionTool
/** 默认表情 */
static NSArray *_defaultEmotions;
/** emoji表情 */
static NSArray *_emojiEmotions;

/** 最近表情 */
static NSMutableArray *_recentEmotions;

+ (NSArray *)defaultEmotions
{
    if (!_defaultEmotions) {

        NSString *plist = [[NSBundle mainBundle] pathForResource:@"TKEmoji.bundle/TKEmoji.plist" ofType:nil];
        NSArray *dictArray = [NSArray arrayWithContentsOfFile:plist];
        NSMutableArray *emojiArray = [NSMutableArray array];
        for (NSDictionary *dict in dictArray){
            TKEmotion *emotion = [[TKEmotion alloc]init];
            emotion.chs = dict[@"chs"];
            emotion.cht = dict[@"cht"];
            emotion.png = dict[@"png"];
            [emojiArray addObject:emotion];
        }
        _defaultEmotions = [NSArray arrayWithArray:emojiArray];
        
        [_defaultEmotions makeObjectsPerformSelector:@selector(setDirectory:) withObject:@"TKEmoji.bundle"];
    }
    return _defaultEmotions;
}


+ (NSArray *)emojiEmotions
{
    if (!_emojiEmotions) {
        
        NSString *plist = [[NSBundle mainBundle] pathForResource:@"TKEmoji.bundle/TKEmoji.plist" ofType:nil];
        NSArray *dictArray = [NSArray arrayWithContentsOfFile:plist];
        NSMutableArray *emojiArray = [NSMutableArray array];
        for (NSDictionary *dict in dictArray){
            TKEmotion *emotion = [[TKEmotion alloc]init];
            emotion.chs = dict[@"chs"];
            emotion.cht = dict[@"cht"];
            emotion.png = dict[@"png"];
            [emojiArray addObject:emotion];
        }
        _emojiEmotions = [NSArray arrayWithArray:emojiArray];
        [_emojiEmotions makeObjectsPerformSelector:@selector(setDirectory:) withObject:@"TKEmoji.bundle"];
        
    }
    return _emojiEmotions;
}




+ (NSArray *)recentEmotions
{
    if (!_recentEmotions) {
        // 去沙盒中加载最近使用的表情数据
        _recentEmotions = [NSKeyedUnarchiver unarchiveObjectWithFile:HMRecentFilepath];
        if (!_recentEmotions) { // 沙盒中没有任何数据
            _recentEmotions = [NSMutableArray array];
        }
    }
    return _recentEmotions;
}

// Emotion -- 戴口罩 -- Emoji的plist里面加载的表情
+ (void)addRecentEmotion:(TKEmotion *)emotion
{
    // 加载最近的表情数据
    [self recentEmotions];
    
    // 删除之前的表情
    [_recentEmotions removeObject:emotion];
    
    // 添加最新的表情
    [_recentEmotions insertObject:emotion atIndex:0];
    
    // 存储到沙盒中
    [NSKeyedArchiver archiveRootObject:_recentEmotions toFile:HMRecentFilepath];
    
}

+ (TKEmotion *)emotionWithDesc:(NSString *)desc
{
    if (!desc) return nil;
    
    __block TKEmotion *foundEmotion = nil;
    
    // 从默认表情中找
    [[self defaultEmotions] enumerateObjectsUsingBlock:^(TKEmotion *emotion, NSUInteger idx, BOOL *stop) {
        if ([desc isEqualToString:emotion.chs] || [desc isEqualToString:emotion.cht]) {
            foundEmotion = emotion;
            *stop = YES;
        }
    }];
    
    if (foundEmotion) return foundEmotion;
    
    // 从浪小花表情中查找
//    [[self lxhEmotions] enumerateObjectsUsingBlock:^(HMEmotion *emotion, NSUInteger idx, BOOL *stop) {
//        if ([desc isEqualToString:emotion.chs] || [desc isEqualToString:emotion.cht]) {
//            foundEmotion = emotion;
//            *stop = YES;
//        }
//    }];
    return foundEmotion;
}
@end

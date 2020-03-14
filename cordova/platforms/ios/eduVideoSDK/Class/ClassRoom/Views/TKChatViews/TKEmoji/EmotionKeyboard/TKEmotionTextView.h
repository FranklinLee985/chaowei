//
//  HMEmotionTextView.h
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.

#import "TKTextView.h"
@class TKEmotion;

@interface TKEmotionTextView : TKTextView
/**
 *  拼接表情到最后面
 */
- (void)appendEmotion:(TKEmotion *)emotion;
/**
 *  具体的文字内容
 */
- (NSString *)realText;

@end

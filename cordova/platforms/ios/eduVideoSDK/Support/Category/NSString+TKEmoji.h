//
//  NSString+Emoji.h
//  EduClass
//
//  Created by lyy on 2018/5/4.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (TKEmoji)
/**
 *  将十六进制的编码转为emoji字符
 */
+ (NSString *)tkEmojiWithIntCode:(int)intCode;

/**
 *  将十六进制的编码转为emoji字符
 */
+ (NSString *)tkEmojiWithStringCode:(NSString *)stringCode;

/**
 *  是否为emoji字符
 */
- (BOOL)tkIsEmoji;
@end

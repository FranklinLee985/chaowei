//
//  NSAttributedString+TKEmoji.h
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <Foundation/Foundation.h>
//@import UIKit;
#import <UIKit/UIKit.h>
@interface NSAttributedString (TKEmoji)
+ (NSAttributedString *)tkEmojiAttributedString:(NSString *)string withFont:(UIFont *)font withColor:(UIColor *)color;
//+ (void)tkMatchURL:(NSString *)text;
+ (NSString *)tkRemoveEmojiAttributedString:(NSString *)string withFont:(UIFont *)font withColor:(UIColor *)color;

+ (NSInteger)tkGetStringLengthWithString:(NSString *)string;
@end

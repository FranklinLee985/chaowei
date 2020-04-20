//
//  TKTheme.h
//  EduClass
//
//  Created by lyy on 2018/5/14.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@interface TKTheme : UIView

/**
 @return String value
 */
+ (NSString *)stringWithPath:(NSString *)name;

/**
 @return UIColor value
 */
+ (UIColor *)colorWithPath:(NSString *)path;

/**
 @return CGColor value
 */
+ (CGColorRef  )cgColorWithPath:(NSString *)path;

/**
 @return Image value
 */
+ (UIImage *)imageWithPath:(NSString *)path;

/**
 @return Font value
 */
+ (UIFont *)fontWithPath:(NSString *)path;

/**
 @return float value
 */
+ (CGFloat) floatWithPath:(NSString *)path;
@end

//
//  TKTheme.m
//  EduClass
//
//  Created by lyy on 2018/5/14.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKTheme.h"
#import "TXSakura.h"

@implementation TKTheme

+ (NSString *)stringWithPath:(NSString *)name{
    return [TXSakuraManager tx_stringWithPath:name];
}


+ (UIColor *)colorWithPath:(NSString *)path{
    return [TXSakuraManager tx_colorWithPath:path];
    
}

+ (CGColorRef  )cgColorWithPath:(NSString *)path{
    return [TXSakuraManager tx_cgColorWithPath:path];
}

+ (UIImage *)imageWithPath:(NSString *)path{
    return [TXSakuraManager tx_imageWithPath:path];
}

+ (UIFont *)fontWithPath:(NSString *)path{
    return [TXSakuraManager tx_fontWithPath:path];
}

+ (CGFloat) floatWithPath:(NSString *)path {
    return [TXSakuraManager tx_floatWithPath:path];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

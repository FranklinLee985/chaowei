//
//  UIView+Extension.h
//  EduClass
//
//  Created by lyy on 2018/5/4.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (TKExtension)

@property (nonatomic, assign) CGFloat x;
@property (nonatomic, assign) CGFloat y;
@property (nonatomic, assign) CGFloat centerX;
@property (nonatomic, assign) CGFloat centerY;
@property (nonatomic, assign) CGFloat width;
@property (nonatomic, assign) CGFloat height;
@property (nonatomic, assign) CGSize size;
@property (nonatomic, assign,readonly) CGFloat rightX;
@property (nonatomic, assign,readonly) CGFloat leftX;
@property (nonatomic, assign,readonly) CGFloat bottomY;

- (void)moveToPositionAtView:(UIView *)view withLeft:(CGFloat)left top:(CGFloat)top;
@end

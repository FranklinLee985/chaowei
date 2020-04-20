//
//  TKRedDotView.h
//  EduClass
//
//  Created by lyy on 2018/5/11.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TKRedDotView : UIImageView

@property (nonatomic, assign) CGFloat radius;

@property (nonatomic, strong) UIColor *color;

@property (nonatomic, assign) CGFloat borderWidth;

@property (nonatomic, strong) UIColor *borderColor;

@property (nonatomic, assign) CGPoint offset;

@property (nonatomic, copy) void (^refreshBlock)(TKRedDotView *view);

+ (void)setDefaultRadius:(CGFloat)radius;

+ (void)setDefaultColor:(UIColor *)color;

@end


@interface TKBadgeView : UIImageView

@property (nonatomic, copy) NSString *badgeValue;

@property (nonatomic, assign) CGPoint offset;

@property (nonatomic, strong) UIColor *color;

@end

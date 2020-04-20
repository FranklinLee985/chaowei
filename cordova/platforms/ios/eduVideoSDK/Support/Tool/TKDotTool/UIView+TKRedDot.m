//
//  UIView+TKRedDot.m
//  EduClass
//
//  Created by lyy on 2018/5/11.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "UIView+TKRedDot.h"
#import <objc/runtime.h>
#import "TKRedDotView.h"

#pragma mark- TKRedDotView interface extension
@interface TKRedDotView ()
@property (nonatomic, weak) NSLayoutConstraint *layoutCenterX;
@property (nonatomic, weak) NSLayoutConstraint *layoutCenterY;
@end

@interface TKBadgeView ()
@property (nonatomic, weak) NSLayoutConstraint *layoutCenterX;
@property (nonatomic, weak) NSLayoutConstraint *layoutCenterY;
@end

@interface UIView ()
@property (nonatomic, strong) TKRedDotView *redDotView;
@property (nonatomic, strong) TKBadgeView *badgeView;
@end

@implementation UIView (TKRedDot)

- (BOOL)showRedDot {
    return [objc_getAssociatedObject(self, _cmd) boolValue];
}

- (void)setShowRedDot:(BOOL)showRedDot {
//    if (self.showRedDot != showRedDot) {
        objc_setAssociatedObject(self, @selector(showRedDot), @(showRedDot), OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        [self _refreshHiddenState];
//    }
}

- (TKRedDotView *)redDotView {
    TKRedDotView *dotView = objc_getAssociatedObject(self, _cmd);
    if (!dotView) {
        dotView = [[TKRedDotView alloc] init];
        dotView.hidden = YES;
        __weak __typeof(self) weakSelf = self;
        dotView.refreshBlock = ^(TKRedDotView *view) {
            [weakSelf refreshRedDotView:view];
        };
        objc_setAssociatedObject(self, _cmd, dotView, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        [self addSubview:dotView];
        [self _layoutDotView:dotView];
    }
    return dotView;
}

- (TKBadgeView *)badgeView {
    TKBadgeView *badgeView = objc_getAssociatedObject(self, _cmd);
    if (!badgeView) {
        badgeView = [[TKBadgeView alloc] init];
        badgeView.hidden = YES;
  
        objc_setAssociatedObject(self, _cmd, badgeView, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        [self addSubview:badgeView];
        [self _layoutBadgeView:badgeView];
    }
    return badgeView;
}

//offset
- (CGPoint)redDotOffset {
    return self.redDotView.offset;
}

- (void)setRedDotOffset:(CGPoint)redDotOffset {
    self.redDotView.offset = redDotOffset;
}

//radius
- (CGFloat)redDotRadius {
    return self.redDotView.radius;
}

- (void)setRedDotRadius:(CGFloat)redDotRadius {
    self.redDotView.radius = redDotRadius;
}

//color
- (UIColor *)redDotColor {
    return self.redDotView.color;
}

- (void)setRedDotColor:(UIColor *)redDotColor {
    self.redDotView.color = redDotColor;
}

//border
- (UIColor *)redDotBorderColor {
    return self.redDotView.borderColor;
}

- (void)setRedDotBorderColor:(UIColor *)redDotBorderColor {
    self.redDotView.borderColor = redDotBorderColor;
}

- (CGFloat)redDotBorderWitdh {
    return self.redDotView.borderWidth;
}

- (void)setRedDotBorderWitdh:(CGFloat)redDotBorderWitdh {
    self.redDotView.borderWidth = redDotBorderWitdh;
}

//badge
- (NSString *)badgeValue {
    return self.badgeView.badgeValue;
}

- (void)setBadgeValue:(NSString *)badgeValue {
    if ([self.badgeView.badgeValue isEqualToString:badgeValue]) return;
    self.badgeView.badgeValue = badgeValue;
    self.badgeView.hidden = !badgeValue;
    [self _refreshHiddenState];
}

- (CGPoint)badgeOffset {
    return self.badgeView.offset;
}

- (void)setBadgeOffset:(CGPoint)badgeOffset {
    self.badgeView.offset = badgeOffset;
    [self _refreshBadgeLayout];
}

- (UIColor *)badgeColor {
    return self.badgeView.color;
}

- (void)setBadgeColor:(UIColor *)badgeColor {
    self.badgeView.color = badgeColor;
}

//pravite
- (void)_refreshHiddenState {
    self.redDotView.hidden = (!self.showRedDot || self.badgeValue);
}

- (void)_layoutDotView:(TKRedDotView *)dotView {
    
    dotView.translatesAutoresizingMaskIntoConstraints = NO;
    CGFloat x = - dotView.radius + self.redDotOffset.x;
    CGFloat y = dotView.radius + self.redDotOffset.y;
    NSLayoutConstraint *layoutX = [NSLayoutConstraint constraintWithItem:dotView
                                                               attribute:NSLayoutAttributeLeft
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:self
                                                               attribute:NSLayoutAttributeRight
                                                              multiplier:1
                                                                constant:x];
    NSLayoutConstraint *layoutY = [NSLayoutConstraint constraintWithItem:dotView
                                                               attribute:NSLayoutAttributeBottom
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:self
                                                               attribute:NSLayoutAttributeTop
                                                              multiplier:1
                                                                constant:y];
    dotView.layoutCenterX = layoutX;
    dotView.layoutCenterY = layoutY;
    [self addConstraint:layoutX];
    [self addConstraint:layoutY];

}

- (void)_refreshRedDotLayout {
    CGFloat x = - self.redDotView.radius + self.redDotOffset.x;
    CGFloat y = self.redDotView.radius + self.redDotOffset.y;
    self.redDotView.layoutCenterX.constant = x;
    self.redDotView.layoutCenterY.constant = y;
}

- (void)refreshRedDotView:(TKRedDotView *)view {
    [self _refreshRedDotLayout];
}

- (void)_layoutBadgeView:(TKBadgeView *)bageview {
    bageview.translatesAutoresizingMaskIntoConstraints = NO;
    CGFloat x = -8 + self.badgeOffset.x;
    CGFloat y = 8 + self.badgeOffset.y;
    NSLayoutConstraint *layoutX = [NSLayoutConstraint constraintWithItem:bageview
                                                               attribute:NSLayoutAttributeLeft
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:self
                                                               attribute:NSLayoutAttributeRight
                                                              multiplier:1
                                                                constant:x];
    NSLayoutConstraint *layoutY = [NSLayoutConstraint constraintWithItem:bageview
                                                               attribute:NSLayoutAttributeBottom
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:self
                                                               attribute:NSLayoutAttributeTop
                                                              multiplier:1
                                                                constant:y];
    [self addConstraint:layoutX];
    [self addConstraint:layoutY];
    bageview.layoutCenterX = layoutX;
    bageview.layoutCenterY = layoutY;
}

- (void)_refreshBadgeLayout {
    CGFloat x = - 8 + self.badgeOffset.x;
    CGFloat y = 8 + self.badgeOffset.y;
    self.badgeView.layoutCenterX.constant = x;
    self.badgeView.layoutCenterY.constant = y;
}

@end

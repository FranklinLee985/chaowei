//
//  TKToolBoxBaseView.m
//  EduClass
//
//  Created by YI on 2019/1/16.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKToolBoxBaseView.h"
@interface TKToolBoxBaseView()

@property(nonatomic, assign)CGPoint startLocation;

@end

@implementation TKToolBoxBaseView

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */
- (void) touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
    // Calculate and store offset, and pop view into front if needed
    CGPoint pt = [[touches anyObject] locationInView:self];
    _startLocation = pt;
    [self.superview bringSubviewToFront:self];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{ 
    // 计算偏移量
    CGPoint pt = [[touches anyObject] locationInView:self];

    float 	dx = pt.x - _startLocation.x;
    float 	dy = pt.y - _startLocation.y;
    
    CGPoint newcenter 	= CGPointMake(self.center.x + dx, self.center.y + dy);

    // 设置移动的区域
    float halfx = CGRectGetMidX(self.bounds);
    newcenter.x = MAX(halfx, newcenter.x);
    newcenter.x = MIN(self.superview.bounds.size.width - halfx, newcenter.x);

    float halfy = CGRectGetMidY(self.bounds);
    newcenter.y = MAX(halfy, newcenter.y);
    newcenter.y = MIN(self.superview.bounds.size.height - halfy, newcenter.y);
    
    self.center = newcenter;
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    CGPoint pt = [[touches anyObject] locationInView:[self superview]];

    CGFloat offset_x = pt.x - _startLocation.x;
    CGFloat offset_y = pt.y - _startLocation.y;

    
    offset_x = MAX(0, offset_x);
    offset_x = MIN(CGRectGetWidth(self.superview.frame) - CGRectGetWidth(self.frame), offset_x);
    
    offset_y = MAX(0, offset_y);
    offset_y = MIN(CGRectGetHeight(self.superview.frame)-CGRectGetHeight(self.frame), offset_y);

    
    //重新设置中心点位置
    [self mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.superview.mas_left).offset(offset_x);
        make.top.equalTo(self.superview.mas_top).offset(offset_y);
    }];
    
}
@end

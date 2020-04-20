//
//  TKColorTipView.m
//  EduClass
//
//  Created by talkcloud on 2019/3/28.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKColorTipView.h"

@interface TKColorTipView ()

@property (nonatomic, strong) UIView * contentView;

@end

@implementation TKColorTipView

- (instancetype)init {
    
    self = [super init];
    if (self) {
        
        [self addSubview:self.contentView];
        [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.top.right.equalTo(self);
            make.height.equalTo(self.mas_width).offset(-2);
        }];
    }
    return self;
}

- (void) changeColor:(NSString *)colorString {
    
    self.contentView.backgroundColor = [TKHelperUtil colorWithHexColorString:colorString];
}

- (void)drawRect:(CGRect)rect
{
    CGRect frame  = self.contentView.frame;
    UIColor *color = UIColor.whiteColor;
    [color set]; //设置线条颜色
    
    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:CGPointMake(CGRectGetMidX(frame), CGRectGetMaxY(frame) + 2)];
    [path addLineToPoint:CGPointMake(CGRectGetMidX(frame) - 2, CGRectGetMaxY(frame))];
    [path addLineToPoint:CGPointMake(CGRectGetMidX(frame) + 2, CGRectGetMaxY(frame))];
    [path addLineToPoint:CGPointMake(CGRectGetMidX(frame), CGRectGetMaxY(frame) + 2)];
    [path closePath];
    [path fill];
    
    path.lineWidth = 1;
    path.lineCapStyle = kCGLineCapRound; //线条拐角
    path.lineJoinStyle = kCGLineJoinRound; //终点处理
    
    [path stroke];
}

- (UIView *)contentView {
    if (nil == _contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.backgroundColor = UIColor.clearColor;
        _contentView.layer.borderWidth = 1;
        _contentView.layer.borderColor = UIColor.whiteColor.CGColor;
        _contentView.layer.cornerRadius = 2;
        _contentView.layer.masksToBounds = YES;
    }
    return _contentView;
}

@end

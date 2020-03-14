//
//  TKProgressView.m
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKProgressView.h"
#import <QuartzCore/QuartzCore.h>


@interface TKProgressLayer : CALayer

@property(assign , nonatomic) CGFloat progress;

//背景色
@property (strong , nonatomic) UIColor *trackTintColor;
//进度条颜色
@property (strong , nonatomic) UIColor *progressTintColor;

@end

@implementation TKProgressLayer

+ (BOOL)needsDisplayForKey:(NSString *)key
{
    if ([key isEqualToString:@"progress"]) {
        return YES;
    } else {
        return [super needsDisplayForKey:key];
    }
}

- (void)drawInContext:(CGContextRef)context
{
    CGRect rect = self.bounds;

    //设置背景色
    CGContextSetFillColorWithColor(context, [UIColor clearColor].CGColor);
    CGContextAddRect(context, rect);
    CGContextFillPath(context);

    //设置track
    CGContextSetStrokeColorWithColor(context, self.trackTintColor.CGColor);
    CGContextSetLineWidth(context, rect.size.height);
    CGContextSetLineCap(context, kCGLineCapRound);
    CGContextSetLineJoin(context, kCGLineJoinRound);

    CGContextBeginPath(context);
    CGContextMoveToPoint(context, 10,rect.size.height/2);
    CGContextAddLineToPoint(context, rect.size.width-20, rect.size.height/2);
    CGContextStrokePath(context);
    
    if (self.progress == 0) {
        return;
    }
    
    CGFloat width = (rect.size.width-20) * self.progress;
    if (width <= rect.size.height) {
         width = rect.size.height;
    }
    //设置progress
    CGContextSetStrokeColorWithColor(context, self.progressTintColor.CGColor);
    CGContextSetLineWidth(context, rect.size.height);
    CGContextSetLineCap(context, kCGLineCapRound);
    CGContextSetLineJoin(context, kCGLineJoinRound);
    
    CGContextBeginPath(context);
    CGContextMoveToPoint(context, 10,rect.size.height/2);
    CGContextAddLineToPoint(context, width, rect.size.height/2);
    CGContextStrokePath(context);

}

@end



@interface TKProgressView()

//背景色
@property (strong , nonatomic) UIView *trackView;
//进度条颜色
@property (strong , nonatomic) UIView *progressView;

@property (assign , nonatomic) CGFloat progress;

@end

@implementation TKProgressView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.trackView];
        [self addSubview:self.progressView];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    self.trackView.frame    = self.bounds;
    
    CGFloat width = self.progress * CGRectGetWidth(self.frame);
    self.progressView.frame = CGRectMake(0, 0, width, CGRectGetHeight(self.frame));
}

- (void)setProgress:(CGFloat)progress animated:(BOOL)animated
{
    CGFloat pinnedProgress = MIN(MAX(progress, 0.0f), 1.0f);
    self.progress = pinnedProgress;
    
    [self setNeedsLayout];
}

- (UIView *)trackView
{
    if (!_trackView) {
        _trackView = [[UIView alloc] init];
        _trackView.layer.cornerRadius = 6.5;
        _trackView.clipsToBounds = YES;
        _trackView.sakura.backgroundColor(@"TKToolsBox.answer_trackView");
    }
    return _trackView;
}

- (UIView *)progressView
{
    if (!_progressView) {
        _progressView = [[UIView alloc] init];
        _progressView.layer.cornerRadius = 6.5;
        _progressView.clipsToBounds = YES;
        _progressView.sakura.backgroundColor(@"TKToolsBox.answer_progressView");

    }
    return _progressView;

}

@end

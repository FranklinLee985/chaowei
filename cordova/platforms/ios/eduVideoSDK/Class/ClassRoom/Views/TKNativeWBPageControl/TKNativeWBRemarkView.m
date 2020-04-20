//
//  TKNativeWBRemarkView.m
//  TKWhiteBoard
//
//  Created by maqihan on 2019/2/12.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import "TKNativeWBRemarkView.h"

#define ThemeKP(args) [@"TKNativeWB.TKNativeWBRemarkView." stringByAppendingString:args]

@interface TKNativeWBRemarkView ()

@property(nonatomic, assign)CGPoint startLocation;

@property (strong , nonatomic) UILabel *remarkLabel;

@property (strong , nonatomic) UIView *containerView;
@property (strong , nonatomic) UIView *targetView;

@end

@implementation TKNativeWBRemarkView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _limitWidth = 400;
//        self.sakura.backgroundColor(ThemeKP(@"RemarkViewBackgroundColor"));
//        self.sakura.alpha(ThemeKP(@"RemarkViewBackgroundColorAlpha"));
        self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
        self.layer.cornerRadius = 10;
        self.clipsToBounds = YES;
        
        [self addSubview:self.remarkLabel];
    }
    return self;
}


+ (TKNativeWBRemarkView *)showRemarkViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView
{
    NSAssert(containerView, @"containerView must not be nil.");
    NSAssert(targetView, @"targetView must not be nil.");
    
    TKNativeWBRemarkView *remarkView = [[self alloc] init];
    remarkView.containerView = containerView;
    remarkView.targetView    = targetView;
    [containerView addSubview:remarkView];
    
    return remarkView;
}

+ (BOOL)dismissForView:(UIView *)view;
{
    TKNativeWBRemarkView *remarkView = [self remarkViewForView:view];
    if (remarkView) {
        return YES;
    }
    return NO;
}

+ (TKNativeWBRemarkView *)remarkViewForView:(UIView *)view {
    NSEnumerator *subviewsEnum = [view.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:self]) {
            TKNativeWBRemarkView *remarkView = (TKNativeWBRemarkView *)subview;
            return remarkView;
        }
    }
    return nil;
}


- (void)updateConstraints
{
    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(self.remarkLabel.mas_width).offset(20);
        make.height.equalTo(self.remarkLabel.mas_height).offset(20);
        make.bottom.equalTo(self.targetView.mas_top).offset(-20);
        make.centerX.equalTo(self.targetView.mas_centerX);
    }];
    
    [self.remarkLabel mas_updateConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@(_limitWidth));
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
    }];
    
    [super updateConstraints];
}


- (void)setRemarkContent:(NSString *)remarkContent
{
    _remarkContent = remarkContent;
    
    self.remarkLabel.text = remarkContent;
    
    [self setNeedsUpdateConstraints];
    [self setNeedsDisplay];
}

- (UILabel *)remarkLabel
{
    if (!_remarkLabel) {
        _remarkLabel = [[UILabel alloc] init];
        _remarkLabel.textColor = [UIColor whiteColor];
        _remarkLabel.font = [UIFont systemFontOfSize:18];
        _remarkLabel.numberOfLines = 0;
        _remarkLabel.textAlignment = NSTextAlignmentCenter;
        _remarkLabel.backgroundColor = [UIColor clearColor];
    }
    return _remarkLabel;
}

@end

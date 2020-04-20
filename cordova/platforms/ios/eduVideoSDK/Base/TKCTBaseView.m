//
//  TKCTBaseView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTBaseView.h"
@interface TKCTBaseView()


@end

@implementation TKCTBaseView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        
        [self setBackgroundColor:[UIColor clearColor]];
        
        self.bgButton = [UIButton buttonWithType:UIButtonTypeCustom];
        self.bgButton.frame = self.bounds;
//        self.bgButton.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.3];
        self.bgButton.backgroundColor = [UIColor clearColor];
        [self.bgButton addTarget:self action:@selector(dismissAlertt) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:self.bgButton];
        
        //背景图片
        self.backImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        self.backImageView.image = [UIImage tkResizedImageWithName:@"TKBaseView.base_bg_big"];
        self.backImageView.backgroundColor = [UIColor clearColor];
        self.backImageView.userInteractionEnabled = YES;
        [self addSubview:self.backImageView];
        
        _contentImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        _contentImageView.backgroundColor = [UIColor clearColor];
        _contentImageView.userInteractionEnabled = YES;
        [self.backImageView addSubview:_contentImageView];
        
        //关闭按钮
        _closeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _closeButton.sakura.image(@"TKBaseView.btn_close",UIControlStateNormal);
        CGFloat height = self.titleH;
        _closeButton.frame = CGRectMake(CGRectGetWidth(self.backImageView.frame)-height, 0, height, height);
        [_closeButton addTarget:self action:@selector(dismissAlert) forControlEvents:UIControlEventTouchUpInside];
        [self.backImageView addSubview:_closeButton];
    }
    return self;
}

- (void)dismissAlertt
{
    
}

- (void)touchOutSide{
    [self dismissAlert];
}

- (void)show
{
    [TKMainWindow addSubview:self];
    [UIView animateWithDuration: 0.25 animations:^{
        
        self.layer.affineTransform = CGAffineTransformMakeScale(1.0, 1.0);
        self.alpha = 1;

    } completion:^(BOOL finished) {
        
    }];
    
}

- (void)show:(UIView *)view
{
    [view addSubview:self];
    [UIView animateWithDuration: 0.25 animations:^{
        
        self.layer.affineTransform = CGAffineTransformMakeScale(1.0, 1.0);
        self.alpha = 1;
    } completion:^(BOOL finished) {
        
    }];
    
}

- (UIViewController *)appRootViewController
{
    UIViewController *appRootVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *topVC = appRootVC;
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}

- (void)hidden{
    
    [self dismissAlert];
}
- (void)dismissAlert
{
    
    [UIView animateWithDuration:0.3f
                     animations:^{
                         
                         self.alpha = 0.0;
                         
                     }
                     completion:^(BOOL finished){
                         
                         [self removeFromSuperview];
                         if (self.dismissBlock) {
                             self.dismissBlock();
                         }
                     }];
    
}

- (void)setTitleText:(NSString *)titleText {
    
    if (titleText.length == 0) {
        return;
    }
    _titleText = titleText;
    self.titleLabel.text = _titleText;
}
- (UILabel *)titleLabel {
    if (!_titleLabel) {
        
        UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(0,0,self.backImageView.width,self.titleH)];
        lbl.textAlignment = NSTextAlignmentCenter;
        lbl.sakura.textColor(@"TKBaseView.titleColor");
        //@"TKBaseView.titleColor"
        //@"TKListView.coursewareButtonDefaultColor"
        lbl.font = TITLE_FONT;
        _titleLabel = lbl;
        
        [self.backImageView addSubview:self.titleLabel];
        [self bringSubviewToFront:_closeButton];
    }
    return _titleLabel;
}

// 标题栏 高度
- (CGFloat)titleH {
    return IS_PAD ? 50 : 40;
}

@end



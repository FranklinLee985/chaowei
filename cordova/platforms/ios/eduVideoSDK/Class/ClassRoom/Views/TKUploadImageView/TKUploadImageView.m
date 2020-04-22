//
//  TKUploadImageView.m
//  EduClassPad
//
//  Created by ifeng on 2017/10/17.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKUploadImageView.h"

@implementation TKUploadImageView

- (void)setProgress:(CGFloat)progress
    {
        if (!_progressLabel) {
            _progressLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, 20)];
            [TKUtil setBottom:_progressLabel To:self.frame.size.height];
            _progressLabel.backgroundColor = [UIColor clearColor];
            _progressLabel.textColor = [UIColor whiteColor];
            _progressLabel.font = [UIFont systemFontOfSize:20];
            _progressLabel.textAlignment = NSTextAlignmentCenter;
            
            
            _progressView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, 0)];
            _progressView.backgroundColor = [UIColor blackColor];
            _progressView.autoresizingMask = UIViewAutoresizingFlexibleTopMargin;
            _progressView.alpha = 0.6;
            [self addSubview:_progressView];
            
            [self addSubview:_progressLabel];
            
            _cancelButton = [[UIButton alloc] initWithFrame:CGRectMake(self.frame.size.width-50, 0, 50, 50)];
            //_cancelButton.center = CGPointMake(self.frame.size.width / 2, self.frame.size.height / 2);
            [_cancelButton setBackgroundImage:LOADIMAGE(@"btn_closed_normal") forState:UIControlStateNormal];
            [_cancelButton addTarget:_target action:_action forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:_cancelButton];
        }
        [TKUtil setBottom:_progressView To:self.frame.size.height];
        [TKUtil setHeight:_progressView To:self.frame.size.height * progress];
        _progressLabel.text = [NSString stringWithFormat:@"%d%@",(int)(progress * 100),@"%"];
    }
    
- (void)layoutSubviews
    {
        [super layoutSubviews];
        
        _progressLabel.frame = CGRectMake(0, 0, self.frame.size.width, 30);
        [TKUtil setBottom:_progressLabel To:self.frame.size.height];
    }

@end

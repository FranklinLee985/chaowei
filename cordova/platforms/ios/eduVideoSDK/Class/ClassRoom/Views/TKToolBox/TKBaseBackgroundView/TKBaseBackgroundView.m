//
//  TKBaseBackgroundView.m
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKBaseBackgroundView.h"
#import "TKEduSessionHandle.h"


#define ThemeKP(args) [@"TKToolsBox." stringByAppendingString:args]

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)


@interface TKBaseBackgroundView()

@property (strong , nonatomic) UIView  *angleView;

@end

@implementation TKBaseBackgroundView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];

        [self addSubview:self.backgroundView];
        [self.backgroundView addSubview:self.titleLabel];
        [self.backgroundView addSubview:self.cancelButton];
        [self.backgroundView addSubview:self.angleView];
        [self.backgroundView addSubview:self.contentView];

        [self.backgroundView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.centerY.equalTo(self.mas_centerY);
            make.width.equalTo(@Fit(481));
            make.height.equalTo(@Fit(361-90));
        }];
        
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.backgroundView.mas_centerX);
            make.top.equalTo(self.backgroundView.mas_top);
            make.height.equalTo(@Fit(50));
        }];

        [self.cancelButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.backgroundView.mas_right);
            make.top.equalTo(self.backgroundView.mas_top);
            make.width.equalTo(@Fit(60));
            make.height.equalTo(@Fit(50));

        }];
        
        [self.angleView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.backgroundView.mas_top).offset(Fit(50));
            make.left.equalTo(self.backgroundView.mas_left).offset(4);
            make.right.equalTo(self.backgroundView.mas_right).offset(-4);
            make.bottom.equalTo(self.backgroundView.mas_bottom).offset(-Fit(50));
        }];

        [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.backgroundView.mas_top).offset(Fit(50));
            make.left.equalTo(self.backgroundView.mas_left).offset(4);
            make.right.equalTo(self.backgroundView.mas_right).offset(-4);
            make.bottom.equalTo(self.backgroundView.mas_bottom).offset(-4);
        }];
    }
    return self;
}

- (void)cancelButtonAction
{
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    [self removeFromSuperview];
}

- (UIView *)backgroundView
{
    if (!_backgroundView) {
        _backgroundView = [[UIView alloc] init];
        _backgroundView.sakura.backgroundColor(ThemeKP(@"backgroudColor"));
        _backgroundView.layer.cornerRadius = 10;
        _backgroundView.clipsToBounds = YES;
    }
    return _backgroundView;
}

- (UIView *)contentView
{
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.sakura.backgroundColor(ThemeKP(@"backgroudContentColor"));
        _contentView.layer.cornerRadius = 10;
        _contentView.clipsToBounds = YES;
    }
    return _contentView;
}

- (UIView *)angleView
{
    if (!_angleView) {
        _angleView = [[UIView alloc] init];
        _angleView.sakura.backgroundColor(ThemeKP(@"backgroudContentColor"));
    }
    return _angleView;
}

- (UILabel *)titleLabel
{
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:18];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.text = @"这是标题";
    }
    return _titleLabel;
}

-(UIButton *)cancelButton
{
    if (!_cancelButton) {
        _cancelButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _cancelButton.sakura.image(@"TKBaseView.btn_close",UIControlStateNormal);
        [_cancelButton addTarget:self action:@selector(cancelButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cancelButton;
}

@end

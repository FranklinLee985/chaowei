//
//  TKCTNetTipView.m
//  EduClass
//
//  Created by talkcloud on 2019/3/20.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKCTNetTipView.h"

#define ThemeKP(args) [@"ClassRoom.TKNavView." stringByAppendingString:args]

@interface TKCTNetTipView ()

@property (nonatomic, strong) UIButton * bgBtn;

@property (nonatomic, strong) UIImageView * netTipImageView;
@property (nonatomic, strong) UILabel     * netTipLabel;
@property (nonatomic, strong) UIImageView * netDetailSignImageView;

@property (nonatomic, assign) TKNetQuality netQuality;

@end

@implementation TKCTNetTipView

- (instancetype)init {
    
    if (self = [super init]) {
        
        _bgBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_bgBtn addTarget:self action:@selector(bgBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_bgBtn];
        [_bgBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        
        _netTipImageView = [[UIImageView alloc] init];
        [self addSubview:_netTipImageView];
        [_netTipImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.and.centerY.equalTo(self);
            make.width.and.height.equalTo(@(16));
        }];
        
        _netDetailSignImageView = [[UIImageView alloc] init];
        [self addSubview:_netDetailSignImageView];
        [_netDetailSignImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self);
            make.size.mas_equalTo(CGSizeMake(7, 5));
            make.right.equalTo(self);
        }];
        
        _netTipLabel = [[UILabel alloc] init];
        _netTipLabel.font = TKFont(14);
        [self addSubview:_netTipLabel];
        [_netTipLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.netTipImageView);
            make.left.equalTo(self.netTipImageView.mas_right).offset(6);
            make.right.equalTo(self.netDetailSignImageView.mas_left).offset(-4);
        }];
        
        
        [self setDefaultDate];
    }
    return self;
}

- (void) setDefaultDate {
    
    self.netQuality = TKNetQuality_Excellent;
    
    _netTipImageView.sakura.image(ThemeKP(@"netstate_tip_green"));
    _netTipLabel.text = TKMTLocalized(@"netstate.excellent");
    _netTipLabel.sakura.textColor(ThemeKP(@"netstate_tip_textColor_green"));
    _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_green") : ThemeKP(@"netstate_sign_down_green"));
}

- (void) bgBtnClick:(UIButton *) sender {
    
    [self changeDetailSignImage:YES];
    
    if (self.netStateBlock) {
        self.netStateBlock(sender.selected);
    }
}

- (void) changeDetailSignImage:(BOOL)isShow {
    
    _bgBtn.selected = isShow;
    
    switch (self.netQuality) {
        case TKNetQuality_Excellent:
        case TKNetQuality_Good:
        {
            _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_green") : ThemeKP(@"netstate_sign_down_green"));
            break;
        }
        case TKNetQuality_Accepted:
        case TKNetQuality_Bad:
        {
            _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_orange") : ThemeKP(@"netstate_sign_down_orange"));
            break;
        }
        case TKNetQuality_VeryBad:
        case TKNetQuality_Down:
        {
            _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_red") : ThemeKP(@"netstate_sign_down_red"));
            break;
        }
    }
}

- (void) changeNetTipState:(id)state {
    
    self.netState = state;
    
    TKNetQuality quality;
    if ([state isKindOfClass:[TKAudioStats class]]) {
        quality = [(TKAudioStats *)state netLevel];
    } else {
        quality = [(TKVideoStats *)state netLevel];
    }
    
    if (quality == self.netQuality) {
        return;
    }
    self.netQuality = quality;
    
    switch (self.netQuality) {
        case TKNetQuality_Excellent:
        case TKNetQuality_Good:
        {
            _netTipImageView.sakura.image(ThemeKP(@"netstate_tip_green"));
            _netTipLabel.text = TKMTLocalized(@"netstate.excellent");
            _netTipLabel.sakura.textColor(ThemeKP(@"netstate_tip_textColor_green"));
            _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_green") : ThemeKP(@"netstate_sign_down_green"));
            break;
        }
        case TKNetQuality_Accepted:
        case TKNetQuality_Bad:
        {
            _netTipImageView.sakura.image(ThemeKP(@"netstate_tip_orange"));
            _netTipLabel.text = TKMTLocalized(@"netstate.medium");
            _netTipLabel.sakura.textColor(ThemeKP(@"netstate_tip_textColor_orange"));
            _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_orange") : ThemeKP(@"netstate_sign_down_orange"));
            break;
        }
        case TKNetQuality_VeryBad:
        case TKNetQuality_Down:
        {
            _netTipImageView.sakura.image(ThemeKP(@"netstate_tip_red"));
            _netTipLabel.text = TKMTLocalized(@"netstate.bad");
            _netTipLabel.sakura.textColor(ThemeKP(@"netstate_tip_textColor_red"));
            _netDetailSignImageView.sakura.image(_bgBtn.selected ? ThemeKP(@"netstate_sign_up_red") : ThemeKP(@"netstate_sign_down_red"));
            break;
        }
    }
}

@end

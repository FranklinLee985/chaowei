//
//  TKPlaySpeedView.m
//  test
//
//  Created by admin on 2018/5/30.
//  Copyright © 2018年 admin. All rights reserved.
//

#import "TKPlaySpeedView.h"

#define ThemeKP(args) [@"ClassRoom.PlayBack." stringByAppendingString:args]
@interface TKPlaySpeedView ()

@property (nonatomic, strong) UIButton *btn05X;
@property (nonatomic, strong) UIButton *btn075X;
@property (nonatomic, strong) UIButton *btn10X;
@property (nonatomic, strong) UIButton *btn125X;
@property (nonatomic, strong) UIButton *btn15X;
@property (nonatomic, strong) UIButton *btn20X;

@property (nonatomic, strong) NSArray<UIButton *> *btnArr;
@property (nonatomic, assign) CGFloat eachHeight; // 每个按钮的高度
@end

@implementation TKPlaySpeedView


- (instancetype)initWithFrame:(CGRect)frame {
   
    self = [super initWithFrame: frame];
    if (self) {
    
        // 圆角 背景色
        self.layer.cornerRadius = 10.;
        self.layer.masksToBounds = YES;
       
        self.alpha = 0.5; self.sakura.backgroundColor(ThemeKP( @"playSpeedBackgroundColor"));
        
        // 初始化
        _eachHeight = self.frame.size.height / 6.;
        
        _btn05X = [UIButton buttonWithType: UIButtonTypeCustom];
        _btn075X = [UIButton buttonWithType: UIButtonTypeCustom];
        _btn10X = [UIButton buttonWithType: UIButtonTypeCustom];
        _btn125X = [UIButton buttonWithType: UIButtonTypeCustom];
        _btn15X = [UIButton buttonWithType: UIButtonTypeCustom];
        _btn20X = [UIButton buttonWithType: UIButtonTypeCustom];
        
        _btnArr = [NSArray arrayWithObjects:
                   _btn05X,
                   _btn075X,
                   _btn10X,
                   _btn125X,
                   _btn15X,
                   _btn20X
                   , nil];
        
        for ( UIButton *btn in _btnArr) {
            
            // 通用属性
            btn.titleLabel.font = [UIFont systemFontOfSize: 14.];
            btn.sakura.titleColor(ThemeKP(@"speedBtnFontColorNormal"), UIControlStateNormal);
            btn.sakura.titleColor(ThemeKP(@"speedBtnFontColorSelected"), UIControlStateSelected);
            [btn setSelected:NO];
        }

    }
    
    return self;
}

- (void)layoutSubviews {

    for (UIButton *btn in _btnArr) {

        NSString *strTitle;
        NSInteger index = [_btnArr indexOfObject:btn];
        btn.frame = CGRectMake(0, (0+index)* _eachHeight, self.width, _eachHeight);

        switch (index) {
            case 0:
            {
                strTitle = @"0.5x";
                btn.tag = 805;
            }
                break;
            case 1:
            {
                strTitle = @"0.75x";
                btn.tag = 875;
            }
                break;
            case 2:
            {
                strTitle = @"1.0x";
                btn.tag = 810;
            }
                break;
            case 3:
            {
                strTitle = @"1.25x";
                btn.tag = 825;
            }
                break;
            case 4:
            {
                strTitle = @"1.5x";
                btn.tag = 815;
            }
                break;
            case 5:
            {
                strTitle = @"2.0x";
                btn.tag = 820;
            }
                break;
            default:
                break;
        }
        // 设置标题和点击事件
        [btn setTitle: strTitle forState: UIControlStateNormal];
        [btn setTitle: strTitle forState: UIControlStateSelected];
        [btn addTarget:self action:@selector(btnAction:) forControlEvents: UIControlEventTouchUpInside];
        [self addSubview:btn];
    }
}

// 速度按钮点击事件
- (void)btnAction: (UIButton *)sender {
    // 改变样式
    for (UIButton *btn in _btnArr) {
        
        [btn setSelected: [btn isEqual:sender]];
        
    }
    // 发送通知
    [[NSNotificationCenter defaultCenter] postNotificationName: @"TKPlaySpeedViewNoti" object: [self changeCode: sender.tag]];
}

// 显示
- (void)show {
    if (self) {
        [self setHidden: NO];
    }
}

// 隐藏
- (void)hidden {
    if (self) {
        [self setHidden: YES];
    }
}

- (NSString *)changeCode: (NSInteger)code {
    
    switch (code) {
        case 805:
            return @"0.5";
            break;
        case 875:
            return @"0.75";
            break;
        case 810:
            return @"1.0";
            break;
        case 825:
            return @"1.25";
            break;
        case 815:
            return @"1.5";
            break;
        case 820:
            return @"2.0";
            break;
        default:
            return @"错误代码";
            break;
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

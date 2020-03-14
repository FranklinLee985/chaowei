//
//  TKColorSelectorView.m
//  EduClass
//
//  Created by talkcloud on 2019/3/28.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKColorSelectorView.h"
#import "TKColorListView.h"
#import "TKColorTipView.h"

#define TKColorViewBaseTag 500

@interface TKColorSelectorView ()

@property (nonatomic, strong) NSString        * currentColor;
@property (nonatomic, strong) NSString        * currentChooseColor;
@property (nonatomic, strong) UIView          * currentColorView;
@property (nonatomic, strong) TKColorListView * colorListView;
@property (nonatomic, strong) NSArray         * colorArray;
@property (nonatomic, strong) NSMutableArray  * colorViewMuArray;

@property (nonatomic, strong) TKColorTipView * colorTipView;
@property (nonatomic, strong) UIView * chooseTipView;

@end

@implementation TKColorSelectorView

- (instancetype)init {
    
    self = [super init];
    if (self) {
        
        self.backgroundColor = UIColor.clearColor;
        
        [self addSubview:self.currentColorView];
        [self.currentColorView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.left.bottom.equalTo(self);
            make.width.equalTo(self.mas_height);
        }];
        
        [self addSubview:self.colorListView];
        [self.colorListView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.bottom.right.equalTo(self);
            make.left.equalTo(self.currentColorView.mas_right).offset(10);
        }];
        
        [self.colorViewMuArray removeAllObjects];
        __block UIView * lastView = nil;
        for (int i = 0; i < self.colorArray.count; i ++) {
            
            UIView * colorView = [[UIView alloc] init];
            colorView.backgroundColor = [TKHelperUtil colorWithHexColorString:[self.colorArray objectAtIndex:i]];
            colorView.tag = TKColorViewBaseTag + i;
            [self.colorListView addSubview:colorView];
            [self.colorViewMuArray addObject:colorView];
            [colorView mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.and.bottom.equalTo(self.colorListView);
                make.width.equalTo(self.colorListView).dividedBy(self.colorArray.count);
                if (lastView == nil) {
                    make.left.offset(0);
                } else {
                    make.left.equalTo(lastView.mas_right);
                }
                lastView = colorView;
            }];
        }
        
        [self.colorListView addSubview:self.chooseTipView];
        self.chooseTipView.hidden = YES;
        [self.chooseTipView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.and.height.equalTo(lastView).offset(4);
            make.center.equalTo(lastView);
        }];
        
        self.colorTipView.hidden = YES;
        [self.colorListView addSubview:self.colorTipView];
        [self.colorTipView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(self.currentColorView);
            make.height.equalTo(self.colorTipView.mas_width).offset(2);
            make.bottom.equalTo(lastView.mas_top).offset(-4);
            make.centerX.equalTo(lastView);
        }];
    }
    return self;
}

- (UIView *)currentColorView {
    
    if (nil == _currentColorView) {
        _currentColorView = [[UIView alloc] init];
        _currentColorView.layer.borderWidth = 1;
        _currentColorView.layer.borderColor = UIColor.whiteColor.CGColor;
        _currentColorView.layer.cornerRadius = 2;
        _currentColorView.layer.masksToBounds = YES;
    }
    return _currentColorView;
}

- (UIView *)colorListView {
    
    if (nil == _colorListView) {
        _colorListView = [[TKColorListView alloc] init];
        _colorListView.backgroundColor = UIColor.clearColor;
        _colorListView.layer.borderWidth = 1;
        _colorListView.layer.borderColor = UIColor.whiteColor.CGColor;
        _colorListView.userInteractionEnabled = YES;
        
        tk_weakify(self);
        _colorListView.BeganBlock = ^(CGPoint point) {
            [weakSelf selectorViewTouchDown:point];
        };
        
        UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(selectorViewTap:)];
        [_colorListView addGestureRecognizer:tap];

        UIPanGestureRecognizer * pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(selectorViewPan:)];
        [_colorListView addGestureRecognizer:pan];
    }
    return _colorListView;
}

- (void) selectorViewTouchDown:(CGPoint)locat {
    
    NSInteger index = locat.x / (CGRectGetWidth(self.colorListView.frame) / self.colorArray.count);
    if (index < 0) index = 0;
    if (index >= self.colorArray.count) index = self.colorArray.count - 1;
    
    [self showColorTipWithIndex:index];
}

- (void) selectorViewTap:(UITapGestureRecognizer *)tap {
    
     if (tap.state == UIGestureRecognizerStateEnded) {
        
         self.chooseTipView.hidden = YES;
         self.colorTipView.hidden  = YES;
         
         [self changeDrawColor];
    }
}

- (void) selectorViewPan:(UIPanGestureRecognizer *)pan {
    
    if (pan.state == UIGestureRecognizerStateBegan || pan.state == UIGestureRecognizerStateChanged) {
  
        CGPoint locat = [pan locationInView:pan.view];
        NSInteger index = locat.x / (CGRectGetWidth(self.colorListView.frame) / self.colorArray.count);
        if (index < 0) index = 0;
        if (index >= self.colorArray.count) index = self.colorArray.count - 1;
        
        [self showColorTipWithIndex:index];
        
    } else if (pan.state == UIGestureRecognizerStateEnded) {
        
        self.chooseTipView.hidden = YES;
        self.colorTipView.hidden  = YES;
        
        [self changeDrawColor];
        
    } else {
        
        self.chooseTipView.hidden = YES;
        self.colorTipView.hidden  = YES;
        self.currentColorView.backgroundColor = [TKHelperUtil colorWithHexColorString:self.currentColor];
    }
}

- (void) showColorTipWithIndex:(NSInteger)index {
    
    NSString * chooseColor = [self.colorArray objectAtIndex:index];
    self.currentChooseColor = chooseColor;
    
    self.currentColorView.backgroundColor = [TKHelperUtil colorWithHexColorString:chooseColor];
    [self.colorTipView changeColor:chooseColor];
    
    
    UIView * colorView = [self.colorListView viewWithTag:TKColorViewBaseTag + index];
    [self.chooseTipView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.and.height.equalTo(colorView).offset(4);
        make.center.equalTo(colorView).priorityLow();
    }];
    self.chooseTipView.hidden = NO;
    
    [self.colorTipView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(self.currentColorView);
        make.height.equalTo(self.colorTipView.mas_width).offset(2);
        make.bottom.equalTo(colorView.mas_top).offset(-4);
        make.centerX.equalTo(colorView);
    }];
    self.colorTipView.hidden = NO;
}

- (UIView *)chooseTipView {
    if (_chooseTipView == nil) {
        _chooseTipView = [[UIView alloc] init];
        _chooseTipView.backgroundColor = UIColor.clearColor;
        _chooseTipView.layer.borderWidth = 2;
        _chooseTipView.layer.borderColor = UIColor.whiteColor.CGColor;
    }
    return _chooseTipView;
}

- (TKColorTipView *)colorTipView {
    if (nil == _colorTipView) {
        _colorTipView = [[TKColorTipView alloc] init];
        _colorTipView.backgroundColor = UIColor.clearColor;
    }
    return _colorTipView;
}

- (NSArray *)colorArray {
    if (_colorArray == nil) {
        _colorArray = [NSArray arrayWithObjects:
                       @"#000000", @"#9B9B9B", @"#FFFFFF", @"#FF87A3", @"#FF515F", @"#FF0000",
                       @"#E18838", @"#AC6B00", @"#864706", @"#FF7E0B", @"#FFD33B", @"#FFF52B",
                       @"#B3D330", @"#88BA44", @"#56A648", @"#53B1A4", @"#68C1FF", @"#058CE5",
                       @"#0B48FF", @"#C1C7FF", @"#D25FFA", @"#6E3087", @"#3D2484", @"#142473", nil];
    }
    return _colorArray;
}



- (void)setCurrentSelectColor:(NSString *)curColor {
    
    self.currentColorView.backgroundColor = [TKHelperUtil colorWithHexColorString:curColor];
    self.currentColor = curColor;
    self.currentChooseColor = curColor;
}

- (void) changeDrawColor {
    
    if (!self.currentChooseColor) {
        return;
    }
    
    self.currentColor = self.currentChooseColor;
    if (self.chooseBackBlock) {
        self.chooseBackBlock(self.currentChooseColor);
    }
}

@end

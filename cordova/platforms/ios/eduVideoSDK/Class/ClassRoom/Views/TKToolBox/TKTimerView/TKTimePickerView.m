//
//  TKTimePickerView.m
//  EduClass
//
//  Created by Evan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKTimePickerView.h"
#import "TKTimerView.h"
#import "TKEduSessionHandle.h"

#define ThemeKP(args) [@"TKToolsBox.TKTimerView." stringByAppendingString:args]
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKTimePickerView ()<UIPickerViewDelegate, UIPickerViewDataSource>

@property (nonatomic, strong) UILabel *pickerLabel;


@property (nonatomic, strong) NSMutableArray *minuteArray;
@property (nonatomic, strong) NSMutableArray *secondArray;

@property (nonatomic, strong) UIImageView *maohaoImage;
@property (nonatomic, strong) UIButton *startBtn;// 开始按钮

//@property (nonatomic, copy) NSString *minuteString;// 分钟
//@property (nonatomic, copy) NSString *secondString;// 秒


@end

@implementation TKTimePickerView



- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _minuteArray = [[NSMutableArray alloc] init];
        _secondArray = [[NSMutableArray alloc] init];
        
        for (int i = 0; i < 60; i++) {
            if (i<10) {
                [_minuteArray addObject:[NSString stringWithFormat:@"0%d", i]];
                [_secondArray addObject:[NSString stringWithFormat:@"0%d", i]];
            }else {
                [_minuteArray addObject:[NSString stringWithFormat:@"%d", i]];
                [_secondArray addObject:[NSString stringWithFormat:@"%d", i]];
            }
        }
        
        [_minuteArray addObject:[NSString stringWithFormat:@"%d", 60]];
        
        
        [self addSubview:self.minutePickerView];
        [self addSubview:self.secondPickerView];
        [self addSubview:self.maohaoImage];
        [self addSubview:self.startBtn];
        
        // 是否是巡课
        BOOL isPatrol = [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol;
        
        [self.minutePickerView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.mas_centerY);
            make.left.equalTo(self).offset(Fit(isPatrol ? 60 : 33));
            make.height.equalTo(self);
            make.width.equalTo(@Fit(76));
        }];
        
        
        [self.maohaoImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.mas_centerY);
            make.left.equalTo(self.minutePickerView.mas_right).offset(Fit(23));
            make.size.mas_equalTo(CGSizeMake(Fit(5), Fit(16)));
        }];
        
        
        [self.secondPickerView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.mas_centerY);
            make.left.equalTo(self.maohaoImage.mas_right).offset(Fit(18));
            make.height.equalTo(self);
            make.width.equalTo(@Fit(76));
        }];
        
        [self.startBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.mas_centerY);
            make.left.equalTo(self.secondPickerView.mas_right).offset(Fit(30));
            make.size.mas_equalTo(CGSizeMake(Fit(55), Fit(55)));
        }];
        
        _minuteString = @"05";
        
        self.startBtn.hidden = isPatrol;
        
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
}

#pragma mark - 开始按钮的点击事件
- (void)stratButtonAction:(UIButton *)sender {
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    if (_timePickerDelegate && [self.timePickerDelegate respondsToSelector:@selector(stratButtonActionWithMinute: sconed:)]) {
        [self.timePickerDelegate stratButtonActionWithMinute:_minuteString sconed:_secondString];
    }
}

#pragma mark UIPickerViewDelegate
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    if (pickerView == _minutePickerView) {
        return _minuteArray.count * 1000;
    }else {
        return _secondArray.count * 10000;
    }
    
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    NSString *title;
    if (pickerView == _minutePickerView) {
        title = _minuteArray[row % 61];
    }else {
        title = _secondArray[row % 60];
    }
    return title;
}

- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view {
    // 分割线
    for(UIView *singleLine in pickerView.subviews) {
        if (singleLine.frame.size.height < 1) {
            singleLine.sakura.backgroundColor(ThemeKP(@"tk_timerPicker_LineColor"));
        }
    }
    
    // 文字
    _pickerLabel = (UILabel*)view;
    if (!_pickerLabel){
        _pickerLabel = [[UILabel alloc] init];
        _pickerLabel.adjustsFontSizeToFitWidth = YES;
        [_pickerLabel setTextAlignment:NSTextAlignmentCenter];
        _pickerLabel.sakura.textColor(ThemeKP(@"tk_timerPicker_textColor"));
        _pickerLabel.font = [UIFont systemFontOfSize:Fit(25)];
    }
    
    _pickerLabel.text = [self pickerView:pickerView titleForRow:row forComponent:component];
    return _pickerLabel;
}



- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    
    if (pickerView == _minutePickerView) {
        _minuteString = _minuteArray[row % 61];
    }else {
        _secondString = _secondArray[row % 60];
    }
}

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component {
    return Fit(45);
}


#pragma mark - 懒加载
- (UIPickerView *)minutePickerView {
    if (!_minutePickerView) {
        _minutePickerView = [[UIPickerView alloc] init];
        _minutePickerView.delegate = self;
        _minutePickerView.dataSource = self;
        [_minutePickerView selectRow:5007 inComponent:0 animated:NO];
    }
    return _minutePickerView;
}

- (UIPickerView *)secondPickerView {
    if (!_secondPickerView) {
        _secondPickerView = [[UIPickerView alloc] init];
        _secondPickerView.delegate = self;
        _secondPickerView.dataSource = self;
        [_secondPickerView selectRow:4980 inComponent:0 animated:NO];
    }
    return _secondPickerView;
}

- (UIImageView *)maohaoImage {
    if (!_maohaoImage) {
        _maohaoImage = [[UIImageView alloc] init];
        _maohaoImage.sakura.image(ThemeKP(@"tk_timerPicker_maohao"));
    }
    return _maohaoImage;
}

- (UIButton *)startBtn {
    if (!_startBtn) {
        _startBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _startBtn.sakura.backgroundImage(ThemeKP(@"tk_timerPick_start"), UIControlStateNormal);
        [_startBtn setTitle:TKMTLocalized(@"tool.start") forState:UIControlStateNormal];
        _startBtn.titleLabel.font = [UIFont systemFontOfSize:Fit(16)];
        [_startBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_startBtn addTarget:self action:@selector(stratButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _startBtn;
}


@end

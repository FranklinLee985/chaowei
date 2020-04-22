//
//  TKStuTimerView.m
//  EduClass
//
//  Created by Evan on 2019/1/10.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKStuTimerView.h"
#import "TKCountdown.h"
#import "TKEduSessionHandle.h"


#define ThemeKP(args) [@"TKToolsBox.TKTimerView." stringByAppendingString:args]
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKStuTimerView ()

@property (nonatomic, strong) UIButton *stopButton;

@property (strong, nonatomic)  UILabel *minuteLabel_1;
@property (strong, nonatomic)  UILabel *minuteLabel_2;
@property (strong, nonatomic)  UILabel *secondLabel_1;
@property (strong, nonatomic)  UILabel *secondLabel_2;
@property (nonatomic, strong) UIImageView *maohaoImage;

@property (nonatomic, strong) TKCountdown *countdown;

@property (nonatomic) long nowTimeSp;
@property (nonatomic) long minuteSp;




@end

@implementation TKStuTimerView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        
        _countdown = [[TKCountdown alloc] init];
        [self.backgroundView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.centerY.equalTo(self.mas_centerY);
            make.width.equalTo(@Fit(279));
            make.height.equalTo(@Fit(162));
            make.edges.equalTo(self);
        }];
        self.titleLabel.text = TKMTLocalized(@"tool.jishiqi");
        self.cancelButton.hidden = YES;
        
        [self.contentView addSubview:self.minuteLabel_1];
        [self.contentView addSubview:self.minuteLabel_2];
        [self.contentView addSubview:self.secondLabel_1];
        [self.contentView addSubview:self.secondLabel_2];
        [self.contentView addSubview:self.maohaoImage];
        [self.contentView addSubview:self.stopButton];
        
        [self.minuteLabel_1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.left.equalTo(self.contentView).offset(Fit(17));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        [self.minuteLabel_2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.left.equalTo(self.minuteLabel_1.mas_right).offset(Fit(17));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        [self.maohaoImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.centerX.equalTo(self.contentView.mas_centerX);
            make.size.mas_equalTo(CGSizeMake(Fit(5), Fit(16)));
        }];
        
        
        [self.secondLabel_2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.right.equalTo(self.contentView.mas_right).offset(Fit(-17));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        [self.secondLabel_1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.right.equalTo(self.secondLabel_2.mas_left).offset(Fit(-17));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        
        [self.stopButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.contentView.mas_centerY);
            make.centerX.equalTo(self.contentView.mas_centerX);
            make.size.mas_equalTo(CGSizeMake(Fit(49), Fit(49)));
        }];
        
        self.stopButton.hidden = YES;
        
    }
    return self;
}

// 开始倒计时
- (void)startCountdownWithMinute:(long)minute second:(long)second receiveMsgTime:(long)time {
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    
    [formatter setDateFormat:@"yyyy-MM-dd HH-mm-ss"];
    NSDate *datenow = [NSDate date];
    NSString *currentTimeString_1 = [formatter stringFromDate:datenow];
    NSDate *applyTimeString_1 = [formatter dateFromString:currentTimeString_1];
    _nowTimeSp = (long long)[applyTimeString_1 timeIntervalSince1970];
    
    if (time > _nowTimeSp) {
        _nowTimeSp = time;
    }
    
    if (minute > 0 || second > 0) {
        long seconds = minute * 60 + second;//5分钟后的秒数
        _minuteSp = seconds + time;
    }
    
    
    if (!self.stopButton.hidden) {
        self.stopButton.hidden = YES;
    }
    [self startLongLongStartStamp:_nowTimeSp longlongFinishStamp:_minuteSp];
    
    
}


#pragma mark - 暂停
- (void)pauseTimerWithTimerArray:(NSArray *)array {
    self.stopButton.hidden = NO;
    [_countdown destoryTimer];
    self.minuteLabel_1.text = [NSString stringWithFormat:@"%@", array[0]];
    self.minuteLabel_2.text = [NSString stringWithFormat:@"%@", array[1]];
    self.secondLabel_1.text = [NSString stringWithFormat:@"%@", array[2]];
    self.secondLabel_2.text = [NSString stringWithFormat:@"%@", array[3]];
}


///此方法用两个时间戳做参数进行倒计时
-(void)startLongLongStartStamp:(long)strtL longlongFinishStamp:(long) finishL {
    __weak __typeof(self) weakSelf= self;
    
    
    [_countdown countDownWithStratTimeStamp:strtL finishTimeStamp:finishL completeBlock:^(NSInteger day, NSInteger hour, NSInteger minute, NSInteger second) {
        
        [weakSelf refreshUIDay:day hour:hour minute:minute second:second];
    }];
}

-(void)refreshUIDay:(NSInteger)day hour:(NSInteger)hour minute:(NSInteger)minute second:(NSInteger)second{
    
    
    NSString *str_1 = [NSString stringWithFormat:@"%ld", second];
    NSString *str_2 = [NSString stringWithFormat:@"%ld", minute];
    
    if (second == 0 && minute == 0) {
        [self playMP3];
        [_countdown destoryTimer];
    }
    
    if (minute<10) {
        self.minuteLabel_1.text = [NSString stringWithFormat:@"%@", @"0"];
        self.minuteLabel_2.text = [NSString stringWithFormat:@"%@",str_2];
    }else{
        self.minuteLabel_1.text = [NSString stringWithFormat:@"%@",[str_2 substringToIndex:1]];
        self.minuteLabel_2.text = [NSString stringWithFormat:@"%@",[str_2 substringFromIndex:1]];
    }
    if (second<10) {
        self.secondLabel_1.text = [NSString stringWithFormat:@"%@",@"0"];
        self.secondLabel_2.text = [NSString stringWithFormat:@"%@",str_1];
    }else{
        self.secondLabel_1.text = [NSString stringWithFormat:@"%@",[str_1 substringToIndex:1]];
        self.secondLabel_2.text = [NSString stringWithFormat:@"%@",[str_1 substringFromIndex:1]];
    }
}

- (void)dealloc {
    
    [_countdown destoryTimer];  //控制器释放的时候一点要停止计时器，以免再次进入发生错误
}

- (void)playMP3 {

    [[TKEduSessionHandle shareInstance] startPlayAudioFile:LOADWAV(@"timer_default.wav") loop:NO];
    
}


#pragma mark - 懒加载
- (UIButton *)stopButton {
    if (!_stopButton) {
        _stopButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _stopButton.sakura.backgroundImage(ThemeKP(@"tk_timerPicker_stupause"), UIControlStateNormal);
    }
    return _stopButton;
}


- (UILabel *)minuteLabel_1 {
    if (!_minuteLabel_1) {
        _minuteLabel_1 = [[UILabel alloc] init];
        _minuteLabel_1.sakura.backgroundColor(ThemeKP(@"tk_timerPicker_countDownTimeBackColor"));
        _minuteLabel_1.sakura.textColor(ThemeKP(@"tk_timerPicker_countDownTimeTextColor"));
        _minuteLabel_1.text = @"0";
        _minuteLabel_1.font = [UIFont systemFontOfSize:Fit(32)];
        _minuteLabel_1.textAlignment = NSTextAlignmentCenter;
        _minuteLabel_1.layer.cornerRadius = 5;
        _minuteLabel_1.layer.masksToBounds = YES;
    }
    return _minuteLabel_1;
}

- (UILabel *)minuteLabel_2 {
    if (!_minuteLabel_2) {
        _minuteLabel_2 = [[UILabel alloc] init];
        _minuteLabel_2.sakura.backgroundColor(ThemeKP(@"tk_timerPicker_countDownTimeBackColor"));
        _minuteLabel_2.sakura.textColor(ThemeKP(@"tk_timerPicker_countDownTimeTextColor"));
        _minuteLabel_2.font = [UIFont systemFontOfSize:Fit(32)];
        _minuteLabel_2.text = @"0";
        _minuteLabel_2.textAlignment = NSTextAlignmentCenter;
        _minuteLabel_2.layer.cornerRadius = 5;
        _minuteLabel_2.layer.masksToBounds = YES;
    }
    return _minuteLabel_2;
}

- (UILabel *)secondLabel_1 {
    if (!_secondLabel_1) {
        _secondLabel_1 = [[UILabel alloc] init];
        _secondLabel_1.sakura.backgroundColor(ThemeKP(@"tk_timerPicker_countDownTimeBackColor"));
        _secondLabel_1.sakura.textColor(ThemeKP(@"tk_timerPicker_countDownTimeTextColor"));
        _secondLabel_1.font = [UIFont systemFontOfSize:Fit(32)];
        _secondLabel_1.text = @"0";
        _secondLabel_1.textAlignment = NSTextAlignmentCenter;
        _secondLabel_1.layer.cornerRadius = 5;
        _secondLabel_1.layer.masksToBounds = YES;
    }
    return _secondLabel_1;
}

- (UILabel *)secondLabel_2 {
    if (!_secondLabel_2) {
        _secondLabel_2 = [[UILabel alloc] init];
        _secondLabel_2.sakura.backgroundColor(ThemeKP(@"tk_timerPicker_countDownTimeBackColor"));
        _secondLabel_2.sakura.textColor(ThemeKP(@"tk_timerPicker_countDownTimeTextColor"));
        _secondLabel_2.font = [UIFont systemFontOfSize:Fit(32)];
        _secondLabel_2.text = @"0";
        _secondLabel_2.textAlignment = NSTextAlignmentCenter;
        _secondLabel_2.layer.cornerRadius = 5;
        _secondLabel_2.layer.masksToBounds = YES;
    }
    return _secondLabel_2;
}

- (UIImageView *)maohaoImage {
    if (!_maohaoImage) {
        _maohaoImage = [[UIImageView alloc] init];
        _maohaoImage.sakura.image(ThemeKP(@"tk_timerPicker_maohao"));
    }
    return _maohaoImage;
}

@end

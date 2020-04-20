//
//  TKTimeCountdownView.m
//  EduClass
//
//  Created by Evan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKTimeCountdownView.h"
#import "TKCountdown.h"
#import "TKEduSessionHandle.h"


#define ThemeKP(args) [@"TKToolsBox.TKTimerView." stringByAppendingString:args]
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKTimeCountdownView ()

@property (nonatomic, strong) TKCountdown *countdown;


@property (nonatomic, strong) UIButton *stopButton;
@property (nonatomic, strong) UIButton *startAndPauseButton;

@property (strong, nonatomic)  UILabel *minuteLabel_1;
@property (strong, nonatomic)  UILabel *minuteLabel_2;
@property (strong, nonatomic)  UILabel *secondLabel_1;
@property (strong, nonatomic)  UILabel *secondLabel_2;

@property (nonatomic, strong) UIImageView *maohaoImage;


@property (nonatomic) long nowTimeSp;
@property (nonatomic) long minuteSp;




@end

@implementation TKTimeCountdownView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        _countdown = [[TKCountdown alloc] init];
        
        [self addSubview:self.minuteLabel_1];
        [self addSubview:self.minuteLabel_2];
        [self addSubview:self.secondLabel_1];
        [self addSubview:self.secondLabel_2];
        [self addSubview:self.maohaoImage];
        [self addSubview:self.stopButton];
        [self addSubview:self.startAndPauseButton];
        
        BOOL isPatrol = [TKRoomManager instance].localUser.role == TKUserType_Patrol;
        
        [self.minuteLabel_1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self).offset(Fit(isPatrol?45:23));
            make.left.equalTo(self).offset(Fit(48));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        [self.minuteLabel_2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.minuteLabel_1);
            make.left.equalTo(self.minuteLabel_1.mas_right).offset(Fit(17));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        [self.maohaoImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.minuteLabel_2.mas_centerY);
            make.left.equalTo(self.minuteLabel_2.mas_right).offset(Fit(22));
            make.size.mas_equalTo(CGSizeMake(Fit(5), Fit(16)));
        }];
        
        [self.secondLabel_1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.minuteLabel_1);
            make.left.equalTo(self.maohaoImage.mas_right).offset(Fit(22));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        [self.secondLabel_2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.minuteLabel_1);
            make.left.equalTo(self.secondLabel_1.mas_right).offset(Fit(17));
            make.size.mas_equalTo(CGSizeMake(Fit(39), Fit(50)));
        }];
        
        
        [self.stopButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.minuteLabel_2.mas_bottom).offset(Fit(20));
            make.left.equalTo(self).offset(Fit(107));
            make.size.mas_equalTo(CGSizeMake(Fit(34), Fit(34)));
        }];
        
        [self.startAndPauseButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.minuteLabel_2.mas_bottom).offset(Fit(20));
            make.left.equalTo(self.stopButton.mas_right).offset(Fit(55));
            make.size.mas_equalTo(CGSizeMake(Fit(34), Fit(34)));
        }];
        
        self.stopButton.hidden = self.startAndPauseButton.hidden = isPatrol;
        
        
    }
    return self;
}


- (void)stopCountDownButtonAction:(UIButton *)sender {
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    _startAndPauseButton.selected = NO;
    [_countdown destoryTimer];
    if (self.delegate && [self.delegate respondsToSelector:@selector(stopCountDownButtonAction)]) {
        [self.delegate stopCountDownButtonAction];
    }
}

#pragma mark - 开始暂停 按钮 点击事件
- (void)startAndPauseButtonAction:(UIButton *)sender {
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    sender.selected = !sender.selected;
    if (sender.selected) {// 暂停
//        [_countdown destoryTimer];
        [self startAndPauseWithIsStatus:false];
    }else {// 开始
        [self startAndPauseWithIsStatus:true];
//        [self startCountdownWithMinute:_restratMinute second:_restartSecond];
    }
}

#pragma mark - 暂停
- (void)pauseTimerWithTimerArray:(NSArray *)array {
    _startAndPauseButton.selected = YES;
    [_countdown destoryTimer];
    self.minuteLabel_1.text = [NSString stringWithFormat:@"%@", array[0]];
    self.minuteLabel_2.text = [NSString stringWithFormat:@"%@", array[1]];
    self.secondLabel_1.text = [NSString stringWithFormat:@"%@", array[2]];
    self.secondLabel_2.text = [NSString stringWithFormat:@"%@", array[3]];
}

- (void)playBackPauseWithTimerArray:(NSArray *)array {
    [_countdown destoryTimer];
    self.minuteLabel_1.text = [NSString stringWithFormat:@"%@", array[0]];
    self.minuteLabel_2.text = [NSString stringWithFormat:@"%@", array[1]];
    self.secondLabel_1.text = [NSString stringWithFormat:@"%@", array[2]];
    self.secondLabel_2.text = [NSString stringWithFormat:@"%@", array[3]];
}


- (void)startAndPauseWithIsStatus:(BOOL)isStatus {
    
    NSMutableArray *timerArray = [self arrayWithMinute:_restratMinute second:_restartSecond];
    
    NSDictionary *dataDic = @{
                              @"isStatus":isStatus?@YES:@NO,
                              @"sutdentTimerArry":timerArray,
                              @"isShow":@NO,
                              @"isRestart":@NO
                              };
    
    NSString *str = [TKUtil dictionaryToJSONString:dataDic];
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sTimer ID:@"timerMesg" To:sTellAll Data:str Save:true AssociatedMsgID:nil AssociatedUserID:nil expires:0 completion:nil];
}

- (NSMutableArray *)arrayWithMinute:(NSInteger)min second:(NSInteger)sec {
    NSMutableArray *timerArray = [NSMutableArray array];
    if (min < 10) {
        [timerArray insertObject:@(0) atIndex:0];
        [timerArray insertObject:@(min) atIndex:1];
    }else {
        NSInteger decade = min / 10;
        NSInteger unit = min % 10;
        [timerArray insertObject:@(decade) atIndex:0];
        [timerArray insertObject:@(unit) atIndex:1];
    }
    
    if (sec < 10) {
        [timerArray insertObject:@(0) atIndex:2];
        [timerArray insertObject:@(sec) atIndex:3];
    }else {
        NSInteger decade = sec / 10;
        NSInteger unit = sec % 10;
        [timerArray insertObject:@(decade) atIndex:2];
        [timerArray insertObject:@(unit) atIndex:3];
    }
    return timerArray;
}


// 暂停
- (void)pauseTimer {
    [_countdown destoryTimer];
}

- (void)startCountdownWithMinute:(long)minute second:(long)second receiveMsgTime:(long)time{
    
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
        _minuteSp = time + seconds;
    }
    
    if (_startAndPauseButton.selected) {
        _startAndPauseButton.selected = NO;
    }
    
    [self startLongLongStartStamp:_nowTimeSp longlongFinishStamp:_minuteSp];
    
}


- (void)startCountdownWithStartData:(NSDate *)StartDate finishDate:(NSDate *)finishDate {
    __weak __typeof(self) weakSelf= self;
    [_countdown countDownWithStratTimeData:StartDate finishTimeData:finishDate completeBlock:^(NSInteger day, NSInteger hour, NSInteger minute, NSInteger second) {
        [weakSelf refreshUIDay:day hour:hour minute:minute second:second];
    }];
}


///此方法用两个时间戳做参数进行倒计时
-(void)startLongLongStartStamp:(long)strtL longlongFinishStamp:(long) finishL {
    __weak __typeof(self) weakSelf= self;
    
    NSLog(@"second = %ld, minute = %ld", strtL, finishL);
    
    [_countdown countDownWithStratTimeStamp:strtL finishTimeStamp:finishL completeBlock:^(NSInteger day, NSInteger hour, NSInteger minute, NSInteger second) {
        
        [weakSelf refreshUIDay:day hour:hour minute:minute second:second];
    }];
}

-(void)refreshUIDay:(NSInteger)day hour:(NSInteger)hour minute:(NSInteger)minute second:(NSInteger)second{
    
    
    _restratMinute = (long)minute;
    _restartSecond = (long)second;
    
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
        _stopButton.sakura.backgroundImage(ThemeKP(@"tk_timerPicker_countSownStop"), UIControlStateNormal);
        [_stopButton addTarget:self action:@selector(stopCountDownButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _stopButton;
}

- (UIButton *)startAndPauseButton {
    if (!_startAndPauseButton) {
        _startAndPauseButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _startAndPauseButton.sakura.backgroundImage(ThemeKP(@"tk_timerPicker_countDownPause"), UIControlStateNormal);
        _startAndPauseButton.sakura.backgroundImage(ThemeKP(@"tk_timerPicker_countDownStrat"), UIControlStateSelected);
        [_startAndPauseButton addTarget:self action:@selector(startAndPauseButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _startAndPauseButton;
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

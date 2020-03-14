//
//  TKTimerView.m
//  EduClass
//
//  Created by Evan on 2019/1/8.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKTimerView.h"
#import "TKTimePickerView.h"
#import "TKTimeCountdownView.h"
#import "TKEduSessionHandle.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)


@interface TKTimerView ()<TimePickerDelegate, TKCountDownDelegate>

@property (nonatomic, strong) TKTimePickerView *timePickerView;// 时间选择


@end

@implementation TKTimerView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self.backgroundView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.centerY.equalTo(self.mas_centerY);
            make.width.equalTo(@Fit(340));
            make.height.equalTo(@Fit(198));
            make.edges.equalTo(self);
        }];
        self.titleLabel.text = TKMTLocalized(@"tool.jishiqi");
        [self.cancelButton addTarget:self action:@selector(cancleButtonAction:) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.timePickerView];
        [self.contentView addSubview:self.timeCountdownView];
        
        [self.timePickerView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
        }];
        
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
            self.cancelButton.hidden = YES;
            self.timePickerView.minutePickerView.userInteractionEnabled = NO;
            self.timePickerView.secondPickerView.userInteractionEnabled = NO;
        }
        
        [self.timeCountdownView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
        }];
        self.timeCountdownView.hidden = YES;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(startOrPauseChangeNotification:) name:@"TimerStateDuringPlaybackNotification" object:nil];
    }
    return self;
}

// 回放时 点击暂停按钮 计时器响应
- (void)startOrPauseChangeNotification:(NSNotification *)center {
    
    if (self.timeCountdownView.hidden) {
        return;
    }
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"YYYY年MM月dd日HH:mm:ss"];
    NSDate *datenow = [NSDate date];
    NSString *currentTimeString_1 = [formatter stringFromDate:datenow];
    NSDate *applyTimeString_1 = [formatter dateFromString:currentTimeString_1];
    long nowTimeSp = (long long)[applyTimeString_1 timeIntervalSince1970];
    
    NSMutableArray *timeArray = [self arrayWithMinute:self.timeCountdownView.restratMinute second:self.timeCountdownView.restartSecond];
    
    BOOL state = [center.object boolValue];
    if (state) {
        [self.timeCountdownView playBackPauseWithTimerArray:timeArray];
    }else {
        [self.timeCountdownView startCountdownWithMinute:self.timeCountdownView.restratMinute second:self.timeCountdownView.restartSecond receiveMsgTime:nowTimeSp];
    }
    
    
}

- (void)cancleButtonAction:(UIButton *)sender {
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sTimer ID:@"timerMesg" To:sTellAll Data:@"" completion:nil];
}

#pragma mark - 开始计时
- (void)startTimerCountdownWithMinute:(long)minute second:(long)second receiveMsgTime:(long)time{
    self.timePickerView.hidden = YES;
    self.timeCountdownView.hidden = NO;
    [self.timeCountdownView startCountdownWithMinute:minute second:second receiveMsgTime:time];
}

#pragma mark - 暂停
- (void)pauseTimerWithTimerArray:(NSArray *)array {
    [self.timeCountdownView pauseTimerWithTimerArray:array];
}

#pragma mark - 停止 -- 重新开始
- (void)stopCountDown {
    [self.timeCountdownView pauseTimer];
    self.timePickerView.hidden = NO;
    self.timePickerView.minuteString = @"05";
    self.timePickerView.secondString = @"00";
    self.timeCountdownView.hidden = YES;
    [self.timePickerView.minutePickerView selectRow:5007 inComponent:0 animated:NO];
    [self.timePickerView.secondPickerView selectRow:4980 inComponent:0 animated:NO];
}

#pragma mark - 代理方法
- (void)stratButtonActionWithMinute:(NSString *)minute sconed:(NSString *)sconed {
    
    NSInteger min = [minute integerValue];
    NSInteger sec = [sconed integerValue];
    NSMutableArray *timerArray = [self arrayWithMinute:min second:sec];
    
    NSDictionary *dataDic = @{
                              @"isStatus":@YES,
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


- (void)stopCountDownButtonAction {
    
    
    NSArray *timerArray = @[@0, @5, @0, @0];
    
    NSDictionary *dataDic = @{
                              @"isStatus":@NO,
                              @"sutdentTimerArry":timerArray,
                              @"isShow":@NO,
                              @"isRestart":@YES
                              };
    
    NSString *str = [TKUtil dictionaryToJSONString:dataDic];
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sTimer ID:@"timerMesg" To:sTellAll Data:str Save:true AssociatedMsgID:nil AssociatedUserID:nil expires:0 completion:nil];
    
}


#pragma mark - 懒加载
- (TKTimePickerView *)timePickerView {
    if (!_timePickerView) {
        _timePickerView = [[TKTimePickerView alloc] init];
        _timePickerView.timePickerDelegate = self;
    }
    return _timePickerView;
}

- (TKTimeCountdownView *)timeCountdownView {
    if (!_timeCountdownView) {
        _timeCountdownView = [[TKTimeCountdownView alloc] init];
        _timeCountdownView.delegate = self;
    }
    return _timeCountdownView;
}

@end

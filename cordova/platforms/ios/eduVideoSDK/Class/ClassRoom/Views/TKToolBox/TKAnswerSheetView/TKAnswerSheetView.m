//
//  TKAnswerSheetView.m
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetView.h"
#import "TKAnswerSheetSetupView.h"
#import "TKAnswerSheetDetailView.h"
#import "TKAnswerSheetRecordView.h"
#import "TKAnswerSheetSubmitView.h"
#import "TKAnswerSheetData.h"
#import "TKEduSessionHandle.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKAnswerSheetView()<TKAnswerSheetSetupViewDelegate,TKAnswerSheetDetailViewDelegate,TKAnswerSheetRecordViewDelegate,TKAnswerSheetSubmitViewDelegate>
{
    int hours;        //时
    int minutes;      //分
    int seconds;      //秒
}
@property (strong , nonatomic) TKAnswerSheetSetupView  *setupView;
@property (strong , nonatomic) TKAnswerSheetDetailView *detailView;
@property (strong , nonatomic) TKAnswerSheetRecordView *recordView;
@property (strong , nonatomic) TKAnswerSheetSubmitView *submitView;

@property (strong , nonatomic) NSTimer *timer;

@property (assign , nonatomic) CGFloat detailViewHeight;

@end

@implementation TKAnswerSheetView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self commonInit];
        
        [self.contentView addSubview:self.setupView];
        [self.contentView addSubview:self.detailView];
        [self.contentView addSubview:self.recordView];
        [self.contentView addSubview:self.submitView];
        
        [self.setupView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
        }];
        
        [self.detailView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
        }];
        
        [self.recordView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
        }];
        
        [self.submitView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView);
        }];

    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)commonInit
{
    self.titleLabel.text = TKMTLocalized(@"tool.datiqiqi");
    
    self.state = TKAnswerSheetState_End;
    
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role == TKUserType_Teacher) {
        self.cancelButton.hidden = NO;
    }else{
        self.cancelButton.hidden = YES;
    }
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(timerStateChangeNotification:) name:@"TimerStateDuringPlaybackNotification" object:nil];
}

- (void)timerStateChangeNotification:(NSNotification *)center
{
    if (!_timer) {
        return;
    }
    
    BOOL state = [center.object boolValue];

    if (state) {
        //关闭定时器
        [self.timer setFireDate:[NSDate distantFuture]];
    }else{
        //开启定时器
        [self.timer setFireDate:[NSDate distantPast]];
    }
}

- (void)showTimeWithTimeStamp:(NSString *)time
{
    if (!time.length) {
        return;
    }
    
    int interval = 0;
    if (time.length < 10) {
        interval = time.intValue;
    }else{
        //记录开始的时间戳
        [TKAnswerSheetData shareInstance].startTime = time.longLongValue;
        
        //当前时间戳
        int interval0  = [[NSDate date] timeIntervalSince1970];
        interval = interval0 - time.intValue;
    }
    
    [self timeStringWithTime:interval];
    [self timerAction:nil];
}

- (void)setDict:(NSDictionary *)dict
{
    _dict = dict;
    
    if (!dict) {
        return;
    }
    
    //没反回自己提交的答案需要本地记录
    //...
    
    //将数据处理为同一种格式
    NSDictionary *sameDict = [self sameDataWithDict:dict];
    
    //存储数据
    if ([sameDict.allKeys containsObject:@"quesID"]) {
        [TKAnswerSheetData shareInstance].quesID  = [sameDict objectForKey:@"quesID"];
    }
    
    if ([sameDict.allKeys containsObject:@"options"]) {
        [TKAnswerSheetData shareInstance].options = [sameDict valueForKey:@"options"];
    }
    
    if ([sameDict.allKeys containsObject:@"answer"]) {
        [TKAnswerSheetData shareInstance].answerABC = [self answerABC:[sameDict valueForKey:@"answer"]];
        [TKAnswerSheetData shareInstance].answer123 = [sameDict valueForKey:@"answer"];
    }
    
    if ([sameDict.allKeys containsObject:@"count"]) {
        [TKAnswerSheetData shareInstance].count = [[sameDict valueForKey:@"count"] integerValue];
    }
    
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;

    if (self.viewType == TKAnswerSheetType_Submit) {
        self.submitView.optionsCount =  [[sameDict objectForKey:@"options"] count];
        self.submitView.answerArray  =  [sameDict objectForKey:@"answer"];
        return;
    }else if (self.viewType == TKAnswerSheetType_Detail){
        
        self.detailView.dict   = sameDict;
        
        NSArray *options = [sameDict valueForKey:@"options"];
        NSArray *answer  = [sameDict valueForKey:@"answer"];
        //更新高度
        [self didPressReleaseButton:nil answer:answer option:options];
        
        if (role == TKUserType_Student) {
            //公布结果
            int time = [[sameDict valueForKey:@"time"] intValue];
            NSString *timeString = [NSString stringWithFormat:@"%@：%@",TKMTLocalized(@"tool.time"),[self timeStringWithTime:time]];
            self.detailView.timeString = timeString;
            self.recordView.timeString = timeString;
        }
    } else if (self.viewType == TKAnswerSheetType_Record) {
        self.detailView.dict   = sameDict;
        NSArray *options = [sameDict valueForKey:@"options"];
        self.detailView.optionsCount = options.count;
    }
    
}

//将数据处理为同一种格式
- (NSDictionary *)sameDataWithDict:(NSDictionary *)dict
{
    //count:多少人提交答案 5
    //answer:正确答案  数组类型 @[@2，@3]
    //options：每个答案 有多少人选择 数组类型 @[0,0,2,4]
    //time：耗时 30
    //timeStamp:答题卡开始时的时间戳
    //quesID

    if (![dict isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    NSMutableDictionary *resultDict = [NSMutableDictionary dictionary];
    
    //第1种数据类型
    if ([dict.allKeys containsObject:@"result"] && [dict.allKeys containsObject:@"ansTime"]) {
        //处理数据
        
        [resultDict setValue:dict[@"resultNum"]    forKey:@"count"];
        [resultDict setValue:dict[@"rightOptions"] forKey:@"answer"];
        [resultDict setValue:dict[@"result"]       forKey:@"options"];
        [resultDict setValue:dict[@"ansTime"]      forKey:@"time"];

    }
    
    //第2种数据类型
    if ([dict.allKeys containsObject:@"answerCount"] && [dict.allKeys containsObject:@"values"]) {
        //处理数据
        
        [resultDict setValue:dict[@"answerCount"] forKey:@"count"];
        
        NSDictionary *temp     = [dict objectForKey:@"values"];
        NSInteger optionsCount = [TKAnswerSheetData shareInstance].options.count;

        if (optionsCount == 0) {
            optionsCount = 8;
        }
        NSMutableArray *options = [NSMutableArray arrayWithCapacity:10];
        for (int i = 0; i<optionsCount; i++) {
            NSString *key = [NSString stringWithFormat:@"%d",i];
            if ([temp.allKeys containsObject:key]) {
                [options addObject:@([temp[key] integerValue])];
            }else{
                [options addObject:@0];
            }
        }
        [resultDict setValue:options  forKey:@"options"];
    }
    
    //第3种数据类型
    if ([dict.allKeys containsObject:@"state"] && [dict.allKeys containsObject:@"quesID"]) {
        //处理数据
        NSNumber *count = [dict valueForKeyPath:@"state.resultNum"];
        [resultDict setValue:count forKey:@"count"];
        [resultDict setValue:dict[@"rightOptions"] forKey:@"answer"];
        [resultDict setValue:dict[@"quesID"] forKey:@"quesID"];
//        NSString *time = [dict valueForKeyPath:@"state.event.ts"];
//        [resultDict setValue:time forKey:@"time"];
        
        NSArray *array = [dict valueForKey:@"options"];
        NSMutableArray *options = [NSMutableArray arrayWithCapacity:10];
        for (int i = 0; i<array.count; i++) {
            [options addObject:@0];
        }
        [resultDict setValue:options  forKey:@"options"];
    }
    
    return resultDict;
}

- (NSArray *)answerABC:(NSArray *)answer123
{
    if (answer123.count == 0) {
        return nil;
    }
    
    NSMutableArray *answer = [NSMutableArray arrayWithCapacity:10];
    for (int i = 0; i < answer123.count; i++) {
        int temp = [answer123[i] intValue];
        NSString *string = [NSString stringWithFormat:@"%c",temp + 65];
        [answer addObject:string];
    }
    return answer;
}

- (NSString *)timeStringWithTime:(int)time
{
    hours   = (int)floor(time / 3600);
    minutes = (int)floor((time - hours * 3600) / 60);
    seconds = (int)floor(time - hours * 3600 - minutes * 60);
    
    if (hours < 0) {
        hours = 0;
    }
    
    if (minutes < 0) {
        minutes = 0;
    }
    
    if (seconds < 0) {
        seconds = 0;
    }
    
    NSString *string = [NSString stringWithFormat:@"%02d:%02d:%02d",hours,minutes,seconds];
    return string;
}

- (void)setViewType:(TKAnswerSheetType)viewType
{
    _viewType = viewType;
    BOOL isPatrol = [TKRoomManager instance].localUser.role == TKUserType_Patrol;
    if (viewType == TKAnswerSheetType_Setup) {
        [self.setupView reset];
        self.detailView.hidden = YES;
        self.recordView.hidden = YES;
        self.submitView.hidden = YES;
        self.setupView.hidden  = NO;
        
        [self didChangeHeight:361-(isPatrol?190:90)];
        
    }else if (viewType == TKAnswerSheetType_Submit){
        self.detailView.hidden = YES;
        self.recordView.hidden = YES;
        self.submitView.hidden = NO;
        self.setupView.hidden  = YES;
        
    }else if (viewType == TKAnswerSheetType_Detail){
        self.detailView.hidden = NO;
        self.recordView.hidden = YES;
        self.submitView.hidden = YES;
        self.setupView.hidden  = YES;
        
    }else if (viewType == TKAnswerSheetType_Record){
        self.detailView.hidden = YES;
        self.setupView.hidden  = YES;
        self.recordView.hidden = NO;
        self.submitView.hidden  = YES;
    }
}

- (void)setState:(TKAnswerSheetState)state
{
    _state = state;
    if (state == TKAnswerSheetState_Start) {
        
        //当身份为老师时 添加计时器
        if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Student) {
            [self timer];
            [self.detailView startTimer];
            [self.recordView fixFinishButtonState:NO];
        }
        [self.detailView releaseButtonShow:NO buttonSelected:NO];
        [self.recordView releaseButtonShow:NO buttonSelected:NO];

    }else if(state == TKAnswerSheetState_End){
        [self invalidateTimer];
        [self.detailView invalidateTimer];
        [self.recordView fixFinishButtonState:YES];
        if ([TKEduClassRoom shareInstance].roomJson.configuration.autoShowAnswerAfterAnswer) {
            [self.detailView releaseButtonShow:YES buttonSelected:NO];
            [self.recordView releaseButtonShow:YES buttonSelected:NO];
        }

    }else if (state == TKAnswerSheetState_Release){
        
        if ([TKEduClassRoom shareInstance].roomJson.configuration.autoShowAnswerAfterAnswer) {
            [self.detailView releaseButtonShow:YES buttonSelected:YES];
            [self.recordView releaseButtonShow:YES buttonSelected:YES];
        }
        
        //通过设置hidden 设置数据
        self.detailView.hidden = self.detailView.hidden;
        self.recordView.hidden = self.recordView.hidden;
    }
}

- (void)invalidateTimer
{
    if (_timer) {
        [_timer invalidate];
        _timer = nil;
    }
}

#pragma mark - Action
- (void)timerAction:(NSTimer *)timer
{
    seconds = seconds + 1;
    if (seconds >= 60) {
        seconds = 0;
        minutes = minutes + 1;
    }
    
    if (minutes >= 60) {
        minutes = 0;
        hours = hours + 1;
    }
    
    if (hours >= 99) {
        seconds = 0;
        minutes = 0;
        hours = 0;
    }
    
    if (hours < 0) {
        hours = 0;
    }
    
    if (minutes < 0) {
        minutes = 0;
    }
    
    if (seconds < 0) {
        seconds = 0;
    }
    
    NSString *timeString = [NSString stringWithFormat:@"%@：%02d:%02d:%02d",TKMTLocalized(@"tool.time"),hours,minutes,seconds];
    self.detailView.timeString = timeString;
    self.recordView.timeString = timeString;
}

#pragma mark - TKAnswerSheetSetupViewDelegate
- (void)didPressReleaseButton:(UIButton *)button answer:(NSArray *)answers option:(NSArray *)options
{
    self.viewType = TKAnswerSheetType_Detail;

    self.detailView.optionsCount = options.count;
    
    //根据数据个数更改约束
    [self heightForViewWithOptions:options.count];
}

- (void)didChangeHeight:(CGFloat)height
{
    [self.backgroundView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
        make.width.equalTo(@Fit(481));
        make.height.equalTo(@Fit(height));
        make.edges.equalTo(self);
    }];
    
}

- (void)heightForViewWithOptions:(NSInteger)count
{
    NSInteger cellCount    = count > 4 ? 4 : count;
    CGFloat cellHeight     = 45 * cellCount;
    
    self.detailViewHeight = cellHeight + 200;
    
    [self.backgroundView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
        make.width.equalTo(@Fit(481));
        make.height.equalTo(@Fit(self.detailViewHeight));
        make.edges.equalTo(self);

    }];
}
#pragma mark - 重写父类方法

- (void)cancelButtonAction
{
    [self removeFromSuperview];
    //发送结束信令
    NSDictionary *roomDict = [TKEduSessionHandle shareInstance].roomMgr.getRoomProperty;
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];
    
    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:@"Question" ID:msgID To:sTellAll Data:@{} completion:nil];
    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:@"PublishResult" ID:@"PublishResult" To:sTellAll Data:@{} completion:nil];
}

- (void)removeFromSuperview
{
    [super removeFromSuperview];
    [self invalidateTimer];
}

#pragma mark - TKAnswerSheetDetailViewDelegate
//切换到统计
- (void)didPressRecordButton:(UIButton *)button
{
    self.viewType = TKAnswerSheetType_Record;

    [self.backgroundView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
        make.width.equalTo(@Fit(481));
        make.height.equalTo(@Fit(390));
        make.edges.equalTo(self);

    }];
}

- (void)didPublishAnswer
{
    [self invalidateTimer];
}

//获取答题卡状态
- (NSInteger)answerSheetType
{
    return self.state;
}

#pragma mark - TKAnswerSheetRecordViewDelegate
//切换到详情
- (void)didPressDetailButton:(UIButton *)button
{
    self.viewType = TKAnswerSheetType_Detail;
    
    [self.backgroundView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
        make.width.equalTo(@Fit(481));
        make.height.equalTo(@Fit(self.detailViewHeight));
        make.edges.equalTo(self);

    }];
    
}

- (TKAnswerSheetSetupView *)setupView
{
    if (!_setupView) {
        _setupView = [[TKAnswerSheetSetupView alloc] init];
        _setupView.delegate = self;
    }
    return _setupView;
}

- (TKAnswerSheetDetailView *)detailView
{
    if (!_detailView) {
        _detailView = [[TKAnswerSheetDetailView alloc] init];
        _detailView.delegate = self;

    }
    return _detailView;
}

- (TKAnswerSheetRecordView *)recordView
{
    if (!_recordView) {
        _recordView = [[TKAnswerSheetRecordView alloc] init];
        _recordView.delegate = self;
    }
    return _recordView;
}

- (TKAnswerSheetSubmitView *)submitView
{
    if (!_submitView) {
        _submitView = [[TKAnswerSheetSubmitView alloc] init];
        _submitView.delegate = self;
    }
    return _submitView;

}

- (NSTimer *)timer
{
    if (!_timer) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerAction:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
    return _timer;
}

@end

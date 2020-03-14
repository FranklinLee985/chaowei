//
//  TKAnswerSheetDetailView.m
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetDetailView.h"
#import "TKAnswerSheetDetailCell.h"
#import "TKAnswerSheetData.h"
#import "TKAnswerSheetLayout.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKAnswerSheetDetailView()<UICollectionViewDelegate,UICollectionViewDataSource>

@property (strong , nonatomic) UICollectionView    *collectionView;
@property (strong , nonatomic) TKAnswerSheetLayout *flowLayout;

@property (strong , nonatomic) UILabel *timeLabel;
@property (strong , nonatomic) UILabel *numLabel;
@property (strong , nonatomic) UILabel *answerLabel;

@property (strong , nonatomic) UIButton *detailButton;
//通过控制selected属性改变button的标题 no：结束答题 yes：重新开始
@property (strong , nonatomic) UIButton *finishButton;
@property (strong , nonatomic) UIButton *releaseButton;

@property (strong , nonatomic) NSTimer *timer;

@end

@implementation TKAnswerSheetDetailView

static NSString * const reuseID = @"TKAnswerSheetDetailCellID";

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        
        [self addSubview:self.collectionView];
        [self.collectionView registerClass:[TKAnswerSheetDetailCell class] forCellWithReuseIdentifier:reuseID];
        
        [self addSubview:self.timeLabel];
        [self addSubview:self.numLabel];
        [self addSubview:self.answerLabel];

        [self addSubview:self.detailButton];
        [self addSubview:self.finishButton];
        [self addSubview:self.releaseButton];

        [self.numLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(10);
            make.top.equalTo(self.mas_top).offset(Fit(20));
        }];
        
        [self.timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.top.equalTo(self.mas_top).offset(Fit(20));
        }];
        
        [self.answerLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(20);
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(30));
        }];
        
        [self.releaseButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.answerLabel.mas_right).offset(15);
            make.centerY.equalTo(self.answerLabel.mas_centerY);
            make.height.equalTo(@Fit(26));
            make.width.equalTo(@Fit(85));
            
        }];
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.right.equalTo(self.mas_right);
            make.top.equalTo(self.numLabel.mas_bottom).offset(Fit(20));
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(88));
        }];
        
        [self.detailButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-20);
            make.top.equalTo(self.mas_top).offset(Fit(20));
            make.height.equalTo(@Fit(22));
            make.width.equalTo(@Fit(56));
        }];
        
        [self.finishButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-20);
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(16));
            make.height.equalTo(@Fit(46));
            make.width.equalTo(@Fit(106));
        }];
        
        self.finishButton.hidden = ([TKRoomManager instance].localUser.role == TKUserType_Patrol ||
                                    [TKRoomManager instance].localUser.role == TKUserType_Student);
        
    }
    return self;
}

- (void)commonInit
{
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role == TKUserType_Teacher){
        self.finishButton.hidden = NO;
    }else{
        self.finishButton.hidden = YES;
    }
}

//开启定时器获取答案
- (void)startTimer
{
    //当身份为老师时 添加计时器
    if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Student) {
        [self timer];
    }
    
    //重置button标题
    self.finishButton.selected = NO;
//    self.releaseButton.hidden = YES;
}

//关闭定时器
- (void)invalidateTimer
{
    if (_timer) {
        [_timer invalidate];
        _timer = nil;
    }
    
    //重置button标题
    self.finishButton.selected = YES;
//    self.releaseButton.hidden = YES;
}

- (void)releaseButtonShow:(BOOL)state buttonSelected:(BOOL)selected
{
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        self.releaseButton.hidden = !state;
        self.releaseButton.selected = selected;
    }else{
        self.releaseButton.hidden = YES;
    }
}

- (void)showAnswerLabel
{
    self.answerLabel.hidden = NO;
}

- (void)removeFromSuperview
{
    [self invalidateTimer];
    [super removeFromSuperview];
}


- (void)setTimeString:(NSString *)timeString
{
    _timeString = timeString;
    
    self.timeLabel.text = timeString;
}

- (void)setHidden:(BOOL)hidden
{
    [super setHidden:hidden];
    
    if (!hidden) {
        
        if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Student) {
            
            NSArray *answerABC = [[TKAnswerSheetData shareInstance] answerABC];
            if (answerABC && answerABC.count) {
                NSString *answerString = [answerABC componentsJoinedByString:@","];
                self.answerLabel.text = [NSString stringWithFormat:@"%@：%@",TKMTLocalized(@"tool.zhengquedaan"),answerString];
            } else {
                self.answerLabel.text = @"";
            }
            
        }else{
            NSInteger state = [self.delegate answerSheetType];
            if (state == 2) {
                //公布答案了
                NSArray *answerABC = [[TKAnswerSheetData shareInstance] answerABC];
                NSString *answerString = [answerABC componentsJoinedByString:@","];
                
                NSArray *myAnswerABC = [[TKAnswerSheetData shareInstance] myAnswerABC];
                if (!myAnswerABC) {
                    self.answerLabel.text = [NSString stringWithFormat:@"%@：%@  %@：",TKMTLocalized(@"tool.zhengquedaan"),answerString, TKMTLocalized(@"tool.wodedaan")];
                }else{
                    NSString *myAnswerString = [myAnswerABC componentsJoinedByString:@","];
                    self.answerLabel.text = [NSString stringWithFormat:@"%@：%@  %@：%@",TKMTLocalized(@"tool.zhengquedaan"),answerString, TKMTLocalized(@"tool.wodedaan"),myAnswerString];
                }

            }else{
                NSArray *myAnswerABC = [[TKAnswerSheetData shareInstance] myAnswerABC];
                if (!myAnswerABC) {
                    self.answerLabel.text = TKMTLocalized(@"tool.wodedaan");
                }else{
                    NSString *myAnswerString = [myAnswerABC componentsJoinedByString:@","];
                    self.answerLabel.text = [NSString stringWithFormat:@"%@：%@",TKMTLocalized(@"tool.wodedaan"),myAnswerString];
                }
            }
        }
    }
}

- (void)setOptionsCount:(NSInteger)optionsCount
{
    _optionsCount = optionsCount;
    [self.collectionView reloadData];
}

- (void)setDict:(NSDictionary *)dict
{
    _dict = dict;
    
    //count:多少人提交答案 5
    //answer:正确答案  数组类型 @[@2，@3]
    //options：每个答案 有多少人选择 数组类型 @[0,0,2,4]
    //time：耗时 30
    //timeStamp:答题卡开始时的时间戳
    //quesID
    
    NSString *num       = [[dict valueForKey:@"count"] stringValue];
    self.numLabel.text  = [NSString stringWithFormat:@"%@：%@%@",TKMTLocalized(@"tool.datirenshu"),num,TKMTLocalized(@"tool.ren")];

    [self.collectionView reloadData];
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

#pragma mark - Action
- (void)timerAction:(NSTimer *)timer
{
    NSDictionary *dict2 = @{@"associatedMsgID":@"Question",
                            @"type":@"getCount",
                            };
    
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];
    NSString *toID         = [TKEduSessionHandle shareInstance].localUser.peerID;
    //toID
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"GetQuestionCount" ID:msgID To:toID Data:@{} Save:NO extensionData:dict2 completion:nil];
}

- (void)detailButtonAction
{
    if ([self.delegate respondsToSelector:@selector(didPressRecordButton:)]) {
        [self.delegate didPressRecordButton:self.detailButton];
    }
}

- (void)finishButtonAction
{
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];

    
    if (self.finishButton.selected) {
        
        //发送结束信令
        [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:@"Question" ID:msgID To:sTellAll Data:@{} completion:nil];
        [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:@"PublishResult" ID:@"PublishResult" To:sTellAll Data:@{} completion:nil];
        
        //重新开始
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"Question"
                                                             ID:msgID
                                                             To:sTellAll
                                                           Data:@"{\"action\":\"open\"}"
                                                           Save:YES
                                                  extensionData:@{@"write2DB":@YES}
                                                     completion:nil];
 
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"AnswerDrag"
                                                             ID:@"AnswerDrag"
                                                             To:sTellAllExpectSender
                                                           Data:@"{\"percentLeft\":0.5,\"percentTop\":0,\"isDrag\":true}"
                                                           Save:YES
                                                  extensionData:@{@"write2DB":@YES}
                                                     completion:nil];
    }else{
        
        //结束答题
        if ([self.delegate respondsToSelector:@selector(didPublishAnswer)]) {
            [self.delegate didPublishAnswer];
        }
        [self invalidateTimer];
        
        NSArray *answer123  = [TKAnswerSheetData shareInstance].answer123;
        long long startTime = [TKAnswerSheetData shareInstance].startTime;
        NSInteger num       = [TKAnswerSheetData shareInstance].count;
        NSArray *options    = [TKAnswerSheetData shareInstance].options;

        //结束答题
        NSString *json1 = [[TKAnswerSheetData shareInstance] endWithAnswer:answer123 options:options time:startTime num:num];
        NSDictionary *dict1 = @{@"write2DB" : @YES};
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"Question" ID:msgID To:sTellAll Data:json1 Save:YES extensionData:dict1 completion:nil];
        //公布答案
        NSString *json2 = [[TKAnswerSheetData shareInstance] publishWithAnswer:answer123 options:options time:startTime num:num publish:NO];
        NSDictionary *dict2 = @{@"associatedMsgID" : @"ClassBegin"};
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"PublishResult" ID:@"PublishResult" To:sTellAll Data:json2 Save:YES extensionData:dict2 completion:nil];
    }
}

//公布答案
- (void)releaseButtonAction
{
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role == TKUserType_Patrol) {
        return;
    }
    
    if (self.releaseButton.selected) {
        return;
    }
    
    NSArray *answer123  = [TKAnswerSheetData shareInstance].answer123;
    long long startTime = [TKAnswerSheetData shareInstance].startTime;
    NSInteger num       = [TKAnswerSheetData shareInstance].count;
    NSArray *options    = [TKAnswerSheetData shareInstance].options;
    
    //公布答案
    NSString *json2 = [[TKAnswerSheetData shareInstance] publishWithAnswer:answer123 options:options time:startTime num:num publish:YES];
    NSDictionary *dict2 = @{@"associatedMsgID" : @"ClassBegin"};
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"PublishResult" ID:@"PublishResult" To:sTellAll Data:json2 Save:YES extensionData:dict2 completion:nil];
}

#pragma mark - UICollectionView

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.optionsCount;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    TKAnswerSheetDetailCell * cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    
    //转化为ABC
    int temp = (int)indexPath.row;
    NSString *string = [NSString stringWithFormat:@"%c",temp + 65];
    cell.serialLabel.text = string;
    
    
    if (!self.dict) {
        cell.numLabel.text    = @"0人";
        [cell.progressView setProgress:0 animated:YES];
        return cell;
    }
    
    NSArray *result    = [self.dict objectForKey:@"options"];
    NSNumber *count    = [self.dict objectForKey:@"count"];
    CGFloat percent    = [result[indexPath.row] floatValue] / count.floatValue;
    cell.numLabel.text = [NSString stringWithFormat:@"%d%@",[result[indexPath.row] intValue],TKMTLocalized(@"tool.ren")];

    [cell.progressView setProgress:count.intValue == 0 ? 0:percent animated:YES];

    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.optionsCount > 4) {
        return CGSizeMake(CGRectGetWidth(collectionView.frame)/2, Fit(45));

    }else{
        return CGSizeMake(CGRectGetWidth(collectionView.frame), Fit(45));
    }
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
}

#pragma mark - Getter

- (TKAnswerSheetLayout *)flowLayout
{
    if (!_flowLayout) {
        _flowLayout = [[TKAnswerSheetLayout alloc] init];
        _flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
    }
    return _flowLayout;
}

- (UICollectionView *)collectionView
{
    if (!_collectionView) {
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:self.flowLayout];
        _collectionView.pagingEnabled = NO;
        _collectionView.scrollEnabled = NO;
        _collectionView.dataSource =self;
        _collectionView.delegate   =self;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.backgroundColor = [UIColor clearColor];
    }
    return _collectionView;
}

- (UIButton *)detailButton
{
    if (!_detailButton) {
        _detailButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_detailButton setTitle:TKMTLocalized(@"tool.detail") forState:UIControlStateNormal];
        _detailButton.titleLabel.font = [UIFont systemFontOfSize:Fit(12)];
        [_detailButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
       _detailButton.sakura.backgroundColor(@"TKToolsBox.answer_button_1");
        _detailButton.layer.cornerRadius = Fit(11);
        _detailButton.clipsToBounds = YES;
        
        [_detailButton addTarget:self action:@selector(detailButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _detailButton;
}

- (UIButton *)finishButton
{
    if (!_finishButton) {
        _finishButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_finishButton setTitle:TKMTLocalized(@"tool.end") forState:UIControlStateNormal];
        [_finishButton setTitle:TKMTLocalized(@"tool.restart") forState:UIControlStateSelected];
        _finishButton.titleLabel.font = [UIFont systemFontOfSize:Fit(16)];
        [_finishButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _finishButton.sakura.backgroundColor(@"TKToolsBox.publish_button");
        _finishButton.layer.cornerRadius = Fit(23);
        _finishButton.clipsToBounds = YES;
        [_finishButton addTarget:self action:@selector(finishButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _finishButton;
}

- (UIButton *)releaseButton
{
    if (!_releaseButton) {
        _releaseButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_releaseButton setTitle:TKMTLocalized(@"tool.publish") forState:UIControlStateNormal];
        [_releaseButton setTitle:TKMTLocalized(@"tool.didpublish") forState:UIControlStateSelected];
        _releaseButton.titleLabel.font = [UIFont systemFontOfSize:Fit(14)];
        [_releaseButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _releaseButton.sakura.backgroundColor(@"TKToolsBox.answer_button");
        _releaseButton.layer.cornerRadius = Fit(13);
        _releaseButton.clipsToBounds = YES;
        _releaseButton.hidden = YES;
        [_releaseButton addTarget:self action:@selector(releaseButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _releaseButton;
}

- (UILabel *)numLabel
{
    if (!_numLabel) {
        _numLabel = [[UILabel alloc] init];
        _numLabel.font = [UIFont systemFontOfSize:12];
        _numLabel.sakura.textColor(@"TKToolsBox.answer_text_1");
        _numLabel.text = [NSString stringWithFormat:@"%@：0%@",TKMTLocalized(@"tool.datirenshu"),TKMTLocalized(@"tool.ren")];
    }
    return _numLabel;
}

- (UILabel *)timeLabel
{
    if (!_timeLabel) {
        _timeLabel = [[UILabel alloc] init];
        _timeLabel.font = [UIFont systemFontOfSize:12];
        _timeLabel.sakura.textColor(@"TKToolsBox.answer_text_1");
        _timeLabel.text = [NSString stringWithFormat:@"%@：00:00:00",TKMTLocalized(@"tool.time")];
    }
    return _timeLabel;
}

- (UILabel *)answerLabel
{
    if (!_answerLabel) {
        _answerLabel = [[UILabel alloc] init];
        _answerLabel.font = [UIFont systemFontOfSize:Fit(14)];
        _answerLabel.sakura.textColor(@"TKToolsBox.answer_text_1");
//        _answerLabel.text = TKMTLocalized(@"tool.zhengquedaan");
    }
    return _answerLabel;
}

- (NSTimer *)timer
{
    if (!_timer) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.5 target:self selector:@selector(timerAction:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
    return _timer;
}

- (void)dealloc
{
}


@end

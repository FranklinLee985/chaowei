//
//  TKAnswerSheetRecordView.m
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetRecordView.h"
#import "TKAnswerSheetRecordCell.h"
#import "TKAnswerSheetData.h"
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKAnswerSheetRecordView()<UICollectionViewDelegate,UICollectionViewDataSource>
{
    NSInteger _currentPage;
}
@property (strong , nonatomic) UICollectionView    *collectionView;
@property (strong , nonatomic) UICollectionViewFlowLayout *flowLayout;

@property (strong , nonatomic) UILabel *timeLabel;
@property (strong , nonatomic) UILabel *numLabel;
@property (strong , nonatomic) UILabel *answerLabel;

@property (strong , nonatomic) UIButton *recordButton;
@property (strong , nonatomic) UIButton *finishButton;
@property (strong , nonatomic) UIButton *releaseButton;

//翻页
@property (strong , nonatomic) UIButton *pageUpButton;
@property (strong , nonatomic) UIButton *pageDownButton;
@property (strong , nonatomic) UILabel  *pageLabel;

@property (strong , nonatomic) NSTimer        *timer;

@property (strong , nonatomic) NSArray *dataArray;
@property (strong , nonatomic) NSDictionary *pageInfo;


@end

@implementation TKAnswerSheetRecordView

static NSString * const reuseID = @"TKAnswerSheetRecordCellID";

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        
        [self addSubview:self.collectionView];
        [self.collectionView registerClass:[TKAnswerSheetRecordCell class] forCellWithReuseIdentifier:reuseID];
        
        [self addSubview:self.timeLabel];
        [self addSubview:self.numLabel];
        [self addSubview:self.answerLabel];
        
        [self addSubview:self.recordButton];
        [self addSubview:self.finishButton];
        [self addSubview:self.releaseButton];
        
        [self addSubview:self.pageLabel];
        [self addSubview:self.pageUpButton];
        [self addSubview:self.pageDownButton];

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
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.right.equalTo(self.mas_right);
            make.top.equalTo(self.numLabel.mas_bottom).offset(Fit(20));
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(105));
        }];
        
        [self.recordButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-20);
            make.top.equalTo(self.mas_top).offset(Fit(20));
            make.height.equalTo(@Fit(22));
            make.width.equalTo(@Fit(56));
        }];
        
        [self.releaseButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.answerLabel.mas_right).offset(15);
            make.centerY.equalTo(self.answerLabel.mas_centerY);
            make.height.equalTo(@Fit(26));
            make.width.equalTo(@Fit(85));

        }];
        
        [self.finishButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-20);
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(16));
            make.height.equalTo(@Fit(46));
            make.width.equalTo(@Fit(106));
        }];
        
        [self.pageLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.top.equalTo(self.collectionView.mas_bottom).offset(Fit(20));
        }];
        
        [self.pageUpButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.pageLabel.mas_left).offset(-20);
            make.centerY.equalTo(self.pageLabel.mas_centerY);
        }];

        [self.pageDownButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.pageLabel.mas_right).offset(20);
            make.centerY.equalTo(self.pageLabel.mas_centerY);
        }];
    }
    return self;
}

- (void)commonInit
{
    _currentPage = 1;
    
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role == TKUserType_Teacher){
        self.finishButton.hidden = NO;
    }else{
        self.finishButton.hidden = YES;
    }
}

- (void)invalidateTimer
{
    if (_timer) {
        [_timer invalidate];
        _timer = nil;
    }
}

- (void)setTimeString:(NSString *)timeString
{
    _timeString = timeString;
    
    self.timeLabel.text = timeString;
}


//重写方法
- (void)setHidden:(BOOL)hidden
{
    [super setHidden:hidden];
    
    if (!hidden) {
        
        [self timer];
        
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
                    self.answerLabel.text =  TKMTLocalized(@"tool.wodedaan");
                    
                }else{
                    NSString *myAnswerString = [myAnswerABC componentsJoinedByString:@","];
                    self.answerLabel.text = [NSString stringWithFormat:@"%@：%@", TKMTLocalized(@"tool.wodedaan"),myAnswerString];
                }
            }
        }

    }else{
        [self invalidateTimer];
    }
}

- (void)releaseButtonShow:(BOOL)state buttonSelected:(BOOL)selected;
{
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        self.releaseButton.hidden = !state;
        self.releaseButton.selected = selected;
    }else{
        self.releaseButton.hidden = YES;
    }
}

- (void)fixFinishButtonState:(BOOL)select {
    self.finishButton.selected = select;
}

- (void)removeFromSuperview
{
    [super removeFromSuperview];
    [self invalidateTimer];
}

#pragma mark - Action

- (void)timerAction:(NSTimer *)timer
{
    //https://demo.talk-cloud.net:443/ClientAPI/simplifyAnswer?ts=1547190507280
    NSString *URLString = [NSString stringWithFormat:@"https://%@:%@/ClientAPI/simplifyAnswer",sHost,sPort];
    NSString *quesID       = [TKAnswerSheetData shareInstance].quesID;
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    if (!quesID || !roomDict || roomDict.count == 0) {
        return;
    }
    NSDictionary *parameters = @{
                                 @"serial":roomDict[@"serial"],
                                 @"id":quesID,
                                 @"page":@"1",
                                 };
    
    [[TKAnswerSheetData shareInstance] POST:URLString parameters:parameters success:^(id  _Nonnull responseObject)
     {
         NSInteger status = [[responseObject objectForKey:@"result"] integerValue];
         if (status == 0) {
             self.dataArray = [responseObject objectForKey:@"data"];
             self.pageInfo  = [responseObject objectForKey:@"pageinfo"];
             
             [self.collectionView reloadData];
         }
     } failure:^(NSError * _Nonnull error) {
         
     }];
}

- (void)refreshByAnswerSheetViewWithDic:(NSDictionary *)dic
{
    
}

- (void)recordButtonAction
{
    if ([self.delegate respondsToSelector:@selector(didPressDetailButton:)]) {
        [self.delegate didPressDetailButton:self.recordButton];
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
    
    } else {
        //结束答题
        if ([self.delegate respondsToSelector:@selector(didPublishAnswer)]) {
            [self.delegate didPublishAnswer];
        }
        self.finishButton.selected = YES;
        NSArray *answer123  = [TKAnswerSheetData shareInstance].answer123;
        long long startTime = [TKAnswerSheetData shareInstance].startTime;
        NSInteger num       = [TKAnswerSheetData shareInstance].count;
        NSArray *options    = [TKAnswerSheetData shareInstance].options;

        //结束答题
        NSString *json1 = [[TKAnswerSheetData shareInstance] endWithAnswer:answer123 options:options time:startTime num:num];
        NSDictionary *dict1 = @{@"write2DB" : @YES};
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"Question" ID:msgID To:sTellAll Data:json1 Save:YES extensionData:dict1 completion:nil];

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

- (void)pageUpButtonAction
{
    if (_currentPage <= 1) {
        return;
    }
    _currentPage--;
    _pageUpButton.enabled = (_currentPage != 1);
}

- (void)pageDownButtonAction
{
    if (_currentPage >= [[self.pageInfo objectForKey:@"page"] integerValue]) {
        return;
    }
    _currentPage++;
    _pageUpButton.enabled = (_currentPage < [[self.pageInfo objectForKey:@"page"] integerValue]);
}

#pragma mark - UICollectionView

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    if (self.dataArray.count == 0) {
        return 1;
    }
    return self.dataArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    TKAnswerSheetRecordCell * cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    
    if (self.dataArray.count == 0) {
        cell.nickname.text  = TKMTLocalized(@"tool.nodata");
        self.numLabel.text  = [NSString stringWithFormat:@"%@：0%@",TKMTLocalized(@"tool.datirenshu"),TKMTLocalized(@"tool.ren")];
        self.pageLabel.text = @"1/1";

    }else{
        NSDictionary *dict = self.dataArray[indexPath.item];
        
        NSArray *answer       = [dict objectForKey:@"options"];
        NSMutableArray *codes = [NSMutableArray arrayWithCapacity:10];
        for (int i = 0; i < answer.count; i++) {
            int temp = [answer[i] intValue];
            NSString *string = [NSString stringWithFormat:@"%c",temp + 65];
            [codes addObject:string];
        }
        NSString *answerString = [codes componentsJoinedByString:@","];
        
        cell.nickname.text    = [dict objectForKey:@"studentname"];
        cell.answerLabel.text = [NSString stringWithFormat:@"%@：%@",TKMTLocalized(@"tool.choose"),answerString];
        cell.timeLabel.text   = [NSString stringWithFormat:@"%@：%@",TKMTLocalized(@"tool.time"),[dict objectForKey:@"timestr"]];
        NSInteger count = [[self.pageInfo objectForKey:@"count"] integerValue] * [[self.pageInfo objectForKey:@"page"] integerValue];
        
        self.numLabel.text  = [NSString stringWithFormat:@"%@：%zd%@",TKMTLocalized(@"tool.datirenshu"),count,TKMTLocalized(@"tool.ren")];
        self.pageLabel.text = [NSString stringWithFormat:@"%zd/%zd",_currentPage,[[self.pageInfo objectForKey:@"page"] integerValue]];

    }
    
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(CGRectGetWidth(collectionView.frame), Fit(45));
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
}

#pragma mark - Getter

- (UICollectionViewFlowLayout *)flowLayout
{
    if (!_flowLayout) {
        _flowLayout = [[UICollectionViewFlowLayout alloc] init];
        _flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        _flowLayout.minimumLineSpacing      = 0;
        _flowLayout.minimumInteritemSpacing = 0;
    }
    return _flowLayout;
}

- (UICollectionView *)collectionView
{
    if (!_collectionView) {
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:self.flowLayout];
        _collectionView.pagingEnabled = NO;
        _collectionView.scrollEnabled = YES;
        _collectionView.dataSource =self;
        _collectionView.delegate   =self;
        _collectionView.showsVerticalScrollIndicator = YES;
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.backgroundColor = [UIColor clearColor];
    }
    return _collectionView;
}

- (UIButton *)recordButton
{
    if (!_recordButton) {
        _recordButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_recordButton setTitle:TKMTLocalized(@"tool.tongji") forState:UIControlStateNormal];
        _recordButton.titleLabel.font = [UIFont systemFontOfSize:Fit(12)];
        [_recordButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _recordButton.sakura.backgroundColor(@"TKToolsBox.answer_button_1");
        _recordButton.layer.cornerRadius = Fit(11);
        _recordButton.clipsToBounds = YES;
        
        [_recordButton addTarget:self action:@selector(recordButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _recordButton;
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
//        _timeLabel.text = @"用时：00:00:00";
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

- (UILabel *)pageLabel
{
    if (!_pageLabel) {
        _pageLabel = [[UILabel alloc] init];
        _pageLabel.textAlignment = NSTextAlignmentCenter;
        _pageLabel.font = [UIFont systemFontOfSize:Fit(13)];
        _pageLabel.sakura.textColor(@"TKToolsBox.answer_text_1");
        _pageLabel.text = @"1/1";
    }
    return _pageLabel;
}

- (UIButton *)pageUpButton
{
    if (!_pageUpButton) {
        _pageUpButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _pageUpButton.sakura.image(@"TKToolsBox.answer_page_up",UIControlStateNormal);
    _pageUpButton.sakura.image(@"TKToolsBox.answer_page_up_1",UIControlStateDisabled);
        _pageUpButton.enabled = (_currentPage != 1);
        [_pageUpButton addTarget:self action:@selector(pageUpButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _pageUpButton;
}

- (UIButton *)pageDownButton
{
    if (!_pageDownButton) {
        _pageDownButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _pageDownButton.sakura.image(@"TKToolsBox.answer_page_down",UIControlStateNormal);
        _pageDownButton.sakura.image(@"TKToolsBox.answer_page_down_1",UIControlStateDisabled);
        _pageDownButton.enabled = (_currentPage < [[self.pageInfo objectForKey:@"page"] integerValue]);
        [_pageDownButton addTarget:self action:@selector(pageDownButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _pageDownButton;
}

- (NSTimer *)timer
{
    if (!_timer) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:2 target:self selector:@selector(timerAction:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
    return _timer;
}

- (void)dealloc
{
}

@end

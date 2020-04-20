//
//  TKAnswerSheetSubmitView.m
//  EduClass
//
//  Created by maqihan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//


#import "TKAnswerSheetSubmitView.h"
#import "TKAnswerSheetSetupCell.h"
#import "TKAnswerSheetData.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKAnswerSheetSubmitView()<UICollectionViewDelegate,UICollectionViewDataSource>
{
    BOOL _modify;
}
@property (strong , nonatomic) UICollectionView           *collectionView;
@property (strong , nonatomic) UICollectionViewFlowLayout *flowLayout;

@property (strong , nonatomic) UIButton *submitButton;
//自己选择的结果
@property (strong , nonatomic) NSMutableArray *resultArray;
//A~H数据
@property (strong , nonatomic) NSMutableArray *dataArray;

@end

@implementation TKAnswerSheetSubmitView

static NSString * const reuseID = @"TKAnswerSheetSetupCellID";

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        
        [self addSubview:self.collectionView];
        [self.collectionView registerClass:[TKAnswerSheetSetupCell class] forCellWithReuseIdentifier:reuseID];
        
        [self addSubview:self.submitButton];
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.right.equalTo(self.mas_right);
            make.top.equalTo(self.mas_top).offset(Fit(20));
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(124));
        }];
        
        [self.submitButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(23));
            make.height.equalTo(@Fit(44));
            make.width.equalTo(@Fit(175));
        }];
        
    }
    return self;
}

- (void)commonInit
{

}

- (void)setOptionsCount:(NSInteger)optionsCount
{
    _optionsCount = optionsCount;
    //检查数据 并修正
    //返回的数组内元素类型为nsnumber
    
    if (optionsCount == 0) {
        return;
    }
    
    //通知视图更改高度
    if (optionsCount > 4) {
        if ([self.delegate respondsToSelector:@selector(didChangeHeight:)]) {
            [self.delegate didChangeHeight:361];
        }

    }else{
        if ([self.delegate respondsToSelector:@selector(didChangeHeight:)]) {
            [self.delegate didChangeHeight:361-90];
        }
    }
    
    [self.dataArray removeAllObjects];
    [self.answerArray removeAllObjects];

    NSArray *array = @[@"A",@"B",@"C",@"D",@"E",@"F",@"G",@"H"];
    NSArray *subArray = [array subarrayWithRange:NSMakeRange(0, optionsCount)];
    [self.dataArray addObjectsFromArray:subArray];
    
    [self.collectionView reloadData];
}
#pragma mark - Action

- (void)submitButtonAction
{
    if (self.resultArray.count == 0) {
        return;
    }
    
    if (self.submitButton.selected) {
        //修改答案 清除之前的所选答案
        [self.resultArray removeAllObjects];
        [self.collectionView reloadData];
        self.submitButton.selected = NO;
        return;
    }
    
    //提交答案
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    //roomid 是 serial
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];
    
    NSDictionary *answerDict = [self answerDicForArray:self.resultArray];

    NSString *json   = [[TKAnswerSheetData shareInstance] submitWithAnswer:self.answerArray selected:self.resultArray options:self.dataArray modify:_modify];
    NSInteger status = _modify ? 1 : 0;
    
    NSDictionary *dict    = [NSDictionary dictionaryWithObjectsAndKeys:
                              @"Question", @"associatedMsgID",
                              @(YES),      @"write2DB",
                              @"count",    @"type",
                              @(status),   @"modify",
                              answerDict,  @"actions",nil];
    
//    标准版用的是__none 从逻辑上讲 感觉应该用__all，暂时未发现问题
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"AnswerCommit" ID:msgID To:sTellNone Data:json Save:NO extensionData:dict completion:^(NSError * _Nonnull error) {
        if (!error) {
            _modify = YES;
            self.submitButton.selected = YES;
        }
    }];
}

- (NSDictionary *)answerDicForArray:(NSArray *)selected
{
    //将选择的答案转化为@"A",@"B",@"C"... -->@"1",@"2",@"3"...
    NSMutableArray *array123 = [NSMutableArray arrayWithCapacity:10];
    for (NSString *item in selected) {
        int code = [item characterAtIndex:0] - 65;
        NSString *key = [NSString stringWithFormat:@"%d",code];
        [array123 addObject:key];
    }
    
    //修改答案
    NSMutableDictionary *answerDict = [NSMutableDictionary dictionaryWithCapacity:10];
    for (NSString *item in array123) {
        if (![[TKAnswerSheetData shareInstance].myAnswer containsObject:item]) {
            [answerDict setValue:@1 forKey:item];
        }
    }
    
    for (NSString *item in [TKAnswerSheetData shareInstance].myAnswer) {
        if (![array123 containsObject:item]) {
            [answerDict setValue:@(-1) forKey:item];
        }
    }
    
    return answerDict;
}

#pragma mark - UICollectionView

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.dataArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    TKAnswerSheetSetupCell * cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    
    NSString *answer = self.dataArray[indexPath.item];
    if ([self.resultArray containsObject:answer]) {
        cell.itemImage.sakura.image([NSString stringWithFormat:@"TKToolsBox.answer_%@_1",answer]);
    }else{
        cell.itemImage.sakura.image([NSString stringWithFormat:@"TKToolsBox.answer_%@_0",answer]);
    }
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(floor(Fit(73)), floor(Fit(73)));
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewFlowLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    CGFloat space = (CGRectGetWidth(collectionView.frame)- Fit(73) * 4)/5;
    return UIEdgeInsetsMake(0, floor(space), 0, floor(space));
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return floor(Fit(15));
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewFlowLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section
{
    CGFloat space = (CGRectGetWidth(collectionView.frame) - Fit(73) * 4)/5;
    return floor(space);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role != TKUserType_Student) {
        return;
    }
    
    if (self.submitButton.selected) {
        return;
    }
    
    NSString *answer = self.dataArray[indexPath.item];
    
    if ([self.resultArray containsObject:answer]) {
        [self.resultArray removeObject:answer];
    }else{
        [self.resultArray addObject:answer];
    }
    [self.collectionView reloadData];
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
        _collectionView.scrollEnabled = NO;
        _collectionView.dataSource =self;
        _collectionView.delegate   =self;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.backgroundColor = [UIColor clearColor];
    }
    return _collectionView;
}

- (UIButton *)submitButton
{
    if (!_submitButton) {
        _submitButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_submitButton setTitle:TKMTLocalized(@"tool.submit") forState:UIControlStateNormal];
        [_submitButton setTitle:TKMTLocalized(@"tool.modify") forState:UIControlStateSelected];
        _submitButton.titleLabel.font = [UIFont systemFontOfSize:Fit(16)];
        [_submitButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _submitButton.sakura.backgroundColor(@"TKToolsBox.answer_button");
        _submitButton.layer.cornerRadius = Fit(22);
        _submitButton.clipsToBounds = YES;
        _submitButton.selected = NO;
        
        [_submitButton addTarget:self action:@selector(submitButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _submitButton;
}

- (NSMutableArray *)resultArray
{
    if (!_resultArray) {
        _resultArray = [NSMutableArray arrayWithCapacity:10];
    }
    return _resultArray;
}

- (NSMutableArray *)dataArray
{
    if (!_dataArray) {
        _dataArray = [NSMutableArray arrayWithCapacity:10];
    }
    return _dataArray;
}

- (NSMutableArray *)answerArray
{
    if (!_answerArray) {
        _answerArray = [NSMutableArray arrayWithCapacity:10];
    }
    return _answerArray;
}

- (void)dealloc
{
    
}

@end

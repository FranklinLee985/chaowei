//
//  TKAnswerSheetSetupView.m
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetSetupView.h"
#import "TKAnswerSheetSetupCell.h"

#import "TKAnswerSheetData.h"
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKAnswerSheetSetupView()<UICollectionViewDelegate,UICollectionViewDataSource>

@property (strong , nonatomic) UICollectionView           *collectionView;
@property (strong , nonatomic) UICollectionViewFlowLayout *flowLayout;

@property (strong , nonatomic) UIButton *addButton;
@property (strong , nonatomic) UIButton *minusButton;
@property (strong , nonatomic) UIButton *finishButton;

@property (strong , nonatomic) UILabel *label;

@property (strong , nonatomic) NSMutableArray *dataArray;
@property (strong , nonatomic) NSMutableArray *resultArray;

@end

@implementation TKAnswerSheetSetupView
static NSString * const reuseID = @"TKAnswerSheetSetupCellID";

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        
        [self addSubview:self.collectionView];
        [self.collectionView registerClass:[TKAnswerSheetSetupCell class] forCellWithReuseIdentifier:reuseID];

        [self addSubview:self.addButton];
        [self addSubview:self.minusButton];
        [self addSubview:self.finishButton];

        [self addSubview:self.label];
        
        BOOL isPatrol = [TKRoomManager instance].localUser.role == TKUserType_Patrol;
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.right.equalTo(self.mas_right);
            make.top.equalTo(self.mas_top).offset(Fit(20));
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(isPatrol ? 25 : 125));
        }];
        
        [self.addButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(40);
            make.top.equalTo(self.collectionView.mas_bottom).offset(25);
            make.height.equalTo(@Fit(26));
            make.width.equalTo(@Fit(86));
        }];
        
        [self.minusButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.addButton.mas_right).offset(30);
            make.top.equalTo(self.collectionView.mas_bottom).offset(25);
            make.height.equalTo(@Fit(26));
            make.width.equalTo(@Fit(86));

        }];
        
        [self.label mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.addButton.mas_left);
            make.bottom.equalTo(self.mas_bottom).offset(-Fit(30));
        }];
        
        [self.finishButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-26);
            make.bottom.equalTo(self.mas_bottom).offset(-16);
            make.height.equalTo(@Fit(46));
            make.width.equalTo(@Fit(106));
        }];
        
        self.addButton.hidden =
        self.minusButton.hidden =
        self.finishButton.hidden =
        [TKRoomManager instance].localUser.role == TKUserType_Patrol;
    }
    return self;
}

- (void)commonInit
{
    [self.dataArray removeAllObjects];
    [self.dataArray addObjectsFromArray:@[@"A",@"B",@"C",@"D"]];
}

- (void)reset
{
    [self.dataArray removeAllObjects];
    [self.dataArray addObjectsFromArray:@[@"A",@"B",@"C",@"D"]];
    [self.resultArray removeAllObjects];
    
    self.addButton.enabled = YES;
    self.addButton.alpha   = 1;
    
    self.minusButton.enabled = YES;
    self.minusButton.alpha   = 1;
    
    [self.collectionView reloadData];
}


#pragma mark - Action

- (void)addButtonAction
{
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role != TKUserType_Teacher) {
        return;
    }
    _minusButton.sakura.backgroundColor(@"TKToolsBox.answer_button");
    if (self.dataArray.count > 6) {
        self.addButton.enabled = NO;
        _addButton.sakura.backgroundColor(@"TKToolsBox.answer_button_disable");
        self.addButton.alpha   = 0.5;
    }
    self.minusButton.enabled = YES;
    self.minusButton.alpha   = 1;

    if (self.dataArray.count == 4) {
        //更新高度
        if ([self.delegate respondsToSelector:@selector(didChangeHeight:)]) {
            [self.delegate didChangeHeight:361];
        }
    }
    
    NSString *string0 = [self.dataArray lastObject];
    int code = [string0 characterAtIndex:0];
    
    NSString *string1 = [NSString stringWithFormat:@"%c",++code];
    [self.dataArray addObject:string1];
    [self.collectionView reloadData];
}

- (void)minusButtonAction
{
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role != TKUserType_Teacher) {
        return;
    }
    _addButton.sakura.backgroundColor(@"TKToolsBox.answer_button");
    if (self.dataArray.count < 4) {
        self.minusButton.enabled = NO;
        _minusButton.sakura.backgroundColor(@"TKToolsBox.answer_button_disable");
        self.minusButton.alpha   = 0.5;
    }
    self.addButton.enabled = YES;
    self.addButton.alpha = 1;

    if (self.dataArray.count == 5) {
        //更新高度
        if ([self.delegate respondsToSelector:@selector(didChangeHeight:)]) {
            [self.delegate didChangeHeight:361-90];
        }
    }
    
    NSString *string = [self.dataArray lastObject];
    if ([self.resultArray containsObject:string]) {
        [self.resultArray removeObject:string];
        
        //记录正确答案
        [TKAnswerSheetData shareInstance].answerABC = self.resultArray;

        NSString *stringABC = [self.resultArray componentsJoinedByString:@","];
        NSString *answerString = [NSString stringWithFormat:@"%@:%@",TKMTLocalized(@"tool.zhengquedaan"),stringABC];
        self.label.text  = answerString;
    }
    [self.dataArray removeLastObject];
    [self.collectionView reloadData];
}

- (void)finishButtonAction
{
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role != TKUserType_Teacher) {
        return;
    }
    
    //请至少设置1个正确答案
    if (!self.resultArray.count) {
        [TKUtil showMessage:TKMTLocalized(@"tool.leastanswer")];
        return;
    }
    
    //拍序数组
    NSArray *sortedArray = [self.resultArray sortedArrayUsingComparator:^NSComparisonResult(NSString *obj1, NSString *obj2) {
        return  [obj1 compare:obj2];
    }];
    
    if ([self.delegate respondsToSelector:@selector(didPressReleaseButton:answer:option:)]) {
        [self.delegate didPressReleaseButton:self.finishButton answer:sortedArray option:self.dataArray];
    }
    
    //发布答题
    NSString *json = [[TKAnswerSheetData shareInstance] releaseWithAnswer:sortedArray options:self.dataArray];
    NSDictionary *dict2 = @{@"associatedMsgID":@"Question",
                            @"write2DB":@YES,};
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    //roomid 是 serial
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];
    
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"Question" ID:msgID To:sTellAll Data:json Save:YES extensionData:dict2 completion:nil];
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

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    CGFloat space = (CGRectGetWidth(collectionView.frame)- Fit(73) * 4)/5;
    return UIEdgeInsetsMake(0, floor(space), 0, floor(space));
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return floor(Fit(15));
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section
{
    
    CGFloat space = (CGRectGetWidth(collectionView.frame) - Fit(73) * 4)/5;
    return floor(space);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    TKUserRoleType role = [TKEduSessionHandle shareInstance].localUser.role;
    if (role != TKUserType_Teacher) {
        return;
    }
    
    NSString *answer = self.dataArray[indexPath.item];
    
    if ([self.resultArray containsObject:answer]) {
        [self.resultArray removeObject:answer];
    }else{
        [self.resultArray addObject:answer];
    }

    //拍序数组
    NSArray *sortedArray = [self.resultArray sortedArrayUsingComparator:^NSComparisonResult(NSString *obj1, NSString *obj2) {
        return  [obj1 compare:obj2];
    }];
    
    [self.resultArray removeAllObjects];
    [self.resultArray addObjectsFromArray:sortedArray];
    //记录正确答案
    [TKAnswerSheetData shareInstance].answerABC = sortedArray;

    //组合字符串
    NSString *string = [sortedArray componentsJoinedByString:@","];
    NSString *answerString = [NSString stringWithFormat:@"%@:%@",TKMTLocalized(@"tool.zhengquedaan"),string];

    self.label.text  = answerString;
    
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

- (UIButton *)addButton
{
    if (!_addButton) {
        _addButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_addButton setTitle:TKMTLocalized(@"tool.tianjia") forState:UIControlStateNormal];
        _addButton.titleLabel.font = [UIFont systemFontOfSize:Fit(14)];
        [_addButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _addButton.sakura.backgroundColor(@"TKToolsBox.answer_button");
        _addButton.layer.cornerRadius = Fit(13);
        _addButton.clipsToBounds = YES;
        
        [_addButton addTarget:self action:@selector(addButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _addButton;
}

- (UIButton *)minusButton
{
    if (!_minusButton) {
        _minusButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_minusButton setTitle:TKMTLocalized(@"tool.shanchu") forState:UIControlStateNormal];
        _minusButton.sakura.backgroundColor(@"TKToolsBox.answer_button");
        _minusButton.titleLabel.font = [UIFont systemFontOfSize:Fit(14)];
        [_minusButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _minusButton.layer.cornerRadius = Fit(13);
        _minusButton.clipsToBounds = YES;
        [_minusButton addTarget:self action:@selector(minusButtonAction) forControlEvents:UIControlEventTouchUpInside];
        
    }
    return _minusButton;
}

- (UIButton *)finishButton
{
    if (!_finishButton) {
        _finishButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_finishButton setTitle:TKMTLocalized(@"tool.fabudati") forState:UIControlStateNormal];
        _finishButton.titleLabel.font = [UIFont systemFontOfSize:Fit(16)];
        [_finishButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _finishButton.sakura.backgroundColor(@"TKToolsBox.publish_button");
        _finishButton.layer.cornerRadius = Fit(23);
        _finishButton.clipsToBounds = YES;
        [_finishButton addTarget:self action:@selector(finishButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _finishButton;
}

- (UILabel *)label
{
    if (!_label) {
        _label = [[UILabel alloc] init];
        _label.font = [UIFont systemFontOfSize:Fit(14)];
        _label.sakura.textColor(@"TKToolsBox.answer_text_1");
//        _label.text = TKMTLocalized(@"tool.zhengquedaan");
    }
    return _label;
}

- (NSMutableArray *)dataArray
{
    if (!_dataArray) {
        _dataArray = [NSMutableArray arrayWithCapacity:10];
    }
    return _dataArray;
}

- (NSMutableArray *)resultArray
{
    if (!_resultArray) {
        _resultArray = [NSMutableArray arrayWithCapacity:10];
    }
    return _resultArray;
}


- (void)dealloc
{
    
}
@end

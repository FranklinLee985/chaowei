//
//  TKManyStylePopView.m
//  EduClass
//
//  Created by maqihan on 2019/4/15.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKManyStylePopView.h"
#import "TKEduSessionHandle.h"
#import "TKStylePopViewCell.h"

#define ThemeKP(args) [@"layoutView." stringByAppendingString:args]
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKManyStylePopView()<UICollectionViewDelegate,UICollectionViewDataSource>

@property (strong , nonatomic) UICollectionView           *collectionView;
@property (strong , nonatomic) UICollectionViewFlowLayout *flowLayout;
@property (strong , nonatomic) NSArray *dataArray;

@property (strong , nonatomic) UIView *containerView;
@property (strong , nonatomic) UIView *targetView;
@property (strong , nonatomic) UIView *contentView;


@end

@implementation TKManyStylePopView

static NSString * const reuseID = @"TKStylePopViewCellID";

+ (TKManyStylePopView *)showPopViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView
{
    NSAssert(containerView, @"containerView must not be nil.");
    NSAssert(targetView, @"targetView must not be nil.");

    static TKManyStylePopView *_stylePopView = nil;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _stylePopView = [[TKManyStylePopView alloc] init];
    });
    
    _stylePopView.backgroundColor = [UIColor clearColor];
    _stylePopView.frame = containerView.bounds;
    _stylePopView.containerView = containerView;
    _stylePopView.targetView    = targetView;
    [containerView addSubview:_stylePopView];
    [_stylePopView showUsingAnimation];

    return _stylePopView;
}

+ (BOOL)dismissForView:(UIView *)view;
{
    TKManyStylePopView *popView = [self popViewForView:view];
    if (popView) {
        [popView hideUsingAnimation];
        return YES;
    }
    return NO;
}

+ (TKManyStylePopView *)popViewForView:(UIView *)view {
    NSEnumerator *subviewsEnum = [view.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:self]) {
            TKManyStylePopView *popView = (TKManyStylePopView *)subview;
            return popView;
        }
    }
    return nil;
}

- (void)updateConstraints
{
    if (!self.targetView || !self.contentView) {
        [super updateConstraints];
        return;
    }
    
    CGFloat width = 352;
    
    BOOL isSurpass = self.targetView.centerX + Fit(width) / 2 > ScreenW;
    
    [self.contentView mas_updateConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(@(CGSizeMake(Fit(width), Fit(150))));
        make.top.equalTo(self.targetView.mas_bottom).offset(25);
        isSurpass ? make.right.equalTo(self.mas_right).offset(-10) : make.centerX.equalTo(self.targetView.mas_centerX);
    }];
    
    [self.collectionView mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView.mas_top);
        make.left.equalTo(self.contentView.mas_left);
        make.right.equalTo(self.contentView.mas_right);
        make.bottom.equalTo(self.contentView.mas_bottom);
    }];
    
    [super updateConstraints];
}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        
        [self addSubview:self.contentView];
        [self.contentView addSubview:self.collectionView];
        
        [self.collectionView registerClass:[TKStylePopViewCell class] forCellWithReuseIdentifier:reuseID];
    }
    return self;
}

- (void)commonInit
{
    self.dataArray =  @[@{@"icon_0":@"manyLayoutView.tk_top_default",
                          @"icon_1":@"manyLayoutView.tk_top_selected",
                          @"title":TKMTLocalized(@"Button.ManyLayout.Top")},
                        
                        @{@"icon_0":@"manyLayoutView.tk_speaker_default",
                          @"icon_1":@"manyLayoutView.tk_speaker_selected",
                          @"title":TKMTLocalized(@"Button.ManyLayout.Speaker")},
                        
                        @{@"icon_0":@"manyLayoutView.tk_free_default",
                          @"icon_1":@"manyLayoutView.tk_free_selected",
                          @"title":TKMTLocalized(@"Button.ManyLayout.Free")}];
    
    [self.collectionView reloadData];
}

- (void)setViewStyle:(TKRoomLayout)viewStyle
{
    _viewStyle = viewStyle;
    
    [self.collectionView reloadData];
}

- (void)showUsingAnimation
{
    if (![NSThread isMainThread]) {
        return;
    }
    
    [self animateIn:YES completion:^(BOOL finished) {
    }];
}

- (void)hideUsingAnimation
{
    if (![NSThread isMainThread]) {
        return;
    }
    if ([self.targetView isKindOfClass:UIButton.class]) {
        
        UIButton *btn = (UIButton *)self.targetView;
        btn.selected  = NO;
    }
    [self animateIn:NO completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

- (void)animateIn:(BOOL)animatingIn completion:(void(^)(BOOL finished))completion
{
    //初始化状态
    if (animatingIn) {
        self.contentView.transform = CGAffineTransformMakeScale(0.5f, 0.5f);
        self.contentView.alpha     = 0.5;
        self.alpha                    = 0.0;
        
    } else{
        
        self.contentView.transform = CGAffineTransformIdentity;
        self.contentView.alpha     = 1;
        self.alpha                    = 1;
    }
    
    dispatch_block_t animations = ^{
        if (animatingIn) {
            self.contentView.transform = CGAffineTransformIdentity;
            self.contentView.alpha     = 1;
            self.alpha                    = 1;
            [self setNeedsDisplay];
            
        } else{
            self.contentView.transform = CGAffineTransformMakeScale(0.5f, 0.5f);
            self.contentView.alpha     = 0.5;
            self.alpha                    = 0.0;
            [self setNeedsDisplay];
        }
    };
    
    [UIView animateWithDuration:0.3 delay:0. usingSpringWithDamping:1.f initialSpringVelocity:0.f options:UIViewAnimationOptionBeginFromCurrentState animations:animations completion:completion];
}

#pragma mark - Action

- (void)finishButtonAction
{
    
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
    TKStylePopViewCell * cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    NSDictionary *dict   = self.dataArray[indexPath.item];
    cell.itemTitle.text  = dict[@"title"];
    cell.itemTitle.textColor = UIColor.whiteColor;
    cell.itemImage.sakura.image(dict[@"icon_0"]);
    
    if (self.viewStyle == CoursewareDown && indexPath.item == 0) {
        cell.itemImage.sakura.image(dict[@"icon_1"]);
        cell.itemTitle.sakura.textColor(@"TKStylePopView.title_selected_color");
    }else if (self.viewStyle == MainPeople && indexPath.item == 1){
        cell.itemImage.sakura.image(dict[@"icon_1"]);
        cell.itemTitle.sakura.textColor(@"TKStylePopView.title_selected_color");
    }else if (self.viewStyle == OnlyVideo && indexPath.item == 2){
        cell.itemImage.sakura.image(dict[@"icon_1"]);
        cell.itemTitle.sakura.textColor(@"TKStylePopView.title_selected_color");
    }
    
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(Fit(62), CGRectGetHeight(collectionView.frame));
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(0, Fit(27), 0, Fit(27));
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return (CGRectGetWidth(collectionView.frame)-Fit(27)*2 - Fit(62) * self.dataArray.count) / (self.dataArray.count - 1);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [[self class] dismissForView:self.containerView];
    
    NSString *style = @"";
    if (indexPath.item == 0) {
        style = @"CoursewareDown";
    }else if (indexPath.item == 1){
        style = @"MainPeople";
    }else{
        style = @"OnlyVideo";
    }
    
    NSString *tellID = sTellAll;
    if ([TKEduSessionHandle shareInstance].isClassBegin) {
        //上课，布局同步
        tellID = sTellAll;
    }else{
        //没上课，布局不同步
        NSString *uerID = [TKEduSessionHandle shareInstance].localUser.peerID;
        tellID = uerID;
    }
    
    //发送信令
    [[self class] publishStyleSignalingWithStyle:style tellID:tellID];
}

//发送样式同步信令
+ (void)publishStyleSignalingWithStyle:(NSString *)style tellID:(NSString *)tellID
{
    //TKManyLayout_Speaker布局时 默认是老师
    if ([style isEqualToString:@"MainPeople"]) {
        
        NSArray *users = [[TKEduSessionHandle shareInstance] userStdntAndTchrArray];
        NSString *teacherID = nil;
        
        for (TKRoomUser *user in users) {
            if (user.role == TKUserType_Teacher) {
                teacherID = user.peerID;
            }
        }
        if (teacherID) {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"MainPeopleExchangeVideo"
                                                                 ID:@"MainPeopleExchangeVideo"
                                                                 To:tellID
                                                               Data:@{@"doubleId":teacherID}
                                                               Save:YES
                                                         completion:nil];
        }
    }
    
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sSwitchLayout
                                                         ID:sSwitchLayout
                                                         To:tellID
                                                       Data:@{@"nowLayout":style}
                                                       Save:YES
                                                 completion:nil];
    
    
    
    //切换布局需要复位所有视频的信令
    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sVideoSplitScreen ID:sVideoSplitScreen To:sTellAll Data:@{} completion:nil];
    
    NSMutableDictionary *dict    = [NSMutableDictionary dictionary];
    NSMutableDictionary *addDict = [NSMutableDictionary dictionary];
    
    NSArray *users = [[TKEduSessionHandle shareInstance] userStdntAndTchrArray];
    
    for (TKRoomUser *user in users) {
        
        NSDictionary *info = @{@"percentTop":@(0),@"percentLeft":@(0),@"isDrag":@(NO)};
        [addDict setValue:info forKey:user.peerID];
    }
    [dict setValue:addDict forKey:@"otherVideoStyle"];
    [[TKEduSessionHandle shareInstance] publishVideoDragWithDic:dict To:sTellAll];
}


#pragma mark - Drawing

- (void)drawRect:(CGRect)rect
{
    CGRect targetframe  = self.targetView.frame;
    CGRect contentframe = self.contentView.frame;
    UIColor *color = self.contentView.backgroundColor;
    [color set]; //设置线条颜色

    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:CGPointMake(CGRectGetMidX(targetframe), CGRectGetMinY(contentframe) - 10)];
    [path addLineToPoint:CGPointMake(CGRectGetMidX(targetframe) - 8, CGRectGetMinY(contentframe))];
    [path addLineToPoint:CGPointMake(CGRectGetMidX(targetframe) + 8, CGRectGetMinY(contentframe))];
    [path addLineToPoint:CGPointMake(CGRectGetMidX(targetframe), CGRectGetMinY(contentframe) - 10)];
    [path closePath];
    [path fill];

    path.lineWidth = 1;
    path.lineCapStyle = kCGLineCapRound; //线条拐角
    path.lineJoinStyle = kCGLineJoinRound; //终点处理

    [path stroke];
}

#pragma mark - touches

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    CGPoint point = [[touches anyObject] locationInView:self];
    point = [self.contentView.layer convertPoint:point fromLayer:self.layer];
    if ([self.contentView.layer containsPoint:point]) {
        return;
    }
    [[self class] dismissForView:self.containerView];
}

#pragma mark - Getter

- (UICollectionViewFlowLayout *)flowLayout
{
    if (!_flowLayout) {
        _flowLayout = [[UICollectionViewFlowLayout alloc] init];
        _flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
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
        _collectionView.dataSource =self;
        _collectionView.delegate   =self;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.backgroundColor = [UIColor clearColor];
    }
    return _collectionView;
}

- (UIView *)contentView
{
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.layer.cornerRadius = Fit(20);
        _contentView.clipsToBounds = YES;
        _contentView.sakura.backgroundColor(@"TKToolsBox.popViewBackColor");
    }
    return _contentView;
}


@end

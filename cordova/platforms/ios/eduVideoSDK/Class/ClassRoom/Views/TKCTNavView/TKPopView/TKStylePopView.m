//
//  TKStylePopView.m
//  EduClass
//
//  Created by maqihan on 2019/4/1.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKStylePopView.h"
#import "TKEduSessionHandle.h"
#import "TKStylePopViewCell.h"

#define ThemeKP(args) [@"layoutView." stringByAppendingString:args]
#define Fit(height) (IS_PAD ? (height) : (height) *0.6)

@interface TKStylePopView()<UICollectionViewDelegate,UICollectionViewDataSource>

@property (strong , nonatomic) UICollectionView           *collectionView;
@property (strong , nonatomic) UICollectionViewFlowLayout *flowLayout;
@property (strong , nonatomic) NSArray *dataArray;

@property (strong , nonatomic) UIView *containerView;
@property (strong , nonatomic) UIView *targetView;
@property (strong , nonatomic) UIView *contentView;

@end

@implementation TKStylePopView

static NSString * const reuseID = @"TKStylePopViewCellID";
static TKStylePopView *singleton = nil;

+ (TKStylePopView *)showPopViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView
{
    NSAssert(containerView, @"containerView must not be nil.");
    NSAssert(targetView, @"targetView must not be nil.");
    @synchronized (self) {
        if (!singleton) {
            
            singleton = [[self alloc] init];
        }
    }
    singleton.frame = containerView.bounds;
    singleton.backgroundColor =  [UIColor clearColor];
    singleton.containerView = containerView;
    singleton.targetView    = targetView;
    
    [containerView addSubview:singleton];
    [singleton showUsingAnimation];
    
    return singleton;
}

+ (BOOL)dismissForView:(UIView *)view;
{
    TKStylePopView *popView = [self popViewForView:view];
    if (popView) {
        [popView hideUsingAnimation];
        return YES;
    }
    return NO;
}

+ (TKStylePopView *)popViewForView:(UIView *)view {
    NSEnumerator *subviewsEnum = [view.subviews reverseObjectEnumerator];
    for (UIView *subview in subviewsEnum) {
        if ([subview isKindOfClass:self]) {
            TKStylePopView *popView = (TKStylePopView *)subview;
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
    self.dataArray =  @[@{@"icon_0":@"layoutView.tk_changgui_default",
                          @"icon_1":@"layoutView.tk_changgui_selected",
                          @"title":TKMTLocalized(@"Button.DefaultLayout")},
                        
                        @{@"icon_0":@"layoutView.tk_shuangshi_default",
                          @"icon_1":@"layoutView.tk_shuangshi_selected",
                          @"title":TKMTLocalized(@"Button.DoubleLayout")},
                        
                        @{@"icon_0":@"layoutView.tk_shipin_default",
                          @"icon_1":@"layoutView.tk_shipin_selected",
                          @"title":TKMTLocalized(@"Button.VideoLayout")}];

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
    
//    if (indexPath.item == self.viewStyle - 51) {
//        cell.itemImage.sakura.image(dict[@"icon_1"]);
//        cell.itemTitle.sakura.textColor(@"TKStylePopView.title_selected_color");
//    }
    if (self.viewStyle == oneToOne && indexPath.item == 0) {
        cell.itemImage.sakura.image(dict[@"icon_1"]);
        cell.itemTitle.sakura.textColor(@"TKStylePopView.title_selected_color");
    }else if (self.viewStyle == oneToOneDoubleDivision && indexPath.item == 1){
        cell.itemImage.sakura.image(dict[@"icon_1"]);
        cell.itemTitle.sakura.textColor(@"TKStylePopView.title_selected_color");
    }else if (self.viewStyle == oneToOneDoubleVideo && indexPath.item == 2){
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
    
    if (self.viewStyle == indexPath.item) {
        return;
    }

    NSString *style = @"";
    
    if (indexPath.item == 0) {
        style = @"oneToOne";
        self.viewStyle = oneToOne;
        
    }else if (indexPath.item == 1){
        style = @"oneToOneDoubleDivision";
        self.viewStyle = oneToOneDoubleDivision;
        
    }else if (indexPath.item == 2){
        style = @"oneToOneDoubleVideo";
        self.viewStyle = oneToOneDoubleVideo;
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
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sSwitchLayout
                                                         ID:sSwitchLayout
                                                         To:tellID
                                                       Data:@{@"nowLayout":style}
                                                       Save:YES
                                                 completion:nil];
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

- (void)dealloc
{
    
}


@end

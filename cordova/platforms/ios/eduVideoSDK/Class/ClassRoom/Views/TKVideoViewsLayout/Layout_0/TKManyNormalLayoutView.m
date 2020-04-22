//
//  TKManyNormalLayoutView.m
//  EduClass
//
//  Created by maqihan on 2019/4/3.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKManyNormalLayoutView.h"
#import "TKVideoCollectionViewCell.h"
#import "TKEduSessionHandle.h"
#import "TKManyNormalLayout.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)
#define space 3

@interface TKManyNormalLayoutView ()<UICollectionViewDelegate,UICollectionViewDataSource>
{
    //视频默认宽高
    CGSize _videoViewNormalSize;
}
//视频
@property (strong , nonatomic) UICollectionView   *collectionView;
@property (strong , nonatomic) TKManyNormalLayout *flowLayout;

//数据源
@property (strong , nonatomic , nullable) NSMutableArray <TKCTVideoSmallView *>*dataArray;

@end

@implementation TKManyNormalLayoutView
static NSString * const reuseID = @"TKVideoCollectionViewCellID";

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self commonInit];
        
        [self addSubview:self.collectionView];
        
        [self.collectionView registerClass:[TKVideoCollectionViewCell class] forCellWithReuseIdentifier:reuseID];
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.top.equalTo(self.mas_top);
            make.bottom.equalTo(self.mas_bottom);
            make.right.equalTo(self.mas_right);
        }];
    }
    return self;
}

- (void)commonInit
{
    self.backgroundColor = [[TKTheme colorWithPath:@"layoutView.tk_videoView_bg"] colorWithAlphaComponent:[TKTheme floatWithPath:@"layoutView.tk_videoView_bg_alpha"]];
    
    //计算宽高
    CGFloat width  = floor((ScreenW - 8 * space) / 7.0);
    CGFloat height = width * 3.0/4;//必须保证事3:4 不能取整
    _videoViewNormalSize = CGSizeMake(width, height);
}

//重写父类方法
- (void)setVideoArray:(NSArray<TKCTVideoSmallView *> *)videoArray
{
    [super setVideoArray:videoArray];
    
    //刷新view
    [self refreshAllView];
}

//刷新all View 位置
- (void)refreshAllView
{
    //清理数据
    [self.dataArray removeAllObjects];
    
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        //数据
        if (videoView.isSplit || videoView.isDrag || videoView.isPicInPic) {
            [self removeObjectWithUserID:videoView.iPeerId];
        }else{
            [self.dataArray addObject:videoView];
        }
        
        //layout
        if (videoView.isSplit) {
            videoView.videoMode = TKVideoViewMode_Fit;
        }else{
            videoView.videoMode = TKVideoViewMode_Fill;
        }
    }
    self.flowLayout.haveTeacher = [self containsTeacher];
    
    [self.flowLayout invalidateLayout];
    [self.collectionView reloadData];
}


//缩放
- (void)scaleVideoViewWithZoomRatio:(CGFloat)ratio userID:(NSString *)userID superview:(UIView *)superview
{
    TKCTVideoSmallView *videoView = [self videoViewWithUserID:userID];

    if (ratio < 1) {
        ratio = 1;
    }
    
    if (videoView) {
        CGRect frame = [self scaleFrameWithVideoView:videoView scale:ratio superview:superview];
        videoView.frame = frame;
    }
}

//拖拽回调
- (void)dragVideoView:(TKCTVideoSmallView *)videoView left:(CGFloat)left top:(CGFloat)top superview:(UIView *)superview
{
    left = left > 1 ? 1 :left;
    left = left < 0 ? 0 :left;
    top  = top > 1 ? 1 :top;
    top  = top < 0 ? 0 :top;

    //每次拖动事件发生，都会返回所有被拖拽的对象，这样无法判断当前正在拖拽哪个对象，所以无法更改层级结构
    CGRect frame = [self dragFrameWithVideoView:videoView left:left top:top superview:superview];
    videoView.frame = frame;
    [superview addSubview:videoView];
    
    [self refreshAllView];
}

//分屏回调
- (void)splitScreenWithInfo:(NSDictionary *)dict superview:(UIView *)superview
{
    [self refreshAllView];
}

- (TKCTVideoSmallView *)videoViewWithUserID:(NSString *)userID
{
    
    if (!userID.length) {
        return nil;
    }
    
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if ([videoView.iPeerId isEqualToString:userID]) {
            return videoView;
        }
    }
    return nil;
}

- (BOOL)containsObjectWithUserID:(NSString *)userID
{
    for (TKCTVideoSmallView *videoView in self.dataArray) {
        if ([videoView.iPeerId isEqualToString:userID]) {
            return YES;
        }
    }
    return NO;
}

- (BOOL)containsTeacher
{
    for (TKCTVideoSmallView *videoView in self.dataArray) {
        if (videoView.iRoomUser.role == TKUserType_Teacher) {
            return YES;
        }
    }
    return NO;
}

//根据人数以及是否有老师 调整学生布局 0 1 2
- (NSInteger)layoutVideoView
{
    if (self.dataArray.count <= 7) {
        return 0;
    }
    
    BOOL haveTeacher = [self containsTeacher];
    
    if (haveTeacher) {
        
        if (self.dataArray.count >= 8 && self.dataArray.count <= 12) {
            return 1;
        }else{
            //有老师 13 ～24
            return 2;
        }
        
    }else{
        if (self.dataArray.count >= 8 && self.dataArray.count <= 13) {
            
            return 1;
        }else{
            //没老师 14 ～24
            return 2;
        }
    }
}

- (void)removeObjectWithUserID:(NSString *)userID
{
    [self.dataArray enumerateObjectsUsingBlock:^(TKCTVideoSmallView * _Nonnull videoView, NSUInteger idx, BOOL * _Nonnull stop) {
        
        if ([videoView.iPeerId isEqualToString:userID]) {
            [self.dataArray removeObject:videoView];
        }
    }];
}

- (CGRect)dragFrameWithVideoView:(TKCTVideoSmallView *)videoView left:(CGFloat)left top:(CGFloat)top superview:(UIView *)superview
{
    CGRect frame;
    CGFloat d_width  = CGRectGetWidth(superview.frame) - CGRectGetWidth(videoView.frame);
    CGFloat d_height = CGRectGetHeight(superview.frame) - CGRectGetHeight(videoView.frame);
    
    frame = CGRectMake(d_width*left, d_height*top, CGRectGetWidth(videoView.frame), CGRectGetHeight(videoView.frame));

    if (CGRectGetWidth(frame) < _videoViewNormalSize.width) {
        frame.size = _videoViewNormalSize;
    }
    
    if (CGRectGetHeight(frame) < _videoViewNormalSize.height) {
        frame.size = _videoViewNormalSize;
    }
    return frame;
}

- (CGRect)scaleFrameWithVideoView:(TKCTVideoSmallView *)videoView scale:(CGFloat)ratio superview:(UIView *)view
{
    CGRect frame;
    if(ratio >= 1)
    {
        frame = CGRectMake(CGRectGetMinX(videoView.frame), CGRectGetMinY(videoView.frame), _videoViewNormalSize.width * ratio, _videoViewNormalSize.height * ratio);
        
    }else{
        frame = CGRectMake(CGRectGetMinX(videoView.frame), CGRectGetMinY(videoView.frame), _videoViewNormalSize.width, _videoViewNormalSize.height);
    }
    
    //边界检测
    if (CGRectGetWidth(frame) > CGRectGetWidth(view.frame)) {
        frame.size.width  = CGRectGetWidth(view.frame);
        frame.size.height = 3.0/4 * CGRectGetWidth(view.frame);
    }
    if (CGRectGetHeight(frame) > CGRectGetHeight(view.frame)) {
        frame.size.height  = CGRectGetHeight(view.frame);
        frame.size.width = 4.0/3 * CGRectGetHeight(view.frame);
    }
    
    return frame;
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
    TKVideoCollectionViewCell *cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    
    TKCTVideoSmallView *video = self.dataArray[indexPath.item];

    if (indexPath.item == 0 && [self containsTeacher]) {
        //老师
        video.videoMode  = TKVideoViewMode_Fill;
        video.maskLayout = TKMaskViewLayout_Normal;

    }else{
        //学生
        NSInteger state = [self layoutVideoView];
        switch (state) {
            case 0:
            {
                video.videoMode  = TKVideoViewMode_Fill;
                video.maskLayout = TKMaskViewLayout_Normal;
            }
                break;
            case 1:
            {
                video.videoMode = TKVideoViewMode_Top;
                video.maskLayout = TKMaskViewLayout_Normal;
            }
                break;

            default:
            {
                video.videoMode = TKVideoViewMode_Fill;
                video.maskLayout = TKMaskViewLayout_More;
            }
                break;
        }
    }
    
    [cell addVideoView:video];
    cell.contentView.sakura.backgroundColor(@"ClassRoom.TKVideoView.View_bg_color");
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    //老师
    if (indexPath.item == 0 && [self containsTeacher]) {
        return _videoViewNormalSize;
    }
    
    //学生
    NSInteger state = [self layoutVideoView];
    CGSize size = CGSizeZero;

    if (state == 0) {
        return _videoViewNormalSize;

    }else if(state == 1){

        if ([self containsTeacher]) {
            CGFloat width = (CGRectGetWidth(collectionView.frame) - (self.dataArray.count-1) * space - _videoViewNormalSize.width) / (self.dataArray.count-1);
            size = CGSizeMake(floor(width) , CGRectGetHeight(collectionView.frame));

        }else{
            CGFloat width = (CGRectGetWidth(collectionView.frame) - (self.dataArray.count-1) * space) / (self.dataArray.count);
            size = CGSizeMake(floor(width) , CGRectGetHeight(collectionView.frame));
        }

    }else{
        if ([self containsTeacher]) {
            CGFloat width  = (CGRectGetWidth(collectionView.frame) - space * (14 - 1) - _videoViewNormalSize.width) / 12;
            CGFloat height = (CGRectGetHeight(collectionView.frame) - space) / 2;
            size = CGSizeMake(floor(width), floor(height));
            // floor 取整导致 宽度减少
        }else{
            CGFloat width  = (CGRectGetWidth(collectionView.frame) - space * (16 - 1)) / 14;
            CGFloat height = (CGRectGetHeight(collectionView.frame) - space) / 2;
            
            size = CGSizeMake(floor(width), floor(height));
        }
    }

    
    return size;
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    if (self.dataArray.count <= 7) {
        CGFloat allViewWidth = self.dataArray.count * _videoViewNormalSize.width + (self.dataArray.count - 1) * space;
        CGFloat off_x = (ScreenW - allViewWidth) / 2;
        return UIEdgeInsetsMake(0, off_x, 0, off_x);
    }
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return space;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section
{
    return space;
}

#pragma mark - Getter

- (TKManyNormalLayout *)flowLayout
{
    if (!_flowLayout) {
        _flowLayout = [[TKManyNormalLayout alloc] init];
        _flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
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
        _collectionView.clipsToBounds = NO;
        _collectionView.backgroundColor = [UIColor clearColor];
    }
    return _collectionView;
}


- (NSMutableArray<TKCTVideoSmallView *> *)dataArray
{
    if (!_dataArray) {
        _dataArray = [NSMutableArray array];
    }
    return _dataArray;
}

@end


//
//  TKManyFreeLayoutView.m
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKManyFreeLayoutView.h"
#import "TKCTVideoSmallView.h"
#import "TKManyFreeCollectionViewCell.h"
#import "TKManyFreeLayout.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)
#define space 3

@interface TKManyFreeLayoutView ()<UICollectionViewDelegate,UICollectionViewDataSource>

//学生视频
@property (strong , nonatomic) UICollectionView *collectionView;
@property (strong , nonatomic) TKManyFreeLayout *flowLayout;

//学生数据
@property (strong , nonatomic , nullable) NSMutableArray <TKCTVideoSmallView *>*students;

@end
@implementation TKManyFreeLayoutView

static NSString * const reuseID = @"TKSpeakerCollectionViewCellID";


- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self commonInit];
        
        [self addSubview:self.collectionView];
        
        [self.collectionView registerClass:[TKManyFreeCollectionViewCell class] forCellWithReuseIdentifier:reuseID];
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    return self;
}

- (void)commonInit
{
    self.backgroundColor = [[TKTheme colorWithPath:@"layoutView.tk_videoView_bg"] colorWithAlphaComponent:[TKTheme floatWithPath:@"layoutView.tk_videoView_bg_alpha"]];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
}

//重写父类方法
- (void)setVideoArray:(NSArray<TKCTVideoSmallView *> *)videoArray
{
    [super setVideoArray:videoArray];
    
    //清理数据
    [self.students removeAllObjects];
    
    //此处不能用videoArray 要使用父类 self.videoArray 排序过
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        [self.students addObject:videoView];
    }
    //刷新view
    [self.collectionView reloadData];
}

//
- (CGSize)sizeForCell
{
    CGSize size = CGSizeZero;
    CGFloat width  = CGRectGetWidth(self.collectionView.frame);
    CGFloat height = CGRectGetHeight(self.collectionView.frame);
    
    if (self.students.count == 1) {
        size = CGSizeMake(4.0/3 * height, height);

    }else if(self.students.count == 2){
        size = CGSizeMake(width/2, 3.0/4*width/2);
        
    }else if(self.students.count == 3){
        
    }else if(self.students.count == 4){
        size = CGSizeMake(4.0/3 * height/2, height/2);
        
    }else if(self.students.count == 5){
        
    }else if(self.students.count == 6){
        if (IS_PAD) {
            size = CGSizeMake(width/3, 3.0/4*width/3);
        }else{
            size = CGSizeMake(4.0/3*height/2, height/2);
        }

    }else if(self.students.count >= 7 && self.students.count <= 9){
        size = CGSizeMake(4.0/3 * height/3, height/3);
        
    }else if(self.students.count >= 10 && self.students.count <= 16){
        size = CGSizeMake(4.0/3 * height/4, height/4);
        
    }else if(self.students.count >= 17 && self.students.count <= 20){
        if (IS_PAD) {
            size = CGSizeMake(width/5, 3.0/4*width/5);
        }else{
            size = CGSizeMake(4.0/3*height/4, height/4);
        }

    }else if(self.students.count >= 21){
        if (IS_PAD) {
            size = CGSizeMake(width/6, 3.0/4*width/6);
        }else{
           size = CGSizeMake(4.0/3*height/4, height/4);
        }
    }

    return size;
}

- (UIEdgeInsets)edgeInsetsForCell
{
    CGSize size = [self sizeForCell];;
    CGFloat width  = CGRectGetWidth(self.collectionView.frame);
    CGFloat height = CGRectGetHeight(self.collectionView.frame);
    
    if(self.students.count == 2){
        return UIEdgeInsetsMake((height - size.height)/2, 0, (height - size.height)/2, 0);

    }else if(self.students.count == 4){
        return UIEdgeInsetsMake(0, (width - 2*size.width)/2, 0, (width - 2*size.width)/2);

    }else if(self.students.count == 6){
        if (IS_PAD) {
            return UIEdgeInsetsMake((height - 2 * size.height)/2, 0, (height - 2 * size.height)/2, 0);
        }else{
            return UIEdgeInsetsMake(0, (width - 3*size.width)/2, 0, (width - 3*size.width)/2);
        }
    }else if(self.students.count >= 7 && self.students.count <= 9){
        return UIEdgeInsetsMake(0, (width - 3*size.width)/2, 0, (width - 3*size.width)/2);

    }else if(self.students.count >= 10 && self.students.count <= 12){
        return UIEdgeInsetsMake(0, (width - 3 * size.width)/2, 0, (width - 3 * size.width)/2);

    }else if(self.students.count >= 13 && self.students.count <= 16){
        return UIEdgeInsetsMake(0, (width - 4 * size.width)/2, 0, (width - 4 * size.width)/2);

    }else if(self.students.count >= 17 && self.students.count <= 20){

        if (IS_PAD) {
            return UIEdgeInsetsMake((height - 4*size.height)/2, 0, (height - 4*size.height)/2, 0);
        }else{
            return UIEdgeInsetsMake(0, (width - 5 * size.width)/2, 0, (width - 5 * size.width)/2);
        }

    }else if(self.students.count >= 21){
        if (IS_PAD) {
            return UIEdgeInsetsMake((height - 4*size.height)/2, 0, (height - 4*size.height)/2, 0);
        }else{
            return UIEdgeInsetsMake(0, (width - 5 * size.width)/2, 0, (width - 5 * size.width)/2);
        }
    }
    
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

#pragma mark - UICollectionView

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.students.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    TKManyFreeCollectionViewCell *cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    TKCTVideoSmallView *video = self.students[indexPath.item];
    [cell addVideoView:video];
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self sizeForCell];
}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return [self edgeInsetsForCell];
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return 0;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section
{
    return 0;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{

}

#pragma mark - Getter

- (TKManyFreeLayout *)flowLayout
{
    if (!_flowLayout) {
        _flowLayout = [[TKManyFreeLayout alloc] init];
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
        _collectionView.backgroundColor = [UIColor clearColor];
    }
    return _collectionView;
}

- (NSMutableArray<TKCTVideoSmallView *> *)students
{
    if (!_students) {
        _students = [NSMutableArray array];
    }
    return _students;
}

@end

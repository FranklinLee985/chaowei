//
//  TKManySpeakerLayoutView.m
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKManySpeakerLayoutView.h"
#import "TKSpeakerView.h"
#import "TKCTVideoSmallView.h"
#import "TKSpeakerCollectionViewCell.h"
#import "TKEduSessionHandle.h"

#define Fit(height) (IS_PAD ? (height) : (height) *0.6)
#define space 3

@interface TKManySpeakerLayoutView ()<UICollectionViewDelegate,UICollectionViewDataSource>

//主讲视频
@property (strong , nonatomic) TKSpeakerView *speakerContentView;
// 是否主讲是老师
@property (assign , nonatomic)BOOL isTeacher;
@property (strong , nonatomic) UICollectionView           *collectionView;
@property (strong , nonatomic) UICollectionViewFlowLayout *flowLayout;



//学生数据
@property (strong , nonatomic , nullable) NSMutableArray <TKCTVideoSmallView *>*students;
@property (assign , nonatomic)CGSize itemSize;
@end

@implementation TKManySpeakerLayoutView

static NSString * const reuseID = @"TKSpeakerCollectionViewCellID";


- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self addSubview:self.speakerContentView];
        [self addSubview:self.collectionView];
        
        [self.collectionView registerClass:[TKSpeakerCollectionViewCell class] forCellWithReuseIdentifier:reuseID];
        
        [self.speakerContentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.centerY.equalTo(self.mas_centerY);
            make.width.equalTo(self.mas_width).multipliedBy(2.0/3);
            make.height.equalTo(self.speakerContentView.mas_width).multipliedBy(3.0/4);
        }];
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.speakerContentView.mas_right).offset(space);
            make.top.equalTo(self.mas_top).offset(space);
            make.bottom.equalTo(self.mas_bottom).offset(-space);
            make.right.equalTo(self.mas_right).offset(-space);
        }];
        
        [self commonInit];
       
    }
    return self;
}

- (void)commonInit
{
    self.peerID = @"";
    self.backgroundColor = [[TKTheme colorWithPath:@"layoutView.tk_videoView_bg"] colorWithAlphaComponent:[TKTheme floatWithPath:@"layoutView.tk_videoView_bg_alpha"]];

}

- (void)calculateItemSize
{
    
    CGFloat width  = CGRectGetWidth(self.collectionView.frame);
    CGFloat height = CGRectGetHeight(self.collectionView.frame);
    
    //1列的情况 能放下多少个视频
    CGFloat cellHeight_1 = width * 3.0/4;
    NSInteger count_1    = 1 * floor(height / cellHeight_1);
    
    //2列的情况 能放下多少个视频
    CGFloat cellHeight_2 = width/2 * 3.0/4;
    NSInteger count_2    = 2 * floor(height / cellHeight_2);
    
    //3列的情况 能放下多少个视频
    CGFloat cellHeight_3 = width/3 * 3.0/4;
    NSInteger count_3    = 3 * floor(height / cellHeight_3);
    
    if (self.students.count <= count_1) {
        
        self.itemSize = CGSizeMake(width, cellHeight_1);
    }
    else if(self.students.count > count_1 && self.students.count <= count_2){
        
        self.itemSize = CGSizeMake(floor((width - space)/2), 3.0/4 * (width - space)/2);
    }
    else if(self.students.count > count_2 && self.students.count <= count_3){
        
        self.itemSize = CGSizeMake(floor((width - 2*space)/3), 3.0/4 * (width - 2*space)/3);
    }
    else{
        
        self.itemSize = CGSizeMake(floor((width - 3*space)/4), 3.0/4 * (width - 3*space)/4);
    }
    
}
//重写父类方法
- (void)setVideoArray:(NSArray<TKCTVideoSmallView *> *)videoArray
{
    [super setVideoArray:videoArray];
    
    //清理数据
    TKCTVideoSmallView *teacherVideo = nil;
    [self.students removeAllObjects];
    
    // 找出主讲者
    for (TKCTVideoSmallView *videoView in videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        
        if (videoView.isSpeaker) {
            self.speaker = videoView;
        }
        
        if (videoView.iRoomUser.role == TKUserType_Teacher) {
            teacherVideo = videoView;
        }
    }

    if (self.speaker) {
        self.peerID = self.speaker.iPeerId;
    }
    
    [self.speakerContentView addVideoView:self.speaker];

    //分离数据 学生
    for (TKCTVideoSmallView *videoView in self.videoArray) {
        if (!videoView.iRoomUser) {
            continue;
        }
        if (!videoView.isSpeaker && videoView.iRoomUser) {
            [self.students addObject:videoView];
        }
    }
    
    //如果学生和老师切换位置了， 老师始终在数组第一位置
    if (self.speaker.iRoomUser.role != TKUserType_Teacher) {
        
        [self.students enumerateObjectsUsingBlock:^(TKCTVideoSmallView * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            
            if (obj.iRoomUser && obj.iRoomUser.role == TKUserType_Teacher && idx != 0) {
                [self.students exchangeObjectAtIndex:idx withObjectAtIndex:0];
            }
        }];
    }
    
    //教室内只有老师一个人或者主讲没有视频 需要将老师居中
    if (self.students.count == 0) {
        
        [self.speakerContentView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self.mas_centerX);
            make.centerY.equalTo(self.mas_centerY);
            make.width.equalTo(self.mas_height).multipliedBy(4.0/3);
            make.height.equalTo(self.mas_height);
        }];
        
    }
    else{
        [self.speakerContentView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.centerY.equalTo(self.mas_centerY);
            make.width.equalTo(self.mas_width).multipliedBy(2.0/3);
            make.height.equalTo(self.speakerContentView.mas_width).multipliedBy(3.0/4);
        }];
        
    }
    [self layoutIfNeeded];
    //刷新view
    [self calculateItemSize];
    [self.collectionView reloadData];
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
    TKSpeakerCollectionViewCell *cell  = [collectionView dequeueReusableCellWithReuseIdentifier:reuseID forIndexPath:indexPath];
    
    TKCTVideoSmallView *video = self.students[indexPath.item];
    
    [cell addVideoView:video];
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    return self.itemSize;
}

//-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
//{
//    return UIEdgeInsetsMake(space, space, space, space);
//}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return space;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section
{
    return space;
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

- (TKSpeakerView *)speakerContentView
{
    if (!_speakerContentView) {
        _speakerContentView = [[TKSpeakerView alloc] init];
//        _speakerContentView.backgroundColor = [UIColor clearColor];
        
    }
    return _speakerContentView;
}

- (NSMutableArray<TKCTVideoSmallView *> *)students
{
    if (!_students) {
        _students = [NSMutableArray array];
    }
    return _students;
}


@end

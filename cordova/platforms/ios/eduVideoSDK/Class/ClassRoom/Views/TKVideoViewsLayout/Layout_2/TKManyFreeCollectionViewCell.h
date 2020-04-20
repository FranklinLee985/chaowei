//
//  TKManyFreeCollectionViewCell.h
//  EduClass
//
//  Created by maqihan on 2019/4/14.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class TKCTVideoSmallView;

@interface TKManyFreeCollectionViewCell : UICollectionViewCell
@property (weak , nonatomic , nullable) TKCTVideoSmallView *videoView;

- (void)addVideoView:(TKCTVideoSmallView *)videoView;

@end

NS_ASSUME_NONNULL_END

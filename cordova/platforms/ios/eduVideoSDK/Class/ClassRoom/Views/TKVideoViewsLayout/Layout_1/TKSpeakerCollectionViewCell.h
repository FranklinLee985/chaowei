//
//  TKSpeakerCollectionViewCell.h
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class TKCTVideoSmallView;
@interface TKSpeakerCollectionViewCell : UICollectionViewCell
//用户视频
@property (weak , nonatomic , nullable) TKCTVideoSmallView *videoView;

- (void)addVideoView:(TKCTVideoSmallView *)videoView;

@end

NS_ASSUME_NONNULL_END

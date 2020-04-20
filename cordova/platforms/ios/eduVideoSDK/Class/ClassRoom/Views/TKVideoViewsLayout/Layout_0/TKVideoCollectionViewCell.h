//
//  TKVideoCollectionViewCell.h
//  EduClass
//
//  Created by maqihan on 2019/4/3.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKCTVideoSmallView.h"


NS_ASSUME_NONNULL_BEGIN

@interface TKVideoCollectionViewCell : UICollectionViewCell

@property (weak , nonatomic) TKCTVideoSmallView *videoView;

- (void)addVideoView:(TKCTVideoSmallView *)videoView;

@end

NS_ASSUME_NONNULL_END

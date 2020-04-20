//
//  TKVideoCollectionViewCell.m
//  EduClass
//
//  Created by maqihan on 2019/4/3.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKVideoCollectionViewCell.h"
#import "TKEduSessionHandle.h"

@interface TKVideoCollectionViewCell()

@end


@implementation TKVideoCollectionViewCell

- (void)addVideoView:(TKCTVideoSmallView *)videoView
{
    [self.contentView addSubview:videoView];
    self.videoView = videoView;

    [self setNeedsLayout];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    if (self.videoView.superview == self.contentView) {
        self.videoView.frame = self.contentView.bounds;
    }
}


@end

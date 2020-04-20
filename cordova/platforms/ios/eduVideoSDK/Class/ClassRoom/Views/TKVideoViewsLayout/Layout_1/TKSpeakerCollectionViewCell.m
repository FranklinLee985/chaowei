//
//  TKSpeakerCollectionViewCell.m
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKSpeakerCollectionViewCell.h"
#import "TKCTVideoSmallView.h"

@implementation TKSpeakerCollectionViewCell

- (void)addVideoView:(TKCTVideoSmallView *)videoView
{
    if (videoView.superview != self.contentView) {
        [self.contentView addSubview:videoView];
    }
    
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

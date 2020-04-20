//
//  TKSpeakerView.m
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKSpeakerView.h"
#import "TKCTVideoSmallView.h"

@interface TKSpeakerView()

@property(nonatomic, strong)UIImageView *placeholder;

@end

@implementation TKSpeakerView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        self.sakura.backgroundColor(@"ClassRoom.TKVideoView.videoPlaceholderBackColor");
        [self addSubview:self.placeholder];

        [self.placeholder mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(self);
            if (IS_IPHONE) {
                make.width.equalTo(self.mas_width).multipliedBy(0.6);
                make.height.equalTo(self.mas_height).multipliedBy(0.6);
            }
        }];

    }
    return self;
}

- (void)addVideoView:(TKCTVideoSmallView *)videoView
{
    if (videoView.superview != self) {
        [self addSubview:videoView];
        self.videoView = videoView;
    }

    [self setNeedsLayout];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    if (self.videoView.superview == self) {
        self.videoView.frame = self.bounds;
    }
}

- (UIImageView *)placeholder {

    if (_placeholder == nil) {

        _placeholder = [UIImageView new];
        _placeholder.contentMode = UIViewContentModeScaleAspectFit;
        _placeholder.sakura.image(@"ClassRoom.TKVideoView.placeholdVideoImage");

    }

    return _placeholder;
}

@end

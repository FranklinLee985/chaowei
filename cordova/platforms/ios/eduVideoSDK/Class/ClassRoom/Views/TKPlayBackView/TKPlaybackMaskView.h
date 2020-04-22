//
//  TKPlaybackMaskView.h
//  EduClassPad
//
//  Created by MAC-MiNi on 2017/9/11.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TKProgressSlider;

@interface TKPlaybackMaskView : UIView

@property (nonatomic, strong) UIButton *playButton;//播放按钮
@property (nonatomic, strong) TKProgressSlider *iProgressSlider;//播放进度条

//时间更新
- (void)update:(NSTimeInterval)current;

//回放结束
- (void)playbackEnd;

//获取当前播放时间
- (void)getPlayDuration:(NSTimeInterval)duration;

- (void)showTool;
@end

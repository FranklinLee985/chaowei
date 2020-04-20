//
//  UIImageView+DJExtension.m
//  EduClass
//
//  Created by lyy on 2018/5/4.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "UIImageView+TKExtension.h"

@implementation UIImageView (TKExtension)

// 播放GIF
- (void)tkPlayGifAnim:(NSArray *)images
{
    if (!images.count) {
        return;
    }
    //动画图片数组
    self.animationImages = images;
    //执行一次完整动画所需的时长
    self.animationDuration = images.count/10.0;
    //动画重复次数, 设置成0 就是无限循环
    self.animationRepeatCount = 0;
    [self startAnimating];
}
// 停止动画
- (void)tkStopGifAnim
{
    if (self.isAnimating) {
        [self stopAnimating];
    }
//    [self removeFromSuperview];
}


@end

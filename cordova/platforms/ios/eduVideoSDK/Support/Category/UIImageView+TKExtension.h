//
//  UIImageView+DJExtension.h
//  EduClass
//
//  Created by lyy on 2018/5/4.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImageView (TKExtension)

// 播放GIF
- (void)tkPlayGifAnim:(NSArray *)images;
// 停止动画
- (void)tkStopGifAnim;

@end

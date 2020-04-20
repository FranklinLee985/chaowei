//
//  TKProgressView.h
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKProgressView : UIView
//背景色
@property (strong , nonatomic) UIColor *trackTintColor;
//进度条颜色
@property (strong , nonatomic) UIColor *progressTintColor;

- (void)setProgress:(CGFloat)progress animated:(BOOL)animated;


@end

NS_ASSUME_NONNULL_END

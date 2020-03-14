//
//  TKNativeWBRemarkView.h
//  TKWhiteBoard
//
//  Created by maqihan on 2019/2/12.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKNativeWBRemarkView : UIView

//限制宽度 默认200 pt
@property (assign , nonatomic) CGFloat  limitWidth;
//需要备注的内容
@property (nonatomic, strong) NSString *remarkContent;


+ (TKNativeWBRemarkView *)showRemarkViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView;

+ (BOOL)dismissForView:(UIView *)view;

@end

NS_ASSUME_NONNULL_END

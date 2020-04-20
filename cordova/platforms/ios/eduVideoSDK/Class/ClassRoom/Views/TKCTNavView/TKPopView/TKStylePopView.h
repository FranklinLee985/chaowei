//
//  TKStylePopView.h
//  EduClass
//
//  Created by maqihan on 2019/4/1.
//  Copyright © 2019 talkcloud. All rights reserved.
//  一对一

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class TKStylePopView;

@interface TKStylePopView : UIView

+ (TKStylePopView *)showPopViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView;
+ (BOOL)dismissForView:(UIView *)view;

//发送样式同步信令
+ (void)publishStyleSignalingWithStyle:(NSString *)style tellID:(NSString *)tellID;

//页面布局
@property (nonatomic, assign) TKRoomLayout viewStyle;

@end

NS_ASSUME_NONNULL_END

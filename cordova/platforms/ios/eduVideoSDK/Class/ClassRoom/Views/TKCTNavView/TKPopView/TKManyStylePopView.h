//
//  TKManyStylePopView.h
//  EduClass
//
//  Created by maqihan on 2019/4/15.
//  Copyright © 2019 talkcloud. All rights reserved.
//	

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKManyStylePopView : UIView

+ (TKManyStylePopView *)showPopViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView;
+ (BOOL)dismissForView:(UIView *)view;

//发送样式同步信令
+ (void)publishStyleSignalingWithStyle:(NSString *)style tellID:(NSString *)tellID;

//页面布局
@property (nonatomic, assign) TKRoomLayout viewStyle;

@end

NS_ASSUME_NONNULL_END

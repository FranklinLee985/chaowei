//
//  TKManyNormalLayoutView.h
//  EduClass
//
//  Created by maqihan on 2019/4/3.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKLayoutBaseView.h"

NS_ASSUME_NONNULL_BEGIN
/**
 经典布局，视频在顶部
 */
@interface TKManyNormalLayoutView : TKLayoutBaseView

//分屏
- (void)splitScreenWithInfo:(NSDictionary *)dict superview:(UIView *)superview;

//拖拽
- (void)dragVideoView:(TKCTVideoSmallView *)videoView left:(CGFloat)left top:(CGFloat)top superview:(UIView *)view;

//缩放
- (void)scaleVideoViewWithZoomRatio:(CGFloat)ratio userID:(NSString *)userID superview:(UIView *)superview;

@end

NS_ASSUME_NONNULL_END

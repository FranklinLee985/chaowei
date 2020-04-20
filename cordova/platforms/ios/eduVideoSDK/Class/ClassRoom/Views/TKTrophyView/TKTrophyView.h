//
//  TKTrophyView.h
//  EduClass
//
//  Created by lyy on 2018/5/25.
//  Copyright © 2018年 talkcloud. All rights reserved.
//  自定义奖杯

#import "TKCTBaseView.h"

@interface TKTrophyView : TKCTBaseView


@property (nonatomic, copy) void(^sendTrophy)(NSDictionary *message);


- (id)initWithFrame:(CGRect)frame chatController:(NSString *)chatController;

/**
 显示自定义奖杯弹出框

 @param view 视频视图
 @param message 消息
 */
- (void)showOnView:(UIView *)view trophyMessage:(NSArray *)message;


@end

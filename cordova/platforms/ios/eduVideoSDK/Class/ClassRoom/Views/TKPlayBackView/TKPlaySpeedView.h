//
//  TKPlaySpeedView.h
//  test
//
//  Created by Yi on 2018/5/30.
//  Copyright © 2018年 admin. All rights reserved.
//  调速view 请调用initWithFrame 宽76, 高221
//  点击后 发送通知“TKPlaySpeedViewNoti”， 取object 格式“0.5， 1.75...”

#import <UIKit/UIKit.h>

@interface TKPlaySpeedView : UIView

- (void)show;
- (void)hidden;

@end

//
//  UIControl+clickedOnce.h
//  EduClassPad
//
//  Created by ifeng on 2017/7/12.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIControl (TKClickedOnce)
@property (nonatomic, assign) NSTimeInterval itk_acceptEventInterval;   // 可以用这个给重复点击加间隔
@property (nonatomic, assign) NSTimeInterval itk_acceptedEventTime;   // 可以用这个给重复点击加间隔
-(void)tkButtonExchangeImplementations;
@end

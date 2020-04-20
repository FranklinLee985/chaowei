//
//  UIView+TKRedDot.h
//  EduClass
//
//  Created by lyy on 2018/5/11.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (TKRedDot)

/**
 *  是否显示小红点
 */
@property (nonatomic, assign) BOOL showRedDot;

/**
 *  小红点半径
 */
@property (nonatomic, assign) CGFloat redDotRadius;

/**
 *  小红点偏移量，
 *  跟tabBarItem有所不同，通常小红点中心是所在view的右上角，所以默认为此
 */
@property (nonatomic, assign) CGPoint redDotOffset;

/**
 *  小红点颜色
 */
@property (nonatomic, strong) UIColor *redDotColor;

/**
 *  红点的边线宽度，默认0
 */
@property (nonatomic, assign) CGFloat redDotBorderWitdh;

/**
 *  小红点边线颜色，默认白色
 */
@property (nonatomic, strong) UIColor *redDotBorderColor;

/**
 *  显示badge, badge优先于小红点
 */
@property (nonatomic, copy) NSString *badgeValue;

/**
 *  显示badge时的offset
 */
@property (nonatomic, assign) CGPoint badgeOffset;

/**
 *  badge时的color
 */
@property (nonatomic, strong) UIColor *badgeColor;

@end

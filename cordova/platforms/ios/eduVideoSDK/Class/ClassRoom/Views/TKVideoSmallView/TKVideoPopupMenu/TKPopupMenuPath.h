//
//  YBPopupMenuPath.h
//  EduClass
//
//  Created by lyy on 2018/4/23.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, YBPopupMenuArrowDirection) {
    TKPopupMenuArrowDirectionTop = 0,  //箭头朝上
    TKPopupMenuArrowDirectionBottom,   //箭头朝下
    TKPopupMenuArrowDirectionLeft,     //箭头朝左
    TKPopupMenuArrowDirectionRight,    //箭头朝右
    TKPopupMenuArrowDirectionNone ,     //没有箭头
    TKPopupMenuArrowDirectionCenter,    //箭头在superview 中心

};

@interface TKPopupMenuPath : NSObject

+ (CAShapeLayer *)yb_maskLayerWithRect:(CGRect)rect
                            rectCorner:(UIRectCorner)rectCorner
                          cornerRadius:(CGFloat)cornerRadius
                            arrowWidth:(CGFloat)arrowWidth
                           arrowHeight:(CGFloat)arrowHeight
                         arrowPosition:(CGFloat)arrowPosition
                        arrowDirection:(YBPopupMenuArrowDirection)arrowDirection;

+ (UIBezierPath *)yb_bezierPathWithRect:(CGRect)rect
                             rectCorner:(UIRectCorner)rectCorner
                           cornerRadius:(CGFloat)cornerRadius
                            borderWidth:(CGFloat)borderWidth
                            borderColor:(UIColor *)borderColor
                        backgroundColor:(UIColor *)backgroundColor
                             arrowWidth:(CGFloat)arrowWidth
                            arrowHeight:(CGFloat)arrowHeight
                          arrowPosition:(CGFloat)arrowPosition
                         arrowDirection:(YBPopupMenuArrowDirection)arrowDirection;
@end

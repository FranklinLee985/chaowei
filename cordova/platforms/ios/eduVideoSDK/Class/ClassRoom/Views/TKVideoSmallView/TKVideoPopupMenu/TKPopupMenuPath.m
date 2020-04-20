//
//  TKPopupMenuPath.m
//  EduClass
//
//  Created by lyy on 2018/4/23.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKPopupMenuPath.h"
#import "TKRectConst.h"

@implementation TKPopupMenuPath

+ (CAShapeLayer *)yb_maskLayerWithRect:(CGRect)rect
                            rectCorner:(UIRectCorner)rectCorner
                          cornerRadius:(CGFloat)cornerRadius
                            arrowWidth:(CGFloat)arrowWidth
                           arrowHeight:(CGFloat)arrowHeight
                         arrowPosition:(CGFloat)arrowPosition
                        arrowDirection:(YBPopupMenuArrowDirection)arrowDirection
{
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    shapeLayer.path = [self yb_bezierPathWithRect:rect rectCorner:rectCorner cornerRadius:cornerRadius borderWidth:0 borderColor:nil backgroundColor:nil arrowWidth:arrowWidth arrowHeight:arrowHeight arrowPosition:arrowPosition arrowDirection:arrowDirection].CGPath;
    return shapeLayer;
}


+ (UIBezierPath *)yb_bezierPathWithRect:(CGRect)rect
                             rectCorner:(UIRectCorner)rectCorner
                           cornerRadius:(CGFloat)cornerRadius
                            borderWidth:(CGFloat)borderWidth
                            borderColor:(UIColor *)borderColor
                        backgroundColor:(UIColor *)backgroundColor
                             arrowWidth:(CGFloat)arrowWidth
                            arrowHeight:(CGFloat)arrowHeight
                          arrowPosition:(CGFloat)arrowPosition
                         arrowDirection:(YBPopupMenuArrowDirection)arrowDirection
{
    UIBezierPath *bezierPath = [UIBezierPath bezierPath];
    if (borderColor) {
        [borderColor setStroke];
    }
    if (backgroundColor) {
        [backgroundColor setFill];
    }
    
    bezierPath.lineWidth = borderWidth;
    rect = CGRectMake(borderWidth / 2, borderWidth / 2, TKRectWidth(rect) - borderWidth, TKRectHeight(rect) - borderWidth);
    CGFloat topRightRadius = 0,topLeftRadius = 0,bottomRightRadius = 0,bottomLeftRadius = 0;
    
    CGPoint topRightArcCenter,topLeftArcCenter,bottomRightArcCenter,bottomLeftArcCenter;
    
    if (rectCorner & UIRectCornerTopLeft) {
        topLeftRadius = cornerRadius;
    }
    if (rectCorner & UIRectCornerTopRight) {
        topRightRadius = cornerRadius;
    }
    if (rectCorner & UIRectCornerBottomLeft) {
        bottomLeftRadius = cornerRadius;
    }
    if (rectCorner & UIRectCornerBottomRight) {
        bottomRightRadius = cornerRadius;
    }
    
    if (arrowDirection == TKPopupMenuArrowDirectionTop) {
        topLeftArcCenter = CGPointMake(topLeftRadius + TKRectX(rect), arrowHeight + topLeftRadius + TKRectX(rect));
        topRightArcCenter = CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect), arrowHeight + topRightRadius + TKRectX(rect));
        bottomLeftArcCenter = CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) - bottomLeftRadius + TKRectX(rect));
        bottomRightArcCenter = CGPointMake(TKRectWidth(rect) - bottomRightRadius + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius + TKRectX(rect));
        
        if (arrowPosition < topLeftRadius + arrowWidth / 2) {
            arrowPosition = topLeftRadius + arrowWidth / 2;
        }else if (arrowPosition > TKRectWidth(rect) - topRightRadius - arrowWidth / 2) {
            arrowPosition = TKRectWidth(rect) - topRightRadius - arrowWidth / 2;
        }
        [bezierPath moveToPoint:CGPointMake(arrowPosition - arrowWidth / 2, arrowHeight + TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(arrowPosition, TKRectTop(rect) + TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(arrowPosition + arrowWidth / 2, arrowHeight + TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - topRightRadius, arrowHeight + TKRectX(rect))];
        [bezierPath addArcWithCenter:topRightArcCenter radius:topRightRadius startAngle:M_PI * 3 / 2 endAngle:2 * M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius - TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomRightArcCenter radius:bottomRightRadius startAngle:0 endAngle:M_PI_2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) + TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomLeftArcCenter radius:bottomLeftRadius startAngle:M_PI_2 endAngle:M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectX(rect), arrowHeight + topLeftRadius + TKRectX(rect))];
        [bezierPath addArcWithCenter:topLeftArcCenter radius:topLeftRadius startAngle:M_PI endAngle:M_PI * 3 / 2 clockwise:YES];
        
    }else if (arrowDirection == TKPopupMenuArrowDirectionBottom) {
        topLeftArcCenter = CGPointMake(topLeftRadius + TKRectX(rect),topLeftRadius + TKRectX(rect));
        topRightArcCenter = CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect), topRightRadius + TKRectX(rect));
        bottomLeftArcCenter = CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) - bottomLeftRadius + TKRectX(rect) - arrowHeight);
        bottomRightArcCenter = CGPointMake(TKRectWidth(rect) - bottomRightRadius + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius + TKRectX(rect) - arrowHeight);
        if (arrowPosition < bottomLeftRadius + arrowWidth / 2) {
            arrowPosition = bottomLeftRadius + arrowWidth / 2;
        }else if (arrowPosition > TKRectWidth(rect) - bottomRightRadius - arrowWidth / 2) {
            arrowPosition = TKRectWidth(rect) - bottomRightRadius - arrowWidth / 2;
        }
        [bezierPath moveToPoint:CGPointMake(arrowPosition + arrowWidth / 2, TKRectHeight(rect) - arrowHeight + TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(arrowPosition, TKRectHeight(rect) + TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(arrowPosition - arrowWidth / 2, TKRectHeight(rect) - arrowHeight + TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) - arrowHeight + TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomLeftArcCenter radius:bottomLeftRadius startAngle:M_PI_2 endAngle:M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectX(rect), topLeftRadius + TKRectX(rect))];
        [bezierPath addArcWithCenter:topLeftArcCenter radius:topLeftRadius startAngle:M_PI endAngle:M_PI * 3 / 2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect), TKRectX(rect))];
        [bezierPath addArcWithCenter:topRightArcCenter radius:topRightRadius startAngle:M_PI * 3 / 2 endAngle:2 * M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius - TKRectX(rect) - arrowHeight)];
        [bezierPath addArcWithCenter:bottomRightArcCenter radius:bottomRightRadius startAngle:0 endAngle:M_PI_2 clockwise:YES];
        
    }else if (arrowDirection == TKPopupMenuArrowDirectionLeft) {
        //箭头在左
        
        topLeftArcCenter = CGPointMake(topLeftRadius + TKRectX(rect) + arrowHeight,topLeftRadius + TKRectX(rect));
        topRightArcCenter = CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect), topRightRadius + TKRectX(rect));
        bottomLeftArcCenter = CGPointMake(bottomLeftRadius + TKRectX(rect) + arrowHeight, TKRectHeight(rect) - bottomLeftRadius + TKRectX(rect));
        bottomRightArcCenter = CGPointMake(TKRectWidth(rect) - bottomRightRadius + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius + TKRectX(rect));
        if (arrowPosition < topLeftRadius + arrowWidth / 2) {
            arrowPosition = topLeftRadius + arrowWidth / 2;
        }else if (arrowPosition > TKRectHeight(rect) - bottomLeftRadius - arrowWidth / 2) {
            arrowPosition = TKRectHeight(rect) - bottomLeftRadius - arrowWidth / 2;
        }
        [bezierPath moveToPoint:CGPointMake(arrowHeight + TKRectX(rect), arrowPosition + arrowWidth / 2)];
        [bezierPath addLineToPoint:CGPointMake(TKRectX(rect), arrowPosition)];
        [bezierPath addLineToPoint:CGPointMake(arrowHeight + TKRectX(rect), arrowPosition - arrowWidth / 2)];
        [bezierPath addLineToPoint:CGPointMake(arrowHeight + TKRectX(rect), topLeftRadius + TKRectX(rect))];
        [bezierPath addArcWithCenter:topLeftArcCenter radius:topLeftRadius startAngle:M_PI endAngle:M_PI * 3 / 2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - topRightRadius, TKRectX(rect))];
        [bezierPath addArcWithCenter:topRightArcCenter radius:topRightRadius startAngle:M_PI * 3 / 2 endAngle:2 * M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius - TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomRightArcCenter radius:bottomRightRadius startAngle:0 endAngle:M_PI_2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(arrowHeight + bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) + TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomLeftArcCenter radius:bottomLeftRadius startAngle:M_PI_2 endAngle:M_PI clockwise:YES];
        
    }else if (arrowDirection == TKPopupMenuArrowDirectionRight) {
        //箭头在右
        
        topLeftArcCenter = CGPointMake(topLeftRadius + TKRectX(rect),topLeftRadius + TKRectX(rect));
        topRightArcCenter = CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect) - arrowHeight, topRightRadius + TKRectX(rect));
        
        bottomLeftArcCenter = CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) - bottomLeftRadius + TKRectX(rect));
        
        bottomRightArcCenter = CGPointMake(TKRectWidth(rect) - bottomRightRadius + TKRectX(rect) - arrowHeight, TKRectHeight(rect) - bottomRightRadius + TKRectX(rect));
        
        if (arrowPosition < topRightRadius + arrowWidth / 2) {
            //设置箭头的位置
            arrowPosition = topRightRadius + bottomRightArcCenter.y / 2;
            
        }else if (arrowPosition > TKRectHeight(rect) - bottomRightRadius - arrowWidth / 2) {
            arrowPosition = TKRectHeight(rect) - bottomRightRadius - arrowWidth / 2;
        }
        [bezierPath moveToPoint:CGPointMake(TKRectWidth(rect) - arrowHeight + TKRectX(rect), arrowPosition - arrowWidth / 2)];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) + TKRectX(rect), arrowPosition)];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - arrowHeight + TKRectX(rect), arrowPosition + arrowWidth / 2)];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - arrowHeight + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius - TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomRightArcCenter radius:bottomRightRadius startAngle:0 endAngle:M_PI_2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) + TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomLeftArcCenter radius:bottomLeftRadius startAngle:M_PI_2 endAngle:M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectX(rect), arrowHeight + topLeftRadius + TKRectX(rect))];
        [bezierPath addArcWithCenter:topLeftArcCenter radius:topLeftRadius startAngle:M_PI endAngle:M_PI * 3 / 2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect) - arrowHeight, TKRectX(rect))];
        [bezierPath addArcWithCenter:topRightArcCenter radius:topRightRadius startAngle:M_PI * 3 / 2 endAngle:2 * M_PI clockwise:YES];
        
    }else if (arrowDirection == TKPopupMenuArrowDirectionNone) {
        topLeftArcCenter = CGPointMake(topLeftRadius + TKRectX(rect),  topLeftRadius + TKRectX(rect));
        topRightArcCenter = CGPointMake(TKRectWidth(rect) - topRightRadius + TKRectX(rect),  topRightRadius + TKRectX(rect));
        bottomLeftArcCenter = CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) - bottomLeftRadius + TKRectX(rect));
        bottomRightArcCenter = CGPointMake(TKRectWidth(rect) - bottomRightRadius + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius + TKRectX(rect));
        [bezierPath moveToPoint:CGPointMake(topLeftRadius + TKRectX(rect), TKRectX(rect))];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) - topRightRadius, TKRectX(rect))];
        [bezierPath addArcWithCenter:topRightArcCenter radius:topRightRadius startAngle:M_PI * 3 / 2 endAngle:2 * M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectWidth(rect) + TKRectX(rect), TKRectHeight(rect) - bottomRightRadius - TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomRightArcCenter radius:bottomRightRadius startAngle:0 endAngle:M_PI_2 clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(bottomLeftRadius + TKRectX(rect), TKRectHeight(rect) + TKRectX(rect))];
        [bezierPath addArcWithCenter:bottomLeftArcCenter radius:bottomLeftRadius startAngle:M_PI_2 endAngle:M_PI clockwise:YES];
        [bezierPath addLineToPoint:CGPointMake(TKRectX(rect), arrowHeight + topLeftRadius + TKRectX(rect))];
        [bezierPath addArcWithCenter:topLeftArcCenter radius:topLeftRadius startAngle:M_PI endAngle:M_PI * 3 / 2 clockwise:YES];
    }
    
    [bezierPath closePath];
    return bezierPath;
}

@end

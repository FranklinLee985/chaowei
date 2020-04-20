//
//  TKRectConst.h
//  EduClass
//
//  Created by lyy on 2018/4/23.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

UIKIT_STATIC_INLINE CGFloat TKRectWidth(CGRect rect)
{
    return rect.size.width;
}

UIKIT_STATIC_INLINE CGFloat TKRectHeight(CGRect rect)
{
    return rect.size.height;
}

UIKIT_STATIC_INLINE CGFloat TKRectX(CGRect rect)
{
    return rect.origin.x;
}

UIKIT_STATIC_INLINE CGFloat TKRectY(CGRect rect)
{
    return rect.origin.y;
}

UIKIT_STATIC_INLINE CGFloat TKRectTop(CGRect rect)
{
    return rect.origin.y;
}

UIKIT_STATIC_INLINE CGFloat TKRectBottom(CGRect rect)
{
    return rect.origin.y + rect.size.height;
}

UIKIT_STATIC_INLINE CGFloat TKRectLeft(CGRect rect)
{
    return rect.origin.x;
}

UIKIT_STATIC_INLINE CGFloat TKRectRight(CGRect rect)
{
    return rect.origin.x + rect.size.width;
}






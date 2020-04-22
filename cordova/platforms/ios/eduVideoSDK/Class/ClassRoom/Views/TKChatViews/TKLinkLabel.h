//
//  TKLinkLabel.h
//  EduClass
//
//  Created by lyy on 2018/8/7.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
//#import "NSAttributedString+Emotion.h"

// 链接类型
typedef NS_ENUM(NSInteger, TKLinkType)
{
    TKLinkTypeUserHandle,     //用户昵称  eg: @kingzwt
    TKLinkTypeHashTag,        //内容标签  eg: #hello
    TKLinkTypeURL,            //链接地址  eg: http://www.baidu.com
    TKLinkTypePhoneNumber     //电话号码  eg: 13888888888
};

// 可用于识别的链接类型
typedef NS_OPTIONS(NSUInteger, TKLinkDetectionTypes)
{
    TKLinkDetectionTypeUserHandle  = (1 << 0),
    TKLinkDetectionTypeHashTag     = (1 << 1),
    TKLinkDetectionTypeURL         = (1 << 2),
    TKLinkDetectionTypePhoneNumber = (1 << 3),
    
    TKLinkDetectionTypeNone        = 0,
    TKLinkDetectionTypeAll         = NSUIntegerMax
};

typedef void (^TKLinkHandler)(TKLinkType linkType, NSString *string, NSRange range);

@interface TKLinkLabel : UILabel <NSLayoutManagerDelegate>

@property (nonatomic, assign, getter = isAutomaticLinkDetectionEnabled) BOOL automaticLinkDetectionEnabled;

@property (nonatomic, strong) UIColor *linkColor;

@property (nonatomic, strong) UIColor *linkHighlightColor;

@property (nonatomic, strong) UIColor *linkBackgroundColor;

@property (nonatomic, assign) TKLinkDetectionTypes linkDetectionTypes;

@property (nonatomic, copy) TKLinkHandler linkTapHandler;

@property (nonatomic, copy) TKLinkHandler linkLongPressHandler;

@property (nonatomic, assign) BOOL isWhiteColor;

@end

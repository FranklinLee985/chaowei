//
//  HMEmotionToolbar.h
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.
//  表情底部的工具条

#import <UIKit/UIKit.h>
@class TKEmotionToolbar;

typedef enum {
//    HMEmotionTypeRecent, // 最近
    HMEmotionTypeEmoji, // Emoji
//    HMEmotionTypeSend,//发送
} HMEmotionType;

@protocol HMEmotionToolbarDelegate <NSObject>

@optional
- (void)emotionToolbar:(TKEmotionToolbar *)toolbar didSelectedButton:(HMEmotionType)emotionType;
@end

@interface TKEmotionToolbar : UIView
@property (nonatomic, weak) id<HMEmotionToolbarDelegate> delegate;
@end

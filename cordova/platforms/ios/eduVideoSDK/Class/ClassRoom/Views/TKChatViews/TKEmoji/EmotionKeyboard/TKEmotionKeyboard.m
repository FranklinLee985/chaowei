//
//  TKEmotionKeyboard.m
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.
//  表情键盘

#import "TKEmojiHeader.h"
#import "TKEmotionKeyboard.h"
#import "TKEmotionListView.h"
#import "TKEmotionToolbar.h"
#import "TKEmotionTool.h"
#import "UIView+TKExtension.h"

@interface TKEmotionKeyboard() <HMEmotionToolbarDelegate>
/** 表情列表 */
@property (nonatomic, weak) TKEmotionListView *listView;
/** 表情工具条 */
@property (nonatomic, weak) TKEmotionToolbar *toollbar;
@end

@implementation TKEmotionKeyboard

+ (instancetype)keyboard
{
    return [[self alloc] init];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"TKEmoji.bundle/emoticon_keyboard_background"]];
        
        // 1.添加表情列表
        TKEmotionListView *listView = [[TKEmotionListView alloc] init];
        [self addSubview:listView];
        self.listView = listView;
        
        // 2.添加表情工具条
//        HMEmotionToolbar *toollbar = [[HMEmotionToolbar alloc] init];
//        toollbar.delegate = self;
//        [self addSubview:toollbar];
//        self.toollbar = toollbar;
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    // 1.设置工具条的frame
//    self.toollbar.width = self.width;
//    self.toollbar.height = 35;
//    self.toollbar.y = self.height - self.toollbar.height;
    
    // 2.设置表情列表的frame
    self.listView.width = self.width;
    self.listView.height = self.frame.size.height;
    self.listView.emotions = [TKEmotionTool defaultEmotions];
}

#pragma mark - HMEmotionToolbarDelegate
- (void)emotionToolbar:(TKEmotionToolbar *)toolbar didSelectedButton:(HMEmotionType)emotionType
{
    switch (emotionType) {
            
        case HMEmotionTypeEmoji: // Emoji
            self.listView.emotions = [TKEmotionTool defaultEmotions];
            break;
            
            
//        case HMEmotionTypeRecent: // 最近
//            self.listView.emotions = [HMEmotionTool recentEmotions];
//            break;
            
//        case HMEmotionTypeSend: //发送
//            [self addNoti];
//            break;

    }
}
@end

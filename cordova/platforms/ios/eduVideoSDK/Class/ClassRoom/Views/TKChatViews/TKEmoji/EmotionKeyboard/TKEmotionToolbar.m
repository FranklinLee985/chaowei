//
//  HMEmotionToolbar.m
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.
#import "TKEmojiHeader.h"
#define HMEmotionToolbarButtonMaxCount 1
#import "UIView+TKExtension.h"
#import "TKEmotionToolbar.h"

@interface TKEmotionToolbar()
/** 记录当前选中的按钮 */
@property (nonatomic, weak) UIButton *selectedButton;
@end

@implementation TKEmotionToolbar

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // 1.添加4个按钮
//        [self setupButton:@"最近" tag:HMEmotionTypeRecent];
        [self setupButton:@"默认" tag:HMEmotionTypeEmoji];
//        [self setupButton:@"发送" tag:HMEmotionTypeSend];
        // 2.监听表情选中的通知
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(emotionDidSelected:) name:TKEmotionDidSelectedNotification object:nil];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

/**
 *  表情选中
 */
- (void)emotionDidSelected:(NSNotification *)note
{
//    if (self.selectedButton.tag == HMEmotionTypeRecent) {
        [self buttonClick:self.selectedButton];
//    }
}

/**
 *  添加按钮
 *
 *  @param title 按钮文字
 */
- (UIButton *)setupButton:(NSString *)title tag:(HMEmotionType)tag
{
    UIButton *button = [[UIButton alloc] init];
    button.tag = tag;
    
    // 文字
    [button setTitle:title forState:UIControlStateNormal];
    [button setTitleColor:[UIColor darkGrayColor] forState:UIControlStateNormal];
    [button setTitleColor:[UIColor colorWithRed:52/255.0 green:152/255.0 blue:219/255.0 alpha:1] forState:UIControlStateSelected];
    [button addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchUpInside];
    button.titleLabel.font = [UIFont systemFontOfSize:13];
    
    // 添加按钮
    [self addSubview:button];
    
    
    return button;
}

/**
 *  监听按钮点击
 */
- (void)buttonClick:(UIButton *)button
{
    // 1.控制按钮状态
    self.selectedButton.selected = NO;
    button.selected = YES;
    self.selectedButton = button;
    
    // 2.通知代理
    if ([self.delegate respondsToSelector:@selector(emotionToolbar:didSelectedButton:)]) {
        [self.delegate emotionToolbar:self didSelectedButton:(HMEmotionType)button.tag];
    }
}

- (void)setDelegate:(id<HMEmotionToolbarDelegate>)delegate
{
    _delegate = delegate;
    
    // 获得“默认”按钮
//    UIButton *defaultButton = (UIButton *)[self viewWithTag:HMEmotionTypeEmoji];
    // 默认选中“默认”按钮
//    [self buttonClick:defaultButton];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    // 设置工具条按钮的frame
    CGFloat buttonW = self.width / HMEmotionToolbarButtonMaxCount;
    CGFloat buttonH = self.height;
    for (int i = 0; i<HMEmotionToolbarButtonMaxCount; i++) {
        
        UIButton *button = self.subviews[i];
        button.width = buttonW;
        button.height = buttonH;
        button.x = i * buttonW;
    }
}

@end

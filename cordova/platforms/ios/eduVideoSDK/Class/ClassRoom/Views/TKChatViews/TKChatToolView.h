//
//  TKChatToolView.h
//  EduClass
//
//  Created by lyy on 2018/4/27.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKEmotionTextView.h"


@protocol TKChatToolViewDelegate <NSObject>
@optional
- (void)sendMessage:(NSString *)message;
- (void)chatToolViewDidBeginEditing:(UITextView *)textView;
- (void)chatToolViewChangeHeight:(CGFloat)height;

@end

@interface TKChatToolView : UIView

@property (nonatomic, strong) TKEmotionTextView *inputField;//输入框
@property (nonatomic, strong) UIButton *emotionButton;//表情按钮


/**
 是否是 自定义视图
 */
@property (nonatomic, assign) BOOL isCustomInputView;
@property (nonatomic, weak) id<TKChatToolViewDelegate> delegate;

- (instancetype)initWithFrame:(CGRect)frame isDistance:(BOOL)isDistance;

@end

//
//  TKLoginInputView.h
//  EduClass
//
//  Created by lyy on 2018/4/17.
//  Copyright © 2018年 拓课云. All rights reserved.
//

#import <UIKit/UIKit.h>

//#import "TKPickViewTextField.h"

@protocol TKLoginChoiceRoleDelegate <NSObject>

/**
 角色选择
 */
- (void)choiceRole:(int)role;
- (void)clickChoiceRole;
@end

@protocol TKLoginInputViewDelegate <NSObject>

/**
 文本（课堂号）输入回调

 @param textField 输入控件
 @param range 范围
 @param string 文本
 @return return NO to not change text
 */
- (BOOL)loginTextField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string;

- (void)beginInputTextField;
@end



@interface TKLoginInputView : UIView

@property (nonatomic, weak) id<TKLoginInputViewDelegate> inputDelegate;
@property (nonatomic, weak) id<TKLoginChoiceRoleDelegate> choiceRoleDelegate;

@property (nonatomic, strong) NSString *text;
@property (nonatomic, strong) UITextField *inputView;//输入框

/**
 自定义input

 @param frame 位置大小
 @param text 显示的文本
 @param placeholder 未输入时的默认值
 @param show 是否显示角色选择器
 @param imageName icon标签
 @return return value description
 */
- (instancetype)initWithFrame:(CGRect)frame showText:(NSString *)text placeholderText:(NSString *)placeholder isShow:(BOOL)show setImageName:(NSString *)imageName;


- (void)choiceCancel;
@end

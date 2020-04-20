//
//  TKPasswordView.h
//  EduClass
//
//  Created by lyy on 2018/5/3.
//  Copyright © 2018年 talkcloud. All rights reserved.
//


#import <UIKit/UIKit.h>
#import <TKRoomSDK/TKRoomSDK.h>

@class TKMacro;

@interface TKAlertView : UIView

/**
 提示框

 @param title 标题
 @param content 内容
 @param confirmTitle 确定按钮
 @return id
 */
- (id)initWithTitle:(NSString *)title
        contentText:(NSString *)content
       confirmTitle:(NSString *)confirmTitle;


/**
 提示框

 @param title 标题
 @param content 内容
 @param leftTitle 左按钮
 @param rightTitle 右按钮
 @return id
 */
- (id)initWithTitle:(NSString *)title
        contentText:(NSString *)content
          leftTitle:(NSString *)leftTitle
         rightTitle:(NSString *)rightTitle;

/**
 警示类 提示框
 
 确定按钮 红色 其他同上
 */
- (id)initForWarningWithTitle:(NSString *)title
                  contentText:(NSString *)content
                    leftTitle:(NSString *)leftTitle
                   rightTitle:(NSString *)rightTitle;
/**
 输入密码提示框

 @param title 标题
 @param style 提示内容样式
 @param confirmTitle 确定按钮
 @return id
 */
- (id)initWithInputTitle:(NSString *)title
                   style:(TKRoomErrorCode)style
                   tplID:(NSString *)tplid
                userrole:(TKUserRoleType)userrole
            confirmTitle:(NSString *)confirmTitle;


- (void)show;
- (void)showOnWindow;
- (void)dismissAlert;


@property (nonatomic, copy) dispatch_block_t lelftBlock;
@property (nonatomic, copy) dispatch_block_t rightBlock;
@property (nonatomic, copy) dispatch_block_t dismissBlock;

@property (nonatomic, copy) void(^confirmBlock)(NSString *password);

@end



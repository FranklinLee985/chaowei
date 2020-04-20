//
//  TKChatMessageModel.h
//  EduClassPad
//
//  Created by ifeng on 2017/5/12.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <Foundation/Foundation.h>
//@import UIKit;
#import <UIKit/UIKit.h>
@interface TKChatMessageModel : NSObject
@property (nonatomic, strong) NSString *iUserName;
@property (nonatomic, strong) NSString *iMessage;
@property (nonatomic, strong) NSString *iCospath;
@property (nonatomic, strong) NSString *iTime;
@property (nonatomic, copy)   NSString *iFromid;
@property (nonatomic, assign) TKChatMessageType iChatMessageType;
@property (nonatomic, assign) TKChatRoleType iChatRoleType;

@property (nonatomic, strong) UIColor  * iMessageTypeColor;
@property (nonatomic, strong) NSString * iTranslationMessage;

@property (nonatomic, assign) CGFloat messageHeight;// 消息高度
@property (nonatomic, assign) CGFloat translationHeight;// 翻译高度
@property (nonatomic, assign) CGFloat height;// 行高


- (instancetype) initWithMsgType:(TKChatMessageType)iMsgType role:(TKChatRoleType)iRole message:(NSString *)iMessage cospath:(NSString *)iCospath userName:(NSString *)iUserName fromid:(NSString *)iFromid time:(NSString *)iTime;

@end

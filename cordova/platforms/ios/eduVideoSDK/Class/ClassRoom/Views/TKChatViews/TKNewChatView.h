//
//  TKNewChatView.h
//  EduClass
//
//  Created by talk on 2018/11/22.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKEduSessionHandle.h"
#import "TKChatToolView.h"
#import "TKChatMessageModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKNewChatView : UIView 

@property (nonatomic, strong) UIButton *leftBtn;
@property (nonatomic, strong) TKChatToolView *keyboardView;// 实际 聊天输入工具条
@property (nonatomic, strong) void(^messageBtnClickBlock)(UIButton *);
@property (nonatomic, assign) float msgCount;
@property (nonatomic, copy)	  void(^hideComplete)();

- (void)hide:(BOOL)hide ;
- (void)setBadgeNumber:(float)num;
- (void)setUserRoleType:(TKUserRoleType)type;
- (void)removeSubviews;

//点击消息按钮
- (void)showMessageView:(UIButton *)sender;
//接收聊天消息
- (void)messageReceived:(NSString *)message
                 fromID:(NSString *)peerID
              extension:(NSDictionary *)extension;
- (void)reloadData;

@end

NS_ASSUME_NONNULL_END

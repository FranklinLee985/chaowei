//
//  TKWhiteBroadDelegate.h
//  TKWhiteBroad
//
//  Created by MAC-MiNi on 2018/4/9.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol TKWhiteBoardManagerDelegate <NSObject>

@required

/**
 文件列表回调
 @param fileList 文件列表 是一个NSArray类型的数据
 */
- (void)onWhiteBroadFileList:(NSArray *)fileList;

/**
PubMsg消息
 */
- (void)onWhiteBroadPubMsgWithMsgID:(NSString *)msgID
                            msgName:(NSString *)msgName
                               data:(NSObject *)data
                             fromID:(NSString *)fromID
                             inList:(BOOL)inlist
                                 ts:(long)ts;

/**
 msglist消息

 @param msgList 消息
 */
- (void)onWhiteBoardOnRoomConnectedMsglist:(NSDictionary *)msgList;

/**
 界面更新
 */
- (void)onWhiteBoardViewStateUpdate:(NSDictionary *)message;

/**
 教室加载状态
 
 */
- (void)onWhiteBoardLoadedState:(NSDictionary *)message;
@end


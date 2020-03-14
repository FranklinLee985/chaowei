//
//  TKPlaybackDelegate.h
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/10/19.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#ifndef TKPlaybackDelegate_h
#define TKPlaybackDelegate_h

#import "TKRoomDelegate.h"

@protocol TKPlaybackManagerDelegate<TKRoomManagerDelegate>

@optional
/**
 回放时收到聊天消息
 @param message 聊天消息内容
 @param peerID 发送者用户ID
 @param ts 发送消息的时间戳
 @param extension 消息扩展信息（用户昵称、用户角色等等）
 */
- (void)roomManagerPlaybackMessageReceived:(NSString *)message
                                    fromID:(NSString *)peerID
                                        ts:(NSTimeInterval)ts
                                 extension:(NSDictionary *)extension;
/**
 获取到回放总时长的回调
 @param duration 回放的总时长
 */
- (void)roomManagerReceivePlaybackDuration:(NSTimeInterval)duration;

/**
 回放时接收到从服务器发来的回放进度变化
 @param time 变化的时间进度
 */
- (void)roomManagerPlaybackUpdateTime:(NSTimeInterval)time;

/**
 回放时清理
 */
- (void)roomManagerPlaybackClearAll;

/**
 回放播放完毕
 */
- (void)roomManagerPlaybackEnd;

@end

#endif /* TKPlaybackDelegate_h */

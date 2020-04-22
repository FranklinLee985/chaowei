//
//  TKRoomDelegate.h
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/3/20.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//
#import "TKRoomDefines.h"

#pragma mark - JoinRoomNotification
// 可以在userInfo 获取相关信息，包括error和链路相关统计信息
//加入房间成功
FOUNDATION_EXTERN NSNotificationName const TKRoomManagerJoinRoomSuccessNotification;
//加入房间失败
FOUNDATION_EXTERN NSNotificationName const TKRoomManagerJoinRoomFailedNotification;

#pragma mark - TKRoomManagerDelegate
@protocol TKRoomManagerDelegate<NSObject>

@optional
/**
    成功进入房间
 */
- (void)roomManagerRoomJoined;

/**
    已经离开房间
 */
- (void)roomManagerRoomLeft;

/**
 失去连接
 */
- (void)roomManagerOnConnectionLost;

/**
 发生错误 回调

 @param error error
 */
- (void)roomManagerDidOccuredError:(NSError *)error;

/**
 发生警告 回调

 @param code 警告码
 */
- (void)roomManagerDidOccuredWaring:(TKRoomWarningCode)code;

/**
    有用户进入房间
    @param peerID 用户ID
    @param inList true：在自己之前进入；false：在自己之后进入
 */
- (void)roomManagerUserJoined:(NSString *)peerID inList:(BOOL)inList;

/**
    有用户离开房间
    @param peerID 用户ID
 */
- (void)roomManagerUserLeft:(NSString *)peerID;

/**
    自己被踢出房间
    @param reason 被踢原因
 */
- (void)roomManagerKickedOut:(NSDictionary *)reason;

/**
    有用户的属性发生了变化
    @param peerID 用户ID
    @param properties 发生变化的属性
    @param fromId 修改用户属性消息的发送方的id
 */
- (void)roomManagerUserPropertyChanged:(NSString *)peerID
                            properties:(NSDictionary*)properties
                                fromId:(NSString *)fromId;

/**
    用户视频状态变化，此回调已经失效
    @param peerID 用户ID
    @param state 视频发布状态
 */

- (void)roomManagerPublishStateWithUserID:(NSString *)peerID publishState:(TKPublishState)state TK_Deprecated("Did deprecated, please use '- (void)roomManagerOnUserVideoStatus:state:' '- (void)roomManagerOnUserVideoStatus:deviceId:state:' '- (void)roomManagerOnUserAudioStatus:state:' ");

/**
 用户视频状态变化的通知

 @param peerID 用户ID
 @param state 视频状态
 */
- (void)roomManagerOnUserVideoStatus:(NSString *)peerID
                               state:(TKMediaState)state;
/**
 用户某一视频设备的视频状态变化的通知（多流模式）
 
 @param peerID 用户ID
 @param deviceId 视频设备ID
 @param state 视频状态
 */
- (void)roomManagerOnUserVideoStatus:(NSString *)peerID
                            deviceId:(NSString *)deviceId
                               state:(TKMediaState)state;
/**
 用户音频状态变化的通知，
 
 @param peerID 用户ID
 @param state 音频状态
 */
- (void)roomManagerOnUserAudioStatus:(NSString *)peerID
                               state:(TKMediaState)state;



/**
    收到自定义信令 发布消息
    @param msgID 消息id
    @param msgName 消息名字
    @param ts 消息时间戳
    @param data 消息数据，可以是Number、String、NSDictionary或NSArray
    @param fromID  消息发布者的ID
    @param inlist 是否是inlist中的信息
 */
- (void)roomManagerOnRemotePubMsgWithMsgID:(NSString *)msgID
                                   msgName:(NSString *)msgName
                                      data:(NSObject *)data
                                    fromID:(NSString *)fromID
                                    inList:(BOOL)inlist
                                        ts:(long)ts;

/**
    收到自定义信令 删去消息
    @param msgID 消息id
    @param msgName 消息名字
    @param ts 消息时间戳
    @param data 消息数据，可以是Number、String、NSDictionary或NSArray
    @param fromID  消息发布者的ID
    @param inlist 是否是inlist中的信息
 */
- (void)roomManagerOnRemoteDelMsgWithMsgID:(NSString *)msgID
                                   msgName:(NSString *)msgName
                                      data:(NSObject *)data
                                    fromID:(NSString *)fromID 
                                    inList:(BOOL)inlist
                                        ts:(long)ts;
/**
    收到聊天消息
    @param message 聊天消息内容
    @param peerID 发送者用户ID
    @param extension 消息扩展信息（用户昵称、用户角色等等）
 */
- (void)roomManagerMessageReceived:(NSString *)message
                            fromID:(NSString *)peerID
                         extension:(NSDictionary *)extension;
/**
 视频数据统计
 
 @param peerId 用户ID
 @param stats 数据信息
 */
- (void)roomManagerOnVideoStatsReport:(NSString *)peerId stats:(TKVideoStats *)stats;
/**
 音频数据统计
 
 @param peerId 用户ID
 @param stats 数据信息
 */
- (void)roomManagerOnAudioStatsReport:(NSString *)peerId stats:(TKAudioStats *)stats;


/**
 音视频统计数据

 @param stats 视频和音频的统计数据
 */
- (void)roomManagerOnRtcStatsReport:(TKRtcStats *)stats;


/**
 用户的音频音量大小变化的回调

 @param peeID 用户ID
 @param volume 音量大小（0 - 32670）
 */
- (void)roomManagerOnAudioVolumeWithPeerID:(NSString *)peeID volume:(int)volume;

/**
 纯音频 与音视频 教室切换
 
 @param fromId 用户ID  切换房间模式的用户ID
 @param onlyAudio 是否是纯音频
 */
- (void)roomManagerOnAudioRoomSwitch:(NSString *)fromId onlyAudio:(BOOL)onlyAudio;

/**
 播放某用户视频，渲染视频第一帧时，会收到回调；如果没有unplay某用户的视频，而再次play该用户视频时，不会再次收到此回调。

 @param peerID 用户ID
 @param width 视频宽
 @param height 视频高
 @param type 视频类型
 */
- (void)roomManagerOnFirstVideoFrameWithPeerID:(NSString *)peerID
                                         width:(NSInteger)width
                                        height:(NSInteger)height
                                     mediaType:(TKMediaType)type;

/**
 播放用户某一视频设备视频，渲染视频第一帧时，会收到回调；如果没有unplay，而再次play视频时，不会再次收到此回调。

 @param peerID 用户ID
 @param deviceId 视频设备ID
 @param width 视频宽
 @param height 视频高
 @param type 视频类型
 */
- (void)roomManagerOnFirstVideoFrameWithPeerID:(NSString *)peerID
                                      deviceId:(NSString *)deviceId
                                         width:(NSInteger)width
                                        height:(NSInteger)height
                                     mediaType:(TKMediaType)type;



/**
 视频播放过程中画面状态回调

 @param peerId 用户ID
 @param deviceId 视频设备ID
 @param state 画面状态
 @param type 视频类型
 */
- (void)roomManagerOnVideoStateChange:(NSString *)peerId
                             deviceId:(NSString *)deviceId
                           videoState:(TKRenderState)state
                            mediaType:(TKMediaType)type;

/**
 音频播放过程中状态回调
 
 @param peerId 用户ID
 @param state 音频状态
 @param type 类型
 */
- (void)roomManagerOnAudioStateChange:(NSString *)peerId
                           audioState:(TKRenderState)state
                            mediaType:(TKMediaType)type;

/**
 播放某用户音频，会收到此回调；如果没有unplay某用户的音频，而再次play该用户音频时，不会再次收到此回调。

 @param peerID 用户ID
 @param type 音频类型
 */
- (void)roomManagerOnFirstAudioFrameWithPeerID:(NSString *)peerID mediaType:(TKMediaType)type;

/**
 网络测速回调
 @param networkQuality 网速质量 (TKNetQuality_Down 测速失败)
 @param delay 延迟(毫秒)
 */
- (void)onNetworkQuality:(TKNetQuality)networkQuality
                   delay:(NSInteger)delay;

#pragma mark meidia
/**
    用户媒体流发布状态 变化回调
    @param peerId 用户id
    @param state 0:取消  非0：发布
    @param message 扩展消息
 */
- (void)roomManagerOnShareMediaState:(NSString *)peerId
                               state:(TKMediaState)state
                    extensionMessage:(NSDictionary *)message;

/**
    更新媒体流的信息回调
    @param duration 媒体流当前播放的时间点
    @param pos 媒体流当前的进度
    @param isPlay 播放（YES）暂停（NO）
 */
- (void)roomManagerUpdateMediaStream:(NSTimeInterval)duration
                                 pos:(NSTimeInterval)pos
                              isPlay:(BOOL)isPlay;

/**
    媒体流加载出第一帧画面回调
 */
- (void)roomManagerMediaLoaded;

#pragma mark screen
/**
    用户桌面共享状态 变化回调
    @param peerId 用户id
    @param state 状态0:取消  非0：发布
 */
- (void)roomManagerOnShareScreenState:(NSString *)peerId
                                state:(TKMediaState)state;

#pragma mark file
/**
    用户电影共享状态 变化回调
    @param peerId 用户id
    @param state 状态0:取消  非0：发布
    @param message 扩展消息
 */
- (void)roomManagerOnShareFileState:(NSString *)peerId
                              state:(TKMediaState)state
                   extensionMessage:(NSDictionary *)message;



@end

#pragma mark - TKMediaFrameDelegate
@protocol TKMediaFrameDelegate<NSObject>

///************************该部分回调函数 均不是线程安全的************************///

@optional
/**
 本地采集的音频数据

 @param frame 音频数据
 @param type 采集源
 */
- (void)onCaptureAudioFrame:(TKAudioFrame *)frame sourceType:(TKMediaType)type;
/**
 本地采集的视频数据
 
 @param frame 视频数据
 @param type 采集源
 */
- (void)onCaptureVideoFrame:(TKVideoFrame *)frame sourceType:(TKMediaType)type;

/**
 收到远端音频数据

 @param frame 音频数据
 @param peerId 用户ID
 @param type 采集源
 */
- (void)onRenderAudioFrame:(TKAudioFrame *)frame uid:(NSString *)peerId sourceType:(TKMediaType)type;
/**
 收到远端视频数据
 
 @param frame 视频数据
 @param peerId 用户ID
 @param type 采集源
 */
- (void)onRenderVideoFrame:(TKVideoFrame *)frame uid:(NSString *)peerId sourceType:(TKMediaType)type;

/**
 收到远端某一视频设备视频数据

 @param frame 视频数据
 @param peerId 用户ID
 @param deviceId 设备ID
 @param type 采集源
 */
- (void)onRenderVideoFrame:(TKVideoFrame *)frame uid:(NSString *)peerId deviceId:(NSString *)deviceId sourceType:(TKMediaType)type;
@end




//
//  TKPlaybackManager.h
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/10/19.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TKRoomManager.h"
#import "TKPlaybackDelegate.h"
NS_ASSUME_NONNULL_BEGIN
@interface TKPlaybackManager : NSObject
/**
 本地用户对象
 */
@property (nonatomic, strong, readonly) TKRoomUser *localUser;

/**
 单例
 @return TKPlaybackManager单例
 */
+ (instancetype)instance;

/**
 销毁TKPlaybackManager单例
 */
+ (void)destory;

/**
 设置打印SDK日志等级
 
 @param level 日志等级
 @param logPath 日志需要写入沙盒的路径; 默认路径为：沙盒Documents/TKLog。日志等级为TKLog_None时，不会写入沙盒。
 @param debug 是否时debug模式，debug模式：控制台打印，release模式：控制台不打印。
 */
+ (int)setLogLevel:(TKLogLevel)level logPath:(NSString * _Nullable)logPath debugToConsole:(BOOL)debug;

/**
 设置AppID
 
 @param appKey appID
 @param optional 房间扩展信息
 */
- (int)initWithAppKey:(NSString *)appKey optional:(NSDictionary * _Nullable)optional;

/**
 设置TKPlaybackManagerDelegate
 @param playbackDelegate 实现了TKPlaybackManagerDelegate回调接口的对象
 */
- (int)registerPlaybackManagerDelegate:(id<TKPlaybackManagerDelegate> _Nullable)playbackDelegate;

/**
 进入回放房间
 
 @param host 服务器地址
 @param port 服务器端口
 @param nickname 本地用户的昵称
 @param roomParams Dic格式，内含进入房间所需的基本参数，比如：NSDictionary类型，key值详情见 TKRoomDefines.h 相关定义
 @param userParams Dic格式，内含进入房间时用户的初始化的信息。比如 giftNumber（礼物数）
 */
- (int)joinPlaybackRoomWithHost:(NSString *)host
                           port:(int)port
                       nickName:(NSString *)nickname
                     roomParams:(NSDictionary *)roomParams
                     userParams:(NSDictionary * _Nullable)userParams;
/**
 获取房间属性
 */
- (NSDictionary *)getRoomProperty;

/**
 获取房间用户
 @param peerId 用户ID
 @return TKRoomUser
 */
- (TKRoomUser * _Nullable)getRoomUserWithUId:(NSString *)peerId;

/**
 对同一个用户，可以调用多次此函数。当传入的view和上次传入的一致时，函数不执行任何操作，直接返回成功；当传入的view和上次传入的不一致时，换用新的view播放该用户的视频
 须在主线程调用。
 @param peerID 用户Peerid
 @param renderType 视频渲染模式
 @param window 视频窗口
 @param completion 设置用于播放视频的view的block
 */
- (int)playVideo:(NSString *)peerID
      renderType:(TKRenderMode)renderType
          window:(UIView *)window
      completion:(completion_block _Nullable)completion;


/**
 播放用户 某一视频设备视频
 
 @param peerID 用户ID
 @param canvas 视频窗口
 @param deviceId 设备ID
 @param completion 设置用于播放视频的view的block
 */
- (int)playVideo:(NSString *)peerID
          canvas:(TKVideoCanvas *)canvas
        deviceId:(NSString *_Nullable)deviceId
      completion:(completion_block _Nullable)completion;

/**
 播放某个用户的音频
 
 @param peerID 用户Peerid
 @param completion 取消播放某个音频后的block
 */
- (int)playAudio:(NSString *)peerID completion:(completion_block _Nullable)completion;


/**
 停止播放某个用户的视频
 
 @param peerID 用户Peerid
 @param completion 取消播放某个视频后的block
 */
- (int)unPlayVideo:(NSString *)peerID completion:(completion_block _Nullable)completion;


/**
 停止播放用户 某一视频设备视频
 
 @param peerID 用户ID
 @param deviceId 设备ID
 @param completion 取消播放某个视频后的block
 */
- (int)unPlayVideo:(NSString *)peerID deviceId:(NSString *_Nullable)deviceId completion:(completion_block _Nullable)completion;
/**
 停止播放某个用户的音频
 
 @param peerID 用户Peerid
 @param completion 取消播放某个音频后的block
 */
- (int)unPlayAudio:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 播放媒体流
 须在主线程调用。
 @param peerId 用户id
 @param completion 播放后的回调
 */
- (int)playMediaFile:(NSString *)peerId
          renderType:(TKRenderMode)renderType
              window:(UIView *)window
          completion:(completion_block _Nullable)completion;
/**
 停止播放媒体流
 
 @param peerId 用户id
 @param completion 播放后的回调
 */
- (int)unPlayMediaFile:(NSString *)peerId completion:(completion_block _Nullable)completion;
/**
 暂停媒体流
 
 @param pause 暂停
 */
- (int)pauseMediaFile:(BOOL)pause;

/**
 设置进度
 
 @param pos 媒体流的位置
 */
- (int)seekMediaFile:(NSTimeInterval)pos;

/**
 播放桌面共享
 须在主线程调用。
 @param peerID 共享桌面的用户id
 @param completion 播放共享桌面后的回调
 */
- (int)playScreen:(NSString *)peerID
       renderType:(TKRenderMode)renderType
           window:(UIView *)window
       completion:(completion_block _Nullable)completion;

/**
 关闭共享桌面
 
 @param peerID 共享桌面的用户id
 @param completion 关闭共享桌面的回调
 */
- (int)unPlayScreen:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 播放电影
 须在主线程调用。
 @param peerID 共享电影文件的用户id
 @param completion 播放电影文件后的回调
 */
- (int)playFile:(NSString *)peerID
     renderType:(TKRenderMode)renderType
         window:(UIView *)window
     completion:(completion_block _Nullable)completion;

/**
 关闭电影
 
 @param peerID 共享电影文件的用户id
 @param completion 关闭共享电影文件的回调
 */
- (int)unPlayFile:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 回放拖动播放滑块
 
 @param positionTime 回放的时间
 */
- (int)seekPlayback:(NSTimeInterval)positionTime;

/**
 停止回放
 */
- (int)pausePlayback;

/**
 开始回放
 */
- (int)playback;

/**
 离开房间 异步退出房间
 
 @param completion 离开房间后的回调
 */
- (int)leaveRoom:(completion_block _Nullable)completion;

/**
 离开房间
 
 @param sync YES:同步退出 NO:退出
 @param completion 离开房间后的回调
 */
- (int)leaveRoom:(BOOL)sync completion:(completion_block _Nullable)completion;

@end
NS_ASSUME_NONNULL_END

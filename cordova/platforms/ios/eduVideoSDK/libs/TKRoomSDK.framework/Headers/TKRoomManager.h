//
//  TKRoomManager.h
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/3/20.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TKRoomDelegate.h"
#import "TKRoomDefines.h"

@class TKRoomUser;
NS_ASSUME_NONNULL_BEGIN

@interface TKRoomManager : NSObject
    ///-----------------------------------
    /// @name Properties
    ///-----------------------------------

/**
 本地用户对象
 */
@property (nonatomic, strong, readonly) TKRoomUser *localUser;

/**
 是否在后台
 */
@property (nonatomic, assign) BOOL inBackground;

    ///-----------------------------------
    /// @name Methods
    ///-----------------------------------

/**
 单例
 @return TKRoomManager单例
 */
+ (instancetype)instance;

/**
 销毁TKRoomManager单例
 */
+ (void)destory;

/**
 设置打印SDK日志等级
 
 @param level 日志等级
 @param logPath 日志需要写入沙盒的路径; 默认路径为：沙盒Libary/Caches/TKSDKLogs。
 @param debug 是否时debug模式，debug模式：控制台打印，release模式：控制台不打印。
 @return 0表示调用成功，非0表示调用失败
 */
+ (int)setLogLevel:(TKLogLevel)level logPath:(NSString * _Nullable)logPath debugToConsole:(BOOL)debug;

#pragma mark 初始化
/**
 设置AppID
 
 @param appKey appID
 @param optional 房间扩展信息  TKRoomDefines.h文件中定义了相关传值key
 @return 0表示调用成功，非0表示调用失败
 */
- (int)initWithAppKey:(NSString *)appKey optional:(NSDictionary * _Nullable)optional;


/**
 更改服务器
 
 @param serverName 更改服务器
 @return 0表示调用成功，非0表示调用失败
 */
- (int)changeCurrentServer:(NSString *)serverName;

/**
 获取服务器列表
 
 @return NSArray<NSDictionary *>* 实例对象
 */
- (NSArray * _Nullable)getServerList;

/**
 设置TKRoomManagerDelegate 代理
 @param roomDelegate 实现了TKRoomManagerDelegate回调接口的对象
 @return 0表示调用成功，非0表示调用失败
 */
- (int)registerRoomManagerDelegate:(id<TKRoomManagerDelegate> _Nullable)roomDelegate;

/**
 设置音视频数据 TKMediaFrameDelegate的代理
 
 @param mediaDelegate 实现了TKMediaFrameDelegate回调接口的对象
 @return 0表示调用成功，非0表示调用失败
 */
- (int)registerMediaDelegate:(id<TKMediaFrameDelegate> _Nullable)mediaDelegate;

#pragma mark 加入/离开房间

/**
 进入房间
 
 @param host 服务器地址 默认是https
 @param port 服务器端口 若在初始化“- (int)initWithAppKey:optional:”接口中设置TKRoomSettingOptionalSecureSocket为YES，表示支持https或者wss，所以此端口需要设置为：443(默认)；若初始化接口设置为NO或者不设置，端口为：80(默认).
 @param nickname 本地用户的昵称
 @param roomParams Dic格式，内含进入房间所需的基本参数，比如：NSDictionary类型，key值详情见 TKRoomDefines.h 相关定义
 @param userParams  Dic格式，内含进入房间时用户的初始化的信息。比如 giftNumber（礼物数）
 @return 0表示调用成功，非0表示调用失败
 */
- (int)joinRoomWithHost:(NSString *)host
                   port:(int)port
               nickName:(NSString *)nickname
             roomParams:(NSDictionary *)roomParams
             userParams:(NSDictionary * _Nullable)userParams;

/**
 加入即时房间
 
 @param roomId 房间ID
 @param nickname 昵称
 @param third_uid 用户ID
 @param userParams 用户信息，可自定义属性
 @return 0表示调用成功，非0表示调用失败
 */
- (int)joinRoomEx:(NSString *)roomId
         nickName:(NSString * _Nullable)nickname
        third_uid:(NSString * _Nullable)third_uid
       userParams:(NSDictionary * _Nullable)userParams;

/**
 离开房间 异步退出房间
 
 @param completion 离开房间后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)leaveRoom:(completion_block _Nullable)completion;

/**
 离开房间
 
 @param sync YES:同步退出 NO:退出
 @param completion 离开房间后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)leaveRoom:(BOOL)sync Completion:(completion_block _Nullable)completion;

#pragma mark 房间属性
/**
 获取房间属性
 */
- (NSDictionary *)getRoomProperty; 
/**
 获取房间用户
 @param peerId 用户ID
 @return TKRoomUser
 @return 0表示调用成功，非0表示调用失败
 */
- (TKRoomUser * _Nullable)getRoomUserWithUId:(NSString *)peerId;

#pragma mark change用户属性
/**
 修改某个用户的一个属性
 
 @param peerID 要修改的用户ID
 @param tellWhom 要将此修改通知给谁。NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param key 要修改的用户属性名字，可以是您自定义的名字
 @param value 要修改的用户属性，可以是Number、String、NSDictionary或NSArray
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)changeUserProperty:(NSString *)peerID
                 tellWhom:(NSString *)tellWhom
                      key:(NSString *)key
                    value:(NSObject *)value
               completion:(completion_block _Nullable)completion;

/**
 修改某个用户的一个属性
 
 @param peerID 要修改的用户ID
 @param tellWhom 要将此修改通知给谁。NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param data 更改的属性 NSDictionary
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)changeUserProperty:(NSString *)peerID
                 tellWhom:(NSString *)tellWhom
                     data:(NSDictionary *)data
               completion:(completion_block _Nullable)completion;


#pragma mark 发布音视频
/**
 发布自己的视频
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)publishVideo:(completion_block _Nullable)completion;
/**
 停止发布自己的视频
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPublishVideo:(completion_block _Nullable)completion;
/**
 发布自己的音频
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)publishAudio:(completion_block _Nullable)completion;

/**
 停止发布自己的视频
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPublishAudio:(completion_block _Nullable)completion;

#pragma mark 聊天消息
/**
 发送聊天信息功能函数
 @param message 发送的聊天消息文本 , 支持 NSString 、NSDictionary
 @param toID 发送给谁 , NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param extension 扩展的发送的聊天消息数据,例如：消息类型； 支持 NSString(JSON字符串string) 、NSDictionary
 @return 0表示调用成功，非0表示调用失败
 */
- (int)sendMessage:(NSObject *)message
              toID:(NSString *)toID
     extensionJson:(NSObject * _Nullable)extension;

#pragma mark 信令消息
/**
 发布自定义消息
 
 @param msgName 消息名字
 @param msgID ：消息id
 @param toID 要通知给哪些用户。NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param data 消息数据，可以是Number、String、NSDictionary或NSArray
 @param save ：是否保存，详见3.5：自定义信令
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)pubMsg:(NSString *)msgName
        msgID:(NSString *)msgID
         toID:(NSString *)toID
         data:(NSObject * _Nullable)data
         save:(BOOL)save
   completion:(completion_block _Nullable)completion;

    //expires ：这个消息，多长时间结束，以秒为单位，是相对时间。一般用于classbegin，给定一个相对时间
- (int)pubMsg:(NSString *)msgName
        msgID:(NSString *)msgID
         toID:(NSString *)toID
         data:(NSObject * _Nullable)data
         save:(BOOL)save
associatedMsgID:(NSString * _Nullable)associatedMsgID
associatedUserID:(NSString * _Nullable)associatedUserID
      expires:(NSTimeInterval)expires
   completion:(completion_block _Nullable)completion;

    //expendData:拓展数据，与msgName同级
- (int)pubMsg:(NSString *)msgName
        msgID:(NSString *)msgID
         toID:(NSString *)toID
         data:(NSObject * _Nullable)data
         save:(BOOL)save
extensionData:(NSDictionary * _Nullable)extensionData
   completion:(completion_block _Nullable)completion;

/**
 删除自定义消息
 @param msgName 消息名字
 @param msgID ：消息id
 @param toID 要通知给哪些用户。NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param data 消息数据，可以是Number、String、NSDictionary或NSArray
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)delMsg:(NSString *)msgName
        msgID:(NSString *)msgID
         toID:(NSString *)toID
         data:(NSObject * _Nullable)data
   completion:(completion_block _Nullable)completion;

#pragma mark 播放音视频
/**
 该方法设置本地视频镜像
 
 @param mode 镜像模式
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setLocalVideoMirrorMode:(TKVideoMirrorMode)mode;

/**
 对同一个用户，可以调用多次此函数。当传入的view和上次传入的一致时，函数不执行任何操作，直接返回成功；当传入的view和上次传入的不一致时，换用新的view播放该用户的视频
 须在主线程调用。
 @param peerID 用户Peerid
 @param renderType 视频渲染模式
 @param window 视频窗口
 @param completion 设置用于播放视频的view的block
 @return 0表示调用成功，非0表示调用失败
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
 @return 0表示调用成功，非0表示调用失败
 */
- (int)playVideo:(NSString *)peerID
          canvas:(TKVideoCanvas *)canvas
        deviceId:(NSString *_Nullable)deviceId
      completion:(completion_block _Nullable)completion;

/**
 播放某个用户的音频
 
 @param peerID 用户Peerid
 @param completion 取消播放某个音频后的block
 @return 0表示调用成功，非0表示调用失败
 */
- (int)playAudio:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 停止播放某个用户的视频
 
 @param peerID 用户Peerid
 @param completion 取消播放某个视频后的block
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPlayVideo:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 停止播放用户 某一视频设备视频

 @param peerID 用户ID
 @param deviceId 设备ID
 @param completion 取消播放某个视频后的block
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPlayVideo:(NSString *)peerID deviceId:(NSString *_Nullable)deviceId completion:(completion_block _Nullable)completion;
/**
 停止播放某个用户的音频
 
 @param peerID 用户Peerid
 @param completion 取消播放某个音频后的block
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPlayAudio:(NSString *)peerID completion:(completion_block _Nullable)completion;

#pragma mark - 是否禁用音视频设备
/**
 设置启用/禁用摄像头
 
 @param enable ：true：启用摄像头；false：禁用摄像头
 @return 0表示调用成功，非0表示调用失败
 */
- (int)enableLocalVideo:(BOOL)enable;


/**
 自己音频的开启关闭
 
 @param enable YES:开启 NO:关闭
 @return 0表示调用成功，非0表示调用失败
 */
- (int)enableLocalAudio:(BOOL)enable;

#pragma mark 发布流媒体
/**
 发布流媒体
 
 @param mediaPath 文件的url
 @param toID 发布媒体流给谁 NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param attributes 参数
 @param completion 发布媒体流后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)startShareMediaFile:(NSString *)mediaPath
                   isVideo:(BOOL)isVideo
                      toID:(NSString *)toID
                attributes:(NSDictionary *)attributes
                     block:(completion_block _Nullable)completion;

/**
 取消媒体流
 
 @param completion  取消媒体流后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)stopShareMediaFile:(completion_block _Nullable)completion;

#pragma mark 播放流媒体
/**
 播放媒体流
 须在主线程调用。
 @param peerId 用户id
 @param completion 播放后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)playMediaFile:(NSString *)peerId
          renderType:(TKRenderMode)renderType
              window:(UIView *)window
          completion:(completion_block _Nullable)completion;
/**
 停止播放媒体流
 
 @param peerId 用户id
 @param completion 播放后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPlayMediaFile:(NSString *)peerId completion:(completion_block _Nullable)completion;
/**
 暂停媒体流
 
 @param pause 暂停
 @return 0表示调用成功，非0表示调用失败
 */
- (int)pauseMediaFile:(BOOL)pause;

/**
 设置进度
 
 @param pos 媒体流的位置
 @return 0表示调用成功，非0表示调用失败
 */
- (int)seekMediaFile:(NSTimeInterval)pos;

/**
 设置用户音量
 
 @param volume 音量 0.0 - 1.0
 @param peerId 用户ID
 @param type 视频类型 （包括摄像头采集的视频、共享的多媒体视频）
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setRemoteAudioVolume:(CGFloat)volume peerId:(NSString *)peerId type:(TKMediaType)type;

/**
 播放桌面共享
 须在主线程调用。
 @param peerID 共享桌面的用户id
 @param completion 播放共享桌面后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)playScreen:(NSString *)peerID
       renderType:(TKRenderMode)renderType
           window:(UIView *)window
       completion:(completion_block _Nullable)completion;

/**
 关闭共享桌面
 
 @param peerID 共享桌面的用户id
 @param completion 关闭共享桌面的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPlayScreen:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 播放电影
 须在主线程调用。
 @param peerID 共享电影文件的用户id
 @param completion 播放电影文件后的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)playFile:(NSString *)peerID
     renderType:(TKRenderMode)renderType
         window:(UIView *)window
     completion:(completion_block _Nullable)completion;

/**
 关闭电影
 
 @param peerID 共享电影文件的用户id
 @param completion 关闭共享电影文件的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)unPlayFile:(NSString *)peerID completion:(completion_block _Nullable)completion;

#pragma mark 服务器录制
/**
 开始服务器录制
 
 @param recordType 录制类型
 @param convert 录制件数据格式，只有在recordtype = 0与1的情况下起作用。
 0: 表示不转换(mkv格式）
 1：表示webm(recordtype其他值时，固定状态)
 2：表示 mp4
 @param layout 只有在recordtype = 3的情况下起作用。 0：横屏，1：竖屏
 @param expiresabs expiresabs 录制时长
 @param expires expires 结束录制时的时间戳
 @return 0表示调用成功，非0表示调用失败
 */
- (int)startServerRecord:(TKRecordType)recordType convert:(NSInteger)convert layout:(NSInteger)layout expiresabs:(NSInteger)expiresabs expires:(NSInteger)expires;

/**
 停止服务器录制
 @return 0表示调用成功，非0表示调用失败
 */
- (int)stopServerRecord;

/**
 开始本地录制音频
 
 @param sandboxPath 有效的沙盒文件路径，如xxx/Library/Caches/audioRecord.mp3。
 注：
 1、保存音频文件为MP3格式；
 2、如果两次传入的路径相同，录制数据会覆盖；
 3、文件路径必须是有效路径，否则录制失败。例如：路径不存在或者不是文件路径（而是文件夹路径），则录制失败。
 @return 0 设置成功, 非0 失败
 */
- (int)startAudioRecord:(NSString *)sandboxPath;

/**
 暂停录制音频

 @param pause 是否暂停
 @return 0 设置成功, 非0 失败
 */
- (int)pauseAudioRecord:(BOOL)pause;

/**
 停止录制音频
 
 @return 0 设置成功, 非0 失败
 */
- (int)stopAudioRecord;


#pragma mark 房间功能
/**
 设置视频分辨率
 
 @param profile TKVideoProfile实例对象
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setVideoProfile:(TKVideoProfile *)profile;

/**
 切换纯音频教室
 
 @param isSwitch yes：纯音频教室。no：音视频教室
 @return 0表示调用成功，非0表示调用失败
 */
- (int)switchOnlyAudioRoom:(BOOL)isSwitch;

/**
 将一个用户踢出房间
 
 @param peerID 用户id
 @param reason 原因
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)evictUser:(NSString *)peerID evictReason:(NSNumber *)reason completion:(completion_block _Nullable)completion;

/**
 将一个用户踢出房间
 
 @param peerID 该用户的id
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)evictUser:(NSString *)peerID completion:(completion_block _Nullable)completion;

/**
 切换本地摄像头
 
 @param isFront  true：使用前置摄像头；false：使用后置摄像头
 @return 0表示调用成功，非0表示调用失败
 */
- (int)selectCameraPosition:(BOOL)isFront;

/**
 是否外放
 
 @param use YES:外放 NO:关闭
 @return 0表示调用成功，非0表示调用失败
 */
- (int)useLoudSpeaker:(BOOL)use;

/**
 设置视频方向
 
 @param orientation 设备取向
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setVideoOrientation:(UIDeviceOrientation)orientation;

/**
 开启/关闭 多码流

 @param enable 开启/关闭
 @return 0表示调用成功，非0表示调用失败
 */
- (int)enableDualStream:(BOOL)enable;

/**
 设置小流分辨率

 @param profile TKVideoProfile实例对象
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setSmallStreamParameter:(TKVideoProfile *)profile;

/**
 设置远端视频流默认类型，若不设置默认为TKVideoStream_Big

 @param streamType 视频流类型
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setRemoteDefaultVideoStreamType:(TKVideoStreamType)streamType;

/**
 设置某一用户（除自己之外）的某一视频设备的视频流类型

 @param streamType 视频流类型
 @param peerID 用户ID
 @param deviceID 视频设备ID
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setRemoteVideoStreamType:(TKVideoStreamType)streamType peerId:(NSString *)peerID deviceId:(NSString * _Nullable)deviceID;

#pragma mark 播放音频文件
/**
 开始播放
 @param filePath 本地文件路径 支持mp3、wav
 @param loop     是否循环
 @param progress 播放进度回调, audioID：播放音频标识, current：当前播放时间（单位：毫秒）, total：音频总时长（单位：毫秒）
 @return 返回播放音频标识, 若返回-1:表示播放失败.
 */

- (int)startPlayAudioFile:(NSString *)filePath loop:(BOOL)loop progress:(progress_block _Nullable)progress;

/**
 停止播放
 @param audioId  playAudioFile接口返回的标识, 此参数为-1时，表示停止所有正在播放的音频
 @return 0 设置成功, -1 失败
 */

- (int)stopPlayAudioFile:(int)audioId;

/**
 暂停播放
 @param audioId  playAudioFile接口返回的标识
 @return 0 设置成功, -1 失败
 */

- (int)pauseAudioFile:(int)audioId;

/**
 恢复播放
 @param audioId  playAudioFile接口返回的标识
 @return 0 设置成功, -1 失败
 */

- (int)resumeAudioFile:(int)audioId;

/**
 设置播放音量
 @param volume   播放时混音音量 取值范围:0.0~1.0  默认1.0
 @param audioId  startPlayAudioFile接口返回的标识
 @return 0 设置成功, -1 失败
 */

- (BOOL)setAudioFileVolume:(CGFloat)volume soundId:(int)audioId;

#pragma mark 播放媒体音视频文件

/**
 开始播放媒体音视频

 @param filePath 文件路径
 @param window 视频view
 @param loop 是否循环播放
 @param progress 播放进度
 @return 返回一个播放ID, 如果播放失败返回-1.
 */
- (int)startPlayMediaFile:(NSString *)filePath window:(UIView * _Nullable)window loop:(BOOL)loop progress:(progress_block _Nullable)progress;

/**
 停止播放媒体音视频

 @param playID 播放ID startPlayMediaFile接口返回的播放标识ID, 此参数为-1时，表示停止所有正在播放文件
 @return 0 设置成功, -1 失败。
 */
- (int)stopPlayMediaFile:(int)playID;

/**
 暂停播放媒体音视频

 @param playID 播放ID
 @return 0 设置成功, -1 失败
 */
- (int)pausePlayMedia:(int)playID;
/**
 继续播放媒体音视频
 
 @param playID 播放ID
 @return 0 设置成功, -1 失败
 */
- (int)resumePlayMedia:(int)playID;
/**
 seek播放媒体音视频
 
 @param playID 播放ID
 @param pos seek时间位置(0.0 - 1.0)
 @return 0 设置成功, -1 失败
 */
- (int)seekPlayMedia:(int)playID pos:(double)pos;
/**
 设置播放媒体音视频的音量
 @param playID 播放ID
 @param volume 音量大小（0.0 - 1.0）
 @return 0 设置成功, -1 失败
 */
- (int)setPlayMedia:(int)playID volume:(CGFloat)volume;

#pragma mark 网络测速
/**
 开始测速
 */
- (void)startNetworkTest;

/**
 停止测速
 */
- (void)stopNetworkTest;

#pragma mark 大规模房间相关接口
    //************************************************************************************************//
    //************************************* 大规模房间相关接口 ******************************************//
    //************************************************************************************************//
/**
 大规模房间时获取指定用户ID的用户
 在非大规模时使用- (TKRoomUser *)getRoomUserWithUId: 获取房间用户
 @param peerID 指定的用户ID
 @param callback 回调Block
 @return 0表示调用成功，非0表示调用失败
 */
- (int)getRoomUserWithPeerId:(NSString *)peerID callback:(void (^)(TKRoomUser *_Nullable user, NSError *_Nullable error))callback;

/**
 大规模教室时获取房间人数（可根据用户角色获取人数）
 
 @param role 用户角色
 @param callback 人数的callBack
 @return 0表示调用成功，非0表示调用失败
 */
- (int)getRoomUserNumberWithRole:(NSArray * _Nullable)role
                          search:(NSString * _Nullable)search
                        callback:(void (^)(NSInteger num, NSError *error))callback;

/**
 大规模教室时获取房间用户信息（用户列表）
 
 @param role 用户角色
 @param start 起始位置
 @param max 需要获取的人数
 @param callback 获取到的用户信息callback
 @return 0表示调用成功，非0表示调用失败
 */
- (int)getRoomUsersWithRole:(NSArray * _Nullable)role
                 startIndex:(NSInteger)start
                  maxNumber:(NSInteger)max
                     search:(NSString * _Nullable)search
                      order:(NSDictionary * _Nullable)order
                   callback:(void (^)(NSArray <TKRoomUser *>* _Nonnull users , NSError *error) )callback;

/**
 批量改变指定了用户ID的用户属性（适用于高并发房间）
 
 @param peerIDs 指定了用户ID的用户ID数组
 @param tellWhom 要将此修改通知给谁。NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param properties 要修改的属性
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)batchChangeUserPropertyByIds:(NSArray <NSString *>*)peerIDs
                           tellWhom:(NSString *)tellWhom
                           property:(NSDictionary *)properties
                         completion:(completion_block _Nullable)completion;

/**
 改变指定了角色的用户属性（适用于高并发房间）
 
 @param roles 指定的用户角色的数组
 @param tellWhom 要将此修改通知给谁。NSString类型，详情见 TKRoomDefines.h 相关定义. 可以是某一用户ID，表示此信令只发送给该用户
 @param properties 要修改的属性
 @param completion 完成的回调
 @return 0表示调用成功，非0表示调用失败
 */
- (int)changeUserPropertyByRole:(NSArray *)roles
                       tellWhom:(NSString *)tellWhom
                       property:(NSDictionary *)properties
                     completion:(completion_block _Nullable)completion;


#pragma mark deprecated
#warning "deprecated"
/**
 设置发布音视频 属性
 
 @param attributes 属性
 @return 0表示调用成功，非0表示调用失败
 */
- (int)setAttributes:(NSDictionary *)attributes TK_Deprecated("Will deprecated!!!");

/**
 录制用户的视频流
 
 @param peerId 用户id
 @param convert 0 不转换, 1 webm, 2 mp4
 @param completion 回调block，第一个参数为0时，表示成功，非0表示失败；第二个参数为视频路径。
 @return 0表示调用成功，非0表示调用失败
 */
- (int)startRecordUser:(NSString *)peerId
               convert:(NSInteger)convert
            completion:(void (^)(NSInteger ret, NSString *path))completion TK_Deprecated("Will deprecated!!! use - (int)startServerRecord:convert:layout:expiresabs:expires: replaced");

/**
 结束用户的视频流录制
 
 @param peerId 用户id
 @param completion 回调block，参数为0，表示成功；非0表示失败。
 @return 0表示调用成功，非0表示调用失败
 */
- (int)stopRecordUser:(NSString *)peerId completion:(void (^)(NSInteger, NSString *path))completion TK_Deprecated("Will deprecated!!! use - (int)stopServerRecord replaced");

/**
 当前本地摄像头是否被启用
 
 @return true：摄像头可用；false：摄像头被禁用
 */
- (BOOL)isVideoEnabled TK_Deprecated("Will deprecated!!!");
/**
 当前本地音频设备是否被启用
 
 @return true：可用；false：被禁用
 */
- (BOOL)isAudioEnabled TK_Deprecated("Will deprecated!!!");


@end
NS_ASSUME_NONNULL_END


//
//  TKEduSessionHandle.h
//  EduClassPad
//
//  Created by ifeng on 2017/5/10.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TKEduClassRoom.h"
#import "TKMediaMarkView.h"


NS_ASSUME_NONNULL_BEGIN
@class TKChatMessageModel,TKMediaDocModel,TKDocmentDocModel,RoomUser,RoomManager,TKDocumentListView,TKProgressHUD;

@protocol TKEduSessionClassRoomDelegate<NSObject>
- (void)sessionClassRoomDidOccuredError:(NSError *_Nullable)error;
- (void)sessionRoomManagerDidOccuredWaring:(TKRoomWarningCode)code;
@end

#pragma mark 1 TKEduSessionDelegate

@protocol TKEduSessionDelegate <NSObject>
@optional//自己进入课堂
- (void)sessionManagerRoomJoined;
//自己离开课堂
- (void)sessionManagerRoomLeft ;
//自己被踢
-(void) sessionManagerSelfEvicted:(NSDictionary *)reason;
//观看/取消视频
- (void)sessionManagerVideoStateWithUserID:(NSString *)peerID publishState:(TKMediaState)state;
//播放/取消音频
- (void)sessionManagerAudioStateWithUserID:(NSString *)peerID publishState:(TKMediaState)state;
//音量变化
- (void)sessionManagerOnAudioVolumeWithPeerID:(NSString *)peeID volume:(int)volume;
// 音视频状态
- (void)sessionManagerOnAVStateWithPeerID:(NSString *)peerID state:(id)state;
//用户进入
- (void)sessionManagerUserJoined:(TKRoomUser *)user InList:(BOOL)inList ;
//用户离开
- (void)sessionManagerUserLeft:(NSString *)peerID;
//用户信息变化 sGiftNumber sCandraw sRaisehand sPublishstate
- (void)sessionManagerUserChanged:(TKRoomUser *)user Properties:(NSDictionary*)properties fromId:(NSString *)fromId;
//聊天信息
- (void)sessionManagerMessageReceived:(NSString *)message
                               fromID:(NSString *)peerID
                            extension:(NSDictionary *)extension;
//回放的聊天信息
//- (void)sessionManagerPlaybackMessageReceived:(NSString *)message ofUser:(TKRoomUser *)user ts:(NSTimeInterval)ts;

- (void)sessionManagerRoomManagerPlaybackMessageReceived:(NSString *)message
                                                  fromID:(NSString *)peerID
                                                      ts:(NSTimeInterval)ts
                                               extension:(NSDictionary *)extension;
//进入会议失败
- (void)sessionManagerDidFailWithError:(NSError *)error;
- (void)sessionManagerOnConnectionLost;
- (void)sessionManagerDidOccuredWaring:(TKRoomWarningCode)code;
//白板等相关信令
- (void)sessionManagerOnRemoteMsg:(BOOL)add ID:(NSString*)msgID Name:(NSString*)msgName TS:(unsigned long)ts Data:(NSObject*)data InList:(BOOL)inlist;

//获取礼物数
- (void)sessionManagerGetGiftNumber:(void(^ _Nullable)())completion;
// 获取用户(普通\高并发) 通用
- (void)sessionHandleGetUserWithPeerID:(NSString *)peerID complete: (void(^)(TKRoomUser *))complete;
#pragma mark media

/**
 用户媒体流发布状态 变化回调
 @param peerId 用户id
 @param state 0:取消  非0：发布
 @param message 扩展消息
 */
- (void)sessionManagerOnShareMediaState:(NSString *)peerId
                                  state:(TKMediaState)state
                       extensionMessage:(NSDictionary *)message;

/**
 更新媒体流的信息回调
 @param duration 媒体流当前播放的时间点
 @param pos 媒体流当前的进度
 @param isPlay 播放（YES）暂停（NO）
 */
- (void)sessionManagerUpdateMediaStream:(NSTimeInterval)duration
                                    pos:(NSTimeInterval)pos
                                 isPlay:(BOOL)isPlay;

- (void)sessionManagerMediaLoaded;

#pragma mark Screen
- (void)sessionManagerOnShareScreenState:(NSString *)peerId state:(TKMediaState)state ;

#pragma mark file
- (void)sessionManagerOnShareFileState:(NSString *)peerId state:(TKMediaState)state extensionMessage:(NSDictionary *)message;

#pragma mark 回放
- (void)sessionManagerReceivePlaybackDuration:(NSTimeInterval)duration;
- (void)sessionManagerPlaybackUpdateTime:(NSTimeInterval)time;
- (void)sessionManagerPlaybackClearAll;
- (void)sessionManagerPlaybackEnd;

#pragma mark 设备检测
- (void)noCamera;
- (void)noMicrophone;
- (void)noCameraAndNoMicrophone;

#pragma mark 首次发布或订阅失败3次
- (void)networkTrouble;
- (void)networkChanged;
@end

@protocol TKEduBoardDelegate <NSObject>
@optional

- (void)boardOnViewStateUpdate:(NSDictionary *_Nullable)message;

- (void)boardOnFullScreen:(BOOL)isFull;

- (void)boardVideoFlagExit;
@end

#pragma mark - TKEduSessionHandle

@interface TKEduSessionHandle : NSObject

@property (nonatomic, assign) BOOL bigRoom;//大规模教室
@property (strong, nonatomic, nullable) NSMutableArray *cacheMsgPool;//缓存数据
@property (nonatomic, weak) id<TKEduRoomDelegate>    iRoomDelegate;
@property (nonatomic, weak) id<TKEduSessionDelegate> iSessionDelegate;
@property (nonatomic, weak) id<TKEduBoardDelegate>   iWhiteBoardDelegate;
@property (nonatomic, weak) id<TKEduSessionClassRoomDelegate> iClassRoomDelegate;

@property (nonatomic, strong) TKRoomManager *roomMgr;
@property (nonatomic, strong) TKPlaybackManager *playbackMgr;

@property (nonatomic, strong) UIView *whiteboardView;//记录白板
@property (nonatomic, assign) CGFloat bottomHeight;//记录bottom的高度

@property (nonatomic, strong, readonly) TKRoomUser *localUser;


@property (nonatomic, copy) NSDictionary *iParamDic;
@property (nonatomic,strong) NSMutableDictionary *iPublishDic;// 发布<key peerID, value roomUser>的用户
@property (nonatomic,strong) NSMutableArray *iUserList; // 房间用户

@property (nonatomic, assign) NSInteger onPlatformNum;// 当前在台上人数
@property (nonatomic, assign) NSInteger onPlatformClickTimes;// 上下台按钮点击次数

#pragma mark 自定义
@property (nonatomic, strong) TKRoomUser *iTeacherUser;
@property (nonatomic, assign) BOOL isClassBegin;// 是否上课
@property (nonatomic, assign) BOOL isOnlyAudioRoom;//音频教室
@property (nonatomic, assign) BOOL isAllMuteAudio;//全体静音
@property (nonatomic, assign) BOOL isunMuteAudio;//全体发言状态
@property (nonatomic, assign) BOOL isAllShutUp;// 全体禁言
@property (nonatomic, assign) BOOL iIsCanOffertoDraw;//yes 可以 no 不可以
@property (nonatomic, assign) BOOL isHeadphones;//是否是耳机
@property (nonatomic, assign) BOOL iHasPublishStd;//是否有发布的学生
@property (nonatomic, assign) BOOL iStdOutBottom;//是否有拖出去的视频
@property (nonatomic, assign) BOOL iIsFullState;//是否全屏状态
@property (nonatomic, assign) BOOL isPicInPic;  //是否有画中画(右下角小视频)
@property (nonatomic, assign) BOOL iIsSplitScreen;//是否分屏状态
@property (nonatomic, strong) NSNumber * videoRatio;//mp4视频比例
#pragma mark 白板
@property (nonatomic, strong) TKWhiteBoardManager *whiteBoardManager;// 白板管理对象

@property (nonatomic,strong) TKMediaDocModel    *iCurrentMediaDocModel;
@property (nonatomic,strong) TKMediaDocModel    *iPreMediaDocModel;
@property (nonatomic,strong) TKDocmentDocModel  *iCurrentDocmentModel;
@property(nonatomic,strong)  UIView *iDocumentListView;
@property(nonatomic,strong)  UIView *iMediaListView;
@property (nonatomic,strong) NSMutableArray     *iDocmentMutableArray;
@property (nonatomic,strong) NSMutableDictionary*iDocmentMutableDic;
@property (nonatomic,strong) NSMutableArray     *iMediaMutableArray;
@property (nonatomic,strong) NSMutableDictionary*iMediaMutableDic;

@property (nonatomic,assign)BOOL iIsPlaying;//是否播放中
@property (nonatomic,assign)BOOL isPlayMedia;//是否有音频
@property (nonatomic, assign) CGFloat iVolume;//音量 默认最大，耳机一半
@property (nonatomic,assign)BOOL isLocal;
@property (nonatomic,assign)BOOL isPlayback;  // 是否是回放
@property (nonatomic,assign)BOOL iIsJoined;//是否加入了房间
@property (nonatomic, assign) BOOL isSendLogMessage;//2017-11-10是否打印h5日志

@property (nonatomic, assign)TKRoomLayout roomLayout;// 当前布局样式
@property (nonatomic, assign)TKUpdateImageUseType updateImageUseType;
//smallview的菜单 是否正在消失
@property (nonatomic,assign) BOOL dismissing;


#pragma mark - 配置项
@property (assign,nonatomic)BOOL iIsCanDraw;

//未读聊天消息
@property (strong,nonatomic)NSMutableArray * _Nullable unReadMessagesArray;

@property (assign, nonatomic) BOOL UIDidAppear;
@property (nonatomic, strong) TKMediaMarkView *mediaMarkView;

+(instancetype _Nullable )shareInstance;

+(void)destroy;

//为了防止,还没进入教室就建立音视频链接 所以初始化需要在登陆教室的时候进行
//- (void)initializeSDK;

- (void)configureSession:(NSDictionary*)paramDic
       aClassRoomDelgate:(id<TKEduSessionClassRoomDelegate>)aClassRoomDelgate
           aRoomDelegate:(id<TKEduRoomDelegate>) aRoomDelegate;

- (void)setSessionDelegate:(id<TKEduSessionDelegate>) aSessionDelegate
            aBoardDelegate:(id<TKEduBoardDelegate>)aBoardDelegate;


// 回放进入接口
- (void)configurePlaybackSession:(NSDictionary*)paramDic
                   aRoomDelegate:(id<TKEduRoomDelegate>) aRoomDelegate
                aSessionDelegate:(id<TKEduSessionDelegate>) aSessionDelegate
                  aBoardDelegate:(id<TKEduBoardDelegate>)aBoardDelegate;


-(void)joinEduClassRoomWithParam:(NSDictionary *)aParamDic aProperties:(NSDictionary *)aProperties;

- (int)sessionHandleSetDeviceOrientation:(UIDeviceOrientation)orientation;

- (void)sessionHandleLeaveRoom:(void (^ _Nullable)(NSError *error))block;

-(void) sessionHandleLeaveRoom:(BOOL)force Completion:(void (^ _Nullable)(NSError *))block;

-(void)sessionHandleVideoProfile:(TKVideoProfile *)videoProfile;

- (int)sessionHandlePlayVideo:(NSString *)peerID
                   renderType:(TKRenderMode)renderType
                       window:(UIView *)window
                   completion:(completion_block)completion;

- (void)sessionHandleUnPlayVideo:(NSString*)peerID completion:(void (^ _Nullable)(NSError *error))block;

// 播放音频
- (int)sessionHandlePlayAudio:(NSString *)peerID
                   completion:(completion_block)completion;
// 停止音频
- (void)sessionHandleUnPlayAudio:(NSString*)peerID completion:(void (^ _Nullable)(NSError *error))block;

- (void)sessionHandleChangeUserProperty:(NSString*)peerID TellWhom:(NSString*)tellWhom Key:(NSString*)key Value:(NSObject*)value completion:(void (^ _Nullable)(NSError *error))block;
- (void)sessionHandleChangeUserProperty:(NSString*)peerID TellWhom:(NSString*)tellWhom data:(NSDictionary*)data completion:(void (^ _Nullable)(NSError *error))block;

- (void)sessionHandleChangeUserPropertyByRole:(NSArray *)roles
                                     tellWhom:(NSString *)tellWhom
                                     property:(NSDictionary *)properties
                                   completion:(completion_block _Nullable)completion;

/**
 进入前台
 */
- (void)sessionHandleApplicationWillEnterForeground;

- (void)sessionHandleChangeUserPublish:(NSString*)peerID Publish:(int)publish completion:(void (^ _Nullable)(NSError *error))block;

- (void)sessionHandleSendMessage:(NSObject *)message toID:(NSString *)toID extensionJson:(NSObject *)extension;

- (int)sessionHandleGetRoomUserNumberWithRole:(NSArray * _Nullable)role callback:(void (^ _Nullable)(NSInteger num, NSError *error))callback;


- (int)sessionHandleGetRoomUsersWithRole:(NSArray * _Nullable)role startIndex:(NSInteger)start maxNumber:(NSInteger)max callback:(void (^ _Nullable)(NSArray <TKRoomUser *>* _Nonnull users , NSError *error) )callback;

- (void)sessionHandlePubMsg:(NSString*)msgName ID:(NSString*)msgID To:(NSString*)toID Data:(NSObject*)data Save:(BOOL)save completion:(void (^ _Nullable)(NSError *error))block;
- (void)sessionHandlePubMsg:(NSString *)msgName ID:(NSString *)msgID To:(NSString *)toID Data:(NSObject *)data Save:(BOOL)save AssociatedMsgID:(NSString * _Nullable)associatedMsgID AssociatedUserID:(NSString * _Nullable)associatedUserID
                    expires:(NSTimeInterval)expires completion:(void (^ _Nullable)(NSError *))block;
- (void)sessionHandlePubMsg:(NSString *)msgName ID:(NSString *)msgID To:(NSString *)toID Data:(NSObject *)data Save:(BOOL)save extensionData:(NSDictionary *)extensionData
 completion:(void (^_Nullable)(NSError *))block;

- (void)sessionHandleDelMsg:(NSString*)msgName ID:(NSString*)msgID To:(NSString*)toID Data:(NSObject*)data completion:(void (^ _Nullable)(NSError *error))block;

- (void)sessionHandleEvictUser:(NSString *)peerID  evictReason:(NSNumber *)reason completion:(completion_block)completion;

-(void)publishVideoDragWithDic:(NSDictionary * )aVideoDic To:(NSString *)to;
//WebRTC & Media

- (void)sessionHandleSelectCameraPosition:(BOOL)isFront;

- (void)sessionHandleEnableAllAudio:(BOOL)enable;

- (void)sessionHandleEnableAudio:(BOOL)enable;

#pragma mark media
//发布媒体流
- (void)sessionHandlePublishMedia:(NSString *)fileurl hasVideo:(BOOL)hasVideo fileid:(NSString *)fileid  filename:(NSString *)filename toID:(NSString*)toID block:(void (^ _Nullable)(NSError *))block;
//关闭媒体流
- (void)sessionHandleUnpublishMedia:(void (^ _Nullable)(NSError *))block;
//播放媒体流
- (int)sessionHandlePlayMediaFile:(NSString *)peerId renderType:(TKRenderMode)renderType window:(UIView *)window completion:(completion_block)completion;
- (int)sessionHandleUnPlayMediaFile:(NSString *)peerId completion:(completion_block)completion;
//媒体流暂停
-(void)sessionHandleMediaPause:(BOOL)pause;
//媒体流进度
-(void)sessionHandleMediaSeektoPos:(NSTimeInterval)pos;
//媒体流音量
//-(void)sessionHandleMediaVolum:(CGFloat)volum;
- (int)sessionHandleSetRemoteAudioVolume:(CGFloat)volume peerId:(NSString *)peerId type:(TKMediaType)type;

// 纯音频教室 控制
- (void) sessionHandleChangeAudioOnlyRoom;

#pragma mark Screen

- (int)sessionHandlePlayScreen:(NSString *)peerID renderType:(TKRenderMode)renderType window:(UIView *)window completion:(completion_block)completion;
- (int)sessionHandleUnPlayScreen:(NSString *)peerID completion:(completion_block)completion;


#pragma mark file
- (int)sessionHandlePlayFile:(NSString *)peerID renderType:(TKRenderMode)renderType window:(UIView *)window completion:(completion_block)completion;
- (int)sessionHandleUnPlayFile:(NSString *)peerID completion:(completion_block)completion;
- (void)sessionHandleFullScreenSend:(BOOL)isFull;

#pragma 其他
-(void)clearAllClassData;
-(void)clearMessageList;
//message
- (NSArray *)messageList;
- (void)addOrReplaceMessage:(TKChatMessageModel *)aMessageModel;
- (void)addTranslationMessage:(TKChatMessageModel *)aMessageModel;
- (BOOL)judgmentOfTheSameMessage:(NSString *)message lastSendTime:(NSString *)time;

//user
- (NSArray *)userArray;
- (TKRoomUser *)getUserWithPeerId:(NSString *)peerId;
- (void)addUser:(TKRoomUser *)aRoomUser;
- (void)delUser:(TKRoomUser *)aRoomUser;
//user 老师和学生
- (NSArray *)userStdntAndTchrArray;
- (void)addUserStdntAndTchr:(TKRoomUser *)aRoomUser;
- (void)delUserStdntAndTchr:(TKRoomUser *)aRoomUser;
-(TKRoomUser *)userInUserList:(NSString*)peerId ;
//除了老师teacher和巡课Patrol
- (NSArray *)userListExpecPtrlAndTchr;
//特殊身份 助教等
-(void)addSecialUser:(TKRoomUser *)aRoomUser;
-(void)delSecialUser:(TKRoomUser *)aRoomUser;
-(NSDictionary *)secialUserDic;

//publish
-(void)addPublishUser:(TKRoomUser *)aRoomUser;
-(void)delePublishUser:(TKRoomUser *)aRoomUser;
-(NSDictionary *)publishUserDic;

#pragma mark 影音
-(void)deleteaMediaDocModel:(TKMediaDocModel*)aMediaDocModel To:(NSString *)to;
#pragma mark 文档
-(void)publishtDocMentDocModel:(TKDocmentDocModel*)tDocmentDocModel To:(NSString *)to aTellLocal:(BOOL)aTellLocal;
//删除文档
-(void)deleteDocMentDocModel:(TKDocmentDocModel*)aDocmentDocModel To:(NSString *)to;
//添加文档
-(void)addDocMentDocModel:(TKDocmentDocModel*)aDocmentDocModel To:(NSString *)to;
//老师点击下课时获取文档
- (TKDocmentDocModel *)getClassOverDocument;
#pragma mark 白板
//文档
-(NSDictionary *)docmentDic;
-(TKDocmentDocModel*)getDocmentFromFiledId:(NSString *)aFiledId;
//白板
- (TKDocmentDocModel *)whiteBoard;
//文档数组
- (NSArray <TKDocmentDocModel *> *)docmentArray;
//教室文档文件
- (NSArray <TKDocmentDocModel *> *)classDocmentArray;
//公用文档文件
- (NSArray <TKDocmentDocModel *> *)systemDocmentArray;

- (bool)addOrReplaceDocmentArray:(TKDocmentDocModel *)aDocmentDocModel;
- (void)delDocmentArray:(TKDocmentDocModel *)aDocmentDocModel;
- (void)fileListResetToDefault;         // 使文档列表中的文档复位
//音视频
-(NSDictionary *)meidaDic;
-(TKMediaDocModel*)getMediaFromFiledId:(NSString *)aFiledId;

//媒体文件
- (NSArray *)mediaArray;
//教室媒体文件
- (NSArray *)classMediaArray;
//公用媒体文件
- (NSArray *)systemMediaArray;

- (void)addOrReplaceMediaArray:(TKMediaDocModel *)aMediaDocModel;
- (void)delMediaArray:(TKMediaDocModel *)aMediaDocModel;


-(BOOL)isEqualFileId:(id)aModel  aSecondModel:(id)aSecondModel;

#pragma mark 设置HUD
-(void)configureHUD:(NSString *)aString  aIsShow:(BOOL)aIsShow;

#pragma mark 回放相关
- (void)playback;
- (void)pausePlayback;
- (void)seekPlayback:(NSTimeInterval)positionTime;

#pragma mark 设置权限
//画笔权限以及翻页权限初始化
- (void)configureDraw:(BOOL)isDraw isSend:(BOOL)isSend to:(NSString *)to peerID:(NSString*)peerID;

// 播放声音
- (void)startPlayAudioFile:(NSString *)filePath loop:(BOOL)loop;

#pragma mark - 白板层

- (void)wbSessionManagerPrePage;
- (void)wbSessionManagerNextPage;
- (void)wbSessionManagerTurnToPage:(int)pageNum;
- (void)wbSessionManagerEnlarge;
- (void)wbSessionManagerNarrow;
- (void)wbSessionManagerResetEnlarge;

- (void)wbSessionManagerBrushToolDidSelect:(TKBrushToolType)brushToolType;
- (void)wbSessionManagerDidSelectDrawType:(TKDrawType)type color:(NSString *)hexColor widthProgress:(float)progress;

NS_ASSUME_NONNULL_END
@end

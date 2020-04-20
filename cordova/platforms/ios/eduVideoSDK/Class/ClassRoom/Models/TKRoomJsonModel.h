//
//  TKRoomModel.h
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/10/19.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>
@class TKRoomConfiguration;
#
#pragma mark - TKRoomProperty 房间属性
#
@interface TKRoomJsonModel : NSObject

//企业用户类型：1.标准用户 2.网校用户 3.会议用户   -1 不做任何处理
@property (nonatomic, copy) NSString *companyidentify;

@property (nonatomic, copy) NSString *mobilelayout;
@property (nonatomic, copy) NSString *padlayout;
@property (nonatomic, copy) NSString *realpoint;
@property (nonatomic, copy) NSString *realsilentpoint;

/**
 自己的用户角色
 */
@property (nonatomic, assign) TKUserRoleType roomrole;

/**
 自己的ID
 */
@property (nonatomic, copy) NSString *thirdid;
    //room
/**
 公司ID
 */
@property (nonatomic, copy) NSString *companyid;

/**
 房间ID
 */
@property (nonatomic, copy) NSString *roomid;

/**
 房间类型 0:表示一对一教室  非0:表示一多教室
 */
@property (nonatomic, assign) TKUIRoomType roomtype;

/**
 房间名称
 */
@property (nonatomic, copy) NSString *roomname;

/**
 房间最大视频数
 */
@property (nonatomic, copy) NSString *maxvideo;
/**
 房间最大音频数
 */ 
@property (nonatomic, copy) NSString *maxaudio;

@property (nonatomic, strong) NSNumber *videotype;
@property (nonatomic, assign) TKRoomLayout roomlayout;
/**
 房间最大分辨率 视频宽
 */
@property (nonatomic, copy) NSString *videowidth;
/**
 房间最大分辨率 视频高
 */
@property (nonatomic, copy) NSString *videoheight;
/**
 房间最大分辨率 视频fps
 */
@property (nonatomic, copy) NSString *videoframerate;

@property (nonatomic, assign) NSTimeInterval begintime;
@property (nonatomic, assign) NSTimeInterval endtime;
@property (nonatomic, copy) NSString *newstarttime;
@property (nonatomic, copy) NSString *newendtime;

/**
 房间文档服务器地址
 */
@property (nonatomic, copy) NSString *ClassDocServerAddr;

/**
 房间文档服务器备份地址
 */
@property (nonatomic, strong) NSArray *ClassDocServerAddrBackup;
/**
 房间web服务器地址
 */
@property (nonatomic, copy) NSString *currentWebAddr;
/**
 当前连接的服务器
 */
@property (nonatomic, copy) NSString *currentServer;
/**
 白板底色
 */
@property (nonatomic, copy) NSString *whiteboardcolor;

/**
 房间配置项
 */
@property (nonatomic, copy) NSString *chairmancontrol;

/**
 自定义奖杯
 */
@property (nonatomic, strong) NSArray *trophy;

/**
 模板id
 */
@property (nonatomic, copy) NSString *tplId;

/**
 皮肤id
 */
@property (nonatomic, copy) NSString *skinId;

/**
 新版 皮肤 id 无企业0 默认purple 黑black
 */
@property (nonatomic, copy) NSString *colourid;

/**
 皮肤资源
 */
@property (nonatomic, copy) NSString *skinResource;

@property (copy, nonatomic) NSString *vcodec;


/**
 配置项
 */
@property (nonatomic, strong)TKRoomConfiguration *configuration;


/**
 是否是回放
 */
@property (nonatomic, assign)BOOL isPlayback;

- (instancetype)initWithDictionary: (NSDictionary *)dic isPlayback:(BOOL)isPlayback;


@end

#
#pragma mark - TKRoomConfiguration 房间设置的相关配置项
#
//配置项
@interface TKRoomConfiguration : NSObject

@property (nonatomic, strong) NSString * cString;
@property (nonatomic, assign) BOOL     isPlayBack;
/**
自动上课
 */
@property (nonatomic, assign) BOOL autoStartClassFlag;

/**
 课堂结束时自动退出房间
 */
@property (nonatomic, assign) BOOL autoQuitClassWhenClassOverFlag;

/**
 是否允许学生关闭音视频
 */
@property (nonatomic, assign) BOOL allowStudentCloseAV;

/**
 画笔权限
 */
@property (nonatomic, assign) BOOL canDrawFlag;

/**
 翻页权限
 */
@property (nonatomic, assign) BOOL canPageTurningFlag;

/**
 是否隐藏上下课按钮
 */
@property (nonatomic, assign) BOOL hideClassBeginEndButton;

/**
 助教是否可以上台
 */
@property (nonatomic, assign) BOOL assistantCanPublish;

/**
 上课前是否发布视频
 */
@property (nonatomic, assign) BOOL beforeClassPubVideoFlag;

/**
 答题结束后自动展示答案
 */
@property (nonatomic, assign) BOOL autoShowAnswerAfterAnswer;

/**
 下课后不允许离开课堂
 */
@property (nonatomic, assign) BOOL forbidLeaveClassFlag;

/**
 自动开启音视频
 */
@property (nonatomic, assign) BOOL autoOpenAudioAndVideoFlag;

/**
 视频标注
 */
@property (nonatomic, assign) BOOL videoWhiteboardFlag;

/**
 课件备注
 */
@property (nonatomic, assign) BOOL coursewareRemarkFlag;

/**
 MP4播放结束时是否自动关闭MP4播放的视频
 */
@property (nonatomic, assign) BOOL pauseWhenOver;

/**
 聊天是否允许发送图片
 */
@property (nonatomic, assign) BOOL isChatAllowSendImage;

/**
 文档分类
 */
@property (nonatomic, assign) BOOL documentCategoryFlag;

/**
 按下课时间结束课堂
 */
@property (nonatomic, assign) BOOL endClassTimeFlag;

/**
 分组
 */
@property (nonatomic, assign) BOOL groupFlag;

/**
 自定义白板底色
 */
@property (nonatomic, assign) BOOL whiteboardColorFlag;

/**
 自定义奖杯
 */
@property (nonatomic, assign) BOOL customTrophyFlag;

/**
 巡课身份隐藏下课按钮
 */
@property (assign, nonatomic) BOOL hideClassEndBtn;

/**
 切换纯音频教室
 */
@property (assign, nonatomic) BOOL canChangedToAudioOnly;

/**
 在白板中播放视频
 */
@property (nonatomic) BOOL coursewareOpenInWhiteboard;

/**
 课件全屏同步
 */
@property (assign, nonatomic) BOOL coursewareFullSynchronize;

/**
 在白板进行涂鸦的用户会在右下角显示用户昵称3秒
 3秒后昵称小时, 且自己无法看到自己的昵称显示,仅显示其他用户昵称
 */
@property (assign, nonatomic) BOOL isShowWriteUpTheName;

/**
 排序小视频
 */
@property (nonatomic, assign) BOOL sortSmallVideo;

/**
 学生端不显示网络状态
 */
@property (nonatomic, assign) BOOL unShowStudentNetState;

/**
 只看老师和自己的视频
 */
@property (nonatomic, assign) BOOL onlyMeAndTeacherVideo;

/**
 禁用翻页
 */
@property (nonatomic, assign) BOOL isHiddenPageFlip;

/**
 0:中英互译/1:中日互译
 */
@property (nonatomic, assign) BOOL isChineseJapaneseTranslation;

/**
 是否隐藏画笔工具形状
 */
@property (nonatomic, assign) BOOL shouldHideShapeOnDrawToolView;

/**
 是否隐藏画笔选择工具鼠标
 */
@property (nonatomic, assign) BOOL shouldHideMouseOnDrawToolView;

/**
 是否隐藏画笔调色盘字体字号选择
 */
@property (nonatomic, assign) BOOL shouldHideFontOnDrawSelectorView;

/**
 画笔穿透
 */
@property (nonatomic, assign) BOOL isPenCanPenetration;

/**
 隐藏踢人
 */
@property (nonatomic, assign) BOOL isHiddenKickOutStudentBtn;

/**
 课件预加载
 */
@property (nonatomic, assign) BOOL coursewarePreload;

/**
不提示助教进教室
*/
@property (nonatomic,assign) BOOL isPromptAssistantJoinRoom;


- (instancetype)initWithConfigurationString: (NSString *)configurationString isPlayback:(BOOL)isPlayback;
@end


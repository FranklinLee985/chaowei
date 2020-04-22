//
//  TKMacro.h
//  whiteBoardDemo
//
//  Created by ifeng on 2017/2/28.
//  Copyright © 2017年 beijing. All rights reserved.
//

#ifndef TKMacro_h
#define TKMacro_h
#ifdef DEBUG
#define TKLog(...) NSLog(__VA_ARGS__)
#else
#define TKLog(...) do { } while (0)
#endif  

#import <UIKit/UIKit.h>

static  NSString *const kAppKey   = @"";


#pragma mark - ssssssssss
// 内网
//static  NSString * sHost   = @"global.talk-cloud.neiwang";

// 公网
static  NSString * sHost   = @"global.talk-cloud.net";

// demo
//static  NSString * sHost   = @"demo.talk-cloud.net";

// global11
//static  NSString * sHost   = @"testing.talk-cloud.net";

//用户昵称\房间号\老师密码 (用于测试数据\ 注释后将读取输入的信息)
#if DEBUG

//#define Class_NickName         @"肉丸"
//#define SERVER_ClassID         @"985979563"
//#define SERVER_ClassPwd        @"1"
#else


#endif

// https
#define HTTPS_MACRO 1

#define sHttp 	 HTTPS_MACRO ? @"https" : @"http"
#define sPort 	 HTTPS_MACRO ? @"443"   : @"80"
#define isHTTPS  HTTPS_MACRO ? @"YES"	: @"NO"

//色值设置
#define UIColorRGB(rgb) ([[UIColor alloc] initWithRed:(((rgb >> 16) & 0xff) / 255.0f) green:(((rgb >> 8) & 0xff) / 255.0f) blue:(((rgb) & 0xff) / 							 255.0f) alpha:1.0f])
#define UIColorRGBA(rgb,a) ([[UIColor alloc] initWithRed:(((rgb >> 16) & 0xff) / 255.0f) green:(((rgb >> 8) & 0xff) / 255.0f) blue:(((rgb) & 0xff) / 					 255.0f) alpha:a])
#define RGBCOLOR(r,g,b) [UIColor colorWithRed:(r)/255.0f green:(g)/255.0f blue:(b)/255.0f alpha:1.0]
#define RGBACOLOR(r,g,b,a) [UIColor colorWithRed:(r)/255.0f green:(g)/255.0f blue:(b)/255.0f alpha:a]

// 屏幕 尺寸
#define ScreenH [UIScreen mainScreen].bounds.size.height
#define ScreenW [UIScreen mainScreen].bounds.size.width
// 导航栏高度
#define TKNavHeight (IS_IPHONE ? 45 : 60)
// 屏幕比例，相对pad 1024 * 768
#define Proportion (ScreenH/768.0)
#define TKMainWindow  [UIApplication sharedApplication].keyWindow
#define TKScreenScale [UIScreen mainScreen].scale

#define IS_IPHONE (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
#define IS_IPHONE_X ((ScreenW == 812.0f) ? YES : NO)
#define IS_PAD (UI_USER_INTERFACE_IDIOM()== UIUserInterfaceIdiomPad)

// 系统判定
#define iOS7 ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0)
#define iOS7Later ([UIDevice currentDevice].systemVersion.floatValue >= 7.0f)
#define iOS8Later ([UIDevice currentDevice].systemVersion.floatValue >= 8.0f)
#define iOS9Later ([UIDevice currentDevice].systemVersion.floatValue >= 9.0f)
#define iOS9_1Later ([UIDevice currentDevice].systemVersion.floatValue >= 9.1f)
#define iOS10_0Later ([UIDevice currentDevice].systemVersion.floatValue >= 10.0f)

// 引用
#define tk_weakify(var)   __weak __typeof(var) weakSelf = var
#define tk_strongify(var) __strong __typeof(var) strongSelf = var

// 字体
#define TKFont(s) [UIFont systemFontOfSize:s]
// >9.0
//[UIFont fontWithName:@"PingFang-SC-Light" size:s]

#define TITLE_FONT TKFont(16)
#define TEXT_FONT  TKFont(14)
#define IS_CH_SYMBOL(chr) ((int)(chr)>127)

// 最大小视频数量
#define sMaxVideo  7

#define TK_BUNDLE_NAME @ "TKResources.bundle"
#define TK_BUNDLE [NSBundle bundleWithPath: [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent: TK_BUNDLE_NAME]]
#define LOADIMAGE(name) [UIImage imageWithContentsOfFile:[[TK_BUNDLE resourcePath] stringByAppendingPathComponent:name]]
#define LOADWAV(name) [[TK_BUNDLE resourcePath] stringByAppendingPathComponent:name]

#define TKMTLocalized(s) [TK_BUNDLE localizedStringForKey:s value:@"" table:nil]

// 奖杯
#define TrophyImgPath(companyid, name) 	 [NSString stringWithFormat:@"Trophy/%@/%@/trophyimg",companyid,name]
#define TrophyGifPath(companyid, name) 	 [NSString stringWithFormat:@"Trophy/%@/%@/icon",companyid,name]
#define TrophyAudioPath(companyid, name) [NSString stringWithFormat:@"Trophy/%@/%@/trophyvoice.wav",companyid,name]


// 百度翻译
static NSString *const sAPP_ID_BaiDu = @"20180130000119815";
static NSString *const sSECURITY_KEY = @"MeLC5NI37txuT_wtTd0B";
static NSString *const sTRANS_API_HOST = @"http://api.fanyi.baidu.com/api/trans/vip/translate";

static  NSString *const sMobile           		    = @"mobile";//拍照上传入口
static  NSString *const sLowConsume                 = @"LowConsume";

// 信令
static  NSString *const sPubMsg                     = @"pubMsg";//发送信令
static  NSString *const sDelMsg                     = @"delMsg";//删除信令

static  NSString *const sClassBegin                 = @"ClassBegin";//上课
static  NSString *const sStreamFailure              = @"StreamFailure";
static  NSString *const sAllAll                     = @"__AllAll";
static  NSString *const sVideoDraghandle            = @"videoDraghandle";//视频拖拽
static  NSString *const sVideoSplitScreen           = @"VideoSplitScreen";//分屏
static  NSString *const sDoubleClickVideo           = @"doubleClickVideo";//双击视频
static  NSString *const sVideoZoom                  = @"VideoChangeSize";//视频缩放
static  NSString *const sChangeServerArea           = @"RemoteControl";// 助教协助切换服务器（课件服务器）
static  NSString *const sServerName                 = @"servername";//助教协助切换服务器（优选网络）
static  NSString *const sUpdateTime                 = @"UpdateTime";
static  NSString *const sMuteAudio                  = @"MuteAudio";// 全体静音
static  NSString *const sEveryoneBanChat            = @"EveryoneBanChat";//全体禁言
static  NSString *const sOnlyAudioRoom              = @"OnlyAudioRoom"; //音频教室
static  NSString *const sWBFullScreen               = @"FullScreen";// 全屏
static  NSString *const sBigRoom                    = @"BigRoom";// 大并发
static  NSString *const sTimer                      = @"timer";// 计时器
static  NSString *const sShowPageBeforeClass        = @"ShowPageBeforeClass";// 课前切换课件
static  NSString *const sSwitchLayout               = @"switchLayout";// 布局切换

// 白板信令
static  NSString *const sWBPageCount                = @"WBPageCount";//加页
static  NSString *const sShowPage                   = @"ShowPage";//显示文档
static  NSString *const sDocumentFilePage_ShowPage  = @"DocumentFilePage_ShowPage";//ShowPage ID
static  NSString *const sActionShow                 = @"show";
static  NSString *const sSharpsChange               = @"SharpsChange";//画笔
static  NSString *const sDocumentChange             = @"DocumentChange";//添加或删除文档
static  NSString *const sOnPageFinished             = @"onPageFinished";
static  NSString *const sChangeWebPageFullScreen    = @"changeWebPageFullScreen";//白板放大事件
static  NSString *const sOnJsPlay                   = @"onJsPlay";
static  NSString *const scloseDynamicPptWebPlay     = @"closeDynamicPptWebPlay";//closeNewPptVideo更改为closeDynamicPptWebPlay

// 用户属性
static  NSString *const sRaisehand           		= @"raisehand"; // 举手
static  NSString *const sPrimaryColor    	    	= @"primaryColor";//画笔颜色值
static  NSString *const sPublishstate   	    	= @"publishstate";//发布状态
static  NSString *const sGiftNumber                 = @"giftnumber";
static  NSString *const sGiftinfo                   = @"giftinfo";
static  NSString *const sDisablechat                = @"disablechat";// 禁言
static  NSString *const sCandraw                    = @"candraw";// 画笔
static  NSString *const sUdpState                   = @"udpstate";//UDP状态发生变化，1是畅通，2是防火墙导致不畅通
static  NSString *const sVolume                     = @"volume";
static  NSString *const sDisableVideo               = @"disablevideo";
static  NSString *const sDisableAudio               = @"disableaudio";
static  NSString *const sFromId                     = @"fromId";
static  NSString *const sUser                		= @"User";
static  NSString *const sIsInBackGround      		= @"isInBackGround";

// 工具箱 - 抢答器
static  NSString *const sQiangDaQi                  = @"qiangDaQi";
static  NSString *const sQiangDaQiMesg              = @"qiangDaQiMesg";
static  NSString *const sQiangDaZhe                 = @"QiangDaZhe";
static  NSString *const sResponderDrag              = @"ResponderDrag";
static  NSString *const sActionID                   = @"actionID";

// 自定义
static  NSString *const sNeedPictureInPictureSmall  = @"needPictureInPictureSmall";

// 通知对象
static  NSString *const sTellAll                    = @"__all";//所有人
static  NSString *const sTellNone                   = @"__none";
static  NSString *const sTellAllExpectSender        = @"__allExceptSender";//除自己以外的所有人
static  NSString *const sTellAllExpectAuditor       = @"__allExceptAuditor";//除旁听用户以外的所有人
static  NSString *const sSuperUsers                 = @"__allSuperUsers";

//小白板
static  NSString *const sAssociatedMsgID            = @"associatedMsgID";
static  NSString *const sName                       = @"name";
static  NSString *const s_Prepareing                = @"_prepareing";
static  NSString *const s_Dispenseed                = @"_dispenseed";
static  NSString *const s_Recycle                   = @"_recycle";
static  NSString *const s_AgainDispenseed           = @"_againDispenseed";

static  NSString *const sBlackBoardState            = @"blackBoardState";
static  NSString *const sCurrentTapKey              = @"currentTapKey";
static  NSString *const sBlackBoard_new             = @"BlackBoard_new";
static  NSString *const sUserHasNewBlackBoard       = @"UserHasNewBlackBoard";
static  NSString *const sWhiteboardID               = @"whiteboardID";

static  NSString *const sBlackBoardCommon           = @"blackBoardCommon";

// 播放mp3，mp4
static  NSString *const sVideo_MediaFilePage_ShowPage   = @"Video_MediaFilePage_ShowPage";
static  NSString *const sAudio_MediaFilePage_ShowPage   = @"Audio_MediaFilePage_ShowPage";

//白板类型
static  NSString *const sVideoWhiteboard                = @"VideoWhiteboard";

//拍摄照片、选择照片上传
static  NSString *const sTakePhotosUploadNotification   = @"sTakePhotosUploadNotification";
static  NSString *const sChoosePhotosUploadNotification = @"sChoosePhotosUploadNotification";

static NSString *const kTKMethodNameKey = @"TKCacheMsg_MethodName"; //缓存函数名
static NSString *const kTKParameterKey = @"TKCacheMsg_Parameter";  //缓存参数

// 皮肤
static NSString *const TKCartoonSkin = @"purple";// 默认紫色 皮肤
static NSString *const TKBlackSkin   = @"black"; // 黑皮
static NSString *const TKOrangeSkin   = @"tigerlily"; // 橙皮

// 被T时间
static NSString *const TKKickTime = @"TKKickTime";
static NSString *const TKKickRoom = @"TKKickRoom";

// 上下台时间间隔标识符
static NSString *const TKUnderPlatformTime = @"TKUnderPlatformTime";

#endif /* TKMacro_h */

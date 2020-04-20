//
//  TKEnumHeader.h
//  EduClass
//
//  Created by lyy on 2018/5/8.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#ifndef TKEnumHeader_h
#define TKEnumHeader_h

typedef NS_ENUM(NSInteger,EKickOutReason) {
    
    EKickOutReason_ChairmanKickout,            //老师踢出
    EKickOutReason_Repeat                    //重复登录
};

typedef NS_ENUM(NSInteger, TKPromptType) {
    TKPromptTypeStartReady1Minute,  //距离上课还有1分钟,White 249,249,249
    TKPromptTypeStartPass1Minute,   //超过上课时间,White 249,249,249,blue:78,100,196
    TKPromptTypeEndWill1Minute,         //距离下课还1分钟,Yellow 155,136 58
    TKPromptTypeEndPass,             //超时,Red 215 0 0
    TKPromptTypeEndPass5Minute,     //超时5分钟,Red
    TKPromptTypeEndPass3Minute     //超时3分钟,Red,
    
};

typedef NS_ENUM(NSInteger, TKChatMessageType) {
    
    TKChatMessageTypeTips,          /** 提示信息： */
    TKChatMessageTypeText,           /** 聊天文字消息 */
    TKChatMessageTypeOnlyImage,     /** 聊天图片消息 */
};

typedef NS_ENUM(NSInteger, TKChatRoleType) {
    
    TKChatRoleTypeTeacher,      /** 老师 */
    TKChatRoleTypeMe,           /** 我 */
    TKChatRoleTypeOtherUer,     /** 其他 */
};

//上传图片的用途
typedef NS_ENUM(NSInteger, TKUpdateImageUseType) {
    TKUpdateImageUseType_Document = 0,// 作为课件
    TKUpdateImageUseType_Message  = 1,// 聊天用图
};

typedef NS_ENUM(NSInteger, PublishState) {
    TKPublishStateNONE           = 0,            //没有
    TKPublishStateAUDIOONLY      = 1,            //只有音频
    TKPublishStateVIDEOONLY      = 2,            //只有视频
    TKPublishStateBOTH           = 3,            //都有
    TKPublishStateNONEONSTAGE    = 4,            //音视频都没有 但还在台上
    TKPublishStateLocalNONE      = 5             //本地显示流
};


typedef NS_ENUM(NSInteger, TKUIRoomType) {
    TKRoomTypeOneToOne   = 0,            //小班
    TKRoomTypeOneToMore  = 3,           //大班
};

typedef NS_ENUM(NSInteger, TKMediaProgressAction) {
    TKMediaProgressActionOtherNeedProgress     =-1,            //别人向我要进度
    TKMediaProgressActionPlayOrPause           =0,            //播放或暂停
    TKMediaProgressActionChangeProgress        =1            //进度改变
    
};
typedef NS_ENUM(NSInteger, TKPlayertype) {
    TKPlayertypeAudio,    // 播放音频
    TKPlayertypeVideo     // 播放视频
};
typedef NS_ENUM(NSInteger, TKFileListType) {
    TKFileListTypeAudioAndVideo,    //视频列表
    TKFileListTypeDocument,        // 文档列表
    TKFileListTypeUserList,        //用户列表
    TKVideoTypeUserList
};

typedef NS_ENUM(NSInteger, TKFileType) {//文件类型
    TKClassFileType,    //课堂文件
    TKSystemFileType    //公共文件
};

typedef NS_ENUM(NSInteger, TKDeviceType) {//设备类型
    AndroidPad,
    iPad,
    AndroidPhone,
    iPhone,
    WindowClient,
    WindowPC,
    MacClient,
    MacPC,
    AndroidTV
};

typedef NS_ENUM(NSInteger, TKSortFileType) {
    TKSortNone,//未排序
    TKSortDescending,//降序
    TKSortAscending,//升序
};
typedef NS_ENUM(NSInteger, TKPageShowType) {
    TKDocumentPageShow,//未排序
    TKUserListPageShow,//降序
};

typedef NS_ENUM(NSInteger, TKRoomLayout)
{
    CoursewareDown	= 1,            //视频置顶
    VideoDown		= 2,                //视频置底
    Encompassment,            //视频围绕
    Bilateral,                //多人模式
    MainPeople		= 6,                //主讲排列
    OnlyVideo		= 7,                //自由视频
    
    oneToOne				= 51,    //常规布局(一对一)
    oneToOneDoubleDivision	= 52,    //双师布局(一对一)
    oneToOneDoubleVideo		= 53     //视频布局(一对一)
};

//videoView的填充方式
typedef NS_ENUM(NSInteger, TKVideoViewMode) {
    TKVideoViewMode_Fill = 0, //不按比例 完全填充
    TKVideoViewMode_Fit , //按4:3比例缩放 居中
    TKVideoViewMode_Top , //按4:3比例缩放 顶部对齐
};

//maskView 中视频的三种布局
typedef NS_ENUM(NSInteger, TKMaskViewLayout) {
    TKMaskViewLayout_Normal = 0,//正常布局
    TKMaskViewLayout_More , //当人数较多时，videoView比较小，需要更改mask上面的布局
};

#endif /* TKEnumHeader_h */

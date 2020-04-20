//
//  TKEduClassRoomNetWorkManager.h
//  EduClassPad
//
//  Created by ifeng on 2017/5/10.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <Foundation/Foundation.h>
@class TKRoomUser;

//@import UIKit;

#import <UIKit/UIKit.h>

typedef int(^bCheckRoomdidComplete)( id _Nullable response ,NSString* _Nullable aPassWord);
typedef int(^bCheckRoomError)( NSError *_Nullable aError);
typedef void(^bGetGifInfoComplete)( id _Nullable response);
typedef int(^bGetGifInfoError)( NSError *_Nullable aError);
typedef int(^bTranslationComplete)( id _Nullable response ,NSString* _Nullable aTranslationString);
typedef int(^bComplete)( id _Nullable response);
typedef int(^bError)( id _Nullable response);
typedef void(^bSendGifInfoComplete)( id _Nullable response);
typedef int(^bSendGifInfoError)( NSError *_Nullable aError);
@protocol TKEduNetWorkDelegate <NSObject>

@optional
- (void)uploadProgress:(int)req totalBytesSent:(int64_t)totalBytesSent bytesTotal:(int64_t)bytesTotal;
- (void)uploadFileResponse:(id _Nullable )Response req:(int)req;
- (void)getMeetingFileResponse:(id _Nullable )Response;

@end

@interface TKEduNetManager : NSObject

+ (instancetype _Nonnull )initTKEduNetManagerWithDelegate:(id < TKEduNetWorkDelegate> _Nonnull)delegate;

//获取礼物数
+(void)getGiftinfo:(NSString *_Nonnull)aRoomId aParticipantId:(NSString *_Nonnull)aParticipantId aHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort aGetGifInfoComplete:(bGetGifInfoComplete _Nullable )aGetGifInfoComplete aGetGifInfoError:(bGetGifInfoError _Nullable)aGetGifInfoError;
//
+(void)translation:(NSString * _Nonnull )aTranslationString aTranslationComplete:(bTranslationComplete _Nonnull )aTranslationComplete;
+(void)delRoomFile:(NSString * _Nonnull )roomID docid:(NSString *_Nonnull)docid isMedia:(bool)isMedia    aHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort aDelComplete:(bComplete _Nonnull )aDelComplete aNetError:(bError _Nullable) aNetError;

+(void)sendGifForRoomUser:(NSArray *_Nonnull)aRoomUserArray  roomID:(NSString * _Nonnull )roomID   aMySelf:(TKRoomUser *_Nonnull)aMySelf aHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort aSendComplete:(bSendGifInfoComplete _Nonnull )aSendComplete aNetError:(bError _Nullable) aNetError;

#pragma mark 上传文档
+ (int)uploadWithaHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort  roomID:(NSString*_Nullable)roomID imageUse:(NSNumber *_Nullable)imageUse fileData:(NSData *_Nullable)fileData fileName:(NSString *_Nullable)fileName  fileType:(NSString *_Nullable)fileType userName:(NSString *_Nullable)userName userID:(NSString *_Nullable)userID delegate:(id _Nullable )delegate;

- (int)uploadWithaHost2:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort  roomID:(NSString*_Nullable)roomID fileData:(NSData *_Nullable)fileData fileName:(NSString *_Nullable)fileName  fileType:(NSString *_Nullable)fileType userName:(NSString *_Nullable)userName userID:(NSString *_Nullable)userID;

- (void)getmeetingfile:(int)meetingid requestURL:(NSString *_Nullable)requestURL;
#pragma mark 下课
+(void)classBeginEnd:(NSString * _Nonnull )roomID companyid:(NSString *_Nonnull)companyid  aHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort userid:(NSString *_Nullable)userid roleid:(NSString *_Nullable)roleid aComplete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;

#pragma mark 上课
+(void)classBeginStar:(NSString * _Nonnull )roomID companyid:(NSString *_Nonnull)companyid  aHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort userid:(NSString *_Nullable)userid roleid:(NSString *_Nullable)roleid aComplete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;

#pragma mark 获取区域列表
+ (void)getAreaListWithHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort aComplete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;

#pragma mark 当前默认选择的区域
+ (void)getDefaultAreaWithComplete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;

/**
 获取安装文件的版本信息

 @param aHost host
 @param aPort port
 @param version 本地版本
 @param type 客户端种类，0：mac 客户端 1：PC Conference  2:Android pad  3:安卓盒子 4:Android mobile 5:IOS mobile 6：ipad（本文新增） 7：新学问老师端 8：新学问tv端
   companydomain  公司域名
 @param aComplete 返回消息
 @param aNetError 错误消息
 */
+ (void)getupdateinfoWithaHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort Version:(NSString *_Nonnull )version Type:(int)type Complete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;

/**
 MP3文件下载

 @param url 文件名称
 @param aComplete 返回消息
 @param aNetError 错误消息
 */
+ (void)downLoadMp3File:(NSString*_Nonnull)url Complete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;

+ (void)downLoadTaskToSandboxWithHost:(NSString *_Nonnull)host taskDic:(NSDictionary *_Nonnull)taskDic complete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;
/**
 获取回放的room.json数据
 
 @param path 访问地址
 @param aComplete 返回消息
 @param aNetError 错误消息
 */
+ (void)getRoomJsonWithPath:(NSString *_Nonnull)path Complete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;


//获取系统时间
+(void)systemtime:(NSDictionary *_Nonnull)aParam Complete:(bComplete _Nonnull )aComplete aNetError:(bError _Nullable) aNetError;


//获取课件备注
//+(void)getRemarkWithRoomID:(NSString *_Nonnull)aRoomId aHost:(NSString*_Nonnull)aHost aPort:(NSString *_Nonnull)aPort complete:(bComplete _Nonnull )aComplete error:(bError _Nullable) aNetError;

@end

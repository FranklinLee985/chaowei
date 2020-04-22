//
//  TKEduRoomDelegate.h
//  EduClass
//
//  Created by 李合意 on 2019/10/15.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol TKEduRoomDelegate <NSObject>

@optional


/**
 进入房间失败
 
 @param result 错误码 详情看 TKRoomSDK -> TKRoomDefines ->TKRoomErrorCode 结构体
 
 @param desc 失败的原因描述
 */
- (void)onEnterRoomFailed:(int)result Description:(NSString*)desc;

/**
 被踢回调

 @param reason 0:被老师踢出（暂时无） 1：重复登录
 */
- (void)onKitout:(int)reason;

/**
 进入课堂成功后的回调
 */
- (void)joinRoomComplete;

/**
 离开课堂成功后的回调
 */
- (void)leftRoomComplete;

/**
 课堂开始的回调
 */
- (void)onClassBegin;

/**
 课堂结束的回调
 */
- (void)onClassDismiss;

/**
 摄像头打开失败回调
 */
- (void)onCameraDidOpenError;


@end

NS_ASSUME_NONNULL_END

//
//  TKEduClassManager.h
//  TKUISDK
//
//  Created by TK on 2019/9/24.
//  Copyright © 2019 TK. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TKEduRoomDelegate.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKEduClassManager : NSObject


+ (instancetype)shareInstance;
/**
 checkRoom成功后 房间属性
 */
@property (nonatomic, strong)NSDictionary *roomJson;


/**
 进入房间的函数

 @param paramDic NSDictionary类型，键值需要传递serial（课堂号）、host（服务器地址）、port（服务器端口号）、nickname（用户昵称）、userid(用户ID，可选)、password(密码)、clientType(客户端类型）
 @param controller 视图控制器， 通常与下边delegate相同
 @param delegate 遵循TKEduEnterClassRoomDelegate代理，供给用户进行处理
 @param isFromWeb 是否是从网址链接进入进入
 @return 是否成功 0 成功 其他失败
 */
- (int)joinRoomWithParamDic:(NSDictionary*)paramDic
             ViewController:(UIViewController*)controller
                   Delegate:(id<TKEduRoomDelegate>)delegate
                  isFromWeb:(BOOL)isFromWeb;

/**
 进入回放房间的函数
 
 @param paramDic
 键值需要传递serial（课堂号）、host（服务器地址）、port（服务器端口号）、nickname（用户昵称）、userid(用户ID，可选)、password(密码)、clientType(客户端类型）
 @param controller 视图控制器，通常与下边delegate相同
 @param delegate 遵循TKEduEnterClassRoomDelegate代理，供给用户进行处理
 @param isFromWeb 是否是从网址链接进入进入
 @return 是否成功 0 成功 其他失败
 */
- (int)joinPlaybackRoomWithParamDic:(NSDictionary *)paramDic
                     ViewController:(UIViewController*)controller
                           Delegate:(id<TKEduRoomDelegate>)delegate
                          isFromWeb:(BOOL)isFromWeb;

/**
 从网页链接进入房间(直播\回放)

 @param url 网页url
 */
- (void)joinRoomWithUrl:(NSString*)url;


/// 离开房间
- (void)leaveRoom;

/// AppDelegate  applicationDidBecomeActive 请调用此方法
- (void)applicationDidBecomeActive;

@end


NS_ASSUME_NONNULL_END

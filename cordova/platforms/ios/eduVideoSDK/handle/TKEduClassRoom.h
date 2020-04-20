//
//  TKEduClassRoom.h
//  EduClassPad
//
//  Created by TK on 2017/5/10.
//  Copyright © 2017年 beijing. All rights reserved.
//  Version: 2.1.0
//

#import <Foundation/Foundation.h>
#import "TKRoomJsonModel.h"
#import <UIKit/UIKit.h>
#import "TKEduRoomDelegate.h"

@interface TKEduClassRoom : NSObject

+ (instancetype)shareInstance;
    
/**
 checkRoom成功后 房间属性
 */
@property (nonatomic, strong)TKRoomJsonModel *roomJson;



/// 视图控制器  用于接收代理回调  和 教室退出后的显示页面

/// 采用标准版登录页无需设置
@property (nonatomic, strong)UIViewController<TKEduRoomDelegate> *rootVC;

/**
 进入房间的函数

 @param paramDic NSDictionary类型，键值需要传递serial（课堂号）、host（服务器地址）、port（服务器端口号）、nickname（用户昵称）、userid(用户ID，可选)、password(密码)、clientType(客户端类型）
 @param controller 当前页面的控制器，通常与下边delegate相同
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
 
 @param paramDic 内 键值需要传递serial（课堂号）、host（服务器地址）、port（服务器端口号）、nickname（用户昵称）、userid(用户ID，可选)、password(密码)、clientType(客户端类型）
 @param controller 当前页面的控制器，通常与下边delegate相同
 @param delegate 遵循TKEduEnterClassRoomDelegate代理，供给用户进行处理
 @param isFromWeb 是否是从网址链接进入进入
 @return 是否成功 0 成功 其他失败
 */
- (int)joinPlaybackRoomWithParamDic:(NSDictionary *)paramDic
                     ViewController:(UIViewController*)controller
                           Delegate:(id<TKEduRoomDelegate>)delegate
                          isFromWeb:(BOOL)isFromWeb;

/**
 从网页链接进入房间

 @param url 网页url
 */
- (void)joinRoomWithUrl:(NSString*)url;


/// 离开房间
- (void)leaveRoom;

/// AppDelegate  applicationDidBecomeActive 请调用此方法
- (void)applicationDidBecomeActive;

@end

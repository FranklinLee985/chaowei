//
//  TKServersLog.h
//  TKRoomSDK
//
//  Created by yi on 2019/8/22.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TKRoomDefines.h"

NS_ASSUME_NONNULL_BEGIN


@interface TKServersLog : NSObject

/**
 设置打印SDK日志等级
 
 @param level 日志等级
 @param logPath 日志需要写入沙盒的路径; 默认路径为：沙盒Libary/Caches/TKSDKLogs。
 @param debug 是否时debug模式，debug模式：控制台打印，release模式：控制台不打印。
 @return 0表示调用成功，非0表示调用失败
 */
+ (int)setLogLevel:(TKLogLevel)level logPath:(NSString * _Nullable)logPath debugToConsole:(BOOL)debug;


/**
 上传一条日志
 @param level  日志类型 ( TKLoggerLevelError TKLoggerLevelDebug TKLoggerLevelInfo TKLoggerLevelWarning ) 暂不支持其他类型
 @param text 日志内容
 */
+ (void)uploadLogWithLevel:(TKLogLevel)level Text:(NSString *)text;

@end

NS_ASSUME_NONNULL_END

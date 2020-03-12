//
//  TKAPPSetConfig.h
//  EduClass
//
//  Created by maqihan on 2018/11/30.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKAPPSetConfig : NSObject

+ (instancetype)shareInstance;


/// 初始化 主题 崩溃上报  更新
/// @param appID bugly appid (传@""  默认使用拓课云上报)
- (void)setupAPPWithAppID:(NSString *)appID;


@end

NS_ASSUME_NONNULL_END

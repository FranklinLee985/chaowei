//
//  XWMP3ToPCM.h
//  EduClassPad
//
//  Created by lyy on 2018/01/17.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TKMP3ToPCM : NSObject

+ (void)mp3ToPcmWithMp3FilePath:(NSString *)mp3FilePath pcmFilePath:(NSString *)pcmFilePath completion:(void (^)(BOOL convertState))block;

@end

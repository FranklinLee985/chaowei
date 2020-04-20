//
//  TKMediaPlayingHelper.h
//  EduClass
//
//  Created by Yibo on 2019/9/5.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKMediaPlayingHelper : NSObject

@property (nonatomic, assign) BOOL isOnScreen;
@property (nonatomic, assign) BOOL isPaused;//手动暂停置为YES
@property (nonatomic, assign) NSTimeInterval progress;//视频播放进度，以秒记
+ (instancetype)sharedInstance;

- (void)pause;

- (void)resume;

- (void)resolveBlackScreen;

@end

NS_ASSUME_NONNULL_END

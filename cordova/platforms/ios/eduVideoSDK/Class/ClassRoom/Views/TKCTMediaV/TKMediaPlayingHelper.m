//
//  TKMediaPlayingHelper.m
//  EduClass
//
//  Created by Yibo on 2019/9/5.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKMediaPlayingHelper.h"

static TKMediaPlayingHelper *helper;
@implementation TKMediaPlayingHelper

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        helper = [[self alloc] init];
        helper.isOnScreen = NO;
    });
    
    return helper;
}

- (void)pause
{
    if (self.isOnScreen && !self.isPaused) {
        [[TKEduSessionHandle shareInstance] sessionHandleMediaPause:YES];
    }
}

- (void)resume
{
    if (self.isOnScreen && !self.isPaused) {
        [[TKEduSessionHandle shareInstance] sessionHandleMediaPause:NO];
    }
}

- (void)resolveBlackScreen
{
    if (self.isOnScreen && self.isPaused) {
        
        [[TKEduSessionHandle shareInstance] sessionHandleMediaSeektoPos:self.progress - 1];
        [[TKEduSessionHandle shareInstance] sessionHandleMediaPause:NO];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_MSEC)), dispatch_get_main_queue(), ^{
            [[TKEduSessionHandle shareInstance] sessionHandleMediaSeektoPos:self.progress - 10];
            [[TKEduSessionHandle shareInstance] sessionHandleMediaPause:YES];
        });
    }
}

@end

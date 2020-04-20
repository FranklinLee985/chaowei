//
//  TKPopViewHelper.m
//  EduClass
//
//  Created by Yibo on 2019/9/10.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKPopViewHelper.h"

static TKPopViewHelper *helper = nil;
@implementation TKPopViewHelper

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        helper = [[self alloc] init];
        [helper clearAfterClass];
    });
    
    return helper;
}

- (NSArray *)states
{
    if ([TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomTypeOneToOne && ![TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish) {
        return @[@(self.isAnswerToolOn),
                 @(self.isTurntableToolOn),
                 @(self.isTimerToolOn),
                 @(self.isMiniWhiteboardToolOn),
                 ];
    } else {
        return @[@(self.isAnswerToolOn),
                 @(self.isTurntableToolOn),
                 @(self.isTimerToolOn),
                 @(self.isResponderToolOn),
                 @(self.isMiniWhiteboardToolOn),
                 ];
    }
}

- (void)clearAfterClass
{
    self.isAnswerToolOn         = NO;
    self.isTurntableToolOn      = NO;
    self.isTimerToolOn          = NO;
    self.isResponderToolOn      = NO;
    self.isMiniWhiteboardToolOn = NO;
}

@end

//
//  TKPopViewHelper.h
//  EduClass
//
//  Created by Yibo on 2019/9/10.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKPopViewHelper : NSObject

@property (nonatomic, assign) BOOL isAnswerToolOn;
@property (nonatomic, assign) BOOL isTurntableToolOn;
@property (nonatomic, assign) BOOL isTimerToolOn;
@property (nonatomic, assign) BOOL isResponderToolOn;
@property (nonatomic, assign) BOOL isMiniWhiteboardToolOn;

+ (instancetype)sharedInstance;

- (NSArray *)states;

- (void)clearAfterClass;

@end

NS_ASSUME_NONNULL_END

//
//  TKChatMessageModel.m
//  EduClassPad
//
//  Created by ifeng on 2017/5/12.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKChatMessageModel.h"


@implementation TKChatMessageModel

- (instancetype)initWithMsgType:(TKChatMessageType)iMsgType role:(TKChatRoleType)iRole message:(NSString *)iMessage cospath:(NSString *)iCospath userName:(NSString *)iUserName fromid:(NSString *)iFromid time:(NSString *)iTime {
    
    if (self = [super init]) {
        
        _iChatMessageType = iMsgType;
        _iChatRoleType    = iRole;
        
        _iCospath         = iCospath ?: @"";
        _iMessage         = iMessage ?: @"";
        _iUserName        = iUserName ?: @"";
        _iFromid          = iFromid ?: @"";
        _iTime            = iTime ?: @"";
    }
    return self;
}

@end

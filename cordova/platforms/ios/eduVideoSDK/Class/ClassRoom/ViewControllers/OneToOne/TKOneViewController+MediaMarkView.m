//
//  TKManyViewController+MediaMarkView.m
//  EduClass
//
//  Created by Yibo on 2019/4/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKOneViewController+MediaMarkView.h"

@implementation TKOneViewController (MediaMarkView)

- (void)mediaMarkRecoveryAfterGetinClass:(NSNotification *)notification
{
    NSDictionary *dict        = notification.userInfo;
    NSDictionary *response    = [dict objectForKey:TKWhiteBoardOnRoomConnectedRoomMsgKey];
    
    NSString *tJsonDataJsonString;
    if (response) {
        NSData *tJsonData = [NSJSONSerialization dataWithJSONObject:response options:NSJSONWritingPrettyPrinted error:nil];
        tJsonDataJsonString = [[NSString alloc] initWithData:tJsonData encoding:NSUTF8StringEncoding];
    }else{
        tJsonDataJsonString = @"";
    }
    
    if (!response || response.allKeys.count == 0) {
        return;
    }
    
    NSDictionary *msglist = [response objectForKey:@"msglist"];
    for (NSString *key in msglist.allKeys) {
        NSDictionary *dictionary = [msglist objectForKey:key];
        NSString *associatedMsgID = [dictionary objectForKey:@"associatedMsgID"];
        if ([associatedMsgID isEqualToString:@"VideoWhiteboard"]) {
            //表示改信令用于视频标注
            NSString *name = [dictionary objectForKey:@"name"];
            if ([name isEqualToString:sSharpsChange]) {
                [[TKEduSessionHandle shareInstance].mediaMarkView.recoveryArray addObject:[msglist objectForKey:key]];
            }
        } else {
            if ([key isEqualToString:sVideoWhiteboard]) {
                [[TKEduSessionHandle shareInstance].mediaMarkView.recoveryArray addObject:[msglist objectForKey:key]];
            }
        }
    }
}

@end

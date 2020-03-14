//
//  TKManyViewController+MiniWhiteBoard.m
//  EduClass
//
//  Created by Yibo on 2019/4/2.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKOneViewController+MiniWhiteBoard.h"

@implementation TKOneViewController (MiniWhiteBoard)

- (void)miniWBRecoveryAfterGetinClass:(NSNotification *)notification
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
    NSMutableArray *miniWhiteboardRecoveryMSG = [@[] mutableCopy];
    
    for (NSString *key in msglist.allKeys) {
        NSDictionary *dictionary = [msglist objectForKey:key];
        NSString *associatedMsgID = [dictionary objectForKey:@"associatedMsgID"];
        if ([associatedMsgID isEqualToString:sBlackBoard_new] || [associatedMsgID isEqualToString:sClassBegin]) {
            //表示改信令用于小白板
            NSString *name = [dictionary objectForKey:@"name"];
            if ([name isEqualToString:sSharpsChange] || [name isEqualToString:sBlackBoard_new] || [name isEqualToString:sUserHasNewBlackBoard]) {
                [miniWhiteboardRecoveryMSG addObject:[msglist objectForKey:key]];
            }
        } else {
            if ([key isEqualToString:@"BigRoom"]) {
                //大并发教室
                self.miniWB.bigRoom = YES;
            }
        }
    }
    
    //开始恢复小白板数据
    //将miniWhiteboardRecoveryMSG元素按seq值排序
    if (miniWhiteboardRecoveryMSG.count != 0) {
        //老师绘制优先排列
        __block NSMutableArray *shapeChange = [@[] mutableCopy];
        __block NSMutableArray *userHasNewBlackBoard = [@[] mutableCopy];
        
        [miniWhiteboardRecoveryMSG enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *name = [obj objectForKey:@"name"];
            if ([name isEqualToString:@"SharpsChange"]) {
                [shapeChange addObject:obj];
            } else {
                [userHasNewBlackBoard addObject:obj];
            }
        }];
        
        miniWhiteboardRecoveryMSG = [NSMutableArray arrayWithArray:shapeChange];
        [miniWhiteboardRecoveryMSG addObjectsFromArray:userHasNewBlackBoard];
        
        for (NSDictionary *dic in miniWhiteboardRecoveryMSG) {
            [self.miniWB handleSignal:dic isDel:NO];
        }
    }
}

@end

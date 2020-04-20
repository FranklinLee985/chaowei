//
//  TKStudentSegmentObject.m
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/7.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import "TKStudentSegmentObject.h"
#import <TKRoomSDK/TKRoomSDK.h>

@implementation TKStudentSegmentObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary
{
    if (self = [super init]) {
        
        id dataObject = dictionary;
        NSMutableDictionary *data = nil;
        if ([dataObject isKindOfClass:[NSDictionary class]]) {
            data = [NSMutableDictionary dictionaryWithDictionary:dataObject];
        }
        if ([dataObject isKindOfClass:[NSString class]]) {
            data = [NSJSONSerialization JSONObjectWithData:[(NSString *)dataObject dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:nil];
        }

        
        self.ID = [data objectForKey:@"id"];
        if (!self.ID || self.ID.length == 0) {
            self.ID = @" ";
        }
        
        self.nickName = [data objectForKey:@"nickname"];
        if (!self.nickName || self.nickName.length == 0) {
            self.nickName = @" ";
        }
        
        self.role = [data objectForKey:@"role"];
        if (!self.role || [self.role isEqual:[NSNull null]]) {
            self.role = @(0);
        }
        
        self.publishState = [data objectForKey:@"publishstate"];
        if (!self.publishState || [self.publishState isEqual:[NSNull null]]) {
            self.publishState = @(0);
        }
        
        self.seq = [dictionary objectForKey:@"seq"];
        if (!self.seq || [self.seq isEqual:[NSNull null]]) {
            self.seq = @(0);
        }
        
        self.currentPage = 1;
    }
    
    return self;
}

+ (TKStudentSegmentObject *)teacher
{
    static TKStudentSegmentObject *teacher = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        teacher = [[TKStudentSegmentObject alloc] init];
        teacher.ID = sBlackBoardCommon;
        teacher.seq = @(0);
        teacher.nickName = @"老师";
        teacher.role = @(0);
        teacher.currentPage = 1;
    });
    return teacher;
}
    
- (NSString *)description
{
    return [NSString stringWithFormat:@"ID:%@   |***|   name:%@   |***|   seq:%@",self.ID,self.nickName,self.seq];
}
    
    
@end

//
//  TKStudentSegmentObject.h
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/7.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKStudentSegmentObject : NSObject

@property (nonatomic, copy) NSString *ID;
@property (nonatomic, copy) NSString *nickName;
@property (nonatomic, copy) NSNumber *role;
@property (nonatomic, copy) NSNumber *publishState;
@property (nonatomic, assign) int currentPage;
@property (nonatomic, copy) NSNumber *seq;

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

+ (TKStudentSegmentObject *)teacher;

@end

NS_ASSUME_NONNULL_END

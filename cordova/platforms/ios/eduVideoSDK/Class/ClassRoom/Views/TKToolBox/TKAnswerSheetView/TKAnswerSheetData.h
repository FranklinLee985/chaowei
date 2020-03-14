//
//  TKAnswerSheetData.h
//  EduClass
//
//  Created by maqihan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKAnswerSheetData : NSObject

+ (instancetype)shareInstance;

//问题ID
@property (strong , nonatomic ,nullable) NSString *quesID;
//正确答案 @1 @2 @3
@property (strong , nonatomic,nullable) NSArray  *answer123;
//正确答案 @"A" @"B" @"C"
@property (strong , nonatomic,nullable) NSArray  *answerABC;

//每个答案选择的次数  @[@0,@0,@1,@0,@3]
@property (strong , nonatomic,nullable) NSArray *options;
//记录自己已经提交的答案 @[@1,@3,@4]
@property (strong , nonatomic,nullable) NSArray *myAnswer;
@property (strong , nonatomic,nullable) NSArray *myAnswerABC;

//提交答案的总人数
@property (assign , nonatomic) NSInteger  count;
//发布答题的时间戳
@property (assign , nonatomic) long long startTime;

//答题结束清理数据
- (void)resetData;

//发布答题的数据格式
- (NSString *)releaseWithAnswer:(NSArray *)answer options:(NSArray *)options;

//学生提交答案
- (NSString *)submitWithAnswer:(NSArray *)answer selected:(NSArray *)selected options:(NSArray *)options modify:(BOOL)modify;

//老师结束答题 answer正确答案 options学生选择情况
- (NSString *)endWithAnswer:(NSArray *)answer options:(NSArray *)options time:(long long )time num:(NSInteger)num;
//公布答案
- (NSString *)publishWithAnswer:(NSArray *)answer options:(NSArray *)options time:(long long )time num:(NSInteger)num publish:(BOOL)state;

//获取白板统计数据
//POST
- (NSURLSessionTask *)POST:(NSString *)URLString
                parameters:(id)parameters
                   success:(void (^)(id responseObject))success
                   failure:(void (^)(NSError *error))failure;
@end

NS_ASSUME_NONNULL_END

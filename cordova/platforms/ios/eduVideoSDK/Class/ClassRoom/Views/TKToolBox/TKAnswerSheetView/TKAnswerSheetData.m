//
//  TKAnswerSheetData.m
//  EduClass
//
//  Created by maqihan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKAnswerSheetData.h"
#import "TKEduSessionHandle.h"
#import "TKAFNetworking.h"

@implementation TKAnswerSheetData

+ (instancetype)shareInstance {
    static TKAnswerSheetData *_sharedManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedManager = [[TKAnswerSheetData alloc] init];
    });
    return _sharedManager;
}

- (NSArray *)myAnswerABC
{
    //由123——》ABC
    if (self.myAnswer.count) {
        
        NSMutableArray *answer = [NSMutableArray arrayWithCapacity:10];
        for (int i = 0; i < self.myAnswer.count; i++) {
            int temp = [self.myAnswer[i] intValue];
            NSString *string = [NSString stringWithFormat:@"%c",temp + 65];
            [answer addObject:string];
        }
        
        //排序答案
        NSArray *sortedArray = [answer sortedArrayUsingComparator:^NSComparisonResult(NSString *obj1, NSString *obj2) {
            return  [obj1 compare:obj2];
        }];
        
        return sortedArray;
    }
    return nil;
}

//答题结束清理数据
- (void)resetData
{
    self.quesID = nil;
    self.answerABC = nil;
    self.answer123 = nil;
    self.options = nil;
    self.myAnswer = nil;
    self.count = 0;
    self.startTime = 0;
}

- (NSString *)releaseWithAnswer:(NSArray *)answer options:(NSArray *)options
{
    NSString *json =  @"{\"options\":[{\"hasChose\":true},{\"hasChose\":false},{\"hasChose\":false},{\"hasChose\":false}],\"action\":\"start\",\"rightOptions\":[0],\"state\":{\"show\":true,\"questionState\":\"RUNNING\",\"sizeState\":\"NORMAL\",\"page\":{\"index\":\"STATISTICS\",\"data\":{}},\"options\":[{\"hasChose\":true},{\"hasChose\":false},{\"hasChose\":false},{\"hasChose\":false}],\"result\":[0,0,0,0],\"hasPub\":false,\"ansTime\":0,\"resultNum\":0,\"rightOptions\":[0],\"detailData\":[],\"resizeInfo\":{\"width\":12,\"height\":7.1},\"detailPageInfo\":{\"current\":1,\"total\":1},\"hintShow\":false,\"scale\":1,\"owner\":{},\"event\":{\"type\":\"roompubmsg\",\"message\":{\"id\":\"Question_1093735057\",\"_id\":\"5c36f77c653e676a8f2f8080\",\"seq\":34,\"roomId\":\"1093735057\",\"fromID\":\"46E36A45-D321-43C7-80D1-E95585A91E95\",\"toID\":\"__all\",\"data\":{\"action\":\"open\"},\"userId\":\"46E36A45-D321-43C7-80D1-E95585A91E95\",\"write2DB\":true,\"role\":0,\"ts\":1547106172,\"name\":\"Question\"}}},\"quesID\":\"ques_1547106180731\"}";
    
    //转成字典
    NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
    
    //记录正确答案
    self.answerABC = answer;
    
    //修改字段
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:10];
    for (NSString *item in options) {
        if ([answer containsObject:item]) {
            [array addObject:@{@"hasChose":@(YES)}];
        }else{
            [array addObject:@{@"hasChose":@(NO)}];
        }
    }
    [dict setValue:array forKey:@"options"];
    [dict setValue:array forKeyPath:@"state.options"];

    
    NSMutableArray *array1 = [NSMutableArray arrayWithCapacity:10];
    for (NSString *item in answer) {
        int code = [item characterAtIndex:0] - 65;
        [array1 addObject: @(code)];
    }
    //记录正确答案
    self.answer123 = array1;
    
    [dict setValue:array1 forKey:@"rightOptions"];
    [dict setValue:array1 forKeyPath:@"state.rightOptions"];

    //时间戳
    NSTimeInterval interval = [[NSDate date] timeIntervalSince1970];
    long long ms = interval*1000;
    //老师端存储ID
    self.quesID = [NSString stringWithFormat:@"ques_%lld",ms];
    [dict setValue:self.quesID forKey:@"quesID"];
    
    //记录开始时间
    self.startTime = ms;

    
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    TKRoomUser *localUser  = [TKEduSessionHandle shareInstance].localUser;

    //roomid 是 serial
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];

    
    [dict setValue:msgID               forKeyPath:@"state.event.message.id"];
    [dict setValue:roomDict[@"serial"] forKeyPath:@"state.event.message.roomId"];
    [dict setValue:localUser.peerID    forKeyPath:@"state.event.message.fromID"];
    [dict setValue:localUser.peerID    forKeyPath:@"state.event.message.userId"];
    [dict setValue:@(interval)         forKeyPath:@"state.event.message.ts"];
    [dict setValue:@(localUser.role)   forKeyPath:@"state.event.message.role"];

    //转化为字符串
    NSString *json1 = [TKUtil dictionaryToJSONString:dict];

    return json1;
}

- (NSString *)submitWithAnswer:(NSArray *)answer selected:(NSArray *)selected options:(NSArray *)options modify:(BOOL)modify
{
    NSString *json =  @"{\"options\":[{\"hasChose\":false},{\"hasChose\":false},{\"hasChose\":true},{\"hasChose\":false}],\"actions\":{\"2\":1},\"modify\":0,\"stuName\":\"你在哪\",\"quesID\":\"ques_1547182880492\",\"isRight\":0}";

    //转成字典
    NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
    
    //将选择的答案转化为@"A",@"B",@"C"... -->@1,@2,@3...
    NSMutableArray *array123 = [NSMutableArray arrayWithCapacity:10];
    for (NSString *item in selected) {
        int code = [item characterAtIndex:0] - 65;
        NSNumber *key = [NSNumber numberWithInt:code];
        [array123 addObject:key];
    }
    
    //修改字段
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:10];
    for (int i = 0; i < options.count; i++) {
        if ([array123 containsObject:@(i)]) {
            [array addObject:@{@"hasChose":@(YES)}];
        }else{
            [array addObject:@{@"hasChose":@(NO)}];
        }
    }
    
    //修改答案
    NSMutableDictionary *answerDict = [NSMutableDictionary dictionaryWithCapacity:10];
    for (NSNumber *item in array123) {
        if (![self.myAnswer containsObject:item]) {
            [answerDict setValue:@1 forKey:item.stringValue];
        }
    }
    
    for (NSNumber *item in self.myAnswer) {
        if (![array123 containsObject:item]) {
            [answerDict setValue:@(-1) forKey:item.stringValue];
        }
    }
    
    //记录自己选的答案
    self.myAnswer = array123;
    
    //昵称
    NSString *nickname = [TKEduSessionHandle shareInstance].localUser.nickName;
    
    //是否选择正确答案
    BOOL right = [answer isEqualToArray:array123];
    
    
    NSInteger status = modify ? 1: 0;
    [dict setValue:array      forKey:@"options"];
    [dict setValue:answerDict forKey:@"actions"];
    [dict setValue:@(status)  forKey:@"modify"];
    [dict setValue:nickname   forKey:@"stuName"];
    [dict setValue:self.quesID forKey:@"quesID"];
    [dict setValue:@(right)   forKey:@"isRight"];

    //转化为字符串
    NSString *json1 = [TKUtil dictionaryToJSONString:dict];

    
    return json1;
}

- (NSString *)endWithAnswer:(NSArray *)answer options:(NSArray *)options time:(long long )time num:(NSInteger)num
{
    NSString *json =  @"{\"action\":\"end\",\"state\":{\"show\":true,\"questionState\":\"FINISHED\",\"sizeState\":\"NORMAL\",\"page\":{\"index\":\"STATISTICS\",\"data\":{}},\"options\":[{\"hasChose\":false},{\"hasChose\":false},{\"hasChose\":true},{\"hasChose\":true}],\"result\":[0,1,1,1],\"hasPub\":false,\"ansTime\":11,\"resultNum\":1,\"rightOptions\":[2,3],\"detailData\":[],\"resizeInfo\":{\"width\":12,\"height\":7.1},\"detailPageInfo\":{\"current\":1,\"total\":1},\"hintShow\":false,\"scale\":1,\"owner\":{},\"event\":{\"type\":\"roompubmsg\",\"message\":{\"id\":\"Question_1061808756\",\"totalUsers\":1,\"correctAnswers\":0,\"seq\":257,\"toID\":\"C2740E48-AB6C-4599-9AFA-97449CC13AE1\",\"fromID\":\"C2740E48-AB6C-4599-9AFA-97449CC13AE1\",\"type\":\"getCount\",\"data\":{},\"answerCount\":1,\"values\":{\"1\":\"1\",\"2\":\"1\",\"3\":\"1\"},\"ts\":1548498089,\"name\":\"GetQuestionCount\",\"associatedMsgID\":\"Question\"}}},\"quesID\":\"ques_1548498079536\"}";
    
    //转成字典
    NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
    
    //修改字段
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:10];
    for (int i = 0; i < options.count; i++) {
        if ([answer containsObject:@(i)]) {
            [array addObject:@{@"hasChose":@(YES)}];
        }else{
            [array addObject:@{@"hasChose":@(NO)}];
        }
    }
    
    NSMutableDictionary *values = [NSMutableDictionary dictionaryWithCapacity:10];
    for (int i = 0; i < options.count; i++) {
        if ([options[i] intValue] > 0) {
            NSString *key = [NSString stringWithFormat:@"%d",i];
            [values setValue:options[i] forKey:key];
        }
    }
    
    [dict setValue:array   forKeyPath:@"state.options"];
    [dict setValue:options forKeyPath:@"state.result"];
    [dict setValue:values  forKeyPath:@"state.event.message.values"];

    //时间差
    //有可能是13位 也可能是10位 所以比较蛋疼
    NSString *timeString = [NSString stringWithFormat:@"%lld",time];
    NSInteger mul = (timeString.length == 10) ? 1 : 1000;
    
    NSTimeInterval interval = [[NSDate date] timeIntervalSince1970];
    long long ms    = interval * mul;
    NSInteger time2 = (NSInteger)((ms - time) / mul);
    [dict setValue:@(time2) forKeyPath:@"state.ansTime"];

    
    [dict setValue:@(num)   forKeyPath:@"state.resultNum"];
    [dict setValue:answer   forKeyPath:@"state.rightOptions"];

    
    NSDictionary *roomDict = [TKRoomManager instance].getRoomProperty;
    TKRoomUser *localUser  = [TKEduSessionHandle shareInstance].localUser;
    
    //roomid 是 serial
    NSString *msgID        = [NSString stringWithFormat:@"Question_%@",roomDict[@"serial"]];
    
    
    [dict setValue:msgID               forKeyPath:@"state.event.message.id"];
    [dict setValue:@(num)              forKeyPath:@"state.event.message.totalUsers"];
    [dict setValue:localUser.peerID    forKeyPath:@"state.event.message.fromID"];
    [dict setValue:localUser.peerID    forKeyPath:@"state.event.message.toID"];
    [dict setValue:@(num)              forKeyPath:@"state.event.message.answerCount"];
    [dict setValue:@(interval)         forKeyPath:@"state.event.message.ts"];
    
    [dict setValue:self.quesID         forKeyPath:@"quesID"];

    //转化为字符串
    NSString *json1 = [TKUtil dictionaryToJSONString:dict];


    return json1;
}

- (NSString *)publishWithAnswer:(NSArray *)answer options:(NSArray *)options time:(long long )time num:(NSInteger)num publish:(BOOL)state
{
    NSString *json =  @"{\"result\":[0,1,1,0],\"resultNum\":1,\"ansTime\":4,\"rightOptions\":[0,2,3],\"hasPub\":false}";
    //转成字典
    NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
    
    [dict setValue:@(state)forKey:@"hasPub"];

    //时间差
    //有可能是13位 也可能是10位 所以比较蛋疼
    NSString *timeString = [NSString stringWithFormat:@"%lld",time];
    NSInteger mul = (timeString.length == 10) ? 1 : 1000;
    
    NSTimeInterval interval = [[NSDate date] timeIntervalSince1970];
    long long ms    = interval * mul;
    NSInteger time2 = (NSInteger)((ms - time) / mul);
    
    [dict setValue:answer      forKey:@"rightOptions"];
    [dict setValue:@(time2)    forKey:@"ansTime"];
    [dict setValue:@(num)      forKey:@"resultNum"];
    [dict setValue:options     forKey:@"result"];

    //转化为字符串
    NSString *json1 = [TKUtil dictionaryToJSONString:dict];

    return json1;
}

- (NSURLSessionTask *)POST:(NSString *)URLString
                parameters:(id)parameters
                   success:(void (^)(id responseObject))success
                   failure:(void (^)(NSError *error))failure

{
    //下载新数据
    __block NSURLSessionTask *sessionTask = nil;
    TKAFHTTPSessionManager *sessionManager = [TKAFHTTPSessionManager manager];

    sessionTask = [sessionManager POST:URLString
                            parameters:parameters
                              progress:^(NSProgress * _Nonnull uploadProgress) {
                                  if (uploadProgress){
                                  }
                              }
                               success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
                                   if (success){
                                       success(responseObject);
                                   }
                               }
                               failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                                   if (failure){
                                       failure(error);
                                   }
                               }];
    
    return sessionTask;
}


@end

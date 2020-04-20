//
//  TKAnswerSheetView.h
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKBaseBackgroundView.h"
#import "TKAnswerSheetData.h"

NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSInteger, TKAnswerSheetType) {
    //设置
    TKAnswerSheetType_Setup = 0,
    //提交
    TKAnswerSheetType_Submit  = 1,
    //详情
    TKAnswerSheetType_Detail  = 2,
    //统计
    TKAnswerSheetType_Record  = 3
};

typedef NS_ENUM(NSInteger, TKAnswerSheetState) {
    //结束/未进行（还未点击公布答案）
    TKAnswerSheetState_End  = 0,
    //答题进行中
    TKAnswerSheetState_Start = 1,
    //点击公布答案
    TKAnswerSheetState_Release  = 2,
};

@interface TKAnswerSheetView : TKBaseBackgroundView

//viewType == TKAnswerSheetType_Detail 传参数：答案可选项，正确答案，倒计时
//viewType == TKAnswerSheetType_Setup  传参数：无
//viewType == TKAnswerSheetType_Submit 传参数：答案可选项，正确答案
@property (assign , nonatomic) TKAnswerSheetType viewType;

//根据信令设置这里的值
@property (assign , nonatomic) TKAnswerSheetState state;

//viewType == TKAnswerSheetType_Detail 传参数：答案可选项，正确答案，倒计时
//详情页面需要的数据 格式会有很多种 需要做判断
@property (strong , nonatomic) NSDictionary *dict;

//viewType == TKAnswerSheetType_Detail
//根据时间戳（开始时的时间戳）计算 答题卡开始了多久了
- (void)showTimeWithTimeStamp:(NSString *)time;

@end

NS_ASSUME_NONNULL_END

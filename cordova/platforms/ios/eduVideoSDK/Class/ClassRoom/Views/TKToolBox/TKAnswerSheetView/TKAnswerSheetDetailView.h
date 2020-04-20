//
//  TKAnswerSheetDetailView.h
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@protocol TKAnswerSheetDetailViewDelegate <NSObject>

//切换到统计
- (void)didPressRecordButton:(UIButton *)button;
//公布答案 结束答题
- (void)didPublishAnswer;
//获取答题卡状态
- (NSInteger)answerSheetType;
@end

@interface TKAnswerSheetDetailView : UIView

@property (nonatomic , weak) id<TKAnswerSheetDetailViewDelegate> delegate;

@property (assign , nonatomic) NSInteger optionsCount;

@property (strong , nonatomic) NSDictionary *dict;

@property (strong , nonatomic) NSString *timeString;


//开启定时器获取答案
- (void)startTimer;
//关闭定时器
- (void)invalidateTimer;
//老师端显示公布答案 按钮
- (void)releaseButtonShow:(BOOL)state buttonSelected:(BOOL)selected;

//【注意⚠️】下面数据会有两种格式 需要根据当前的身份判断
//老师收到学生提交的答题 学生收到公布的结果
/* 学生时的数据结构
 ansTime = 29;
 result =     (
 0,
 0,
 1,
 1
 );
 resultNum = 1;
 rightOptions =     (
 2,
 3
 );
 */
/* 学生提交答案
 {
 answerCount = 1;
 associatedMsgID = Question;
 correctAnswers = 1;
 data =     {
 };
 fromID = "5097BB29-D574-4AE6-A600-EE23CA023370";
 id = "Question_1093735057";
 name = GetQuestionCount;
 seq = 42;
 toID = "__all";
 totalUsers = 2;
 ts = 1548127312;
 type = getCount;
 values =     {
 2 = 1;
 3 = 1;
 };
 */

/*
    老师发布答题
 {
 action = start;
 options =     (
 {
 hasChose = 0;
 },
 {
 hasChose = 0;
 },
 {
 hasChose = 1;
 },
 {
 hasChose = 1;
 }
 );
 quesID = "ques_1548127505902";
 rightOptions =     (
 2,
 3
 );
 state =     {
 ansTime = 0;
 detailData =         (
 );
 detailPageInfo =         {
 current = 1;
 total = 1;
 };
 event =         {
 message =             {
 "_id" = 5c36f77c653e676a8f2f8080;
 data =                 {
 action = open;
 };
 fromID = "01F3EB6B-398C-4718-8B55-0AD9ABC1080E";
 id = "Question_1093735057";
 name = Question;
 role = 0;
 roomId = 1093735057;
 seq = 34;
 toID = "__all";
 ts = "1548127505.902929";
 userId = "01F3EB6B-398C-4718-8B55-0AD9ABC1080E";
 write2DB = 1;
 };
 type = roompubmsg;
 };
 hasPub = 0;
 hintShow = 0;
 options =         (
 {
 hasChose = 0;
 },
 {
 hasChose = 0;
 },
 {
 hasChose = 1;
 },
 {
 hasChose = 1;
 }
 );
 owner =         {
 };
 page =         {
 data =             {
 };
 index = STATISTICS;
 };
 questionState = RUNNING;
 resizeInfo =         {
 height = "7.1";
 width = 12;
 };
 result =         (
 0,
 0,
 0,
 0
 );
 resultNum = 0;
 rightOptions =         (
 2,
 3
 );
 scale = 1;
 show = 1;
 sizeState = NORMAL;
 };
 */
@end

NS_ASSUME_NONNULL_END

//
//  TKAnswerSheetRecordView.h
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@protocol TKAnswerSheetRecordViewDelegate <NSObject>

//切换到详情
- (void)didPressDetailButton:(UIButton *)button;
//公布答案 结束答题
- (void)didPublishAnswer;

//获取答题卡状态
- (NSInteger)answerSheetType;

@end

@interface TKAnswerSheetRecordView : UIView

@property (nonatomic , weak) id<TKAnswerSheetRecordViewDelegate> delegate;

@property (strong , nonatomic) NSString *timeString;

//修改结束答题按钮状态
- (void)fixFinishButtonState:(BOOL)select;
//老师端显示公布答案 按钮
- (void)releaseButtonShow:(BOOL)state buttonSelected:(BOOL)selected;
- (void)refreshByAnswerSheetViewWithDic:(NSDictionary *)dic;
@end

NS_ASSUME_NONNULL_END

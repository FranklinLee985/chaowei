//
//  TKAnswerSheetSubmitView.h
//  EduClass
//
//  Created by maqihan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol TKAnswerSheetSubmitViewDelegate <NSObject>

//高度变化
- (void)didChangeHeight:(CGFloat)height;

@end

@interface TKAnswerSheetSubmitView : UIView

//可选范围
@property (assign , nonatomic) NSInteger optionsCount;
//正确答案 @1 @3 ...
@property (strong , nonatomic) NSMutableArray *answerArray;

@property (nonatomic , weak) id<TKAnswerSheetSubmitViewDelegate> delegate;


@end

NS_ASSUME_NONNULL_END

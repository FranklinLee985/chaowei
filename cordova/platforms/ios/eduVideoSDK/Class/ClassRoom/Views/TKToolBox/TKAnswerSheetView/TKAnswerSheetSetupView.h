//
//  TKAnswerSheetSetupView.h
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN
@protocol TKAnswerSheetSetupViewDelegate <NSObject>

//点击发布
- (void)didPressReleaseButton:(UIButton  *)button answer:(NSArray *)answers option:(NSArray *)options;
//高度变化
- (void)didChangeHeight:(CGFloat)height;

@end


@interface TKAnswerSheetSetupView : UIView

@property (nonatomic , weak) id<TKAnswerSheetSetupViewDelegate> delegate;

- (void)reset;

@end

NS_ASSUME_NONNULL_END

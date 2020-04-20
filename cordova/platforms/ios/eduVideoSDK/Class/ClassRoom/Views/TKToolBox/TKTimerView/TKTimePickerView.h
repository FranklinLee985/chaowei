//
//  TKTimePickerView.h
//  EduClass
//
//  Created by Evan on 2019/1/9.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKBaseBackgroundView.h"

NS_ASSUME_NONNULL_BEGIN

@protocol TimePickerDelegate <NSObject>

- (void)stratButtonActionWithMinute:(NSString *)minute sconed:(NSString *)sconed;

@end

@interface TKTimePickerView : UIView

@property (nonatomic, strong) UIPickerView *minutePickerView;// 分选择器
@property (nonatomic, strong) UIPickerView *secondPickerView;// 秒选择器

@property (nonatomic, copy) NSString *minuteString;// 分钟
@property (nonatomic, copy) NSString *secondString;// 秒


@property (nonatomic, weak) id<TimePickerDelegate> timePickerDelegate;


@end

NS_ASSUME_NONNULL_END

//
//  TKColorSelectorView.h
//  EduClass
//
//  Created by talkcloud on 2019/3/28.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKColorSelectorView : UIView

- (void) setCurrentSelectColor:(NSString *)curColor;
@property (nonatomic, copy) void(^chooseBackBlock)(NSString * colorStr);

@end

NS_ASSUME_NONNULL_END

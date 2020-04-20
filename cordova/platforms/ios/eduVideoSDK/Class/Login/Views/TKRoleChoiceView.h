//
//  TKRoleChoiceView.h
//  EduClass
//
//  Created by lyy on 2018/4/28.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol TKRoleChoiceDelegate<NSObject>
- (void)choiceCancel;
- (void)choiceRole:(NSString *)role;
@end

@interface TKRoleChoiceView : UIView

@property (nonatomic, weak) id<TKRoleChoiceDelegate> delegate;

@end

//
//  TKUserListToolView.h
//  EduClass
//
//  Created by lyy on 2018/7/3.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TKUserListToolViewDelegate <NSObject>
- (void)jumpPageNum:(int)pageNum;

@end

@interface TKUserListToolView : UIView

@property (nonatomic, weak) id<TKUserListToolViewDelegate> delegate;

@property (nonatomic, strong) UITextField *textField;


@end

//
//  TKCTUploadView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/19.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TKCTUploadView : UIView

@property (nonatomic, copy) void(^dismiss)();
//显示视图
- (void)showOnView:(UIButton *)view;
//隐藏视图
- (void)dissMissView;

@end

//
//  TKCTNetTipView.h
//  EduClass
//
//  Created by talkcloud on 2019/3/20.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKCTNetTipView : UIView

@property (nonatomic, strong) id netState;// 当前网络状态
@property (nonatomic, copy) void(^netStateBlock)(BOOL isShow);

- (void) changeDetailSignImage:(BOOL)isShow;
- (void) changeNetTipState:(id)state;

@end

NS_ASSUME_NONNULL_END

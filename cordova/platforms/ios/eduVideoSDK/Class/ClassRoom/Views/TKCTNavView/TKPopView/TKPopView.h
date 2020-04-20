//
//  TKPopView.h
//  EduClass
//
//  Created by maqihan on 2019/1/3.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, TKPopViewType) {
    //工具箱
    TKPopViewType_ToolBox = 0,
    //全体控制
    TKPopViewType_AllControl
};

@class TKPopView;

@protocol TKPopViewDelegate <NSObject>
@optional
- (void)popView:(TKPopView *)popView didSelectRowAtIndexPath:(NSString *)actionID;
//将要消失
- (void)popViewWillHidden:(TKPopView *)popView;
//已经消失
- (void)popViewWasHidden:(TKPopView *)popView;
@end

@interface TKPopView : UIView

@property (assign , nonatomic) TKPopViewType popViewType;

@property (weak , nonatomic) id<TKPopViewDelegate> delegate;

+ (TKPopView *)showPopViewAddedTo:(UIView *)containerView pointingAtView:(UIView *)targetView;
+ (BOOL)dismissForView:(UIView *)view;

@end

NS_ASSUME_NONNULL_END

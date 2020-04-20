//
//  JHUD.h
//  JHUDDemo
//
//  Created by 晋先森 on 16/7/11.
//  Copyright © 2016年 晋先森. All rights reserved.
//  https://github.com/jinxiansen
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, TKHUDLoadingType) {
    TKHUDLoadingTypeCircle           = 0,
    TKHUDLoadingTypeCircleJoin       = 1,
    TKHUDLoadingTypeDot              = 2,
    TKHUDLoadingTypeCustomAnimations = 3,
    TKHUDLoadingTypeGifImage         = 4,
    TKHUDLoadingTypeFailure          = 5,
};

@interface TKHUD : UIView

// When TKHUDLoadingTypeFailure, there will be a "refresh" button, and the method.
@property (nonatomic,copy)  void(^JHUDReloadButtonClickedBlock)(void);

@property (nonatomic,strong) UIView  *indicatorView;

@property (nonatomic,strong) UILabel  *messageLabel;

@property (nonatomic,strong) UIButton * refreshButton;

// Default color is [UIColor colorWithRed:0.1 green:0.1 blue:0.1 alpha:0.2]
@property (nonatomic,strong) UIColor  *indicatorBackGroundColor;

// Default color is  [UIColor colorWithRed:0.2 green:0.2 blue:0.2 alpha:0.6]
@property (nonatomic,strong) UIColor  *indicatorForegroundColor;

// Only TKHUDLoadingType is TKHUDLoadingTypeCustomAnimations or TKHUDLoadingTypeFailure, indicatorViewSize values can be changed.
@property (nonatomic,assign) CGSize indicatorViewSize;

// You need to read from the NSbundle GIF image and converted to NSData.
@property (nonatomic) NSData  *gifImageData;

// Only when TKHUDLoadingType is TKHUDLoadingTypeCustomAnimations will only take effect.
@property (nonatomic,strong) NSArray  *customAnimationImages;

@property (nonatomic,strong) UIImage  *customImage;

-(void)showAtView:(UIView *)view hudType:(TKHUDLoadingType)hudType;

-(void)hide;

-(void)hideAfterDelay:(NSTimeInterval)afterDelay;

+(void)showAtView:(UIView *)view message:(NSString *)message;

+(void)showAtView:(UIView *)view message:(NSString *)message hudType:(TKHUDLoadingType)hudType;

#pragma mark - Hide HUD

+(void)hideForView:(UIView *)view;

@end



@interface UIView (MainQueue)

-(void)dispatchMainQueue:(dispatch_block_t)block;

@end








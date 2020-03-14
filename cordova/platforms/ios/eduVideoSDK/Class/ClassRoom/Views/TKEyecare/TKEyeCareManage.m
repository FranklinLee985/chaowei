//
//  TKEyeCareManage.m
//  shade
//
//  Created by Evan on 2019/12/18.
//  Copyright © 2019 Evan. All rights reserved.
//

#import "TKEyeCareManage.h"
#import "TKSkinCoverWindow.h"
#import "TKBaseBasicAnimation.h"


#define TKAnimationDuration 0.25
#define TKKeyWindow  [[UIApplication sharedApplication] keyWindow]
#define TKScreenWidth [UIScreen mainScreen].bounds.size.width
#define TKScreenHeight [UIScreen mainScreen].bounds.size.height

@interface TKEyeCareManage ()

@property (nonatomic, strong) TKSkinCoverWindow *skinCoverWindow;
@property (nonatomic, strong) UIView *eyeMaskView;
// 之前的一个window
@property(nonatomic, weak) UIWindow *previousKeyWindow;
@property (nonatomic, strong) CALayer *coverLayer;

@end

@implementation TKEyeCareManage

// 遮罩层
- (UIView *)eyeMaskView {
    if (!_eyeMaskView) {
        _eyeMaskView = [[UIView alloc]init];
        self.eyeMaskView.frame = CGRectMake(0, 0, TKScreenWidth, TKScreenHeight);
        self.eyeMaskView.backgroundColor = [UIColor colorWithRed:255/255.0 green:122/255.0 blue:0/255.0 alpha:0.4];
        self.eyeMaskView.alpha = 0.4;
        self.eyeMaskView.userInteractionEnabled = NO;
    }
    return _eyeMaskView;
}
// NSUserDefaults存的key
static NSString * const kEyeCareModeStatus = @"kEyeCareModeStatus";
- (BOOL)queryEyeCareModeStatus {
    
    return [[NSUserDefaults standardUserDefaults] boolForKey:kEyeCareModeStatus];
}

//直接使用keyWindow
- (void)switchEyeCareMode:(BOOL)on {
    
    // 将状态写入设置
    [[NSUserDefaults standardUserDefaults] setBool:on forKey:kEyeCareModeStatus];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    if (on) {
        // 显示出来
//        [TKKeyWindow addSubview:self.eyeMaskView];
        _coverLayer = [[CALayer alloc] init];
        _coverLayer.frame = CGRectMake(0, 0, TKScreenWidth, TKScreenHeight);
        _coverLayer.backgroundColor =  [UIColor colorWithRed:255/255.0 green:122/255.0 blue:0/255.0 alpha:0.4].CGColor;
        _coverLayer.opacity = 0.4;
        [[UIApplication sharedApplication].keyWindow.layer addSublayer:_coverLayer];
    }else{
        // 隐藏skinCoverWindow
//        [self.eyeMaskView removeFromSuperview];
        [_coverLayer removeFromSuperlayer];
        
    }
    
}

- (void)switchEyeCareMode2:(BOOL)on {
    // 切换的具体实现
    // 将状态写入设置
    [[NSUserDefaults standardUserDefaults] setBool:on forKey:kEyeCareModeStatus];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    
    if (on) {
        // 记录上一个keywindow
        self.previousKeyWindow = [UIApplication sharedApplication].keyWindow;
        // 显示出来
        self.skinCoverWindow.hidden = NO;
        // 出现动画
        TKBaseBasicAnimation *opacityAnimation = [TKBaseBasicAnimation animationWithKeyPath:@"opacity"];
        opacityAnimation.fromValue = @(0);
        opacityAnimation.toValue = @(1);
        opacityAnimation.duration = TKAnimationDuration;
        opacityAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
        opacityAnimation.fillMode = kCAFillModeForwards;
        opacityAnimation.removedOnCompletion = NO;
        
        opacityAnimation.baseCABasicAnimationDidblock = ^(CAAnimation * _Nonnull anim, BOOL flag) {
            // 把key还给之前的window
            [self.previousKeyWindow makeKeyWindow];
        };
        
        [self.skinCoverWindow.layer addAnimation:opacityAnimation forKey:@"showAnimation"];
    }else{
        [self.previousKeyWindow makeKeyWindow];
        if ([[UIApplication sharedApplication].windows containsObject:self.skinCoverWindow]) {
            // 隐藏skinCoverWindow
            // 消失动画
            TKBaseBasicAnimation *opacityAnimation = [TKBaseBasicAnimation animationWithKeyPath:@"opacity"];
            opacityAnimation.fromValue = @(1);
            opacityAnimation.toValue = @(0);
            opacityAnimation.duration = TKAnimationDuration;
            opacityAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
            opacityAnimation.fillMode = kCAFillModeForwards;
            opacityAnimation.removedOnCompletion = NO;
            opacityAnimation.baseCABasicAnimationDidblock = ^(CAAnimation * _Nonnull anim, BOOL flag) {
                self.skinCoverWindow.hidden = YES;
                self.previousKeyWindow = nil;
            };
            
            [self.skinCoverWindow.layer addAnimation:opacityAnimation forKey:@"hideAnimation"];
        } else {
//            NSAssert(NO, @"Error:关闭护眼模式的时windows没有找到WESkinCoverWindow！！");
        }
    }
}



static NSInteger const kWeSkinCoverWindowLevel = 2099;
#pragma mark - setter & getter
- (TKSkinCoverWindow *)skinCoverWindow {

    if (!_skinCoverWindow) {
        // 给window赋值上初始的frame，在ios9之前如果不赋值系统默认认为是CGRectZero
        _skinCoverWindow = [[TKSkinCoverWindow alloc] initWithFrame:CGRectMake(-50, -50, 10000, 10000)];
        _skinCoverWindow.windowLevel = kWeSkinCoverWindowLevel;
        _skinCoverWindow.userInteractionEnabled = NO;
        // 添加到UIScreen
        [_skinCoverWindow makeKeyWindow];
    }
    return _skinCoverWindow;
}

static TKEyeCareManage *_sharedSingleton = nil;
+ (instancetype)sharedUtil {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //不能再使用alloc方法
        //因为已经重写了allocWithZone方法，所以这里要调用父类的分配空间的方法
        _sharedSingleton = [[super allocWithZone:NULL] init];
    });
    return _sharedSingleton;
}



@end

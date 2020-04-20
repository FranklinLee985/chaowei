//
//  TKDialView.m
//  EduClass
//
//  Created by Yi on 2019/1/7.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKDialView.h"
#import "TKEduSessionHandle.h"

//设置转圈的圈数
 static NSInteger const circleNum = 5;
#define ThemeKP(args) [@"TKToolsBox.TKDialView." stringByAppendingString:args]

@interface TKDialView()<CAAnimationDelegate>

@property (nonatomic, strong) UIButton 		*startBtn;
@property (nonatomic, strong) UIButton 		*closeBtn;
@property (nonatomic, strong) UIImageView   *dialView;
@property (nonatomic, strong) UIView *backView;

@property (nonatomic, assign) BOOL isAnimation;
@property (nonatomic, assign) NSInteger circleAngle;
@property (nonatomic, assign) NSInteger lastDeg;

@end

@implementation TKDialView

- (instancetype)init {
    
    self = [super init];
    if (self) {
        
        CGFloat viewHeigh = fmaxf(ScreenH / 3, 200);
        
        _backView = [[UIView alloc] init];
        [self addSubview:_backView];
        [_backView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.and.width.equalTo(@(viewHeigh));
            make.centerX.equalTo(self.mas_centerX);
            make.centerY.equalTo(self.mas_centerY);
            make.edges.equalTo(self);
        }];

        _dialView = [[UIImageView alloc] init];
        [_dialView setUserInteractionEnabled:YES];
        _dialView.sakura.image(ThemeKP(@"dial_dish_bg"));
        [_backView addSubview:_dialView];
        [_dialView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(_backView);
            make.height.equalTo(_backView);
            make.left.and.top.equalTo(@0);
        }];
        
        _startBtn = [UIButton buttonWithType:UIButtonTypeCustom];
//        _startBtn.sakura.image(ThemeKP(@"dial_start_btn"), UIControlStateNormal);
        _startBtn.sakura.image(([TKRoomManager instance].localUser.role == TKUserType_Patrol) ? ThemeKP(@"dial_start_btn_patrol") : ThemeKP(@"dial_start_btn"), UIControlStateNormal);
        [_startBtn addTarget:self action:@selector(startBtnAction) forControlEvents:UIControlEventTouchUpInside];
        [_backView addSubview:_startBtn];
        [_startBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(_dialView).multipliedBy(0.24);
            make.width.equalTo(_dialView).multipliedBy(0.19);
            make.center.equalTo(_dialView);
        }];

        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
            [_backView mas_updateConstraints:^(MASConstraintMaker *make) {

                make.width.equalTo(_backView.mas_height).offset(40);
            }];
            
            [_dialView mas_makeConstraints:^(MASConstraintMaker *make) {
                make.width.equalTo(_backView).offset(-40);
                
            }];
            // 关闭
            _closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
            _closeBtn.sakura.image(ThemeKP(@"dial_close_btn"), UIControlStateNormal);
            [_closeBtn addTarget:self action:@selector(closeBtnAction) forControlEvents:UIControlEventTouchUpInside];
            [_backView addSubview:_closeBtn];
            [_closeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
                make.width.height.equalTo(@40);
                make.top.equalTo(_dialView.mas_top);
                make.left.equalTo(_dialView.mas_right);
            }];

        }
    }
    return self;
}

- (void)startBtnAction {
    // 学生巡课不可i点击
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    if (_isAnimation) {
        return;
    }
    
    NSInteger lotteryPro = arc4random() % 6;

    if (lotteryPro < 1) {
        _circleAngle = 0;
    }else if (lotteryPro < 2){
        _circleAngle = 60;
    }else if (lotteryPro < 3){
        _circleAngle = 120;
    }else if (lotteryPro < 4){
        _circleAngle = 180;
    }else if (lotteryPro < 5){
        _circleAngle = 240;
    }else if (lotteryPro < 6){
        _circleAngle = 300;
    }
    CGFloat perAngle = M_PI/180.0;
    
    NSInteger deg 		  	= (360 * circleNum + _circleAngle);
    NSNumber *angle         = [NSNumber numberWithFloat: deg * perAngle];

    CABasicAnimation *rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
//    rotationAnimation.fromValue = [NSNumber numberWithInteger:_lastAngle];
    rotationAnimation.toValue = angle;
    rotationAnimation.duration = 3.0f;
    rotationAnimation.cumulative = YES;
    rotationAnimation.delegate = self;
    rotationAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
    rotationAnimation.fillMode = kCAFillModeForwards;
    rotationAnimation.removedOnCompletion = NO;
    
    [_dialView.layer addAnimation:rotationAnimation forKey:nil];
    
    NSString *str = [NSString stringWithFormat:@"rotate(%zddeg)", deg + _lastDeg];
    NSString *data= [TKUtil dictionaryToJSONString:@{@"rotationAngle":str,@"isShow":@(false)}];
    
    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:@"dial"
                                                         ID:@"dialMesg"
                                                         To:sTellAllExpectSender
                                                       Data:data
                                                       Save:YES
                                                 completion:nil];
    
    _lastDeg += 360 * circleNum;
}
- (void)closeBtnAction {

    [self removeFromSuperview];
   
}

- (void)startWithAngle:(id)angle {
    
    if (_isAnimation) {
        return;
    }
    
    NSInteger angleInt = [self parserAngle:angle];
    
    if (angleInt > 0) {
        
        CGFloat angle = angleInt % 360;
        NSNumber *num = [NSNumber numberWithFloat: (circleNum * 360 + angle) * (M_PI/180.0)];
        
        CABasicAnimation *rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
        rotationAnimation.toValue = num;
        rotationAnimation.duration = 3.0f;
        rotationAnimation.cumulative = YES;
        rotationAnimation.delegate = self;
        rotationAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
        rotationAnimation.fillMode = kCAFillModeForwards;
        rotationAnimation.removedOnCompletion = NO;
        
        [_dialView.layer addAnimation:rotationAnimation forKey:nil];
    }
    
}
- (void)setAngle:(NSString *)degStr {
    
    NSInteger deg = [self parserAngle:degStr] % 360;
    _dialView.transform = CGAffineTransformMakeRotation(deg * (M_PI/180.0));
    
}

#pragma mark - 动画代理
- (void)animationDidStart:(CAAnimation *)anim {
    
    _isAnimation = YES;
    _closeBtn.hidden = YES;
    

}
- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
   
    _isAnimation = NO;
    _closeBtn.hidden = NO;
    
}

- (NSInteger)parserAngle:(NSString *)angle {
    // 格式: rotate(17120deg)
    NSString *str = (NSString *)angle;
    str = [str stringByReplacingOccurrencesOfString:@"rotate(" withString:@""];
    str = [str stringByReplacingOccurrencesOfString:@"deg)" withString:@""];
    
    return [str integerValue];
}

- (void)willMoveToWindow:(UIWindow *)newWindow {

    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher && newWindow == nil) {
        
        [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:@"dial"
                                                             ID:@"dialMesg"
                                                             To:sTellAll
                                                           Data:@""
                                                     completion:nil];
    }
    
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

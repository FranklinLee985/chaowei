//
//  TKPasswordView.m
//  EduClass
//
//  Created by lyy on 2018/5/3.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKAlertView.h"
#import <QuartzCore/QuartzCore.h>
#import "TKEduSessionHandle.h"
#define ThemeKP(args) [@"Alert." stringByAppendingString:args]
#define kAlertWidth (IS_PAD ? 388.0f : 298.0f)
#define kAlertHeight (IS_PAD ? 284.0f : 186.0f)

@interface TKAlertView ()
{
    BOOL _leftLeave;
}

@property (nonatomic, strong) UIImageView *backImageView;//背景图
@property (nonatomic, strong) UIView *contentView;//内容区域
@property (nonatomic, strong) UILabel *alertTitleLabel;//标题label
@property (nonatomic, strong) UILabel *alertContentLabel;//内容label
@property (nonatomic, strong) UIButton *leftBtn;//左按钮
@property (nonatomic, strong) UIButton *rightBtn;//右按钮
@property (nonatomic, strong) UIView *inputView;//输入视图
@property (nonatomic, strong) UITextField *inputTextField;//输入框
@property (nonatomic, strong) UIButton *confimBtn;//确定按钮

@property (nonatomic, strong) UIView *backView;//黑色透明视图
@property (nonatomic, strong) UIButton *xButton;//关闭按钮

@end

@implementation TKAlertView

#define kTitleYOffset 15.0f
#define kTitleHeight 25.0f

#define kContentOffset 30.0f
#define kBetweenLabelOffset 20.0f

#define KButtonHeight (IS_PAD ? 40.0f : 30.0f)

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        //键盘弹起、收起的通知
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
        
        self.backImageView = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, kAlertWidth, kAlertHeight)];
        self.backImageView.sakura.image(ThemeKP(@"AlertBackImage"));
        self.backImageView.userInteractionEnabled = YES;
        [self addSubview:self.backImageView];
        
        
        _contentView = [[UIView alloc]init];
        _contentView.frame = CGRectMake(kAlertWidth*0.06, kAlertHeight*0.18, kAlertWidth*0.88, kAlertHeight*0.82);
        [self.backImageView addSubview:_contentView];
        
        
        self.alertTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, kAlertWidth, kAlertHeight*0.18)];
        self.alertTitleLabel.font = [UIFont systemFontOfSize:17.0f];
        self.alertTitleLabel.sakura.textColor(ThemeKP(@"titleColor_right"));
        [self addSubview:self.alertTitleLabel];
        
        self.alertTitleLabel.textAlignment = NSTextAlignmentCenter;
        
        self.xButton = ({
            
            CGFloat buttonHW = kAlertHeight*0.18;
            UIButton *xButton = [UIButton buttonWithType:UIButtonTypeCustom];
            xButton.sakura.image(ThemeKP(@"btn_close"),UIControlStateNormal);
            xButton.frame = CGRectMake(kAlertWidth - buttonHW - 2, 0, buttonHW, buttonHW);
            [self addSubview:xButton];
            [xButton addTarget:self action:@selector(dismissAlert) forControlEvents:UIControlEventTouchUpInside];
            xButton;
        });
    }
    return self;
}


- (id)initWithTitle:(NSString *)title
        contentText:(NSString *)content
       confirmTitle:(NSString *)confirmTitle
{
    if (self = [super init]) {
        
        
        self.alertTitleLabel.text = title;
        
        self.xButton.hidden = YES;
        
        self.alertContentLabel = ({//内容
            UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10,10,CGRectGetWidth(_contentView.frame)-20,(CGRectGetHeight(_contentView.frame)-20)/2.0)];
            label.numberOfLines = 0;
            label.textAlignment = NSTextAlignmentCenter;
            label.sakura.textColor(ThemeKP(@"userlistTextColor"));
            label.font = [UIFont systemFontOfSize:15.0f];
            [self.contentView addSubview:label];
            
            label.text = title;
            label.text = content;
            label;
        });
        
        self.confimBtn = ({//确定
            
            UIButton *confimBtn = [UIButton buttonWithType:(UIButtonTypeCustom)];
            confimBtn.sakura.backgroundImage(ThemeKP(@"ok_button_click"),UIControlStateNormal);
            confimBtn.titleLabel.font = TKFont(15);
            confimBtn.frame = CGRectMake((CGRectGetWidth(_contentView.frame)-KButtonHeight * 3.5)/2, CGRectGetHeight(_contentView.frame)*2.0/3.0, KButtonHeight * 3.5, KButtonHeight);
            
            [self.contentView addSubview:confimBtn];
            [confimBtn setTitle:confirmTitle forState:(UIControlStateNormal)];
            confimBtn.sakura.titleColor(ThemeKP(@"titleColor_right"),UIControlStateNormal);
            [confimBtn addTarget:self action:@selector(rightBtnClicked:) forControlEvents:(UIControlEventTouchUpInside)];
            
            confimBtn;
        });
    }
    return self;
}

- (id)initWithTitle:(NSString *)title
        contentText:(NSString *)content
          leftTitle:(NSString *)leftTitle
         rightTitle:(NSString *)rightTitle{
    
    if (self = [super init]) {
        
        self.alertTitleLabel.text = title;
        
        self.xButton.hidden = YES;
        
        self.alertContentLabel = ({//内容
            UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10,10,CGRectGetWidth(_contentView.frame)-20,(CGRectGetHeight(_contentView.frame)-20)/2.0)];
            label.numberOfLines = 0;
            label.textAlignment = NSTextAlignmentCenter;
            label.sakura.textColor(ThemeKP(@"userlistTextColor"));
            label.font = [UIFont systemFontOfSize:15.0f];
            [self.contentView addSubview:label];
            
            label.text = title;
            label.text = content;
            label;
            
        });
        
        
        CGFloat btnWidth = KButtonHeight * 3.5;
        CGFloat marginX = (CGRectGetWidth(_contentView.frame) - btnWidth*2)/3.0;
        
        self.leftBtn = ({//左按钮
            UIButton *confimBtn = [UIButton buttonWithType:(UIButtonTypeCustom)];
            confimBtn.sakura.backgroundImage(ThemeKP(@"cancel_button_click"),UIControlStateNormal);
            confimBtn.titleLabel.font = TKFont(15);
            confimBtn.frame = CGRectMake(marginX, CGRectGetHeight(_contentView.frame)*2.0/3.0, btnWidth, KButtonHeight);
            
            [self.contentView addSubview:confimBtn];
            [confimBtn setTitle:leftTitle forState:(UIControlStateNormal)];
            confimBtn.sakura.titleColor(ThemeKP(@"titleColor_left"),UIControlStateNormal);
            [confimBtn addTarget:self action:@selector(leftBtnClicked:) forControlEvents:(UIControlEventTouchUpInside)];
            confimBtn;
        });
        
        self.rightBtn = ({//右按钮
            UIButton *confimBtn = [UIButton buttonWithType:(UIButtonTypeCustom)];
            confimBtn.sakura.backgroundImage(ThemeKP(@"ok_button_click"),UIControlStateNormal);
            confimBtn.titleLabel.font = TKFont(15);
            confimBtn.frame = CGRectMake(CGRectGetMaxX(self.leftBtn.frame)+marginX, CGRectGetHeight(_contentView.frame)*2.0/3.0, btnWidth, KButtonHeight);
            
            [self.contentView addSubview:confimBtn];
            [confimBtn setTitle:rightTitle forState:(UIControlStateNormal)];
            confimBtn.sakura.titleColor(ThemeKP(@"titleColor_right"),UIControlStateNormal);
            [confimBtn addTarget:self action:@selector(rightBtnClicked:) forControlEvents:(UIControlEventTouchUpInside)];
            confimBtn;
        });
    }
    return self;
    
}

- (id)initForWarningWithTitle:(NSString *)title
                  contentText:(NSString *)content
                    leftTitle:(NSString *)leftTitle
                   rightTitle:(NSString *)rightTitle{
    
    self = [self initWithTitle:title contentText:content leftTitle:leftTitle rightTitle:rightTitle];
    if (self) {
        
        self.leftBtn.sakura.backgroundImage(ThemeKP(@"warn_button_left"),UIControlStateNormal);
        self.rightBtn.sakura.backgroundImage(ThemeKP(@"warn_button_right"),UIControlStateNormal);
    }
    return self;
}

- (id)initWithInputTitle:(NSString *)title
                   style:(TKRoomErrorCode)style
                   tplID:(NSString *)tplid
                userrole:(TKUserRoleType)userrole
            confirmTitle:(NSString *)confirmTitle{
    
    if (self = [super init]) {
        
        
        CGFloat contentLabelWidth = CGRectGetWidth(_contentView.frame);
        CGFloat contentLabelHeigh = CGRectGetHeight(_contentView.frame);
        
        self.alertContentLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, contentLabelWidth-20, fmaxf(contentLabelHeigh/3, 50))];
        self.alertContentLabel.numberOfLines = 0;
        self.alertContentLabel.textAlignment = self.alertTitleLabel.textAlignment = NSTextAlignmentCenter;
        self.alertContentLabel.font = [UIFont systemFontOfSize:15.0f];
        [self.contentView addSubview:self.alertContentLabel];
        
        self.alertTitleLabel.text = title;
        
        
        
        if (style == TKErrorCode_CheckRoom_NeedPassword) {
            
            self.alertContentLabel.hidden = NO;
            self.alertContentLabel.text = TKMTLocalized(@"Error.NeedPwd");
            self.alertContentLabel.sakura.textColor(ThemeKP(@"userlistTextColor"));
            
            // cartoon模版。 提示文字改变
            if (userrole == TKUserType_Teacher) self.alertContentLabel.text = TKMTLocalized(@"Error.NeedPwd.teacher");
            else if (userrole == TKUserType_Student) self.alertContentLabel.text = TKMTLocalized(@"Error.NeedPwd.student");
            else if (userrole == TKUserType_Patrol) self.alertContentLabel.text = TKMTLocalized(@"Error.NeedPwd.tourCourse");
            else self.alertContentLabel.text = TKMTLocalized(@"Error.NeedPwd");
        
        }else if(style == TKErrorCode_CheckRoom_PasswordError ||  style ==  TKErrorCode_CheckRoom_WrongPasswordForRole){
            
            self.alertContentLabel.hidden = YES;
            UIButton *btn = [[UIButton alloc]initWithFrame:CGRectMake(10, 0, contentLabelWidth-20, fmaxf(contentLabelHeigh/3, 50))];
            btn.backgroundColor = [UIColor clearColor];
            btn.sakura.image(ThemeKP(@"icon_error"),UIControlStateNormal);
            [self.contentView addSubview:btn];
            [btn setTitle: TKMTLocalized(@"Error.PwdError") forState:(UIControlStateNormal)];
            btn.sakura.titleColor(ThemeKP(@"alert_Error_TextColor"),UIControlStateNormal);
            btn.titleLabel.font = [UIFont systemFontOfSize:15.0f];
        }
        
        
        self.inputView = [[UIView alloc]initWithFrame:CGRectMake(10, CGRectGetHeight(_contentView.frame)/2-27, CGRectGetWidth(_contentView.frame)-20, 42)];
        self.inputView.sakura.backgroundColor(ThemeKP(@"alertInputBackColor"));
        self.inputView.sakura.alpha(ThemeKP(@"alertInputAlpha"));
        self.inputView.layer.masksToBounds = YES;
        self.inputView.layer.cornerRadius = 21;
        self.inputView.layer.borderWidth = 1;
        self.inputView.layer.borderColor =  [TKTheme cgColorWithPath:ThemeKP(@"alertBoardColor")];
        [self.contentView addSubview:self.inputView];
        
        self.inputTextField = [[UITextField alloc]initWithFrame:CGRectMake(20, CGRectGetHeight(_contentView.frame)/2-27, CGRectGetWidth(_contentView.frame)-40-20, 42)];
        self.inputTextField.secureTextEntry = YES;
        [self.contentView addSubview:self.inputTextField];
        self.inputTextField.font = TKFont(15);
        self.inputTextField.placeholder = TKMTLocalized(@"Prompt.inputPlaceholder");
        self.inputTextField.sakura.textColor(ThemeKP(@"alertInputTextColor"));
        self.inputTextField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:TKMTLocalized(@"Prompt.inputPlaceholder") attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14],NSForegroundColorAttributeName:[TKTheme colorWithPath:ThemeKP(@"alertInputPlaceholderColor")]}];
        [self.inputTextField becomeFirstResponder];
        
        UIButton *passwordBtn = [[UIButton alloc]initWithFrame:CGRectMake(CGRectGetMaxX(self.inputTextField.frame), CGRectGetMinY(self.inputTextField.frame)+(CGRectGetHeight(_inputView.frame)-17)/2.0, 21, 17)];
        passwordBtn.sakura.image(ThemeKP(@"icon_hide_pwd"),UIControlStateNormal);
        passwordBtn.sakura.image(ThemeKP(@"icon_show_pwd"),UIControlStateSelected);
        passwordBtn.selected = NO;
        [passwordBtn addTarget:self action:@selector(showPassWordClick:) forControlEvents:(UIControlEventTouchUpInside)];
        [self.contentView addSubview:passwordBtn];
        
        
        {//确定
            
            UIButton *confimBtn = [UIButton buttonWithType:(UIButtonTypeCustom)];
            confimBtn.sakura.backgroundImage(ThemeKP(@"ok_button_click"),UIControlStateNormal);
            confimBtn.titleLabel.font = TKFont(15);
            confimBtn.frame = CGRectMake((CGRectGetWidth(_contentView.frame)-KButtonHeight * 3.5)/2, (CGRectGetHeight(_contentView.frame)-CGRectGetMaxY(_inputView.frame)-KButtonHeight)/2 + CGRectGetMaxY(_inputView.frame), KButtonHeight * 3.5, KButtonHeight);
            [self.contentView addSubview:confimBtn];
            [confimBtn setTitle:confirmTitle forState:(UIControlStateNormal)];
            confimBtn.sakura.titleColor(ThemeKP(@"titleColor_right"),UIControlStateNormal);
            [confimBtn addTarget:self action:@selector(confimBtnClicked:) forControlEvents:(UIControlEventTouchUpInside)];
            
        }
        
        
    }
    return self;
}
//输入密码明文暗文切换
- (void)showPassWordClick:(UIButton *)sender{
    sender.selected = !sender.selected;
    
    if (sender.selected) { // 按下去了就是明文
        
        NSString *tempPwdStr = self.inputTextField.text;
        self.inputTextField.text = @""; // 这句代码可以防止切换的时候光标偏移
        self.inputTextField.secureTextEntry = NO;
        self.inputTextField.text = tempPwdStr;
        
    } else { // 暗文
        
        NSString *tempPwdStr = self.inputTextField.text;
        self.inputTextField.text = @"";
        self.inputTextField.secureTextEntry = YES;
        self.inputTextField.text = tempPwdStr;
    }
    
}

- (void)leftBtnClicked:(id)sender
{
    _leftLeave = YES;
    [self dismissAlert];
    if (self.lelftBlock) {
        self.lelftBlock();
    }
}
- (void)rightBtnClicked:(id)sender
{
    [self dismissAlert];
    if (self.rightBlock) {
        self.rightBlock();
    }
}
- (void)confimBtnClicked:(id)sender
{
    if ([self.inputTextField.text isEqualToString:@""] || !self.inputTextField.text) {
        return;
    }
    [self dismissAlert];
    if (self.confirmBlock) {
        self.confirmBlock(self.inputTextField.text);
    }
}
- (void)show
{
    UIViewController *topVC = [self appRootViewController];
    self.frame = CGRectMake((CGRectGetWidth(topVC.view.bounds) - kAlertWidth) * 0.5, - kAlertHeight - 30, kAlertWidth, kAlertHeight);
    [topVC.view addSubview:self];
}
- (void)showOnWindow {
    self.frame = CGRectMake((ScreenW - kAlertWidth) * 0.5, - kAlertHeight - 30, kAlertWidth, kAlertHeight);
    [TKMainWindow addSubview:self];
}

- (void)dismissAlert
{
    [self removeFromSuperview];
    if (self.dismissBlock) {
        self.dismissBlock();
    }
}

- (UIViewController *)appRootViewController
{
    UIViewController *appRootVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *topVC = appRootVC;
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}


- (void)removeFromSuperview
{
    [self.backView removeFromSuperview];
    self.backView = nil;
  
    [super removeFromSuperview];

}

- (void)willMoveToSuperview:(UIView *)newSuperview
{
    if (newSuperview == nil) {
        return;
    }
    UIViewController *topVC = [self appRootViewController];

    if (!self.backView) {
        self.backView = [[UIView alloc] initWithFrame:topVC.view.bounds];
        self.backView.backgroundColor = [UIColor blackColor];
        self.backView.alpha = 0.5f;
        self.backView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    }
    
    [topVC.view addSubview:self.backView];
    
    CGRect afterFrame = CGRectMake((CGRectGetWidth(topVC.view.bounds) - kAlertWidth) * 0.5, (CGRectGetHeight(topVC.view.bounds) - kAlertHeight) * 0.5, kAlertWidth, kAlertHeight);
    self.frame = afterFrame;
    [super willMoveToSuperview:newSuperview];
}

#pragma mark keyboard Notification
- (void)keyboardWillShow:(NSNotification*)notification
{
    if (![self.inputTextField isFirstResponder]) {
        return;
    }
    
    CGRect keyboardFrame = [[[notification userInfo] objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    //    keyboardFrame = [self convertRect:keyboardFrame fromView:nil];

  
    CGFloat keyboardHeight = keyboardFrame.size.height;
   
    
    double duration = ([[[notification userInfo] objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue]);
    void (^aBlock)() = ^void() {
       self.transform = CGAffineTransformMakeTranslation(0, - keyboardHeight/2);
    };
    
    [UIView animateWithDuration:duration delay:0.0 options:(UIViewAnimationOptionCurveEaseInOut | UIViewAnimationOptionTransitionNone) animations:aBlock completion:nil];
    
   
    
}


- (void)keyboardWillHide:(NSNotification *)notification
{
    
    double duration = [[[notification userInfo] objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
   
    void (^aBlock)() = ^void() {
        
        self.transform = CGAffineTransformIdentity;
     
        
    };
    
    [UIView animateWithDuration:duration delay:0.0 options:(UIViewAnimationOptionCurveEaseInOut | UIViewAnimationOptionTransitionNone) animations:aBlock completion:nil];
    
    
    
}


@end



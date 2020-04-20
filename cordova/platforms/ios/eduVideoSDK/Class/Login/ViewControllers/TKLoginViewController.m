//
//  TKLoginViewController.m
//  EduClass
//
//  Created by lyy on 2018/4/17.
//  Copyright © 2018年 beijing. All rights reserved.
//

#import "TKLoginViewController.h"
#import "TKTextFieldLimitManager.h"
#import "TKLoginInputView.h"
#import "TKIPhoneTypeString.h"
#import <AudioToolbox/AudioToolbox.h>
#import "TKEyeCareManage.h"

//输入框的高度
#define inputHeigt 42
//输入框之间的间距
#define inputMarginTop 21
#define loginButtonHeight 51



@interface TKLoginViewController ()<TKLoginChoiceRoleDelegate,TKLoginInputViewDelegate>

@property (nonatomic, strong) UIImageView *logoImageView;

@property (nonatomic, strong) TKLoginInputView *roomidView;//课堂号
@property (nonatomic, strong) TKLoginInputView *nicknameView;//昵称
@property (nonatomic, strong) TKLoginInputView *roleView;//角色选择器
@property (nonatomic, strong) UIButton *loginButton;//登录按钮
@property (nonatomic, strong) UILabel *versionLabel;//版本号

@property (assign, nonatomic) NSInteger role;
@property (strong, nonatomic) NSString *defaultServer;//默认服务

@property (nonatomic, assign) NSInteger inputWidth;//输入框的宽度

@property (nonatomic, assign) BOOL netSwitchable;
@property (nonatomic, strong) UIImageView *backgroundImageView;//底层背景图

@end

@implementation TKLoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // 初始化背景图
    self.backgroundImageView = ({
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        imageView.image = [UIImage imageNamed:@"tk_login_bg"];
        imageView.contentMode =  UIViewContentModeScaleAspectFill;
        imageView.userInteractionEnabled = YES;
        
        [self.view addSubview:imageView];
        
        imageView;
    });
    
    
    // 初始化控件
    [self.view addSubview:self.logoImageView];
    [self.view addSubview:self.roomidView];
    [self.view addSubview:self.nicknameView];
    [self.view addSubview:self.roleView];
    [self.view addSubview:self.loginButton];
    [self.view addSubview:self.versionLabel];
    
    //设置默认显示的内容
    [self setupData];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [TXSakuraManager shiftSakuraWithName:TKBlackSkin type:TXSakuraTypeMainBundle];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self startEyeCareMode];
#if DEBUG
    self.netSwitchable = YES;
#endif
}

// 退出教室 取消护眼模式
- (void)startEyeCareMode {
    // 护眼模式配置
    if ([[TKEyeCareManage sharedUtil] queryEyeCareModeStatus]) {
        [[TKEyeCareManage sharedUtil] switchEyeCareMode:NO];
    }
}

-(BOOL)shouldAutorotate
{
    return NO;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation{
    return UIInterfaceOrientationPortrait;
}
/////////////////////////////////////////添加切换环境按钮////////////////////////////////////////////
//#if DEBUG
//
//- (void)viewWillDisappear:(BOOL)animated
//{
//    [super viewWillDisappear:animated];
//
//    self.netSwitchable = NO;
//}
//
//- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event
//{
//    if (!self.netSwitchable) {
//        return;
//    }
//
//    //摇动结束
//    if (event.subtype == UIEventSubtypeMotionShake)
//    {
//        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
//        if (motion == UIEventSubtypeMotionShake) {
//            UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"切换环境" message:nil preferredStyle:UIAlertControllerStyleAlert];
//            UIAlertAction *demoAction = [UIAlertAction actionWithTitle:@"Demo" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//
//                sHost = @"demo.talk-cloud.net";
//            }];
//            UIAlertAction *neiwangAction = [UIAlertAction actionWithTitle:@"内网" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//                sHost = @"global.talk-cloud.neiwang";
//            }];
//            UIAlertAction *globalAction = [UIAlertAction actionWithTitle:@"Global" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//                sHost = @"global.talk-cloud.net";
//            }];
//            [alertVC addAction:demoAction];
//            [alertVC addAction:neiwangAction];
//            [alertVC addAction:globalAction];
//
//            [self presentViewController:alertVC animated:YES completion:nil];
//        }
//    }
//}
//#endif
/////////////////////////////////////////添加切换环境按钮////////////////////////////////////////////
- (void)viewWillLayoutSubviews
{
    [super viewWillLayoutSubviews];
    // 保证执行一次 以后可进行横屏loginView 优化 主要是iPhone 或者区分iPad 和 iPhone。
    if (self.inputWidth) {
        return;
    }
    
    self.inputWidth = 323;
    
    /**感觉 1 和 2 重复了啊*/
    //1
    if ([[TKIPhoneTypeString checkIPhoneType] isEqualToString:@"iPhone 5"]
        || [[TKIPhoneTypeString checkIPhoneType] isEqualToString:@"iPhone 5C"]
        || [[TKIPhoneTypeString checkIPhoneType] isEqualToString:@"iPhone 5S"]) {
        
        self.inputWidth = 323 * [UIScreen mainScreen].bounds.size.width / 375;
    }
    //2
    //此处主要适配5/5s的显示
    if((self.inputWidth-CGRectGetWidth(self.view.frame))>0 && (self.inputWidth-CGRectGetWidth(self.view.frame)) <= 40 ){
        self.inputWidth = CGRectGetWidth(self.view.frame)-40;
    }
    
    self.backgroundImageView.frame = self.view.bounds;
    self.logoImageView.frame  = CGRectMake(0, (CGRectGetHeight(self.view.frame)-453)*0.4, CGRectGetWidth(self.view.frame), 103);
    
    self.roomidView.frame = CGRectMake((CGRectGetWidth(self.view.frame)-self.inputWidth)/2, CGRectGetMaxY(self.logoImageView.frame)+inputHeigt*2, self.inputWidth, inputHeigt);
    self.nicknameView.frame = CGRectMake(CGRectGetMinX(self.roomidView.frame),CGRectGetMaxY(self.roomidView.frame)+20 , CGRectGetWidth(self.roomidView.frame), inputHeigt);
    self.roleView.frame = CGRectMake(CGRectGetMinX(self.nicknameView.frame),CGRectGetMaxY(self.nicknameView.frame)+20 , CGRectGetWidth(self.nicknameView.frame), inputHeigt);
    
    self.loginButton.frame    = CGRectMake(CGRectGetMinX(self.roleView.frame),CGRectGetMaxY(self.roleView.frame)+inputHeigt , CGRectGetWidth(self.roleView.frame), loginButtonHeight);
    self.versionLabel.frame   = CGRectMake(0, [TKUtil isiPhoneX]?CGRectGetHeight(self.view.frame) -40-17:CGRectGetHeight(self.view.frame) -40, CGRectGetWidth(self.view.frame), 40);
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

#pragma mark - 点击登录

- (void)loginButtonAction:(id)sender
{
    [self.view endEditing:YES];
    [_roleView choiceCancel];
    // 回放测试
//    NSString *url = @"enterroomnew://replay?host=cna.talk-cloud.net&domain=test&serial=1836335066&type=0&path=global.talk-cloud.net:8081/8351bcbe-52c4-4dc7-afea-a5190bd6ea0e-1836335066/&tplId=default&skinId=black&skinResource=&colourid=tigerlily&position=51&companyidentify=1";
//    [[TKEduClassRoom shareInstance] joinRoomWithUrl:url];
//    return;
    
    /**信息检查*/
    if (!self.nicknameView.inputView.text.length
        || [TKUtil isEmpty:self.nicknameView.inputView.text]){
        //昵称不能为空
        NSString *content =  TKMTLocalized(@"Prompt.nicknameNotNull");
        TKAlertView *alert = [[TKAlertView alloc] initWithTitle:TKMTLocalized(@"Prompt.prompt") contentText:content confirmTitle:TKMTLocalized(@"Prompt.Know")];
        [alert show];
        return;
    }
    
    if (!self.roomidView.inputView.text.length
        || [TKUtil isEmpty:self.roomidView.inputView.text]) {
        //教室号不能为空
        NSString *content =  TKMTLocalized(@"Prompt.RoomIDNotNull");
        TKAlertView *alert = [[TKAlertView alloc] initWithTitle:TKMTLocalized(@"Prompt.prompt") contentText:content confirmTitle:TKMTLocalized(@"Prompt.Know")];
        [alert show];
        return;
    }
    
    if (self.role == TKUserType_Student) {
        // 学生被T 3分钟内不能登录
        id idTime = [[NSUserDefaults standardUserDefaults] objectForKey:TKKickTime];
        if (idTime && [idTime isKindOfClass:NSDate.class]) {
            NSDate *time = (NSDate *)idTime;
            NSDate *curTime = [NSDate date];
            NSTimeInterval delta = [curTime timeIntervalSinceDate:time]; // 计算出相差多少秒
            
            if (delta < 60 * 3) {
                
                NSString *room = [[NSUserDefaults standardUserDefaults] objectForKey:TKKickRoom];
                if ([room isEqualToString:self.roomidView.inputView.text]) {
                    
                    NSString *content =  TKMTLocalized(@"Prompt.kick");
                    TKAlertView *alert = [[TKAlertView alloc] initWithTitle:TKMTLocalized(@"Prompt.prompt") contentText:content confirmTitle:TKMTLocalized(@"Prompt.Know")];
                    [alert show];
                    return;
                }
            }else {
                [[NSUserDefaults standardUserDefaults] removeObjectForKey:TKKickTime];
            }
        }
    }
    
    /**登陆教室*/
    if ([TKUtil isDomain:sHost] == YES) {
        NSArray *array = [sHost componentsSeparatedByString:@"."];
        self.defaultServer = [NSString stringWithFormat:@"%@", array[0]];
    } else {
        self.defaultServer = @"global";
    }
    
    NSString *tRoomIDString = [self.roomidView.inputView.text stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    NSMutableDictionary *parameters = @{
                                 @"serial"    :tRoomIDString,
                                 @"host"      :sHost,
                                 @"port"      :sPort,
                                 @"nickname"  :self.nicknameView.inputView.text,
                                 @"userrole"  :@(self.role),
                                 @"server"    :self.defaultServer,
                                 @"clientType":@(3),
                                 }.mutableCopy;
    
#if DEBUG
    
#ifdef SERVER_ClassID
    [parameters setValue:SERVER_ClassID forKey:@"serial"];
#endif
    
#ifdef Class_NickName
    [parameters setValue:Class_NickName forKey:@"nickname"];
#endif
    
#ifdef SERVER_ClassPwd
//    if (self.role != 2) {
    
        [parameters setValue:SERVER_ClassPwd forKey:@"password"];
//    }
#endif
    
#endif
    
    
    [[TKEduClassRoom shareInstance] joinRoomWithParamDic:parameters ViewController:self Delegate:self isFromWeb:NO];
}

#pragma mark - Private

- (void)setupData
{
    NSString *meetignID =[[NSUserDefaults standardUserDefaults] objectForKey:@"meetingID"];
    if (meetignID != nil && [meetignID isKindOfClass:[NSString class]])
    {
        _roomidView.inputView.text = meetignID;
    }
    NSString *nickName =[[NSUserDefaults standardUserDefaults] objectForKey:@"nickName"];
    if (nickName != nil && [nickName isKindOfClass:[NSString class]])
    {
        _nicknameView.inputView.text = nickName;
    }
    NSNumber  *role = [[NSUserDefaults standardUserDefaults] objectForKey:@"userrole"];
    //0-老师 ,1-助教，2-学生 4-寻课A
    if (role != nil && [role isKindOfClass:[NSNumber class]])
    {
        _role = [role intValue];
        switch ([role intValue]) {
            case 0:
                _roleView.text = TKMTLocalized(@"Role.Teacher");
                break;
            case 2:
                _roleView.text = TKMTLocalized(@"Role.Student");
                break;
            case 4:
                _roleView.text = TKMTLocalized(@"Role.Patrol");
                break;
            default:
                break;
        }
        
        
    }else{
        _role = 2;
        _roleView.text = TKMTLocalized(@"Role.Student");
    }
}

#pragma mark - 自定义代理
- (void)clickChoiceRole {
    [_nicknameView.inputView resignFirstResponder];
    [_roomidView.inputView resignFirstResponder];
}

-(void)choiceRole:(int)role
{
    _role = role;
}

- (BOOL)loginTextField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    
    if ([string isEqualToString:@"\n"]) {
        [self.view endEditing:YES];
        return  NO;
    }
    // 验证教室号 是否是数字
    if ([textField isEqual:_roomidView.inputView]) {
        
        bool tIsChange = [TKUtil validateNumber:string];
        if (!tIsChange) {
            [TKUtil showMessage:TKMTLocalized(@"Prompt.onlyNumber")];
            
        }
        return tIsChange;
    }
    else {
        return YES;
    }
}

#pragma mark - TKEduEnterClassRoomDelegate

- (void)onEnterRoomFailed:(int)result Description:(NSString*)desc{
    TKLog(@"-----onEnterRoomFailed");
}

- (void)onKitout:(EKickOutReason)reason{
    TKLog(@"-----onKitout");
}

- (void)joinRoomComplete{
    TKLog(@"-----joinRoomComplete");
    //成功进入教室 本地做记录
    NSString *tRoomIDString = [self.roomidView.inputView.text stringByReplacingOccurrencesOfString:@" " withString:@""];
    [[NSUserDefaults standardUserDefaults] setObject:tRoomIDString forKey:@"meetingID"];
    [[NSUserDefaults standardUserDefaults] setObject:self.nicknameView.inputView.text forKey:@"nickName"];
    [[NSUserDefaults standardUserDefaults] setObject:@(self.role) forKey:@"userrole"];
}

- (void)leftRoomComplete{
    TKLog(@"-----leftRoomComplete");
}

- (void)onClassBegin{
    TKLog(@"-----onClassBegin");
}

- (void)onClassDismiss{
    TKLog(@"-----onClassDismiss");
}

- (void)onCameraDidOpenError{
    TKLog(@"-----onCameraDidOpenError");
}

#pragma mark - Getter
- (void)beginInputTextField {
    
    [_roleView choiceCancel];
}
- (UIImageView *)logoImageView
{
    if (!_logoImageView) {
        _logoImageView = [[UIImageView alloc] init];
        _logoImageView.image = [UIImage imageNamed:@"tk_login_logo"];
        _logoImageView.contentMode = UIViewContentModeScaleAspectFit;
    }
    return _logoImageView;
}

- (UIButton *)loginButton
{
    if (!_loginButton) {
        _loginButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
        [_loginButton setBackgroundImage:[UIImage imageNamed:@"tk_bt_join_room_bg"] forState:UIControlStateNormal];
        [_loginButton setBackgroundImage:[UIImage imageNamed:@"tk_bt_join_room_bg"] forState:UIControlStateSelected];
        [_loginButton setTitle:TKMTLocalized(@"Login.EnterRoom") forState:UIControlStateNormal];
        [_loginButton setTitleColor:[TKHelperUtil colorWithHexColorString:@"ffffff"] forState:UIControlStateNormal];
        [_loginButton addTarget:self action:@selector(loginButtonAction:) forControlEvents:(UIControlEventTouchUpInside)];
    }
    return _loginButton;
}

- (TKLoginInputView *)roomidView
{
    if (!_roomidView) {
        _roomidView = [[TKLoginInputView alloc] initWithFrame:CGRectZero showText:nil placeholderText:TKMTLocalized(@"Label.roomPlaceholder") isShow:NO setImageName:@"tk_icon_room_number"];
        _roomidView.inputDelegate = self;
        _roomidView.inputView.keyboardType = UIKeyboardTypeNumberPad;
    }
    return _roomidView;
}

- (TKLoginInputView *)nicknameView
{
    if (!_nicknameView) {
        _nicknameView = [[TKLoginInputView alloc] initWithFrame:CGRectZero showText:nil placeholderText:TKMTLocalized(@"Label.nicknamePlaceholder") isShow:NO setImageName:@"tk_login_nick_name_icon"];
        _nicknameView.inputDelegate = self;
        //限制昵称为24个字符
        [[TKTextFieldLimitManager sharedManager] limitTextField:_nicknameView.inputView bytesLength:24 handler:nil];
    }
    return _nicknameView;
}

- (TKLoginInputView *)roleView
{
    if (!_roleView) {
        _roleView = [[TKLoginInputView alloc] initWithFrame:CGRectZero showText:@"学生" placeholderText:nil isShow:YES setImageName:@"tk_login_right_icon"];
        _roleView.choiceRoleDelegate = self;
    }
    return _roleView;
}

- (UILabel *)versionLabel
{
    if (!_versionLabel) {
        _versionLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _versionLabel.textAlignment = NSTextAlignmentCenter;
        _versionLabel.textColor = [TKHelperUtil colorWithHexColorString:@"8C8E97"];
        _versionLabel.text =  [NSString stringWithFormat:@"v%@",[[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]];
        _versionLabel.font = [UIFont systemFontOfSize:13];
    }
    return _versionLabel;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

//
//  ViewController.m
//  TKUIDEMO
//
//  Created by 李合意 on 2019/10/14.
//  Copyright © 2019 李合意. All rights reserved.
//

#import "ViewController.h"
#import <TKUISDK/TKUISDK.h>
#import <TKRoomSDK/TKRoomSDK.h>

@interface ViewController ()<TKEduRoomDelegate>

@property (weak, nonatomic) IBOutlet UITextField *roomID;
@property (weak, nonatomic) IBOutlet UITextField *pwd;

@property (weak, nonatomic) IBOutlet UITextField *roleNum;

@property (weak, nonatomic) IBOutlet UISwitch *switchServer;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
//    NSMutableDictionary *parameters = @{
//                                 @"serial"    :@"",
//                                 @"host"      :@"",
//                                 @"port"      :sPort,
//                                 @"nickname"  :self.nicknameView.inputView.text,
//                                 @"userrole"  :@(self.role),
//                                 @"server"    :self.defaultServer,
//                                 @"clientType":@(3)
//
//                                 }.mutableCopy;
    
    
}

- (IBAction)loginAction:(id)sender {
    
#pragma mark - 回放
    /*
     格式:
     {
     colourid = purple;
     domain = gx1;
     host = "democn.talk-cloud.net";
     layout = 0;
     path = "demo.talk-cloud.net:8081/AC278A9B-BDC9-45F5-B90C-**********-1238514125/";
     serial = 1238514125;
     server = democn;
     skinId = "beyond_default";
     skinResource = "";
     tplId = beyond;
     type = 0;
     userid = "48194";
     }
     */


 
    // 精简
//    NSDictionary *param = @{
//        // 房间号
//        @"serial" : @"471450199",
//        // 客户端类型 iOS = 3
//        @"clientType" : @3,
//        // 地址
//        @"host" : @"global.talk-cloud.net",
//        // 回放路径
//        @"path" :
////            @"http://global.talk-cloud.net/replay/471450199/10032/1572834643624/",
//            @"global.talk-cloud.net:8081/a5e93f8c-7f54-4550-a232-f9adc8103812-471450199/",
//        // 回放字段
//        @"playback": @1,
//        //
//        @"type":@3,
//    };
//    [[TKEduClassManager shareInstance] joinPlaybackRoomWithParamDic:param
//                                                     ViewController:self
//                                                           Delegate:self
//                                                          isFromWeb:NO];
//
//
//    return;
#pragma mark - 通过链接进入
//    NSString *url = @"enterroomnew://replay?host=cna.talk-cloud.net&domain=bjmlk&serial=1119310518&type=3&path=global.talk-cloud.net:8081/6495e27f-d2f9-47d9-8f25-998df19e8d8f-1119310518/&tplId=beyond&skinId=beyond_default&skinResource=&colourid=purple&layout=1&companyidentify=1";
//
//    [[TKEduClassManager shareInstance] joinRoomWithUrl:url];
#pragma mark - 直播

    NSDictionary *param = @ {
        // 房间号
        @"serial":@"1716470343",
        // 密码
        @"password": @(1),
        // 用户角色 老师(0) 助教(1) 学生(2)
        @"userrole": @(2),
        // 用户昵称
        @"nickname": @"肉丸",
        // 地址
        @"host": @"global.talk-cloud.net",
        // 主机
        @"server": @"global",
        // 端口号
        @"port": @"443",
        // 客户端类型 iOS = 3
        @"clientType": @"3",
        // (可选)用户ID的key值如果不传用户ID这个字段，sdk会自动生成用户ID(不可传空字符)
        @"userid":@"abc123",
    };
    [[TKEduClassManager shareInstance] joinRoomWithParamDic:param
                                             ViewController:self
                                                   Delegate:self
                                                  isFromWeb:NO];
 
}

/**
 进入房间失败
 
 @param result 错误码 详情看 TKRoomSDK -> TKRoomDefines ->TKRoomErrorCode 结构体
 
 @param desc 失败的原因描述
 */
- (void)onEnterRoomFailed:(int)result Description:(NSString*)desc
{
    
}

/**
 被踢回调

 @param reason 0:被老师踢出（暂时无） 1：重复登录
 */
- (void)onKitout:(int)reason
{
    
}

/**
 进入课堂成功后的回调
 */
- (void)joinRoomComplete
{
    
}

/**
 离开课堂成功后的回调
 */
- (void)leftRoomComplete
{
    
}

/**
 课堂开始的回调
 */
- (void)onClassBegin
{
    
}

/**
 课堂结束的回调
 */
- (void)onClassDismiss
{
    
}

/**
 摄像头打开失败回调
 */
- (void)onCameraDidOpenError
{
    
}
@end

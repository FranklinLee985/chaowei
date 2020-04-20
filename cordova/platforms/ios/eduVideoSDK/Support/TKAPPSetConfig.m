//
//  TKAPPSetConfig.m
//  EduClass
//
//  Created by maqihan on 2018/11/30.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import "TKAPPSetConfig.h"
#import "LTUpdate.h"
#import <HockeySDK/HockeySDK.h>

// 崩溃日志
//#define CRASH_REPORT_ADDRESS    @"https://global.talk-cloud.com/update/public"
#define CRASH_REPORT_ADDRESS    @"http://doc.talk-cloud.net/update/reports/?C=M;O=D"

#define CRASH_IDENTIFIER        @"bfe4ad0dd2c941c3b3ce0453a0c6aa65"


@interface TKAPPSetConfig ()<BITHockeyManagerDelegate>

@end

@implementation TKAPPSetConfig

+ (instancetype)shareInstance {
    static TKAPPSetConfig *_sharedManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedManager = [[TKAPPSetConfig alloc] init];
    });
    return _sharedManager;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        
    }
    return self;
}

- (void)setupAPPWithBuglyID:(NSString *)buglyId
{
    //设置主题
    NSArray * sakuraArray = @[TKCartoonSkin,TKBlackSkin,TKOrangeSkin];
    [TXSakuraManager registerLocalSakuraWithNames:sakuraArray];
    
    //切换到默认主题
    NSString *name = [TXSakuraManager getSakuraCurrentName];
    NSInteger type = [TXSakuraManager getSakuraCurrentType];
    if (![sakuraArray containsObject:name]) {
        name = TKCartoonSkin;// 防止 新版本更换皮肤名字后 用户打开app闪退
        type = TXSakuraTypeMainBundle;
    }
    [TXSakuraManager shiftSakuraWithName:name type:type];
    
    //注册HockeySDK
    
    [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:CRASH_IDENTIFIER
                                                           delegate:self];
    [BITHockeyManager sharedHockeyManager].logLevel = BITLogLevelWarning;
    [BITHockeyManager sharedHockeyManager].serverURL = CRASH_REPORT_ADDRESS;
    
    [[BITHockeyManager sharedHockeyManager] startManager];
    [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
    [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus: BITCrashManagerStatusAutoSend];
    [self checkForUpdate];
    if ([self didCrashInLastSessionOnStartup])
    {
        TKLog(@"------Crash detected.");
    }
    
    /*  上边定义了 自动发送报告BITCrashManagerStatusAutoSend,也用下边的代码可以自定义弹出
    //自定义crash弹出框
    [[BITHockeyManager sharedHockeyManager].crashManager setAlertViewHandler:^{
        TKAlertView *alert = [[TKAlertView alloc]initWithTitle:TKMTLocalized(@"Prompt.ExceptionReport") contentText:TKMTLocalized(@"Prompt.AbnormalityReport") leftTitle:TKMTLocalized(@"Prompt.Cancel") rightTitle:TKMTLocalized(@"Button.send")];
        alert.rightBlock = ^{
            
            BITCrashMetaData *metaData = [BITCrashMetaData new];
            [metaData setUserProvidedDescription:@"TalkCloud"];
            [[BITHockeyManager sharedHockeyManager].crashManager handleUserInput:BITCrashManagerUserInputSend withUserProvidedMetaData:metaData];
        };
        alert.lelftBlock = ^{
            BITCrashMetaData *metaData = [BITCrashMetaData new];
            [metaData setUserProvidedDescription:@"TalkCloud"];
            [[BITHockeyManager sharedHockeyManager].crashManager handleUserInput:BITCrashManagerUserInputDontSend withUserProvidedMetaData:metaData];
        };
        [alert show];
        
    }];
	*/
}

- (void)checkForUpdate//检测版本升级
{
    //    NSString *version = [TKUtil getTKVersion];
    NSString *version = @"2018072900";
    
    if ([[BITHockeyManager sharedHockeyManager] appEnvironment])//YES代表appstore安装
    {
        LTUpdate *upd = (LTUpdate*)[LTUpdate shared];
        [upd clearSkippedVersion];
        [upd update:100 complete:^(BOOL isNewVersionAvailable, LTUpdateVersionDetails *__unused versionDetails) {
            if (isNewVersionAvailable) {
                
                [TKEduNetManager getupdateinfoWithaHost:sHost aPort:sPort Version:version Type:10 Complete:^int(id  _Nullable response) {
                    if ([response[@"result"]intValue] != -1) {
                        int updateflag = [response[@"updateflag"]intValue];
                        
                        switch (updateflag) {
                            case 0://不强制
                                break;
                            case 1://强制升级
                                [upd alertForcedToUpdateLatestVersion:LTUpdateOption];
                                break;
                            case 2://有条件升级
                                [upd alertLatestVersion:LTUpdateOption | LTUpdateSkip];
                                break;
                            default:
                                break;
                        }
                    }else{
                        [upd alertLatestVersion:LTUpdateOption | LTUpdateSkip];
                    }
                    return 0;
                } aNetError:^int(id  _Nullable response) {
                    
                    [upd alertLatestVersion:LTUpdateOption | LTUpdateSkip];
                    
                    return -1;
                }];
                
            }
            
        }];
    }
    
}

- (BOOL)didCrashInLastSessionOnStartup
{
    return ([[BITHockeyManager sharedHockeyManager].crashManager didCrashInLastSession] &&
            [[BITHockeyManager sharedHockeyManager].crashManager timeIntervalCrashInLastSessionOccurred] < 5);
}

#pragma mark - BITCrashManagerDelegate
//crashManagerWillCancelSendingCrashReport
- (void)crashManagerWillCancelSendingCrashReport:(BITCrashManager *)__unused crashManager
{
    if ([self didCrashInLastSessionOnStartup])
    {
        
    }
}

//crashManagerWillCancelSendingCrashReport
- (void)crashManager:(BITCrashManager *)__unused crashManager didFailWithError:(NSError *)__unused error
{
    if ([self didCrashInLastSessionOnStartup])
    {
        
    }
}

//crashManagerWillCancelSendingCrashReport
- (void)crashManagerDidFinishSendingCrashReport:(BITCrashManager *)__unused crashManager
{
    if ([self didCrashInLastSessionOnStartup])
    {
        
    }
}

@end

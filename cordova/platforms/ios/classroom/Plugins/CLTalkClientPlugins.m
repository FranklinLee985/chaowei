//
//  CLTalkClientPlugins.m
//  classroom
//
//  Created by 乐 y on 2020/3/14.
//

#import "CLTalkClientPlugins.h"
#import "TKLoginViewController.h"
#import <MBProgressHUD/MBProgressHUD.h>
#import <Cordova/CDV.h>
#import <UIKit/UIKit.h>
#import <TKRoomSDK/TKRoomSDK.h>

@implementation CLTalkClientPlugins

- (void)showToast:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    NSString *message = command.arguments.firstObject;
    if ([message isKindOfClass:[NSString class]]) {
        if (message.length > 0) {
            UIView *view = [UIApplication sharedApplication].windows.lastObject.rootViewController.view;
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
            hud.mode = MBProgressHUDModeText;
            hud.label.text = message;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
                [hud hideAnimated:YES];
            });
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)joinRoom:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    NSString *message = command.arguments.firstObject;
    if ([message isKindOfClass:[NSString class]]) {
        if (message.length > 0 && [NSURL URLWithString: message]) {
            [[TKEduClassRoom shareInstance] joinRoomWithUrl:message];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
@end

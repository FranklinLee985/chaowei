/********* talkplus.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <UIKit/UIViewController.h>
#import <UIKit/UIKit.h>
//#import "../../../TKEduClass 3.3.7/EduClass/eduVideoSDK/handle/TKEduClassRoom.h"

//.//cordova/platforms/ios/classroom/Plugins/cordova-plugin-talkplus/talkplus.m
//.//cordova/platforms/ios/TKEduClass 3.3.7/EduClass/eduVideoSDK/handle/TKEduClassRoom.h

@interface talkplus : CDVPlugin {
  // Member variables go here.
}

- (void)showToast:(CDVInvokedUrlCommand*)command;
- (void)joinRoom:(CDVInvokedUrlCommand*)command;
@end

@implementation talkplus

- (void)showToast:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
	//show alert
	
	UIAlertView *alert=[[UIAlertView alloc] initWithTitle:echo message:@"test" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"other", nil];
	[alert show];
	
	//
	
	
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)joinRoom:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
//	 [[TKEduClassRoom shareInstance] joinRoomWithUrl:echo];
	
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end

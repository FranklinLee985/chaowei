//
//  AppDelegate.m
//  TKUIDEMO
//
//  Created by 李合意 on 2019/10/14.
//  Copyright © 2019 李合意. All rights reserved.
//

#import "AppDelegate.h"
#import <TKUISDK/TKUISDK.h>
@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    NSBundle *bd = [NSBundle bundleWithPath: [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent: @"TKResources.bundle"]];
    NSString *str = [bd localizedStringForKey:@"Prompt.prompt" value:@"" table:nil];
    NSLog(@"%@",str);
    NSLog(@"%@",[[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] firstObject]);
     [[TKAPPSetConfig shareInstance] setupAPPWithAppID:@""];
    
    return YES;
}


//#pragma mark - UISceneSession lifecycle
//
//
//- (UISceneConfiguration *)application:(UIApplication *)application configurationForConnectingSceneSession:(UISceneSession *)connectingSceneSession options:(UISceneConnectionOptions *)options {
//    // Called when a new scene session is being created.
//    // Use this method to select a configuration to create the new scene with.
//    return [[UISceneConfiguration alloc] initWithName:@"Default Configuration" sessionRole:connectingSceneSession.role];
//}
//
//
//- (void)application:(UIApplication *)application didDiscardSceneSessions:(NSSet<UISceneSession *> *)sceneSessions {
//    // Called when the user discards a scene session.
//    // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
//    // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
//}


@end

//
//  TKToolsResponderView.h
//  EduClass
//
//  Created by talkcloud on 2019/1/8.
//  Copyright © 2019年 talkcloud. All rights reserved.
//	抢答器

#import <UIKit/UIKit.h>
#import "TKToolBoxBaseView.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKToolsResponderView : TKToolBoxBaseView

- (void) receiveShowResponderViewWith:(NSDictionary *) dict;
- (void) receiveResponderUser:(NSDictionary *)dict peerid:(NSString *)peerid;

@end

NS_ASSUME_NONNULL_END

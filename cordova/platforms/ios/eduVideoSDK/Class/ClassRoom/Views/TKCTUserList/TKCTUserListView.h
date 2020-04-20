//
//  TKCTUserListView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTBaseView.h"

@interface TKCTUserListView : TKCTBaseView

- (id)initWithFrame:(CGRect)frame userList:(NSString *)userListController;
- (void)dismissAlert;

@end

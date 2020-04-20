//
//  TKCTUserListTableViewCell.h
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TKCTUserListTableViewCellDelegate <NSObject>

-(void)removeblock;

@end

@interface TKCTUserListTableViewCell : UITableViewCell
@property (nonatomic, weak) id<TKCTUserListTableViewCellDelegate> delegate;

@property (nonatomic, strong) TKRoomUser *roomUser;
//-(void)configaration:(id)aModel withFileListType:(FileListType)aFileListType isClassBegin:(BOOL)isClassBegin;

@end

//
//  TKCTFileListTableViewCell.h
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TKMediaDocModel,TKDocmentDocModel,RoomUser;


@protocol listCTProtocol <NSObject>
- (void)watchFile:(UIButton *)aButton aIndexPath:(NSIndexPath *)aIndexPath withModel:(id)model;
- (void)deleteFile:(UIButton *)aButton aIndexPath:(NSIndexPath *)aIndexPath withModel:(id)model;

@end

@interface TKCTFileListTableViewCell : UITableViewCell

@property (weak, nonatomic) id<listCTProtocol> delegate;

@property (strong, nonatomic) UIButton *watchBtn;
@property (strong, nonatomic) UIButton *deleteBtn;

@property (nonatomic,assign)  TKFileListType  iFileListType;
@property (nonatomic, strong) NSString *text;
@property (strong, nonatomic) TKMediaDocModel *iMediaDocModel;
@property (strong, nonatomic) TKDocmentDocModel *iDocmentDocModel;
@property (strong, nonatomic) RoomUser *iRoomUserModel;
@property (strong, nonatomic) NSIndexPath *iIndexPath;

@property (nonatomic, assign) BOOL hiddenDeleteBtn;

-(void)configaration:(id)aModel withFileListType:(TKFileListType)aFileListType isClassBegin:(BOOL)isClassBegin;

@end


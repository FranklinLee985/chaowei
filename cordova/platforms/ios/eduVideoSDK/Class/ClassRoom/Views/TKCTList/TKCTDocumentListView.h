//
//  TKCTDocumentListView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/16.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "TKEduSessionHandle.h"
@class TKOneToMoreRoomController;

@protocol TKCTDocumentListDelegate <NSObject>
- (void)watchFile;
- (void)deleteFile;
@end

@interface TKCTDocumentListView : UIView<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, weak) id<TKCTDocumentListDelegate> documentDelegate;

@property (nonatomic,weak)TKOneToMoreRoomController*  delegate;

@property (nonatomic, assign) BOOL isShow;

-(instancetype)initWithFrame:(CGRect)frame;

-(void)show:(TKFileListType)aFileListType isClassBegin:(BOOL)isClassBegin;

-(void)hide;

- (void)reloadData;

@end



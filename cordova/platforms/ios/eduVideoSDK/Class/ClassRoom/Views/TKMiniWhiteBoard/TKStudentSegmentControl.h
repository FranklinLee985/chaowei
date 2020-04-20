//
//  TKStudentsIndicator.h
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/7.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKStudentSegmentObject.h"

NS_ASSUME_NONNULL_BEGIN
@protocol TKStudentSegmentControlDelegate <NSObject>


- (void)didSelectStudent:(TKStudentSegmentObject *)student;

@end

@interface TKStudentSegmentControl : UIView

@property (nonatomic, weak) id <TKStudentSegmentControlDelegate>delegate;

@property (nonatomic, strong) NSMutableArray <TKStudentSegmentObject *> *students;
@property (nonatomic, strong) NSMutableArray <UIButton *>* buttons;

- (instancetype)initWithDelegate:(id<TKStudentSegmentControlDelegate>)delegate;

- (BOOL)addStudent:(TKStudentSegmentObject *)student;

- (void)removeStudent:(TKStudentSegmentObject *)student;

- (void)chooseStudent:(TKStudentSegmentObject *)student;

- (void)resetUI;

@end

NS_ASSUME_NONNULL_END

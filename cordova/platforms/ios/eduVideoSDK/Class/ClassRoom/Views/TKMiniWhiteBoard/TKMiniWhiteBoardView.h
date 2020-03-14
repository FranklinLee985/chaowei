//
//  TKMiniWhiteBoardView.h
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/7.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//
//  小白板分辨率跟原生白板的分辨率相同
//

#import <UIKit/UIKit.h>
#import "TKStudentSegmentControl.h"
#import "TKStudentSegmentObject.h"
#import "TKBrushSelectorView.h"
#import <TKWhiteBoard/TKDrawView.h>


typedef NS_ENUM(NSInteger, TKMiniWhiteBoardState) {
    TKMiniWhiteBoardStatePrepareing,        //准备
    TKMiniWhiteBoardStateDispenseed,        //分发
    TKMiniWhiteBoardStateAgainDispenseed,   //再次分发
    TKMiniWhiteBoardStateRecycle,           //回收
    
};

NS_ASSUME_NONNULL_BEGIN

@interface TKMiniWhiteBoardView : UIView<TKStudentSegmentControlDelegate, TKBrushSelectorViewDelegate, TKDrawViewDelegate>

@property (nonatomic, strong) TKStudentSegmentControl *segmentCotnrol;  //小白板分页控制器
@property (nonatomic, assign) TKMiniWhiteBoardState state;              //小白板状态
@property (nonatomic, strong) TKDrawView *tkDrawView;                   //小白板涂鸦层
@property (nonatomic, strong) TKStudentSegmentObject *choosedStudent;   //小白板选中的学生
@property (nonatomic, strong) TKBrushSelectorView *selectorView;        //小白板调色盘
@property (nonatomic, assign, getter=isBigRoom) BOOL bigRoom;           //小白板是否是大并发教室

//切换状态
- (void)switchStates:(TKMiniWhiteBoardState)state;

//增加学生画布
- (BOOL)addStudent:(TKStudentSegmentObject *)student;

//移除学生画布
- (void)removeStudent:(TKStudentSegmentObject *)student;

//(信令)选中学生画布
- (void)chooseStudent:(TKStudentSegmentObject *)student;

//小白板隐藏时清理数据
- (void)clear;

/**
 处理小白板信令

 @param dictionary 信令数据
 @param isDel 添加或删除信令
 */
- (void)handleSignal:(NSDictionary *)dictionary isDel:(BOOL)isDel;

@end

NS_ASSUME_NONNULL_END

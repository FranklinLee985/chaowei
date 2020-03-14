//
//  TKMediaMarkView.h
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/17.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//	视频标注

#import <UIKit/UIKit.h>
#import <TKWhiteBoard/TKDrawView.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKMediaMarkView : UIView <TKDrawViewDelegate>

@property (nonatomic, strong) TKDrawView *tkDrawView;
@property (nonatomic, strong) UIButton *exitBtn;
@property (nonatomic, strong) UIButton *eraserBtn;
@property (nonatomic, strong) NSNumber *videoRatio;
@property (nonatomic, strong) NSMutableArray *recoveryArray;

//接收处理截屏信令
- (void)handleSignal:(NSDictionary *)dictionary isDel:(BOOL)isDel;

//恢复数据
- (void)recoveryMediaMark;

- (void)clear;

@end

NS_ASSUME_NONNULL_END

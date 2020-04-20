//
//  TKScreenShotFactory.h
//  EduClass
//
//  Created by Yibo on 2019/4/8.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TKScreenShotView.h"
#import "TKBrushToolView.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKScreenShotFactory : NSObject

@property (nonatomic, strong) NSMutableDictionary<NSString*, TKScreenShotView *> *screenshotDic;// 存放截屏字典<fileid:info>
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) TKBrushToolView *brush;
@property (nonatomic, assign) BOOL canDraw;
@property (nonatomic, assign) TKBrushToolType toolType;
+ (instancetype)sharedFactory;

- (void)setDrawType:(TKDrawType)type color:(NSString *)hexColor progress:(float)progress;

//接收处理截屏信令
+ (void)handleSignal:(NSDictionary *)dictionary isDel:(BOOL)isDel;

// 教室截屏 桌面截屏
+ (void)captureImgWithParam:(NSDictionary *)param msgName:(NSString *) msgName delete:(BOOL)state;

//下课清理数据
+ (void)clearAfterClass;
@end

NS_ASSUME_NONNULL_END

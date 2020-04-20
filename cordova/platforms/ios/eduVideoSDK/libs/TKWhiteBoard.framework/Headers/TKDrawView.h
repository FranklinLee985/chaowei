//
//  TKDrawView.h
//  TKWhiteBoard
//
//  Created by Yibo on 2019/4/1.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKWhiteBoardEnum.h"
@class DrawView;

NS_ASSUME_NONNULL_BEGIN

@protocol TKDrawViewDelegate <NSObject>


/**
 涂鸦数据回调
 
 @param fileid 所属文件id
 @param shapeID 涂鸦id
 @param shapeData 涂鸦数据
 */
- (void)addSharpWithFileID:(NSString *)fileid shapeID:(NSString *)shapeID shapeData:(NSData *)shapeData;

@end

@interface TKDrawView : UIView
@property (nonatomic, strong) DrawView *drawView;                       //涂鸦显示层
@property (nonatomic, strong) DrawView *rtDrawView;                     //实时绘制层
@property (nonatomic, copy) NSString *fileid;                           //涂鸦所属文件id
@property (nonatomic, assign) int pageid;                               //涂鸦所属文件页码
@property (nonatomic, assign) float iFontScale;                         //涂鸦比例，当前涂鸦frame.width / 960
@property (nonatomic, weak) id <TKDrawViewDelegate>delegate;

- (instancetype)initWithDelegate:(nullable id<TKDrawViewDelegate>)delegate;


/**
 是否已经设置了画笔
 
 @return 画笔状态
 */
- (BOOL)hasDraw;

/**
 设置画笔
 
 @param drawType 画笔类型
 @param hexColor 16进制画笔颜色
 @param progress 画笔粗细，0.05f~1.0f
 */
- (void)setDrawType:(TKDrawType)drawType
           hexColor:(NSString *)hexColor
           progress:(float)progress;



/**
 翻到涂鸦所属文件及页码
 
 @param fileID 文件id
 @param pageID 页码数
 @param refresh 是否立即刷新
 */
- (void)switchToFileID:(NSString *)fileID
                pageID:(int)pageID
    refreshImmediately:(BOOL)refresh;


/**
 添加一笔涂鸦
 
 @param data 涂鸦数据字典
 @param refresh 是否立即刷新
 */
- (void)addDrawData:(NSDictionary *)data
 refreshImmediately:(BOOL)refresh;


/**
 撤销一笔涂鸦
 */
- (void)undoDraw;


/**
 清空涂鸦
 
 @param clearID 清空id
 */
- (void)clearDraw:(NSString *)clearID;


/**
 清理一页数据
 
 @param fileID 文件id
 @param pageNum 页码
 */
- (void)clearOnePageWithFileID:(NSString *)fileID pageNum:(int)pageNum;

/**
 设置画布工作模式
 
 @param mode 模式枚举：工作模式or观察模式
 */
- (void)setWorkMode:(TKWorkMode)mode;


/**
 下课清理数据
 */
- (void)clearDataAfterClass;

@end

NS_ASSUME_NONNULL_END

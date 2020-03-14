//
//  TKSortTool.h
//  XPWGLQIANTAI
//
//  Created by  杭州信配iOS开发 on 2017/4/27.
//  Copyright © 2017年  杭州信配iOS开发. All rights reserved.
//

#import <Foundation/Foundation.h>
/*
 * TXSectionBlock 返回组信息代码块
 */
typedef void (^TXSectionBlock) (id sectionContent);
/*
 * TXSectionBlock 返回排序后的代码块
 */
typedef void (^TXSortTheValueOfBlock) (id returnValue);

@interface TKSortTool : NSObject

+(instancetype )shareInstance;

/**
 按名称排序

 @param array 数组
 @param fileListType 存储数据的类型
 @param sortWay 排序方式
 @param sectionBlock 返回的区块内容
 @param sortTheValueOfBlock 返回的数据内容
 
 */
+ (void)sortByNameWithArray:(NSArray*)array
               fileListType:(TKFileListType)fileListType
                   sortWay:(TKSortFileType)sortWay
               sectionBlock:(TXSectionBlock)sectionBlock
        sortTheValueOfBlock:(TXSortTheValueOfBlock)sortTheValueOfBlock;

/**
 按类型排序

 @param array 数组
 @param sortWay 存储数据的类型
 @param sectionBlock 返回的区块内容
 @param sortTheValueOfBlock 返回的数据内容
 */
+ (void)sortByTypeWithArray:(NSArray *)array
               fileListType:(TKFileListType)fileListType
                    sortWay:(TKSortFileType)sortWay
               sectionBlock:(TXSectionBlock)sectionBlock
        sortTheValueOfBlock:(TXSortTheValueOfBlock)sortTheValueOfBlock;

/**
 按时间排序
 
 @param array 数组
 @param sortWay 存储数据的类型
 @param sectionBlock 返回的区块内容
 @param sortTheValueOfBlock 返回的数据内容
 */
+ (void)sortByTimeWithArray:(NSArray *)array
               fileListType:(TKFileListType)fileListType
                    sortWay:(TKSortFileType)sortWay
               sectionBlock:(TXSectionBlock)sectionBlock
        sortTheValueOfBlock:(TXSortTheValueOfBlock)sortTheValueOfBlock;



+ (NSInteger)sortByNameWithArray:(NSArray<TKRoomUser *> *)array peerID:(NSString *)peerID tExist:(BOOL)isExist;


/**
 按照peerid排序

 @param array 视频数组
 @param sortTheValueOfBlock 回调
 */
+ (void)sortByPeerIDWithArray:(NSArray *)array
          sortTheValueOfBlock:(TXSortTheValueOfBlock)sortTheValueOfBlock;

@end

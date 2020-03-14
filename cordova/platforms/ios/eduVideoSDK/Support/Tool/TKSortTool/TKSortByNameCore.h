//
//  TXSortByNameCore.h
//  TXSortByNameToolDemo
//
//  Created by  杭州信配iOS开发 on 2017/4/27.
//  Copyright © 2017年 张天雄. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TKSortByNameCore : NSObject
/*对一组汉字名称,按照首个汉字的首字母进行分组排序
 * sourceArray  待分组排序的汉字集合
 * isKey 是否包含排序后的字母,例如:A
 * return 返回一个排序分组后的数组
 */
+ (NSArray*)sortDataByFirstLetterWithArray:(NSArray*)sourceArray isIncludeKeys:(BOOL)isKey comparisonResult:(NSComparisonResult)comparisonResult;



@end

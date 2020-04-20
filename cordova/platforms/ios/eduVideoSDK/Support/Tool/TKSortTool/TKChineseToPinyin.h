//
//  TXChineseToPinyin.h
//  TXSortByNameToolDemo
//
//  Created by  杭州信配iOS开发 on 2017/4/27.
//  Copyright © 2017年 张天雄. All rights reserved.
//

#import <Foundation/Foundation.h>
@interface TKChineseToPinyin : NSObject
+ (NSString *) pinyinFromChiniseString:(NSString *)string;
+ (char) sortSectionTitle:(NSString *)string;
char TKpinyinFirstLetter(unsigned short hanzi);
@end

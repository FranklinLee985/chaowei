//
//  TKHelperUtil.h
//  EduClass
//
//  Created by lyy on 2018/4/27.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@interface TKHelperUtil : NSObject

//根据设备类型返回设备头像
+ (NSString *)returnDeviceImageName:(NSString *)devicetype;

//根据设备类型返回udp状态下的设备头像
+ (NSString *)returnUDPDeviceImageName:(NSString *)devicetype;

//返回播放mp3所需要的动画图片（数组中存放的是UIImage)
+ (NSArray *)mp3PlayGif;

//返回播放mp4所需要的动画图片（数组中存放的是UIImage)
+ (NSArray *)mp4PlayGif;

//返回文档、媒体列表icon
+(NSString *)docmentOrMediaImage:(NSString*)aType;

+ (void)phontLibraryAction;

// 根据色值匹配图片
+ (NSString *)imageNameWithPrimaryColor:(NSString *)PrimaryColor;

/**
 *  字符串转色值
 */
+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString;
+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString alpha:(float)alpha;

/**
 UIColor 转 6位十六进制字符串 #开头
 */
+ (NSString *) hexColorStringWithColor:(UIColor *)color;

/**
 计算字符串长度

 */
+ (CGSize)sizeForString:(NSString *)string font:(UIFont *)font size:(CGSize)size;

/**
 计算当前总页数
 */
+ (int)returnTotalPageNum:(NSInteger)totalPage showPage:(NSInteger)pageNum;

/**
 获取课堂视频分辨率
 */
+ (CGFloat)returnClassRoomDpi;

/**
 是否是url
 */
+ (BOOL)isURL:(NSString *)text;

/**
 设置视频格式
 */
+ (void)setVideoFormat;

/**
 拉伸图片

 @param imageName 名称
 @return 图片
 */
+ (UIImage *)resizableImageWithImageName:(NSString *)imageName;

/**
 获取cpu
 */
+ (float)GetCpuUsage;

/**
 获取当前内存使用
 */
+ (CGFloat)GetCurrentTaskUsedMemory;


// 菊花
+ (void)HUDShowMessage:(NSString*)msg addedToView:(UIView*)view;
// 菊花
+ (void)HUDShowMessage:(NSString*)msg addedToView:(UIView*)view showTime:(CGFloat)time;
@end

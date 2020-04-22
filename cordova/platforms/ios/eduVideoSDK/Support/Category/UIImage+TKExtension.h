//
//  UIImage+TKExtension.h
//  EduClass
//
//  Created by lyy on 2018/5/4.
//  Copyright © 2018年 talkcloud. All rights reserved.
//


#import <UIKit/UIKit.h>

@interface UIImage (TKExtension)

///*
// *加载图片
// */
//+ (UIImage *)imageWithName:(NSString *)name;


/*
 *返回一张自由拉伸的图片
 */
+ (UIImage *)tkResizedImageWithName:(NSString *)name;

/*
 *返回旋转后的图片
 */
+ (UIImage *)tkFixOrientation:(UIImage *)aImage;
@end

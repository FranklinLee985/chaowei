//
//  TKUtil.h
//  emmnew
//
//  Created by mac on 14-3-27.
//
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonCryptor.h>
#import <Security/Security.h>

#define EMMIOSVER ([[[[UIDevice currentDevice] systemVersion] substringToIndex:1] intValue])

#ifndef GETDICVALUE
#define GETDICVALUE(Dic, key) \
([[Dic objectForKey:key] isKindOfClass:[NSNull class]] ? nil : [Dic objectForKey:key])
#endif


//typedef NS_OPTIONS(NSUInteger, UIRectCorner) {
//    UIRectCornerTopLeft     = 1 << 0,//左上角
//    UIRectCornerTopRight    = 1 << 1,//右上角
//    UIRectCornerBottomLeft  = 1 << 2,//左下角
//    UIRectCornerBottomRight = 1 << 3,//右下角
//    UIRectCornerAllCorners  = ~0UL   //全部
//};


@interface TKUtil : NSObject

+ (instancetype)shareInstance;


#pragma mark 是否是iPhoneX
+(BOOL)isiPhoneX;

+(BOOL)IS_IPHONEX;

//播放礼物动画的音频
- (void)playVoiceWithFileURL:(NSString *)fileUrl;
+ (NSString*)fullPath:(NSString*)shortPath;

+ (NSString*)GetDicString:(NSDictionary*)dic Key:(NSString*)key;
+ (int)GetDicInt:(NSDictionary*)dic Key:(NSString*)key;

//#define GETLEFT(v)      (v.frame.origin.x)
//#define GETRIGHT(v)     (v.frame.origin.x + v.frame.size.width)
//#define GETTOP(v)       (v.frame.origin.y)
//#define GETBOTTOM(v)    (v.frame.origin.y + v.frame.size.height)
+ (void)setLeft:(UIView*)v To:(CGFloat)x;
+ (void)setTop:(UIView*)v To:(CGFloat)y;
+ (void)setRight:(UIView*)v To:(CGFloat)right;
+ (void)setBottom:(UIView*)v To:(CGFloat)bottom;
+ (void)setWidth:(UIView*)v To:(CGFloat)width;
+ (void)setHeight:(UIView*)v To:(CGFloat)height;
+ (void)setCenter:(UIView*)v ToFrame:(CGRect)frame;
+(UIView *)setCornerForView:(UIView * )aView;
#pragma mark 获取是否是媒体文件
+(BOOL)getIsMedia:(NSString*)filetype;
+(BOOL)isVideo:(NSString *)filetype;
#pragma mark 获取字符串长度
+ (CGFloat) widthForTextString:(NSString *)tStr height:(CGFloat)tHeight fontSize:(CGFloat)tSize;

#pragma mark 加密
+(NSString*)TripleDES:(NSString*)plainText encryptOrDecrypt:(CCOperation)encryptOrDecrypt;
+(NSMutableDictionary *)decodeUrl:(NSString *)aDecodeUrl;
+(NSMutableDictionary *)decodeParam:(NSString *)aParamString;
+(NSString *) md5HexDigest:(NSString *)aString;


#pragma mark 将时间戳转换成字符串
+(NSString *)timestampToFormatString:(NSTimeInterval)ts;
#pragma mark 获取当前时间
+(NSString *)currentTime;
+(NSString *)currentTimeToSeconds;
#pragma mark 只能是数字
+ (BOOL)validateNumber:(NSString*)number;
#pragma mark globle
+ (void)showMessage:(NSString *)message ;
+ (void)showClassEndMessage:(NSString *)message;
#pragma mark 几位数
+(NSInteger)numberBit:(NSInteger)aNumber;
#pragma mark 判断当前语言
+(BOOL)isEnglishLanguage;
#pragma mark 获取文件url
+(NSString*)absolutefileUrl:(NSString*)fileUrl webIp:(NSString*)webIp webPort:(NSString*)webPort;
#pragma mark 将字典转换为JSON字符串
+(NSString *)dictionaryToJSONString:(NSDictionary *)dic;
#pragma mark 检测语言
+(NSString*)getCurrentLanguage;
+(BOOL)isSimplifiedChinese;
#pragma mark 是否符合设备
+(bool)deviceisConform;
#pragma mark 是否是域名
+(BOOL)isDomain:(NSString *)host;
+(NSString*)optString:(NSDictionary*)dic Key:(NSObject*)key;
+(NSNumber *)getNSNumberFromDic:(NSDictionary*)dic Key:(NSObject*)key;
+(NSDictionary *)getDictionaryFromDic:(NSDictionary*)dic Key:(NSObject*)key;
+(NSInteger)getIntegerValueFromDic:(NSDictionary*)dic Key:(NSObject*)key;
+(BOOL)getBOOValueFromDic:(NSDictionary*)dic Key:(NSObject*)key;
+(id)getValueFromDic:(NSDictionary*)dic Key:(NSObject*)key Class:(Class)cls;
#pragma mark 根据控件高度动态设置字体大小
+(int)getCurrentFontSize:(CGSize)size withString:(NSString *)string;
/**
 获取版本号

 @return 版本号
 */
+(NSString *)getTKVersion;
+(NSString *)getTKBuild;
/**
 判断是否全是空格

 @param str 字符串
 @return 是否是空格
 */
+ (BOOL)isEmpty:(NSString *)str;

/**
 获取年月日时分秒

 @return 返回时间
 */
+(NSString *)getCurrentDateTime;

/**
 获取当前时间

 @return YYYY-MM-dd HH:mm:ss
 */
+(NSString*)getCurrentTimes;
/**
 获取当前时间
 
 @return HH:mm
 */
+(NSString *)getCurrentHoursAndMinutes:(NSString *)time;

/**
 获取当前时间戳

 @return 时间戳
 */
+(NSTimeInterval)getNowTimeTimestamp;

/**
 获取时间

 @param time 时间
 @return 年月日
 */
+(NSString *)getData:(NSString *)time;

+ (id) processDictionaryIsNSNull:(id)obj;
@end


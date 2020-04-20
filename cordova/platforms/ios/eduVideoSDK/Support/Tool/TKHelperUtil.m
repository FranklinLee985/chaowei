//
//  TKHelperUtil.m
//  EduClass
//
//  Created by lyy on 2018/4/27.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKHelperUtil.h"
#import <Photos/Photos.h>
#import "sys/utsname.h"
#import "TKEduSessionHandle.h"
#import <mach/mach.h>
#import <sys/sysctl.h>
#import <MBProgressHUD.h>

#define DeviceTheme(args) [@"DeviceType.Default." stringByAppendingString:args]
#define DeviceUDPTheme(args) [@"DeviceType.UdpImpassability." stringByAppendingString:args]

#define DocumentTypeTheme(args) [@"TKDocumentListView." stringByAppendingString:args]

@implementation TKHelperUtil

+ (NSString *)returnDeviceImageName:(NSString *)devicetype{
    
    if (!devicetype || devicetype.length == 0) {
        devicetype = @"DeviceUnknown";
    }
    
    return DeviceTheme(devicetype);
    
}
+ (NSString *)returnUDPDeviceImageName:(NSString *)devicetype{
    
    if (!devicetype || devicetype.length == 0) {
        devicetype = @"DeviceUnknown";
    }
    
    NSString *de = [NSString stringWithFormat:@"%@_udp",devicetype];
    
    return DeviceUDPTheme(de);
    
}

+ (NSArray *)mp3PlayGif{
    
    NSMutableArray *array  = [NSMutableArray array];
    for (int i = 0; i<40; i++) {
        if (i<10) {
            NSString *str = [TKTheme stringWithPath:@"ClassRoom.TKMediaView.mp3Loading"];
            
            UIImage *img = [UIImage imageNamed:[NSString stringWithFormat:@"%@%d",str,i]];
            [array addObject:img];
        }else{
            NSString *str = [TKTheme stringWithPath:@"ClassRoom.TKMediaView.mp3Loading2"];
            UIImage *img = [UIImage imageNamed:[NSString stringWithFormat:@"%@%d",str,i]];
            [array addObject:img];
        }
    }
    return [NSArray arrayWithArray:array];
}
+ (NSArray *)mp4PlayGif{
    
    NSMutableArray *array  = [NSMutableArray array];
    for (int i = 0; i<38; i++) {
        if (i<10) {
            NSString *str = [TKTheme stringWithPath:@"ClassRoom.TKMediaView.mp4Loading"];
            
            UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"%@%d",str,i]];
           
            
            [array addObject:image];
        }else{
            NSString *str = [TKTheme stringWithPath:@"ClassRoom.TKMediaView.mp4Loading2"];
            
            UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"%@%d",str,i]];
            
           
            
            [array addObject:image];
        }
    }
    return [NSArray arrayWithArray:array];
}


+(NSString *)docmentOrMediaImage:(NSString*)aType{
    
    NSString *tString = @"";
    if ([aType isEqualToString:@"whiteboard"]) {
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_empty")];
        
    }else if ([aType isEqualToString:@"xls"]||[aType isEqualToString:@"xlsx"]||[aType isEqualToString:@"xlt"]||[aType isEqualToString:@"xlsm"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_excel")];
    }else if ([aType isEqualToString:@"jpg"]|| [aType isEqualToString:@"jpeg"]||[aType isEqualToString:@"png"] ||[aType isEqualToString:@"gif"] || [aType isEqualToString:@"bmp"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_images")];
    }
    else if ([aType isEqualToString:@"ppt"] || [aType isEqualToString:@"pptx"] || [aType isEqualToString:@"pps"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_ppt")];
    }
    else if ([aType isEqualToString:@"docx"]|| [aType isEqualToString:@"doc"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_word")];
    }
    else if ([aType isEqualToString:@"txt"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_text_pad")];
    }
    else if ([aType isEqualToString:@"pdf"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_pdf")];
    }
    else if ([aType isEqualToString:@"mp3"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_mp3")];
    }
    else if ([aType isEqualToString:@"mp4"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_mp4")];
    } else if ([aType isEqualToString:@"zip"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_h5")];
    } else if ([aType isEqualToString:@"html"]){
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_h5")];
    }else{
        tString = [TKTheme stringWithPath:DocumentTypeTheme(@"icon_unknown")];
    }
        
    return tString;
    
    
}

// 根据色值匹配图片
+ (NSString *)imageNameWithPrimaryColor:(NSString *)PrimaryColor {
    
    NSString *imageName;
    
    if ([PrimaryColor caseInsensitiveCompare:@"#000000"] == NSOrderedSame) {
        imageName = @"icon_pen_black";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#5ac9fa"] == NSOrderedSame) {
        imageName = @"icon_pen_blue";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#ffcc00"] == NSOrderedSame) {
        imageName = @"icon_pen_yellow";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#ED3E3A"] == NSOrderedSame) {
        imageName = @"icon_pen_red";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#4740D2"] == NSOrderedSame) {
        imageName = @"icon_pen_deep purple";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#007BFF"] == NSOrderedSame) {
        imageName = @"icon_pen_deep blue";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#09C62B"] == NSOrderedSame) {
        imageName = @"icon_pen_green";
    }else if ([PrimaryColor caseInsensitiveCompare:@"#EDEDED"] == NSOrderedSame) {
        imageName = @"icon_pen_ white";
    }
    return imageName;
}


//授权照片
+ (void)phontLibraryAction{
    [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
    }];
}


+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString{
//    return [UIColor redColor];
    return [TKHelperUtil colorWithHexColorString:hexColorString alpha:1.0f];
}

+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString alpha:(float)alpha
{
    if ([hexColorString length] <6){//长度不合法
        return [UIColor blackColor];
    }
    NSString *tempString=[hexColorString lowercaseString];
    if ([tempString hasPrefix:@"0x"]){//检查开头是0x
        tempString = [tempString substringFromIndex:2];
    }else if ([tempString hasPrefix:@"#"]){//检查开头是#
        tempString = [tempString substringFromIndex:1];
    }
    if ([tempString length] !=6){
        return [UIColor blackColor];
    }
    //分解三种颜色的值
    NSRange range;
    range.location =0;
    range.length =2;
    NSString *rString = [tempString substringWithRange:range];
    range.location =2;
    NSString *gString = [tempString substringWithRange:range];
    range.location =4;
    NSString *bString = [tempString substringWithRange:range];
    //取三种颜色值
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString]scanHexInt:&r];
    [[NSScanner scannerWithString:gString]scanHexInt:&g];
    [[NSScanner scannerWithString:bString]scanHexInt:&b];
    return [UIColor colorWithRed:((float) r /255.0f)
                           green:((float) g /255.0f)
                            blue:((float) b /255.0f)
                           alpha:alpha];
}

+ (NSString *)hexColorStringWithColor:(UIColor *)color {
    
    CGFloat r, g, b, a;
    
    if ([color getRed:&r green:&g blue:&b alpha:&a]) {
        
        return [NSString stringWithFormat:@"#%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255)];
    }
    return nil;
}

+ (CGSize)sizeForString:(NSString *)string font:(UIFont *)font size:(CGSize)size
{
    return [string boundingRectWithSize:size options:NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:@{NSFontAttributeName : font} context:nil].size;
}

+ (int)returnTotalPageNum:(NSInteger)totalPage showPage:(NSInteger)pageNum{
    int addition =(int) totalPage/pageNum;
    int remainder = (int)totalPage%pageNum;
    return addition + (remainder==0?0:1);
}

+ (CGFloat)returnClassRoomDpi{
    
    CGFloat dpi = (CGFloat)[[TKEduClassRoom shareInstance].roomJson.videoheight integerValue]/
                    [[TKEduClassRoom shareInstance].roomJson.videowidth integerValue];
    
    if(isnan(dpi) || !dpi){//如果分辨率不存在默认设置为0     （4 : 3）
        dpi = 3.0/4;
    }
    return dpi;
}

+ (BOOL)isURL:(NSString *)text{
    
    NSString *regex =@"[a-zA-z]+://[^\\s]*";
    NSError *error;
    
    NSRegularExpression *regular = [[NSRegularExpression alloc] initWithPattern:regex options:0 error:&error];
    if (!error) {
        NSArray *results = [regular matchesInString:text options:0 range:NSMakeRange(0, text.length)];
//        for (NSTextCheckingResult *result in results) {
//            
//            return true;
//        }
        
        if (results.count) {
            return true;
        }
        return false;
    }
    else { // 如果有错误，则把错误打印出来
        TKLog(@"error - %@", error);
        return false;
    }
}

+ (void)setVideoFormat{

    int vcodec = [[TKEduClassRoom shareInstance].roomJson.vcodec intValue];
    if ((vcodec == 0 || vcodec == 1)) {//vcodec 是否支持VP8
        
        NSInteger videoframerate = [[TKEduClassRoom shareInstance].roomJson.videoframerate integerValue];

        TKVideoProfile *profile =  [TKVideoProfile new];
        profile.width = 640;
        profile.height = 480;
        profile.maxfps = videoframerate;
        
        [[TKEduSessionHandle shareInstance] sessionHandleVideoProfile:profile];

    }
    //设置前置和后置均不开启镜像模式
    [[TKRoomManager instance] setLocalVideoMirrorMode:(TKVideoMirrorModeDisabled)];
}


+ (UIImage *)resizableImageWithImageName:(NSString *)imageName{

    UIImage *image = [UIImage imageNamed:imageName];
    // 设置端盖的值--其它方向不需要拉伸，只拉伸头部
    CGFloat left = image.size.width * 0.45;
    UIEdgeInsets edgeInsets = UIEdgeInsetsMake(0, left, 0, image.size.width*0.54);
    // 设置拉伸的模式
    UIImageResizingMode mode = UIImageResizingModeStretch;
    // 拉伸图片
    UIImage *newImage = [image resizableImageWithCapInsets:edgeInsets resizingMode:mode];
    return newImage;
    
}


+ (float)GetCpuUsage {
    kern_return_t kr;
    task_info_data_t tinfo;
    mach_msg_type_number_t task_info_count;
    
    task_info_count = TASK_INFO_MAX;
    kr = task_info(mach_task_self(), TASK_BASIC_INFO, (task_info_t)tinfo, &task_info_count);
    if (kr != KERN_SUCCESS) {
        return 0;
    }
    
    task_basic_info_t      basic_info;
    thread_array_t         thread_list;
    mach_msg_type_number_t thread_count;
    
    thread_info_data_t     thinfo;
    mach_msg_type_number_t thread_info_count;
    
    thread_basic_info_t basic_info_th;
    uint32_t stat_thread = 0; // Mach threads
    
    basic_info = (task_basic_info_t)tinfo;
    
    // get threads in the task
    kr = task_threads(mach_task_self(), &thread_list, &thread_count);
    if (kr != KERN_SUCCESS) {
        return 0;
    }
    if (thread_count > 0)
        stat_thread += thread_count;
    
    long tot_sec = 0;
    long tot_usec = 0;
    float tot_cpu = 0;
    int j;
    
    for (j = 0; j < thread_count; j++)
    {
        thread_info_count = THREAD_INFO_MAX;
        kr = thread_info(thread_list[j], THREAD_BASIC_INFO,
                         (thread_info_t)thinfo, &thread_info_count);
        if (kr != KERN_SUCCESS) {
            return 0;
        }
        
        basic_info_th = (thread_basic_info_t)thinfo;
        
        if (!(basic_info_th->flags & TH_FLAGS_IDLE)) {
            tot_sec = tot_sec + basic_info_th->user_time.seconds + basic_info_th->system_time.seconds;
            tot_usec = tot_usec + basic_info_th->system_time.microseconds + basic_info_th->system_time.microseconds;
            tot_cpu = tot_cpu + basic_info_th->cpu_usage / (float)TH_USAGE_SCALE * 100.0;
        }
        
    } // for each thread
    
    kr = vm_deallocate(mach_task_self(), (vm_offset_t)thread_list, thread_count * sizeof(thread_t));
    assert(kr == KERN_SUCCESS);
    
    return tot_cpu;
}
+ (CGFloat)GetCurrentTaskUsedMemory {
    
    task_basic_info_data_t taskInfo;
    mach_msg_type_number_t infoCount = TASK_BASIC_INFO_COUNT;
    kern_return_t kernReturn = task_info(mach_task_self(),
                                         TASK_BASIC_INFO,
                                         (task_info_t)&taskInfo,
                                         &infoCount);
    
    if (kernReturn != KERN_SUCCESS) {
        return NSNotFound;
    }
    return taskInfo.resident_size/1024.0/1024.0/2.0;
    
}

+ (void)HUDShowMessage:(NSString*)msg addedToView:(UIView*)view
{
    [TKHelperUtil HUDShowMessage:msg addedToView:view showTime:1.0f];
}

+ (void)HUDShowMessage:(NSString*)msg addedToView:(UIView*)view showTime:(CGFloat)time
{
    static MBProgressHUD* hud = nil;
    if(hud != nil && hud.superview != view)
    {
        UIView* theSuper = hud.superview;
        hud = nil;
        [MBProgressHUD hideHUDForView:theSuper animated:NO];
    }
    if (!hud) {
        hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    }
    hud.mode = MBProgressHUDModeText;
    hud.label.text = msg;
    hud.hidden = NO;
    hud.alpha = 1.0f;
    [hud hideAnimated:YES afterDelay:time];
    
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    switch (orientation) {
        case UIInterfaceOrientationLandscapeRight:
            hud.transform = CGAffineTransformMakeRotation(-M_PI * 2);
            break;
        case UIInterfaceOrientationLandscapeLeft:
            hud.transform = CGAffineTransformMakeRotation(-M_PI * 2);
            break;
        case UIInterfaceOrientationPortrait:
            hud.transform = CGAffineTransformIdentity;
            break;
        default:
            break;
    }
}
@end

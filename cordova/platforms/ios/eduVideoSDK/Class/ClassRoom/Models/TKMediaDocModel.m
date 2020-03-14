//
//  MediaDocModel.m
//  EduClassPad
//
//  Created by ifeng on 2017/5/20.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKMediaDocModel.h"

@implementation TKMediaDocModel
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"fileid"]) {
        
        if ([value isKindOfClass:NSNumber.class]) {
            
            _fileid = ((NSNumber *)value).stringValue;
            return;
        }
    }
    [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key
{
    if ([key isEqualToString:@"isContentDocument"]) {
        _isContentDocument = [NSNumber numberWithInteger:[value integerValue]];
    } else {
//        NSLog(@"%@--%@没有定义%@",@(__FILE__),@(__FUNCTION__), key);
    }
}
//-(void)setValue:(id)value forKey:(NSString *)key{
//    NSLog(@"key:%@ value:%@",key,value);
//}
@end

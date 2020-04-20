//
//  TKDocmentDocModel.m
//  EduClassPad
//
//  Created by ifeng on 2017/5/31.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKDocmentDocModel.h"
@implementation TKDocmentDocModel
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
-(void)dynamicpptUpdate{
    //如果是动态ppt
    if ([_dynamicppt intValue]) {
        if (_downloadpath) {
            _swfpath = [_downloadpath copy];
        }
        _action = sActionShow;
    }else{
        _action = @"";
    }
}

- (void)resetToDefault {
    self.currpage = [[NSNumber alloc] initWithInt:1];
    self.pptstep = [[NSNumber alloc] initWithInt:0];
    self.steptotal = [[NSNumber alloc] initWithInt:0];
    self.pptslide = [[NSNumber alloc] initWithInt:1];
    if (self.fileid.intValue == 0) {
        self.pagenum = [[NSNumber alloc] initWithInt:1];
    }
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@---%@",self.fileid, self.filecategory];
}

@end

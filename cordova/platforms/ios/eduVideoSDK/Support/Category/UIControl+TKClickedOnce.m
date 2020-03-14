//
//  UIControl+clickedOnce.m
//  EduClassPad
//
//  Created by ifeng on 2017/7/12.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "UIControl+TKClickedOnce.h"
#import <objc/runtime.h>
@implementation UIControl (TKClickedOnce)
static const char *UIControl_acceptEventInterval = "UIControl_acceptEventInterval";
static const char *UIControl_acceptedEventTime   = "UIControl_acceptedEventTime";

-(NSTimeInterval)itk_acceptEventInterval{
    return [objc_getAssociatedObject(self, UIControl_acceptEventInterval) doubleValue];
}


-(void)setItk_acceptEventInterval:(NSTimeInterval)itk_acceptEventInterval{
    objc_setAssociatedObject(self, UIControl_acceptEventInterval, @(itk_acceptEventInterval), OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}


-(NSTimeInterval)itk_acceptedEventTime{
    return [objc_getAssociatedObject(self, UIControl_acceptedEventTime)doubleValue];
}

-(void)setItk_acceptedEventTime:(NSTimeInterval)itk_acceptedEventTime{
    objc_setAssociatedObject(self, UIControl_acceptedEventTime, @(itk_acceptedEventTime), OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
//[[self class] cancelPreviousPerformRequestsWithTarget:self selector:@selector(Dosomething:) object:sender];
//[self performSelector:@selector(Dosomething:) withObject:sender afterDelay:3];
+(void)load{
    @autoreleasepool {
        Method a = class_getInstanceMethod([self class], @selector(sendAction:to:forEvent:));
        Method b = class_getInstanceMethod([self class], @selector(__itkSendAction:to:forEvent:));
        method_exchangeImplementations(a, b);
    }
}

-(void)tkButtonExchangeImplementations{
    //return;
    @autoreleasepool {
        Method a = class_getInstanceMethod([self class], @selector(sendAction:to:forEvent:));
        Method b = class_getInstanceMethod([self class], @selector(__itkSendAction:to:forEvent:));
        method_exchangeImplementations(a, b);
    }
}

-(void)__itkSendAction:(SEL)action to:(id)target forEvent:(UIEvent *)event{
    
    if (NSDate.date.timeIntervalSince1970 - self.itk_acceptedEventTime < self.itk_acceptEventInterval) return;
    
    if (self.itk_acceptEventInterval > 0)
    {
        self.itk_acceptedEventTime = NSDate.date.timeIntervalSince1970;
    }
    [self __itkSendAction:action to:target forEvent:event];
}
@end

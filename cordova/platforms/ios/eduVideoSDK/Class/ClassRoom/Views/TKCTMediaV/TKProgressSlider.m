//
//  TKProgressSlider.m
//  EduClass
//
//  Created by lyy on 2018/5/3.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKProgressSlider.h"

@implementation TKProgressSlider

- (CGRect)trackRectForBounds:(CGRect)bounds
{
    CGFloat height = 5.;
    CGFloat frameH = bounds.size.height;
    // 垂直居中
    return CGRectMake(0, frameH/2 - height/2, CGRectGetWidth(self.frame), height);
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/


@end

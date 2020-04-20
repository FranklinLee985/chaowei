//
//  TKTrophyButton.m
//  EduClass
//
//  Created by lyy on 2018/5/25.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKTrophyButton.h"

@implementation TKTrophyButton

- (CGRect)imageRectForContentRect:(CGRect)contentRect{
    
    CGFloat imageY = 5;
    CGFloat imageW = contentRect.size.width - 10;
    CGFloat imageX = 5;
    CGFloat imageH = contentRect.size.height-10;
    return CGRectMake(imageX, imageY, imageW, imageH);
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

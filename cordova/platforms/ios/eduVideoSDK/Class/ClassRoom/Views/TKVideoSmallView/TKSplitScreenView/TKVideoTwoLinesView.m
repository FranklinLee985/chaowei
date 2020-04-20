//
//  TKVideoTwoLinesView.m
//  EduClassPad
//
//  Created by lyy on 2018/1/29.
//  Copyright © 2018年 beijing. All rights reserved.
//

#import "TKVideoTwoLinesView.h"
#import "TKCTVideoSmallView.h"

@implementation TKVideoTwoLinesView
- (void)setVideoSmallViewArray:(NSMutableArray *)videoSmallViewArray{
    
    if (videoSmallViewArray.count == 7) {
        
        for (int i = 0; i<videoSmallViewArray.count; i++) {
            
            TKCTVideoSmallView *view =(TKCTVideoSmallView *) videoSmallViewArray[i];
            
            if (view.iVideoViewTag == -1) {
                [videoSmallViewArray exchangeObjectAtIndex:1 withObjectAtIndex:i];
                break;
            }
        }
        
    }
    
    CGFloat w = CGRectGetWidth(self.frame);
    CGFloat h = CGRectGetHeight(self.frame);
    CGFloat x = 0, y = 0;
    NSInteger count = videoSmallViewArray.count, next = 0;
    
    for (int i = 0; i<2; i++) {
        NSInteger jMax = (i==0?(count/2):(count/2+count%2));
        
        for (int j=0; j<jMax; j++) {
            x = (w/jMax*j);
            TKCTVideoSmallView *view =(TKCTVideoSmallView *) videoSmallViewArray[next++];
            [self addSubview:view];
            view.frame = CGRectMake(x, y, w/jMax,h/2);
            
        }
        x = 0;
        y = h/2;
    }
    
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

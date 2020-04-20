//
//  TKVideoThreeLinesView.m
//  EduClassPad
//
//  Created by lyy on 2018/1/29.
//  Copyright © 2018年 beijing. All rights reserved.
//

#import "TKVideoThreeLinesView.h"
#import "TKCTVideoSmallView.h"

@implementation TKVideoThreeLinesView
- (void)setVideoSmallViewArray:(NSMutableArray *)videoSmallViewArray{
   
    CGFloat w = CGRectGetWidth(self.frame);
    CGFloat h = CGRectGetHeight(self.frame);
    CGFloat x = 0, y = 0;
    NSInteger count = videoSmallViewArray.count, next = 0;
    
    
    NSInteger count0,count1,count2;
    
    if (count%3==2) {
        count0 = count/3;
        count1 = count2 = count/3+1;

    }else if(count%3==1){
        count0 = count/3;
        count1 = (count == 13?(count/3+1):count/3);
        count2 = (count == 13?count/3:(count/3+1));
    }else{
        count0 = count1 = count2 = count/3;
    }
    
    NSMutableArray *countArray = [NSMutableArray arrayWithObjects:@(count0),@(count1),@(count2), nil];
    
    
    for (int i = 0; i<3; i++) {
        NSInteger jMax = [countArray[i] integerValue];
        for (int j=0; j<jMax; j++) {
            x = (w/jMax*j);
            TKCTVideoSmallView *view =(TKCTVideoSmallView *) videoSmallViewArray[next++];
            [self addSubview:view];
            view.frame = CGRectMake(x, y, w/jMax,h/3);
            
        }
        x = 0;
        y = (i==0?h/3:h/3*2);
        
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

//
//  TKVideoThirteenView.m
//  EduClassPad
//
//  Created by lyy on 2018/1/29.
//  Copyright © 2018年 beijing. All rights reserved.
//

#import "TKVideoThirteenView.h"
#import "TKCTVideoSmallView.h"

@implementation TKVideoThirteenView

- (void)setVideoSmallViewArray:(NSMutableArray *)videoSmallViewArray{
    
   
    for (int i = 0; i<videoSmallViewArray.count; i++) {
        
        TKCTVideoSmallView *view =(TKCTVideoSmallView *) videoSmallViewArray[i];
        
        if (view.iVideoViewTag == -1) {
            [videoSmallViewArray exchangeObjectAtIndex:6 withObjectAtIndex:i];
            break;
        }
    }
    
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
    
    CGFloat width = w/4;
    CGFloat studentW = (w-w/4)/4;
    
    
    
    
    for (int i = 0; i<3; i++) {
        NSInteger jMax = [countArray[i] integerValue];
        for (int j=0; j<jMax; j++) {
            
            x =(i==1)?(j>2?studentW*(j-1)+width:studentW*j):width*j;
            TKCTVideoSmallView *view =(TKCTVideoSmallView *) videoSmallViewArray[next++];
            [self addSubview:view];
            view.frame = CGRectMake(x, y, (i==1)?(j==2?width:studentW):w/jMax,h/3);
            
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

//
//  TKSplitScreenBaseView.m
//  EduClassPad
//
//  Created by lyy on 2017/11/23.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import "TKVideoOneView.h"
#import "TKCTVideoSmallView.h"

@interface TKVideoOneView()
@end

@implementation TKVideoOneView

- (void)awakeFromNib{
    [super awakeFromNib];
    
}
- (void)setVideoSmallViewArray:(NSMutableArray *)videoSmallViewArray{
    
    CGFloat w = CGRectGetWidth(self.frame);
    CGFloat h = CGRectGetHeight(self.frame);
    CGFloat x = 0, y = 0;
    NSUInteger count = videoSmallViewArray.count, next = 0;
    
    for(int i=1;i<=2;i++){
        for (int j=0; j<(i==1?1:count-1); j++) {
            TKCTVideoSmallView *view =(TKCTVideoSmallView *) videoSmallViewArray[next++];
            [self addSubview:view];
            view.frame = CGRectMake(x, y, w/(count/2+1), h/((i==1?1:count-1)/2+1));
            y =h/((i==1?1:count-1)/2+1);
        }
        y = 0;
        x = w/(count/2+1);
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

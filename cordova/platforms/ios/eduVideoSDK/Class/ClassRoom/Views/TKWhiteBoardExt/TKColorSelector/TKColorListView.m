//
//  TKColorListView.m
//  EduClass
//
//  Created by talkcloud on 2019/4/2.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKColorListView.h"

@implementation TKColorListView

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    
    CGPoint point = [[touches anyObject] locationInView:self];
    if (self.BeganBlock) {
        self.BeganBlock(point);
    }
    
    [super touchesBegan:touches withEvent:event];
}

@end

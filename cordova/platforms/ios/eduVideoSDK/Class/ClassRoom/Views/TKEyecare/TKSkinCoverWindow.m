//
//  TKSkinCoverWindow.m
//  shade
//
//  Created by Evan on 2019/12/18.
//  Copyright © 2019 Evan. All rights reserved.
//

#import "TKSkinCoverWindow.h"

@implementation TKSkinCoverWindow

- (instancetype)initWithFrame:(CGRect)frame {
    
    if (self = [super initWithFrame:frame]) {
        // 移除所有的子layer
        [self.layer.sublayers makeObjectsPerformSelector:@selector(removeFromSuperlayer)];
        // 添加layer
        CALayer *skinCoverLayer = [CALayer layer];
        skinCoverLayer.frame = CGRectMake(0, 0, frame.size.width, frame.size.height);
        skinCoverLayer.backgroundColor = [UIColor colorWithRed:255/255.0 green:122/255.0 blue:0/255.0 alpha:0.4].CGColor;
        skinCoverLayer.opacity = 0.4;
        [self.layer addSublayer:skinCoverLayer];
       
    }
    return self;
}

@end

//
//  TKBaseBasicAnimation.m
//  shade
//
//  Created by Evan on 2019/12/19.
//  Copyright © 2019 Evan. All rights reserved.
//

#import "TKBaseBasicAnimation.h"

@implementation TKBaseBasicAnimation

- (void)setBaseCABasicAnimationDidblock:(BaseCABasicAnimationDidblock)baseCABasicAnimationDidblock {
    self.delegate = self;
    _baseCABasicAnimationDidblock = baseCABasicAnimationDidblock;
    
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
    if (self.baseCABasicAnimationDidblock) {
        self.baseCABasicAnimationDidblock(anim, flag);
    }
}

@end

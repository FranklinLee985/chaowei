//
//  TKBaseBasicAnimation.h
//  shade
//
//  Created by Evan on 2019/12/19.
//  Copyright © 2019 Evan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <QuartzCore/QuartzCore.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^BaseCABasicAnimationDidblock)(CAAnimation * anim, BOOL flag);
@interface TKBaseBasicAnimation : CABasicAnimation <CAAnimationDelegate>

/** block */
@property (nonatomic, copy) BaseCABasicAnimationDidblock baseCABasicAnimationDidblock;

@end

NS_ASSUME_NONNULL_END

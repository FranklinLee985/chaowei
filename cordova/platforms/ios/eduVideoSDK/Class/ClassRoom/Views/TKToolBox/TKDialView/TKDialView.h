//
//  TKDialView.h
//  EduClass
//
//  Created by Yi on 2019/1/7.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKToolBoxBaseView.h"

NS_ASSUME_NONNULL_BEGIN


@interface TKDialView : TKToolBoxBaseView

- (void)startWithAngle:(id)angle;
- (void)setAngle:(NSString *)degStr;

@end

NS_ASSUME_NONNULL_END

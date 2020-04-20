//
//  TKCTNetDetailView.h
//  EduClass
//
//  Created by talkcloud on 2019/3/20.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKCTNetDetailView : UIView

+ (TKCTNetDetailView *) showDetailViewWithPoint:(CGPoint)point diss:(void(^)(void))block;
- (void) changeDetailData:(id)state;

@end

NS_ASSUME_NONNULL_END

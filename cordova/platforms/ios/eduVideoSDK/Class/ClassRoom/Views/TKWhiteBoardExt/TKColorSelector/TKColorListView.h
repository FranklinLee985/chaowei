//
//  TKColorListView.h
//  EduClass
//
//  Created by talkcloud on 2019/4/2.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKColorListView : UIView

@property (nonatomic, copy) void(^BeganBlock)(CGPoint point);

@end

NS_ASSUME_NONNULL_END

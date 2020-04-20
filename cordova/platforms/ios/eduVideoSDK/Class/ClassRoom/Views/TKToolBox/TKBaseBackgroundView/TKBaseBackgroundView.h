//
//  TKBaseBackgroundView.h
//  EduClass
//
//  Created by maqihan on 2019/1/4.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKToolBoxBaseView.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKBaseBackgroundView : TKToolBoxBaseView

@property (strong , nonatomic) UIView  *backgroundView;
@property (strong , nonatomic) UIView  *contentView;

@property (strong , nonatomic) UILabel  *titleLabel;
@property (strong , nonatomic) UIButton *cancelButton;

@end

NS_ASSUME_NONNULL_END

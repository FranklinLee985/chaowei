//
//  TKAnswerSheetDetailCell.h
//  EduClass
//
//  Created by maqihan on 2019/1/7.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKProgressView.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKAnswerSheetDetailCell : UICollectionViewCell

@property (strong , nonatomic) TKProgressView  *progressView;

@property (strong , nonatomic) UILabel         *numLabel;
@property (strong , nonatomic) UILabel         *serialLabel;

@end

NS_ASSUME_NONNULL_END

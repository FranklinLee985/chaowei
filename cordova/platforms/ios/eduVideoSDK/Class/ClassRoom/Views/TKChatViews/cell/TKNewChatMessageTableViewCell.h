//
//  TKNewChatMessageTableViewCell.h
//  EduClass
//
//  Created by talk on 2018/11/22.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKChatMessageModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKNewChatMessageTableViewCell : UITableViewCell

@property (nonatomic, strong) TKChatMessageModel *chatModel;
@property (nonatomic, strong) UIImageView *bubbleView;

+ (CGFloat)heightForCellWithText:(NSString *)text limitWidth:(CGFloat)width;

@end

NS_ASSUME_NONNULL_END

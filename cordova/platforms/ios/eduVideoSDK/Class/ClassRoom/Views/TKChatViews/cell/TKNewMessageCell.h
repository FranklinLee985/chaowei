//
//  TMNewMessageCell.h
//  EduClass
//
//  Created by talk on 2018/11/21.
//  Copyright © 2018年 talkcloud. All rights reserved.
//


NS_ASSUME_NONNULL_BEGIN

@interface TKNewMessageCell : UITableViewCell

@property (nonatomic, strong) UIView *bubbleView;
@property (nonatomic, strong) NSString *iMessageText;

- (void)setTextColor:(nullable UIColor *)color;

@end

NS_ASSUME_NONNULL_END

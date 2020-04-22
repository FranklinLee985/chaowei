//
//  TMNewMessageCell.m
//  EduClass
//
//  Created by talk on 2018/11/21.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKNewMessageCell.h"

#define ThemeKP(args) [@"ClassRoom.TKChatViews." stringByAppendingString:args]

@interface TKNewMessageCell ()

@property (nonatomic, strong) UILabel *iMessageLabel;
@property (nonatomic, strong) UIImageView *backgroudImageView;

@end

@implementation TKNewMessageCell

-(id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.backgroundColor             = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];

        [self setupView];
    }
    return self;
}

- (void)setupView
{
    self.bubbleView = [[UIView alloc] init];
    self.bubbleView.sakura.backgroundColor(@"ClassRoom.TKChatViews.chat_bubble_system_color");
    self.bubbleView.sakura.alpha(@"ClassRoom.TKChatViews.chat_bubble_system_alpha");
    [self.contentView addSubview:self.bubbleView];
    [self.contentView sendSubviewToBack:self.bubbleView];
    
    _iMessageLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    _iMessageLabel.textColor = [UIColor whiteColor];
    _iMessageLabel.backgroundColor = UIColor.clearColor;
    _iMessageLabel.textAlignment = NSTextAlignmentCenter;
    _iMessageLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
    _iMessageLabel.numberOfLines = 1;
    [_iMessageLabel setFont:TKFont(11)];
    [self.contentView addSubview:_iMessageLabel];
}

- (void)setIMessageText:(NSString *)iMessageText {
    _iMessageText = iMessageText;
    
    _iMessageLabel.text = _iMessageText;
}

- (void)layoutSubviews
{
    [super layoutSubviews];

    CGSize size = [TKHelperUtil sizeForString:self.iMessageLabel.text font:TKFont(11) size:CGSizeMake(self.width / 3 * 2 - 20, CGFLOAT_MAX)];
    
    self.bubbleView.frame = CGRectMake(0, 0, size.width + 10, 22);
    self.bubbleView.layer.cornerRadius = self.bubbleView.height / 2;
    self.bubbleView.layer.masksToBounds = YES;
    
    self.iMessageLabel.frame = self.bubbleView.frame;
    self.iMessageLabel.centerY = self.bubbleView.centerY;
    [self.contentView bringSubviewToFront:self.iMessageLabel];
}

- (void)setTextColor:(nullable UIColor *)color
{
    if (!color) {
        self.iMessageLabel.sakura.textColor(@"ClassRoom.TKChatViews.chatTipsMsgColor");
    } else {
        //聊天框禁言消息进入退出消息不需要时间，禁言消息显示红色
        self.iMessageLabel.textColor = color;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

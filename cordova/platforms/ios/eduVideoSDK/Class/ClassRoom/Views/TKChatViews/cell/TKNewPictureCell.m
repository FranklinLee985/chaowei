//
//  TKNewPictureCell.m
//  EduClass
//
//  Created by talkcloud on 2019/7/11.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKNewPictureCell.h"

@interface TKNewPictureCell ()

@property (nonatomic, strong) UILabel *iNickNameLabel;//用户名

@property (nonatomic, strong) UIImageView * bubbleView;
@property (nonatomic, strong) UIImageView * msgImageView;
@property (nonatomic, strong) UIImageView * bigImageView;
@end

@implementation TKNewPictureCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.backgroundColor = UIColor.clearColor;
        self.contentView.backgroundColor = UIColor.clearColor;
        
        _bubbleView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _bubbleView.sakura.image(@"ClassRoom.TKChatViews.chat_bubble");
        UIImage *image = _bubbleView.image;
        CGFloat top = image.size.height/2.0;
        CGFloat left = image.size.width/2.0;
        CGFloat bottom = image.size.height/2.0;
        CGFloat right = image.size.width/2.0;
        _bubbleView.image = [image resizableImageWithCapInsets:UIEdgeInsetsMake(top, left, bottom, right) resizingMode:UIImageResizingModeStretch];
        [self.contentView addSubview:_bubbleView];
        
        UILabel *tLabel = [[UILabel alloc] init];
        tLabel.textAlignment = NSTextAlignmentLeft ;
        tLabel.backgroundColor = [UIColor clearColor];
        tLabel.lineBreakMode = NSLineBreakByTruncatingTail;
        tLabel.font = TKFont(14);
        _iNickNameLabel = tLabel;
        [self.contentView addSubview:_iNickNameLabel];
        
        _msgImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _msgImageView.contentMode = UIViewContentModeScaleAspectFit;
        [self.contentView addSubview:_msgImageView];
        
        _msgImageView.userInteractionEnabled = YES;
        [_msgImageView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(smallImageClick:)]];
    }
    return self;
}

- (void)setChatModel:(TKChatMessageModel *)chatModel {
    _chatModel = chatModel;
    
    self.iNickNameLabel.sakura.textColor(chatModel.iChatRoleType == TKChatRoleTypeMe ? @"TKUserListTableView.coursewareButtonYellowColor" : @"TKUserListTableView.coursewareButtonWhiteColor");
    self.iNickNameLabel.text = [NSString stringWithFormat:@"%@:", (chatModel.iChatRoleType == TKChatRoleTypeMe) ? TKMTLocalized(@"Role.Me") : chatModel.iUserName];
    
    NSMutableString * path = [NSMutableString stringWithFormat:@"%@", _chatModel.iMessage];
    [path insertString:@"-1" atIndex: [path rangeOfString:@"."].location];// 文件名需要加-1
    
    NSString * urlStr = [_chatModel.iCospath stringByAppendingPathComponent:path];
    [_msgImageView sd_setImageWithURL:[NSURL URLWithString:urlStr] placeholderImage:[UIImage imageNamed:@"tk_login_logo"] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {

        [self setNeedsLayout];
        [self layoutIfNeeded];
    }];
//    cospath = "https://demodoc-1253417915.cos.ap-guangzhou.myqcloud.com";
//    msg = "/cospath/20190712_163543_zdsfvfyh.png";
//    msgtype = onlyimg;
//    time = "16:35";
//    type = 0;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    if (_msgImageView.image) {
        
        CGSize nickSize = [TKHelperUtil sizeForString:_iNickNameLabel.text font:TKFont(14) size:CGSizeMake(100, 30)];
        _iNickNameLabel.frame = CGRectMake(11, 0, nickSize.width + 2, 30);
        
        CGSize size = [_msgImageView.image size];
        CGFloat imageHeight = self.height - 8 - 8 - 30;
        CGFloat imageWidth  = imageHeight * (size.width / size.height);
        imageWidth = fminf(imageWidth, self.width * 0.7);
        imageWidth = fmaxf(imageWidth, imageHeight);
        _bubbleView.frame = CGRectMake(0, 0, fmaxf(11 + nickSize.width + 11, 11 + imageWidth + 11), self.height - 8);
        _msgImageView.frame = CGRectMake(11, 30, imageWidth, imageHeight);
    }
}

- (void)smallImageClick:(UITapGestureRecognizer *)tap {
    
    if (nil == _bigImageView) {
        _bigImageView = [[UIImageView alloc] initWithFrame:TKMainWindow.bounds];
        _bigImageView.userInteractionEnabled = YES;
        [_bigImageView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(bigImageClick:)]];
    }
    
    [TKMainWindow addSubview:_bigImageView];
    _bigImageView.image = [_msgImageView.image copy];
//    _bigImageView.backgroundColor = UIColor.whiteColor;
    CGSize size = [_bigImageView.image size];
    if (size.width >= ScreenW || size.height >= ScreenH) {
        _bigImageView.contentMode = UIViewContentModeScaleAspectFit;
    } else {
        _bigImageView.contentMode = UIViewContentModeCenter;
    }
}

- (void)bigImageClick:(UITapGestureRecognizer *) tap {
    
    if (_bigImageView) {
        [_bigImageView removeFromSuperview];
        _bigImageView = nil;
    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

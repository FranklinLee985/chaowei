//
//  TKNewChatMessageTableViewCell.m
//  EduClass
//
//  Created by talk on 2018/11/11.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKNewChatMessageTableViewCell.h"
#import "NSAttributedString+TKEmoji.h"
#import "TKLinkLabel.h"
#import "TKEduClassRoom.h"

#define ThemeKP(args) [@"ClassRoom.TKChatViews." stringByAppendingString:args]

@interface TKNewChatMessageTableViewCell ()

@property (nonatomic, strong) UIButton *iTranslationButton;//翻译按钮
@property (nonatomic, strong) UILabel *iNickNameLabel;//用户名
@property (nonatomic, strong) TKLinkLabel *iMessageLabel;//聊天内容

@property (nonatomic, strong) UIView *translationBorderView;// 分割线
@property (nonatomic, strong) UILabel *iMessageTranslationLabel;//翻译内容
@property (nonatomic, strong) NSString *cString;

@end

@implementation TKNewChatMessageTableViewCell

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        
        self.userInteractionEnabled = YES;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.contentView.backgroundColor = [UIColor clearColor];
        self.backgroundColor             = [UIColor clearColor];
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 5;
        
        [self setupView];
    }
    return self;
}

- (void)setupView {
    
    //昵称
    {
        _iNickNameLabel = ({
            UILabel *tLabel = [[UILabel alloc] init];
            tLabel.textAlignment = NSTextAlignmentLeft ;
            tLabel.sakura.textColor(ThemeKP(@"chatNameColor"));
            tLabel.backgroundColor = [UIColor clearColor];
            tLabel.font = TKFont(14);
            tLabel;
        });
        [self.contentView addSubview:_iNickNameLabel];
    }
    //内容
    {
        _iMessageLabel = ({
            TKLinkLabel *tLabel = [[TKLinkLabel alloc] init];
            tLabel.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            tLabel.backgroundColor = [UIColor clearColor];
            tLabel.font = TKFont(14);
            tLabel.numberOfLines = 0;
            tLabel.linkTapHandler = ^(TKLinkType linkType, NSString *string, NSRange range) {
                if (linkType == TKLinkTypeURL) {//打开连接
                    if(![TKHelperUtil isURL:string]){
                        string = [NSString stringWithFormat:@"http://%@",string];
                    }
                    NSURL *url = [NSURL URLWithString:string];
                    
                    [[UIApplication sharedApplication] openURL:url];
                }
            };
            tLabel.linkLongPressHandler = ^(TKLinkType linkType, NSString *string, NSRange range) {
                
                if (linkType == TKLinkTypeURL) {//复制链接
                    self.cString = string;
                    [self becomeFirstResponder];
                    UIMenuItem * item = [[UIMenuItem alloc]initWithTitle:TKMTLocalized(@"Menu.Copy") action:@selector(newFunc)];
                    [[UIMenuController sharedMenuController] setTargetRect:self.frame inView:self.superview];
                    [UIMenuController sharedMenuController].menuItems = @[item];
                    [UIMenuController sharedMenuController].menuVisible = YES;
                }
            };
            tLabel;
        });
        [self.contentView addSubview:_iMessageLabel];
    }
    
    //翻译按钮
    {
        _iTranslationButton = ({
            UIButton *tLeftButton = [UIButton buttonWithType:UIButtonTypeCustom];
            tLeftButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
            tLeftButton.userInteractionEnabled = NO;
            
            //根据配置项决定中日互译还是中英互译
            BOOL isZR = [TKEduClassRoom shareInstance].roomJson.configuration.isChineseJapaneseTranslation;
            NSString *nImage = isZR ? @"ClassRoom.TKChatViews.cj_translation_normal": @"ClassRoom.TKChatViews.translation_normal";
            NSString *sImage = isZR ? @"ClassRoom.TKChatViews.cj_translation_selected" : @"ClassRoom.TKChatViews.translation_selected";
            tLeftButton.sakura.image(nImage, UIControlStateNormal);
            tLeftButton.sakura.image(sImage, UIControlStateSelected);
            
            tLeftButton;
        });
        [self.contentView addSubview:_iTranslationButton];
    }
    
    //翻译
    {
        // 分割线
        _translationBorderView = ({
            UIView *view = [[UIView alloc] init];
            view.sakura.backgroundColor(ThemeKP(@"chatLineColor"));
            view.sakura.alpha(ThemeKP(@"chatLinealpha"));
            view;
        });
        [self.contentView addSubview:_translationBorderView];
        
        _iMessageTranslationLabel = ({
            UILabel *tLabel = [[UILabel alloc] init];
            tLabel.sakura.textColor(ThemeKP(@"chatTransColor"));
            tLabel.font            = TKFont(14);
            tLabel.numberOfLines = 0;
            tLabel;
        });
        [self.contentView addSubview:_iMessageTranslationLabel];
    }
}

- (void)setChatModel:(TKChatMessageModel *)chatModel
{
    _chatModel = chatModel;
    
    //=====增加表情的识别，表情不进行翻译 ===  +  === 对链接地址不进行翻译======
    NSString * iText = [NSAttributedString tkRemoveEmojiAttributedString:_chatModel.iMessage
                                                                  withFont:TEXT_FONT
                                                                 withColor:[UIColor whiteColor]];
    BOOL hasTrans = _chatModel.iTranslationMessage.length > 0;
    self.iTranslationButton.selected = hasTrans || (iText.length == 0);
    self.iMessageTranslationLabel.text = _chatModel.iTranslationMessage;
    self.translationBorderView.hidden = !hasTrans;
    self.iMessageTranslationLabel.hidden = !hasTrans;

    self.iMessageLabel.isWhiteColor = YES;
    self.iMessageLabel.sakura.textColor(ThemeKP(@"chatMessageColor"));
    self.iMessageLabel.attributedText = (NSMutableAttributedString *)[NSAttributedString tkEmojiAttributedString:_chatModel.iMessage withFont:TEXT_FONT withColor:UIColor.whiteColor];
    
    //自己的名字显示黄色他人名字显示白色
    self.iNickNameLabel.sakura.textColor(chatModel.iChatRoleType == TKChatRoleTypeMe ? @"TKUserListTableView.coursewareButtonYellowColor" : @"TKUserListTableView.coursewareButtonWhiteColor");
    self.iNickNameLabel.text = [NSString stringWithFormat:@"%@:", (chatModel.iChatRoleType == TKChatRoleTypeMe) ? TKMTLocalized(@"Role.Me") : chatModel.iUserName];
    
    if (!_bubbleView) {
        _bubbleView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _bubbleView.sakura.image(@"ClassRoom.TKChatViews.chat_bubble");
        UIImage *image = _bubbleView.image;
        CGFloat top = image.size.height/2.0;
        CGFloat left = image.size.width/2.0;
        CGFloat bottom = image.size.height/2.0;
        CGFloat right = image.size.width/2.0;
        _bubbleView.image = [image resizableImageWithCapInsets:UIEdgeInsetsMake(top, left, bottom, right) resizingMode:UIImageResizingModeStretch];
        [self.contentView addSubview:_bubbleView];
        [self.contentView sendSubviewToBack:_bubbleView];
    }
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    CGSize nameSize = [TKHelperUtil sizeForString:self.iNickNameLabel.text font:TKFont(14) size:CGSizeMake(fminf(self.width / 3, 100), CGFLOAT_MAX)];
    self.iNickNameLabel.size = nameSize;
    self.iNickNameLabel.x = 11;
    self.iNickNameLabel.y = 16 * Proportion;
    
    CGFloat limitWidth = self.width - 11 -  self.iNickNameLabel.width - 10 - 10 - 10 - 20;
    CGSize msgSize = [self.iMessageLabel sizeThatFits:CGSizeMake(limitWidth, CGFLOAT_MAX)];
    self.iMessageLabel.size = msgSize;
    self.iMessageLabel.x = CGRectGetMaxX(self.iNickNameLabel.frame) + 10;
    self.iMessageLabel.y = 16 * Proportion;
    
    CGFloat tTranslateLabelHeigh = 22;
    self.iTranslationButton.autoresizingMask = UIViewAutoresizingNone;
    self.iTranslationButton.frame = CGRectMake(_iMessageLabel.rightX + 10,
                                               _iNickNameLabel.y,
                                               tTranslateLabelHeigh, tTranslateLabelHeigh);
    
    float bubbleWidth = CGRectGetMaxX(self.iTranslationButton.frame) + 10;
    _bubbleView.frame = CGRectMake(0, 0, bubbleWidth, self.height - 8);
    
    // 显示分割线
    if (self.translationBorderView.hidden == NO) {
        self.translationBorderView.frame = CGRectMake(10,
                                                      CGRectGetMaxY(self.iMessageLabel.frame) + 10,
                                                      bubbleWidth - 20,
                                                      1);
    }
    
    if (self.iMessageTranslationLabel.hidden == NO) {
        
        self.iMessageTranslationLabel.textColor = UIColor.whiteColor;
        self.iMessageTranslationLabel.x = self.iNickNameLabel.x;
        self.iMessageTranslationLabel.y = CGRectGetMaxY(self.translationBorderView.frame) + 10;
        CGSize trSize = [TKHelperUtil sizeForString:self.iMessageTranslationLabel.text font:TKFont(15) size:CGSizeMake(bubbleWidth - 20, CGFLOAT_MAX)];
        self.iMessageTranslationLabel.size = trSize;
    }
}

//根据显示内容计算高度
+ (CGFloat)heightForCellWithText:(NSString *)text limitWidth:(CGFloat)width
{
   NSAttributedString *attributedText = [NSAttributedString tkEmojiAttributedString:text withFont:TEXT_FONT withColor:UIColor.whiteColor];

    UILabel *tempLabel = [[UILabel alloc] init];
    tempLabel.font = TKFont(14);
    tempLabel.numberOfLines = 0;
    tempLabel.attributedText = attributedText;
    
    CGSize labelSize = [tempLabel sizeThatFits:CGSizeMake(width, CGFLOAT_MAX)];
    return labelSize.height;
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma 长按复制

-(BOOL)canBecomeFirstResponder {
    return YES;
}

// 可以响应的方法
-(BOOL)canPerformAction:(SEL)action withSender:(id)sender {
    if (action == @selector(newFunc)) {
        return YES;
    }
    return NO;
}

//针对于响应方法的实现
-(void)copy:(id)sender {
    UIPasteboard *pboard = [UIPasteboard generalPasteboard];
    pboard.string = self.cString;
}

-(void)newFunc{
    UIPasteboard *pboard = [UIPasteboard generalPasteboard];
    pboard.string = self.cString;
}

@end

//
//  HMTextView.m
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.

#import "TKEmojiHeader.h"
#import "TKTextView.h"
#import "UIView+TKExtension.h"

@interface TKTextView() <UITextViewDelegate>
@property (nonatomic, weak) UILabel *placehoderLabel;
@end

@implementation TKTextView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.scrollEnabled = NO;
        // 添加一个显示提醒文字的label（显示占位文字的label）
        UILabel *placehoderLabel = [[UILabel alloc] init];
        placehoderLabel.numberOfLines = 0;
        placehoderLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:placehoderLabel];
        self.placehoderLabel = placehoderLabel;
//        self.textAlignment = NSTextAlignmentCenter;
        // 设置默认的占位文字颜色
        self.placehoderColor = [TKTheme colorWithPath:@"ClassRoom.TKChatViews.chatToolPlacehoderColor"];
        // 设置默认的字体
        self.font = [UIFont systemFontOfSize:14];
        self.contentInset = UIEdgeInsetsMake(0, 5, self.height, 5);
         //不要设置自己的代理为自己本身
        // 监听内部文字改变
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textDidChange) name:UITextViewTextDidChangeNotification object:self];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 监听文字改变
- (void)textDidChange
{
    // text属性：只包括普通的文本字符串
    // attributedText：包括了显示在textView里面的所有内容（表情、text）
    self.placehoderLabel.hidden = self.hasText;
    if (self.hasText) {
        [self contentSizeToFit];
        
    }
}

#pragma mark - 公共方法
- (void)setText:(NSString *)text
{
    [super setText:text];
    
    [self textDidChange];
}

- (void)setAttributedText:(NSAttributedString *)attributedText
{
    [super setAttributedText:attributedText];
    
    [self textDidChange];
}

- (void)setPlacehoder:(NSString *)placehoder
{
    _placehoder = [placehoder copy];
    
    // 设置文字
    self.placehoderLabel.text = placehoder;
    
    // 重新计算子控件的fame
    [self setNeedsLayout];
}

- (void)setPlacehoderColor:(UIColor *)placehoderColor
{
    _placehoderColor = placehoderColor;
    
    // 设置颜色
    self.placehoderLabel.textColor = placehoderColor;
}

- (void)setFont:(UIFont *)font
{
    [super setFont:font];
    
    self.placehoderLabel.font = font;
    
    // 重新计算子控件的fame
    [self setNeedsLayout];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    self.placehoderLabel.y = 5;
    self.placehoderLabel.x = 5;
    self.placehoderLabel.width = self.width - 2 * self.placehoderLabel.x;
    
    // 根据文字计算label的高度
    CGSize maxSize = CGSizeMake(self.placehoderLabel.width, MAXFLOAT);
//    CGSize placehoderSize = [self.placehoder sizeWithFont:self.placehoderLabel.font constrainedToSize:maxSize];
    
    
    NSDictionary * tdic = [NSDictionary dictionaryWithObjectsAndKeys:self.placehoderLabel.font,NSFontAttributeName,nil];
    CGSize  placehoderSize =[self.placehoder boundingRectWithSize:maxSize options:NSStringDrawingUsesLineFragmentOrigin  attributes:tdic context:nil].size;
    
    self.placehoderLabel.height = placehoderSize.height;
}

- (void)contentSizeToFit
{
    //先判断一下有没有文字（没文字就没必要设置居中了）
    if([self.text length]>0)
    {
        //textView的contentSize属性
        CGSize contentSize = self.contentSize;
        //textView的内边距属性
        UIEdgeInsets offset;
        CGSize newSize = contentSize;
        
        //如果文字内容高度没有超过textView的高度
        if(contentSize.height <= self.frame.size.height)
        {
            //textView的高度减去文字高度除以2就是Y方向的偏移量，也就是textView的上内边距
            CGFloat offsetY = (self.frame.size.height - contentSize.height)/2;
            offset = UIEdgeInsetsMake(offsetY, 0, 0, 0);
        }
        else          //如果文字高度超出textView的高度
        {
            newSize = self.frame.size;
            offset = UIEdgeInsetsZero;
            CGFloat fontSize = 15;
            
            //通过一个while循环，设置textView的文字大小，使内容不超过整个textView的高度（这个根据需要可以自己设置）
            while (contentSize.height > self.frame.size.height)
            {
                [self setFont:[UIFont fontWithName:@"Helvetica Neue" size:fontSize--]];
                contentSize = self.contentSize;
            }
            newSize = contentSize;
        }
        
        //根据前面计算设置textView的ContentSize和Y方向偏移量
        [self setContentSize:newSize];
        [self setContentInset:offset];
        
    }
}

@end

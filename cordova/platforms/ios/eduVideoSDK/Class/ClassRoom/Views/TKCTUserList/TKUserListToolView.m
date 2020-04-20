//
//  TKUserListToolView.m
//  EduClass
//
//  Created by lyy on 2018/7/3.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKUserListToolView.h"


#define ThemeKP(args) [@"ClassRoom.TKChatViews." stringByAppendingString:args]
@interface TKUserListToolView()
@property (nonatomic, strong) UIButton *sendButton;
@end

@implementation TKUserListToolView

- (instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        
        
        self.sakura.backgroundColor(@"TKUserListView.userList_toolBackColor");
        _textField = [[UITextField alloc]init];
        _textField.backgroundColor = [UIColor whiteColor];
        _textField.layer.masksToBounds = YES;
        _textField.layer.cornerRadius = 5;
        _textField.keyboardType = UIKeyboardTypePhonePad;
        
        [self addSubview:_textField];
        _sendButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
        _sendButton.sakura.backgroundImage(ThemeKP(@"button_send_default"),UIControlStateNormal);
        [_sendButton setTitle:TKMTLocalized(@"Button.jump") forState:(UIControlStateNormal)];
        [_sendButton addTarget:self action:@selector(jumpButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
        [self addSubview:_sendButton];
    }
    return self;
}
- (void)jumpButtonClick:(UIButton *)sender{
    [_textField resignFirstResponder];
    if (self.delegate && [self.delegate respondsToSelector:@selector(jumpPageNum:)]) {
        [self.delegate jumpPageNum:[_textField.text intValue]];
    }
}
- (void)layoutSubviews{
    
    _textField.frame = CGRectMake(10, 5, self.width-20 - 70, self.height-10);
    _sendButton.frame = CGRectMake(_textField.rightX+5, 5, 60, self.height-10);
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

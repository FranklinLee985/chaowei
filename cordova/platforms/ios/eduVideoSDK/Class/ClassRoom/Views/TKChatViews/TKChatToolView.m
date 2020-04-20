//
//  TKChatToolView.m
//  EduClass
//
//  Created by lyy on 2018/4/27.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKChatToolView.h"
#import "TKEmotionTextView.h"
#import "TKEmotionKeyboard.h"
#import "TKEmojiHeader.h"

#define ThemeKP(args) [@"ClassRoom.TKChatViews." stringByAppendingString:args]
#define emotionWidth 30 // 表情按钮 宽度

@interface TKChatToolView()<UITextViewDelegate>

@property (nonatomic, strong) UIImageView *backImageView;//背景图
@property (nonatomic, strong) TKEmotionKeyboard *kerboard; //自定义表情键盘
@property (nonatomic, strong) UIButton *sendButton;//发送按钮
@property (nonatomic, strong) UIButton *picButton;//图片选择按钮
@property (nonatomic, assign) BOOL isDistance;


@property (nonatomic, assign) NSInteger lineCount;
@end

@implementation TKChatToolView
- (instancetype)initWithFrame:(CGRect)frame isDistance:(BOOL)isDistance{
    if (self = [super initWithFrame:frame]) {

        [self loadNotificatio];

        _inputField =({
            
            TKEmotionTextView *tInputField =  [[TKEmotionTextView alloc] init];
            tInputField.sakura.backgroundColor(ThemeKP(@"chatToolBackgroundColor"));
            tInputField.sakura.textColor(ThemeKP(@"chatToolTextColor"));
            tInputField.placehoder = TKMTLocalized(@"Say.say");
            tInputField.font = [UIFont systemFontOfSize:15];
            tInputField.delegate = self;
            //        tInputField.maxNumberOfLines = 5;
            tInputField.returnKeyType = UIReturnKeySend;
            tInputField.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
            
            tInputField.layer.masksToBounds = YES;
            tInputField.layer.cornerRadius = 5;
            tInputField.layer.borderColor = [TKTheme cgColorWithPath:ThemeKP(@"chatToolTextFBorderColor")];
            tInputField.layer.borderWidth = [TKTheme floatWithPath:ThemeKP(@"chatToolTextFBorderWidth")];
            tInputField;
            
        });
        self.inputField.layer.borderColor = UIColor.clearColor.CGColor;
        [self addSubview:_inputField];
        
        _sendButton =({
            UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
            
            [button setTitle:TKMTLocalized(@"Button.send") forState:(UIControlStateNormal)];
            button.sakura.titleColor(ThemeKP(@"chatToolBackgroundColor"),UIControlStateNormal);
            button.sakura.backgroundImage(ThemeKP(@"button_send_default"),UIControlStateNormal);
            [button addTarget:self action:@selector(sendButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
            
            button;
        });
        
        [self addSubview:_sendButton];
        
        
        _emotionButton = ({
            UIButton *button = [UIButton  buttonWithType:(UIButtonTypeCustom)];
            
            button.sakura.image(ThemeKP(@"icon_expression"),UIControlStateNormal);
            button.sakura.image(ThemeKP(@"icon_keyboard"),UIControlStateSelected);
            
            [button addTarget:self action:@selector(emotionButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        [self addSubview:_emotionButton];
        
        if ([TKEduClassRoom shareInstance].roomJson.configuration.isChatAllowSendImage) {
            
            _picButton = ({
                UIButton *button = [UIButton  buttonWithType:(UIButtonTypeCustom)];

                button.sakura.image(ThemeKP(@"icon_pic"),UIControlStateNormal);
                button.sakura.image(ThemeKP(@"icon_pic"),UIControlStateSelected);

                [button addTarget:self action:@selector(pictureSendClick:) forControlEvents:(UIControlEventTouchUpInside)];
                button;
            });
            [self addSubview:_picButton];
        }
        
        [self loadLayout];
        self.isDistance = isDistance;
    }
    
    
    return self;
}

- (void)loadLayout{
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(touchMainPage:)
                                                name:stouchMainPageNotification
                                              object:nil];
    
    
    //添加表情选中的通知    监听键盘
    // 监听表情选中的通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(emotionDidSelected:) name:TKEmotionDidSelectedNotification object:nil];
    // 监听删除按钮点击的通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(emotionDidDeleted:) name:TKEmotionDidDeletedNotification object:nil];
    
}
- (void)loadNotificatio{
    
}
- (void)layoutSubviews{

    CGFloat sendWidth = 60;
    
    CGFloat inputWidth = self.width - sendWidth - 5 ;
    
    CGFloat inputX = 0;
    
    if (_isDistance) {
        
        inputWidth = self.width-sendWidth - 20 ;
        inputX = 5;
    }
    
    CGFloat inputHeight;
    
    if (self.height>30) {
        inputHeight = 30;
    }

    _inputField.frame = CGRectMake(inputX, 5, inputWidth, self.height - 10);
    _inputField.textContainerInset = UIEdgeInsetsMake(5, 2, 0, emotionWidth * 2 + 5);
    _sendButton.frame = CGRectMake(_inputField.rightX + 5, 5, sendWidth, 34.);
    _emotionButton.frame = CGRectMake(_inputField.rightX - emotionWidth - 5, _sendButton.y, emotionWidth, _sendButton.height);
    if (_picButton) _picButton.frame =CGRectMake(_emotionButton.leftX - emotionWidth, _emotionButton.y, emotionWidth, _emotionButton.height);
}


#pragma mark - 发送事件
- (void)sendButtonClick:(UIButton *)sender{
    if (!_inputField || !_inputField.realText || _inputField.realText.length == 0)
    {
        return;
    }
    
    if([[_inputField.realText stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]length]==0) {
        return;
    }
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(sendMessage:)]) {
        _emotionButton.selected = YES;
        [self.delegate sendMessage:_inputField.realText];
        
    }
}

#pragma mark - 点击表情
- (void)emotionButtonClick:(UIButton *)sender{
    
    // 关闭键盘
    [self.inputField resignFirstResponder];
    
    // 可编辑的
    if (_isDistance) {
        
        // 切换状态 调用setter方法 ⤵️
        self.isCustomInputView = !_isCustomInputView;
        
    }
    
    else {
        
        _isCustomInputView = YES;

    }
    // 更换完毕
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        // 打开键盘
        [self.inputField becomeFirstResponder];
    });

   
}

- (void)pictureSendClick:(UIButton *) sender {
    
    //判断是否自己被禁言
    BOOL disablechat = [TKUtil getBOOValueFromDic:[TKEduSessionHandle shareInstance].localUser.properties Key:sDisablechat];
    if (disablechat) {
        return;
    }
    
    // 关闭键盘
    [self.inputField resignFirstResponder];
    
    [TKEduSessionHandle shareInstance].updateImageUseType = TKUpdateImageUseType_Message;
    [[NSNotificationCenter defaultCenter] postNotificationName:sChoosePhotosUploadNotification object:sChoosePhotosUploadNotification];
}

- (BOOL)textViewShouldBeginEditing:(UITextView *)textView {
    
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(chatToolViewDidBeginEditing:)]) {
        [self.delegate chatToolViewDidBeginEditing:_inputField];
    }
    // 是否可以编辑
    return _isDistance;
}



- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    
    
    
//    _inputField.height = _inputField.height + 10;
    if ([text isEqualToString:@"\n"]){ //判断输入的字是否是回车，即按下return
        //在这里做你响应return键的代码
        [self sendButtonClick:nil];
        return NO; //这里返回NO，就代表return键值失效，即页面上按下return，不会出现换行，如果为yes，则输入页面会换行
    }
    
    return YES;
}

- (void)textViewDidChange:(UITextView *)textView {
    // 换行 逻辑
    [_inputField sizeToFit];

    if (_inputField.height < 33.) {
        _inputField.height = 33.;
    }
    else {
        _inputField.height = _inputField.height;
        self.height = _inputField.height + 10;
        
        if (self.delegate && [self.delegate respondsToSelector:@selector(chatToolViewChangeHeight:)]) {
            _emotionButton.selected = YES;
            [self.delegate chatToolViewChangeHeight: self.height];
            
        }
    }
}
#pragma mark - 表情键盘初始化
- (TKEmotionKeyboard *)kerboard {
    if (!_kerboard) {
        self.kerboard = [TKEmotionKeyboard keyboard];
        
        self.kerboard.frame = CGRectMake(0, 0, ScreenW, TKKeyBoardHeight);
        
        //        self.kerboard.width = SCREEN_WIDTH;
        //        self.kerboard.height = 216;
    }
    return _kerboard;
}
- (void)touchMainPage:(NSNotification*)notify{
    self.emotionButton.selected = _isDistance ;
    [self.inputField resignFirstResponder];
}
- (void)emotionDidSelected:(NSNotification *)note
{
    TKEmotion *emotion = note.userInfo[TKSelectedEmotion];
    // 1.拼接表情
    [_inputField appendEmotion:emotion];
    
}

/**
 *  当点击表情键盘上的删除按钮时调用
 */
- (void)emotionDidDeleted:(NSNotification *)note
{
    // 往回删
    [_inputField deleteBackward];
}



// 动态 样式展示
- (void)setIsCustomInputView:(BOOL)isCustomInputView {
    
    _isCustomInputView = isCustomInputView;
    _inputField.inputView = _isCustomInputView ? self.kerboard : nil;
    
    // 切换按钮状态
    _emotionButton.selected = _isCustomInputView ;
}



/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
